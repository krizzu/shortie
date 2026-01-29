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
