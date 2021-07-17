package com.drbrosdev.studytextscan.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import com.drbrosdev.studytextscan.persistence.entity.Scan
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM scan")
    fun getAllScans(): Flow<List<Scan>>
}