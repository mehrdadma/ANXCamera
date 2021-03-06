package android.support.v4.media;

import android.content.Intent;
import android.os.IBinder;
import android.support.v4.media.MediaLibraryService2.MediaLibrarySession;

class MediaLibraryService2ImplBase extends MediaSessionService2ImplBase {
    MediaLibraryService2ImplBase() {
    }

    public MediaLibrarySession getSession() {
        return (MediaLibrarySession) super.getSession();
    }

    public int getSessionType() {
        return 2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x003f  */
    public IBinder onBind(Intent intent) {
        char c2;
        String action = intent.getAction();
        int hashCode = action.hashCode();
        if (hashCode != 901933117) {
            if (hashCode == 1665850838 && action.equals(MediaBrowserServiceCompat.SERVICE_INTERFACE)) {
                c2 = 1;
                return c2 != 0 ? c2 != 1 ? super.onBind(intent) : getSession().getImpl().getLegacySessionBinder() : getSession().getSessionBinder();
            }
        } else if (action.equals(MediaLibraryService2.SERVICE_INTERFACE)) {
            c2 = 0;
            if (c2 != 0) {
            }
        }
        c2 = 65535;
        if (c2 != 0) {
        }
    }
}
