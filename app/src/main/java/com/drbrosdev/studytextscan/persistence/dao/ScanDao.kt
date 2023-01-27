package com.drbrosdev.studytextscan.persistence.dao

import androidx.room.*
import com.drbrosdev.studytextscan.persistence.entity.Scan
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM scan WHERE is_pinned=0 ORDER BY date_created DESC")
    fun getAllScans(): Flow<List<Scan>>
    @Query("SELECT * FROM scan WHERE is_pinned=1 ORDER BY date_created DESC")
    fun getAllPinnedScans(): Flow<List<Scan>>

    @Insert
    suspend fun insertScan(scan: Scan): Long

    @Delete
    suspend fun deleteScan(scan: Scan)

    @Query("SELECT * FROM scan WHERE scan_id=:id")
    fun getScanById(id: Int): Flow<Scan>

    @Update
    suspend fun updateScan(scan: Scan)
}