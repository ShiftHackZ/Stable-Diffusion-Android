package com.google.android.datatransport;

import androidx.annotation.Nullable;

public class AutoValue_Event<T> extends Event<T> {

    AutoValue_Event(@Nullable Integer code, T payload, Priority priority) {


    }

    @Nullable
    @Override
    public Integer getCode() {
        return 0;
    }

    @Override
    public T getPayload() {
        return null;
    }

    @Override
    public Priority getPriority() {
        return null;
    }
}
