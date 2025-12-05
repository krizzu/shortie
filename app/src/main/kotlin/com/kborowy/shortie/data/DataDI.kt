package com.kborowy.shortie.data

import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.migrations.com.kborowy.shortie.data.counter.GlobalCounter
import org.koin.dsl.module

val DataDIModule = module {
    single<UrlsRepository> { UrlsRepository(db = get()) }
    single<GlobalCounter> { GlobalCounter(db = get()) }
}
