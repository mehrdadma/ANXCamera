package com.bumptech.glide.load.engine.bitmap_recycle;

/* compiled from: ArrayPool */
public interface b {
    public static final int vr = 65536;

    void G();

    <T> T a(int i, Class<T> cls);

    @Deprecated
    <T> void a(T t, Class<T> cls);

    <T> T b(int i, Class<T> cls);

    <T> void put(T t);

    void trimMemory(int i);
}
