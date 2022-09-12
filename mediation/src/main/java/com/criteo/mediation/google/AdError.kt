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

import com.criteo.publisher.CriteoErrorCode
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest

internal const val ERROR_CODE_DOMAIN = BuildConfig.LIBRARY_PACKAGE_NAME

internal fun CriteoErrorCode.toAdMobAdError(): AdError {
  return when (this) {
    CriteoErrorCode.ERROR_CODE_NO_FILL -> AdError(
      AdRequest.ERROR_CODE_NO_FILL,
      "No fill",
      ERROR_CODE_DOMAIN
    )
    CriteoErrorCode.ERROR_CODE_NETWORK_ERROR -> AdError(
      AdRequest.ERROR_CODE_NETWORK_ERROR,
      "Network error",
      ERROR_CODE_DOMAIN
    )
    CriteoErrorCode.ERROR_CODE_INVALID_REQUEST -> AdError(
      AdRequest.ERROR_CODE_INVALID_REQUEST,
      "Invalid request",
      ERROR_CODE_DOMAIN
    )
    CriteoErrorCode.ERROR_CODE_INTERNAL_ERROR -> AdError(
      AdRequest.ERROR_CODE_INTERNAL_ERROR,
      "Internal error",
      ERROR_CODE_DOMAIN
    )
    else -> throw UnsupportedOperationException("Unknown Criteo error code: $this")
  }
}

internal fun emptyServerParameterError() =
  AdError(AdRequest.ERROR_CODE_INVALID_REQUEST, "Server parameter was empty.", ERROR_CODE_DOMAIN)

internal fun readingServerParameterError() =
  AdError(
    AdRequest.ERROR_CODE_INTERNAL_ERROR,
    "Adapter failed to read server parameters",
    ERROR_CODE_DOMAIN
  )

internal fun adapterInitializationError() =
  AdError(AdRequest.ERROR_CODE_INTERNAL_ERROR, "Adapter failed to initialize", ERROR_CODE_DOMAIN)

internal fun noFillError() = AdError(AdRequest.ERROR_CODE_NO_FILL, "No fill", ERROR_CODE_DOMAIN)
