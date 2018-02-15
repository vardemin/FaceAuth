package com.vardemin.faceauth.di.module;


import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Application-level module
 * Provides context
 */
@Module
public class AppModule {
    @NonNull
    private final Context context;

    public AppModule(@NonNull Context context) {
        this.context = context;
    }

    /**
     * Provide Application context
     *
     * @return context
     */
    @Provides
    @Singleton
    Context provideContext() {
        return this.context;
    }
}

