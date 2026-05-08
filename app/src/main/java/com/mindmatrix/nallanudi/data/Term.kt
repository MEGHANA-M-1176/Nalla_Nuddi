package com.mindmatrix.nallanudi.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "terms")
data class Term(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val englishTerm: String,
    val kannadaExplanation: String,
    val example: String,
    val subject: String,
    var isSaved: Boolean = false
)
