package com.google.common.flogger;

public class FluentLogger {

    public static FluentLogger forEnclosingClass() {
        return new FluentLogger();
    }
}
//com/google/firebase/encoders/json/JsonDataEncoderBuilder;