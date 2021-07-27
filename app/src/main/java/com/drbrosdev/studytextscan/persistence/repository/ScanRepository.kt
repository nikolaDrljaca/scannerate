package com.drbrosdev.studytextscan.persistence.repository

import com.drbrosdev.studytextscan.persistence.database.ApplicationDatabase
import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.util.Resource
import kotlinx.coroutines.flow.flow

class ScanRepository(
    database: ApplicationDatabase
) {
    private val dao = database.scanDao

    fun getAllScans() = dao.getAllScans()

    suspend fun insertScan(scan: Scan) = dao.insertScan(scan)
    suspend fun deleteScan(scan: Scan) = dao.deleteScan(scan)

    fun getScanById(id: Int) = flow<Resource<Scan>> {
        emit(Resource.Loading())
        try {
            val scan = dao.getScanById(id)
            emit(Resource.Success(scan))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}