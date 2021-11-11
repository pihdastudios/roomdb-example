package com.egg.xample

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EmployeeDao {
    @Query("SELECT * from employee_table ORDER BY first_name ASC")
    fun getAllWords(): LiveData<List<Employee>>

    @Insert
    suspend fun insert(employee: Employee)

    @Query("DELETE FROM employee_table")
    suspend fun deleteAll()

    @Update
    suspend fun update(employee: Employee)

    @Query("UPDATE employee_table SET first_name = :firstName WHERE id == :id")
    suspend fun updateItem(firstName: String, id: Int)

    @Delete
    suspend fun deleteWord(employee: Employee)
}