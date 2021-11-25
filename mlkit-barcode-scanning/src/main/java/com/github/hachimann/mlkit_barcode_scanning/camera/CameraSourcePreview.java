package com.github.hachimann.mlkit_barcode_scanning.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.github.hachimann.mlkit_barcode_scanning.R;
import com.google.android.gms.common.images.Size;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class CameraSourcePreview extends FrameLayout {

    private final SurfaceView surfaceView;
    private GraphicOverlay graphicOverlay;
    private boolean startRequested;
    private boolean surfaceAvailable;
    private CameraSource cameraSource;
    private Size cameraPreviewSize;
    private static final String TAG = "CameraSourcePreview";

    public CameraSourcePreview(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
        surfaceView = new SurfaceView(context);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(surfaceView);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        graphicOverlay = findViewById(R.id.camera_preview_graphic_overlay);
    }

    public final void start(@NotNull CameraSource cameraSource) throws IOException {
        this.cameraSource = cameraSource;
        startRequested = true;
        startIfReady();
    }

    public final void stop() {
        if (cameraSource != null) {
            cameraSource.stop();
            cameraSource = null;
            startRequested = false;
        }
    }

    @SuppressLint({"MissingPermission"})
    private void startIfReady() throws IOException {
        if (startRequested && surfaceAvailable) {
            if (cameraSource != null) {
                cameraSource.start(surfaceView.getHolder());
            }
            requestLayout();
            if (graphicOverlay != null) {
                if (cameraSource != null)
                    graphicOverlay.setCameraInfo(cameraSource);
                graphicOverlay.clear();
            }
            startRequested = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (getChildCount() > 0) {
            int width = right - left;
            int height = bottom - top;
            int previewWidth = width;
            int previewHeight = height;

            if (cameraSource != null && cameraSource.getPreviewSize() != null) {
                cameraPreviewSize = cameraSource.getPreviewSize();
            }

            if (getContext().getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_PORTRAIT) {
                // Camera's natural orientation is landscape, so need to swap width and height.
                if (cameraPreviewSize != null) {
                    previewWidth = cameraPreviewSize.getHeight();
                    previewHeight = cameraPreviewSize.getWidth();
                }
            } else {
                if (cameraPreviewSize != null) {
                    previewWidth = cameraPreviewSize.getWidth();
                    previewHeight = cameraPreviewSize.getHeight();
                }
            }

            // Center the child SurfaceView within the parent.
            int scaledChildWidth = previewWidth * height / previewHeight;
            if (width * previewHeight < height * previewWidth) {
                for (int i = 0; i < getChildCount(); ++i) {
                    getChildAt(i).layout(
                            (width - scaledChildWidth) / 2, 0,
                            (width + scaledChildWidth) / 2, height
                    );
                }
            } else {
                int scaledChildHeight = previewHeight * width / previewWidth;
                for (int i = 0; i < getChildCount(); ++i) {
                    if (!(getChildAt(i) instanceof FrameLayout)) {
                        getChildAt(i).layout(
                                0, (height - scaledChildHeight) / 2,
                                width, (height + scaledChildHeight) / 2
                        );
                    } else {
                        getChildAt(i).layout(
                                (width - scaledChildWidth) / 2, 0,
                                (width + scaledChildWidth) / 2, height
                        );
                    }
                }
            }
        }

        try {
            startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private final class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(@NotNull SurfaceHolder surface) {
            surfaceAvailable = true;
            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(@NotNull SurfaceHolder surface) {
            surfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(@NotNull SurfaceHolder holder, int format, int width, int height) {
        }
    }
}

