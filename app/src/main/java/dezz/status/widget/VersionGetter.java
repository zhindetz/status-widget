package dezz.status.widget;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class VersionGetter {
    private static final String TAG = "VersionGetter";

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogsActivity.log(TAG, "Error getting app version name", e);
            return null;
        }
    }
}
