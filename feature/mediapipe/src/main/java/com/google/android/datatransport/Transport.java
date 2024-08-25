package com.google.android.datatransport;

public interface Transport<T> {
    void send(Event<T> var1);

    void schedule(Event<T> var1, TransportScheduleCallback var2);
}
