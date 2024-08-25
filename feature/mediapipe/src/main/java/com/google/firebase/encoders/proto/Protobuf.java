package com.google.firebase.encoders.proto;

public @interface Protobuf {
    int tag();

    /** Specifies numeric field encoding. */
    IntEncoding intEncoding() default IntEncoding.DEFAULT;

    enum IntEncoding {
        DEFAULT,
        SIGNED,
        FIXED
    }
}
