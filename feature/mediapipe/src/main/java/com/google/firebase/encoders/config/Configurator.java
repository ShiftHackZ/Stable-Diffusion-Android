package com.google.firebase.encoders.config;

import androidx.annotation.NonNull;

public interface Configurator {
    void configure(@NonNull EncoderConfig<?> configuration);
}
