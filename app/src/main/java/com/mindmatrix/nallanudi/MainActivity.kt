package com.mindmatrix.nallanudi

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mindmatrix.nallanudi.data.AppDatabase
import com.mindmatrix.nallanudi.data.Term
import com.mindmatrix.nallanudi.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: TermAdapter
    private lateinit var flashcardAdapter: FlashcardAdapter
    private lateinit var tts: TextToSpeech

    private var currentFilter: String = "All"
    private var currentQuery: String = ""
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        tts = TextToSpeech(this, this)

        setupRecyclerView()
        setupViewPager()
        setupSearchView()
        setupTabLayout()

        lifecycleScope.launch {
            checkAndPopulateDb()
            setupWordOfDay()
            loadTerms()
        }
    }

    private fun setupRecyclerView() {
        adapter = TermAdapter(
            onTtsClick = { term ->
                tts.speak(term, TextToSpeech.QUEUE_FLUSH, null, null)
            },
            onSaveClick = { term ->
                lifecycleScope.launch(Dispatchers.IO) {
                    term.isSaved = !term.isSaved
                    database.termDao().updateTerm(term)
                }
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupViewPager() {
        flashcardAdapter = FlashcardAdapter(
            onTtsClick = { term ->
                tts.speak(term, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        )
        binding.viewPager.adapter = flashcardAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query ?: ""
                loadTerms()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                loadTerms()
                return true
            }
        })
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentFilter = tab?.text.toString()
                
                if (currentFilter == "My List") {
                    binding.recyclerView.visibility = android.view.View.GONE
                    binding.viewPager.visibility = android.view.View.VISIBLE
                } else {
                    binding.recyclerView.visibility = android.view.View.VISIBLE
                    binding.viewPager.visibility = android.view.View.GONE
                }
                
                loadTerms()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadTerms() {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch(Dispatchers.IO) {
            val termsFlow = when (currentFilter) {
                "All" -> if (currentQuery.isEmpty()) database.termDao().getAllTerms() else database.termDao().searchTerms(currentQuery)
                "My List" -> database.termDao().getSavedTerms()
                else -> database.termDao().getTermsBySubject(currentFilter)
            }
            
            termsFlow.collectLatest { list ->
                val filteredList = if (currentFilter != "All" && currentFilter != "My List" && currentQuery.isNotEmpty()) {
                    list.filter { it.englishTerm.contains(currentQuery, ignoreCase = true) }
                } else if (currentFilter == "My List" && currentQuery.isNotEmpty()) {
                    list.filter { it.englishTerm.contains(currentQuery, ignoreCase = true) }
                } else {
                    list
                }
                
                withContext(Dispatchers.Main) {
                    if (currentFilter == "My List") {
                        flashcardAdapter.submitList(filteredList)
                    } else {
                        adapter.submitList(filteredList)
                    }
                }
            }
        }
    }

    private fun setupWordOfDay() {
        lifecycleScope.launch(Dispatchers.IO) {
            database.termDao().getAllTerms().collectLatest { terms ->
                if (terms.isNotEmpty()) {
                    val index = ((System.currentTimeMillis() / (1000 * 60 * 60 * 24)) % terms.size).toInt()
                    val wod = terms[index]
                    withContext(Dispatchers.Main) {
                        binding.tvWodTerm.text = wod.englishTerm
                        binding.tvWodKannada.text = wod.kannadaExplanation
                    }
                }
            }
        }
    }

    private suspend fun checkAndPopulateDb() {
        withContext(Dispatchers.IO) {
            if (database.termDao().getTermCount() == 0) {
                val initialTerms = listOf(
                    // Science
                    Term(englishTerm = "Gravity", kannadaExplanation = "ಗುರುತ್ವಾಕರ್ಷಣೆ (Gurutvakarshane)", example = "ಸೇಬು ಕೆಳಗೆ ಬೀಳಲು ಗುರುತ್ವಾಕರ್ಷಣೆ ಕಾರಣ.", subject = "Science"),
                    Term(englishTerm = "Photosynthesis", kannadaExplanation = "ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆ (Dyutisamshleshane)", example = "ಸಸ್ಯಗಳು ಸೂರ್ಯನ ಬೆಳಕಿನಿಂದ ಆಹಾರ ತಯಾರಿಸುತ್ತವೆ.", subject = "Science"),
                    Term(englishTerm = "Osmosis", kannadaExplanation = "ರಸವಿಸರಣೆ (Rasavisarane)", example = "ಬೇರುಗಳು ಮಣ್ಣಿನಿಂದ ನೀರನ್ನು ಹೀರಿಕೊಳ್ಳುವುದು.", subject = "Science"),
                    Term(englishTerm = "Hypothesis", kannadaExplanation = "ಪರಿಕಲ್ಪನೆ (Parikalpane)", example = "ಪ್ರಯೋಗಕ್ಕೆ ಮುನ್ನ ಮಾಡುವ ಊಹೆ.", subject = "Science"),
                    Term(englishTerm = "Velocity", kannadaExplanation = "ವೇಗ (Vega)", example = "ಒಂದು ನಿರ್ದಿಷ್ಟ ದಿಕ್ಕಿನಲ್ಲಿ ಚಲಿಸುವ ವೇಗ.", subject = "Science"),
                    Term(englishTerm = "Friction", kannadaExplanation = "ಘರ್ಷಣೆ (Gharshane)", example = "ಎರಡು ಮೇಲ್ಮೈಗಳ ನಡುವಿನ ಪ್ರತಿರೋಧ.", subject = "Science"),
                    Term(englishTerm = "Density", kannadaExplanation = "ಸಾಂದ್ರತೆ (Saandrathe)", example = "ವಸ್ತುವಿನ ತೂಕ ಮತ್ತು ಗಾತ್ರದ ಅನುಪಾತ.", subject = "Science"),
                    Term(englishTerm = "Molecule", kannadaExplanation = "ಅಣು (Anu)", example = "ವಸ್ತುವಿನ ಅತಿ ಸಣ್ಣ ಕಣ.", subject = "Science"),
                    Term(englishTerm = "Evolution", kannadaExplanation = "ವಿಕಾಸ (Vikasa)", example = "ಜೀವಿಗಳ ಕಾಲಾನಂತರದ ಬೆಳವಣಿಗೆ.", subject = "Science"),
                    Term(englishTerm = "Digestion", kannadaExplanation = "ಜೀರ್ಣಕ್ರಿಯೆ (Jeernakriye)", example = "ಆಹಾರವನ್ನು ದೇಹವು ಹೀರಿಕೊಳ್ಳಲು ಒಡೆಯುವುದು.", subject = "Science"),

                    // Math
                    Term(englishTerm = "Trigonometry", kannadaExplanation = "ತ್ರಿಕೋನಮಿತಿ (Trikonamiti)", example = "ತ್ರಿಕೋನದ ಕೋನಗಳು ಮತ್ತು ಬದಿಗಳ ಅಧ್ಯಯನ.", subject = "Math"),
                    Term(englishTerm = "Equation", kannadaExplanation = "ಸಮೀಕರಣ (Sameekarana)", example = "ಎರಡು ಮೌಲ್ಯಗಳು ಸಮ ಎಂದು ತೋರಿಸುವ ಗಣಿತದ ವಾಕ್ಯ.", subject = "Math"),
                    Term(englishTerm = "Algebra", kannadaExplanation = "ಬೀಜಗಣಿತ (Beejaganitha)", example = "ಅಕ್ಷರಗಳನ್ನು ಬಳಸಿ ಮಾಡುವ ಗಣಿತ.", subject = "Math"),
                    Term(englishTerm = "Geometry", kannadaExplanation = "ರೇಖಾಗಣಿತ (Rekhaganitha)", example = "ಆಕಾರಗಳು ಮತ್ತು ಸ್ಥಳದ ಅಧ್ಯಯನ.", subject = "Math"),
                    Term(englishTerm = "Probability", kannadaExplanation = "ಸಂಭವನೀಯತೆ (Sambhavaneeyathe)", example = "ಒಂದು ಘಟನೆ ನಡೆಯುವ ಸಾಧ್ಯತೆ.", subject = "Math"),
                    Term(englishTerm = "Fraction", kannadaExplanation = "ಭಿನ್ನರಾಶಿ (Bhinnaraashi)", example = "ಒಂದು ಪೂರ್ಣದ ಭಾಗ.", subject = "Math"),
                    Term(englishTerm = "Integer", kannadaExplanation = "ಪೂರ್ಣಾಂಕ (Poornanka)", example = "ದಶಮಾಂಶವಿಲ್ಲದ ಸಂಪೂರ್ಣ ಸಂಖ್ಯೆ.", subject = "Math"),
                    Term(englishTerm = "Ratio", kannadaExplanation = "ಅನುಪಾತ (Anupatha)", example = "ಎರಡು ಪ್ರಮಾಣಗಳ ಹೋಲಿಕೆ.", subject = "Math"),
                    Term(englishTerm = "Variable", kannadaExplanation = "ಚರಾಕ್ಷರ (Charakshara)", example = "ಬದಲಾಗುವ ಮೌಲ್ಯವನ್ನು ಹೊಂದಿರುವ ಸಂಕೇತ.", subject = "Math"),
                    Term(englishTerm = "Coordinate", kannadaExplanation = "ನಿರ್ದೇಶಾಂಕ (Nirdeshanka)", example = "ನಕ್ಷೆಯಲ್ಲಿ ಸ್ಥಳವನ್ನು ಸೂಚಿಸುವ ಸಂಖ್ಯೆಗಳು.", subject = "Math"),

                    // Commerce
                    Term(englishTerm = "Profit", kannadaExplanation = "ಲಾಭ (Labha)", example = "ಮಾರಾಟದ ಬೆಲೆ ಕೊಳ್ಳುವ ಬೆಲೆಗಿಂತ ಹೆಚ್ಚಿದ್ದರೆ ಲಾಭ.", subject = "Commerce"),
                    Term(englishTerm = "Asset", kannadaExplanation = "ಆಸ್ತಿ (Aasti)", example = "ಕಂಪನಿಯ ಒಡೆತನದಲ್ಲಿರುವ ಮೌಲ್ಯಯುತ ವಸ್ತುಗಳು.", subject = "Commerce"),
                    Term(englishTerm = "Liability", kannadaExplanation = "ಹೊಣೆಗಾರಿಕೆ (Honegaarike)", example = "ಕಂಪನಿಯು ಬೇರೆಯವರಿಗೆ ನೀಡಬೇಕಾದ ಹಣ.", subject = "Commerce"),
                    Term(englishTerm = "Revenue", kannadaExplanation = "ಆದಾಯ (Aadaaya)", example = "ವ್ಯಾಪಾರದಿಂದ ಬರುವ ಒಟ್ಟು ಹಣ.", subject = "Commerce"),
                    Term(englishTerm = "Investment", kannadaExplanation = "ಹೂಡಿಕೆ (Hoodike)", example = "ಲಾಭದ ನಿರೀಕ್ಷೆಯಿಂದ ಹಣವನ್ನು ಹಾಕುವುದು.", subject = "Commerce"),
                    Term(englishTerm = "Interest", kannadaExplanation = "ಬಡ್ಡಿ (Baddi)", example = "ಸಾಲ ಪಡೆದಿದ್ದಕ್ಕಾಗಿ ನೀಡುವ ಹೆಚ್ಚುವರಿ ಹಣ.", subject = "Commerce"),
                    Term(englishTerm = "Dividend", kannadaExplanation = "ಲಾಭಾಂಶ (Labhamsha)", example = "ಪಾಲುದಾರರಿಗೆ ನೀಡುವ ಲಾಭದ ಒಂದು ಭಾಗ.", subject = "Commerce"),
                    Term(englishTerm = "Ledger", kannadaExplanation = "ಖಾತೆ ಪುಸ್ತಕ (Khaate Pustaka)", example = "ಆರ್ಥಿಕ ವಹಿವಾಟುಗಳ ದಾಖಲೆ.", subject = "Commerce"),
                    Term(englishTerm = "Balance Sheet", kannadaExplanation = "ಆಯವ್ಯಯ ಪಟ್ಟಿ (Aayavyaya Patti)", example = "ಕಂಪನಿಯ ಆರ್ಥಿಕ ಸ್ಥಿತಿಯ ವರದಿ.", subject = "Commerce"),
                    Term(englishTerm = "Equity", kannadaExplanation = "ಷೇರು ಬಂಡವಾಳ (Share Bandavala)", example = "ಕಂಪನಿಯಲ್ಲಿ ಮಾಲೀಕರ ಪಾಲು.", subject = "Commerce")
                )
                database.termDao().insertTerms(initialTerms)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "TTS Language not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
