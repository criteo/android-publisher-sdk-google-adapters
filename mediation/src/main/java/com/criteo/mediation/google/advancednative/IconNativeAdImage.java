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

package com.criteo.mediation.google.advancednative;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.criteo.publisher.advancednative.CriteoMedia;
import com.criteo.publisher.advancednative.CriteoMediaView;
import com.criteo.publisher.advancednative.NativeInternalForAdMob;
import com.google.android.gms.ads.formats.NativeAd;

// TODO: NativeAd.Image is deprecated but still there is no method change in UnifiedNativeAdMapper
class IconNativeAdImage extends NativeAd.Image {

  @NonNull
  private final Drawable drawable;

  @NonNull
  private final Uri uri;

  IconNativeAdImage(@NonNull Drawable drawable, @NonNull Uri uri) {
    this.drawable = drawable;
    this.uri = uri;
  }

  @NonNull
  static IconNativeAdImage create(
      @NonNull CriteoMediaView iconCriteoMediaView,
      @NonNull CriteoMedia mediaContent
  ) {
    Drawable drawable = new IconViewDrawable(NativeInternalForAdMob.getImageView(iconCriteoMediaView));
    Uri uri = Uri.parse(NativeInternalForAdMob.getImageUrl(mediaContent).toString());
    return new IconNativeAdImage(drawable, uri);
  }

  @NonNull
  @Override
  public Drawable getDrawable() {
    return drawable;
  }

  @NonNull
  @Override
  public Uri getUri() {
    return uri;
  }

  @Override
  public double getScale() {
    // SDK does not provide scale, return 1 by default.
    return 1.0;
  }

}
