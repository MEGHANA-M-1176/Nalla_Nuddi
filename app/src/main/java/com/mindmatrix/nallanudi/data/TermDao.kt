package com.mindmatrix.nallanudi.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TermDao {
    @Query("SELECT * FROM terms")
    fun getAllTerms(): Flow<List<Term>>

    @Query("SELECT * FROM terms WHERE subject = :subject")
    fun getTermsBySubject(subject: String): Flow<List<Term>>
    
    @Query("SELECT * FROM terms WHERE englishTerm LIKE '%' || :query || '%'")
    fun searchTerms(query: String): Flow<List<Term>>
    
    @Query("SELECT * FROM terms WHERE isSaved = 1")
    fun getSavedTerms(): Flow<List<Term>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerms(terms: List<Term>)

    @Update
    suspend fun updateTerm(term: Term)

    @Query("SELECT COUNT(*) FROM terms")
    suspend fun getTermCount(): Int
}
