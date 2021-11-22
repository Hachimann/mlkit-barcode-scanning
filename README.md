# Mlkit Barcode Scanning
[![](https://jitpack.io/v/Hachimann/mlkit-barcode-scanning.svg)](https://jitpack.io/#Hachimann/mlkit-barcode-scanning)

Barcode scanner library for Android, based on [ML Kit's barcode scanning API][1].

This project is based on [ML Kit Vision Quickstart Sample App][2] and provides the ability to quickly connect barcode scanning to the Android app.

Features:

1. Ability to get results through interfaces;
2. Can be embedded in an Activity;
3. Ability to use your own graphic overlay;
4. Barcode scanning settings.

An example of how to use the library can be found [here][3]

## Installation

Add the dependency to your `build.gradle` file:

```groovy
dependencies {
  implementation 'com.github.hachimann:mlkit-barcode-scanning:${version}'
}
```

If you want to use advanced scanning results, you should also add this library to your `build.gradle` file:

```groovy
dependencies {
  implementation 'com.google.mlkit:barcode-scanning:${version}'
}
```

## Usage

Add CameraSourcePreview to your XML like any other view and include GraphicOverlay:

```xml
<com.github.hachimann.mlkit_barcode_scanning.camera.CameraSourcePreview
  android:id="@+id/camera_preview"
  android:layout_width="match_parent"
  android:layout_height="match_parent">

  <include layout="@layout/camera_preview_overlay" />

</com.github.hachimann.mlkit_barcode_scanning.camera.CameraSourcePreview>
```

To get started add the following code to `onCreate`:

```java
try {
  barcodeScan = new BarcodeScan.Builder(this, findViewById(R.id.camera_preview)).build();
} catch (Exception exception) {
  exception.printStackTrace();
}
```

To start the camera, add the following code to `onResume`:

```java
try {
  barcodeScan.startCameraSource();
} catch (Exception exception) {
  exception.printStackTrace();
}
```

To stop the camera, add the following code to `onPause` and `onDestroy`:

```java
@Override
protected void onPause() {
  super.onPause();
  barcodeScan.stopCameraPreview();
}

@Override
public void onDestroy() {
  super.onDestroy();
  barcodeScan.stopCameraSource();
}
```

## Customization and advanced options

To set your own graphic overlay:

```java
try {
  barcodeScan = new BarcodeScan.Builder(this, findViewById(R.id.camera_preview))
    .setGraphicOverlay(graphicOverlay)
    .build();
} catch (Exception exception) {
  exception.printStackTrace();
}
```

To set your own PromptChip:

```java
try {
  barcodeScan = new BarcodeScan.Builder(this, findViewById(R.id.camera_preview))
    .setPromptChip(promptChip)
    .build();
} catch (Exception exception) {
  exception.printStackTrace();
}
```

To set your own PromptChipAnimator:

```java
try {
  barcodeScan = new BarcodeScan.Builder(this, findViewById(R.id.camera_preview))
    .setPromptChipAnimator(promptChipAnimator)
    .build();
} catch (Exception exception) {
  exception.printStackTrace();
}
```

To enable or disable the barcode size check:

```java
try {
  barcodeScan = new BarcodeScan.Builder(this, findViewById(R.id.camera_preview))
    .setEnableBarcodeSizeCheck(false)
    .build();
} catch (Exception exception) {
  exception.printStackTrace();
}
```

To set the height and width of the barcode recognition area:

```java
try {
  barcodeScan = new BarcodeScan.Builder(this, findViewById(R.id.camera_preview))
    .setBarcodeReticleWidth(60)
    .setBarcodeReticleHeight(30)
    .build();
} catch (Exception exception) {
  exception.printStackTrace();
}
```

To set the minimum barcode width for which the recognition can be performed:

```java
try {
  barcodeScan = new BarcodeScan.Builder(this, findViewById(R.id.camera_preview))
    .setMinimumBarcodeWidth(50)
    .build();
} catch (Exception exception) {
  exception.printStackTrace();
}
```

To set a delay after barcode recognition:

```java
try {
  barcodeScan = new BarcodeScan.Builder(this, findViewById(R.id.camera_preview))
    .setDelayLoadingBarcodeResult(false)
    .build();
} catch (Exception exception) {
  exception.printStackTrace();
}
```

## Getting barcode scan results

There are two interfaces to get the barcode scan results.
One of them `onBarcodeStringResult` returns the result of scanning as a string, the other `onBarcodeResult` - as an object of type Barcode. To use this type and get advanced scan results, the `com.google.mlkit:barcode-scanning` library must be connected.

onBarcodeStringResult:

```java
@Override
public void onBarcodeStringResult(String barcodeString) {

}
```

onBarcodeResult:

```java
@Override
public void onBarcodeResult(Barcode barcode) {

}
```

[1]: https://developers.google.com/ml-kit
[2]: https://github.com/googlesamples/mlkit/tree/master/android/vision-quickstart
[3]: https://github.com/Hachimann/mlkit-barcode-scanning/tree/master/app
