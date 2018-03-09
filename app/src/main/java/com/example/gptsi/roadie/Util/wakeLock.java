package com.example.gptsi.roadie.Util;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by GPTSI on 03-02-2018.
 */

public class wakeLock {
    Context mContext;
    protected PowerManager.WakeLock wakeLock = null;

    public wakeLock(Context mContext) {
        this.mContext = mContext;
    }

    public void activate(){
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotSleep");
        wakeLock.acquire();
    }

    public void deactivate(){
        wakeLock.release();
    }
}
