package com.github.hachimann.mlkit_barcode_scanning.barcodedetection;

import android.os.Parcel;
import android.os.Parcelable;

public class BarcodeField implements Parcelable {
    String label;
    String value;

    public BarcodeField(String label, String value) {
        this.label = label;
        this.value = value;
    }

    protected BarcodeField(Parcel in) {
        label = in.readString();
        value = in.readString();
    }

    public static final Creator<BarcodeField> CREATOR = new Creator<>() {
        @Override
        public BarcodeField createFromParcel(Parcel in) {
            return new BarcodeField(in);
        }

        @Override
        public BarcodeField[] newArray(int size) {
            return new BarcodeField[size];
        }
    };

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(label);
        parcel.writeString(value);
    }
}
