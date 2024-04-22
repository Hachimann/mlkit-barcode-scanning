package com.github.hachimann.mlkitbarcodesscan;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.hachimann.mlkit_barcode_scanning.BarcodeResultListener;
import com.github.hachimann.mlkit_barcode_scanning.BarcodeScan;
import com.github.hachimann.mlkit_barcode_scanning.BarcodeStringResultListener;
import com.github.hachimann.mlkit_barcode_scanning.barcodedetection.BarcodeField;
import com.github.hachimann.mlkit_barcode_scanning.barcodedetection.BarcodeResultFragment;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BarcodeResultListener,
        BarcodeStringResultListener {
    BarcodeScan barcodeScan;
    private View flashButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            barcodeScan = new BarcodeScan.Builder(this, findViewById(R.id.camera_preview))
                    .setEnableBarcodeSizeCheck(false)
                    .setBarcodeReticleWidth(60)
                    .setBarcodeReticleHeight(30)
                    .setMinimumBarcodeWidth(50)
                    .setDelayLoadingBarcodeResult(false)
                    .build();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        settingsButton = findViewById(R.id.settings_button);
        setFlashButton();
    }

    private void setFlashButton() {
        flashButton = findViewById(R.id.flash_button);
        flashButton.setOnClickListener(view -> {
            if (flashButton.isSelected()) {
                barcodeScan.enableFlash(false);
                flashButton.setSelected(false);
            } else {
                barcodeScan.enableFlash(true);
                flashButton.setSelected(true);
            }
        });
    }

    /**
     * Starts the camera.
     */
    @Override
    public void onResume() {
        super.onResume();
        settingsButton.setEnabled(true);
        try {
            barcodeScan.startCameraSource();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        BarcodeResultFragment.dismiss(getSupportFragmentManager());
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        flashButton.setSelected(false);
        barcodeScan.stopCameraPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        barcodeScan.stopCameraSource();
    }

    public View settingsButton;

    @Override
    public void onBarcodeStringResult(String barcodeString) {
        ArrayList<BarcodeField> barcodeFields = new ArrayList<>();
        barcodeFields.add(new BarcodeField("Value", (barcodeString)));
        BarcodeResultFragment.show(getSupportFragmentManager(),
                barcodeFields);
    }

    @Override
    public void onBarcodeResult(Barcode barcode) {
        Log.i("Barcode", Objects.requireNonNull(barcode.getRawValue()));
    }
}