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

package com.criteo.mediation.google.advancednative

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.test.rule.ActivityTestRule
import com.criteo.mediation.google.activity.DummyActivity
import com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait
import com.criteo.publisher.concurrent.ThreadingUtil.waitForMessageQueueToBeIdle
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class IconViewDrawableTest {

  @Rule
  @JvmField
  var testRule: ActivityTestRule<DummyActivity> = ActivityTestRule(DummyActivity::class.java)

  @Test
  fun setImageDrawable_GivenDifferentDrawables_UpdateAccordingly() {
    lateinit var drawable1: Drawable
    lateinit var drawable2: Drawable
    lateinit var delegateImageView: ImageView
    lateinit var displayedImageView: ImageView

    testRule.runOnUiThread {
      val activity = testRule.activity
      drawable1  = activity.getDrawable(android.R.drawable.ic_delete)!!
      drawable2  = activity.getDrawable(android.R.drawable.ic_secure)!!
      assertThat(drawable1.intrinsicWidth).isNotEqualTo(drawable2.intrinsicWidth)

      delegateImageView = ImageView(activity)
      displayedImageView = ImageView(activity)
    }

    val drawable = IconViewDrawable(delegateImageView)

    testRule.runOnUiThread {
      val activity = testRule.activity
      displayedImageView.setImageDrawable(drawable)
      activity.setContentView(displayedImageView)
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
