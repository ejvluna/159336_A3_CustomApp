// ui/theme/Color.kt

/**
 * Color definitions for the Verifica app's theme.
 *
 * This file contains all color constants used throughout the app,
 * following Material Design 3 theming principles.
 *
 * Colors are organized by:
 * - Primary/Secondary/Tertiary colors
 * - Neutral colors
 * - Status colors (success, warning, error)
 *
 * All colors are defined as Compose Color objects for consistent theming.
 */

package com.example.customapp.ui.theme

import androidx.compose.ui.graphics.Color

// Modern Teal/Coral Color Palette
// Primary: Deep Teal (trustworthy, modern)
val Teal80 = Color(0xFFB2EBF2)      // Light teal for dark mode
val Teal40 = Color(0xFF00897B)      // Deep teal for light mode (primary)

// Accent: Vibrant Coral (energetic, draws attention)
val Coral80 = Color(0xFFFFB3BA)     // Light coral for dark mode
val Coral40 = Color(0xFFFF6B6B)     // Vibrant coral for light mode (accent)

// Secondary: Soft Blue-Grey (neutral, complementary)
val BlueGrey80 = Color(0xFFB0BEC5)
val BlueGrey40 = Color(0xFF455A64)

// Status Colors
val StatusTrue = Color(0xFF2E7D32)      // Green
val StatusFalse = Color(0xFFC62828)     // Red
val StatusMisleading = Color(0xFFF57C00) // Orange
val StatusUnverified = Color(0xFF616161) // Grey

