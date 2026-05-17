package com.example.nammasantheledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nammasantheledger.ui.screens.CustomerDetailScreen
import com.example.nammasantheledger.ui.screens.HomeScreen
import com.example.nammasantheledger.ui.screens.QuickEntryScreen
import com.example.nammasantheledger.viewmodel.LedgerViewModel
import com.example.nammasantheledger.viewmodel.LedgerViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val app = application as LedgerApplication
                    val viewModel: LedgerViewModel = viewModel(
                        factory = LedgerViewModelFactory(app.repository)
                    )
                    
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToQuickEntry = { navController.navigate("quick_entry") },
                                onNavigateToCustomerDetail = { customerId -> 
                                    navController.navigate("customer_detail/$customerId") 
                                }
                            )
                        }
                        composable("quick_entry") {
                            QuickEntryScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("customer_detail/{customerId}") { backStackEntry ->
                            val customerId = backStackEntry.arguments?.getString("customerId")?.toIntOrNull()
                            if (customerId != null) {
                                CustomerDetailScreen(
                                    viewModel = viewModel,
                                    customerId = customerId,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
