package com.github.hachimann.mlkit_barcode_scanning.barcodedetection;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.core.content.ContextCompat;

import com.github.hachimann.mlkit_barcode_scanning.R;
import com.github.hachimann.mlkit_barcode_scanning.camera.CameraReticleAnimator;
import com.github.hachimann.mlkit_barcode_scanning.camera.GraphicOverlay;

import org.jetbrains.annotations.NotNull;

/**
 * A camera reticle that locates at the center of canvas to indicate the system is active but has
 * not detected a barcode yet.
 */
public final class BarcodeReticleGraphic extends BarcodeGraphicBase {

    private final Paint ripplePaint;
    private final int rippleSizeOffset;
    private final int rippleStrokeWidth;
    private final int rippleAlpha;
    private final CameraReticleAnimator animator;

    public BarcodeReticleGraphic(@NotNull GraphicOverlay overlay,
                                 @NotNull CameraReticleAnimator animator) {
        super(overlay);
        this.animator = animator;
        Resources resources = overlay.getResources();
        ripplePaint = new Paint();
        ripplePaint.setStyle(Paint.Style.STROKE);
        ripplePaint.setColor(ContextCompat.getColor(context, R.color.reticle_ripple));
        rippleSizeOffset =
                resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_size_offset);
        rippleStrokeWidth =
                resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_stroke_width);
        rippleAlpha = ripplePaint.getAlpha();
    }

    public void draw(@NotNull Canvas canvas) {
        super.draw(canvas);
        // Draws the ripple to simulate the breathing animation effect.
        ripplePaint.setAlpha((int) (rippleAlpha * animator.getRippleAlphaScale()));
        ripplePaint.setStrokeWidth(rippleStrokeWidth * animator.getRippleStrokeWidthScale());
        float offset = rippleSizeOffset * animator.getRippleSizeScale();
        RectF rippleRect = new RectF(
                getBoxRect().left - offset,
                getBoxRect().top - offset,
                getBoxRect().right + offset,
                getBoxRect().bottom + offset
        );
        canvas.drawRoundRect(rippleRect, getBoxCornerRadius(),
                getBoxCornerRadius(), ripplePaint);
    }
}
