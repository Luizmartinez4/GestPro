package com.example.gestpro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {

    @Query("SELECT * FROM farm WHERE id = 1 LIMIT 1")
    fun observeFarm(): Flow<FarmEntity?>

    @Query("SELECT * FROM plots ORDER BY number")
    fun observePlots(): Flow<List<PlotEntity>>

    @Query("SELECT * FROM plot_inputs WHERE plotId = :plotId ORDER BY sortOrder")
    fun observeInputs(plotId: Long): Flow<List<PlotInputEntity>>

    @Query("SELECT COUNT(*) FROM farm")
    suspend fun countFarm(): Int

    @Query("SELECT COUNT(*) FROM plots")
    suspend fun countPlots(): Int

    @Query("SELECT COUNT(*) FROM plot_inputs")
    suspend fun countInputs(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFarm(farm: FarmEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlots(plots: List<PlotEntity>)

    @Insert
    suspend fun insertInputs(inputs: List<PlotInputEntity>)
}
