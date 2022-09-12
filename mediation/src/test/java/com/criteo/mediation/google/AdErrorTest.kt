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
import com.google.android.gms.ads.AdRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AdErrorTest {

    @Test
    fun givenCriteoErrorCode_ToAdMobAdError_ShouldReturnProperAdError() {
        assertThat(CriteoErrorCode.ERROR_CODE_NO_FILL.toAdMobAdError()).satisfies {
            assertThat(it.code).isEqualTo(AdRequest.ERROR_CODE_NO_FILL)
            assertThat(it.domain).isEqualTo(ERROR_CODE_DOMAIN)
        }

        assertThat(CriteoErrorCode.ERROR_CODE_NETWORK_ERROR.toAdMobAdError()).satisfies {
            assertThat(it.code).isEqualTo(AdRequest.ERROR_CODE_NETWORK_ERROR)
            assertThat(it.domain).isEqualTo(ERROR_CODE_DOMAIN)
        }

        assertThat(CriteoErrorCode.ERROR_CODE_INVALID_REQUEST.toAdMobAdError()).satisfies {
            assertThat(it.code).isEqualTo(AdRequest.ERROR_CODE_INVALID_REQUEST)
            assertThat(it.domain).isEqualTo(ERROR_CODE_DOMAIN)
        }

        assertThat(CriteoErrorCode.ERROR_CODE_INTERNAL_ERROR.toAdMobAdError()).satisfies {
            assertThat(it.code).isEqualTo(AdRequest.ERROR_CODE_INTERNAL_ERROR)
            assertThat(it.domain).isEqualTo(ERROR_CODE_DOMAIN)
        }
    }
}
