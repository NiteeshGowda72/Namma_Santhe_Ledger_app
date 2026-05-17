package com.example.nammasantheledger.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammasantheledger.data.Customer
import com.example.nammasantheledger.data.TransactionEntity
import com.example.nammasantheledger.data.TransactionType
import com.example.nammasantheledger.viewmodel.LedgerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    viewModel: LedgerViewModel,
    customerId: Int,
    onNavigateBack: () -> Unit
) {
    var customer by remember { mutableStateOf<Customer?>(null) }
    val transactions by viewModel.getTransactionsForCustomer(customerId).collectAsState()
    val context = LocalContext.current

    LaunchedEffect(customerId) {
        viewModel.getCustomerById(customerId) {
            customer = it
        }
    }

    val totalDue = transactions.sumOf { 
        if (it.type == TransactionType.CREDIT) it.amount else -it.amount 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            if (customer != null && totalDue > 0) {
                BottomAppBar {
                    Button(
                        onClick = {
                            val message = "Namaskara ${customer?.name}, your pending due amount is ₹${totalDue.format(2)}. Please make the payment."
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                val url = "https://wa.me/${customer?.phoneNumber}?text=${Uri.encode(message)}"
                                data = Uri.parse(url)
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)) // WhatsApp green
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send WhatsApp Reminder")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Total Due: ₹${totalDue.format(2)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (totalDue > 0) Color.Red else Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Transaction History", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(transactions) { transaction ->
                    TransactionItem(transaction)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val isCredit = transaction.type == TransactionType.CREDIT
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (isCredit) "Credit (Udari)" else "Payment Received",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = dateFormat.format(Date(transaction.timestamp)),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Text(
            text = "₹${transaction.amount.format(2)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCredit) Color.Red else Color(0xFF4CAF50)
        )
    }
}
