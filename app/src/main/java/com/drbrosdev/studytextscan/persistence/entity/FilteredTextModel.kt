package com.drbrosdev.studytextscan.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "filtered_text_model",
    foreignKeys = [
        ForeignKey(
            entity = Scan::class,
            parentColumns = ["scan_id"],
            childColumns = ["scan_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FilteredTextModel(
    @ColumnInfo(name = "filtered_text_model_id") @PrimaryKey(autoGenerate = true) val filteredTextModelId: Int = 0,
    @ColumnInfo(name = "scan_id") val scanId: Int,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "content") val content: String
) {
}