package com.gujaratifitness.app.core.di

import com.gujaratifitness.app.core.database.DatabaseFactory
import com.gujaratifitness.app.core.network.HttpClientFactory
import com.gujaratifitness.app.data.remote.createSupabase
import org.koin.dsl.module
import org.koin.core.context.startKoin

val appModule = module {
    // Network Ktor Client
    single { HttpClientFactory().create() }
    
    // Supabase Client
    single { createSupabase() }
    
    // SQLDelight Database
    single { DatabaseFactory(get()).createDatabase() }
}

fun initKoin(
    additionalModules: List<org.koin.core.module.Module> = emptyList(),
    appDeclaration: org.koin.dsl.KoinAppDeclaration = {}
) = startKoin {
    appDeclaration()
    modules(
        appModule,
        dataModule,
        domainModule,
        *additionalModules.toTypedArray()
    )
}
