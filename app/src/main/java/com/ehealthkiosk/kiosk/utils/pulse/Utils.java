/*
 * Copyright (C) 2016 The Android Project XieZhigang(xzg4080813@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ehealthkiosk.kiosk.utils.pulse;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 判断俩个byte数组是否相等
 * 
 * @author XieZhigang(xzg4080813@163.com) tools 工具类
 * 
 */
public class Utils {
	
	static public boolean byteArrayEqual(byte[] base, byte[] b) {
		if (base.length > b.length) {
			return false;
		}
		for (int i = 0; i < base.length; i++) {
			if (base[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * format byte array to string for display not convert byte array to string
	 * usually use to show debug or error message
	 * 
	 * 将 byte 数组格式化成字符串，一般用于调试
	 * 
	 */
	static public String bytesToHexString(byte[] src) {
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

	static public String bytesToHexString(byte mbyte) {

		byte[] src = new byte[] { mbyte };

		StringBuilder stringBuilder = new StringBuilder("0x");
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

	static public int byteToInt(byte mbyte) {
		return (int) (mbyte & 0xff);
	}

	/**
	 * 
	 * convert from dip to pixel 单位装换：dip -> pixel
	 * 
	 */
	static public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 
	 * convert from pixel to dip 单位装换：pixel -> dip
	 * 
	 */
	static public int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将short转成byte[2]
	 * 
	 * @param a
	 * @return
	 */
	public static byte[] short2Byte(short a) {
		byte[] b = new byte[2];

		b[0] = (byte) (a >> 8);
		b[1] = (byte) (a);

		return b;
	}

	/**
	 * 将short转成byte[2]
	 * 
	 * @param a
	 * @param b
	 * @param offset
	 *            b中的偏移量
	 */
	public static void short2Byte(short a, byte[] b, int offset) {
		b[offset] = (byte) (a >> 8);
		b[offset + 1] = (byte) (a);
	}

	/**
	 * 将byte[2]转换成short
	 * 
	 * @param b
	 * @return
	 */
	public static short byte2Short(byte[] b) {
		return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));
	}

	/**
	 * 将byte[2]转换成short
	 * 
	 * @param b
	 * @param offset
	 * @return
	 */
	public static short byte2Short(byte[] b, int offset) {
		return (short) (((b[offset] & 0xff) << 8) | (b[offset + 1] & 0xff));
	}

	/**
	 * long转byte[8]
	 * 
	 * @param a
	 * @param b
	 * @param offset
	 *            b的偏移量
	 */
	public static void long2Byte(long a, byte[] b, int offset) {
		b[offset + 0] = (byte) (a >> 56);
		b[offset + 1] = (byte) (a >> 48);
		b[offset + 2] = (byte) (a >> 40);
		b[offset + 3] = (byte) (a >> 32);

		b[offset + 4] = (byte) (a >> 24);
		b[offset + 5] = (byte) (a >> 16);
		b[offset + 6] = (byte) (a >> 8);
		b[offset + 7] = (byte) (a);
	}

	/**
	 * byte[8]转long
	 * 
	 * @param b
	 * @param offset
	 *            b的偏移量
	 * @return
	 */
	public static long byte2Long(byte[] b, int offset) {
		return ((((long) b[offset + 0] & 0xff) << 56)
				| (((long) b[offset + 1] & 0xff) << 48)
				| (((long) b[offset + 2] & 0xff) << 40)
				| (((long) b[offset + 3] & 0xff) << 32)

				| (((long) b[offset + 4] & 0xff) << 24)
				| (((long) b[offset + 5] & 0xff) << 16)
				| (((long) b[offset + 6] & 0xff) << 8) | (((long) b[offset + 7] & 0xff) << 0));
	}

	/**
	 * byte[8]转long
	 * 
	 * @param b
	 * @return
	 */
	public static long byte2Long(byte[] b) {
		return ((b[0] & 0xff) << 56) | ((b[1] & 0xff) << 48)
				| ((b[2] & 0xff) << 40) | ((b[3] & 0xff) << 32) |

				((b[4] & 0xff) << 24) | ((b[5] & 0xff) << 16)
				| ((b[6] & 0xff) << 8) | (b[7] & 0xff);
	}

	/**
	 * long转byte[8]
	 * 
	 * @param a
	 * @return
	 */
	public static byte[] long2Byte(long a) {
		byte[] b = new byte[4 * 2];

		b[0] = (byte) (a >> 56);
		b[1] = (byte) (a >> 48);
		b[2] = (byte) (a >> 40);
		b[3] = (byte) (a >> 32);

		b[4] = (byte) (a >> 24);
		b[5] = (byte) (a >> 16);
		b[6] = (byte) (a >> 8);
		b[7] = (byte) (a >> 0);

		return b;
	}

	/**
	 * byte数组转int
	 * 
	 * @param b
	 * @return
	 */
	public static int byte2Int(byte[] b) {
		return ((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16)
				| ((b[2] & 0xff) << 8) | (b[3] & 0xff);
	}

	/**
	 * byte数组转int
	 * 
	 * @param b
	 * @param offset
	 * @return
	 */
	public static int byte2Int(byte[] b, int offset) {
		return ((b[offset++] & 0xff) << 24) | ((b[offset++] & 0xff) << 16)
				| ((b[offset++] & 0xff) << 8) | (b[offset++] & 0xff);
	}

	/**
	 * int转byte数组
	 * 
	 * @param a
	 * @return
	 */
	public static byte[] int2Byte(int a) {
		byte[] b = new byte[4];
		b[0] = (byte) (a >> 24);
		b[1] = (byte) (a >> 16);
		b[2] = (byte) (a >> 8);
		b[3] = (byte) (a);

		return b;
	}

	/**
	 * int转byte数组
	 * 
	 * @param a
	 * @param b
	 * @param offset
	 * @return
	 */
	public static void int2Byte(int a, byte[] b, int offset) {
		b[offset++] = (byte) (a >> 24);
		b[offset++] = (byte) (a >> 16);
		b[offset++] = (byte) (a >> 8);
		b[offset++] = (byte) (a);
	}

	/**
	 * 
	 * delete file 删除文件
	 * 
	 */
	static public void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		}
	}

	public static boolean copyAssetFileToPath(Context context, String fileName,
                                              String path) {
		boolean isSuccessful = false;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		try {

			InputStream is = context.getResources().getAssets().open(fileName);
			dis = new DataInputStream(is);
			byte[] dst = new byte[dis.available()];
			dis.readFully(dst);
			dos = new DataOutputStream(new FileOutputStream(new File(path,
					fileName)));
			dos.write(dst);
			isSuccessful = true;
		} catch (IOException e1) {
			e1.printStackTrace();
			isSuccessful = false;
		} finally {
			try {
				if (dis != null) {
					dis.close();
					dis = null;
				}
				if (dos != null) {
					dos.close();
					dos = null;
				}
			} catch (IOException e1) {
			}
		}
		return isSuccessful;
	}

	/**
	 * 
	 * use for adapter class in list view, function is display UI 内部工具类，一般在 list
	 * view 中 adapter 类使用，用于 UI 的显示
	 * 
	 */
	static public class ViewHolder {
		@SuppressWarnings("unchecked")
		public static <T extends View> T get(View view, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				view.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}
	}
	
	/**
	 * 把毫秒转化成日期
	 * 
	 * @param dateFormat
	 *            (日期格式，例如：MM/dd/yyyy HH:mm:ss)
	 * @param millSec
	 *            (毫秒数)
	 * @return
	 */
	public static String transferLongToDate(String dateFormat, Long millSec) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = new Date(millSec);
		return sdf.format(date);
	}
}
