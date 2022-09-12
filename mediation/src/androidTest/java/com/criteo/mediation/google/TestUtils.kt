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

import com.google.android.gms.ads.AdError
import org.mockito.ArgumentMatcher

/**
 * [AdError] does not implement equals so we have to provide our implementation
 */
fun AdError.isEqualTo(secondAdError: AdError) : Boolean {
    return this.code == secondAdError.code &&
            this.message == secondAdError.message &&
            this.domain == secondAdError.domain &&
            this.cause == secondAdError.cause
}

class IsEqualToOtherAdError(private val expectedAdError: AdError): ArgumentMatcher<AdError> {
    override fun matches(actualAdError: AdError?): Boolean {
        return actualAdError?.isEqualTo(expectedAdError) ?: false
    }
}
