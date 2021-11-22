package com.egg.xample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EmployeeViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: EmployeeRepository
    val allWords: LiveData<List<Employee>>

    init {
        val wordsDao = EmployeeRoomDatabase.getDatabase(application, scope).employeeDao()
        repository = EmployeeRepository(wordsDao)
        allWords = repository.allWords
    }

    fun insert(employee: Employee) = scope.launch(Dispatchers.IO) {
        repository.insert(employee)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    suspend fun deleteAll() {
        repository.deleteAll()
    }

    suspend fun deleteWord(employee: Employee) {
        repository.deleteWord(employee)
    }

    suspend fun updateWord(employee: Employee) {
        repository.update(employee)
    }

}