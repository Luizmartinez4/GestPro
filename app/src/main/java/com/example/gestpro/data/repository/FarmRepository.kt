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

    /** Cria os dados fixos, tabela por tabela, apenas se ainda não existirem. */
    suspend fun seedIfEmpty() {
        db.withTransaction {
            if (dao.countFarm() == 0) dao.insertFarm(FarmSeed.farm)
            if (dao.countPlots() == 0) dao.insertPlots(FarmSeed.plots)
            if (dao.countInputs() == 0 && dao.countPlots() > 0) dao.insertInputs(FarmSeed.inputs)
        }
    }
}
