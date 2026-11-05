package com.artha.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryVisual(
    val icon: ImageVector,
    val color: Color,
    val label: String
)

object CategoryVisuals {

    private val ColorMap = mapOf(
        "cat_food_dining" to (Icons.Rounded.Restaurant to Color(0xFFF59E0B)),
        "cat_groceries" to (Icons.Rounded.ShoppingCart to Color(0xFF10B981)),
        "cat_shopping" to (Icons.Rounded.ShoppingBag to Color(0xFFEC4899)),
        "cat_transport" to (Icons.Rounded.DirectionsCar to Color(0xFF3B82F6)),
        "cat_bills" to (Icons.Rounded.ReceiptLong to Color(0xFF6366F1)),
        "cat_housing" to (Icons.Rounded.Home to Color(0xFF8B5CF6)),
        "cat_entertainment" to (Icons.Rounded.Movie to Color(0xFFF43F5E)),
        "cat_health" to (Icons.Rounded.MedicalServices to Color(0xFF14B8A6)),
        "cat_education" to (Icons.Rounded.School to Color(0xFF06B6D4)),
        "cat_travel" to (Icons.Rounded.Flight to Color(0xFF0284C7)),
        "cat_fitness" to (Icons.Rounded.FitnessCenter to Color(0xFFE11D48)),
        "cat_gifts" to (Icons.Rounded.CardGiftcard to Color(0xFFD946EF)),
        "cat_salary" to (Icons.Rounded.Payments to Color(0xFF10B981)),
        "cat_investments" to (Icons.Rounded.TrendingUp to Color(0xFF059669)),
        "cat_freelance" to (Icons.Rounded.Work to Color(0xFF2563EB))
    )

