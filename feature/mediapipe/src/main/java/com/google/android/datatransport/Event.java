package com.google.android.datatransport;


import androidx.annotation.Nullable;

public abstract class Event<T> {
    public Event() {
    }

    @Nullable
    public abstract Integer getCode();

    @Nullable
    public abstract T getPayload();

    @Nullable
    public abstract Priority getPriority();

    @Nullable
    public static <T> Event<T> ofData(int code, T payload) {
        return null;
    }

    @Nullable
    public static <T> Event<T> ofData(T payload) {
        return null;
    }

    @Nullable
    public static <T> Event<T> ofTelemetry(int code, T value) {
        return null;
    }

    @Nullable
    public static <T> Event<T> ofTelemetry(T value) {
        return null;
    }

    @Nullable
    public static <T> Event<T> ofUrgent(int code, T value) {
        return null;
    }

    @Nullable
    public static <T> Event<T> ofUrgent(T value) {
        return null;
    }
}
