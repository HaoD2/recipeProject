package com.example.recipesproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.recipesproject.ui.theme.RecipesProjectTheme
import com.example.recipesproject.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationApp()
        }
    }
}

@Composable
fun NavigationApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "inventory"
            ) {
                composable("inventory") { InventoryScreen() }
                composable("shopping") { ShoppingScreen() }
                composable("recipes") { RecipeScreen() }
                composable("profile") { ProfileScreen() }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val items = listOf(
        Triple("inventory", "Inventory", Icons.Default.Home),
        Triple("shopping", "Shopping", Icons.Default.ShoppingCart),
        Triple("recipes", "Recipes", Icons.Default.List),
        Triple("profile", "Profile", Icons.Default.Person)
    )

    Surface(color = MaterialTheme.colorScheme.surface) {
        NavigationBar {
            items.forEach { (route, label, icon) ->
                NavigationBarItem(
                    selected = currentRoute == route,
                    onClick = {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    label = { Text(label) },
                    icon = { Icon(icon, contentDescription = label) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen() {
    var searchText by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    val inventoryList = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                        modifier = Modifier.fillMaxWidth(0.9f),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (inventoryList.isEmpty()) {
                Text("", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(inventoryList) { item ->
                        InventoryCard(item)
                    }
                }
            }
        }

    }
    if (showAddDialog) {
        AddInventoryButton(
            onDismiss = { showAddDialog = false },
            inventoryList = inventoryList
        )
    }
}

@Composable
fun InventoryCard(item: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { /* to do */ }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "More Options"
                )
            }
        }
    }
}

@Composable
fun AddInventoryButton(onDismiss: () -> Unit, inventoryList: MutableList<String>) {
    var newItemInventory by remember { mutableStateOf("") }
    var newExpireDateInventory by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add Ingredient") },
        text = {
            Column {
                TextField(
                    value = newItemInventory,
                    onValueChange = { newItemInventory = it },
                    label = { Text("Enter Ingredient Name") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = newExpireDateInventory,
                    onValueChange = { newExpireDateInventory = it },
                    label = { Text("Enter Expiry Date") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (newItemInventory.isNotBlank()) {
                    inventoryList.add("$newItemInventory (Expires: $newExpireDateInventory)")
                    onDismiss()
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen() {
    var showAddDialog by remember { mutableStateOf(false) }
    val shoppingList = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                        Text(" Add Item", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (shoppingList.isEmpty()) {
                Text("", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(shoppingList) { item ->
                        ShoppingCard(item)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddShoppingButton(
            onDismiss = { showAddDialog = false },
            shoppingList = shoppingList
        )
    }
}

@Composable
fun AddShoppingButton(onDismiss: () -> Unit, shoppingList: MutableList<String>) {
    var newitemShopping by remember { mutableStateOf("") }
    var newquantityShopping by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add to Shopping List") },
        text = {
            Column {
                TextField(
                    value = newitemShopping,
                    onValueChange = { newitemShopping = it },
                    label = { Text("Enter Item Name") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = newquantityShopping,
                    onValueChange = { newquantityShopping = it },
                    label = { Text("Enter Quantity") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (newitemShopping.isNotBlank()) {
                    shoppingList.add("$newitemShopping (Quantities: $newquantityShopping)")
                    onDismiss()
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ShoppingCard(item: String) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { /* to do */ }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "More Options"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(viewModel: RecipeViewModel = hiltViewModel()) {
    var selectedType by remember { mutableStateOf("") }
    var selectedCuisine by remember { mutableStateOf("") }

    val recipes by viewModel.recipes.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Recipes") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Type", style = MaterialTheme.typography.titleMedium)
            SlidableButtonRow(
                options = listOf("Breakfast", "Lunch", "Dinner"),
                selectedOption = selectedType,
                onSelect = { selectedType = it }
            )
            Text("Cuisine", style = MaterialTheme.typography.titleMedium)
            SlidableButtonRow(
                options = listOf("Chinese", "Japanese", "Western"),
                selectedOption = selectedCuisine,
                onSelect = { selectedCuisine = it }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* to do */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Generate Recipe")
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (recipes.isEmpty()) {
                Text("No Recipes Found", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                    items(recipes) { recipe ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = recipe.recipeName, style = MaterialTheme.typography.titleLarge)
                                Text(text = "Type: ${recipe.mealType} | Cuisine: ${recipe.mealCuisine}", style = MaterialTheme.typography.bodyMedium)

                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SlidableButtonRow(
    options: List<String>,
    selectedOption: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(options) { option ->
            Button(
                onClick = { onSelect(option) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedOption == option) MaterialTheme.colorScheme.primary else Color.Gray
                )
            ) {
                Text(option)
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    val avatarImage = painterResource(id = R.drawable.ic_launcher_foreground)
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = avatarImage,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "User Name",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Email: user@example.com")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    NavigationApp()
}
