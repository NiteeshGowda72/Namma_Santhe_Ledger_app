package com.example.nammasantheledger.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType {
    CREDIT, // Vendor gave credit to customer
    PAYMENT // Customer paid back vendor
}

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val amount: Double,
    val type: TransactionType,
    val timestamp: Long = System.currentTimeMillis()
)
