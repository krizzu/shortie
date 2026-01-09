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

/** Alias already exists */
class AliasAlreadyExistsError(message: String = "Alias/hash already used") :
    BadRequestError(message)

/** Tried to set expiry date in past */
class ExpiryInPastError(message: String = "Expiry date cannot be in the past") :
    BadRequestError(message)
