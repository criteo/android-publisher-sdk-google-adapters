package com.criteo.mediation.google;

import android.util.Log;
import androidx.annotation.Nullable;

public class PreconditionsUtil {

  private static final String TAG = PreconditionsUtil.class.getSimpleName();

  public static boolean isNotNull(@Nullable Object value) {
    if (value == null) {
      NullPointerException exception = new NullPointerException("Expected non null value, but null occurs.");
      Log.w(TAG, exception);
      if (BuildConfig.DEBUG) {
        throw exception;
      }
      return false;
    }
    return true;
  }

}
