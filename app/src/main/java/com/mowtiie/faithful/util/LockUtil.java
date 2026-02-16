package com.mowtiie.faithful.util;

public class LockUtil {

    private static LockUtil instance;
    private long lastUsedTime = 0;
    private final long LOCK_TIMEOUT = 30000; // 30 seconds

    public static synchronized LockUtil getInstance() {
        if (instance == null) instance = new LockUtil();
        return instance;
    }

    public void updateLastUsed() {
        lastUsedTime = System.currentTimeMillis();
    }

    public boolean shouldLock() {
        if (lastUsedTime == 0) return true;
        return (System.currentTimeMillis() - lastUsedTime) > LOCK_TIMEOUT;
    }
}