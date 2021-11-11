package com.egg.xample

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WordDao {
    @Query("SELECT * from word_table ORDER BY word ASC")
    fun getAllWords(): LiveData<List<Word>>

    @Insert
    suspend fun insert(word: Word)

    @Query("DELETE FROM word_table")
    suspend fun deleteAll()

    @Update
    suspend fun update(word: Word)

    @Query("UPDATE word_table SET word = :word WHERE id == :id")
    suspend fun updateItem(word: String, id: Int)

    @Delete
    suspend fun deleteWord(word: Word)
}