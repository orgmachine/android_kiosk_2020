package com.ehealthkiosk.kiosk.ui.fragments.tests;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.MessageEvent;
import com.ehealthkiosk.kiosk.model.profilelist.ProfilesItem;
import com.ehealthkiosk.kiosk.ui.fragments.BaseDeviceFragment;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.ehealthkiosk.kiosk.utils.weight.VScaleGattAttributes;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bodivis.scalelib.BleManager;
import cn.com.bodivis.scalelib.bean.EvaluationResultBean;
import cn.com.bodivis.scalelib.bean.UserBean;
import cn.com.bodivis.scalelib.callback.BleGattCallback;
import cn.com.bodivis.scalelib.callback.BleMtuChangedCallback;
import cn.com.bodivis.scalelib.callback.BleNotifyCallback;
import cn.com.bodivis.scalelib.callback.BleReadCallback;
import cn.com.bodivis.scalelib.callback.BleScanCallback;
import cn.com.bodivis.scalelib.callback.BleWriteCallback;
import cn.com.bodivis.scalelib.data.BleDevice;
import cn.com.bodivis.scalelib.exception.BleException;
import cn.com.bodivis.scalelib.scale.M1ScalesUtil;
import cn.com.bodivis.scalelib.scale.N1ScalesUtil;
import cn.com.bodivis.scalelib.scale.ScalesUtils;
import si.inova.neatle.Neatle;

import static cn.com.bodivis.scalelib.utils.HexUtil.charToByte;

public class WeightTestFragment extends BaseDeviceFragment {

    private static final String tag = "WeightTestFragment";
    boolean running = true;

