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

package com.criteo.mediation.google.advancednative;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

/**
 * Drawable wrapping the drawable of an {@link ImageView} dynamically.
 * <p>
 * If the drawable of the wrapped image view changes (via {@link ImageView#setImageDrawable(Drawable)})
 * for instance), then this drawable changes accordingly to reflect the new drawable.
 * <p>
 * This is intended to be used for drawables that would be downloaded and put inside the wrapped
 * image view. As the download is expected to only last few seconds, the updating phase only last
 * few seconds as well. After this amount of time, this drawable is not updated anymore, except in
 * case of redraw.
 * <p>
 * Note that the implementation uses a {@link LevelListDrawable} as a base class. However, this is
 * only used as a helper to manage the different drawables. This drawable has no notion of level.
 */
class IconViewDrawable extends LevelListDrawable {

  @VisibleForTesting
  static final long UPDATE_DELAY_MS = 100;
  private static final long MAX_UPDATE_DURATION_MS = 10_000;
  private static final long MAX_UPDATE_STEPS = MAX_UPDATE_DURATION_MS / UPDATE_DELAY_MS;

  @NonNull
  private final ImageView iconView;

  @NonNull
  private final Handler handler;

  private long remainingUpdateSteps;
  private int nextLevel;

  IconViewDrawable(@NonNull ImageView iconView) {
    super();
    this.iconView = iconView;
    this.nextLevel = 0;
    this.handler = new Handler(Looper.getMainLooper());
    this.remainingUpdateSteps = MAX_UPDATE_STEPS;
  }

  @Override
  public void draw(@NonNull Canvas canvas) {
    updateDrawable();
    super.draw(canvas);
  }

  private void updateDrawable() {
    Drawable imageDrawable = iconView.getDrawable();
    Drawable current = getCurrent();
    if (current == null || current != imageDrawable) {
      if (imageDrawable != null) {
        addLevel(nextLevel, nextLevel, imageDrawable);
        setLevel(nextLevel);
        nextLevel++;
      }
    }

    postUpdateDrawable();
  }

  private void postUpdateDrawable() {
    handler.removeCallbacksAndMessages(null);
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        remainingUpdateSteps--;
        if (remainingUpdateSteps > 0) {
          updateDrawable();
        }
      }
    }, UPDATE_DELAY_MS);
  }

}
