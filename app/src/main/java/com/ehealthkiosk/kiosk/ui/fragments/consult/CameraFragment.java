package com.ehealthkiosk.kiosk.ui.fragments.consult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageData;
import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageDetails;
import com.ehealthkiosk.kiosk.model.sendDermaImage.sendImageResultData;
import com.ehealthkiosk.kiosk.ui.SubmitImagePresenter;
import com.ehealthkiosk.kiosk.ui.SubmitImagePresenterImpl;
import com.ehealthkiosk.kiosk.ui.SubmitImageView;
import com.ehealthkiosk.kiosk.ui.activities.PDFViewActivity;
import com.ehealthkiosk.kiosk.ui.fragments.DermascopeTestFragment;
import com.ehealthkiosk.kiosk.utils.CameraParent;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.jiangdg.usbcamera.utils.FileUtils;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraFragment extends Fragment implements SubmitImageView, CameraDialog.CameraDialogParent, CameraViewInterface.Callback{

    Activity parent;

    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
    String filePath;

    private static final int REQUEST_CODE = 1;
    private List<String> mMissPermissions = new ArrayList<>();

    @BindView(R.id.camera_view)
    public View mTextureView;
    @BindView(R.id.cancel)
    public Button cancel;
    @BindView(R.id.send_derma)
    public Button send;
    @BindView(R.id.description)
    public EditText description;

    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;

    SubmitImagePresenter submitImagePresenter;

    SendImageData sendImageData;
    com.ehealthkiosk.kiosk.model.sendDermaImage.sendImageResultData sendImageResultData;

    ProgressDialog progressDialog;


    private boolean isRequest;
    private boolean isPreview;

    String reportType;

    private UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            // request open permission

            Log.d("usbcam", "Camera on attach");
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper != null) {

                    Log.d("usbcam", "calling request permission");
                    mCameraHelper.requestPermission(0);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
                Common_Utils.showToast(device.getDeviceName() + " is out");
            }
        }


        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {

            Log.d("usbcam", "On connect dev");
            if (!isConnected) {
                Log.d("usbcam", "not connected");
                Common_Utils.showToast("fail to connect,please check resolution params");
                isPreview = false;
            } else {
                Log.d("usbcam", "Connected");
                isPreview = true;
                Common_Utils.showToast("connecting");
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            Common_Utils.showToast("disconnecting");
        }
    };




    public CameraFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this, v);

        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
        parent = getActivity();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreview();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CameraParent)parent).closeCamera();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        setupCamera();
        super.onResume();
    }

    private void showPreview() {

        Bitmap bm = ((TextureView) mTextureView).getBitmap();
        String path = Common_Utils.saveImage(bm);

        Log.d("usbcam", path);
        filePath = path;
        showDialog();
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);

        File file = new File(filePath);
        Uri imageUri = Uri.fromFile(file);


        //Yes Button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Code2care ", "Yes button Clicked!");
                captureImage();
                dialog.dismiss();
            }
        });
        //Yes Button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Code2care ", "Yes button Clicked!");
                dialog.dismiss();
            }
        });




        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_image, null);
        ImageView image = dialoglayout.findViewById(R.id.imageView);
        builder.setView(dialoglayout);
        builder.show();

        Glide.with(parent)
                .load(imageUri)
                .into(image);
    }

//
//    public void captureImage(){
//        submitImagePresenter = new SubmitImagePresenterImpl(this);
//
//
//        String profileId = SharedPrefUtils.getProfileId(parent);
//        sendImageData = new SendImageData();
//        sendImageData.setPhoto(new File(filePath));
//        sendImageData.setId(Integer.valueOf(profileId));
//        sendImageData.setReportType(Constants.REPORT_TYPE_DERMA);
//        sendImageData.setDescription(description.getText().toString());
//
//        submitImagePresenter.setSendImageData(sendImageData);
//
//
//        parent.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                Common_Utils.showProgress(parent);
//            }
//        });
//    }

    private void setupCamera() {

        mUVCCameraView = (CameraViewInterface) mTextureView;
        mUVCCameraView.setCallback(this);

        mCameraHelper.initUSBMonitor(parent, mUVCCameraView, listener);

        mCameraHelper.setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
            @Override
            public void onPreviewResult(byte[] nv21Yuv) {
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("usbcam", "On Start called for the camera");
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FileUtils.releaseFile();
        // step.4 release uvc camera resources
        if (mCameraHelper != null) {
            mCameraHelper.release();
        }
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mCameraHelper.getUSBMonitor();
    }
    @Override
    public void onDialogResult(boolean canceled) {

    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        Log.d("usbcam", "surface created called");
        mCameraHelper.startPreview(mUVCCameraView);
        if (true) {
            Log.d("usbcam", "camera view will be attached");
            mCameraHelper.startPreview(mUVCCameraView);
            isPreview = true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        Log.d("usbcam", "surface destroy called");
        if (isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.stopPreview();
            isPreview = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mMissPermissions.remove(permissions[i]);
                }
            }
        }
    }



    @Override
    public void showProgress() {

    }

    @Override
    public void showSuccess(SendImageDetails sendImageResponse) {
        Common_Utils.hideProgress();
        String img = sendImageResponse.getData().getImage_url();
        Log.d("pranav", "image upload got url " + img);
        String desc = description.getText().toString();
        if(description.getText().toString().equals("")){
            desc = "Patient Image";
        }
        ((CameraParent)parent).sendImageChat(
                img,
                "Dermoscope",
                desc
        );

    }

    @Override
    public void showError(String msg) {
        Common_Utils.hideProgress();
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private void captureImage() {
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Common_Utils.showProgress(parent);
            }
        });
        String profileId = SharedPrefUtils.getProfileId(parent);

        HashMap<String, RequestBody> comment = new HashMap<>();
        comment.put("profile_id", RestClient.createPartFromString(String.valueOf(profileId)));
        comment.put("description", RestClient.createPartFromString(description.getText().toString()));
        comment.put("report_type", RestClient.createPartFromString(Constants.REPORT_TYPE_DERMA));

        List<MultipartBody.Part> bodies = new ArrayList<>();
            bodies.add(RestClient.preparePNGPart("images", new File(filePath)));


        Call<SendImageDetails> call = RestClient.getClient().uploadImages(
                token, kioskId, comment, bodies
        );

        call.enqueue(new Callback<SendImageDetails>() {
            @Override
            public void onResponse(Call<SendImageDetails> call, Response<SendImageDetails> response) {
                Log.d("pranav", response.body().toString());

                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Common_Utils.hideProgress();
                    }
                });

                String img = response.body().getData().getImage_url();
                Log.d("pranav", "image upload got url " + img);
                String desc = description.getText().toString();
                if(description.getText().toString().equals("")){
                    desc = "Patient Image";
                }
                ((CameraParent)parent).sendImageChat(
                        img,
                        "Dermoscope",
                        desc
                );


            }

            @Override
            public void onFailure(Call<SendImageDetails> call, Throwable t) {
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Common_Utils.hideProgress();
                    }
                });
                Log.d("pranav", t.toString());
                Common_Utils.showToast("Unable to send images, please try again");
            }
        });
    }

}
