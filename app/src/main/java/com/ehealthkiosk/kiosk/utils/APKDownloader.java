package com.ehealthkiosk.kiosk.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.ehealthkiosk.kiosk.R;

import java.io.File;

public class APKDownloader {

    public static void downloadAPK(Context context){
        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "AppName.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            file.delete();

        //get url of app on server
        String url = Constants.base_url +  "kiosk/update";

        //set download manager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading Latest APK");
        request.setTitle("yolohealth.apk");

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Log.d("TAG", "Download is finished now");
                Uri apkURI = FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext()
                                .getPackageName() + ".provider", file);
                install.setDataAndType(apkURI, manager.getMimeTypeForDownloadedFile(downloadId));
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Log.d("TAG", "will start installer");
                context.startActivity(install);

                context.unregisterReceiver(this);
                ((Activity)context).finish();
            }
        };
        //register receiver for when .apk download is compete
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }
}
