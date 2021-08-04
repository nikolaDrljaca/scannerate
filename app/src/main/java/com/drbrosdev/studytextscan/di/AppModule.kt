package com.drbrosdev.studytextscan.di

import android.content.Context
import androidx.room.Room
import com.drbrosdev.studytextscan.datastore.AppPreferences
import com.drbrosdev.studytextscan.datastore.datastore
import com.drbrosdev.studytextscan.persistence.database.ApplicationDatabase
import com.drbrosdev.studytextscan.service.pdfExport.PdfExportServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private fun provideDatabase(context: Context) =
    Room.databaseBuilder(
        context,
        ApplicationDatabase::class.java,
        "posts_database"
    ).fallbackToDestructiveMigration().build()

private fun providePdfExportService() =
    PdfExportServiceImpl()

private fun providePreferences(context: Context) = AppPreferences(context.datastore)

val appModule = module {
    single { provideDatabase(context = androidContext()) }
    single { providePdfExportService() }
    factory { providePreferences(androidContext()) }
}