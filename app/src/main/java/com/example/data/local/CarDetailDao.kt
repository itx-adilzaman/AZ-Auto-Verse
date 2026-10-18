package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDetailDao {

    @Query("SELECT * FROM cached_cars ORDER BY cachedAt DESC")
    fun getAllCachedCars(): Flow<List<CarEntity>>

    @Query("SELECT * FROM cached_cars WHERE id = :carId LIMIT 1")
    fun getCarById(carId: String): Flow<CarEntity?>

    @Query("SELECT * FROM cached_cars WHERE id = :carId LIMIT 1")
    suspend fun getCarByIdDirect(carId: String): CarEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCars(cars: List<CarEntity>)

    @Query("DELETE FROM cached_cars WHERE id = :carId")
    suspend fun deleteCarById(carId: String)

    @Query("SELECT COUNT(*) FROM cached_cars")
    suspend fun getCachedCarsCount(): Int

    @Query("DELETE FROM cached_cars")
    suspend fun clearAll()
}
