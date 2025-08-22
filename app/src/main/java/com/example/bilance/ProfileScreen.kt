package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bilance.data.BilanceDatabase
import com.example.bilance.data.User
import com.example.bilance.ui.theme.BilanceTheme
import com.example.bilance.ui.theme.LeagueSpartan
import com.example.bilance.ui.theme.Poppins
import com.example.bilance.ui.theme.Purple40
import com.example.bilance.ui.theme.Purple80
import com.example.bilance.ui.theme.PurpleGrey40
import com.example.bilance.ui.theme.PurpleGrey80
import com.example.bilance.ui.theme.SurfaceWhite
import com.example.bilance.ui.theme.TextMuted
import kotlinx.coroutines.launch

// Update the ProfileScreen header section to match TransactionScreen style

// In ProfileScreen.kt - Update the ProfileScreen function
@Composable
fun ProfileScreen(navController: NavController? = null, userEmail: String = "") {
    val context = LocalContext.current
    val database = remember { BilanceDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()
    var userProfile by remember { mutableStateOf<User?>(null) }
    
    // Load user profile from database using the passed email
    LaunchedEffect(userEmail) {
        scope.launch {
            try {
                if (userEmail.isNotEmpty()) {
                    // Get user by the actual email passed from login/signup
                    userProfile = database.userDao().getUserByEmail(userEmail)
                }
                
                // Fallback if no user found or email is empty
                if (userProfile == null) {
                    userProfile = database.userDao().getUserByEmail("demo@bilance.com") ?: User(
                        id = 25030024,
                        fullName = "John Smith",
                        email = userEmail.ifEmpty { "john.smith@example.com" },
                        mobileNumber = "+1234567890",
                        dateOfBirth = "01/01/1990",
                        password = ""
                    )
                }
            } catch (e: Exception) {
                // Fallback if database access fails
                userProfile = User(
                    id = 25030024,
                    fullName = "John Smith",
                    email = userEmail.ifEmpty { "john.smith@example.com" },
                    mobileNumber = "+1234567890",
                    dateOfBirth = "01/01/1990",
                    password = ""
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background purple section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Purple40)
        ) {
            // Header - matching TransactionScreen style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    navController?.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Profile",
                    color = SurfaceWhite,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                IconButton(onClick = { 
                    navController?.navigate("notifications_from_profile")
                }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Space for profile picture - reduced since header takes more space now
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // White card that starts from middle of profile image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.79f) // Takes up bottom 75% of screen
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Space for the overlapping profile picture
                Spacer(modifier = Modifier.height(80.dp))
                
                // User info section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // User Name
                    Text(
                        text = userProfile?.fullName ?: "John Smith",
                        color = Purple80,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // User Email - Show actual email instead of ID
                    Text(
                        text = userProfile?.email ?: userEmail,
                        color = TextMuted,
                        fontFamily = LeagueSpartan,
                        fontSize = 16.sp
                    )
                    
                    // User ID (optional - you can keep this or remove it)
                    Text(
                        text = "ID: ${userProfile?.id ?: "25030024"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PurpleGrey40,
                        fontFamily = LeagueSpartan,
                        fontSize = 13.sp
                    )
                }
                
                // ... rest of your existing menu items code remains the same
                Spacer(modifier = Modifier.height(32.dp))
                
                // Menu items
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    ProfileMenuItem(
                        icon = R.drawable.ic_editprofile,
                        title = "Edit Profile",
                        iconColor = Purple80,
                        onClick = {
                            navController?.navigate("edit_profile")
                        }
                    )

                    ProfileMenuItem(
                        icon = R.drawable.ic_setting,
                        title = "Settings",
                        iconColor = Purple80.copy(alpha = 0.66f),
                        onClick = {
                            navController?.navigate("settings")
                        }
                    )

                    ProfileMenuItem(
                        icon = R.drawable.ic_logout,
                        title = "Logout",
                        iconColor = PurpleGrey80.copy(alpha = 0.8f),
                        onClick = {
                            // Handle logout - navigate back to launch screen
                            UserPreferences.clearSession()
                            navController?.navigate("launch") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
        
        // Profile Picture - positioned to overlap the card
        Box(
            modifier = Modifier
                .size(122.dp)
                .align(Alignment.TopCenter)
                .offset(y = 120.dp) // Adjusted for new header height
                .clip(RoundedCornerShape(61.dp))
                .background(Purple80),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = "Profile Picture",
                tint = SurfaceWhite,
                modifier = Modifier.size(62.dp)
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: Int,
    title: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with colored background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(iconColor.copy(alpha = 0.11f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color.Unspecified, // Remove tint to use original colors
                modifier = Modifier.size(45.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Menu item title
        Text(
            text = title,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            color = Purple80,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Arrow indicator - using simple text
        Text(
            text = "›",
            color = TextMuted,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun SettingsScreen(navController: NavController? = null) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showThresholdDialog by remember { mutableStateOf(false) }
    var showBalanceDialog by remember { mutableStateOf(false) }
    var thresholdText by remember { mutableStateOf("") }
    var balanceText by remember { mutableStateOf("") }
    val currentThreshold = UserPreferences.getMonthlyExpenseThreshold()
    val currentBalance = UserPreferences.getInitialBalance()
    if (showThresholdDialog) {
        AlertDialog(
            onDismissRequest = { showThresholdDialog = false },
            title = { Text("Update Monthly Expense Limit") },
            text = {
                Column {
                    Text("Current: ₹%.2f".format(currentThreshold))
                    OutlinedTextField(
                        value = thresholdText,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) thresholdText = it },
                        label = { Text("New Limit") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val value = thresholdText.toDoubleOrNull()
                        if (value != null) UserPreferences.setMonthlyExpenseThreshold(value)
                        showThresholdDialog = false
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showThresholdDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showBalanceDialog) {
        AlertDialog(
            onDismissRequest = { showBalanceDialog = false },
            title = { Text("Update Current Account Balance") },
            text = {
                Column {
                    Text("Current: ₹%.2f".format(currentBalance))
                    OutlinedTextField(
                        value = balanceText,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) balanceText = it },
                        label = { Text("New Balance") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val value = balanceText.toDoubleOrNull()
                        if (value != null) UserPreferences.setInitialBalance(value)
                        showBalanceDialog = false
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showBalanceDialog = false }) { Text("Cancel") }
            }
        )
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Clear All Data") },
            text = { Text("This will delete all your transactions and reset the app. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        // Clear user preferences
                        UserPreferences.clearSession()
                        UserPreferences.setInitialBalance(0.0)
                        // Navigate back to launch
                        navController?.navigate("launch") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Purple40)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    navController?.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Settings",
                    color = SurfaceWhite,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.size(48.dp)) // Balance the layout
            }
        }
        
        // White card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.79f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "App Settings",
                    color = Purple80,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                
                // Update Monthly Expense Limit
                ProfileMenuItem(
                    icon = R.drawable.ic_editprofile,
                    title = "Update Monthly Expense Limit",
                    iconColor = Purple80.copy(alpha = 0.5f),
                    onClick = { showThresholdDialog = true }
                )

                // Update Current Account Balance
                ProfileMenuItem(
                    icon = R.drawable.ic_salary,
                    title = "Update Current Account Balance",
                    iconColor = Purple80.copy(alpha = 0.5f),
                    onClick = { showBalanceDialog = true }
                )

                // App Version
                ProfileMenuItem(
                    icon = R.drawable.ic_help,
                    title = "App Version",
                    iconColor = Purple80.copy(alpha = 0.3f),
                    onClick = { navController?.navigate("app_version") }
                )

                // Privacy Policy
                ProfileMenuItem(
                    icon = R.drawable.ic_security,
                    title = "Privacy Policy",
                    iconColor = Purple80.copy(alpha = 0.5f),
                    onClick = { navController?.navigate("privacy_policy") }
                )

                // About
                ProfileMenuItem(
                    icon = R.drawable.ic_help,
                    title = "About Bilance",
                    iconColor = Purple80.copy(alpha = 0.7f),
                    onClick = { navController?.navigate("about_bilance") }
                )

                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Danger Zone",
                    color = Color.Red,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Clear All Data
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDeleteDialog = true },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Red.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.Red.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logout),
                                contentDescription = "Clear Data",
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Clear All Data",
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Red,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Delete all transactions and reset app",
                                fontFamily = Poppins,
                                color = Color.Red.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileScreen(navController: NavController? = null, userEmail: String = "") {
    val context = LocalContext.current
    val database = remember { BilanceDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()
    var userProfile by remember { mutableStateOf<User?>(null) }
    
    // Form state
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pushNotifications by remember { mutableStateOf(true) }
    var darkTheme by remember { mutableStateOf(true) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Load user profile
    LaunchedEffect(userEmail) {
        scope.launch {
            try {
                if (userEmail.isNotEmpty()) {
                    userProfile = database.userDao().getUserByEmail(userEmail)
                }
                
                if (userProfile == null) {
                    userProfile = database.userDao().getUserByEmail("demo@bilance.com") ?: User(
                        id = 25030024,
                        fullName = "John Smith",
                        email = userEmail.ifEmpty { "john.smith@example.com" },
                        mobileNumber = "+44 555 5555 55",
                        dateOfBirth = "01/01/1990",
                        password = ""
                    )
                }
                
                // Set form values
                username = userProfile?.fullName ?: "John Smith"
                phone = userProfile?.mobileNumber ?: "+44 555 5555 55"
                email = userProfile?.email ?: userEmail
            } catch (e: Exception) {
                // Fallback values
                username = "John Smith"
                phone = "+44 555 5555 55"
                email = userEmail.ifEmpty { "example@example.com" }
            }
        }
    }
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Profile Updated") },
            text = { Text("Your profile has been successfully updated.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        navController?.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background gradient
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            com.example.bilance.ui.theme.GradientStart,
                            com.example.bilance.ui.theme.GradientEnd
                        )
                    )
                )
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, start = 24.dp, end = 24.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController?.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = com.example.bilance.ui.theme.TextOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = "Edit My Profile",
                    color = com.example.bilance.ui.theme.TextOnPrimary,
                    fontSize = 20.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { navController?.navigate("notifications_from_profile") },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(com.example.bilance.ui.theme.TextOnPrimary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = com.example.bilance.ui.theme.TextOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        // Main content card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            colors = CardDefaults.cardColors(
                containerColor = com.example.bilance.ui.theme.BackgroundPrimary
            ),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Profile Picture Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(60.dp))
                            .background(Purple80),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_user),
                            contentDescription = "Profile Picture",
                            tint = SurfaceWhite,
                            modifier = Modifier.size(60.dp)
                        )
                        
                        // Camera icon overlay
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(com.example.bilance.ui.theme.AccentGreen)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Photo",
                                tint = SurfaceWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = username,
                        color = com.example.bilance.ui.theme.TextPrimary,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    
                    Text(
                        text = "ID: ${userProfile?.id ?: "25030024"}",
                        color = com.example.bilance.ui.theme.TextSecondary,
                        fontFamily = LeagueSpartan,
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Account Settings Section
                Text(
                    text = "Account Settings",
                    color = com.example.bilance.ui.theme.TextPrimary,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple80,
                        unfocusedBorderColor = com.example.bilance.ui.theme.BorderColor,
                        focusedLabelColor = Purple80,
                        unfocusedLabelColor = com.example.bilance.ui.theme.TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Phone field
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple80,
                        unfocusedBorderColor = com.example.bilance.ui.theme.BorderColor,
                        focusedLabelColor = Purple80,
                        unfocusedLabelColor = com.example.bilance.ui.theme.TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple80,
                        unfocusedBorderColor = com.example.bilance.ui.theme.BorderColor,
                        focusedLabelColor = Purple80,
                        unfocusedLabelColor = com.example.bilance.ui.theme.TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Toggle switches
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Push Notifications",
                        color = com.example.bilance.ui.theme.TextPrimary,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Switch(
                        checked = pushNotifications,
                        onCheckedChange = { pushNotifications = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Purple80,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray
                        )
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Turn Dark Theme",
                        color = com.example.bilance.ui.theme.TextPrimary,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = { darkTheme = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Purple80,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray
                        )
                    )
                }
                
                // Update Profile Button
                Button(
                    onClick = {
                        // Update user profile in database
                        scope.launch {
                            try {
                                userProfile?.let { user ->
                                    val updatedUser = user.copy(
                                        fullName = username,
                                        email = email,
                                        mobileNumber = phone
                                    )
                                    database.userDao().updateUser(updatedUser)
                                }
                                showSuccessDialog = true
                            } catch (e: Exception) {
                                // Handle error
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple80
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Update Profile",
                        color = SurfaceWhite,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AppVersionScreen(navController: NavController? = null) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Purple40)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    navController?.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "App Version",
                    color = SurfaceWhite,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.size(48.dp)) // Balance the layout
            }
        }
        
        // White card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.79f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // App Icon
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Purple80),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user),
                        contentDescription = "App Icon",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(60.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Bilance",
                    color = Purple80,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Version 1.0.0",
                    color = TextMuted,
                    fontFamily = LeagueSpartan,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Version Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Purple80.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Version Information",
                            color = Purple80,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        VersionInfoRow("Version", "1.0.0")
                        VersionInfoRow("Build", "2024.01.001")
                        VersionInfoRow("Release Date", "January 2024")
                        VersionInfoRow("Platform", "Android")
                        VersionInfoRow("Min SDK", "API 24")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // What's New
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E8)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "What's New in 1.0.0",
                            color = Color(0xFF2E7D32),
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "• Initial release of Bilance\n• Expense and income tracking\n• SMS transaction parsing\n• Category management\n• Analytics and insights\n• Modern, intuitive UI",
                            color = Color(0xFF2E7D32),
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VersionInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontFamily = Poppins,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Purple80,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun PrivacyPolicyScreen(navController: NavController? = null) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Purple40)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    navController?.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Privacy Policy",
                    color = SurfaceWhite,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.size(48.dp)) // Balance the layout
            }
        }
        
        // White card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.79f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                item {
                    Text(
                        text = "Privacy Policy",
                        color = Purple80,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "Last updated: January 2024",
                        color = TextMuted,
                        fontFamily = LeagueSpartan,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
                
                item {
                    PolicySection(
                        title = "Information We Collect",
                        content = "Bilance collects and stores your financial transaction data locally on your device. This includes:\n\n• Transaction amounts and descriptions\n• Category classifications\n• SMS messages (with your permission)\n• User profile information\n\nWe do not collect or transmit your personal financial data to external servers."
                    )
                }
                
                item {
                    PolicySection(
                        title = "How We Use Your Information",
                        content = "Your data is used exclusively for:\n\n• Displaying your financial overview\n• Generating spending analytics\n• Providing transaction categorization\n• Improving app functionality\n\nAll processing happens locally on your device."
                    )
                }
                
                item {
                    PolicySection(
                        title = "Data Security",
                        content = "We prioritize your data security:\n\n• All data is stored locally on your device\n• No data is transmitted to external servers\n• SMS access requires explicit permission\n• Data is encrypted using Android's built-in security"
                    )
                }
                
                item {
                    PolicySection(
                        title = "Third-Party Services",
                        content = "Bilance does not share your data with third-party services. The app operates independently and does not integrate with external financial institutions or data processors."
                    )
                }
                
                item {
                    PolicySection(
                        title = "Your Rights",
                        content = "You have complete control over your data:\n\n• Access all stored data within the app\n• Delete individual transactions\n• Clear all data through settings\n• Revoke SMS permissions anytime\n• Export your data (coming soon)"
                    )
                }
                
                item {
                    PolicySection(
                        title = "Contact Us",
                        content = "If you have questions about this privacy policy, please contact us at:\n\nEmail: privacy@bilance.app\n\nWe're committed to protecting your privacy and will respond to all inquiries promptly."
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = title,
            color = Purple80,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            color = TextMuted,
            fontFamily = Poppins,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun AboutBilanceScreen(navController: NavController? = null) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Purple40)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    navController?.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "About Bilance",
                    color = SurfaceWhite,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.size(48.dp)) // Balance the layout
            }
        }
        
        // White card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.79f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App Logo
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Purple80),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_user),
                                contentDescription = "Bilance Logo",
                                tint = SurfaceWhite,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Bilance",
                            color = Purple80,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                        
                        Text(
                            text = "Your Personal Finance Manager",
                            color = TextMuted,
                            fontFamily = LeagueSpartan,
                            fontSize = 16.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                item {
                    AboutSection(
                        title = "Our Mission",
                        content = "Bilance is designed to simplify personal finance management. We believe everyone deserves a clear, intuitive way to track their spending, understand their financial patterns, and make informed decisions about their money."
                    )
                }
                
                item {
                    AboutSection(
                        title = "Key Features",
                        content = "• Smart SMS Transaction Parsing\n• Automatic Category Classification\n• Real-time Expense Analytics\n• Intuitive Visual Reports\n• Secure Local Data Storage\n• Modern, Accessible Design"
                    )
                }
                
                item {
                    AboutSection(
                        title = "Privacy First",
                        content = "Your financial data is personal and sensitive. That's why Bilance operates entirely on your device. We don't collect, store, or transmit your financial information to external servers. Your data stays yours."
                    )
                }
                
                item {
                    AboutSection(
                        title = "Technology",
                        content = "Built with modern Android development practices using Jetpack Compose, Room Database, and Material Design 3. Bilance leverages local AI for transaction categorization and provides a seamless user experience."
                    )
                }
                
                item {
                    AboutSection(
                        title = "Support",
                        content = "We're committed to providing excellent support. If you encounter any issues or have suggestions for improvements, please reach out to us. Your feedback helps make Bilance better for everyone."
                    )
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Purple80.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Contact Information",
                                color = Purple80,
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            ContactInfoRow("Email", "support@bilance.app")
                            ContactInfoRow("Website", "bilance.app")
                            ContactInfoRow("Twitter", "@bilance_app")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "© 2024 Bilance. All rights reserved.",
                        color = TextMuted,
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AboutSection(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = title,
            color = Purple80,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            color = TextMuted,
            fontFamily = Poppins,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun ContactInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontFamily = Poppins,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Purple80,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    BilanceTheme {
        ProfileScreen(navController = navController)
    }
}