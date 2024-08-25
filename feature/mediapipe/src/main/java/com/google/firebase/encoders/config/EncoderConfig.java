package com.google.firebase.encoders.config;

import androidx.annotation.NonNull;

import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ValueEncoder;

public interface EncoderConfig<T extends EncoderConfig<T>> {
    @NonNull
    <U> T registerEncoder(@NonNull Class<U> type, @NonNull ObjectEncoder<? super U> encoder);

    @NonNull
    <U> T registerEncoder(@NonNull Class<U> type, @NonNull ValueEncoder<? super U> encoder);
}
