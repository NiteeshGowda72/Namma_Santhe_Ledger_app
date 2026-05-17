package com.example.nammasantheledger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammasantheledger.data.CustomerWithBalance
import com.example.nammasantheledger.data.TransactionType
import com.example.nammasantheledger.viewmodel.LedgerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickEntryScreen(
    viewModel: LedgerViewModel,
    onNavigateBack: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var selectedCustomer by remember { mutableStateOf<CustomerWithBalance?>(null) }
    var newCustomerName by remember { mutableStateOf("") }
    
    var amountText by remember { mutableStateOf("") }

    val customers by viewModel.customersWithBalance.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (step == 1) "Select Customer" else "Enter Amount") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step == 2) step = 1 else onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (step == 1) {
                // Step 1: Select Customer
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search or enter new name...") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (searchQuery.isNotEmpty() && customers.none { it.name.equals(searchQuery, ignoreCase = true) }) {
                    Button(
                        onClick = {
                            viewModel.addCustomer(searchQuery, "") { newId ->
                                selectedCustomer = CustomerWithBalance(newId.toInt(), searchQuery, "", 0.0)
                                step = 2
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add New Customer: '$searchQuery'")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                LazyColumn {
                    items(customers) { customer ->
                        CustomerSelectionItem(
                            customer = customer,
                            onClick = {
                                selectedCustomer = customer
                                step = 2
                            }
                        )
                        Divider()
                    }
                }
            } else {
                // Step 2: Enter Amount & Add Transaction
                Text(
                    text = "Customer: ${selectedCustomer?.name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "₹${if (amountText.isEmpty()) "0" else amountText}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                NumericKeypad(
                    onKeyPress = { key ->
                        if (amountText.length < 8) {
                            amountText += key
                        }
                    },
                    onDelete = {
                        if (amountText.isNotEmpty()) {
                            amountText = amountText.dropLast(1)
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull() ?: 0.0
                            if (amount > 0 && selectedCustomer != null) {
                                viewModel.addTransaction(
                                    customerId = selectedCustomer!!.id,
                                    amount = amount,
                                    type = TransactionType.PAYMENT
                                ) { onNavigateBack() }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f).height(60.dp)
                    ) {
                        Text("LOG PAYMENT")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull() ?: 0.0
                            if (amount > 0 && selectedCustomer != null) {
                                viewModel.addTransaction(
                                    customerId = selectedCustomer!!.id,
                                    amount = amount,
                                    type = TransactionType.CREDIT
                                ) { onNavigateBack() }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                        modifier = Modifier.weight(1f).height(60.dp)
                    ) {
                        Text("ADD UDARI")
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerSelectionItem(customer: CustomerWithBalance, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = customer.name, fontSize = 18.sp)
        if (customer.balance > 0) {
            Text(text = "₹${customer.balance.format(2)}", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun NumericKeypad(onKeyPress: (String) -> Unit, onDelete: () -> Unit) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(".", "0", "DEL")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        for (row in keys) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (key in row) {
                    KeypadButton(
                        text = key,
                        onClick = {
                            if (key == "DEL") onDelete() else onKeyPress(key)
                        },
                        modifier = Modifier.weight(1f).aspectRatio(1.5f).padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun KeypadButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}
