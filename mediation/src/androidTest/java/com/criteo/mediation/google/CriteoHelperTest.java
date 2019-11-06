package com.criteo.mediation.google;

import com.criteo.publisher.Criteo;
import org.junit.Test;

public class CriteoHelperTest {

  @Test(expected = IllegalStateException.class)
  public void givenNotInitializedCriteo_WhenGettingCriteoInstance_ShouldThrow() throws Exception {
    CriteoHelper.givenNotInitializedCriteo();

    Criteo.getInstance();
  }

}