    @BindView(R.id.img_instruction)
    ImageView imgInstruction;
    @BindView(R.id.tv_instruction)
    TextView tvInstruction;

    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_manual)
    Button btnManual;
    @BindView(R.id.btn_skip)
    Button btnSkip;
    @BindView(R.id.tv_reading)
    TextView tvReading;

    @BindView(R.id.layout_setup_weight)
    LinearLayout layoutSetupWeight;

    @BindView(R.id.layout_instruction)
    LinearLayout layoutInstruction;

    @BindView(R.id.layout_btn_weight)
    LinearLayout layoutBtnWeight;

    @BindView(R.id.layout_reading_weight)
    LinearLayout layoutReadingWeight;

    @BindView(R.id.layout_measuring_weight)
    LinearLayout layoutMeasuringWeight;
    @BindView(R.id.btn_continue)
    Button btnContinue;
    @BindView(R.id.btn_manual1)
    Button btnManual1;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.btn_cancel)
    Button btnCancel;

    private String age = "25";

    private UserBean mUser = new UserBean();

    private boolean isScaned = false;
    private BleDevice mBleDevice;
    private EvaluationResultBean evaluationResultBean = new EvaluationResultBean();

    private StringBuilder strBuilder = new StringBuilder();
    private int nextData;//是否有后续包
    private int isTestSuccess;//测试是否成功
    private int packageOrder;//包顺序
    private int infoNumber;//每包数据个数
    private int message;

    boolean isDeviceConnected = false;
    boolean isRetry = false;
    boolean isInitiated = false;

    ProfilesItem profilesItem;
    String heightFt, heightInch;
    double height;


    private static DecimalFormat TWO_DECIMAL_PLACES = new DecimalFormat(".##");

    @Override
    protected int getLayout() {
        return R.layout.fragment_test_weight;
    }

    private void init() {

        showInstruction();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
                showInstruction();


            }
        });
        if(getActivity() != null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            Bundle bundle = new Bundle();
            bundle.putString("name", "weight");
            firebaseAnalytics.logEvent( "weight_fragment_opened", bundle);
        }
        //initBle();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initBle() {
        profilesItem = SharedPrefUtils.getProfile(HealthKioskApp.getAppContext());

        if (profilesItem != null) {
            if (profilesItem.getGender().equalsIgnoreCase("male")) {
                mUser.setSex(0);
            } else if (profilesItem.getGender().equalsIgnoreCase("female")) {
                mUser.setSex(1);
            }
            mUser.setAge(profilesItem.getAge());


        } else {
            mUser.setAge(Integer.parseInt(age));
            mUser.setSex(0);
        }


        heightFt = SharedPrefUtils.getHeightFT(mActivity);
        heightInch = SharedPrefUtils.getHeightInch(mActivity);
        height = Common_Utils.convertFeetandInchesToCentimeter(heightFt, heightInch);
        mUser.setHeight((int) Math.round(height));
        isInitiated = true;
        BleManager.getInstance().init(mActivity.getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setMaxConnectCount(4)
                .setOperateTimeout(20000);
        sacnBle();

    }

    /**
     * 搜索设备
     */
    private void sacnBle() {
        showMeasuring();
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean b) {
                //开始搜索
                System.out.println("onScanStarted");
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Log.d("TAG", bleDevice.getMac());
                System.out.println(bleDevice.getName() + "     name");
                if (TextUtils.isEmpty(bleDevice.getName())) {
                    return;
                }
                /**
                 * 大白设备名：VScale
                 * 小白设备名：BodyMini1
                 */

                if (!isScaned && ((bleDevice.getMac().equals(DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.WEIGHT))))) {

                    isScaned = true;
                    BleManager.getInstance().cancelScan();
                    connectBle(bleDevice);
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                // 扫描结束，列出所有扫描到的符合扫描规则的BLE设备，可能为空（UI线程）

                if (scanResultList.isEmpty()) {
                    System.out.println("onScanFinished" + scanResultList.toString());
                    Toast.makeText(mActivity, getResources().getString(R.string.bluetooth_scan_check_toast),
                            Toast.LENGTH_SHORT).show();
                    if(running) {
                        showInstruction();
                    }
                    isDeviceConnected = false;
                }
            }
        });

    }

    private void connectBle(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {

                System.out.println("onStartConnect");
            }

            @Override
            public void onConnectFail(BleException e) {
                if(!running){
                    return;
                }

                System.out.println("onConnectFail   " + e.toString());
                isDeviceConnected = false;
                showInstruction();
                Toast.makeText(mActivity, getResources().getString(R.string.bluetooth_scan_fail_toast), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
                if(!running){
                    return;
                }

                System.out.println("onConnectSuccess,bleDeviceName = " + bleDevice.getName());
                BleManager.getInstance().cancelScan();

                mBleDevice = bleDevice;
                isDeviceConnected = true;
                operateBle();


            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
                // 连接中断，isActiveDisConnected表示是否是主动调用了断开连接方法（UI线程）
                if(!running){
                    return;
                }
                System.out.println("onDisConnected");
                //TODO 断开后考虑重连
                if (!isActiveDisConnected) {
                    BleManager.getInstance().convertBleDevice(bleDevice.getDevice());
                }
            }
        });
    }

    /**
     * 操作蓝牙
     */
    private void operateBle() {
        if (mBleDevice == null) {
            Log.d(tag, "mBleDevice == null ");
            measureFailure("mBleDevice null");
            return;
        }
        if (VScaleGattAttributes.M1_NAME.equals(mBleDevice.getName())) {
            notifyM1();
        } else {
            BleManager.getInstance().setMtu(mBleDevice, 512, new BleMtuChangedCallback() {
                @Override
                public void onSetMTUFailure(BleException exception) {
                    // 设置MTU失败（UI线程）
                    notifyN1();
                    System.out.println(exception.getDescription());
                }

                @Override
                public void onMtuChanged(int mtu) {
                    // 设置MTU成功，并获得当前设备传输支持的MTU值（UI线程）
                    System.out.println("onMtuChanged " + mtu);
                    notifyN1();

                }
            });
        }
    }

    /**
     * notifyM1大白(●—●)
     */

    private void readBatteryLevel() {
        Log.d("TAG", "Read battery level");
        final UUID batteryService = Neatle.createUUID(0x180F);
        final UUID batteryCharacteristic = Neatle.createUUID(0x2A19);
        BleManager.getInstance().read(mBleDevice, batteryService.toString(), batteryCharacteristic.toString(), new BleReadCallback() {
            @Override
            public void onReadSuccess(byte[] bytes) {
                if(!running){
                    return;
                }
                Toast.makeText(mActivity, getResources().getString(R.string.read_op_success) + bytes.toString(),
                        Toast.LENGTH_LONG).show();
                Log.d("TAG", "bytes" + bytes);
//                int battery =
            }

            @Override
            public void onReadFailure(BleException e) {
                if(!running){
                    return;
                }
                Toast.makeText(mActivity, e.toString() + getResources().getString(R.string.read_op_fail),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void notifyM1() {
        BleManager.getInstance().notify(mBleDevice,
                VScaleGattAttributes.BLE_SCALE_SERVICE_UUID,
                VScaleGattAttributes.BLE_SCALE_TEST_RESULT_CHARACTERISTIC_UUID,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        System.out.println("onNotifySuccess");
                        readBatteryLevel();
                        Log.d("TAG", "Device notified");

                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        measureFailure(exception.getDescription());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 读特征值数据获取成功（UI线程）
                        System.out.println("onCharacteristicChanged");
                        try {
                            //解析大白数据,测试成功
                            EvaluationResultBean resultBean = parseOldScaleData(data);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            measureFailure(ex.getLocalizedMessage());
                        }
                    }
                });

    }

    /**
     * 接收解析大白数据
     *
     * @param srcData
     */
    public EvaluationResultBean parseOldScaleData(byte[] srcData) {

        if (srcData != null) {
            if (srcData.length > 4 && srcData[2] == 0 && srcData[3] == 0) {// 体重数据
                float weight = new ScalesUtils(new M1ScalesUtil()).parseWeight(srcData);
                if (weight == 0) {
                    Log.d(tag, "weight == 0");
                    return null;
                }
                //获取到体重，给设备发送测试者信息
                writeValueToOldScale(mUser);
            } else {// 详细信息
                Log.d(tag, Arrays.toString(srcData));
                EvaluationResultBean resultBean = new ScalesUtils(new M1ScalesUtil()).getResultData(mUser, srcData, evaluationResultBean);
                if (resultBean != null) {
                    //测试成功处理
                    measureSuccess(resultBean);
                    return resultBean;
                } else {
                    measureFailure("Test failed");
                }
            }
        }
        return null;
    }

    /**
     * 大白写入测试者信息数据
     *
     * @param mUser
     */
    private void writeValueToOldScale(UserBean mUser) {
        Log.d(tag, "age " + mUser.getAge() + "height" + mUser.getHeight() + "sex" + mUser.getSex());
        byte[] data = new ScalesUtils(new M1ScalesUtil()).packageDownData(mUser);

        BleManager.getInstance().write(mBleDevice,
                VScaleGattAttributes.BLE_SCALE_SERVICE_UUID,
                VScaleGattAttributes.BLE_SCALE_SET_USER_CHARACTERISTIC_UUID,
                data, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int i, int i1, byte[] bytes) {
                        Log.d(tag, "onWriteSuccess  ");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.d(tag, "onWriteFailure  " + exception.getDescription());
                        measureFailure(exception.getDescription());
                    }
                });
    }

    /**
     * notify小白
     */
    private void notifyN1() {
        BleManager.getInstance().notify(mBleDevice,
                VScaleGattAttributes.BLE_MINI_SCALE_SERVICE_UUID,
                VScaleGattAttributes.BLE_MINI_SCALE_TEST_RESULT_CHARACTERISTIC_UUID,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Log.d(tag, "onNotifySuccess mUser ");
                        Log.d(tag, "age " + mUser.getAge() + "height" + mUser.getHeight() + "sex" + mUser.getSex());
                        byte[] byteArr = new ScalesUtils(new N1ScalesUtil()).packageDownData(mUser);
                        writeValueToMini1Scale(byteArr);
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Log.e(tag, exception.getDescription());
                        measureFailure("\n" +
                                getResources().getString(R.string.write_fail));
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.d(tag, "Receiving mini data");
//
                        parseMINI2Data(data);
                    }
                });
    }

    /**
     * 解析小白
     *
     * @param srcData
     */
    public void parseMINI2Data(byte[] srcData) {
        Log.d(tag, "Receive MINI2 data srcData" + "length" + srcData.length);
        if (srcData != null) {
            // 包序0x01·0x0f
            packageOrder = srcData[1];
            // 体成分个数
            infoNumber = srcData[2];
            // 信息
            message = srcData[3];
            //测试是否成功1失败；0成功
            isTestSuccess = message & 0x01;
            //是否有后续包1有；0没有
            nextData = (message & 0x02) >> 1;
            //cs=1测试失败
            if (isTestSuccess == 1) {
                measureFailure("小白硬件返回测试失败");

                Log.d(tag, "测试失败");
                return;
            } else {
                if (nextData == 1) {
                    String a = bytesToHexString(srcData);
                    String iteminfo = a.substring(8, a.length() - 8);
                    strBuilder.append(iteminfo);
                    byte[] byteArr = new ScalesUtils(new N1ScalesUtil()).conversionCodeData(0x8F);
                    //数据一包接收不完，需要告诉硬件继续发数据
                    writeValueToMini1Scale(byteArr);
                } else if (nextData == 0) {
                    // 没有后续数据，一包可以接收完
                    if (infoNumber == 29 && packageOrder == 1) {
                        showDataToUi(srcData);
                    } else {
                        //有后续的情况，数据拼接
                        byte[] srcAll = new byte[4];
                        byte[] srcAll2 = new byte[4];
                        srcAll[0] = Integer.valueOf(0xBC).byteValue();
                        srcAll[1] = Integer.valueOf(0x01).byteValue();
                        srcAll[2] = Integer.valueOf(0x1D).byteValue();
                        srcAll[3] = Integer.valueOf(0x04).byteValue();
                        srcAll2[0] = Integer.valueOf(0xD4).byteValue();
                        srcAll2[1] = Integer.valueOf(0xC6).byteValue();
                        srcAll2[2] = Integer.valueOf(0xC8).byteValue();
                        srcAll2[3] = Integer.valueOf(0xD4).byteValue();
                        String a = bytesToHexString(srcData);
                        String iteminfo = a.substring(8, a.length() - 8);
                        strBuilder.append(iteminfo);
                        strBuilder.append(bytesToHexString(srcAll2));

                        showDataToUi(hexStringToBytes(bytesToHexString(srcAll) + strBuilder.toString()));
                    }
                }
            }
        }
    }

    /**
     * 小白数据展示
     *
     * @param srcData
     */
    public void showDataToUi(byte[] srcData) {
        //decodeUpdata方法是解析小白数据获取小白数据实体类
        EvaluationResultBean resultBean = new ScalesUtils(new N1ScalesUtil()).getResultData(mUser, srcData, evaluationResultBean);
        if (resultBean != null) {
            //展示数据
            Log.d(tag, "showDataToUiFailed");
            measureSuccess(resultBean);
        } else {
            Log.d(tag, "showDataToUiFailed");
            measureFailure("小白解析数据有误");
        }
    }

    /**
     * 给小白写入测试者信息
     *
     * @param userInfo
     */
    private void writeValueToMini1Scale(byte[] userInfo) {

        BleManager.getInstance().write(mBleDevice,
                VScaleGattAttributes.BLE_MINI_SCALE_SERVICE_UUID,
                VScaleGattAttributes.BLE_MINI_SCALE_SET_USER_CHARACTERISTIC_UUID,
                userInfo, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int i, int i1, byte[] bytes) {
                        Log.d(tag, "onWriteSuccess");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.d(tag, "onWriteFailure  " + exception.getDescription());
                        measureFailure("写入测试者信息失败");
                    }
                });
    }


    /**
     * 测试失败处理
     */
    private void measureFailure(String failureMassage) {
        if(!running){
            return;
        }
        Toast.makeText(mActivity, failureMassage, Toast.LENGTH_SHORT).show();
        try {
            showInstruction();
        }catch(Exception e){

        }
        disconnect();
        isDeviceConnected = false;
    }

    /**
     * 测量成功处理
     */
    public void measureSuccess(EvaluationResultBean resultBean) {
        disconnect();
        isDeviceConnected = false;
        Log.d(tag, resultBean.toString());

        mActivity.setSourceMap(Constants.WEIGHT, Constants.READING_DEVICE);
        showReading(String.valueOf(resultBean.getWeight()) + " Kg");
//        tvReading.setText();

        SharedPrefUtils.setWeight(mActivity, String.valueOf(resultBean.getWeight()));

        SharedPrefUtils.setBMI(mActivity, String.valueOf(resultBean.getBMI()));
        SharedPrefUtils.setHydration(mActivity, String.valueOf(resultBean.getWaterPercentage() * 100));
        SharedPrefUtils.setFat(mActivity, String.valueOf(resultBean.getFatPercentage() * 100));

        float obesity = resultBean.getObesity();
        SharedPrefUtils.setObesity(getActivity(), String.valueOf(obesity));
        float protien = resultBean.getProteinWeight();
        SharedPrefUtils.setProtein(getActivity(), String.valueOf(protien));
        float vfi = resultBean.getVisceralFatPercentage();
        SharedPrefUtils.setVFI(getActivity(), String.valueOf(vfi));
        float bmr = resultBean.getBmr();
        SharedPrefUtils.setBMR(getActivity(), String.valueOf(bmr));
        float bodyAge = resultBean.getBodyAge();
        SharedPrefUtils.setBodyAge(getActivity(), String.valueOf(bodyAge));

        float bonemass = (resultBean.getBoneWeight() / resultBean.getWeight()) * 100;
        SharedPrefUtils.setBonemass(mActivity, String.valueOf(bonemass));
        float muscle = (resultBean.getMuscleWeight() / resultBean.getWeight()) * 100;
        SharedPrefUtils.setMuscle(mActivity, String.valueOf(muscle));

    }

    /**
     * 断开蓝牙
     */
    private void disconnect() {
        if (mBleDevice != null) {
            BleManager.getInstance().clearCharacterCallback(mBleDevice);
            BleManager.getInstance().disconnectAllDevice();
        }
    }

