/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.ehealthkiosk.kiosk.utils.weight;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class VScaleGattAttributes {

	public static final String M1_NAME = "VScale";// 大白的名字
	public static final String N1_MANE = "BodyMini1";//小白名字

	private static HashMap<String, String> attributes = new HashMap();
	public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
//大白UUID
	public static String BLE_SCALE_SERVICE_UUID = "f433bd80-75b8-11e2-97d9-0002a5d5c51b";
	public static String BLE_SCALE_TEST_RESULT_CHARACTERISTIC_UUID = "1a2ea400-75b9-11e2-be05-0002a5d5c51b";
	public static String BLE_SCALE_SET_USER_CHARACTERISTIC_UUID = "29f11080-75b9-11e2-8bf6-0002a5d5c51b";
	public static String BLE_SCALE_USER_INFO_CHARACTERISTIC_UUID = "23b4fec0-75b9-11e2-972a-0002a5d5c51b";
//小白UUID
	public static String BLE_MINI_SCALE_SERVICE_UUID = "0000bca0-0000-1000-8000-00805f9b34fb";
	public static String BLE_MINI_SCALE_TEST_RESULT_CHARACTERISTIC_UUID = "0000bca1-0000-1000-8000-00805f9b34fb";
	public static String BLE_MINI_SCALE_SET_USER_CHARACTERISTIC_UUID = "0000bca2-0000-1000-8000-00805f9b34fb";
	public static String BLE_MINI_SCALE_USER_INFO_CHARACTERISTIC_UUID = "23b4fec0-75b9-11e2-972a-0002a5d5c51b";
	static {
		// Sample Services.
		attributes.put("0000180d-0000-1000-8000-00805f9b34fb",
				"Heart Rate Service");
		attributes.put("0000180a-0000-1000-8000-00805f9b34fb",
				"Device Information Service");
		// Sample Characteristics.
		attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
		attributes.put("00002a29-0000-1000-8000-00805f9b34fb",
				"Manufacturer Name String");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}
}
