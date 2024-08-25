package com.google.firebase.encoders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;

public final class FieldDescriptor {

    private FieldDescriptor(String name, Map<Class<?>, Object> properties) {
    }

    /** Name of the field. */
    @NonNull
    public String getName() {
        return "";
    }

    /**
     * Provides access to extra properties of the field.
     *
     * @return {@code T} annotation if present, null otherwise.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getProperty(@NonNull Class<T> type) {
        return null;
    }

    @NonNull
    public static FieldDescriptor of(@NonNull String name) {
        return new FieldDescriptor(name, Collections.emptyMap());
    }

    @NonNull
    public static Builder builder(@NonNull String name) {
        return new Builder(name);
    }

    public static final class Builder {


        Builder(String name) {

        }

        @NonNull
        public <T extends Annotation> Builder withProperty(@NonNull T value) {
            return this;
        }

        @NonNull
        public FieldDescriptor build() {
            return new FieldDescriptor("", Collections.emptyMap());
        }
    }
}
