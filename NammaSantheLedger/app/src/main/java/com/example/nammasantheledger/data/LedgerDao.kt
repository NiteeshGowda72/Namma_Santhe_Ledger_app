package com.example.nammasantheledger.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class CustomerWithBalance(
    val id: Int,
    val name: String,
    val phoneNumber: String,
    val balance: Double // Positive means customer owes vendor
)

@Dao
interface LedgerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCustomer(customer: Customer): Long

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("""
        SELECT c.id, c.name, c.phoneNumber, 
        COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE -t.amount END), 0.0) as balance
        FROM customers c
        LEFT JOIN transactions t ON c.id = t.customerId
        WHERE c.name LIKE '%' || :searchQuery || '%'
        GROUP BY c.id
        ORDER BY c.name ASC
    """)
    fun getCustomersWithBalance(searchQuery: String): Flow<List<CustomerWithBalance>>

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE -amount END), 0.0)
        FROM transactions
    """)
    fun getTotalOutstanding(): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0)
        FROM transactions
        WHERE type = 'CREDIT' AND timestamp >= :startOfDay
    """)
    fun getTodaysSales(startOfDay: Long): Flow<Double>

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomer(customerId: Int): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM customers WHERE id = :customerId")
    suspend fun getCustomerById(customerId: Int): Customer?
}
