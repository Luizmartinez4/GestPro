package com.example.gestpro.data.repository

import com.example.gestpro.data.local.EmployeeDao
import com.example.gestpro.data.local.EmployeeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class EmployeeRepository(private val dao: EmployeeDao) {

    fun observeEmployees(): Flow<List<EmployeeEntity>> = dao.observeAll()

    fun observeEmployee(id: Long): Flow<EmployeeEntity?> = dao.observeById(id)

    suspend fun getEmployee(id: Long): Result<EmployeeEntity> =
        dao.findById(id)?.let { Result.success(it) }
            ?: Result.failure(Exception("Funcionário não encontrado"))

    /** id == 0 cria; id != 0 atualiza. */
    suspend fun save(employee: EmployeeEntity): Result<Long> = try {
        if (employee.id == 0L) {
            Result.success(dao.insert(employee))
        } else {
            val old = dao.findById(employee.id)
            dao.update(employee)
            if (old?.photoPath != null && old.photoPath != employee.photoPath) deleteFile(old.photoPath)
            Result.success(employee.id)
        }
    } catch (e: Exception) {
        Result.failure(Exception("Não foi possível salvar o funcionário"))
    }

    suspend fun delete(id: Long): Result<Unit> = try {
        val old = dao.findById(id)
        dao.deleteById(id)
        old?.photoPath?.let { deleteFile(it) }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Não foi possível excluir o funcionário"))
    }

    private suspend fun deleteFile(path: String) = withContext(Dispatchers.IO) {
        runCatching { File(path).delete() }
    }
}
