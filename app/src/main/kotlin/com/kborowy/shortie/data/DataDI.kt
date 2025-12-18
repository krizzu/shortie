package com.kborowy.shortie.data

import com.kborowy.shortie.data.counter.GlobalCounter
import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.data.users.UserRepository
import org.koin.dsl.module

val DataDIModule = module {
    single<UrlsRepository> { UrlsRepository(db = get(), coder = get()) }
    single<GlobalCounter> { GlobalCounter(db = get()) }
    single<UserRepository> { UserRepository(db = get()) }
}
