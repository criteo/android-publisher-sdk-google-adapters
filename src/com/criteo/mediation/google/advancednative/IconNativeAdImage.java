package com.criteo.mediation.google.advancednative;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.criteo.publisher.advancednative.CriteoMedia;
import com.criteo.publisher.advancednative.CriteoMediaView;
import com.criteo.publisher.advancednative.NativeInternalForAdMob;
import com.google.android.gms.ads.formats.NativeAd;

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
