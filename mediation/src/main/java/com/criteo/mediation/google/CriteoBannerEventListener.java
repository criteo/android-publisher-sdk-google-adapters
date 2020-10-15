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
import com.criteo.publisher.CriteoBannerAdListener;
import com.criteo.publisher.CriteoBannerView;
import com.criteo.publisher.CriteoErrorCode;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

public class CriteoBannerEventListener implements CriteoBannerAdListener {

    private final CustomEventBannerListener customEventBannerListener;

    public CriteoBannerEventListener(CustomEventBannerListener listener) {
        customEventBannerListener = listener;
    }

    @Override
    public void onAdReceived(@NonNull CriteoBannerView view) {
        customEventBannerListener.onAdLoaded(view);
    }

    @Override
    public void onAdFailedToReceive(@NonNull CriteoErrorCode code) {
        customEventBannerListener.onAdFailedToLoad(ErrorCode.toAdMob(code));
    }

    @Override
    public void onAdLeftApplication() {
        customEventBannerListener.onAdLeftApplication();
    }

    @Override
    public void onAdClicked() {
        customEventBannerListener.onAdClicked();
    }

}
