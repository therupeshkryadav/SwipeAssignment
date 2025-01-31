package com.app.getswipe.assignment.di

import com.app.getswipe.assignment.di.modules.dataModule
import com.app.getswipe.assignment.di.modules.domainModule
import com.app.getswipe.assignment.di.modules.presentationModule
import org.koin.dsl.module

val appModule = module {
    includes(dataModule, domainModule, presentationModule)// Add application-wide dependencies here
}