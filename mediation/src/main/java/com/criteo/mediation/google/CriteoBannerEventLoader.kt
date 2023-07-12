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

import android.view.View
import com.criteo.publisher.CriteoBannerAdListener
import com.criteo.publisher.CriteoBannerView
import com.criteo.publisher.CriteoErrorCode
import com.criteo.publisher.model.BannerAdUnit
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationBannerAd
import com.google.android.gms.ads.mediation.MediationBannerAdCallback
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration

class CriteoBannerEventLoader(
    private val mediationBannerAdConfiguration: MediationBannerAdConfiguration,
    private val mediationAdLoadCallback: MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback>,
    private val bannerAdUnit: BannerAdUnit
) : CriteoBannerAdListener, MediationBannerAd {

    private lateinit var mediationBannerAdCallback: MediationBannerAdCallback
    private lateinit var bannerView: CriteoBannerView

    fun loadAd() {
        bannerView = CriteoBannerView(mediationBannerAdConfiguration.context, bannerAdUnit)
        bannerView.setCriteoBannerAdListener(this)
        bannerView.loadAd()
    }

    override fun onAdReceived(view: CriteoBannerView) {
        bannerView = view
        mediationBannerAdCallback = mediationAdLoadCallback.onSuccess(this)
        mediationBannerAdCallback.reportAdImpression()
    }

    override fun onAdFailedToReceive(code: CriteoErrorCode) {
        mediationAdLoadCallback.onFailure(code.toAdMobAdError())
    }

    override fun onAdLeftApplication() {
        mediationBannerAdCallback.onAdLeftApplication()
    }

    override fun onAdClicked() {
        mediationBannerAdCallback.onAdOpened()
        mediationBannerAdCallback.reportAdClicked()
    }

    override fun getView(): View {
        return bannerView
    }
}
