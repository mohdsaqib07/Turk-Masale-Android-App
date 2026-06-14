package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Red40
import com.example.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ShopViewModel, onNavigate: (String) -> Unit) {
    val user by viewModel.currentUser.collectAsState()
    val addresses by viewModel.getUserAddresses().collectAsState()
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var newAddress by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    if (user != null) {
                        TextButton(onClick = { viewModel.logout() }) { Text("Logout", color = Red40) }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (user == null) {
                Text("Login to your account", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = { if (name.isNotBlank() && email.isNotBlank()) viewModel.loginUser(name, email) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Red40)
                ) { Text("Sign In / Sign Up") }
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(48.dp), tint = Red40)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(user!!.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(user!!.email, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
                
                Button(
                    onClick = { onNavigate("wishlist") },
                    modifier = Modifier.fillMaxWidth()
                ) { 
                    Icon(Icons.Default.Favorite, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("My Wishlist")
                }
                
                Text("Saved Addresses", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(addresses) { addr ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(addr.fullAddress, modifier = Modifier.padding(16.dp))
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = newAddress,
                                onValueChange = { newAddress = it },
                                label = { Text("New Address") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { if (newAddress.isNotBlank()) { viewModel.addAddress(newAddress); newAddress = "" } }) {
                                Text("Add")
                            }
                        }
                    }
                }
            }
        }
    }
}
