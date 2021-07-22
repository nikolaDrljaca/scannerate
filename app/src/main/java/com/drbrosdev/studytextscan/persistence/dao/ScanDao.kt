package com.drbrosdev.studytextscan.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import com.drbrosdev.studytextscan.persistence.entity.Scan

@Dao
interface ScanDao {
    @Query("SELECT * FROM scan")
    fun getAllScans(): List<Scan>
}