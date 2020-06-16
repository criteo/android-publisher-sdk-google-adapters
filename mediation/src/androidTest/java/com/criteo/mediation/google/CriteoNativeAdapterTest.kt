package com.criteo.mediation.google

import android.content.ComponentName
import android.content.Context
import android.provider.Settings.Secure
import android.view.View
import android.widget.TextView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.criteo.mediation.google.activity.DummyActivity
import com.criteo.publisher.BidManager
import com.criteo.publisher.CriteoUtil.givenInitializedCriteo
import com.criteo.publisher.StubConstants
import com.criteo.publisher.TestAdUnits
import com.criteo.publisher.adview.Redirection
import com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait
import com.criteo.publisher.mock.MockBean
import com.criteo.publisher.mock.MockedDependenciesRule
import com.criteo.publisher.mock.SpyBean
import com.criteo.publisher.model.AdUnit
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.net.URI
import java.util.concurrent.CountDownLatch
import javax.inject.Inject


class CriteoNativeAdapterTest {

  private companion object {
    const val ADMOB_AD_UNIT_ID = "ca-app-pub-8459323526901202/2863808899"

    val TITLE_TAG = Any()
    val DESCRIPTION_TAG = Any()
    val PRICE_TAG = Any()
    val CALL_TO_ACTION_TAG = Any()
    val PRODUCT_IMAGE_TAG = Any()
    val ADVERTISER_DOMAIN_TAG = Any()
    val ADVERTISER_DESCRIPTION_TAG = Any()
    val ADVERTISER_LOGO_TAG = Any()
  }

  @Rule
  @JvmField
  val mockedDependenciesRule = MockedDependenciesRule()

  @Rule
  @JvmField
  var scenarioRule: ActivityScenarioRule<DummyActivity> = ActivityScenarioRule(DummyActivity::class.java)

  @Inject
  private lateinit var context: Context

  @SpyBean
  private lateinit var bidManager: BidManager

  @MockBean
  private lateinit var redirection: Redirection

  @Mock
  private lateinit var adListener: AdListener

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)

    val deviceId = Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    MobileAds.initialize(context)
    MobileAds.setRequestConfiguration(RequestConfiguration.Builder()
        .setTestDeviceIds(listOf(deviceId))
        .build())
  }

  @Test
  fun loadNativeAd_GivenValidBid_RenderAllNativePayload() {
    val expectedAssets = StubConstants.STUB_NATIVE_ASSETS
    val expectedProduct = expectedAssets.product
    val adUnit = TestAdUnits.NATIVE
    lateinit var adView: UnifiedNativeAdView
    val adViewIsRendered = CountDownLatch(1)

    // Given
    givenInitializedCriteo(adUnit)
    mockedDependenciesRule.waitForIdleState()
    givenAdUnitReplacedBy(adUnit)

    // When
    val adLoader = AdLoader.Builder(context, ADMOB_AD_UNIT_ID).forUnifiedNativeAd {
      adView = UnifiedNativeAdView(context)
      adView.addView(createTextView(context, TITLE_TAG, it.headline))
      adView.addView(createTextView(context, DESCRIPTION_TAG, it.body))
      adView.addView(createTextView(context, PRICE_TAG, it.price))
      adView.addView(createTextView(context, CALL_TO_ACTION_TAG, it.callToAction))
      adView.addView(createTextView(context, ADVERTISER_DOMAIN_TAG, it.extras["crtn_advdomain"] as String))
      adView.addView(createTextView(context, ADVERTISER_DESCRIPTION_TAG, it.advertiser))
      adView.setNativeAd(it)

      adViewIsRendered.countDown()
    }.withAdListener(adListener).build()

    adLoader.loadAd(AdRequest.Builder().build())

    adViewIsRendered.await()
    mockedDependenciesRule.waitForIdleState()

    // Then
    assertThat(adView.findTextWithTag(TITLE_TAG)).isEqualTo(expectedProduct.title)
    assertThat(adView.findTextWithTag(DESCRIPTION_TAG)).isEqualTo(expectedProduct.description)
    assertThat(adView.findTextWithTag(PRICE_TAG)).isEqualTo(expectedProduct.price)
    assertThat(adView.findTextWithTag(CALL_TO_ACTION_TAG)).isEqualTo(expectedProduct.callToAction)
    assertThat(adView.findTextWithTag(ADVERTISER_DOMAIN_TAG)).isEqualTo(expectedAssets.advertiserDomain)
    assertThat(adView.findTextWithTag(ADVERTISER_DESCRIPTION_TAG)).isEqualTo(expectedAssets.advertiserDescription)

    // TODO images

    // Click
    adView.assertClickRedirectTo(expectedProduct.clickUrl)

    // TODO impression
    // TODO AdChoice
  }

  @Test
  fun loadNativeAd_GivenInvalidBid_NotifyAdMobForFailure() {
    val adUnit = TestAdUnits.NATIVE_UNKNOWN
    lateinit var nativeAd: UnifiedNativeAd
    val adIsReceived = CountDownLatch(1)

    // Given
    givenInitializedCriteo(adUnit)
    mockedDependenciesRule.waitForIdleState()
    givenAdUnitReplacedBy(adUnit)

    // When
    val adLoader = AdLoader.Builder(context, ADMOB_AD_UNIT_ID).forUnifiedNativeAd {
      nativeAd = it
      adIsReceived.countDown()
    }.build()

    adLoader.loadAd(AdRequest.Builder().build())
    adIsReceived.await()
    mockedDependenciesRule.waitForIdleState()

    // In the waterfall, there is first the Criteo adapter, then the AdMob one (which is imposed).
    // Verifying that the response come from the AdMob adapter implies that the Criteo one did
    // notify AdMob framework for the no bid.
    assertThat(nativeAd.responseInfo?.mediationAdapterClassName)
        .isEqualTo("com.google.ads.mediation.admob.AdMobAdapter")
  }

  private fun givenAdUnitReplacedBy(adUnit: AdUnit) {
    // The AdMob account is setup to use a prod AdUnit (for Bugfest). For automated tests, we
    // replace it by a test AdUnit.

    doAnswer {
      val realBidManager = mockingDetails(it.mock).mockCreationSettings.spiedInstance as BidManager
      realBidManager.getBidForAdUnitAndPrefetch(adUnit)
    }.whenever(bidManager).getBidForAdUnitAndPrefetch(any())
  }

  private fun createTextView(context: Context, tag: Any, text: String): TextView {
    val view = TextView(context)
    view.tag = tag
    view.text = text
    return view
  }

  private fun View.findTextWithTag(tag: Any): CharSequence {
    return findViewWithTag<TextView>(tag).text
  }

  private fun View.assertClickRedirectTo(expectedRedirectionUri: URI) {
    clearInvocations(redirection)
    clearInvocations(adListener)

    runOnMainThreadAndWait {
      performClick()
    }

    mockedDependenciesRule.waitForIdleState()

    var expectedComponentName: ComponentName? = null
    scenarioRule.scenario.onActivity {
      expectedComponentName = it.componentName
    }

    verify(redirection).redirect(
        eq(expectedRedirectionUri.toString()),
        eq(expectedComponentName),
        any()
    )

    verify(adListener).onAdClicked()
  }

}