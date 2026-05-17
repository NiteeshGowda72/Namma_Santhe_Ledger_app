package com.example.nammasantheledger.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammasantheledger.data.CustomerWithBalance
import com.example.nammasantheledger.viewmodel.LedgerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LedgerViewModel,
    onNavigateToQuickEntry: () -> Unit,
    onNavigateToCustomerDetail: (Int) -> Unit
) {
    val customers by viewModel.customersWithBalance.collectAsState()
    val totalOutstanding by viewModel.totalOutstanding.collectAsState()
    val todaysSales by viewModel.todaysSales.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Namma-Santhe Ledger") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToQuickEntry) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Dashboard Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryCard(
                    title = "Today's Sales",
                    amount = "₹${todaysSales.format(2)}",
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.width(16.dp))
                SummaryCard(
                    title = "Total Pending",
                    amount = "₹${totalOutstanding.format(2)}",
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFF44336)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search Customer...") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Customer List
            LazyColumn {
                items(customers) { customer ->
                    CustomerListItem(
                        customer = customer,
                        onClick = { onNavigateToCustomerDetail(customer.id) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, amount: String, modifier: Modifier = Modifier, color: Color) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = amount, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CustomerListItem(customer: CustomerWithBalance, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = customer.name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            if (customer.phoneNumber.isNotEmpty()) {
                Text(text = customer.phoneNumber, fontSize = 14.sp, color = Color.Gray)
            }
        }
        val balanceText = if (customer.balance > 0) "₹${customer.balance.format(2)} Due" else "₹0.0"
        val balanceColor = if (customer.balance > 0) Color.Red else Color.Gray
        Text(
            text = balanceText,
            color = balanceColor,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)
