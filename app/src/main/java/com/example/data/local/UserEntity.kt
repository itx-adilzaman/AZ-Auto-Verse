package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AppCurrency
import com.example.model.UnitSystem
import com.example.model.UserProfile

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val phoneNumber: String = "",
    val bio: String = "",
    val location: String = "",
    val dreamCar: String = "",
    val avatarUrl: String = "",
    val preferredCurrency: String = "USD",
    val unitSystem: String = "METRIC",
    val language: String = "English",
    val isAdmin: Boolean = false,
    val favoriteCarIdsString: String = "",
    val compareCarIdsString: String = "",
    val recentlyViewedString: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toUserProfile(): UserProfile {
        return UserProfile(
            id = id,
            name = fullName,
            email = email,
            phoneNumber = phoneNumber,
            bio = bio,
            location = location,
            dreamCar = dreamCar,
            avatarUrl = avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80" },
            preferredCurrency = try { AppCurrency.valueOf(preferredCurrency) } catch (_: Exception) { AppCurrency.USD },
            unitSystem = try { UnitSystem.valueOf(unitSystem) } catch (_: Exception) { UnitSystem.METRIC },
            language = language,
            isAdmin = isAdmin,
            favoriteCarIds = if (favoriteCarIdsString.isBlank()) emptySet() else favoriteCarIdsString.split("|").filter { it.isNotBlank() }.toSet(),
            compareCarIds = if (compareCarIdsString.isBlank()) emptyList() else compareCarIdsString.split("|").filter { it.isNotBlank() },
            recentlyViewedCarIds = if (recentlyViewedString.isBlank()) emptyList() else recentlyViewedString.split("|").filter { it.isNotBlank() }
        )
    }

    companion object {
        fun fromUserProfile(profile: UserProfile, passwordHash: String): UserEntity {
            return UserEntity(
                id = profile.id,
                email = profile.email,
                passwordHash = passwordHash,
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
        }
    }
}
