package com.drbrosdev.studytextscan.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Scan(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "scan_id") val scanId: Long = 0,
    @ColumnInfo(name = "scan_text") val scanText: String,
    @ColumnInfo(name = "date_created") val dateCreated: Long,
    @ColumnInfo(name = "date_modified") val dateModified: Long
) {
}