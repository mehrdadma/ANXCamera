package com.android.camera;

import android.content.Context;
import com.android.camera.data.DataRepository;
import com.android.camera.data.data.global.DataItemGlobal;
import com.android.camera.log.Log;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.util.Locale;
import miui.external.Application;

public class CrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "CameraFCHandler";
    private static CrashHandler sInstance = new CrashHandler();
    private WeakReference<Application> mContextRef;
    private UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Application application) {
        this.mContextRef = new WeakReference<>(application);
        if (this.mDefaultHandler == null) {
            this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    public void uncaughtException(Thread thread, Throwable th) {
        LocationManager.instance().recordLocation(false);
        DataItemGlobal dataItemGlobal = DataRepository.dataItemGlobal();
        Log.e(TAG, String.format(Locale.ENGLISH, "Camera FC, @Module = %d And @CameraId = %d", new Object[]{Integer.valueOf(dataItemGlobal.getCurrentMode()), Integer.valueOf(dataItemGlobal.getCurrentCameraId())}));
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Camera FC, msg=");
        sb.append(th.getMessage());
        Log.e(str, sb.toString(), th);
        if (this.mContextRef != null) {
            CameraSettings.setEdgeMode((Context) this.mContextRef.get(), false);
            Util.setBrightnessRampRate(-1);
            Util.setScreenEffect(false);
            this.mContextRef = null;
        }
        if (this.mDefaultHandler != null) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("mDefaultHandler=");
            sb2.append(this.mDefaultHandler);
            Log.e(str2, sb2.toString());
            this.mDefaultHandler.uncaughtException(thread, th);
            this.mDefaultHandler = null;
        }
    }
}
