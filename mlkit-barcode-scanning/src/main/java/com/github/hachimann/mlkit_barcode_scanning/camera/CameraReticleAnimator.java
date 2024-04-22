package com.github.hachimann.mlkit_barcode_scanning.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import org.jetbrains.annotations.NotNull;

public class CameraReticleAnimator {

    /**
     * Returns the scale value of ripple alpha ranges in [0, 1].
     */
    private float rippleAlphaScale = 0f;

    /**
     * Returns the scale value of ripple size ranges in [0, 1].
     */
    private float rippleSizeScale = 0f;

    /**
     * Returns the scale value of ripple stroke width ranges in [0, 1].
     */
    private float rippleStrokeWidthScale = 1f;

    private final AnimatorSet animatorSet;

    private static final long DURATION_RIPPLE_FADE_IN_MS = 333L;
    private static final long DURATION_RIPPLE_FADE_OUT_MS = 500L;
    private static final long DURATION_RIPPLE_EXPAND_MS = 833L;
    private static final long DURATION_RIPPLE_STROKE_WIDTH_SHRINK_MS = 833L;
    private static final long DURATION_RESTART_DORMANCY_MS = 1333L;
    private static final long START_DELAY_RIPPLE_FADE_OUT_MS = 667L;
    private static final long START_DELAY_RIPPLE_EXPAND_MS = 333L;
    private static final long START_DELAY_RIPPLE_STROKE_WIDTH_SHRINK_MS = 333L;
    private static final long START_DELAY_RESTART_DORMANCY_MS = 1167L;

    public CameraReticleAnimator(@NotNull final GraphicOverlay graphicOverlay) {
        ValueAnimator rippleFadeInAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(
                DURATION_RIPPLE_FADE_IN_MS
        );
        rippleFadeInAnimator.addUpdateListener(valueAnimator -> {
            rippleAlphaScale = (float) valueAnimator.getAnimatedValue();
            graphicOverlay.postInvalidate();
        });

        ValueAnimator rippleFadeOutAnimator = ValueAnimator.ofFloat(1f, 0f).setDuration(
                DURATION_RIPPLE_FADE_OUT_MS
        );
        rippleFadeOutAnimator.setStartDelay(START_DELAY_RIPPLE_FADE_OUT_MS);
        rippleFadeOutAnimator.addUpdateListener(valueAnimator -> {
            rippleAlphaScale = (float) valueAnimator.getAnimatedValue();
            graphicOverlay.postInvalidate();
        });

        ValueAnimator rippleExpandAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(
                DURATION_RIPPLE_EXPAND_MS
        );
        rippleExpandAnimator.setStartDelay(START_DELAY_RIPPLE_EXPAND_MS);
        rippleExpandAnimator.setInterpolator(new FastOutSlowInInterpolator());
        rippleExpandAnimator.addUpdateListener(valueAnimator -> {
            rippleSizeScale = (float) valueAnimator.getAnimatedValue();
            graphicOverlay.postInvalidate();
        });

        ValueAnimator rippleStrokeWidthShrinkAnimator = ValueAnimator.ofFloat(1f, 0.5f).setDuration(
                DURATION_RIPPLE_STROKE_WIDTH_SHRINK_MS
        );
        rippleStrokeWidthShrinkAnimator.setStartDelay(START_DELAY_RIPPLE_STROKE_WIDTH_SHRINK_MS);
        rippleStrokeWidthShrinkAnimator.setInterpolator(new FastOutSlowInInterpolator());
        rippleStrokeWidthShrinkAnimator.addUpdateListener(valueAnimator -> {
            rippleStrokeWidthScale = (float) valueAnimator.getAnimatedValue();
            graphicOverlay.postInvalidate();
        });

        ValueAnimator fakeAnimatorForRestartDelay = ValueAnimator.ofInt(0, 0).setDuration(
                DURATION_RESTART_DORMANCY_MS
        );
        fakeAnimatorForRestartDelay.setStartDelay(START_DELAY_RESTART_DORMANCY_MS);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                rippleFadeInAnimator,
                rippleFadeOutAnimator,
                rippleExpandAnimator,
                rippleStrokeWidthShrinkAnimator,
                fakeAnimatorForRestartDelay
        );

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Reset animation parameters
                resetAnimationParameters();
            }
        });
    }

    private void resetAnimationParameters() {
        rippleAlphaScale = 0f;
        rippleSizeScale = 0f;
        rippleStrokeWidthScale = 1f;
    }

    public final float getRippleAlphaScale() {
        return this.rippleAlphaScale;
    }

    public final float getRippleSizeScale() {
        return this.rippleSizeScale;
    }

    public final float getRippleStrokeWidthScale() {
        return this.rippleStrokeWidthScale;
    }

    public final void start() {
        if (!this.animatorSet.isRunning()) {
            this.animatorSet.start();
        }
    }

    public final void cancel() {
        this.animatorSet.cancel();
        this.rippleAlphaScale = 0.0F;
        this.rippleSizeScale = 0.0F;
        this.rippleStrokeWidthScale = 1.0F;
    }
}