    fun resolve(
        categoryId: String?,
        categoryName: String? = null,
        iconName: String? = null,
        colorHex: String? = null
    ): CategoryVisual {
        // Direct categoryId match
        if (categoryId != null && ColorMap.containsKey(categoryId)) {
            val (icon, color) = ColorMap[categoryId]!!
            return CategoryVisual(
                icon = icon,
                color = if (!colorHex.isNullOrBlank()) parseColor(colorHex, color) else color,
                label = categoryName ?: "Expense"
            )
        }

        // Match by icon name
        val matchedIcon = when (iconName?.lowercase()?.trim()) {
            "restaurant", "food", "dining" -> Icons.Rounded.Restaurant
            "shopping_cart", "groceries", "grocery" -> Icons.Rounded.ShoppingCart
            "shopping_bag", "shopping", "clothes" -> Icons.Rounded.ShoppingBag
            "directions_car", "transport", "fuel", "car", "uber" -> Icons.Rounded.DirectionsCar
            "receipt_long", "bills", "utilities", "electricity" -> Icons.Rounded.ReceiptLong
            "home", "housing", "rent" -> Icons.Rounded.Home
            "movie", "entertainment", "netflix" -> Icons.Rounded.Movie
            "medical_services", "health", "hospital", "pharmacy" -> Icons.Rounded.MedicalServices
            "school", "education", "books" -> Icons.Rounded.School
            "flight", "travel", "vacation" -> Icons.Rounded.Flight
            "fitness_center", "fitness", "gym" -> Icons.Rounded.FitnessCenter
            "card_giftcard", "gifts", "gift" -> Icons.Rounded.CardGiftcard
            "payments", "salary", "wages" -> Icons.Rounded.Payments
            "trending_up", "investments", "sip", "stocks" -> Icons.Rounded.TrendingUp
            "work", "freelance", "business" -> Icons.Rounded.Work
            "swap_horiz", "transfer" -> Icons.Rounded.SwapHoriz
            else -> null
        }

        // Match by category name keywords
        val nameLower = (categoryName ?: "").lowercase()
        val finalIcon = matchedIcon ?: when {
            nameLower.contains("food") || nameLower.contains("dining") || nameLower.contains("restaurant") || nameLower.contains("cafe") -> Icons.Rounded.Restaurant
            nameLower.contains("grocer") || nameLower.contains("supermarket") || nameLower.contains("market") -> Icons.Rounded.ShoppingCart
            nameLower.contains("shop") || nameLower.contains("store") || nameLower.contains("apparel") -> Icons.Rounded.ShoppingBag
            nameLower.contains("car") || nameLower.contains("transport") || nameLower.contains("fuel") || nameLower.contains("petrol") || nameLower.contains("diesel") || nameLower.contains("taxi") || nameLower.contains("uber") -> Icons.Rounded.DirectionsCar
            nameLower.contains("bill") || nameLower.contains("util") || nameLower.contains("electric") || nameLower.contains("wifi") || nameLower.contains("broadband") || nameLower.contains("recharge") -> Icons.Rounded.ReceiptLong
            nameLower.contains("rent") || nameLower.contains("house") || nameLower.contains("home") || nameLower.contains("flat") -> Icons.Rounded.Home
            nameLower.contains("entertain") || nameLower.contains("movie") || nameLower.contains("cinema") || nameLower.contains("sub") || nameLower.contains("netflix") -> Icons.Rounded.Movie
            nameLower.contains("health") || nameLower.contains("medic") || nameLower.contains("doctor") || nameLower.contains("pharmacy") || nameLower.contains("hospital") -> Icons.Rounded.MedicalServices
            nameLower.contains("edu") || nameLower.contains("school") || nameLower.contains("course") || nameLower.contains("college") || nameLower.contains("tuition") -> Icons.Rounded.School
            nameLower.contains("travel") || nameLower.contains("flight") || nameLower.contains("trip") || nameLower.contains("hotel") -> Icons.Rounded.Flight
            nameLower.contains("gym") || nameLower.contains("fit") || nameLower.contains("sport") -> Icons.Rounded.FitnessCenter
            nameLower.contains("salary") || nameLower.contains("income") || nameLower.contains("stipend") -> Icons.Rounded.Payments
            nameLower.contains("invest") || nameLower.contains("stock") || nameLower.contains("fund") || nameLower.contains("sip") -> Icons.Rounded.TrendingUp
            nameLower.contains("transfer") -> Icons.Rounded.SwapHoriz
            else -> Icons.Rounded.Category
        }

        val defaultColor = when (finalIcon) {
            Icons.Rounded.Restaurant -> Color(0xFFF59E0B)
            Icons.Rounded.ShoppingCart -> Color(0xFF10B981)
            Icons.Rounded.ShoppingBag -> Color(0xFFEC4899)
            Icons.Rounded.DirectionsCar -> Color(0xFF3B82F6)
            Icons.Rounded.ReceiptLong -> Color(0xFF6366F1)
            Icons.Rounded.Home -> Color(0xFF8B5CF6)
            Icons.Rounded.Movie -> Color(0xFFF43F5E)
            Icons.Rounded.MedicalServices -> Color(0xFF14B8A6)
            Icons.Rounded.School -> Color(0xFF06B6D4)
            Icons.Rounded.Flight -> Color(0xFF0284C7)
            Icons.Rounded.FitnessCenter -> Color(0xFFE11D48)
            Icons.Rounded.Payments, Icons.Rounded.TrendingUp -> Color(0xFF10B981)
            Icons.Rounded.SwapHoriz -> Color(0xFF64748B)
            else -> Color(0xFF8B5CF6)
        }

        val finalColor = if (!colorHex.isNullOrBlank()) parseColor(colorHex, defaultColor) else defaultColor
        return CategoryVisual(
            icon = finalIcon,
            color = finalColor,
            label = categoryName ?: "Expense"
        )
    }

    private fun parseColor(hex: String, fallback: Color): Color {
        return try {
            val clean = hex.removePrefix("#")
            val fullHex = if (clean.length == 6) "FF$clean" else clean
            Color(fullHex.toLong(16))
        } catch (_: Throwable) {
            fallback
        }
    }
}
