package com.example.gestpro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    // ---- Talhões ----

    @Query("SELECT * FROM plots WHERE id = :id")
    suspend fun getPlot(id: Long): PlotEntity?

    @Query("SELECT COALESCE(MAX(id), 0) FROM plots")
    suspend fun maxPlotId(): Long

    @Query("SELECT COALESCE(MAX(number), 0) FROM plots")
    suspend fun maxPlotNumber(): Int

    @Query("SELECT COUNT(*) FROM plots WHERE number = :number AND id != :exceptId")
    suspend fun countPlotsWithNumber(number: Int, exceptId: Long): Int

    @Query("SELECT * FROM plots WHERE id = :id")
    fun observePlot(id: Long): Flow<PlotEntity?>

    @Insert
    suspend fun insertPlot(plot: PlotEntity)

    @Query("DELETE FROM plots WHERE id = :id")
    suspend fun deletePlot(id: Long)

    @Query("DELETE FROM plot_inputs WHERE plotId = :plotId")
    suspend fun deleteInputsOfPlot(plotId: Long)

    @Update
    suspend fun updatePlot(plot: PlotEntity)

    // ---- Lançamentos (insumos do talhão) ----

    @Query("SELECT * FROM plot_inputs WHERE id = :id")
    suspend fun getInput(id: Long): PlotInputEntity?

    @Query("SELECT COALESCE(MAX(sortOrder), -1) FROM plot_inputs WHERE plotId = :plotId")
    suspend fun maxInputSortOrder(plotId: Long): Int

    @Insert
    suspend fun insertInput(input: PlotInputEntity): Long

    @Update
    suspend fun updateInput(input: PlotInputEntity)

    @Query("DELETE FROM plot_inputs WHERE id = :id")
    suspend fun deleteInput(id: Long)
}
