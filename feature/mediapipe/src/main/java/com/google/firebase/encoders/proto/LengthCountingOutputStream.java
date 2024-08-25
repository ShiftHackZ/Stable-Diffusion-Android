package com.google.firebase.encoders.proto;

import androidx.annotation.NonNull;

import java.io.OutputStream;

public class LengthCountingOutputStream extends OutputStream {

    @Override
    public void write(int b) {

    }

    @Override
    public void write(byte[] b) {

    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) {

    }

    long getLength() {
        return 0L;
    }
}
