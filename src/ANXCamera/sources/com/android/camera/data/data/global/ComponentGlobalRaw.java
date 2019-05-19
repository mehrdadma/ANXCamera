package com.android.camera.data.data.global;

import com.android.camera.R;
import com.android.camera.data.data.ComponentData;
import com.android.camera.data.data.ComponentDataItem;
import java.util.List;

public class ComponentGlobalRaw extends ComponentData {
    public ComponentGlobalRaw(DataItemGlobal dataItemGlobal) {
        super(dataItemGlobal);
    }

    public String getDefaultValue(int i) {
        return null;
    }

    public int getDisplayTitleString() {
        return R.string.pref_camera_picture_format_entry_raw;
    }

    public List<ComponentDataItem> getItems() {
        return null;
    }

    public String getKey(int i) {
        return null;
    }
}
