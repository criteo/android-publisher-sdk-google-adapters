package com.criteo.mediation.google;

import static com.criteo.publisher.CriteoUtil.TEST_CP_ID;
import static com.criteo.publisher.CriteoUtil.clearCriteo;
import static com.criteo.publisher.CriteoUtil.givenInitializedCriteo;
import static com.criteo.publisher.TestAdUnits.BANNER_320_50;
import static com.criteo.publisher.TestAdUnits.INTERSTITIAL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

import com.criteo.publisher.CriteoBannerView;
import com.criteo.publisher.mock.MockedDependenciesRule;
import com.criteo.publisher.model.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class CriteoGoogleAdapterTest {

    @Rule
    public MockedDependenciesRule mockedDependenciesRule = new MockedDependenciesRule();

    @Mock
    private CustomEventInterstitialListener interstitialListener;

    @Mock
    private CustomEventBannerListener bannerListener;

    private AdapterHelper adapterHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        adapterHelper = new AdapterHelper();
    }

    @Test
    public void requestBannerAd_GivenEmptyServerParameter_NotifyForInvalidRequest() throws Exception {
        String serverParameter = "";

        adapterHelper.requestBannerAd(serverParameter, new AdSize(320, 50), bannerListener);

        verify(bannerListener).onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
    }

    @Test
    public void requestBannerAd_GivenServerParameterWithoutCpId_NotifyForError() throws Exception {
        JSONObject serverParams = new JSONObject();
        serverParams.put("adUnitId", BANNER_320_50.getAdUnitId());
        String serverParameter = serverParams.toString();

        adapterHelper.requestBannerAd(serverParameter, new AdSize(320, 50), bannerListener);

        verify(bannerListener).onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
    }

    @Test
    public void requestBannerAd_GivenServerParameterWithoutAdUnit_NotifyForError() throws Exception {
        JSONObject serverParams = new JSONObject();
        serverParams.put("cpId", TEST_CP_ID);
        String serverParameter = serverParams.toString();

        adapterHelper.requestBannerAd(serverParameter, new AdSize(320, 50), bannerListener);

        verify(bannerListener).onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
    }

    @Test
    public void requestInterstitialAd_GivenEmptyServerParameter_NotifyForInvalidRequest() throws Exception {
        String serverParameter = "";

        adapterHelper.requestInterstitialAd(serverParameter, interstitialListener);

        verify(interstitialListener).onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
    }

    @Test
    public void requestInterstitialAd_GivenServerParameterWithoutCpId_NotifyForError() throws Exception {
        JSONObject serverParams = new JSONObject();
        serverParams.put("adUnitId", BANNER_320_50.getAdUnitId());
        String serverParameter = serverParams.toString();

        adapterHelper.requestInterstitialAd(serverParameter, interstitialListener);

        verify(interstitialListener).onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
    }

    @Test
    public void requestInterstitialAd_GivenServerParameterWithoutAdUnit_NotifyForError() throws Exception {
        JSONObject serverParams = new JSONObject();
        serverParams.put("cpId", TEST_CP_ID);
        String serverParameter = serverParams.toString();

        adapterHelper.requestInterstitialAd(serverParameter, interstitialListener);

        verify(interstitialListener).onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
    }

    @Test
    public void givenNotInitializedCriteo_WhenLoadingBannerTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
            throws Exception {
        clearCriteo();

        loadValidBanner();
        loadValidBanner();

        checkMissFirstBannerOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    @Test
    public void givenInitializedCriteo_WhenLoadingBannerTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
            throws Exception {
        givenInitializedCriteo();

        loadValidBanner();
        loadValidBanner();

        checkMissFirstBannerOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    @Test
    public void givenNotInitializedCriteo_WhenLoadingInterstitialTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
          throws Exception {
        clearCriteo();

        loadValidInterstitial();
        loadValidInterstitial();

        checkMissFirstInterstitialOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    @Test
    public void givenInitializedCriteo_WhenLoadingInterstitialTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
            throws Exception {
        givenInitializedCriteo();

        loadValidInterstitial();
        loadValidInterstitial();

        checkMissFirstInterstitialOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    private void loadValidBanner() {
        adapterHelper.requestBannerAd(BANNER_320_50, bannerListener);
        mockedDependenciesRule.waitForIdleState();
    }

    private void loadValidInterstitial() {
        adapterHelper.requestInterstitialAd(INTERSTITIAL, interstitialListener);
        mockedDependenciesRule.waitForIdleState();
    }

    private void checkMissFirstBannerOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
        InOrder inOrder = inOrder(bannerListener);
        inOrder.verify(bannerListener).onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
        inOrder.verify(bannerListener).onAdLoaded(any(CriteoBannerView.class));
        inOrder.verifyNoMoreInteractions();
    }

    private void checkMissFirstInterstitialOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
        InOrder inOrder = inOrder(interstitialListener);
        inOrder.verify(interstitialListener).onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
        inOrder.verify(interstitialListener).onAdLoaded();
        inOrder.verifyNoMoreInteractions();
    }
}
