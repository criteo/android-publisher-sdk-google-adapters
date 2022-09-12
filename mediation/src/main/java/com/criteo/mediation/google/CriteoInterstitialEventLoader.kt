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

import android.content.Context
import com.criteo.publisher.CriteoErrorCode
import com.criteo.publisher.CriteoInterstitial
import com.criteo.publisher.CriteoInterstitialAdListener
import com.criteo.publisher.model.InterstitialAdUnit
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationInterstitialAd
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback

class CriteoInterstitialEventLoader(
    private val mediationAdLoadCallback: MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback>,
    private val interstitialAdUnit: InterstitialAdUnit
) : CriteoInterstitialAdListener, MediationInterstitialAd {

    private lateinit var criteoInterstitial: CriteoInterstitial
    private lateinit var mediationInterstitialAdCallback: MediationInterstitialAdCallback

    fun loadAd() {
        val interstitialAd = CriteoInterstitial(interstitialAdUnit)
        interstitialAd.setCriteoInterstitialAdListener(this)
        interstitialAd.loadAd()
    }

    override fun onAdReceived(interstitial: CriteoInterstitial) {
        criteoInterstitial = interstitial
        mediationInterstitialAdCallback = mediationAdLoadCallback.onSuccess(this)
    }

    override fun onAdFailedToReceive(code: CriteoErrorCode) {
        mediationAdLoadCallback.onFailure(code.toAdMobAdError())
    }

    override fun onAdOpened() {
        mediationInterstitialAdCallback.reportAdImpression()
        mediationInterstitialAdCallback.onAdOpened()
    }

    override fun onAdClosed() {
        mediationInterstitialAdCallback.onAdClosed()
    }

    override fun onAdLeftApplication() {
        mediationInterstitialAdCallback.onAdLeftApplication()
    }

    override fun onAdClicked() {
        mediationInterstitialAdCallback.reportAdClicked()
    }

    override fun showAd(context: Context) {
        criteoInterstitial.show()
    }
}
