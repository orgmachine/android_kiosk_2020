package com.ehealthkiosk.kiosk.api;


import androidx.annotation.NonNull;

import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class RestClient {

    private static final String API_URL = Constants.base_url;
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String PNG_FORM_DATA = "image/png";

    public static RestInterface getClient() {
        return getRetrofit().create(RestInterface.class);
    }

    public static Retrofit getRetrofit() {
        GsonBuilder gBuilder = new GsonBuilder();
        gBuilder.registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory<>());

        Gson gson = gBuilder.create();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringAdapter();
        }
    }

    public static class StringAdapter extends TypeAdapter<String> {
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                //System.out.println(("null  updated to space");

                return "";
            }
            return reader.nextString();
        }

        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }

    public static boolean getStatus(Status status) {
        if (status != null) {
            if (status.getResult() == 1) {
                //Common_Utils.showToast(status.getMessage());
                Common_Utils.hideProgress();
                return true;
            } else {
                Common_Utils.showToast(status.getMessage());
                return false;
            }
        } else {
            Common_Utils.showToast(Constants.SERVER_ERROR);
            return false;
        }
    }

    public static MultipartBody.Part prepareFilePart(String partName, File file) {

        if (file == null)
            return null;

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
    public static MultipartBody.Part preparePNGPart(String partName, File file) {

        if (file == null)
            return null;

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(PNG_FORM_DATA), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }
}
