package com.google.android.datatransport;

public interface TransportFactory {
    @Deprecated
    <T> Transport<T> getTransport(String var1, Class<T> var2, Transformer<T, byte[]> var3);

    <T> Transport<T> getTransport(String var1, Class<T> var2, Encoding var3, Transformer<T, byte[]> var4);
}
