package com.drbrosdev.studytextscan.persistence.repository

import com.drbrosdev.studytextscan.persistence.database.ApplicationDatabase
import com.drbrosdev.studytextscan.persistence.entity.Scan

class ScanRepository(
    database: ApplicationDatabase
) {
    private val dao = database.scanDao

    fun getAllScans() = dao.getAllScans()

    suspend fun insertScan(scan: Scan) = dao.insertScan(scan)
}