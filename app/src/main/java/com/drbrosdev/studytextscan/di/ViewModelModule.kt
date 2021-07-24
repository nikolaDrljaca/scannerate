package com.drbrosdev.studytextscan.di

import com.drbrosdev.studytextscan.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(repo = get()) }
}