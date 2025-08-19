package com.example.bilance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
                    navController?.navigate("notifications")
                }) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Purple80.copy(alpha = 0.23f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bell),
                            contentDescription = "Notifications",
                            tint = SurfaceWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
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
                            // Navigate to edit profile screen
                            // navController?.navigate("edit_profile")
                        }
                    )

                    ProfileMenuItem(
                        icon = R.drawable.ic_security,
                        title = "Security",
                        iconColor = Purple80.copy(alpha = 0.85f),
                        onClick = {
                            // Navigate to security settings
                            // navController?.navigate("security_settings")
                        }
                    )

                    ProfileMenuItem(
                        icon = R.drawable.ic_setting,
                        title = "Settings",
                        iconColor = Purple80.copy(alpha = 0.66f),
                        onClick = {
                            // Show settings options
                            navController?.navigate("settings")
                        }
                    )

                    ProfileMenuItem(
                        icon = R.drawable.ic_help,
                        title = "Help",
                        iconColor = Purple80.copy(alpha = 0.3f),
                        onClick = {
                            // Navigate to help screen
                            // navController?.navigate("help")
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
                
                // App Version
                ProfileMenuItem(
                    icon = R.drawable.ic_help,
                    title = "App Version",
                    iconColor = Purple80.copy(alpha = 0.3f),
                    onClick = { }
                )
                
                // Privacy Policy
                ProfileMenuItem(
                    icon = R.drawable.ic_security,
                    title = "Privacy Policy",
                    iconColor = Purple80.copy(alpha = 0.5f),
                    onClick = { }
                )
                
                // About
                ProfileMenuItem(
                    icon = R.drawable.ic_help,
                    title = "About Bilance",
                    iconColor = Purple80.copy(alpha = 0.7f),
                    onClick = { }
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

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    BilanceTheme {
        ProfileScreen(navController = navController)
    }
}