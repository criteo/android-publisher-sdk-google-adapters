package com.criteo.mediation.google.advancednative

import com.criteo.publisher.CriteoErrorCode
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CriteoNativeEventListenerTest {

  @Mock
  private lateinit var adMobListener: CustomEventNativeListener

  private lateinit var listener: CriteoNativeEventListener

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)

    listener = CriteoNativeEventListener(mock(), adMobListener)
  }

  @Test
  fun onAdFailedToReceive_GivenNoFill_ReportToAdMobListener() {
    listener.onAdFailedToReceive(CriteoErrorCode.ERROR_CODE_NO_FILL)

    verify(adMobListener).onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL)
  }

  @Test
  fun onAdFailedToReceive_GivenInternalError_ReportToAdMobListener() {
    listener.onAdFailedToReceive(CriteoErrorCode.ERROR_CODE_INTERNAL_ERROR)

    verify(adMobListener).onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR)
  }

}