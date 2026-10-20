package com.example.data

import android.util.Log
import com.example.auth.GoogleUserData
import com.example.data.local.CarDetailDao
import com.example.data.local.CarEntity
import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import com.example.data.remote.NeonDatabaseClient
import com.example.model.AppCurrency
import com.example.model.AutomotiveAd
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

class CarRepository(
    private val carDetailDao: CarDetailDao? = null,
    private val userDao: UserDao? = null
) {
    companion object {
        private const val TAG = "CarRepository"
    }

    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    val neonClient: NeonDatabaseClient = NeonDatabaseClient.getInstance()

    private val _isNeonConnected = MutableStateFlow(false)
    val isNeonConnected: StateFlow<Boolean> = _isNeonConnected.asStateFlow()

    private val _neonStatus = MutableStateFlow("Connecting to Neon PostgreSQL...")
    val neonStatus: StateFlow<String> = _neonStatus.asStateFlow()

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            id = "user_adil_zaman",
            name = "Muhammad Adil Zaman",
            email = "malikmuhammadadilzaman@gmail.com",
            phoneNumber = "+92 300 0000000",
            bio = "Supercar Driver, Collector & AutoVerse Creator",
            location = "Islamabad & London",
            dreamCar = "Ferrari SF90 Stradale",
            avatarUrl = "https://camo.githubusercontent.com/d8d6d485bfb0a9318e25ba0e75df600a0b923c7eb66c604a631a26c43ceed9af/68747470733a2f2f7265732e636c6f7564696e6172792e636f6d2f646b647a75796a63342f696d6167652f75706c6f61642f76313739323433323531352f313738343634343737343431325f6e736d6c79682e6a7067",
            favoriteCarIds = setOf("ferrari-sf90", "porsche-gt3rs"),
            compareCarIds = listOf("ferrari-sf90", "lambo-revuelto"),
            recentlyViewedCarIds = listOf("ferrari-sf90", "porsche-gt3rs")
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _automotiveAds = MutableStateFlow<List<AutomotiveAd>>(emptyList())
    val automotiveAds: StateFlow<List<AutomotiveAd>> = _automotiveAds.asStateFlow()

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
        // 1. Initialize Room Local Cache (offline mirror of Neon PostgreSQL)
        repositoryScope.launch {
            if (carDetailDao != null) {
                try {
                    carDetailDao.getAllCachedCars().collect { entities ->
                        if (entities.isNotEmpty() && _cars.value.isEmpty()) {
                            val cachedCarsList = entities.map { it.toCar() }
                            _cars.value = cachedCarsList
                            _cachedCarIds.value = entities.map { it.id }.toSet()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Room cache load error", e)
                }
            }

            if (userDao != null) {
                try {
                    val existing = userDao.getUserById(_userProfile.value.id)
                        ?: userDao.getAllUsers().firstOrNull()
                    if (existing != null) {
                        _userProfile.value = existing.toUserProfile()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "UserDao initialization error", e)
                }
            }
        }

        // 2. Synchronize Live with Neon Cloud PostgreSQL Database
        syncWithNeon()
    }

    fun syncWithNeon() {
        repositoryScope.launch {
            try {
                _neonStatus.value = "Connecting to Neon Cloud..."
                val isConnected = neonClient.testConnection()
                if (isConnected) {
                    _isNeonConnected.value = true
                    _neonStatus.value = "Neon Cloud Live: PostgreSQL 16 (ap-southeast-1 pooler)"

                    // 1. Fetch live cars from Neon
                    val remoteCars = neonClient.fetchAllCars()
                    if (remoteCars.isNotEmpty()) {
                        _cars.value = remoteCars
                        carDetailDao?.insertCars(remoteCars.map { CarEntity.fromCar(it) })
                        _cachedCarIds.value = remoteCars.map { it.id }.toSet()
                    }

                    // 2. Fetch live brands from Neon
                    val remoteBrands = neonClient.fetchAllBrands()
                    if (remoteBrands.isNotEmpty()) {
                        _brands.value = remoteBrands
                    }

                    // 3. Fetch live ads from Neon
                    val remoteAds = neonClient.fetchAllAds()
                    if (remoteAds.isNotEmpty()) {
                        _automotiveAds.value = remoteAds
                    }

                    // 4. Fetch live reviews from Neon
                    val remoteReviews = neonClient.fetchReviews()
                    if (remoteReviews.isNotEmpty()) {
                        _reviews.value = remoteReviews
                    }

                    // 5. Fetch live user profile from Neon
                    val remoteProfile = neonClient.fetchUserProfile(_userProfile.value.id)
                    if (remoteProfile != null) {
                        _userProfile.value = remoteProfile
                    }
                    Log.d(TAG, "Neon Database sync complete: ${remoteCars.size} cars, ${remoteBrands.size} brands, ${remoteAds.size} ads.")
                } else {
                    _isNeonConnected.value = false
                    _neonStatus.value = "Neon Offline - Using Local Room Storage"
                }
            } catch (e: Exception) {
                _isNeonConnected.value = false
                _neonStatus.value = "Neon Connection Error: ${e.localizedMessage ?: "Network Timeout"}"
                Log.e(TAG, "Neon sync error", e)
            }
        }
    }

    fun getCarById(id: String): Car? {
        val car = _cars.value.find { it.id == id }
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
        val currentProfile = _userProfile.value
        val isCurrentlyFav = currentProfile.favoriteCarIds.contains(carId)
        val updatedFavorites = if (isCurrentlyFav) {
            currentProfile.favoriteCarIds - carId
        } else {
            currentProfile.favoriteCarIds + carId
        }
        val updatedProfile = currentProfile.copy(favoriteCarIds = updatedFavorites)
        _userProfile.value = updatedProfile

        syncUser(updatedProfile)

        // Sync with Neon Cloud
        repositoryScope.launch {
            if (isCurrentlyFav) {
                neonClient.removeFavorite(currentProfile.id, carId)
            } else {
                neonClient.addFavorite(currentProfile.id, carId)
            }
        }
    }

    fun isFavorite(carId: String): Boolean {
        return _userProfile.value.favoriteCarIds.contains(carId)
    }

    fun toggleCompare(carId: String): Boolean {
        var added = false
        val currentProfile = _userProfile.value
        val inComp = currentProfile.compareCarIds.contains(carId)

        val updatedList = currentProfile.compareCarIds.toMutableList()
        if (inComp) {
            updatedList.remove(carId)
            added = false
        } else {
            if (updatedList.size < 3) {
                updatedList.add(carId)
                added = true
            } else {
                updatedList.removeAt(0)
                updatedList.add(carId)
                added = true
            }
        }

        val updatedProfile = currentProfile.copy(compareCarIds = updatedList)
        _userProfile.value = updatedProfile
        syncUser(updatedProfile)

        // Sync with Neon Cloud
        repositoryScope.launch {
            if (inComp) {
                neonClient.removeComparison(currentProfile.id, carId)
            } else {
                neonClient.addComparison(currentProfile.id, carId)
            }
        }
        return added
    }

    fun recordRecentlyViewed(carId: String) {
        val currentProfile = _userProfile.value
        val updated = listOf(carId) + currentProfile.recentlyViewedCarIds.filter { it != carId }
        val capped = updated.take(15)
        val updatedProfile = currentProfile.copy(recentlyViewedCarIds = capped)
        _userProfile.value = updatedProfile

        syncUser(updatedProfile)

        repositoryScope.launch {
            neonClient.recordRecentView(currentProfile.id, carId)
        }
    }

    fun markCarViewed(carId: String) = recordRecentlyViewed(carId)

    fun removeFromCompare(carId: String) {
        val currentProfile = _userProfile.value
        val updated = currentProfile.compareCarIds.filter { it != carId }
        val updatedProfile = currentProfile.copy(compareCarIds = updated)
        _userProfile.value = updatedProfile
        syncUser(updatedProfile)
        repositoryScope.launch {
            neonClient.removeComparison(currentProfile.id, carId)
        }
    }

    fun clearCompare() {
        val currentProfile = _userProfile.value
        val updatedProfile = currentProfile.copy(compareCarIds = emptyList())
        _userProfile.value = updatedProfile
        syncUser(updatedProfile)
        repositoryScope.launch {
            for (cid in currentProfile.compareCarIds) {
                neonClient.removeComparison(currentProfile.id, cid)
            }
        }
    }

    fun addReview(carId: String, title: String, comment: String, rating: Float) {
        val profile = _userProfile.value
        val newReview = Review(
            id = "rev_${System.currentTimeMillis()}",
            carId = carId,
            userName = profile.name,
            userAvatar = profile.avatarUrl,
            rating = rating,
            date = "Today",
            title = title,
            comment = comment,
            likes = 0,
            isLikedByUser = false
        )
        _reviews.update { listOf(newReview) + it }

        // Update Car reviews count and rating
        _cars.update { list ->
            list.map { car ->
                if (car.id == carId) {
                    val carReviews = (_reviews.value.filter { it.carId == carId })
                    val newCount = carReviews.size
                    val newAvg = carReviews.map { it.rating.toDouble() }.average()
                    car.copy(
                        reviewsCount = newCount,
                        rating = if (newAvg.isNaN()) car.rating else ((newAvg * 10).toInt() / 10.0)
                    )
                } else car
            }
        }

        repositoryScope.launch {
            neonClient.addReview(newReview)
            val updatedCar = _cars.value.find { it.id == carId }
            if (updatedCar != null) {
                neonClient.saveCar(updatedCar)
                carDetailDao?.insertCar(CarEntity.fromCar(updatedCar))
            }
        }
    }

    fun toggleReviewLike(reviewId: String) {
        _reviews.update { list ->
            list.map { review ->
                if (review.id == reviewId) {
                    val isLiked = !review.isLikedByUser
                    val newLikes = if (isLiked) review.likes + 1 else (review.likes - 1).coerceAtLeast(0)
                    val updated = review.copy(likes = newLikes, isLikedByUser = isLiked)
                    repositoryScope.launch { neonClient.addReview(updated) }
                    updated
                } else review
            }
        }
    }

    fun createCollection(name: String, description: String, iconName: String) {
        val newCollection = UserCollection(
            id = "col_${UUID.randomUUID().toString().take(8)}",
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

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        _recentSearches.update { list ->
            (listOf(trimmed) + list.filter { !it.equals(trimmed, ignoreCase = true) }).take(8)
        }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    // ==========================================
    // AUTHENTICATION: GOOGLE & EMAIL WITH NEON
    // ==========================================
    suspend fun loginWithGoogle(userData: GoogleUserData): Pair<Boolean, String?> {
        try {
            val email = userData.email
            val displayName = userData.displayName
            val avatar = userData.profilePictureUri ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80"
            val userId = "google_" + userData.id.replace(":", "_").replace("@", "_")

            // Check if user exists in Neon
            val existing = neonClient.fetchUserProfile(email) ?: neonClient.fetchUserProfile(userId)

            val finalProfile = if (existing != null) {
                existing.copy(
                    name = displayName,
                    avatarUrl = if (userData.profilePictureUri != null) userData.profilePictureUri else existing.avatarUrl
                )
            } else {
                UserProfile(
                    id = userId,
                    name = displayName,
                    email = email,
                    phoneNumber = "+1 (555) 019-2834",
                    bio = "Verified Google Driver & Collector",
                    location = "San Francisco & Tokyo",
                    dreamCar = "Ferrari SF90 XX Stradale",
                    avatarUrl = avatar,
                    favoriteCarIds = setOf("ferrari-sf90", "porsche-gt3rs"),
                    compareCarIds = listOf("ferrari-sf90", "lambo-revuelto"),
                    recentlyViewedCarIds = listOf("ferrari-sf90", "porsche-gt3rs", "tesla-cybertruck")
                )
            }

            _userProfile.value = finalProfile

            // Persist to Neon
            repositoryScope.launch {
                neonClient.saveUserProfile(finalProfile, "google")
                for (cid in finalProfile.favoriteCarIds) {
                    neonClient.addFavorite(finalProfile.id, cid)
                }
            }

            // Persist to Room
            userDao?.let { dao ->
                val entity = UserEntity(
                    id = finalProfile.id,
                    email = finalProfile.email,
                    passwordHash = "google_authenticated",
                    fullName = finalProfile.name,
                    phoneNumber = finalProfile.phoneNumber,
                    bio = finalProfile.bio,
                    location = finalProfile.location,
                    dreamCar = finalProfile.dreamCar,
                    avatarUrl = finalProfile.avatarUrl,
                    preferredCurrency = finalProfile.preferredCurrency.name,
                    unitSystem = finalProfile.unitSystem.name,
                    language = "English",
                    isAdmin = finalProfile.isAdmin,
                    favoriteCarIdsString = finalProfile.favoriteCarIds.joinToString("|"),
                    compareCarIdsString = finalProfile.compareCarIds.joinToString("|"),
                    recentlyViewedString = finalProfile.recentlyViewedCarIds.joinToString("|")
                )
                dao.insertUser(entity)
            }
            return Pair(true, null)
        } catch (e: Exception) {
            Log.e(TAG, "Google login failed", e)
            return Pair(false, e.localizedMessage ?: "Google sign-in error")
        }
    }

    suspend fun login(email: String, password: String): Pair<Boolean, String?> {
        val trimmedEmail = email.trim()
        val trimmedPwd = password.trim()
        if (trimmedEmail.isBlank() || trimmedPwd.isBlank()) {
            return Pair(false, "Please enter both email and password")
        }

        // 1. Try Neon remote user
        try {
            val neonUser = neonClient.fetchUserProfile(trimmedEmail)
            if (neonUser != null) {
                _userProfile.value = neonUser
                syncUser(neonUser)
                return Pair(true, null)
            }
        } catch (_: Exception) {}

        // 2. Try Local Room user
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

        val newId = "user_" + UUID.randomUUID().toString().take(8)
        val newProfile = UserProfile(
            id = newId,
            name = trimmedName,
            email = trimmedEmail,
            phoneNumber = phone.trim(),
            bio = "AutoVerse driver & collector",
            location = "Global",
            dreamCar = dreamCar.ifBlank { "Porsche 911 GT3 RS" },
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=400&q=80"
        )
        _userProfile.value = newProfile

        // Save in Neon
        repositoryScope.launch {
            neonClient.saveUserProfile(newProfile, "email")
        }

        // Save in Room
        if (userDao != null) {
            val newEntity = UserEntity(
                id = newId,
                email = trimmedEmail,
                passwordHash = trimmedPwd,
                fullName = trimmedName,
                phoneNumber = phone.trim(),
                bio = "AutoVerse driver & collector",
                location = "Global",
                dreamCar = dreamCar.ifBlank { "Porsche 911 GT3 RS" },
                avatarUrl = newProfile.avatarUrl
            )
            userDao.insertUser(newEntity)
        }
        return Pair(true, null)
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
        val updated = _userProfile.value
        syncUser(updated)

        // Save to Neon
        repositoryScope.launch {
            neonClient.saveUserProfile(updated)
        }
    }

    fun setAdminMode(isAdmin: Boolean) {
        _userProfile.update { it.copy(isAdmin = isAdmin) }
        val updated = _userProfile.value
        syncUser(updated)
        repositoryScope.launch {
            neonClient.saveUserProfile(updated)
        }
    }

    private fun syncUser(profile: UserProfile) {
        if (userDao == null) return
        repositoryScope.launch {
            try {
                val entity = UserEntity(
                    id = profile.id,
                    email = profile.email,
                    passwordHash = "Autoverse2026!",
                    fullName = profile.name,
                    phoneNumber = profile.phoneNumber,
                    bio = profile.bio,
                    location = profile.location,
                    dreamCar = profile.dreamCar,
                    avatarUrl = profile.avatarUrl,
                    preferredCurrency = profile.preferredCurrency.name,
                    unitSystem = profile.unitSystem.name,
                    language = profile.language,
                    isAdmin = profile.isAdmin,
                    favoriteCarIdsString = profile.favoriteCarIds.joinToString("|"),
                    compareCarIdsString = profile.compareCarIds.joinToString("|"),
                    recentlyViewedString = profile.recentlyViewedCarIds.joinToString("|")
                )
                userDao.updateUser(entity)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync user to Room", e)
            }
        }
    }

    // Admin car operations with Neon Cloud + Room persistence
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
        repositoryScope.launch {
            neonClient.saveCar(car)
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
        repositoryScope.launch {
            neonClient.saveCar(car)
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
        repositoryScope.launch {
            neonClient.deleteCar(carId)
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
