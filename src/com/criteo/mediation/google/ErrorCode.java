package com.criteo.mediation.google;

import androidx.annotation.NonNull;
import com.criteo.publisher.CriteoErrorCode;
import com.google.android.gms.ads.AdRequest;

public class ErrorCode {

  public static int toAdMob(@NonNull CriteoErrorCode code) {
    switch (code) {
      case ERROR_CODE_INTERNAL_ERROR:
        return AdRequest.ERROR_CODE_INTERNAL_ERROR;
      case ERROR_CODE_NETWORK_ERROR:
        return AdRequest.ERROR_CODE_NETWORK_ERROR;
      case ERROR_CODE_INVALID_REQUEST:
        return AdRequest.ERROR_CODE_INVALID_REQUEST;
      case ERROR_CODE_NO_FILL:
        return AdRequest.ERROR_CODE_NO_FILL;
      default:
        throw new UnsupportedOperationException("Unknown Criteo error code: " + code);
    }
  }

}
