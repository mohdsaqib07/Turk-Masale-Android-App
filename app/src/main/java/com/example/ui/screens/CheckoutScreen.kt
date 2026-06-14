package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Red40
import com.example.viewmodel.ShopViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: ShopViewModel,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    var selectedPayment by remember { mutableStateOf("UPI") }
    val scope = rememberCoroutineScope()
    val total = viewModel.cartTotal

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                Button(
                    onClick = {
                        isProcessing = true
                        scope.launch {
                            delay(2000) // Simulate network delay
                            viewModel.checkout()
                            isProcessing = false
                            onPaymentSuccess()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Red40),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Pay ₹$total securely")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Shipping Address", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = "123 Spice Market, New Delhi, India",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text("Address") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Payment Method", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            
            val methods = listOf("UPI", "Credit / Debit Card", "Net Banking", "Cash on Delivery")
            methods.forEach { method ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPayment == method,
                        onClick = { selectedPayment = method },
                        colors = RadioButtonDefaults.colors(selectedColor = Red40)
                    )
                    Text(method, modifier = Modifier.padding(start = 8.dp))
                }
            }
            
            if (selectedPayment == "Credit / Debit Card") {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Card Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("MM/YY") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Amount Due", fontWeight = FontWeight.Bold)
                    Text("₹$total", fontWeight = FontWeight.Bold, color = Red40)
                }
            }
        }
    }
}
