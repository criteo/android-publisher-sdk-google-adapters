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
