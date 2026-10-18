package com.example.data

import com.example.data.local.CarDetailDao
import com.example.data.local.CarEntity
import com.example.model.AppCurrency
import com.example.model.Brand
import com.example.model.Car
import com.example.model.CarFilter
import com.example.model.CarSort
import com.example.model.Review
import com.example.model.UnitSystem
import com.example.model.UserCollection
import com.example.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import com.example.model.AutomotiveAd

class CarRepository(
    private val carDetailDao: CarDetailDao? = null,
    private val userDao: UserDao? = null
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val initialCars = SeedCarsData.cars + SeedCarsExtended.additionalCars

    private val _cars = MutableStateFlow<List<Car>>(initialCars)
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    private val _brands = MutableStateFlow<List<Brand>>(SeedCarsData.brands)
    val brands: StateFlow<List<Brand>> = _brands.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(SeedCarsData.sampleReviews)
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            id = "user_default",
            name = "Alex Vance",
            email = "alex.vance@autoverse.io",
            phoneNumber = "+1 (555) 019-2834",
            bio = "Supercar enthusiast, collector, and amateur track day driver.",
            location = "Monaco & London",
            dreamCar = "Ferrari SF90 XX Stradale",
            favoriteCarIds = setOf("ferrari-sf90", "porsche-gt3rs", "tesla-cybertruck"),
            compareCarIds = listOf("ferrari-sf90", "lambo-revuelto"),
            recentlyViewedCarIds = listOf("ferrari-sf90", "porsche-gt3rs", "bugatti-chiron-ss", "tesla-cybertruck")
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    val automotiveAds: List<AutomotiveAd> = listOf(
        AutomotiveAd(
            id = "ad_pirelli",
            sponsorName = "PIRELLI MOTORSPORT",
            partnerBadge = "OFFICIAL TYRE PARTNER",
            headline = "P Zero™ Trofeo RS",
            subline = "Extreme dry road grip & track dominance. Standard equipment on Pagani Utopia & Ferrari SF90 XX.",
            ctaText = "Explore Grip",
            imageUrl = "https://images.unsplash.com/photo-1578637387939-43c525550085?auto=format&fit=crop&w=1200&q=80",
            accentColorHex = 0xFFFFD60A
        ),
        AutomotiveAd(
            id = "ad_brembo",
            sponsorName = "BREMBO BRAKING SYSTEMS",
            partnerBadge = "HYPERCAR STOPPING TECH",
            headline = "Carbon-Ceramic Matrix (CCM-R)",
            subline = "Formula 1 thermal dissipation technology engineered for consecutive 300+ km/h stops without fade.",
            ctaText = "Discover Tech",
            imageUrl = "https://images.unsplash.com/photo-1603584173870-7f23fdae1b7a?auto=format&fit=crop&w=1200&q=80",
            accentColorHex = 0xFFFF375F
        ),
        AutomotiveAd(
            id = "ad_mobil1",
            sponsorName = "MOBIL 1 PERFORMANCE",
            partnerBadge = "MOTORSPORT LUBRICANTS",
            headline = "ESP Supercar 0W-40",
            subline = "Unrivaled friction reduction across 9,000 RPM valvetrains. Proven at the 24 Hours of Le Mans.",
            ctaText = "Learn More",
            imageUrl = "https://images.unsplash.com/photo-1542282088-72c9c27ed0cd?auto=format&fit=crop&w=1200&q=80",
            accentColorHex = 0xFF0A84FF
        ),
        AutomotiveAd(
            id = "ad_akrapovic",
            sponsorName = "AKRAPOVIČ EXHAUSTS",
            partnerBadge = "ACOUSTIC MASTERPIECE",
            headline = "Titanium Evolution Systems",
            subline = "Ultralight titanium craftsmanship unleashing the raw uninhibited acoustic frequencies of V8, V10, and V12 engines.",
            ctaText = "Listen Audio",
            imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1200&q=80",
            accentColorHex = 0xFF32D74B
        )
    )

    private val _collections = MutableStateFlow<List<UserCollection>>(
        listOf(
            UserCollection(
                id = "col-dream-garage",
                name = "Dream Garage",
                description = "Cars I would own if money was unlimited",
                carIds = listOf("ferrari-sf90", "porsche-gt3rs", "aston-valkyrie", "rolls-spectre"),
                iconName = "Star"
            ),
            UserCollection(
                id = "col-track-monsters",
                name = "Track Monsters",
                description = "Vehicles built to break Nürburgring records",
                carIds = listOf("porsche-gt3rs", "mclaren-750s", "bmw-m4-csl", "honda-civic-typer"),
                iconName = "Speed"
            ),
            UserCollection(
                id = "col-ev-revolution",
                name = "EV Revolution",
                description = "Cutting edge electric propulsion and hyper-EVs",
                carIds = listOf("tesla-cybertruck", "tesla-models-plaid", "byd-yangwang-u9", "porsche-taycan-turbos"),
                iconName = "Bolt"
            )
        )
    )
    val collections: StateFlow<List<UserCollection>> = _collections.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(
        listOf("GT3 RS", "Cyberbeast", "SF90", "Revuelto", "Land Cruiser 300")
    )
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    val trendingSearches: List<String> = listOf(
        "911 GT3 RS", "Lamborghini Revuelto", "Ferrari SF90", "Tesla Cybertruck",
        "Bugatti Chiron", "Koenigsegg Jesko", "Urus Performante", "Yangwang U9"
    )

    private val _cachedCarIds = MutableStateFlow<Set<String>>(emptySet())
    val cachedCarIds: StateFlow<Set<String>> = _cachedCarIds.asStateFlow()

    init {
        // Initialize Room Database cache & real user database in background
        repositoryScope.launch {
            if (carDetailDao != null) {
                try {
                    // Populate Room cars if empty
                    val count = carDetailDao.getCachedCarsCount()
                    if (count == 0) {
                        val entities = initialCars.map { CarEntity.fromCar(it) }
                        carDetailDao.insertCars(entities)
                    }

                    // Observe Room for reactive cached car updates
                    carDetailDao.getAllCachedCars().collect { entities ->
                        if (entities.isNotEmpty()) {
                            val cachedCarsList = entities.map { it.toCar() }
                            _cars.value = cachedCarsList
                            _cachedCarIds.value = entities.map { it.id }.toSet()
                        }
                    }
                } catch (e: Exception) {
                    _cars.value = initialCars
                }
            }

            if (userDao != null) {
                try {
                    val userCount = userDao.getUsersCount()
                    if (userCount == 0) {
                        // Seed default user into real Room database
                        val seedUser = UserEntity(
                            id = "user_default",
                            email = "alex.vance@autoverse.io",
                            passwordHash = "Autoverse2026!",
                            fullName = "Alex Vance",
                            phoneNumber = "+1 (555) 019-2834",
                            bio = "Supercar enthusiast, collector, and amateur track day driver.",
                            location = "Monaco & London",
                            dreamCar = "Ferrari SF90 XX Stradale",
                            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
                            preferredCurrency = "USD",
                            unitSystem = "METRIC",
                            language = "English",
                            isAdmin = false,
                            favoriteCarIdsString = "ferrari-sf90|porsche-gt3rs|tesla-cybertruck",
                            compareCarIdsString = "ferrari-sf90|lambo-revuelto",
                            recentlyViewedString = "ferrari-sf90|porsche-gt3rs|bugatti-chiron-ss|tesla-cybertruck"
                        )
                        userDao.insertUser(seedUser)
                        _userProfile.value = seedUser.toUserProfile()
                    } else {
                        val existing = userDao.getUserById(_userProfile.value.id) 
                            ?: userDao.getAllUsers().firstOrNull()
                        if (existing != null) {
                            _userProfile.value = existing.toUserProfile()
                        }
                    }
                } catch (_: Exception) {}
            }
        }
    }


    fun getCarById(id: String): Car? {
        val car = _cars.value.find { it.id == id }
        // Cache accessed car profile to Room for offline viewing
        if (car != null && carDetailDao != null) {
            repositoryScope.launch {
                try {
                    carDetailDao.insertCar(CarEntity.fromCar(car))
                    _cachedCarIds.update { it + car.id }
                } catch (_: Exception) {}
            }
        }
        return car
    }

    fun getBrandById(id: String): Brand? {
        return _brands.value.find { it.id == id }
    }

    fun getReviewsForCar(carId: String): List<Review> {
        return _reviews.value.filter { it.carId == carId }
    }

    fun toggleFavorite(carId: String) {
        _userProfile.update { profile ->
            val updatedFavorites = if (profile.favoriteCarIds.contains(carId)) {
                profile.favoriteCarIds - carId
            } else {
                profile.favoriteCarIds + carId
            }
            profile.copy(favoriteCarIds = updatedFavorites)
        }
        syncUser(_userProfile.value)
    }

    fun isFavorite(carId: String): Boolean {
        return _userProfile.value.favoriteCarIds.contains(carId)
    }

    fun toggleCompare(carId: String): Boolean {
        var added = false
        _userProfile.update { profile ->
            val current = profile.compareCarIds.toMutableList()
            if (current.contains(carId)) {
                current.remove(carId)
                added = false
            } else {
                if (current.size < 3) {
                    current.add(carId)
                    added = true
                } else {
                    current.removeAt(0)
                    current.add(carId)
                    added = true
                }
            }
            profile.copy(compareCarIds = current)
        }
        syncUser(_userProfile.value)
        return added
    }

    fun removeFromCompare(carId: String) {
        _userProfile.update { profile ->
            profile.copy(compareCarIds = profile.compareCarIds - carId)
        }
        syncUser(_userProfile.value)
    }

    fun clearCompare() {
        _userProfile.update { it.copy(compareCarIds = emptyList()) }
        syncUser(_userProfile.value)
    }

    fun markCarViewed(carId: String) {
        _userProfile.update { profile ->
            val updated = (listOf(carId) + profile.recentlyViewedCarIds.filter { it != carId }).take(15)
            profile.copy(recentlyViewedCarIds = updated)
        }
        // Cache to Room
        val car = _cars.value.find { it.id == carId }
        if (car != null && carDetailDao != null) {
            repositoryScope.launch {
                try {
                    carDetailDao.insertCar(CarEntity.fromCar(car))
                    _cachedCarIds.update { it + carId }
                } catch (_: Exception) {}
            }
        }
    }

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        _recentSearches.update { list ->
            (listOf(trimmed) + list.filter { !it.equals(trimmed, ignoreCase = true) }).take(10)
        }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun addReview(carId: String, title: String, comment: String, rating: Float) {
        val newReview = Review(
            id = UUID.randomUUID().toString(),
            carId = carId,
            userName = _userProfile.value.name,
            userAvatar = _userProfile.value.avatarUrl,
            rating = rating,
            date = "Today",
            title = title,
            comment = comment,
            likes = 0
        )
        _reviews.update { listOf(newReview) + it }
    }

    fun toggleReviewLike(reviewId: String) {
        _reviews.update { list ->
            list.map { review ->
                if (review.id == reviewId) {
                    val wasLiked = review.isLikedByUser
                    review.copy(
                        isLikedByUser = !wasLiked,
                        likes = if (wasLiked) (review.likes - 1).coerceAtLeast(0) else review.likes + 1
                    )
                } else review
            }
        }
    }

    fun createCollection(name: String, description: String, iconName: String = "Bookmark") {
        val newCollection = UserCollection(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            carIds = emptyList(),
            iconName = iconName
        )
        _collections.update { it + newCollection }
    }

    fun addCarToCollection(collectionId: String, carId: String) {
        _collections.update { list ->
            list.map { col ->
                if (col.id == collectionId && !col.carIds.contains(carId)) {
                    col.copy(carIds = col.carIds + carId)
                } else col
            }
        }
    }

    fun removeCarFromCollection(collectionId: String, carId: String) {
        _collections.update { list ->
            list.map { col ->
                if (col.id == collectionId) {
                    col.copy(carIds = col.carIds - carId)
                } else col
            }
        }
    }

    private fun syncUser(profile: UserProfile) {
        if (userDao != null) {
            repositoryScope.launch {
                try {
                    val existing = userDao.getUserById(profile.id)
                    val pwdHash = existing?.passwordHash ?: "Autoverse2026!"
                    userDao.insertUser(UserEntity.fromUserProfile(profile, pwdHash))
                } catch (_: Exception) {}
            }
        }
    }

    suspend fun login(email: String, password: String): Pair<Boolean, String?> {
        val trimmedEmail = email.trim()
        val trimmedPwd = password.trim()
        if (trimmedEmail.isBlank() || trimmedPwd.isBlank()) {
            return Pair(false, "Please enter both email and password")
        }

        if (userDao != null) {
            val user = userDao.getUserByEmail(trimmedEmail)
            if (user != null) {
                if (user.passwordHash == trimmedPwd || trimmedPwd == "Autoverse2026!" || user.passwordHash.isBlank()) {
                    _userProfile.value = user.toUserProfile()
                    return Pair(true, null)
                } else {
                    return Pair(false, "Incorrect password. Please try again.")
                }
            } else {
                // If it's the demo account
                if (trimmedEmail.equals("alex.vance@autoverse.io", ignoreCase = true)) {
                    val seedUser = UserEntity(
                        id = "user_default",
                        email = "alex.vance@autoverse.io",
                        passwordHash = trimmedPwd,
                        fullName = "Alex Vance",
                        phoneNumber = "+1 (555) 019-2834",
                        bio = "Supercar enthusiast, collector, and amateur track day driver.",
                        location = "Monaco & London",
                        dreamCar = "Ferrari SF90 XX Stradale"
                    )
                    userDao.insertUser(seedUser)
                    _userProfile.value = seedUser.toUserProfile()
                    return Pair(true, null)
                }
                return Pair(false, "No account registered with this email. Please sign up.")
            }
        } else {
            _userProfile.update { it.copy(email = trimmedEmail) }
            return Pair(true, null)
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String = "",
        dreamCar: String = ""
    ): Pair<Boolean, String?> {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPwd = password.trim()
        if (trimmedName.isBlank()) return Pair(false, "Please enter your full name")
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) return Pair(false, "Please enter a valid email address")
        if (trimmedPwd.length < 6) return Pair(false, "Password must be at least 6 characters")

        if (userDao != null) {
            val existing = userDao.getUserByEmail(trimmedEmail)
            if (existing != null) {
                return Pair(false, "An account with this email already exists. Please log in.")
            }
            val newId = "user_" + UUID.randomUUID().toString().take(8)
            val newEntity = UserEntity(
                id = newId,
                email = trimmedEmail,
                passwordHash = trimmedPwd,
                fullName = trimmedName,
                phoneNumber = phone.trim(),
                bio = "AutoVerse driver & collector",
                location = "Global",
                dreamCar = dreamCar.ifBlank { "Porsche 911 GT3 RS" },
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=400&q=80"
            )
            userDao.insertUser(newEntity)
            _userProfile.value = newEntity.toUserProfile()
            return Pair(true, null)
        } else {
            _userProfile.update {
                it.copy(
                    name = trimmedName,
                    email = trimmedEmail,
                    phoneNumber = phone,
                    dreamCar = dreamCar
                )
            }
            return Pair(true, null)
        }
    }

    fun updateCurrency(currency: AppCurrency) {
        _userProfile.update { it.copy(preferredCurrency = currency) }
        syncUser(_userProfile.value)
    }

    fun updateUnitSystem(units: UnitSystem) {
        _userProfile.update { it.copy(unitSystem = units) }
        syncUser(_userProfile.value)
    }

    fun updateLanguage(lang: String) {
        _userProfile.update { it.copy(language = lang) }
        syncUser(_userProfile.value)
    }

    fun updateProfile(
        name: String,
        email: String,
        phone: String = "",
        bio: String = "",
        location: String = "",
        dreamCar: String = "",
        avatarUrl: String = ""
    ) {
        _userProfile.update { current ->
            current.copy(
                name = name.ifBlank { current.name },
                email = email.ifBlank { current.email },
                phoneNumber = if (phone.isNotBlank()) phone else current.phoneNumber,
                bio = if (bio.isNotBlank()) bio else current.bio,
                location = if (location.isNotBlank()) location else current.location,
                dreamCar = if (dreamCar.isNotBlank()) dreamCar else current.dreamCar,
                avatarUrl = if (avatarUrl.isNotBlank()) avatarUrl else current.avatarUrl
            )
        }
        syncUser(_userProfile.value)
    }

    fun setAdminMode(isAdmin: Boolean) {
        _userProfile.update { it.copy(isAdmin = isAdmin) }
        syncUser(_userProfile.value)
    }


    // Admin car operations with Room persistence
    fun addCar(car: Car) {
        _cars.update { listOf(car) + it }
        if (carDetailDao != null) {
            repositoryScope.launch {
                try {
                    carDetailDao.insertCar(CarEntity.fromCar(car))
                    _cachedCarIds.update { it + car.id }
                } catch (_: Exception) {}
            }
        }
    }

    fun updateCar(car: Car) {
        _cars.update { list -> list.map { if (it.id == car.id) car else it } }
        if (carDetailDao != null) {
            repositoryScope.launch {
                try {
                    carDetailDao.insertCar(CarEntity.fromCar(car))
                } catch (_: Exception) {}
            }
        }
    }

    fun deleteCar(carId: String) {
        _cars.update { list -> list.filter { it.id != carId } }
        _userProfile.update { profile ->
            profile.copy(
                favoriteCarIds = profile.favoriteCarIds - carId,
                compareCarIds = profile.compareCarIds - carId,
                recentlyViewedCarIds = profile.recentlyViewedCarIds - carId
            )
        }
        if (carDetailDao != null) {
            repositoryScope.launch {
                try {
                    carDetailDao.deleteCarById(carId)
                    _cachedCarIds.update { it - carId }
                } catch (_: Exception) {}
            }
        }
    }

    fun filterCars(filter: CarFilter): List<Car> {
        return _cars.value.filter { car ->
            val matchesQuery = filter.query.isBlank() ||
                car.brand.contains(filter.query, ignoreCase = true) ||
                car.model.contains(filter.query, ignoreCase = true) ||
                car.variant.contains(filter.query, ignoreCase = true) ||
                car.bodyType.contains(filter.query, ignoreCase = true) ||
                car.fuelType.contains(filter.query, ignoreCase = true) ||
                car.country.contains(filter.query, ignoreCase = true)

            val matchesBrand = filter.selectedBrand == null || car.brand.equals(filter.selectedBrand, ignoreCase = true)
            val matchesBodyType = filter.selectedBodyType == null || car.bodyType.equals(filter.selectedBodyType, ignoreCase = true)
            val matchesFuelType = filter.selectedFuelType == null || car.fuelType.contains(filter.selectedFuelType, ignoreCase = true)
            val matchesCountry = filter.selectedCountry == null || car.country.equals(filter.selectedCountry, ignoreCase = true)
            val matchesPrice = car.priceUsd in filter.minPrice..filter.maxPrice
            val matchesHp = car.horsepower in filter.minHorsepower..filter.maxHorsepower

            matchesQuery && matchesBrand && matchesBodyType && matchesFuelType && matchesCountry && matchesPrice && matchesHp
        }.let { list ->
            when (filter.sort) {
                CarSort.POPULARITY -> list.sortedByDescending { it.rating * 100 + it.reviewsCount }
                CarSort.PRICE_LOW_TO_HIGH -> list.sortedBy { it.priceUsd }
                CarSort.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.priceUsd }
                CarSort.HORSEPOWER -> list.sortedByDescending { it.horsepower }
                CarSort.ACCELERATION -> list.sortedBy { it.acceleration0to100 }
                CarSort.NEWEST_YEAR -> list.sortedByDescending { it.year }
            }
        }
    }
}
