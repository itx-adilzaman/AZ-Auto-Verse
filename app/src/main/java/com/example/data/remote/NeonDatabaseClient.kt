package com.example.data.remote

import android.util.Log
import com.example.model.AppCurrency
import com.example.model.AutomotiveAd
import com.example.model.Brand
import com.example.model.Car
import com.example.model.Review
import com.example.model.UnitSystem
import com.example.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Neon PostgreSQL Cloud Client.
 * Connects directly to Neon Database serverless SQL endpoint over SSL.
 * Manages Cars, Brands, Ads, Reviews, User Profiles, Favorites, and Comparisons.
 */
class NeonDatabaseClient(
    private val connectionString: String = DEFAULT_CONNECTION_STRING,
    private val endpointUrl: String = DEFAULT_ENDPOINT_URL
) {
    companion object {
        const val TAG = "NeonDatabaseClient"
        const val DEFAULT_CONNECTION_STRING =
            "postgresql://neondb_owner:npg_V0Kum5hgxfkw@ep-solitary-truth-a1u00b22-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require"
        const val DEFAULT_ENDPOINT_URL =
            "https://ep-solitary-truth-a1u00b22-pooler.ap-southeast-1.aws.neon.tech/sql"

        @Volatile
        private var INSTANCE: NeonDatabaseClient? = null

        fun getInstance(): NeonDatabaseClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NeonDatabaseClient().also { INSTANCE = it }
            }
        }
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun executeQuery(sql: String, params: JSONArray = JSONArray()): JSONObject? = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("query", sql)
                if (params.length() > 0) {
                    put("params", params)
                }
            }

            val request = Request.Builder()
                .url(endpointUrl)
                .addHeader("Neon-Connection-String", connectionString)
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    JSONObject(body)
                } else {
                    Log.e(TAG, "Neon query failed: HTTP ${response.code} -> $body")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Neon network error executing query: $sql", e)
            null
        }
    }

    suspend fun testConnection(): Boolean = withContext(Dispatchers.IO) {
        val result = executeQuery("SELECT 1 as live_status;")
        result != null && result.has("rows")
    }

    // ==========================================
    // CARS
    // ==========================================
    suspend fun fetchAllCars(): List<Car> = withContext(Dispatchers.IO) {
        val json = executeQuery(
            """
            SELECT 
                id, brand, model, variant, year, body_type, price_usd, fuel_type, engine,
                horsepower, torque_nm, transmission, drivetrain, top_speed_kmh, acceleration_0_100,
                mileage, dimensions, wheelbase, weight_kg, ground_clearance, boot_space, seating,
                tyre_size, brakes, suspension, safety_rating, features, images, description,
                country, rating, reviews_count, is_featured, is_trending, is_electric, is_supercar,
                is_sports, is_suv, is_budget_friendly
            FROM neon_cars
            ORDER BY rating DESC;
            """.trimIndent()
        ) ?: return@withContext emptyList()

        val cars = mutableListOf<Car>()
        val rows = json.optJSONArray("rows") ?: return@withContext emptyList()

        for (i in 0 until rows.length()) {
            val row = rows.getJSONObject(i)
            val featuresList = mutableListOf<String>()
            val featArray = row.optJSONArray("features")
            if (featArray != null) {
                for (j in 0 until featArray.length()) {
                    featuresList.add(featArray.optString(j))
                }
            }

            val imagesList = mutableListOf<String>()
            val imgArray = row.optJSONArray("images")
            if (imgArray != null) {
                for (j in 0 until imgArray.length()) {
                    imagesList.add(imgArray.optString(j))
                }
            }

            cars.add(
                Car(
                    id = row.getString("id"),
                    brand = row.getString("brand"),
                    model = row.getString("model"),
                    variant = row.optString("variant", ""),
                    year = row.getInt("year"),
                    bodyType = row.getString("body_type"),
                    priceUsd = row.getDouble("price_usd"),
                    fuelType = row.getString("fuel_type"),
                    engine = row.getString("engine"),
                    horsepower = row.getInt("horsepower"),
                    torqueNm = row.getInt("torque_nm"),
                    transmission = row.getString("transmission"),
                    drivetrain = row.getString("drivetrain"),
                    topSpeedKmh = row.getInt("top_speed_kmh"),
                    acceleration0to100 = row.getDouble("acceleration_0_100"),
                    mileage = row.getString("mileage"),
                    dimensions = row.optString("dimensions", ""),
                    wheelbase = row.optString("wheelbase", ""),
                    weightKg = row.optInt("weight_kg", 0),
                    groundClearance = row.optString("ground_clearance", ""),
                    bootSpace = row.optString("boot_space", ""),
                    seating = row.optInt("seating", 2),
                    tyreSize = row.optString("tyre_size", ""),
                    brakes = row.optString("brakes", ""),
                    suspension = row.optString("suspension", ""),
                    safetyRating = row.optDouble("safety_rating", 5.0),
                    features = featuresList,
                    images = imagesList,
                    description = row.optString("description", ""),
                    country = row.optString("country", ""),
                    rating = row.optDouble("rating", 4.9),
                    reviewsCount = row.optInt("reviews_count", 0),
                    isFeatured = row.optBoolean("is_featured", false),
                    isTrending = row.optBoolean("is_trending", false),
                    isElectric = row.optBoolean("is_electric", false),
                    isSupercar = row.optBoolean("is_supercar", false),
                    isSports = row.optBoolean("is_sports", false),
                    isSUV = row.optBoolean("is_suv", false),
                    isBudgetFriendly = row.optBoolean("is_budget_friendly", false)
                )
            )
        }
        cars
    }

    suspend fun saveCar(car: Car): Boolean = withContext(Dispatchers.IO) {
        val params = JSONArray().apply {
            put(car.id)
            put(car.brand)
            put(car.model)
            put(car.variant)
            put(car.year)
            put(car.bodyType)
            put(car.priceUsd)
            put(car.fuelType)
            put(car.engine)
            put(car.horsepower)
            put(car.torqueNm)
            put(car.transmission)
            put(car.drivetrain)
            put(car.topSpeedKmh)
            put(car.acceleration0to100)
            put(car.mileage)
            put(car.dimensions)
            put(car.wheelbase)
            put(car.weightKg)
            put(car.groundClearance)
            put(car.bootSpace)
            put(car.seating)
            put(car.tyreSize)
            put(car.brakes)
            put(car.suspension)
            put(car.safetyRating)
            put(JSONArray(car.features).toString())
            put(JSONArray(car.images).toString())
            put(car.description)
            put(car.country)
            put(car.rating)
            put(car.reviewsCount)
            put(car.isFeatured)
            put(car.isTrending)
            put(car.isElectric)
            put(car.isSupercar)
            put(car.isSports)
            put(car.isSUV)
            put(car.isBudgetFriendly)
        }

        val sql = """
            INSERT INTO neon_cars (
                id, brand, model, variant, year, body_type, price_usd, fuel_type, engine,
                horsepower, torque_nm, transmission, drivetrain, top_speed_kmh, acceleration_0_100,
                mileage, dimensions, wheelbase, weight_kg, ground_clearance, boot_space, seating,
                tyre_size, brakes, suspension, safety_rating, features, images, description,
                country, rating, reviews_count, is_featured, is_trending, is_electric, is_supercar,
                is_sports, is_suv, is_budget_friendly
            ) VALUES (
                ${'$'}1, ${'$'}2, ${'$'}3, ${'$'}4, ${'$'}5, ${'$'}6, ${'$'}7, ${'$'}8, ${'$'}9,
                ${'$'}10, ${'$'}11, ${'$'}12, ${'$'}13, ${'$'}14, ${'$'}15, ${'$'}16, ${'$'}17, ${'$'}18,
                ${'$'}19, ${'$'}20, ${'$'}21, ${'$'}22, ${'$'}23, ${'$'}24, ${'$'}25, ${'$'}26,
                ${'$'}27::jsonb, ${'$'}28::jsonb, ${'$'}29, ${'$'}30, ${'$'}31, ${'$'}32, ${'$'}33,
                ${'$'}34, ${'$'}35, ${'$'}36, ${'$'}37, ${'$'}38, ${'$'}39
            )
            ON CONFLICT (id) DO UPDATE SET
                brand = EXCLUDED.brand,
                model = EXCLUDED.model,
                variant = EXCLUDED.variant,
                year = EXCLUDED.year,
                body_type = EXCLUDED.body_type,
                price_usd = EXCLUDED.price_usd,
                fuel_type = EXCLUDED.fuel_type,
                engine = EXCLUDED.engine,
                horsepower = EXCLUDED.horsepower,
                torque_nm = EXCLUDED.torque_nm,
                transmission = EXCLUDED.transmission,
                drivetrain = EXCLUDED.drivetrain,
                top_speed_kmh = EXCLUDED.top_speed_kmh,
                acceleration_0_100 = EXCLUDED.acceleration_0_100,
                mileage = EXCLUDED.mileage,
                dimensions = EXCLUDED.dimensions,
                wheelbase = EXCLUDED.wheelbase,
                weight_kg = EXCLUDED.weight_kg,
                ground_clearance = EXCLUDED.ground_clearance,
                boot_space = EXCLUDED.boot_space,
                seating = EXCLUDED.seating,
                tyre_size = EXCLUDED.tyre_size,
                brakes = EXCLUDED.brakes,
                suspension = EXCLUDED.suspension,
                safety_rating = EXCLUDED.safety_rating,
                features = EXCLUDED.features,
                images = EXCLUDED.images,
                description = EXCLUDED.description,
                country = EXCLUDED.country,
                rating = EXCLUDED.rating,
                reviews_count = EXCLUDED.reviews_count,
                is_featured = EXCLUDED.is_featured,
                is_trending = EXCLUDED.is_trending,
                is_electric = EXCLUDED.is_electric,
                is_supercar = EXCLUDED.is_supercar,
                is_sports = EXCLUDED.is_sports,
                is_suv = EXCLUDED.is_suv,
                is_budget_friendly = EXCLUDED.is_budget_friendly;
        """.trimIndent()

        executeQuery(sql, params) != null
    }

    suspend fun deleteCar(carId: String): Boolean = withContext(Dispatchers.IO) {
        val params = JSONArray().apply { put(carId) }
        executeQuery("DELETE FROM neon_cars WHERE id = ${'$'}1;", params) != null
    }

    // ==========================================
    // BRANDS
    // ==========================================
    suspend fun fetchAllBrands(): List<Brand> = withContext(Dispatchers.IO) {
        val json = executeQuery(
            "SELECT id, name, logo_url, country, founded_year, headquarters, description, total_models FROM neon_brands ORDER BY name ASC;"
        ) ?: return@withContext emptyList()

        val list = mutableListOf<Brand>()
        val rows = json.optJSONArray("rows") ?: return@withContext emptyList()
        for (i in 0 until rows.length()) {
            val row = rows.getJSONObject(i)
            list.add(
                Brand(
                    id = row.getString("id"),
                    name = row.getString("name"),
                    logoUrl = row.getString("logo_url"),
                    country = row.getString("country"),
                    foundedYear = row.getInt("founded_year"),
                    headquarters = row.getString("headquarters"),
                    description = row.getString("description"),
                    totalModels = row.optInt("total_models", 0)
                )
            )
        }
        list
    }

    // ==========================================
    // ADS
    // ==========================================
    suspend fun fetchAllAds(): List<AutomotiveAd> = withContext(Dispatchers.IO) {
        val json = executeQuery(
            "SELECT id, sponsor_name, partner_badge, headline, subline, cta_text, image_url, accent_color_hex FROM neon_ads ORDER BY id ASC;"
        ) ?: return@withContext emptyList()

        val list = mutableListOf<AutomotiveAd>()
        val rows = json.optJSONArray("rows") ?: return@withContext emptyList()
        for (i in 0 until rows.length()) {
            val row = rows.getJSONObject(i)
            list.add(
                AutomotiveAd(
                    id = row.getString("id"),
                    sponsorName = row.getString("sponsor_name"),
                    partnerBadge = row.getString("partner_badge"),
                    headline = row.getString("headline"),
                    subline = row.getString("subline"),
                    ctaText = row.getString("cta_text"),
                    imageUrl = row.getString("image_url"),
                    accentColorHex = row.optLong("accent_color_hex", 0xFF0A84FF)
                )
            )
        }
        list
    }

    // ==========================================
    // REVIEWS
    // ==========================================
    suspend fun fetchReviews(carId: String? = null): List<Review> = withContext(Dispatchers.IO) {
        val query = if (carId != null) {
            "SELECT id, car_id, user_name, user_avatar, rating, date, title, comment, likes, is_liked_by_user FROM neon_reviews WHERE car_id = ${'$'}1 ORDER BY created_at DESC;"
        } else {
            "SELECT id, car_id, user_name, user_avatar, rating, date, title, comment, likes, is_liked_by_user FROM neon_reviews ORDER BY created_at DESC;"
        }
        val params = JSONArray().apply {
            if (carId != null) put(carId)
        }

        val json = executeQuery(query, params) ?: return@withContext emptyList()
        val list = mutableListOf<Review>()
        val rows = json.optJSONArray("rows") ?: return@withContext emptyList()
        for (i in 0 until rows.length()) {
            val row = rows.getJSONObject(i)
            list.add(
                Review(
                    id = row.getString("id"),
                    carId = row.getString("car_id"),
                    userName = row.getString("user_name"),
                    userAvatar = row.getString("user_avatar"),
                    rating = row.getDouble("rating").toFloat(),
                    date = row.getString("date"),
                    title = row.getString("title"),
                    comment = row.getString("comment"),
                    likes = row.optInt("likes", 0),
                    isLikedByUser = row.optBoolean("is_liked_by_user", false)
                )
            )
        }
        list
    }

    suspend fun addReview(review: Review): Boolean = withContext(Dispatchers.IO) {
        val params = JSONArray().apply {
            put(review.id)
            put(review.carId)
            put(review.userName)
            put(review.userAvatar)
            put(review.rating)
            put(review.date)
            put(review.title)
            put(review.comment)
            put(review.likes)
            put(review.isLikedByUser)
        }
        val sql = """
            INSERT INTO neon_reviews (id, car_id, user_name, user_avatar, rating, date, title, comment, likes, is_liked_by_user)
            VALUES (${'$'}1, ${'$'}2, ${'$'}3, ${'$'}4, ${'$'}5, ${'$'}6, ${'$'}7, ${'$'}8, ${'$'}9, ${'$'}10)
            ON CONFLICT (id) DO UPDATE SET
                likes = EXCLUDED.likes,
                is_liked_by_user = EXCLUDED.is_liked_by_user;
        """.trimIndent()
        executeQuery(sql, params) != null
    }

    // ==========================================
    // USER PROFILE & AUTH
    // ==========================================
    suspend fun fetchUserProfile(userId: String): UserProfile? = withContext(Dispatchers.IO) {
        val params = JSONArray().apply { put(userId) }
        val json = executeQuery(
            """
            SELECT id, email, name, phone_number, bio, location, dream_car, avatar_url, preferred_currency, unit_system, is_admin
            FROM neon_users
            WHERE id = ${'$'}1 OR email = ${'$'}1;
            """.trimIndent(),
            params
        ) ?: return@withContext null

        val rows = json.optJSONArray("rows") ?: return@withContext null
        if (rows.length() == 0) return@withContext null
        val row = rows.getJSONObject(0)

        val favs = fetchFavorites(row.getString("id"))
        val comps = fetchComparisons(row.getString("id"))
        val recents = fetchRecentViews(row.getString("id"))

        val currency = try {
            AppCurrency.valueOf(row.optString("preferred_currency", "USD"))
        } catch (_: Exception) {
            AppCurrency.USD
        }

        val units = try {
            UnitSystem.valueOf(row.optString("unit_system", "METRIC"))
        } catch (_: Exception) {
            UnitSystem.METRIC
        }

        UserProfile(
            id = row.getString("id"),
            name = row.getString("name"),
            email = row.getString("email"),
            phoneNumber = row.optString("phone_number", ""),
            bio = row.optString("bio", ""),
            location = row.optString("location", ""),
            dreamCar = row.optString("dream_car", ""),
            avatarUrl = row.optString("avatar_url", ""),
            preferredCurrency = currency,
            unitSystem = units,
            isAdmin = row.optBoolean("is_admin", false),
            favoriteCarIds = favs,
            compareCarIds = comps,
            recentlyViewedCarIds = recents
        )
    }

    suspend fun saveUserProfile(profile: UserProfile, authProvider: String = "google"): Boolean = withContext(Dispatchers.IO) {
        val params = JSONArray().apply {
            put(profile.id)
            put(profile.email)
            put(profile.name)
            put(profile.phoneNumber)
            put(profile.bio)
            put(profile.location)
            put(profile.dreamCar)
            put(profile.avatarUrl)
            put(profile.preferredCurrency.name)
            put(profile.unitSystem.name)
            put(profile.isAdmin)
            put(authProvider)
        }

        val sql = """
            INSERT INTO neon_users (id, email, name, phone_number, bio, location, dream_car, avatar_url, preferred_currency, unit_system, is_admin, auth_provider)
            VALUES (${'$'}1, ${'$'}2, ${'$'}3, ${'$'}4, ${'$'}5, ${'$'}6, ${'$'}7, ${'$'}8, ${'$'}9, ${'$'}10, ${'$'}11, ${'$'}12)
            ON CONFLICT (id) DO UPDATE SET
                email = EXCLUDED.email,
                name = EXCLUDED.name,
                phone_number = EXCLUDED.phone_number,
                bio = EXCLUDED.bio,
                location = EXCLUDED.location,
                dream_car = EXCLUDED.dream_car,
                avatar_url = EXCLUDED.avatar_url,
                preferred_currency = EXCLUDED.preferred_currency,
                unit_system = EXCLUDED.unit_system,
                is_admin = EXCLUDED.is_admin,
                updated_at = CURRENT_TIMESTAMP;
        """.trimIndent()

        executeQuery(sql, params) != null
    }

    // ==========================================
    // FAVORITES & COMPARISONS & RECENT VIEWS
    // ==========================================
    suspend fun fetchFavorites(userId: String): Set<String> = withContext(Dispatchers.IO) {
        val params = JSONArray().apply { put(userId) }
        val json = executeQuery(
            "SELECT car_id FROM neon_favorites WHERE user_id = ${'$'}1 ORDER BY created_at DESC;",
            params
        ) ?: return@withContext emptySet()

        val set = mutableSetOf<String>()
        val rows = json.optJSONArray("rows") ?: return@withContext emptySet()
        for (i in 0 until rows.length()) {
            set.add(rows.getJSONObject(i).getString("car_id"))
        }
        set
    }

    suspend fun addFavorite(userId: String, carId: String): Boolean = withContext(Dispatchers.IO) {
        val params = JSONArray().apply {
            put(userId)
            put(carId)
        }
        executeQuery(
            "INSERT INTO neon_favorites (user_id, car_id) VALUES (${'$'}1, ${'$'}2) ON CONFLICT DO NOTHING;",
            params
        ) != null
    }

    suspend fun removeFavorite(userId: String, carId: String): Boolean = withContext(Dispatchers.IO) {
        val params = JSONArray().apply {
            put(userId)
            put(carId)
        }
        executeQuery(
            "DELETE FROM neon_favorites WHERE user_id = ${'$'}1 AND car_id = ${'$'}2;",
            params
        ) != null
    }

    suspend fun fetchComparisons(userId: String): List<String> = withContext(Dispatchers.IO) {
        val params = JSONArray().apply { put(userId) }
        val json = executeQuery(
            "SELECT car_id FROM neon_comparisons WHERE user_id = ${'$'}1 ORDER BY added_at ASC;",
            params
        ) ?: return@withContext emptyList()

        val list = mutableListOf<String>()
        val rows = json.optJSONArray("rows") ?: return@withContext emptyList()
        for (i in 0 until rows.length()) {
            list.add(rows.getJSONObject(i).getString("car_id"))
        }
        list
    }

    suspend fun addComparison(userId: String, carId: String): Boolean = withContext(Dispatchers.IO) {
        val params = JSONArray().apply {
            put(userId)
            put(carId)
        }
        executeQuery(
            "INSERT INTO neon_comparisons (user_id, car_id) VALUES (${'$'}1, ${'$'}2) ON CONFLICT DO NOTHING;",
            params
        ) != null
    }

    suspend fun removeComparison(userId: String, carId: String): Boolean = withContext(Dispatchers.IO) {
        val params = JSONArray().apply {
            put(userId)
            put(carId)
        }
        executeQuery(
            "DELETE FROM neon_comparisons WHERE user_id = ${'$'}1 AND car_id = ${'$'}2;",
            params
        ) != null
    }

    suspend fun fetchRecentViews(userId: String): List<String> = withContext(Dispatchers.IO) {
        val params = JSONArray().apply { put(userId) }
        val json = executeQuery(
            "SELECT car_id FROM neon_recent_views WHERE user_id = ${'$'}1 ORDER BY viewed_at DESC LIMIT 15;",
            params
        ) ?: return@withContext emptyList()

        val list = mutableListOf<String>()
        val rows = json.optJSONArray("rows") ?: return@withContext emptyList()
        for (i in 0 until rows.length()) {
            list.add(rows.getJSONObject(i).getString("car_id"))
        }
        list
    }

    suspend fun recordRecentView(userId: String, carId: String): Boolean = withContext(Dispatchers.IO) {
        val params = JSONArray().apply {
            put(userId)
            put(carId)
        }
        executeQuery(
            """
            INSERT INTO neon_recent_views (user_id, car_id, viewed_at) 
            VALUES (${'$'}1, ${'$'}2, CURRENT_TIMESTAMP)
            ON CONFLICT (user_id, car_id) DO UPDATE SET viewed_at = CURRENT_TIMESTAMP;
            """.trimIndent(),
            params
        ) != null
    }
}
