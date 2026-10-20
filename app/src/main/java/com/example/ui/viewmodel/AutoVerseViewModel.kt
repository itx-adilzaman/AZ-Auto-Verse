package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CarRepository
import com.example.model.AppCurrency
import com.example.model.Brand
import com.example.model.Car
import com.example.model.CarFilter
import com.example.model.CarSort
import com.example.model.Review
import com.example.model.UnitSystem
import com.example.model.UserCollection
import com.example.model.UserProfile
import com.example.ui.components.NavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class Screen {
    data object Splash : Screen()
    data object Login : Screen()
    data object SignUp : Screen()
    data class Main(val tab: NavTab = NavTab.HOME) : Screen()
    data class CarDetail(val carId: String) : Screen()
    data class BrandDetail(val brandId: String) : Screen()
    data object Search : Screen()
}

class AutoVerseViewModel(
    val repository: CarRepository = CarRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    val cars: StateFlow<List<Car>> = repository.cars
    val brands: StateFlow<List<Brand>> = repository.brands
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val collections: StateFlow<List<UserCollection>> = repository.collections
    val recentSearches: StateFlow<List<String>> = repository.recentSearches
    val trendingSearches: List<String> = repository.trendingSearches
    val cachedCarIds: StateFlow<Set<String>> = repository.cachedCarIds
    val automotiveAds: StateFlow<List<com.example.model.AutomotiveAd>> = repository.automotiveAds
    val isNeonConnected: StateFlow<Boolean> = repository.isNeonConnected
    val neonStatus: StateFlow<String> = repository.neonStatus

    private val _carFilter = MutableStateFlow(CarFilter())
    val carFilter: StateFlow<CarFilter> = _carFilter.asStateFlow()

    val filteredCars: StateFlow<List<Car>> = combine(
        cars,
        _carFilter
    ) { _, filter ->
        repository.filterCars(filter)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback message (e.g. "Added to compare", "Saved to Dream Garage")
    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    // Navigation backstack
    private val backStack = mutableListOf<Screen>()

    init {
        // Initially on Splash
        _currentScreen.value = Screen.Splash
    }

    fun navigateTo(screen: Screen) {
        backStack.add(_currentScreen.value)
        _currentScreen.value = screen
        if (screen is Screen.Main) {
            _currentTab.value = screen.tab
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            val prev = backStack.removeAt(backStack.size - 1)
            _currentScreen.value = prev
            if (prev is Screen.Main) {
                _currentTab.value = prev.tab
            }
            return true
        }
        return false
    }

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
        if (_currentScreen.value !is Screen.Main) {
            _currentScreen.value = Screen.Main(tab)
        }
    }

    fun openCarDetail(carId: String) {
        repository.markCarViewed(carId)
        navigateTo(Screen.CarDetail(carId))
    }

    fun openBrandDetail(brandId: String) {
        navigateTo(Screen.BrandDetail(brandId))
    }

    fun openSearch() {
        navigateTo(Screen.Search)
    }

    fun toggleFavorite(carId: String) {
        repository.toggleFavorite(carId)
        val isFav = repository.isFavorite(carId)
        showNotification(if (isFav) "Added to Saved Cars" else "Removed from Saved Cars")
    }

    fun toggleCompare(carId: String) {
        val added = repository.toggleCompare(carId)
        showNotification(
            if (added) "Car added to comparison (up to 3)"
            else "Car removed from comparison"
        )
    }

    fun removeFromCompare(carId: String) {
        repository.removeFromCompare(carId)
        showNotification("Car removed from comparison")
    }

    fun clearCompare() {
        repository.clearCompare()
        showNotification("Comparison cleared")
    }

    fun setFilterBrand(brand: String?) {
        _carFilter.update { it.copy(selectedBrand = brand) }
    }

    fun setFilterBodyType(bodyType: String?) {
        _carFilter.update { it.copy(selectedBodyType = bodyType) }
    }

    fun setFilterFuelType(fuelType: String?) {
        _carFilter.update { it.copy(selectedFuelType = fuelType) }
    }

    fun setFilterCountry(country: String?) {
        _carFilter.update { it.copy(selectedCountry = country) }
    }

    fun setPriceRange(min: Double, max: Double) {
        _carFilter.update { it.copy(minPrice = min, maxPrice = max) }
    }

    fun setHorsepowerRange(min: Int, max: Int) {
        _carFilter.update { it.copy(minHorsepower = min, maxHorsepower = max) }
    }

    fun setSort(sort: CarSort) {
        _carFilter.update { it.copy(sort = sort) }
    }

    fun setSearchQuery(query: String) {
        _carFilter.update { it.copy(query = query) }
        if (query.isNotBlank()) {
            repository.addRecentSearch(query)
        }
    }

    fun addRecentSearch(query: String) {
        repository.addRecentSearch(query)
    }

    fun resetFilters() {
        _carFilter.value = CarFilter()
    }

    fun updateCurrency(currency: AppCurrency) {
        repository.updateCurrency(currency)
        showNotification("Currency changed to ${currency.code}")
    }

    fun updateUnitSystem(units: UnitSystem) {
        repository.updateUnitSystem(units)
        showNotification("Speed units updated to ${units.speedUnit}")
    }

    fun updateLanguage(lang: String) {
        repository.updateLanguage(lang)
        showNotification("Language updated to $lang")
    }

    fun addReview(carId: String, title: String, comment: String, rating: Float) {
        repository.addReview(carId, title, comment, rating)
        showNotification("Thank you for your review!")
    }

    fun toggleReviewLike(reviewId: String) {
        repository.toggleReviewLike(reviewId)
    }

    fun createCollection(name: String, description: String, iconName: String) {
        repository.createCollection(name, description, iconName)
        showNotification("Collection '$name' created")
    }

    fun addCarToCollection(collectionId: String, carId: String) {
        repository.addCarToCollection(collectionId, carId)
        showNotification("Added car to collection")
    }

    fun removeCarFromCollection(collectionId: String, carId: String) {
        repository.removeCarFromCollection(collectionId, carId)
        showNotification("Car removed from collection")
    }

    fun clearRecentSearches() {
        repository.clearRecentSearches()
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val (success, error) = repository.login(email, password)
            if (success) {
                showNotification("Welcome back, ${userProfile.value.name.substringBefore(" ")}!")
            }
            onResult(success, error)
        }
    }

    fun signInWithGoogle(context: android.content.Context, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = com.example.auth.GoogleAuthHelper.signIn(context)
            if (result.isSuccess) {
                val googleUser = result.getOrThrow()
                val (success, error) = repository.loginWithGoogle(googleUser)
                if (success) {
                    showNotification("Signed in as ${googleUser.displayName} (Google)")
                }
                onResult(success, error)
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "Google sign-in error"
                onResult(false, err)
            }
        }
    }

    fun syncWithNeon() {
        repository.syncWithNeon()
        showNotification("Syncing live with Neon Cloud PostgreSQL...")
    }

    fun register(
        name: String,
        email: String,
        password: String,
        phone: String = "",
        dreamCar: String = "",
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val (success, error) = repository.register(name, email, password, phone, dreamCar)
            if (success) {
                showNotification("Account created! Welcome to AutoVerse, ${name.substringBefore(" ")}.")
            }
            onResult(success, error)
        }
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
        repository.updateProfile(name, email, phone, bio, location, dreamCar, avatarUrl)
        showNotification("Profile updated in database")
    }

    fun logout() {
        navigateTo(Screen.Login)
        showNotification("Logged out successfully")
    }


    fun toggleAdminMode() {
        val next = !userProfile.value.isAdmin
        repository.setAdminMode(next)
        showNotification(if (next) "Admin Mode Activated" else "Admin Mode Deactivated")
    }

    fun addCar(car: Car) {
        repository.addCar(car)
        showNotification("Car '${car.fullName}' published to encyclopedia")
    }

    fun deleteCar(carId: String) {
        val car = repository.getCarById(carId)
        repository.deleteCar(carId)
        showNotification("Car '${car?.fullName ?: "Item"}' deleted from database")
    }

    fun showNotification(msg: String) {
        _snackBarMessage.value = msg
    }

    fun dismissNotification() {
        _snackBarMessage.value = null
    }
}
