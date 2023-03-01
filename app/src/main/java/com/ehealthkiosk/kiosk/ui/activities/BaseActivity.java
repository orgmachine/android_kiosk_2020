package com.ehealthkiosk.kiosk.ui.activities;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.ehealthkiosk.kiosk.BuildConfig;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.MessageEvent;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.PermissionDialogView;
import com.ehealthkiosk.kiosk.utils.VersionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

import static com.ehealthkiosk.kiosk.HealthKioskApp.getAppContext;

public abstract class BaseActivity extends ActivityManagePermission {

    public static int REQUEST_CAMERA = 1;
    public static int SELECT_FILE = 2;


    public void openUserDetailsDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_userdetails, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        switch (event.type) {

        }
    }

    public void getPermissionInfo(final int pos, final AlertDialog dialog) {

        askCompactPermissions(new String[]{
                PermissionUtils.Manifest_CAMERA,
                PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE

        }, new PermissionResult() {

            @Override
            public void permissionGranted() {
                if (pos == 0)
                    openCamera(dialog);
                else
                    openGallery(dialog);

            }

            @Override
            public void permissionDenied() {
                PermissionDialogView.gotoSettingsDialog(BaseActivity.this,
                        PermissionDialogView.PHONE_PERMISSION);
            }

            @Override
            public void permissionForeverDenied() {
                PermissionDialogView.gotoSettingsDialog(BaseActivity.this,
                        PermissionDialogView.PHONE_PERMISSION);
            }
        });
    }

    private void openCamera(Dialog dialog) {

         if (dialog != null){
             dialog.dismiss();
         }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageTempPath(Constants.ProfileImagePathTag));
        startActivityForResult(intent, REQUEST_CAMERA);


        intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                getImageTempPath(Constants.ProfileImagePathTag));
        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, getImageTempPath(Constants.ProfileImagePathTag), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private void openGallery(Dialog dialog) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                SELECT_FILE);
        dialog.dismiss();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Uri getImageTempPath(String from) {
        Uri uri = null;
        try {
            File file = null;
            file = createImageFile();


            if (file != null) {
                if (VersionUtils.isAfter24()) {
                    uri = FileProvider.getUriForFile(getAppContext(),
                            BuildConfig.APPLICATION_ID + ".provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
            }
             Log.d("Image Path: ", uri.getPath());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return uri;
    }

    public File createImageFile() {
        String imgpath = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + getResources().getString(R.string.app_name)
                + File.separator
                + "Media"
                + File.separator + "Images" + File.separator + "Temp";
        File dir = new File(imgpath);
        if (!dir.exists())
            dir.mkdirs();

        String filename = "/default.jpg";

        File nomedia = new File(dir, ".nomedia");
        try {
            nomedia.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(dir, filename);

        return file;
    }

    public Bitmap getRotatedBitmap(String url) {
        try {

            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(url, bounds);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inDither = true;
            Bitmap bm = BitmapFactory.decodeFile(url, opts);

            ExifInterface exif = new ExifInterface(url);
            String orientString = exif
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer
                    .parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2,
                    (float) bm.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0,
                    bounds.outWidth, bounds.outHeight, matrix, true);

            Bitmap decoded = getResizedBitmap(rotatedBitmap, 1024);

            Log.e("Original   dimensions", rotatedBitmap.getWidth() + " " + rotatedBitmap.getHeight());
            Log.e("Compressed dimensions", decoded.getWidth() + " " + decoded.getHeight());
            return decoded;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return null;

    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void copyStream(Uri uri)
            throws IOException {

        InputStream inputStream = getContentResolver().openInputStream(uri);
        FileOutputStream fileOutputStream = new FileOutputStream(createImageFile().getAbsolutePath());

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }

        fileOutputStream.close();
        inputStream.close();

    }

    public File createDir(Bitmap mbitmap) throws Exception {
        // the file to be moved or copied

        String des_path = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + getResources().getString(R.string.app_name)
                + File.separator
                + "Media"
                + File.separator + "Images" + File.separator + "Profile";

        //System.out.println((des_path);
        File des_dir = new File(des_path);

        if (!des_dir.exists()) {
            des_dir.mkdirs();
        }

        File nomedia = new File(des_dir, ".nomedia");
        nomedia.createNewFile();

        String filename = "/user" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        // make sure your target location folder exists!
        File targetLocation = new File(des_dir + filename);

        // just to take note of the location sources
        // Log.d("targetLocation: ", "img" + targetLocation);

        try {

            OutputStream out = new FileOutputStream(targetLocation);
            mbitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Log.v("img", "Copy file successfully.");
            // Log.d("ret img", targetLocation.toString());

            out.flush();
            out.close();

            return targetLocation;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Integer> getVisibleView() {
        List<Integer> skipIds = new ArrayList<>();
        skipIds.add(R.id.toolbar);

        return skipIds;
    }
}
