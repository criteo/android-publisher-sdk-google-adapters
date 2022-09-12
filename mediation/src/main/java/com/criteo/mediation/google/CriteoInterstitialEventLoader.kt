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

package com.criteo.mediation.google;


import androidx.annotation.NonNull;
import com.criteo.publisher.CriteoErrorCode;
import com.criteo.publisher.CriteoInterstitial;
import com.criteo.publisher.CriteoInterstitialAdListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;

public class CriteoInterstitialEventListener implements CriteoInterstitialAdListener {

    private final CustomEventInterstitialListener customEventInterstitialListener;

    public CriteoInterstitialEventListener(CustomEventInterstitialListener listener) {
        customEventInterstitialListener = listener;
    }

    @Override
    public void onAdReceived(@NonNull CriteoInterstitial interstitial) {
        customEventInterstitialListener.onAdLoaded();
    }

    @Override
    public void onAdFailedToReceive(CriteoErrorCode code) {
        customEventInterstitialListener.onAdFailedToLoad(ErrorCode.toAdMob(code));
    }

    @Override
    public void onAdOpened() {
        customEventInterstitialListener.onAdOpened();
    }

    @Override
    public void onAdClosed() {
        customEventInterstitialListener.onAdClosed();
    }

    @Override
    public void onAdLeftApplication() {
        customEventInterstitialListener.onAdLeftApplication();
    }

    @Override
    public void onAdClicked() {
        customEventInterstitialListener.onAdClicked();
    }
}
