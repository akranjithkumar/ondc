package com.example.ondc_buyer.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://10.176.65.28:8080/";
    private static volatile Retrofit retrofit = null;
    private static volatile ApiService apiService = null;

    /**
     * Retry interceptor that automatically retries failed requests up to 3 times.
     */
    private static class RetryInterceptor implements Interceptor {
        private static final int MAX_RETRIES = 3;

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = null;
            IOException lastException = null;

            for (int attempt = 0; attempt <= MAX_RETRIES; attempt++) {
                try {
                    if (response != null) {
                        response.close();
                    }
                    response = chain.proceed(request);
                    if (response.isSuccessful()) {
                        return response;
                    }
                    // Don't retry client errors (4xx), only server errors (5xx)
                    if (response.code() < 500) {
                        return response;
                    }
                } catch (IOException e) {
                    lastException = e;
                    if (attempt == MAX_RETRIES) break;
                    
                    try {
                        // Exponential backoff: 500ms, 1000ms, 2000ms
                        Thread.sleep((long) (500 * Math.pow(2, attempt)));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                }
            }

            if (response != null) return response;
            if (lastException != null) throw lastException;
            throw new IOException("Request failed after " + MAX_RETRIES + " retries");
        }
    }

    public static synchronized ApiService getService() {
        if (apiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new RetryInterceptor())
                    .addInterceptor(logging)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();

            // Handle LocalDateTime serialization/deserialization
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, ctx) -> {
                        try {
                            return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString());
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, ctx) -> {
                        try {
                            return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        } catch (Exception e) {
                            return new JsonPrimitive("");
                        }
                    })
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
