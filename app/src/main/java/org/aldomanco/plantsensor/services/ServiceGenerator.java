package org.aldomanco.plantsensor.services;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Definisce il modo in cui i service devono essere creati.
 * Permette di creare un'istanza di retrofit in grado di comunicare con il server tramite
 * richieste http.
 */
public class ServiceGenerator {

    // create an instance of an http client
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    // build a new instance of Retrofit
    // setting the URL of the web server
    // creating and setting an instance of GSON converter
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create());

    // Retrofit adapts a Java interface to HTTP calls by using annotations on the declared methods to define how requests are made.
    // Create instances using Builder and pass your interface to {@link #create} to generate an implementation.
    private static Retrofit retrofit;

    public static <S> S createService(Class<S> serviceClass) {

        // assign to built retrofit instance the http client with interceptor
        builder.client(httpClient
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        );

        // create the retrofit instance using the configured values in built retrofit instance
        retrofit = builder.build();

        // create an implementation of the API endpoints defined by the service's interface
        return retrofit.create(serviceClass);
    }
}
