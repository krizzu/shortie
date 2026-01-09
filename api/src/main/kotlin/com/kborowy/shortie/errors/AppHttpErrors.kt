/*
 * Copyright 2026 Krzysztof Borowy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kborowy.shortie.errors

import io.ktor.http.HttpStatusCode

/** Error base for all http errors thrown within the app */
sealed class AppHttpError(val statusCode: HttpStatusCode, message: String) :
    RuntimeException(message)

open class UnauthorizedHttpError(message: String = "unauthorized access") :
    AppHttpError(HttpStatusCode.Unauthorized, message)

class NotFoundHttpError(message: String = "resource not found") :
    AppHttpError(HttpStatusCode.NotFound, message)

class GoneHttpError(message: String = "gone") : AppHttpError(HttpStatusCode.Gone, message)

open class BadRequestError(message: String = "bad request") :
    AppHttpError(HttpStatusCode.BadRequest, message)

class InternalServerError(message: String) :
    AppHttpError(HttpStatusCode.InternalServerError, message)
