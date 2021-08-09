package com.drbrosdev.studytextscan.di

import com.drbrosdev.studytextscan.persistence.repository.FilteredTextRepository
import com.drbrosdev.studytextscan.persistence.repository.ScanRepository
import org.koin.dsl.module

val repoModule = module {
    single { ScanRepository(database = get()) }
    single { FilteredTextRepository(database = get()) }
}