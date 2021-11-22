package com.github.hachimann.mlkit_barcode_scanning.barcodedetection;

import static com.github.hachimann.mlkit_barcode_scanning.PreferenceUtils.getBarcodeReticleBox;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import androidx.core.content.ContextCompat;

import com.github.hachimann.mlkit_barcode_scanning.R;
import com.github.hachimann.mlkit_barcode_scanning.camera.GraphicOverlay;

import org.jetbrains.annotations.NotNull;

public abstract class BarcodeGraphicBase extends GraphicOverlay.Graphic {
    private final Paint boxPaint;
    private final Paint scrimPaint;
    private final Paint eraserPaint;
    private final float boxCornerRadius;
    @NotNull
    private final Paint pathPaint;
    @NotNull
    private final RectF boxRect;

    public BarcodeGraphicBase(@NotNull GraphicOverlay overlay) {
        super(overlay);

        boxPaint = new Paint();
        boxPaint.setColor(ContextCompat.getColor(context, R.color.barcode_reticle_stroke));
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth((float) context.getResources().getDimensionPixelOffset(
                R.dimen.barcode_reticle_stroke_width));

        scrimPaint = new Paint();
        scrimPaint.setColor(ContextCompat.getColor(context, R.color.barcode_reticle_background));

        eraserPaint = new Paint();
        eraserPaint.setStrokeWidth(boxPaint.getStrokeWidth());
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        boxCornerRadius = (float) context.getResources().getDimensionPixelOffset(
                R.dimen.barcode_reticle_corner_radius);

        pathPaint = new Paint();
        pathPaint.setColor(Color.WHITE);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(boxPaint.getStrokeWidth());
        pathPaint.setPathEffect(new CornerPathEffect(boxCornerRadius));

        this.boxRect = getBarcodeReticleBox(overlay);
    }

    public final float getBoxCornerRadius() {
        return boxCornerRadius;
    }

    @NotNull
    public final Paint getPathPaint() {
        return pathPaint;
    }

    @NotNull
    public final RectF getBoxRect() {
        return boxRect;
    }

    public void draw(@NotNull Canvas canvas) {
        // Draws the dark background scrim and leaves the box area clear.
        canvas.drawRect(0.0F, 0.0F, (float) canvas.getWidth(),
                (float) canvas.getHeight(), scrimPaint);
        // As the stroke is always centered, so erase twice with FILL and STROKE respectively to clear
        // all area that the box rect would occupy.
        eraserPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint);
        eraserPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint);
        // Draws the box.
        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, boxPaint);
    }
}
