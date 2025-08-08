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
        // Background blue section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF283A5F))
        ) {
            // Header - matching TransactionScreen style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    navController?.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Profile",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { 
                    navController?.navigate("notifications")
                }) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            // Space for profile picture - reduced since header takes more space now
            Spacer(modifier = Modifier.height(20.dp))
        }
        
        // White card that starts from middle of profile image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f) // Takes up bottom 75% of screen
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
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
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF283A5F),
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // User Email - Show actual email instead of ID
                    Text(
                        text = userProfile?.email ?: userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280)
                    )
                    
                    // User ID (optional - you can keep this or remove it)
                    Text(
                        text = "ID: ${userProfile?.id ?: "25030024"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF)
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
                        iconColor = Color(0xFF7BB3FF),
                        onClick = {
                            // Navigate to edit profile screen
                            // navController?.navigate("edit_profile")
                        }
                    )

                    ProfileMenuItem(
                        icon = R.drawable.ic_security,
                        title = "Security",
                        iconColor = Color(0xFF4A90E2),
                        onClick = {
                            // Navigate to security settings
                            // navController?.navigate("security_settings")
                        }
                    )

                    ProfileMenuItem(
                        icon = R.drawable.ic_setting,
                        title = "Setting",
                        iconColor = Color(0xFF5A67D8),
                        onClick = {
                            // Navigate to general settings
                            // navController?.navigate("general_settings")
                        }
                    )

                    ProfileMenuItem(
                        icon = R.drawable.ic_help,
                        title = "Help",
                        iconColor = Color(0xFF9CA3AF),
                        onClick = {
                            // Navigate to help screen
                            // navController?.navigate("help")
                        }
                    )

                    ProfileMenuItem(
                        icon = R.drawable.ic_logout,
                        title = "Logout",
                        iconColor = Color(0xFF4A90E2),
                        onClick = {
                            // Handle logout - navigate back to launch screen
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
                .size(140.dp)
                .align(Alignment.TopCenter)
                .offset(y = 145.dp) // Adjusted for new header height
                .clip(RoundedCornerShape(70.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                tint = Color(0xFF283A5F),
                modifier = Modifier.size(60.dp)
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
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with colored background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color.Unspecified, // Remove tint to use original colors
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Menu item title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        // Arrow indicator
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
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