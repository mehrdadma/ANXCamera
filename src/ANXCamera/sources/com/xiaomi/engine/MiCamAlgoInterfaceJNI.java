package com.xiaomi.engine;

import android.hardware.camera2.params.OutputConfiguration;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import com.xiaomi.engine.TaskSession.FrameCallback;
import com.xiaomi.engine.TaskSession.SessionStatusCallback;
import java.util.List;

class MiCamAlgoInterfaceJNI {
    private static final String LIB_NAME = "com.xiaomi.camera.algojni";
    private static final String TAG = "MiCamAlgoInterfaceJNI";

    static {
        try {
            Log.e(TAG, "start loading com.xiaomi.camera.algojni");
            System.loadLibrary(LIB_NAME);
            Log.d(TAG, "static initializer: loadLibrary com.xiaomi.camera.algojni");
        } catch (UnsatisfiedLinkError e2) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("can not load library:com.xiaomi.camera.algojni : ");
            sb.append(Log.getStackTraceString(e2));
            Log.e(str, sb.toString());
        }
    }

    MiCamAlgoInterfaceJNI() {
    }

    static native long createSessionByOutputConfigurations(BufferFormat bufferFormat, List<OutputConfiguration> list, @NonNull SessionStatusCallback sessionStatusCallback);

    static native long createSessionWithSurfaces(BufferFormat bufferFormat, List<Surface> list, @NonNull SessionStatusCallback sessionStatusCallback);

    static native int deInit();

    static native int destroySession(long j);

    static native int flush(long j);

    static native int init(String str);

    static native int processFrame(long j, @NonNull FrameData frameData, @NonNull FrameCallback frameCallback);
}
