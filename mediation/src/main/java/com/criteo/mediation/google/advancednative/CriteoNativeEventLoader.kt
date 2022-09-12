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

package com.criteo.mediation.google.advancednative

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import com.criteo.mediation.google.isNotNull
import com.criteo.mediation.google.toAdMobAdError
import com.criteo.publisher.CriteoErrorCode
import com.criteo.publisher.advancednative.CriteoMediaView
import com.criteo.publisher.advancednative.CriteoNativeAd
import com.criteo.publisher.advancednative.CriteoNativeAdListener
import com.criteo.publisher.advancednative.CriteoNativeLoader
import com.criteo.publisher.advancednative.CriteoNativeRenderer
import com.criteo.publisher.advancednative.NativeInternalForAdMob
import com.criteo.publisher.advancednative.RendererHelper
import com.criteo.publisher.model.NativeAdUnit
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationNativeAdCallback
import com.google.android.gms.ads.mediation.MediationNativeAdConfiguration
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper

class CriteoNativeEventLoader(
    private val mediationNativeAdConfiguration: MediationNativeAdConfiguration,
    private val mediationAdLoadCallback: MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback>,
    private val nativeAdUnit: NativeAdUnit
) : CriteoNativeAdListener {

    private lateinit var mediationNativeAdCallback: MediationNativeAdCallback

    fun loadAd() {
        val loader = CriteoNativeLoader(nativeAdUnit, this, NoOpNativeRenderer())
        loader.loadAd()
    }

    override fun onAdReceived(nativeAd: CriteoNativeAd) {
        val mapper = CriteoUnifiedNativeAdMapper(mediationNativeAdConfiguration.context, nativeAd, this)
        mediationNativeAdCallback = mediationAdLoadCallback.onSuccess(mapper)
    }

    override fun onAdFailedToReceive(errorCode: CriteoErrorCode) {
        mediationAdLoadCallback.onFailure(errorCode.toAdMobAdError())
    }

    override fun onAdClosed() {
        mediationNativeAdCallback.onAdClosed()
    }

    override fun onAdImpression() {
        mediationNativeAdCallback.reportAdImpression()
    }

    override fun onAdClicked() {
        mediationNativeAdCallback.reportAdClicked()
    }

    override fun onAdLeftApplication() {
        mediationNativeAdCallback.onAdOpened()
        mediationNativeAdCallback.onAdLeftApplication()
    }

    private class CriteoUnifiedNativeAdMapper(
        context: Context?,
        nativeAd: CriteoNativeAd,
        /**
         * Hold the listener until the end of life of this ad
         *
         *
         * Normally it is the job of the native loader to hold the listener. But in case of this
         * adapter, the loader is thrown directly and nothing prevent the listener to be GC. So it is
         * hold here.
         */
        @field:Keep private val listener: CriteoNativeAdListener
    ) : UnifiedNativeAdMapper() {
        private val nativeAd: CriteoNativeAd

        init {

            // Text fields
            headline = nativeAd.title
            body = nativeAd.description
            price = nativeAd.price
            callToAction = nativeAd.callToAction
            advertiser = nativeAd.advertiserDescription
            val bundle = Bundle()
            bundle.putString(
                CRT_NATIVE_ADV_DOMAIN,
                nativeAd.advertiserDomain
            )
            extras = bundle
            if (context != null) {
                val mediaAndLogoRenderer = MediaAndLogoRenderer()
                NativeInternalForAdMob.setRenderer(nativeAd, mediaAndLogoRenderer)
                // createNativeRenderedView calls both createNativeView and renderNativeView of the
                // renderer, so images are now currently being loaded
                val nativeRenderedView = nativeAd.createNativeRenderedView(context, null)

                // Product media
                setMediaView(mediaAndLogoRenderer.productMediaView)
                setHasVideoContent(false)

                // Advertiser logo
                val iconCriteoMediaView = mediaAndLogoRenderer.advertiserLogoView
                if (iconCriteoMediaView.isNotNull()) {
                    val iconImage = IconNativeAdImage.create(
                        iconCriteoMediaView,
                        nativeAd.advertiserLogoMedia
                    )
                    icon = iconImage
                }

                // AdChoice
                val adChoiceView =
                    NativeInternalForAdMob.getAdChoiceView(nativeAd, nativeRenderedView)
                if (adChoiceView.isNotNull()) {
                    adChoiceView.tag = AD_CHOICE_TAG
                    adChoicesContent = adChoiceView
                }
            }

            // Click & impression
            overrideClickHandling = true
            overrideImpressionRecording = true
            this.nativeAd = nativeAd
        }

        override fun trackViews(
            containerView: View,
            clickableAssetViews: Map<String, View>,
            nonClickableAssetViews: Map<String, View>
        ) {
            // The renderer is expected to do nothing, but the SDK will start to watch this view
            // for clicks and impressions
            NativeInternalForAdMob.setRenderer(nativeAd, NoOpNativeRenderer())
            nativeAd.renderNativeView(containerView)

            // As the AdChoice icon is not injected by the SDK, we should explicitly set the
            // click listeners dedicated to AdChoice
            val adChoiceView =
                containerView.findViewWithTag<View>(AD_CHOICE_TAG)
            if (adChoiceView != null) {
                NativeInternalForAdMob.setAdChoiceClickableView(nativeAd, adChoiceView)
            }
        }
    }

    private class MediaAndLogoRenderer : CriteoNativeRenderer {
        lateinit var productMediaView: CriteoMediaView
            private set
        lateinit var advertiserLogoView: CriteoMediaView
            private set

        override fun createNativeView(context: Context, parent: ViewGroup?): View {
            productMediaView = CriteoMediaView(context)
            advertiserLogoView = CriteoMediaView(context)
            return View(context)
        }

        override fun renderNativeView(
            helper: RendererHelper,
            nativeView: View,
            nativeAd: CriteoNativeAd
        ) {
            if (productMediaView.isNotNull()) {
                helper.setMediaInView(nativeAd.productMedia, productMediaView)
            }
            if (advertiserLogoView.isNotNull()) {
                helper.setMediaInView(nativeAd.advertiserLogoMedia, advertiserLogoView)
            }
        }
    }

    companion object {
        private const val CRT_NATIVE_ADV_DOMAIN = "crtn_advdomain"

        @JvmField
        internal val AD_CHOICE_TAG: Any = Any()
    }
}
