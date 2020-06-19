package com.criteo.mediation.google.advancednative;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.criteo.publisher.advancednative.CriteoNativeAd;
import com.criteo.publisher.advancednative.CriteoNativeRenderer;
import com.criteo.publisher.advancednative.RendererHelper;

public class NoOpNativeRenderer implements CriteoNativeRenderer {

  @NonNull
  @Override
  public View createNativeView(@NonNull Context context, @Nullable ViewGroup parent) {
    return new View(context);
  }

  @Override
  public void renderNativeView(
      @NonNull RendererHelper helper,
      @NonNull View nativeView,
      @NonNull CriteoNativeAd nativeAd
  ) {
    // no-op
  }
}
