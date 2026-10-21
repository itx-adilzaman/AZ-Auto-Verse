package com.example

import com.example.data.CarRepository
import com.example.data.SeedCarsData
import com.example.data.SeedCarsExtended
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class NeonSeederTest {

    private val neonConnectionString = "postgresql://neondb_owner:npg_V0Kum5hgxfkw@ep-solitary-truth-a1u00b22-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require"
    private val neonEndpoint = "https://ep-solitary-truth-a1u00b22-pooler.ap-southeast-1.aws.neon.tech/sql"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun executeSql(query: String, params: JSONArray = JSONArray()): JSONObject {
        val payload = JSONObject().apply {
            put("query", query)
            if (params.length() > 0) {
                put("params", params)
            }
        }

        val request = Request.Builder()
            .url(neonEndpoint)
            .addHeader("Neon-Connection-String", neonConnectionString)
            .addHeader("Content-Type", "application/json")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                println("SQL error ($query): $body")
                throw RuntimeException("SQL execution failed: $body")
            }
            return JSONObject(body)
        }
    }

    @org.junit.Ignore("One-time database seeder")
    @Test
    fun seedNeonDatabase() {
        println("=== Starting Neon Database Schema Setup & Seeding ===")

        val tablesDdl = listOf(
            """
            CREATE TABLE IF NOT EXISTS neon_cars (
                id TEXT PRIMARY KEY,
                brand TEXT NOT NULL,
                model TEXT NOT NULL,
                variant TEXT NOT NULL DEFAULT '',
                year INT NOT NULL,
                body_type TEXT NOT NULL,
                price_usd NUMERIC(15, 2) NOT NULL,
                fuel_type TEXT NOT NULL,
                engine TEXT NOT NULL,
                horsepower INT NOT NULL,
                torque_nm INT NOT NULL,
                transmission TEXT NOT NULL,
                drivetrain TEXT NOT NULL,
                top_speed_kmh INT NOT NULL,
                acceleration_0_100 NUMERIC(6, 2) NOT NULL,
                mileage TEXT NOT NULL,
                dimensions TEXT NOT NULL DEFAULT '',
                wheelbase TEXT NOT NULL DEFAULT '',
                weight_kg INT NOT NULL DEFAULT 0,
                ground_clearance TEXT NOT NULL DEFAULT '',
                boot_space TEXT NOT NULL DEFAULT '',
                seating INT NOT NULL DEFAULT 2,
                tyre_size TEXT NOT NULL DEFAULT '',
                brakes TEXT NOT NULL DEFAULT '',
                suspension TEXT NOT NULL DEFAULT '',
                safety_rating NUMERIC(4, 2) NOT NULL DEFAULT 5.0,
                features JSONB NOT NULL DEFAULT '[]'::jsonb,
                images JSONB NOT NULL DEFAULT '[]'::jsonb,
                description TEXT NOT NULL DEFAULT '',
                country TEXT NOT NULL DEFAULT '',
                rating NUMERIC(4, 2) NOT NULL DEFAULT 4.9,
                reviews_count INT NOT NULL DEFAULT 0,
                is_featured BOOLEAN NOT NULL DEFAULT FALSE,
                is_trending BOOLEAN NOT NULL DEFAULT FALSE,
                is_electric BOOLEAN NOT NULL DEFAULT FALSE,
                is_supercar BOOLEAN NOT NULL DEFAULT FALSE,
                is_sports BOOLEAN NOT NULL DEFAULT FALSE,
                is_suv BOOLEAN NOT NULL DEFAULT FALSE,
                is_budget_friendly BOOLEAN NOT NULL DEFAULT FALSE,
                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
            );
            """.trimIndent(),
            """
            CREATE TABLE IF NOT EXISTS neon_brands (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                logo_url TEXT NOT NULL,
                country TEXT NOT NULL,
                founded_year INT NOT NULL,
                headquarters TEXT NOT NULL,
                description TEXT NOT NULL,
                total_models INT NOT NULL DEFAULT 0
            );
            """.trimIndent(),
            """
            CREATE TABLE IF NOT EXISTS neon_ads (
                id TEXT PRIMARY KEY,
                sponsor_name TEXT NOT NULL,
                partner_badge TEXT NOT NULL,
                headline TEXT NOT NULL,
                subline TEXT NOT NULL,
                cta_text TEXT NOT NULL,
                image_url TEXT NOT NULL,
                accent_color_hex BIGINT NOT NULL DEFAULT 4278822143
            );
            """.trimIndent(),
            """
            CREATE TABLE IF NOT EXISTS neon_reviews (
                id TEXT PRIMARY KEY,
                car_id TEXT NOT NULL,
                user_name TEXT NOT NULL,
                user_avatar TEXT NOT NULL,
                rating REAL NOT NULL,
                date TEXT NOT NULL,
                title TEXT NOT NULL,
                comment TEXT NOT NULL,
                likes INT NOT NULL DEFAULT 0,
                is_liked_by_user BOOLEAN NOT NULL DEFAULT FALSE,
                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
            );
            """.trimIndent(),
            """
            CREATE TABLE IF NOT EXISTS neon_users (
                id TEXT PRIMARY KEY,
                email TEXT NOT NULL UNIQUE,
                name TEXT NOT NULL,
                phone_number TEXT NOT NULL DEFAULT '',
                bio TEXT NOT NULL DEFAULT '',
                location TEXT NOT NULL DEFAULT '',
                dream_car TEXT NOT NULL DEFAULT '',
                avatar_url TEXT NOT NULL DEFAULT '',
                preferred_currency TEXT NOT NULL DEFAULT 'USD',
                unit_system TEXT NOT NULL DEFAULT 'METRIC',
                is_admin BOOLEAN NOT NULL DEFAULT FALSE,
                auth_provider TEXT NOT NULL DEFAULT 'email',
                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
            );
            """.trimIndent(),
            """
            CREATE TABLE IF NOT EXISTS neon_favorites (
                user_id TEXT NOT NULL,
                car_id TEXT NOT NULL,
                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (user_id, car_id)
            );
            """.trimIndent(),
            """
            CREATE TABLE IF NOT EXISTS neon_comparisons (
                user_id TEXT NOT NULL,
                car_id TEXT NOT NULL,
                added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (user_id, car_id)
            );
            """.trimIndent(),
            """
            CREATE TABLE IF NOT EXISTS neon_recent_views (
                user_id TEXT NOT NULL,
                car_id TEXT NOT NULL,
                viewed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (user_id, car_id)
            );
            """.trimIndent()
        )

        for (ddl in tablesDdl) {
            executeSql(ddl)
        }
        println("All 8 tables created successfully in Neon PostgreSQL!")

        // 1. Seed Brands
        for (brand in SeedCarsData.brands) {
            val params = JSONArray().apply {
                put(brand.id)
                put(brand.name)
                put(brand.logoUrl)
                put(brand.country)
                put(brand.foundedYear)
                put(brand.headquarters)
                put(brand.description)
                put(brand.totalModels)
            }
            executeSql(
                """
                INSERT INTO neon_brands (id, name, logo_url, country, founded_year, headquarters, description, total_models)
                VALUES (${'$'}1, ${'$'}2, ${'$'}3, ${'$'}4, ${'$'}5, ${'$'}6, ${'$'}7, ${'$'}8)
                ON CONFLICT (id) DO UPDATE SET
                    name = EXCLUDED.name,
                    logo_url = EXCLUDED.logo_url,
                    country = EXCLUDED.country,
                    founded_year = EXCLUDED.founded_year,
                    headquarters = EXCLUDED.headquarters,
                    description = EXCLUDED.description,
                    total_models = EXCLUDED.total_models;
                """.trimIndent(),
                params
            )
        }
        println("Seeded ${SeedCarsData.brands.size} brands into neon_brands")

        // 2. Seed Ads
        val repo = CarRepository()
        for (ad in repo.automotiveAds.value) {
            val params = JSONArray().apply {
                put(ad.id)
                put(ad.sponsorName)
                put(ad.partnerBadge)
                put(ad.headline)
                put(ad.subline)
                put(ad.ctaText)
                put(ad.imageUrl)
                put(ad.accentColorHex)
            }
            executeSql(
                """
                INSERT INTO neon_ads (id, sponsor_name, partner_badge, headline, subline, cta_text, image_url, accent_color_hex)
                VALUES (${'$'}1, ${'$'}2, ${'$'}3, ${'$'}4, ${'$'}5, ${'$'}6, ${'$'}7, ${'$'}8)
                ON CONFLICT (id) DO UPDATE SET
                    sponsor_name = EXCLUDED.sponsor_name,
                    partner_badge = EXCLUDED.partner_badge,
                    headline = EXCLUDED.headline,
                    subline = EXCLUDED.subline,
                    cta_text = EXCLUDED.cta_text,
                    image_url = EXCLUDED.image_url,
                    accent_color_hex = EXCLUDED.accent_color_hex;
                """.trimIndent(),
                params
            )
        }
        println("Seeded ${repo.automotiveAds.value.size} ads into neon_ads")

        // 3. Seed Cars
        val allCars = SeedCarsData.cars + SeedCarsExtended.additionalCars
        for (car in allCars) {
            val featuresJson = JSONArray(car.features).toString()
            val imagesJson = JSONArray(car.images).toString()

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
                put(featuresJson)
                put(imagesJson)
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

            executeSql(
                """
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
                """.trimIndent(),
                params
            )
        }
        println("Seeded ${allCars.size} cars into neon_cars")

        // 4. Seed Reviews
        for (rev in SeedCarsData.sampleReviews) {
            val params = JSONArray().apply {
                put(rev.id)
                put(rev.carId)
                put(rev.userName)
                put(rev.userAvatar)
                put(rev.rating)
                put(rev.date)
                put(rev.title)
                put(rev.comment)
                put(rev.likes)
                put(rev.isLikedByUser)
            }
            executeSql(
                """
                INSERT INTO neon_reviews (id, car_id, user_name, user_avatar, rating, date, title, comment, likes, is_liked_by_user)
                VALUES (${'$'}1, ${'$'}2, ${'$'}3, ${'$'}4, ${'$'}5, ${'$'}6, ${'$'}7, ${'$'}8, ${'$'}9, ${'$'}10)
                ON CONFLICT (id) DO UPDATE SET
                    car_id = EXCLUDED.car_id,
                    user_name = EXCLUDED.user_name,
                    user_avatar = EXCLUDED.user_avatar,
                    rating = EXCLUDED.rating,
                    date = EXCLUDED.date,
                    title = EXCLUDED.title,
                    comment = EXCLUDED.comment,
                    likes = EXCLUDED.likes;
                """.trimIndent(),
                params
            )
        }
        println("Seeded ${SeedCarsData.sampleReviews.size} reviews into neon_reviews")

        // 5. Seed default demo user
        val defaultUser = repo.userProfile.value
        val userParams = JSONArray().apply {
            put(defaultUser.id)
            put(defaultUser.email)
            put(defaultUser.name)
            put(defaultUser.phoneNumber)
            put(defaultUser.bio)
            put(defaultUser.location)
            put(defaultUser.dreamCar)
            put(defaultUser.avatarUrl)
            put(defaultUser.preferredCurrency.name)
            put(defaultUser.unitSystem.name)
            put(defaultUser.isAdmin)
            put("google")
        }
        executeSql(
            """
            INSERT INTO neon_users (id, email, name, phone_number, bio, location, dream_car, avatar_url, preferred_currency, unit_system, is_admin, auth_provider)
            VALUES (${'$'}1, ${'$'}2, ${'$'}3, ${'$'}4, ${'$'}5, ${'$'}6, ${'$'}7, ${'$'}8, ${'$'}9, ${'$'}10, ${'$'}11, ${'$'}12)
            ON CONFLICT (id) DO UPDATE SET
                email = EXCLUDED.email,
                name = EXCLUDED.name,
                phone_number = EXCLUDED.phone_number,
                bio = EXCLUDED.bio,
                location = EXCLUDED.location,
                dream_car = EXCLUDED.dream_car,
                avatar_url = EXCLUDED.avatar_url;
            """.trimIndent(),
            userParams
        )
        println("Seeded default user into neon_users")

        // 6. Seed favorites & comparisons for demo user
        for (carId in defaultUser.favoriteCarIds) {
            val favParams = JSONArray().apply {
                put(defaultUser.id)
                put(carId)
            }
            executeSql(
                """
                INSERT INTO neon_favorites (user_id, car_id)
                VALUES (${'$'}1, ${'$'}2)
                ON CONFLICT (user_id, car_id) DO NOTHING;
                """.trimIndent(),
                favParams
            )
        }
        println("Seeded favorites into neon_favorites")

        for (carId in defaultUser.compareCarIds) {
            val compParams = JSONArray().apply {
                put(defaultUser.id)
                put(carId)
            }
            executeSql(
                """
                INSERT INTO neon_comparisons (user_id, car_id)
                VALUES (${'$'}1, ${'$'}2)
                ON CONFLICT (user_id, car_id) DO NOTHING;
                """.trimIndent(),
                compParams
            )
        }
        println("Seeded comparisons into neon_comparisons")

        println("=== Neon Database Seeding Completed Successfully! ===")
    }
}
