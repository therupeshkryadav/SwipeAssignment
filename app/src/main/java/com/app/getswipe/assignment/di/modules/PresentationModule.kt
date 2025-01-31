package com.app.getswipe.assignment.di.modules

import com.app.getswipe.assignment.presentation.viewModel.ProductViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { ProductViewModel(get(),get(),get(),get()) }
}