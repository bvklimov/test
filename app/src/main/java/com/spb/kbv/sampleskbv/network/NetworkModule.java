package com.spb.kbv.sampleskbv.network;

import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.spb.kbv.sampleskbv.AppConfig;
import com.spb.kbv.sampleskbv.BuildConfig;
import com.spb.kbv.sampleskbv.exceptions.AirplaneException;
import com.spb.kbv.sampleskbv.exceptions.InternetConnectionException;
import com.spb.kbv.sampleskbv.helpers.ConnectionHelper;
import com.spb.kbv.sampleskbv.model.UserCredentials;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    @Provides
    @Singleton
    @NonNull
    Retrofit provideNetworkCall(
            @NonNull ConnectionHelper connectionHelper,
            @NonNull UserCredentials userCredential,
            @NonNull Gson gson) {
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okClientBuilder.addInterceptor(loggingInterceptor);
            okClientBuilder.addNetworkInterceptor(new StethoInterceptor());
        }
        OkHttpClient client = okClientBuilder
                .connectTimeout(AppConfig.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(AppConfig.TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(AppConfig.TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    if (connectionHelper.isInAirplaneMode()) {
                        throw new AirplaneException();
                    }
                    if (!connectionHelper.isInternetConnected()) {
                        throw new InternetConnectionException();
                    }
                    Request request = chain.request().newBuilder()
                            .header("content-type", "application/x-www-form-urlencoded")
                            .build();
                    return chain.proceed(request);
                }).build();

        return new Retrofit.Builder()
                .baseUrl(AppConfig.SERVER_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    @NonNull
    Gson provideGsonParser() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>)
                        (jsonElement, type, jsonDeserializationContext) ->
                                ZonedDateTime.parse(jsonElement.getAsJsonPrimitive().getAsString())
                                        .toLocalDateTime());
        return builder.setLenient().create();
    }

    @Provides
    @Singleton
    @NonNull
    NetworkService provideNetworkService(@NonNull Retrofit retrofit) {
        return retrofit.create(NetworkService.class);
    }
}
