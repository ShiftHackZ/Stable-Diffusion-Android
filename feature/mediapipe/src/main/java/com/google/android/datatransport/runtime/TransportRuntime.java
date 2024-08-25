package com.google.android.datatransport.runtime;

import android.content.Context;

public class TransportRuntime {

    public static void initialize(Context applicationContext) {}

    public static TransportRuntime getInstance() {
        return new TransportRuntime();
    }
}
