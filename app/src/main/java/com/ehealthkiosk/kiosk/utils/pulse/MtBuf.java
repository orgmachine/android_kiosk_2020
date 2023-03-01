package com.ehealthkiosk.kiosk.utils.pulse;

import android.util.Log;

import com.contec.cms50dj_jar.DeviceCommand;
import com.contec.cms50dj_jar.DeviceData50DJ_Jar;
import com.contec.cms50dj_jar.DeviceDataPedometerJar;
import com.contec.cms50dj_jar.DevicePackManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

public class MtBuf {

    private static final String TAG = "Mtbuf";
    public String pulse = null;
    public String spo2 = null;
    private static Vector<Integer> m_buf = null;
    private DevicePackManager mDevicePackManager = new DevicePackManager();
    public MtBuf() {
        m_buf = new Vector<Integer>();
    }

    public synchronized void write(byte[] buf, int count, final OutputStream pOutputStream) throws IOException {
        int receiveNum = mDevicePackManager.arrangeMessage(buf, count);
        switch (receiveNum) {
            case 1:// 得到设备号,发送校时命令 get device number successful
                delay(200);
                pOutputStream.write(DeviceCommand.correctionDateTime());
                Log.e(TAG, "得到设备号 发送校时命令");
                break;
            case 2:// 对时成功, 设置计步器 set time successful, send command to set parameter
                delay(200);
                pOutputStream.write(DeviceCommand.setPedometerInfo(
                        "175", "75", 0, 24, 10000,
                        1, 1));
                Log.e(TAG, "对时成功  设置计步器信息");
                break;
            case 3:// 对时失败 关闭socket set time failed
                Log.e(TAG, "对时失败");
                break;
            case 4:// 无新数据; device has no new spo2 data
                delay(200);
                pOutputStream.write(DeviceCommand.dayPedometerDataCommand());
                Log.e(TAG, "血氧无新数据，请求以天为单位的计步器数据");
                break;
            case 5: // 整个血氧数据接收完成, all spo2 date were received
                delay(200);

                DeviceData50DJ_Jar deviceData50D = mDevicePackManager.getDeviceData50dj();
                byte[] spo2Data = deviceData50D.getmSp02DataList().get(deviceData50D.getmSp02DataList().size() - 1);

                    // 血氧数据
                    String string = "year:" + spo2Data[0] + " month:" + spo2Data[1] + "  day:"
                            + spo2Data[2] + " hour:" + spo2Data[3] + "  min:" + spo2Data[4]
                            + "  second:" + spo2Data[5] + "  spo2:" + spo2Data[6]
                            + "  pluse:" + spo2Data[7];
                pulse = String.valueOf(spo2Data[7]);
                spo2 = String.valueOf(spo2Data[6]);
                    Log.e(TAG, string);


                Log.e(TAG, " 整个血氧数据接收完成，发送以天为单位请求数据命令");
                pOutputStream.write(DeviceCommand.dayPedometerDataCommand());
                break;
            case 6:// 一包数据接收完毕,请求下一包数据 one package spo2 data received, request next package
                delay(200);
                Log.e(TAG, "一包血氧数据接收完毕, 请求下一包数据");
                pOutputStream.write(DeviceCommand.dataUploadSuccessCommand());
                break;
            case 7:// 接收血氧数据失败 get spo2 data failed
                delay(200);
                Log.e(TAG, "血氧数据接收失败，请求以天为单位的计步器数据");
                pOutputStream.write(DeviceCommand.dayPedometerDataCommand());
                break;
            case 8:// 8:设置计步器成功 set parameter failed, send command to get spo2 data
                delay(200);
                pOutputStream.write(DeviceCommand.getDataFromDevice());
                Log.e(TAG, "设置计步器成功，发送请求血氧数据命令");
                break;
            case 9:// 9: 设置计步器失败 set parameter failed
                delay(200);
                pOutputStream.write(DeviceCommand.getDataFromDevice());
                Log.e(TAG, "设置计步器失败，发送请求血氧数据命令 ");
                break;
            case 10:// 以天为单位计步器数据一包上传完成, all step data in days were received
                delay(200);
                pOutputStream.write(DeviceCommand.dayPedometerDataSuccessCommand());
                Log.e(TAG, "以天为单位计步器数据上传完成，发送上传完成命令");
                break;
            case 11:// 以天为单位计步器 数据上一包上传成功 请求下一包数据

                delay(200);
                pOutputStream.write(DeviceCommand.dayPedometerDataCommand());
                Log.e(TAG, "以天为单位计步器数据上一包上传成功，请求下一包数据 ");

                break;
            case 12:// 以天为单位计步器数据全部上传成功，请求以分为单位的数据

                delay(200);
                pOutputStream.write(DeviceCommand.minPedometerDataCommand());
                Log.e(TAG, "以天为单位计步器数据全部上传成功，请求以分为单位的计步器数据");

                break;
            case 13:// 以天为单位计步器数据上传失败,请求以分为单位的数据

                delay(200);
                pOutputStream.write(DeviceCommand.minPedometerDataCommand());
                Log.e(TAG, " 以天为单位计步器数据上传失败，请求以分为单位的计步器数据");

                break;
            case 14:// 以分为单位计步器数据上传完成，发送上传完成命令
                delay(200);
                pOutputStream.write(DeviceCommand.minPedometerDataSuccessCommand());
                Log.e(TAG, "以分为单位计步器数据上传完成，发送上传完成命令");

                break;
            case 15:// 以分为单位计步器数据一包上传完成

                delay(200);
                pOutputStream.write(DeviceCommand.minPedometerDataCommand());
                Log.e(TAG, "以分为单位计步器数据一包上传完成，发送请求下一包的命令");

                break;
            case 16: // all step data in minutes were received,close socket
                // 以分为单位计步器数据全部上传成功，关闭socket
                Log.e(TAG, " 以分为单位计步器数据全部上传成功，关闭socket ");

                //打印数据值
                DeviceDataPedometerJar dataPedometerJar = mDevicePackManager.getM_DeviceDataPedometers();
                for (int i = 0; i < dataPedometerJar.getmPedometerDataDayList().size(); i++) {
                    byte[] dayData = dataPedometerJar.getmPedometerDataDayList().get(i);

                    String string2 = "year:" + dayData[0] + " month:" + dayData[1] + "  day:"
                            + dayData[2] + " steps:"
                            + ((dayData[4] & 0xff) | ((dayData[3] & 0xff) << 8))
                            + "  cal_target:"
                            + ((dayData[6] & 0xff) | ((dayData[5] & 0xff) << 8))
                            + "  cal:"
                            + ((dayData[8] & 0xff) | ((dayData[7] & 0xff) << 8));

                    Log.e(TAG, string2);
                }

                break;
            case 17:// 以天为单位 计步器无新数据, device has no step in days
                delay(200);
                Log.e(TAG, " 以天为单位计步器无新数据，请求以分为单位的数据");
                pOutputStream.write(DeviceCommand.minPedometerDataCommand());
                break;
            case 18:// 以分为单位 计步器无新数据, device has no step data in minutes
                Log.e(TAG, " 以分为单位计步器无新数据");
                break;
            case 19:// 以分为单位 计步器数据上传失败
                Log.e(TAG, " 以分为单位计步器数据上传失败");
                break;
            case 20:// 请求下一包血氧数据
                delay(200);
                Log.e(TAG, " 请求下一包血氧数据");
                pOutputStream.write(DeviceCommand.getDataFromDevice());
                break;
        }
    }

    private void delay(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
