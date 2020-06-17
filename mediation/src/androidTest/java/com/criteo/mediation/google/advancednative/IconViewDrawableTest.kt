package com.criteo.mediation.google.advancednative

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.criteo.mediation.google.activity.DummyActivity
import com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait
import com.criteo.publisher.concurrent.ThreadingUtil.waitForMessageQueueToBeIdle
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class IconViewDrawableTest {

  @Rule
  @JvmField
  var scenarioRule: ActivityScenarioRule<DummyActivity> = ActivityScenarioRule(DummyActivity::class.java)

  @Test
  fun setImageDrawable_GivenDifferentDrawables_UpdateAccordingly() {
    lateinit var drawable1: Drawable
    lateinit var drawable2: Drawable
    lateinit var delegateImageView: ImageView
    lateinit var displayedImageView: ImageView

    scenarioRule.scenario.onActivity {
      drawable1  = it.getDrawable(android.R.drawable.ic_delete)!!
      drawable2  = it.getDrawable(android.R.drawable.ic_secure)!!
      assertThat(drawable1.intrinsicWidth).isNotEqualTo(drawable2.intrinsicWidth)

      delegateImageView = ImageView(it)
      displayedImageView = ImageView(it)
    }

    val drawable = IconViewDrawable(delegateImageView)

    scenarioRule.scenario.onActivity {
      displayedImageView.setImageDrawable(drawable)
      it.setContentView(displayedImageView)
    }

    assertThat(drawable.intrinsicWidth)
        .describedAs("An empty drawable should not have intrinsic size")
        .isEqualTo(-1)

    runOnMainThreadAndWait {
      delegateImageView.setImageDrawable(drawable1)
    }
    waitForIdleState()

    assertThat(drawable.intrinsicWidth).isEqualTo(drawable1.intrinsicWidth)

    runOnMainThreadAndWait {
      delegateImageView.setImageDrawable(drawable2)
    }
    waitForIdleState()

    assertThat(drawable.intrinsicWidth).isEqualTo(drawable2.intrinsicWidth)
  }

  private fun waitForIdleState() {
    // Unfortunately, the tested code rely on delayed async tasks, and waitForMessageQueueToBeIdle
    // does not seem to support this well. Hence, an additional sleep is required.
    Thread.sleep(IconViewDrawable.UPDATE_DELAY_MS)

    // At this point, the update step might be running. We must wait for it.
    waitForMessageQueueToBeIdle()
  }

}