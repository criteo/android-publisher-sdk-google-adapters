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
