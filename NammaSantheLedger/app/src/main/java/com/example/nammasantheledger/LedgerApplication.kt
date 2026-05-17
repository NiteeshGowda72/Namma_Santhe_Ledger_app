package com.example.nammasantheledger

import android.app.Application
import com.example.nammasantheledger.data.AppDatabase
import com.example.nammasantheledger.repository.LedgerRepository

class LedgerApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { LedgerRepository(database.ledgerDao()) }
}
