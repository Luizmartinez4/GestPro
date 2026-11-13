package com.example.gestpro.data.repository

import androidx.room.withTransaction
import com.example.gestpro.data.local.AppDatabase
import com.example.gestpro.data.local.FarmEntity
import com.example.gestpro.data.local.FarmSeed
import com.example.gestpro.data.local.PlotEntity
import com.example.gestpro.data.local.PlotInputEntity
import kotlinx.coroutines.flow.Flow

class FarmRepository(private val db: AppDatabase) {

    private val dao = db.farmDao()

    fun observeFarm(): Flow<FarmEntity?> = dao.observeFarm()
    fun observePlots(): Flow<List<PlotEntity>> = dao.observePlots()
    fun observeInputs(plotId: Long): Flow<List<PlotInputEntity>> = dao.observeInputs(plotId)

    /**
     * Cria os dados fixos só na primeira execução (banco novo, ainda sem a propriedade).
     * Não recria talhões nem lançamentos que o usuário excluiu depois.
     */
    suspend fun seedIfEmpty() {
        if (dao.countFarm() == 0) dao.insertFarm(FarmSeed.farm)
    }

    // ---- Talhões ----

    suspend fun getPlot(id: Long): PlotEntity? = dao.getPlot(id)

    fun observePlot(id: Long): Flow<PlotEntity?> = dao.observePlot(id)

    /** Exclui o talhão e todos os lançamentos dele, numa única transação. */
    suspend fun deletePlot(id: Long) = db.withTransaction {
        dao.deleteInputsOfPlot(id)
        dao.deletePlot(id)
    }

    suspend fun nextPlotNumber(): Int = dao.maxPlotNumber() + 1

    suspend fun isPlotNumberTaken(number: Int, exceptId: Long): Boolean =
        dao.countPlotsWithNumber(number, exceptId) > 0

    /** Cria (quando id = 0) ou atualiza um talhão. Devolve o id salvo. */
    suspend fun savePlot(plot: PlotEntity): Long = db.withTransaction {
        if (plot.id == 0L) {
            val newId = dao.maxPlotId() + 1
            dao.insertPlot(plot.copy(id = newId))
            newId
        } else {
            dao.updatePlot(plot)
            plot.id
        }
    }

    // ---- Lançamentos ----

    suspend fun getInput(id: Long): PlotInputEntity? = dao.getInput(id)

    suspend fun saveInput(input: PlotInputEntity): Long = db.withTransaction {
        if (input.id == 0L) {
            dao.insertInput(input.copy(sortOrder = dao.maxInputSortOrder(input.plotId) + 1))
        } else {
            dao.updateInput(input)
            input.id
        }
    }

    suspend fun deleteInput(id: Long) = dao.deleteInput(id)
}
