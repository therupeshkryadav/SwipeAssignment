package com.app.getswipe.assignment.di.modules

import com.app.getswipe.assignment.domain.usecase.AddProductUseCase
import com.app.getswipe.assignment.domain.usecase.GetProductsUseCase
import com.app.getswipe.assignment.domain.usecase.SearchProductsUseCase
import com.app.getswipe.assignment.domain.usecase.SyncAndClearOfflineProductsUseCase
import org.koin.dsl.module

val domainModule = module {
    single { GetProductsUseCase(get(), get()) }
    single { AddProductUseCase(get(), get()) }
    single { SyncAndClearOfflineProductsUseCase(get(), get()) }
    single { SearchProductsUseCase(get()) }
}