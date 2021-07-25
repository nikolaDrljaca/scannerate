package com.drbrosdev.studytextscan.persistence.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.drbrosdev.studytextscan.persistence.dao.ScanDao
import com.drbrosdev.studytextscan.persistence.database.converters.DateConverter
import com.drbrosdev.studytextscan.persistence.entity.Scan

@Database(
    entities = [
        Scan::class
    ],
    version = 1
)
@TypeConverters(
    DateConverter::class
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract val scanDao: ScanDao
}