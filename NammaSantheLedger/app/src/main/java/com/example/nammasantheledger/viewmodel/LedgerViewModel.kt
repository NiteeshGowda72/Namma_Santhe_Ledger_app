package com.example.nammasantheledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nammasantheledger.data.Customer
import com.example.nammasantheledger.data.TransactionEntity
import com.example.nammasantheledger.data.TransactionType
import com.example.nammasantheledger.repository.LedgerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LedgerViewModel(private val repository: LedgerRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val customersWithBalance = _searchQuery
        .flatMapLatest { query ->
            repository.getCustomersWithBalance(query)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalOutstanding: StateFlow<Double> = repository.getTotalOutstanding()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val todaysSales: StateFlow<Double> = repository.getTodaysSales()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addCustomer(name: String, phoneNumber: String, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.addCustomer(name, phoneNumber)
            onResult(id)
        }
    }

    fun addTransaction(customerId: Int, amount: Double, type: TransactionType, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.addTransaction(
                TransactionEntity(customerId = customerId, amount = amount, type = type)
            )
            onComplete()
        }
    }
    
    fun getTransactionsForCustomer(customerId: Int) = repository.getTransactionsForCustomer(customerId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    fun getCustomerById(customerId: Int, onResult: (Customer?) -> Unit) {
        viewModelScope.launch {
            val customer = repository.getCustomerById(customerId)
            onResult(customer)
        }
    }
}

class LedgerViewModelFactory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LedgerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LedgerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
