package com.example.nammasantheledger.repository

import com.example.nammasantheledger.data.Customer
import com.example.nammasantheledger.data.CustomerWithBalance
import com.example.nammasantheledger.data.LedgerDao
import com.example.nammasantheledger.data.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class LedgerRepository(private val ledgerDao: LedgerDao) {

    fun getCustomersWithBalance(query: String): Flow<List<CustomerWithBalance>> {
        return ledgerDao.getCustomersWithBalance(query)
    }

    fun getTotalOutstanding(): Flow<Double> {
        return ledgerDao.getTotalOutstanding()
    }

    fun getTodaysSales(): Flow<Double> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return ledgerDao.getTodaysSales(calendar.timeInMillis)
    }

    fun getTransactionsForCustomer(customerId: Int): Flow<List<TransactionEntity>> {
        return ledgerDao.getTransactionsForCustomer(customerId)
    }

    suspend fun getCustomerById(customerId: Int): Customer? {
        return ledgerDao.getCustomerById(customerId)
    }

    suspend fun addCustomer(name: String, phoneNumber: String): Long {
        return ledgerDao.insertCustomer(Customer(name = name, phoneNumber = phoneNumber))
    }

    suspend fun addTransaction(transaction: TransactionEntity) {
        ledgerDao.insertTransaction(transaction)
    }
}
