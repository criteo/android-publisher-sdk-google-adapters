package com.criteo.mediation.google

import com.criteo.publisher.TestAdUnits
import com.criteo.publisher.mock.MockedDependenciesRule
import com.criteo.publisher.mock.SpyBean
import com.criteo.publisher.network.PubSdkApi
import com.google.android.gms.ads.mediation.MediationAdRequest
import com.google.android.gms.ads.mediation.MediationAdRequest.*
import com.google.android.gms.ads.mediation.NativeMediationAdRequest
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class TagForChildDirectedTreatmentTest {

    @Rule
    @JvmField
    val mockedDependenciesRule = MockedDependenciesRule()

    @SpyBean
    private lateinit var api: PubSdkApi

    @Test
    fun loadBanner_GivenTagForChildDirectedTreatmentIsTrue_ShouldPassTrueToCdbRequest() {
        val adapterHelper = AdapterHelper()
        whenever(adapterHelper.mediationAdRequest.taggedForChildDirectedTreatment()).thenReturn(
            TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
        )

        adapterHelper.requestBannerAd(TestAdUnits.BANNER_320_480, mock())
        mockedDependenciesRule.waitForIdleState()


        verify(api).loadCdb(check {
            assertThat(it.regs?.tagForChildDirectedTreatment).isEqualTo(true)
        }, any())
    }

    @Test
    fun loadBanner_GivenTagForChildDirectedTreatmentIsFalse_ShouldPassFalseToCdbRequest() {
        val adapterHelper = AdapterHelper()
        whenever(adapterHelper.mediationAdRequest.taggedForChildDirectedTreatment()).thenReturn(
            TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE
        )

        adapterHelper.requestBannerAd(TestAdUnits.BANNER_320_480, mock())
        mockedDependenciesRule.waitForIdleState()


        verify(api).loadCdb(check {
            assertThat(it.regs?.tagForChildDirectedTreatment).isEqualTo(false)
        }, any())
    }

    @Test
    fun loadBanner_GivenTagForChildDirectedTreatmentIsUnspecified_ShouldPassNothingToCdbRequest() {
        val adapterHelper = AdapterHelper()
        whenever(adapterHelper.mediationAdRequest.taggedForChildDirectedTreatment()).thenReturn(
            TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED
        )

        adapterHelper.requestBannerAd(TestAdUnits.BANNER_320_480, mock())
        mockedDependenciesRule.waitForIdleState()


        verify(api).loadCdb(check {
            assertThat(it.regs?.tagForChildDirectedTreatment).isEqualTo(null)
        }, any())
    }

    @Test
    fun loadNative_GivenTagForChildDirectedTreatmentIsTrue_ShouldPassTrueToCdbRequest() {
        val adapterHelper = AdapterHelper()
        whenever(adapterHelper.nativeMediationAdRequest.taggedForChildDirectedTreatment()).thenReturn(
            TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
        )

        adapterHelper.requestNativeAd(TestAdUnits.NATIVE, mock())
        mockedDependenciesRule.waitForIdleState()


        verify(api).loadCdb(check {
            assertThat(it.regs?.tagForChildDirectedTreatment).isEqualTo(true)
        }, any())
    }

    @Test
    fun loadInterstitial_GivenTagForChildDirectedTreatmentIsFalse_ShouldPassFalseToCdbRequest() {
        val adapterHelper = AdapterHelper()
        whenever(adapterHelper.mediationAdRequest.taggedForChildDirectedTreatment()).thenReturn(
            TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE
        )

        adapterHelper.requestInterstitialAd(TestAdUnits.INTERSTITIAL, mock())
        mockedDependenciesRule.waitForIdleState()


        verify(api).loadCdb(check {
            assertThat(it.regs?.tagForChildDirectedTreatment).isEqualTo(false)
        }, any())
    }
}
