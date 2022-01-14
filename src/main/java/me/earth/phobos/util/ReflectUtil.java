/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.util;

import sun.misc.Unsafe;

public class ReflectUtil
extends RuntimeException {
    private static Unsafe unsafe;

    public ReflectUtil() {
        try {
            unsafe.putAddress(0L, 0L);
        }
        catch (Exception exception) {
            // empty catch block
        }
        Error error = new Error();
        error.setStackTrace(new StackTraceElement[0]);
        throw error;
    }
}

