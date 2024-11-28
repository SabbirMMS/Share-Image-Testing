package com.mrmms.shareimagetesting;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textTitle = findViewById(R.id.textTitle);
        TextView textCollectionTitle = findViewById(R.id.textCollectionTitle);
        Button shareButton = findViewById(R.id.shareButton);
        Button shareButton2 = findViewById(R.id.shareButton2);

        // Example data
        String eventTitle = "Sample Event";
        String collectionTitle = "Sample Collection";
        String shareUrl = "https://example.com/share";

        textTitle.setText(eventTitle);
        textCollectionTitle.setText(collectionTitle);

        shareButton.setOnClickListener(view -> {
            shareImageWithText(eventTitle, collectionTitle, shareUrl);
        });

        shareButton2.setOnClickListener(view -> {
            // Call the reusable ShareUtils method
            ShareUtils.shareImageWithText(
                    this,
                    findViewById(R.id.shareThumbnailLayout),
                    eventTitle,
                    collectionTitle,
                    shareUrl
            );
        });
    }

    private void shareImageWithText(String eventTitle, String collectionTitle, String shareUrl) {
        // Capture the view as a Bitmap
        View rootView = findViewById(R.id.shareThumbnailLayout);
        Bitmap bitmap = captureView(rootView);

        // Save the bitmap to a file
        File imageFile = saveBitmapToFile(bitmap);

        if (imageFile != null) {
            // Generate a content URI using FileProvider
            Uri imageUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
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

            startActivity(Intent.createChooser(sendIntent, "Share using"));
        }
    }

    private Bitmap captureView(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        File cacheDir = getCacheDir();
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
