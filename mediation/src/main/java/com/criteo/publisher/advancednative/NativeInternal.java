package com.criteo.publisher.advancednative;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import java.net.URL;

public class NativeInternal {

  /**
   * This class serves as a bridge between this adapter and the internals (package-private) of the
   * SDK.
   * <p>
   * To access the package-private methods, a Java class should be in the same package. This is why
   * this class is @Keep to not mangle the package name. Else the package name could become, {@code
   * com.criteo.a.a} and then the runtime could throw a {@link IllegalAccessError}.
   */
  @Keep
  @RestrictTo(Scope.LIBRARY)
  public NativeInternal() {
    // no-op
  }

  @Nullable
  public static View getAdChoiceView(@NonNull CriteoNativeAd nativeAd, @NonNull View overlappedView) {
    return nativeAd.getAdChoiceView(overlappedView);
  }

  public static void setAdChoiceClickableView(@NonNull CriteoNativeAd nativeAd, @NonNull View adChoiceView) {
    nativeAd.setAdChoiceClickableView(adChoiceView);
  }

  public static void setRenderer(
      @NonNull CriteoNativeAd nativeAd,
      @NonNull CriteoNativeRenderer renderer) {
    nativeAd.setRenderer(new AdChoiceOverlayNativeRenderer(renderer));
  }

  @NonNull
  public static ImageView getImageView(@NonNull CriteoMediaView mediaView) {
    return mediaView.getImageView();
  }

  @NonNull
  public static URL getImageUrl(@NonNull CriteoMedia mediaContent) {
    return mediaContent.getImageUrl();
  }

}
