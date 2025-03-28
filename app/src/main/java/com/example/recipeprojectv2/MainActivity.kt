package com.example.recipeprojectv2

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.recipeprojectv2.data.InventoryWorker
import com.example.recipeprojectv2.model.InventoryModel
import com.example.recipeprojectv2.model.RecipeModel
import com.example.recipeprojectv2.model.ShoppingModel
import com.example.recipeprojectv2.ui.theme.RecipeProjectv2Theme
import com.example.recipeprojectv2.utils.JsonUtil
import com.example.recipeprojectv2.viewmodel.InventoryViewModel
import com.example.recipeprojectv2.viewmodel.ShoppingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DatabaseDebug", "Database path: " + applicationContext.getDatabasePath("app_database"))
        scheduleInventoryCheck(this)
        setContent {
            NavigationApp()
        }

    }
}

fun scheduleInventoryCheck(context: Context) {
    Log.d("WorkManagerDebug", "scheduleInventoryCheck DIPANGGIL")
    val workRequest = OneTimeWorkRequestBuilder<InventoryWorker>().build()
    WorkManager.getInstance(context).enqueue(workRequest)
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
fun InventoryScreen(viewModel: InventoryViewModel = hiltViewModel()) {
    var searchText by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val inventoryList by viewModel.filteredInventory.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            viewModel.updateSearchQuery(it) // Update ViewModel dengan teks pencarian
                        },
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
                onClick = { showAddDialog = true }, // Dialog tetap muncul
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (inventoryList.isEmpty()) {
                Text("No items found", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(inventoryList) { item ->
                        InventoryCardWithCountdown(item)
                    }
                }
            }
        }
    }

    // Dialog untuk menambah item tetap ada
    if (showAddDialog) {
        AddInventoryButton(
            onDismiss = { showAddDialog = false },
            onAdd = { name, category, quantity, expire ->
                viewModel.insert(
                    InventoryModel(
                        name = name,
                        category = category,
                        quantity = quantity,
                        expire = expire
                    )
                )
            }
        )
    }
}

@Composable
fun InventoryCardWithCountdown(item: InventoryModel) {
    var remainingDays by remember { mutableStateOf(TimeUnit.MILLISECONDS.toDays(item.expire - System.currentTimeMillis())) }

    LaunchedEffect(item.expire) {
        while (remainingDays > 0) {
            delay(1000) // Update setiap detik
            remainingDays = TimeUnit.MILLISECONDS.toDays(item.expire - System.currentTimeMillis())
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Category: ${item.category}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Quantity: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Expire: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(item.expire))}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (remainingDays > 0) "Days Remaining: $remainingDays" else "Expired!",
                color = if (remainingDays > 0) Color.Black else Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InventoryCard(inventory: InventoryModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = inventory.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Category: ${inventory.category}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Quantity: ${inventory.quantity}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Expire: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(inventory.expire))}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AddInventoryButton(onDismiss: () -> Unit, onAdd: (String, String, String, Long) -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance() // Menggunakan Calendar untuk kompatibilitas
    var expireMillis by remember { mutableStateOf(calendar.timeInMillis) }

    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            expireMillis = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add Inventory Item") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") }
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { datePickerDialog.show() }
                ) {
                    Text("Select Expiry Date: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expireMillis))}")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank() && category.isNotBlank() && quantity.isNotBlank()) {
                    onAdd(name, category, quantity, expireMillis)
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
fun ShoppingScreen(viewModel: ShoppingViewModel = hiltViewModel()) {
    var searchText by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val shoppingList by viewModel.filteredShopping.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            viewModel.updateSearchQuery(it) // Update ViewModel dengan teks pencarian
                        },
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
            if (shoppingList.isEmpty()) {
                Text("No items found", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(shoppingList) { item ->
                        ShoppingCard(
                            item = item,
                            onDelete = { viewModel.delete(it) } // Menyertakan parameter onDelete
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddShoppingButton(
            onDismiss = { showAddDialog = false },
            onAdd = { name, quantity ->
                viewModel.insert(
                    ShoppingModel(
                        name = name,
                        quantity = quantity
                    )
                )
            }
        )
    }
}
@Composable
fun AddShoppingButton(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var newItem by remember { mutableStateOf("") }
    var newQuantity by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Shopping List") },
        text = {
            Column {
                TextField(
                    value = newItem,
                    onValueChange = { newItem = it },
                    label = { Text("Enter Item Name") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = newQuantity,
                    onValueChange = { newQuantity = it },
                    label = { Text("Enter Quantity") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (newItem.isNotBlank()) {
                    onAdd(newItem, newQuantity) // Menyimpan ke database
                    onDismiss()
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun ShoppingCard(item: ShoppingModel, onDelete: (ShoppingModel) -> Unit) {
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
            Text(
                text = "${item.name} (Quantity: ${item.quantity})",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { onDelete(item) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Item"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen() {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf("") }
    var selectedCuisine by remember { mutableStateOf("") }

    val allRecipes = remember { JsonUtil.getRecipes(context) }

    // Gunakan derivedStateOf agar filter otomatis diperbarui saat selectedType atau selectedCuisine berubah
    val filteredRecipes by remember {
        derivedStateOf {
            allRecipes.filter { recipe ->
                (selectedType.isBlank() || recipe.mealType.equals(selectedType, ignoreCase = true)) &&
                        (selectedCuisine.isBlank() || recipe.cuisine.equals(selectedCuisine, ignoreCase = true))
            }
        }
    }

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
                onClick = { /* Tidak perlu memanggil filterRecipes(), karena derivedStateOf sudah mengatur */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Generate Recipe")
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredRecipes.isEmpty()) {
                Text("No Recipes Found", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredRecipes) { recipe ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = recipe.recipeName,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = recipe.mealType,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Cuisine: ${recipe.cuisine}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Ingredients: ${recipe.ingredientsName.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
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
fun GreetingPreview() {
    RecipeProjectv2Theme {
        NavigationApp()
    }
}