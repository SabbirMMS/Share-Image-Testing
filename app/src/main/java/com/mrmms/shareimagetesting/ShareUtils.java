package com.mrmms.shareimagetesting;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareUtils {

    public static void shareImageWithText(Context context, View thumbnailLayout, String eventTitle, String collectionTitle, String shareUrl) {
        // Capture the view as a Bitmap
        Bitmap bitmap = captureView(thumbnailLayout);

        // Save the bitmap to a file
        File imageFile = saveBitmapToFile(context, bitmap);

        if (imageFile != null) {
            // Generate a content URI using FileProvider
            Uri imageUri = FileProvider.getUriForFile(
                    context,
                    context.getApplicationContext().getPackageName() + ".fileprovider",
                    imageFile
            );

            // Create the sharing intent
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("image/*");
            sendIntent.putExtra(Intent.EXTRA_TITLE, eventTitle + " - " + collectionTitle);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
            sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            sendIntent.setClipData(ClipData.newRawUri("", imageUri));
            sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(sendIntent, "Share using"));
        }
    }

    private static Bitmap captureView(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private static File saveBitmapToFile(Context context, Bitmap bitmap) {
        File cacheDir = context.getCacheDir();
        File imageFile = new File(cacheDir, "shared_image.png");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
