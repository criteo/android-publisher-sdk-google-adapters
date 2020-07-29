package com.criteo.mediation.google

import com.criteo.publisher.CriteoUtil.givenInitializedCriteo
import com.criteo.publisher.TestAdUnits.*
import com.criteo.publisher.csm.MetricHelper
import com.criteo.publisher.csm.MetricSendingQueueConsumer
import com.criteo.publisher.integration.Integration.ADMOB_MEDIATION
import com.criteo.publisher.mock.MockedDependenciesRule
import com.criteo.publisher.mock.SpyBean
import com.criteo.publisher.network.PubSdkApi
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class ProfileIdFunctionalTest {

  @Rule
  @JvmField
  val mockedDependenciesRule = MockedDependenciesRule()

  @SpyBean
  private lateinit var metricSendingQueueConsumer: MetricSendingQueueConsumer

  @SpyBean
  private lateinit var api: PubSdkApi

  @Test
  fun loadBanner_GivenSdkUsedOrNot_UseAdapterProfileIdInAllRequests() {
    val adapterHelper = AdapterHelper()
    adapterHelper.requestBannerAd(BANNER_320_480, mock())
    mockedDependenciesRule.waitForIdleState()

    assertAdapterProfileIdIsUsedInAllRequests()
  }

  @Test
  fun loadInterstitial_GivenSdkUsedOrNot_UseAdapterProfileIdInAllRequests() {
    val adapterHelper = AdapterHelper()
    adapterHelper.requestInterstitialAd(INTERSTITIAL, mock())
    mockedDependenciesRule.waitForIdleState()

    assertAdapterProfileIdIsUsedInAllRequests()
  }

  @Test
  fun loadNative_GivenSdkUsedOrNot_UseAdapterProfileIdInAllRequests() {
    val adapterHelper = AdapterHelper()
    adapterHelper.requestNativeAd(NATIVE, mock())
    mockedDependenciesRule.waitForIdleState()

    assertAdapterProfileIdIsUsedInAllRequests()
  }

  private fun assertAdapterProfileIdIsUsedInAllRequests() {
    verify(api).loadConfig(check {
      assertThat(it.profileId).isEqualTo(ADMOB_MEDIATION.profileId)
    })

    verify(api).loadCdb(check {
      assertThat(it.profileId).isEqualTo(ADMOB_MEDIATION.profileId)
    }, any())

    triggerMetricRequest()

    verify(api).postCsm(check {
      with(MetricHelper) {
        assertThat(it.internalProfileId).isEqualTo(ADMOB_MEDIATION.profileId)
      }
    })
  }

  private fun triggerMetricRequest() {
    // CSM are put in queue during SDK init but they are not sent, so we need to trigger it.
    givenInitializedCriteo()
    mockedDependenciesRule.waitForIdleState()
    metricSendingQueueConsumer.sendMetricBatch()
    mockedDependenciesRule.waitForIdleState()
  }
}