//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.disconnect_btn) {
//            disconnect();
//        } else if (id == R.id.reconnect_btn) {
//            //重新连接
//            isScaned = false;
//            sacnBle();
//        }
//    }

    @Override
    public void onDestroy() {
        disconnect();
        Log.d("TAG", "weight destroy called");
        super.onDestroy();

    }


    @Override
    public void onStop() {
        super.onStop();
        running = false;
        disconnect();
        Log.d("TAG", "weight stop called");
    }

    /**
     * 小白会用
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     * 小白会用
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    void showReading(String reading) {

        layoutSetupWeight.setVisibility(View.INVISIBLE);
        layoutBtnWeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringWeight.setVisibility(View.INVISIBLE);
        layoutReadingWeight.setVisibility(View.VISIBLE);

        tvReading.setText(reading);
    }

    void showMeasuring() {

        layoutSetupWeight.setVisibility(View.INVISIBLE);
        layoutBtnWeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringWeight.setVisibility(View.VISIBLE);
        layoutReadingWeight.setVisibility(View.INVISIBLE);

    }

    void showInstruction() {
        layoutSetupWeight.setVisibility(View.INVISIBLE);
        layoutBtnWeight.setVisibility(View.VISIBLE);
        layoutInstruction.setVisibility(View.VISIBLE);
        layoutMeasuringWeight.setVisibility(View.INVISIBLE);
        layoutReadingWeight.setVisibility(View.INVISIBLE);
    }

    void showSetup() {
        layoutSetupWeight.setVisibility(View.VISIBLE);
        layoutBtnWeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringWeight.setVisibility(View.INVISIBLE);
        layoutReadingWeight.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("TAG", DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.WEIGHT));

        init();
        if (SharedPrefUtils.getWeight(getActivity()) != null
                && !SharedPrefUtils.getWeight(getActivity()).equals("")) {
            showReading( SharedPrefUtils.getWeight(getActivity()) );
        }
    }


    @OnClick({R.id.btn_start, R.id.btn_manual, R.id.btn_skip, R.id.btn_continue, R.id.btn_manual1, R.id.btn_retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if (SharedPrefUtils.getHeightFT(mActivity) != null && !SharedPrefUtils.getHeightFT(mActivity).equals("")
                        && SharedPrefUtils.getHeightInch(mActivity) != null && !SharedPrefUtils.getHeightInch(mActivity).equals("")) {
                    Log.d("TAG", "button clicked");
                    Common_Utils.enableBluetooth();

                    isScaned = false;
                    disconnect();
                    if (isInitiated) {
                        sacnBle();
                    } else {
                        initBle();
                    }
                } else {
                    showInstruction();
                    Toast.makeText(getActivity(), getResources().getString(R.string.height_test_validation),
                            Toast.LENGTH_SHORT).show();
                }
                //operateBle();
//                if (isDeviceConnected){
//                    operateBle();
//                }else{
//                    isRetry = true;
//                    if (isInitiated){
//                        sacnBle();
//                    }else{
//                        initBle();
//                    }
//                }



                break;
            case R.id.btn_manual1:
            case R.id.btn_manual:
                openManualDialog(mActivity);
                break;
            case R.id.btn_skip:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 2));
                break;
            case R.id.btn_continue:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 2));
                break;
            case R.id.btn_retry:
//                isRetry = true;
//                if (isDeviceConnected){
//                    operateBle();
//                }else{
//                    sacnBle();
//                }
                disconnect();
                showInstruction();
                break;

        }
    }

    void openManualDialog(final Context mContext) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = mActivity.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_manual_weight, viewGroup, false);
        Button btnEnter = dialogView.findViewById(R.id.btn_enter);
        final EditText etWeight = dialogView.findViewById(R.id.et_kg);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(etWeight.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_weight_toast), Toast.LENGTH_SHORT).show();
                    return;
                }

                double weight = Double.parseDouble(etWeight.getText().toString());

                if (weight <= 0) {
                    Toast.makeText(mContext, getResources().getString(R.string.invalid_weight_toast), Toast.LENGTH_SHORT).show();
                    return;
                }


                final String weightStr = etWeight.getText().toString();
                SharedPrefUtils.setWeight(mActivity, TWO_DECIMAL_PLACES.format(weight));

                alertDialog.dismiss();


                mActivity.setSourceMap(Constants.WEIGHT, Constants.READING_MANUAL);
                showReading(TWO_DECIMAL_PLACES.format(weight) + " Kg");


            }
        });
    }


}
