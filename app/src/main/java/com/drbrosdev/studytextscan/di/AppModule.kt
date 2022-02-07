package com.drbrosdev.studytextscan.di

import android.content.Context
import androidx.room.Room
import com.drbrosdev.studytextscan.datastore.AppPreferences
import com.drbrosdev.studytextscan.datastore.datastore
import com.drbrosdev.studytextscan.persistence.database.ApplicationDatabase
import com.drbrosdev.studytextscan.service.pdfExport.PdfExportServiceImpl
import com.drbrosdev.studytextscan.service.textFilter.TextFilterServiceImpl
import com.drbrosdev.studytextscan.service.textFilter.TextFilterService
import com.drbrosdev.studytextscan.ui.home.ScanTextFromImageUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
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

private fun provideFilterTextService() =
    TextFilterServiceImpl()

private fun provideScanTextFromImageUseCase(filterTextService: TextFilterService) =
    ScanTextFromImageUseCase(filterTextService)

val appModule = module {
    single { provideDatabase(context = androidContext()) }
    single { providePdfExportService() }
    factory { providePreferences(androidContext()) }
    single { provideFilterTextService() } bind TextFilterService::class
    factory { provideScanTextFromImageUseCase(get()) }
}