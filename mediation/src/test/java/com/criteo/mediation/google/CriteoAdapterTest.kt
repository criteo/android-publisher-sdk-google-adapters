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

import com.criteo.publisher.Criteo
import com.google.android.gms.ads.mediation.InitializationCompleteCallback
import com.google.android.gms.ads.mediation.VersionInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class CriteoAdapterTest {

    private lateinit var adapter: CriteoAdapter

    @Before
    fun setUp() {
        adapter = CriteoAdapter()
    }

    @Test
    fun givenCorrectVersionString_getVersionInfo_shouldReturnVersionInfo() {
        val versionString = "4.2.5.6"
        Mockito.mockStatic(VersionProvider::class.java).use {
            it.`when`<String>{ VersionProvider.getMediationAdapterVersionName() }
                .thenReturn(versionString)

            val actualVersionInfo = adapter.versionInfo

            assertThat(actualVersionInfo.isEqualTo(VersionInfo(4, 2, 506))).isTrue
        }
    }

    @Test
    fun givenShortVersionString_getVersionInfo_shouldReturnDefaultVersionInfo() {
        val versionString = "4.2.5"
        Mockito.mockStatic(VersionProvider::class.java).use {
            it.`when`<String>{ VersionProvider.getMediationAdapterVersionName() }
                .thenReturn(versionString)

            val actualVersionInfo = adapter.versionInfo

            assertThat(actualVersionInfo.isEqualTo(CriteoAdapter.DEFAULT_VERSION_INFO)).isTrue
        }
    }

    @Test
    fun givenInvalidVersionString_getVersionInfo_shouldReturnDefaultVersionInfo() {
        val versionString = "version"
        Mockito.mockStatic(VersionProvider::class.java).use {
            it.`when`<String>{ VersionProvider.getMediationAdapterVersionName() }
                .thenReturn(versionString)

            val actualVersionInfo = adapter.versionInfo

            assertThat(actualVersionInfo.isEqualTo(CriteoAdapter.DEFAULT_VERSION_INFO)).isTrue
        }
    }

    @Test
    fun givenCorrectVersionString_getSDKVersionInfo_shouldReturnVersionInfo() {
        val versionString = "4.2.5"
        Mockito.mockStatic(Criteo::class.java).use {
            it.`when`<String>{ Criteo.getVersion() }
                .thenReturn(versionString)

            val actualVersionInfo = adapter.sdkVersionInfo

            assertThat(actualVersionInfo.isEqualTo(VersionInfo(4, 2, 5))).isTrue
        }
    }

    @Test
    fun givenShortVersionString_getSDKVersionInfo_shouldReturnDefaultVersionInfo() {
        val versionString = "4.2"
        Mockito.mockStatic(Criteo::class.java).use {
            it.`when`<String>{ Criteo.getVersion() }
                .thenReturn(versionString)

            val actualVersionInfo = adapter.sdkVersionInfo

            assertThat(actualVersionInfo.isEqualTo(CriteoAdapter.DEFAULT_VERSION_INFO)).isTrue
        }
    }

    @Test
    fun givenInvalidVersionString_getSDKVersionInfo_shouldReturnDefaultVersionInfo() {
        val versionString = "version"
        Mockito.mockStatic(Criteo::class.java).use {
            it.`when`<String>{ Criteo.getVersion() }
                .thenReturn(versionString)

            val actualVersionInfo = adapter.sdkVersionInfo

            assertThat(actualVersionInfo.isEqualTo(CriteoAdapter.DEFAULT_VERSION_INFO)).isTrue
        }
    }

    @Test
    fun initialize_shouldCallOnInitializationSucceededOnInitializationCompleteCallback() {
        val initializationCompleteCallback: InitializationCompleteCallback = mock()

        adapter.initialize(mock(), initializationCompleteCallback, mock())

        verify(initializationCompleteCallback).onInitializationSucceeded()
    }
}
