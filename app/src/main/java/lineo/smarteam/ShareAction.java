package lineo.smarteam;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ShareActionProvider;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by marco on 20/09/2017.
 * Share a screenshot
 */

public class ShareAction {
    private ShareActionProvider mShareActionProvider;
    private Activity activity;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public ShareAction(ShareActionProvider mShareActionProvider, Activity activity) {
        this.activity = activity;
        this.mShareActionProvider = mShareActionProvider;
    }

    public void share() {
        Bitmap bitmap = takeScreenshot();
        File sendFile = saveFile(bitmap);
        send(sendFile);
    }

    private Bitmap takeScreenshot(){
        View screenView = activity.getWindow().getDecorView().findViewById(android.R.id.content).getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private File saveFile(Bitmap bitmap) {
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/Screenshots";
        Long tsLong = System.currentTimeMillis()/1000;
        String fileName = "smarteam-lineup-" + tsLong.toString() + ".png";
        File dir = new File(dirPath);
        if(!dir.exists()) {
            if(!dir.mkdir()){
                return null;
            }
        }
        verifyStoragePermissions();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private void send(File sendFile){
        Uri uri = Uri.fromFile(sendFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    private void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
