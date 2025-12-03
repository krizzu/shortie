package com.kborowy.shortie.data

import com.kborowy.shortie.data.urls.UrlsRepository
import org.koin.dsl.module

val DataDIModule = module { single<UrlsRepository> { UrlsRepository(db = get()) } }
