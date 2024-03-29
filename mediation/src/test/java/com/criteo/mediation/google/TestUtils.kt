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

import com.google.android.gms.ads.mediation.VersionInfo

/**
 * [VersionInfo] does not implement equals so we have to provide our implementation
 */
fun VersionInfo.isEqualTo(secondAdError: VersionInfo) : Boolean {
    return this.majorVersion == secondAdError.majorVersion &&
            this.minorVersion == secondAdError.minorVersion &&
            this.microVersion == secondAdError.microVersion
}
