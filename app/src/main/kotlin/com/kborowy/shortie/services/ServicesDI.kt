package com.kborowy.shortie.services

import org.koin.dsl.module

val ServicesDIModule = module {
    single<UrlsService> { UrlsService(repo = get(), counter = get(), generator = get()) }
    single<UserService> { UserService(repo = get(), jwt = get()) }
}
