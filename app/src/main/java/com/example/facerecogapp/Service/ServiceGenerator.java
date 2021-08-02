package com.example.facerecogapp.Service;

import com.example.facerecogapp.Const;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static String jwtToken;
    public static void setAuthorizedTokenHeader(String token){
        jwtToken = token;
    }
    private static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request newRequest  = chain.request().newBuilder()
                    .addHeader("Authorization", jwtToken)
                    .build();
            return chain.proceed(newRequest);
        }
    }).connectTimeout(500, TimeUnit.SECONDS)
            .writeTimeout(500, TimeUnit.SECONDS)
            .readTimeout(500, TimeUnit.SECONDS).build();
    private static final String BASE_URL = "http://192.168.1.137:8080/api/";
    private static  final Gson gson = new GsonBuilder()
                    .setLenient()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(
            Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}