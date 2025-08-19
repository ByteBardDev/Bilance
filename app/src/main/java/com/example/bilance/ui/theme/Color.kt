package com.example.bilance.ui.theme

import androidx.compose.ui.graphics.Color

// Modern Finance App Color Palette - Inspired by Figma Design Systems
// Primary Colors - Main brand colors
val PrimaryBlue = Color(0xFF1E3A8A) // Deep professional blue
val PrimaryBlueLight = Color(0xFF3B82F6) // Lighter blue for accents
val SecondaryPurple = Color(0xFF7C3AED) // Modern purple accent
val AccentGreen = Color(0xFF10B981) // Success/income green
val AccentRed = Color(0xFFEF4444) // Expense/error red
val AccentOrange = Color(0xFFF59E0B) // Warning/pending orange

// Background Colors
val BackgroundPrimary = Color(0xFFF8FAFC) // Light gray background
val BackgroundSecondary = Color(0xFFFFFFFF) // Pure white
val BackgroundDark = Color(0xFF0F172A) // Dark mode background
val SurfaceElevated = Color(0xFFFFFFFF) // Elevated surfaces/cards

// Text Colors
val TextPrimary = Color(0xFF1E293B) // Primary text color
val TextSecondary = Color(0xFF64748B) // Secondary text
val TextOnPrimary = Color(0xFFFFFFFF) // Text on colored backgrounds
val TextMuted = Color(0xFF94A3B8) // Muted/placeholder text

// Gradient Colors
val GradientStart = Color(0xFF6366F1) // Indigo
val GradientEnd = Color(0xFF8B5CF6) // Purple
val GradientIncomeStart = Color(0xFF10B981) // Green
val GradientIncomeEnd = Color(0xFF059669) // Darker green
val GradientExpenseStart = Color(0xFFEF4444) // Red
val GradientExpenseEnd = Color(0xFFDC2626) // Darker red

// Card & Component Colors
val CardBackground = Color(0xFFFFFFFF)
val CardShadow = Color(0x1A000000) // 10% black for shadows
val DividerColor = Color(0xFFE2E8F0)
val BorderColor = Color(0xFFCBD5E1)

// Status Colors
val SuccessGreen = Color(0xFF059669)
val StatusErrorRed = Color(0xFFDC2626)
val WarningYellow = Color(0xFFF59E0B)
val InfoBlue = Color(0xFF0EA5E9)

// Old colors for backward compatibility (gradually phase out)
val Purple80 = SecondaryPurple
val PurpleGrey80 = Color(0xFFCBD5E1)
val Pink80 = Color(0xFFF472B6)
val Purple40 = PrimaryBlue
val PurpleGrey40 = TextSecondary
val Pink40 = Color(0xFFEC4899)

// Legacy color mappings for existing code
val PrimaryPurple = PrimaryBlue
val AccentPurple = SecondaryPurple
val DeepBlue = PrimaryBlue
val LightPurple = PurpleGrey80
val OffWhite = BackgroundPrimary
val BackgroundLight = BackgroundPrimary
val SurfaceWhite = BackgroundSecondary
val OnPrimaryDark = TextPrimary
val TextBody = TextPrimary
val TextGrey = TextSecondary
val ErrorRed = StatusErrorRed
val PurpleFaint = Color(0xFFF1F5F9)

// Splash Screen Colors
val SplashBackgroundBlue = PrimaryBlue
val SplashOverlayBlack = Color(0x33000000) // 20% opacity black

// Launch Screen Colors
val LaunchBackgroundLight = BackgroundPrimary
val LaunchButtonBlue = PrimaryBlueLight
val LaunchButtonLight = BackgroundPrimary
val LaunchTextDark = TextPrimary
val LaunchTextBlue = PrimaryBlueLight
val LaunchTextLight = TextOnPrimary
val LaunchLinkText = TextSecondary