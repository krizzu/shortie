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
package com.kborowy.shortie.utils

import com.kborowy.shortie.models.ActionIntent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
class ActionIntentSerializer<T>(private val valueSerializer: KSerializer<T>) :
    KSerializer<ActionIntent<T>> {
    override val descriptor: SerialDescriptor = valueSerializer.descriptor

    override fun serialize(encoder: Encoder, value: ActionIntent<T>) {
        when (value) {
            is ActionIntent.Ignore -> {}
            is ActionIntent.Remove -> encoder.encodeNull()
            is ActionIntent.Set -> encoder.encodeSerializableValue(valueSerializer, value.value)
        }
    }

    override fun deserialize(decoder: Decoder): ActionIntent<T> {
        val input = decoder.decodeNullableSerializableValue(valueSerializer)
        return if (input == null) ActionIntent.Remove else ActionIntent.Set(input)
    }
}
