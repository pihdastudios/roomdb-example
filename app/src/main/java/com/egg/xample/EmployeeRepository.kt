package com.egg.xample

import androidx.lifecycle.LiveData
import androidx.annotation.WorkerThread

class EmployeeRepository(private val employeeDao: EmployeeDao) {

    val allWords: LiveData<List<Employee>> = employeeDao.getAllWords()

    @WorkerThread
    suspend fun insert(employee: Employee) {
        employeeDao.insert(employee)
    }

    suspend fun deleteAll() {
        employeeDao.deleteAll()
    }

    suspend fun deleteWord(employee: Employee) {
        employeeDao.deleteWord(employee)
    }

    suspend fun update(employee: Employee) {
        employeeDao.update(employee)
    }
}