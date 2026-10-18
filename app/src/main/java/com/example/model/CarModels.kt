package com.example.model

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val rateFromUsd: Double,
    val label: String
) {
    USD("USD", "$", 1.0, "US Dollar ($)"),
    PKR("PKR", "₨", 278.50, "Pakistani Rupee (₨)"),
    AED("AED", "د.إ", 3.67, "UAE Dirham (د.إ)"),
    EUR("EUR", "€", 0.92, "Euro (€)"),
    GBP("GBP", "£", 0.79, "British Pound (£)");

    fun formatPrice(usdPrice: Double): String {
        val converted = usdPrice * rateFromUsd
        return when (this) {
            USD -> String.format(java.util.Locale.US, "$%,.0f", converted)
            PKR -> String.format(java.util.Locale.US, "₨%,.0f", converted)
            AED -> String.format(java.util.Locale.US, "د.إ%,.0f", converted)
            EUR -> String.format(java.util.Locale.US, "€%,.0f", converted)
            GBP -> String.format(java.util.Locale.US, "£%,.0f", converted)
        }
    }
}

enum class UnitSystem(
    val speedUnit: String,
    val distanceUnit: String
) {
    METRIC("km/h", "km"),
    IMPERIAL("mph", "mi");

    fun formatSpeed(speedKmh: Int): String {
        return if (this == METRIC) {
            "$speedKmh km/h"
        } else {
            val mph = (speedKmh * 0.621371).toInt()
            "$mph mph"
        }
    }
}

data class Car(
    val id: String,
    val brand: String,
    val model: String,
    val variant: String = "",
    val year: Int,
    val bodyType: String, // Supercar, Sports, SUV, Sedan, Hypercar, Electric, Luxury, Hatchback
    val priceUsd: Double,
    val fuelType: String, // Electric, Hybrid, Twin-Turbo V8, Naturally Aspirated V12, Quad-Turbo W16, Petrol, Diesel
    val engine: String,
    val horsepower: Int,
    val torqueNm: Int,
    val transmission: String,
    val drivetrain: String, // AWD, RWD, FWD
    val topSpeedKmh: Int,
    val acceleration0to100: Double, // in seconds
    val mileage: String, // e.g. "11.2 L/100km" or "18.2 kWh/100km"
    val dimensions: String, // e.g. "4,705 × 1,973 × 1,189 mm"
    val wheelbase: String,
    val weightKg: Int,
    val groundClearance: String,
    val bootSpace: String,
    val seating: Int,
    val tyreSize: String,
    val brakes: String,
    val suspension: String,
    val safetyRating: Double = 5.0,
    val features: List<String>,
    val images: List<String>,
    val description: String,
    val country: String,
    val rating: Double = 4.9,
    val reviewsCount: Int = 128,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isElectric: Boolean = false,
    val isSupercar: Boolean = false,
    val isSports: Boolean = false,
    val isSUV: Boolean = false,
    val isBudgetFriendly: Boolean = false
) {
    val fullName: String get() = if (variant.isNotBlank()) "$brand $model $variant" else "$brand $model"
}

data class Brand(
    val id: String,
    val name: String,
    val logoUrl: String,
    val country: String,
    val foundedYear: Int,
    val headquarters: String,
    val description: String,
    val totalModels: Int
)

data class Review(
    val id: String,
    val carId: String,
    val userName: String,
    val userAvatar: String,
    val rating: Float,
    val date: String,
    val title: String,
    val comment: String,
    val likes: Int = 0,
    val isLikedByUser: Boolean = false
)

data class UserCollection(
    val id: String,
    val name: String,
    val description: String,
    val carIds: List<String>,
    val iconName: String = "Garage"
)

data class UserProfile(
    val id: String = "user_default",
    val name: String = "Alex Vance",
    val email: String = "alex.vance@autoverse.io",
    val phoneNumber: String = "+1 (555) 019-2834",
    val bio: String = "Supercar enthusiast, collector, and track day driver. Passionate about engineering excellence.",
    val location: String = "Monaco & London",
    val dreamCar: String = "Ferrari SF90 XX Stradale",
    val avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
    val preferredCurrency: AppCurrency = AppCurrency.USD,
    val unitSystem: UnitSystem = UnitSystem.METRIC,
    val language: String = "English",
    val isAdmin: Boolean = false,
    val favoriteCarIds: Set<String> = emptySet(),
    val compareCarIds: List<String> = emptyList(),
    val recentlyViewedCarIds: List<String> = emptyList()
)

data class AutomotiveAd(
    val id: String,
    val sponsorName: String,
    val partnerBadge: String,
    val headline: String,
    val subline: String,
    val ctaText: String,
    val imageUrl: String,
    val accentColorHex: Long = 0xFF0A84FF
)


enum class CarSort(val label: String) {
    POPULARITY("Popularity"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    HORSEPOWER("Horsepower (Highest)"),
    ACCELERATION("0-100 km/h (Fastest)"),
    NEWEST_YEAR("Newest Model Year")
}

data class CarFilter(
    val query: String = "",
    val selectedBrand: String? = null,
    val selectedBodyType: String? = null,
    val selectedFuelType: String? = null,
    val selectedCountry: String? = null,
    val minPrice: Double = 0.0,
    val maxPrice: Double = 5_000_000.0,
    val minHorsepower: Int = 0,
    val maxHorsepower: Int = 2000,
    val sort: CarSort = CarSort.POPULARITY
)
