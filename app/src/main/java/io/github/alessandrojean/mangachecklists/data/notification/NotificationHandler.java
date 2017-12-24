package io.github.alessandrojean.mangachecklists.data.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Class copied from Tachiyomi repository.
 * Available at: https://github.com/inorichi/tachiyomi
 */
public class NotificationHandler {

    /**
     * Returns [PendingIntent] that prompts user with apk install intent.
     *
     * @param context context.
     * @param uri uri of apk that is installed.
     */
    public static PendingIntent installApkPendingActivity(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return PendingIntent.getActivity(context, 0, intent, 0);
    }

}
