package com.drbrosdev.studytextscan.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.drbrosdev.studytextscan.persistence.entity.FilteredTextModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FilteredTextModelDao {
    @Insert
    suspend fun insertModel(model: FilteredTextModel)

    @Query("SELECT * FROM FILTERED_TEXT_MODEL")
    fun getAllModels(): Flow<List<FilteredTextModel>>

    @Query("SELECT * FROM FILTERED_TEXT_MODEL WHERE scan_id = :scanId")
    fun getModelsByScanId(scanId: Int): Flow<List<FilteredTextModel>>

}