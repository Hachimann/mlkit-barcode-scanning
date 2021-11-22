package com.github.hachimann.mlkit_barcode_scanning.barcodedetection;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;

import com.github.hachimann.mlkit_barcode_scanning.camera.GraphicOverlay;

import org.jetbrains.annotations.NotNull;

/**
 * Draws the graphic to indicate the barcode result is in loading.
 */
public final class BarcodeLoadingGraphic extends BarcodeGraphicBase {
    private final PointF[] boxClockwiseCoordinates;
    private final Point[] coordinateOffsetBits;
    private final PointF lastPathPoint;
    private final ValueAnimator loadingAnimator;

    public BarcodeLoadingGraphic(@NotNull GraphicOverlay overlay, @NotNull ValueAnimator loadingAnimator) {
        super(overlay);
        this.loadingAnimator = loadingAnimator;
        boxClockwiseCoordinates = new PointF[]{
                new PointF(getBoxRect().left, getBoxRect().top),
                new PointF(getBoxRect().right, getBoxRect().top),
                new PointF(getBoxRect().right, getBoxRect().bottom),
                new PointF(getBoxRect().left, getBoxRect().bottom)
        };
        coordinateOffsetBits = new Point[]{
                new Point(1, 0),
                new Point(0, 1),
                new Point(-1, 0),
                new Point(0, -1)
        };
        lastPathPoint = new PointF();
    }

    public void draw(@NotNull Canvas canvas) {
        super.draw(canvas);

        float boxPerimeter = (getBoxRect().width() + getBoxRect().height()) * 2;
        Path path = new Path();
        // The distance between the box's left-top corner and the starting point of white colored path.
        float offsetLen = boxPerimeter * ((float) loadingAnimator.getAnimatedValue()) % boxPerimeter;
        int i = 0;
        while (i < 4) {
            float edgeLen;
            if (i % 2 == 0)
                edgeLen = getBoxRect().width();
            else
                edgeLen = getBoxRect().height();
            if (offsetLen <= edgeLen) {
                lastPathPoint.x =
                        boxClockwiseCoordinates[i].x + coordinateOffsetBits[i].x * offsetLen;
                lastPathPoint.y =
                        boxClockwiseCoordinates[i].y + coordinateOffsetBits[i].y * offsetLen;
                path.moveTo(lastPathPoint.x, lastPathPoint.y);
                break;
            }

            offsetLen -= edgeLen;
            i++;
        }

        // Computes the path based on the determined starting point and path length.
        float pathLen = boxPerimeter * 0.3f;
        for (int j = 0; j <= 3; ++j) {
            int index = (i + j) % 4;
            int nextIndex = (i + j + 1) % 4;
            // The length between path's current end point and reticle box's next coordinate point.
            float lineLen = Math.abs(boxClockwiseCoordinates[nextIndex].x - lastPathPoint.x) +
                    Math.abs(boxClockwiseCoordinates[nextIndex].y - lastPathPoint.y);
            if (lineLen >= pathLen) {
                path.lineTo(
                        lastPathPoint.x + pathLen * coordinateOffsetBits[index].x,
                        lastPathPoint.y + pathLen * coordinateOffsetBits[index].y
                );
                break;
            }

            lastPathPoint.x = boxClockwiseCoordinates[nextIndex].x;
            lastPathPoint.y = boxClockwiseCoordinates[nextIndex].y;
            path.lineTo(lastPathPoint.x, lastPathPoint.y);
            pathLen -= lineLen;
        }

        canvas.drawPath(path, getPathPaint());
    }
}
