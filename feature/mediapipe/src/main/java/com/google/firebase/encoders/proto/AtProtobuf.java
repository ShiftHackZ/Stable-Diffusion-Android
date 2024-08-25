package com.google.firebase.encoders.proto;

import java.lang.annotation.Annotation;

public class AtProtobuf {
    public AtProtobuf() {

    }

    public AtProtobuf tag(int tag) {

        return this;
    }

    public AtProtobuf intEncoding(Protobuf.IntEncoding intEncoding) {

        return this;
    }

    public static AtProtobuf builder() {
        return new AtProtobuf();
    }

    public Protobuf build() {
        return new ProtobufImpl();
    }

    private static final class ProtobufImpl implements Protobuf {

        ProtobufImpl(int tag, Protobuf.IntEncoding intEncoding) {

        }

        ProtobufImpl() {}

        public Class<? extends Annotation> annotationType() {
            return Protobuf.class;
        }

        public int tag() {
            return 0;
        }

        public Protobuf.IntEncoding intEncoding() {
            return IntEncoding.DEFAULT;
        }
    }
}
