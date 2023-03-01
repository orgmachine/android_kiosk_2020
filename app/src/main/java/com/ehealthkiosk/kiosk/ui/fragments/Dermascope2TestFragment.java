package com.ehealthkiosk.kiosk.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageDetails;
import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageMultipleData;
import com.ehealthkiosk.kiosk.model.sendDermaImage.sendImageResultData;
import com.ehealthkiosk.kiosk.ui.SubmitImagePresenter;
import com.ehealthkiosk.kiosk.ui.SubmitImagePresenterImpl;
import com.ehealthkiosk.kiosk.ui.SubmitImageView;
import com.ehealthkiosk.kiosk.ui.activities.MainActivity;
import com.ehealthkiosk.kiosk.ui.activities.PDFViewActivity;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.jiangdg.usbcamera.utils.FileUtils;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dermascope2TestFragment extends Fragment implements SubmitImageView, CameraDialog.CameraDialogParent, CameraViewInterface.Callback, TextureView.SurfaceTextureListener
{


    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
    int profile_id;

    private static final int REQUEST_CODE = 1;
    private List<String> mMissPermissions = new ArrayList<>();
    private List<String> filePaths = new ArrayList<>();

    private Activity parent;
    ImagesAdapter adapter;

    SubmitImagePresenter submitImagePresenter;

    Button captureImage, generateReport, refresh;
    File imageFile;
    String filePath;
    EditText description;
    GridView grid;

    SendImageMultipleData sendImageData;
    sendImageResultData sendImageResultData;

    ProgressDialog progressDialog;

    // Camera
    public View mTextureView;

    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;

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


    public static Dermascope2TestFragment newInstance(String param1, String param2) {
        Dermascope2TestFragment fragment = new Dermascope2TestFragment();

        return fragment;
    }


    public void showList(){
        adapter = new ImagesAdapter(getActivity(), filePaths);
        grid.setAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dermascope_test, container, false);


        Bundle bundle = getArguments();
        reportType = bundle.getString(Constants.REPORT_TYPE);


        captureImage = rootView.findViewById(R.id.btn_capture_image);
        description = rootView.findViewById(R.id.description);

        generateReport = rootView.findViewById(R.id.btn_generate_report);
        grid = rootView.findViewById(R.id.grid);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        submitImagePresenter = new SubmitImagePresenterImpl(this);
        captureImage.setClickable(false);
        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filePaths.size() >= 4 ){
                    Common_Utils.showToast("You can only add 4 images");
                    return;
                }
                Bitmap bm = ((TextureView) mTextureView).getBitmap();
                String path = Common_Utils.saveImage(bm);

                Log.d("usbcam", path);
                filePaths.add(path);
                showList();
            }
        });

        mTextureView = rootView.findViewById(R.id.camera_view);
//        mTextureView.setSurfaceTextureListener(this);
        parent = getActivity();

        ((MainActivity)parent).hideSteps();

        generateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages();
            }
        });



        profile_id = Integer.valueOf(SharedPrefUtils.getProfileId(parent));

        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
        mUVCCameraView = (CameraViewInterface) mTextureView; mUVCCameraView.setCallback(this);
        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.initUSBMonitor(parent, mUVCCameraView, listener);

        showList();
        return rootView;
    }

    private void uploadImages() {
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Common_Utils.showProgress(parent);
            }
        });
        HashMap<String, RequestBody> comment = new HashMap<>();
        comment.put("profile_id", RestClient.createPartFromString(String.valueOf(profile_id)));
        comment.put("description", RestClient.createPartFromString(description.getText().toString()));
        comment.put("report_type", RestClient.createPartFromString(reportType));

        List<MultipartBody.Part> bodies = new ArrayList<>();
        for(String path: filePaths){
            bodies.add(RestClient.preparePNGPart("images", new File(path)));
        }

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

                Intent i = new Intent(getActivity(), PDFViewActivity.class);
                i.putExtra(Constants.PDF_PATH, response.body().getData().getPdfLink().split("\\?")[0]);
                i.putExtra(Constants.REPORT_DATA, response.body().getData().getReportId());
                i.putExtra(PDFViewActivity.PDF_TYPE, " Report");
                startActivity(i);
                parent.finish();

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

    @Override
    public void showProgress() {

    }

    @Override
    public void showSuccess(SendImageDetails sendImageResponse) {
        if(sendImageResponse != null && !sendImageResponse.equals("")) {
            progressDialog.dismiss();
            sendImageResultData = sendImageResponse.getData();

            if (sendImageResultData != null && !sendImageData.equals("")) {
                Intent i = new Intent(getActivity(), PDFViewActivity.class);
                i.putExtra(Constants.PDF_PATH, sendImageResultData.getPdfLink().split("\\?")[0]);
                i.putExtra(Constants.REPORT_DATA, sendImageResultData.getReportId());
                startActivity(i);
                getActivity().finish();
                captureImage.setVisibility(View.VISIBLE);
            } else {

//                    Toast.makeText(getActivity(), sendImageResponse.getStatus().getMessage(), Toast.LENGTH_SHORT).show();

            }

        }else{
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "empty response - " + sendImageResponse.getStatus().getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void showError(String msg) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), "API Error - " + msg, Toast.LENGTH_LONG).show();


    }

    public static final Dermascope2TestFragment newInstance()
    {
        Dermascope2TestFragment dermascopeTestFragment = new Dermascope2TestFragment();
        return dermascopeTestFragment;
    }


    private void setupCamera() {
        mUVCCameraView = (CameraViewInterface) mTextureView;
        mUVCCameraView.setCallback(Dermascope2TestFragment.this);
        mCameraHelper.initUSBMonitor(parent, mUVCCameraView, listener);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("usbcam", "On Start called for the camera");
        if (mCameraHelper != null) {

            Log.d("usbcam", "On Start called for the camera");
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

    public void showDialog(Uri imageUri){
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);




        //Yes Button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    public void deleteImage(int i){
        filePaths.remove(i);
        showList();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //setupCamera();
        Log.d("pranav", "Surface Texture available");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        Log.d("pranav", "Surface Texture change");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        Log.d("pranav", "Surface Texture updated");
    }

    class ImagesAdapter extends BaseAdapter {

        private final Context mContext;
        private List<String> images;

        public ImagesAdapter(Context context, List<String> images) {
            this.mContext = context;
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String path = images.get(position);

            File file = new File(path);
            Uri imageUri = Uri.fromFile(file);

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.item_image, null);
            }

            RelativeLayout delete = convertView.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteImage(position);
                }
            });

            ImageView image = convertView.findViewById(R.id.image);
            Glide.with(mContext)
                    .load(imageUri)
                    .into(image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(imageUri);
                }
            });

            return convertView;
        }



    }

}
