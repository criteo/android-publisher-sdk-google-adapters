/*
 *    Copyright 2020 Criteo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.criteo.mediation.google

import android.util.Log
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private const val TAG = "PreconditionsUtil"

@OptIn(ExperimentalContracts::class)
internal fun <T> T?.isNotNull(): Boolean {
    contract {
        returns() implies (this@isNotNull != null)
    }
    return if (this == null) {
        val exception = NullPointerException("Expected non null value, but null occurs.")
        Log.w(TAG, exception)
        if (BuildConfig.DEBUG) {
            throw exception
        }
        false
    } else {
        true
    }
}
