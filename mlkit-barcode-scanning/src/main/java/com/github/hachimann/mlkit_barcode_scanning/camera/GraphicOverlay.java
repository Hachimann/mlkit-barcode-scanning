package com.github.hachimann.mlkit_barcode_scanning.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.common.images.Size;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class GraphicOverlay extends View {
    private final Object lock = new Object();

    private int previewWidth = 0;
    private float widthScaleFactor = 1.0f;
    private int previewHeight = 0;
    private float heightScaleFactor = 1.0f;
    private final ArrayList<Graphic> graphics = new ArrayList<>();

    public GraphicOverlay(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Base class for a custom graphics object to be rendered within the graphic overlay. Subclass
     * this and implement the [Graphic.draw] method to define the graphics element. Add
     * instances to the overlay using [GraphicOverlay.add].
     */
    public abstract static class Graphic {
        @NotNull
        protected final Context context;
        @NotNull
        protected final GraphicOverlay overlay;

        protected Graphic(@NotNull GraphicOverlay overlay) {
            this.overlay = overlay;
            context = overlay.getContext();
        }

        /**
         * Draws the graphic on the supplied canvas.
         */
        public abstract void draw(@NotNull Canvas canvas);
    }

    /**
     * Removes all graphics from the overlay.
     */
    public final void clear() {
        synchronized (lock) {
            graphics.clear();
        }
        postInvalidate();
    }

    /**
     * Adds a graphic to the overlay.
     */
    public final void add(@NotNull GraphicOverlay.Graphic graphic) {
        synchronized (lock) {
            graphics.add(graphic);
        }
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform image
     * coordinates later.
     */
    public final void setCameraInfo(@NotNull CameraSource cameraSource) {
        Size previewSize = cameraSource.getPreviewSize();
        if (previewSize == null)
            return;
        if (getContext().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            // Swap width and height when in portrait, since camera's natural orientation is landscape.
            previewWidth = previewSize.getHeight();
            previewHeight = previewSize.getWidth();
        } else {
            previewWidth = previewSize.getWidth();
            previewHeight = previewSize.getHeight();
        }
    }

    public final float translateX(float x) {
        return x * widthScaleFactor;
    }

    public final float translateY(float y) {
        return y * heightScaleFactor;
    }

    /**
     * Adjusts the `rect`'s coordinate from the preview's coordinate system to the view
     * coordinate system.
     */
    @NotNull
    public final RectF translateRect(@NotNull Rect rect) {
        return new RectF(
                translateX((float) rect.left),
                translateY((float) rect.top),
                translateX((float) rect.right),
                translateY((float) rect.bottom)
        );
    }

    /**
     * Draws the overlay with its associated graphic objects.
     */
    @Override
    protected void onDraw(@NotNull Canvas canvas) {
        super.onDraw(canvas);

        if (previewWidth > 0 && previewHeight > 0) {
            widthScaleFactor = (float) getWidth() / previewWidth;
            heightScaleFactor = (float) getHeight() / previewHeight;
        }

        synchronized (lock) {
            for (Graphic graphic : graphics) {
                graphic.draw(canvas);
            }
        }
    }
}

