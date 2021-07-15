package com.drbrosdev.studytextscan.persistence.repository

import com.drbrosdev.studytextscan.persistence.database.ApplicationDatabase

class ScanRepository(
    database: ApplicationDatabase
) {
    private val dao = database.scanDao

    fun getAllScans() = dao.getAllScans()
}