package com.kborowy.shortie.services

import com.kborowy.shortie.services.urls.UrlsService
import org.koin.dsl.module

val ServicesDIModule = module {
    single<UrlsService> { UrlsService(repo = get(), counter = get(), coder = get()) }
    single<UserService> { UserService(repo = get(), jwt = get()) }
}
