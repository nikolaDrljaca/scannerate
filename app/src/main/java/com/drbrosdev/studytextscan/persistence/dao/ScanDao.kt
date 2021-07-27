package com.drbrosdev.studytextscan.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.drbrosdev.studytextscan.persistence.entity.Scan
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM scan ORDER BY date_created DESC")
    fun getAllScans(): Flow<List<Scan>>

    @Insert
    suspend fun insertScan(scan: Scan): Long

    @Delete
    suspend fun deleteScan(scan: Scan)

    @Query("SELECT * FROM scan WHERE scan_id=:id")
    suspend fun getScanById(id: Int): Scan
}