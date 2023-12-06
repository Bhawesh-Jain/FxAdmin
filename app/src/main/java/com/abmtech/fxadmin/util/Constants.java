package com.abmtech.fxadmin.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Constants {

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());
    }


    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 1, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap decodeImage(String image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        byte[] decodedString = new byte[0];
        try {
            decodedString = Base64.decode(image, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("TAG", "decodeImage: Image Decode: ", e);
        }
        if (decodedString != null && decodedString.length > 0) {
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } else {
            return null;
        }
    }

}
