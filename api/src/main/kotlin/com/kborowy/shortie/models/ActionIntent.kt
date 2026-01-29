package com.kborowy.shortie.models

import com.kborowy.shortie.utils.ActionIntentSerializer
import kotlinx.serialization.Serializable

/**
 * value object that carries information about action intent
 * - Ignore means do not change the current value
 * - Remove intent is to remove currently set value
 * - Set is to override current value with provided
 */
@Serializable(with = ActionIntentSerializer::class)
sealed class ActionIntent<out T>(val actionName: String) {
    object Ignore : ActionIntent<Nothing>("ignore")

    object Remove : ActionIntent<Nothing>("remove")

    data class Set<T>(val value: T) : ActionIntent<T>("set")
}
