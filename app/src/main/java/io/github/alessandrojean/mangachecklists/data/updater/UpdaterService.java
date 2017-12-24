package io.github.alessandrojean.mangachecklists.data.updater;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import io.github.alessandrojean.mangachecklists.BuildConfig;
import io.github.alessandrojean.mangachecklists.R;

/**
 * Class copied from Tachiyomi repository.
 * Available at: https://github.com/inorichi/tachiyomi
 */
public class UpdaterService extends IntentService {

    private UpdaterNotifier notifier;
    public static final String EXTRA_DOWNLOAD_URL = BuildConfig.APPLICATION_ID + ".UpdaterService.DOWNLOAD_URL";
    public static final String EXTRA_DOWNLOAD_TITLE = BuildConfig.APPLICATION_ID + ".UpdaterService.DOWNLOAD_TITLE";

    public UpdaterService() {
        super(UpdaterService.class.getName());
        notifier = new UpdaterNotifier(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null)
            return;
        if (intent.getStringExtra(EXTRA_DOWNLOAD_URL) == null)
            return;

        String title = intent.getStringExtra(EXTRA_DOWNLOAD_TITLE) == null
                ? getString(R.string.app_name)
                : intent.getStringExtra(EXTRA_DOWNLOAD_TITLE);
        String url = intent.getStringExtra(EXTRA_DOWNLOAD_URL);

        downloadApk(title, url);
    }

    /**
     * Downloads a new update and let the user install the new version from a notification.
     *
     * @param context the application context.
     * @param url the url to the new update.
     */
    public static void downloadUpdate(Context context, String url, String title) {
        if (title == null)
            title = context.getString(R.string.app_name);

        Intent intent = new Intent(context, UpdaterService.class);
        intent.putExtra(EXTRA_DOWNLOAD_TITLE, title);
        intent.putExtra(EXTRA_DOWNLOAD_URL, url);

        context.startService(intent);
    }

    /**
     * Returns [PendingIntent] that starts a service which downloads the apk specified in url.
     *
     * @param url the url to the new update.
     * @return [PendingIntent]
     */
    public static PendingIntent downloadApkPendingService(Context context, String url) {
        Intent intent = new Intent(context, UpdaterService.class);
        intent.putExtra(EXTRA_DOWNLOAD_URL, url);

        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Called to start downloading apk of new update.
     *
     * @param url url location of file.
     */
    private void downloadApk(String title, String url) {
        notifier.onDownloadStarted(title);

        // Progress of the download.
        int savedProgress = 0;

        // Keep track of the last notification sent to avoid posting to many.
        long lastTick = 0L;

        try {
            URL urll = new URL(url);
            URLConnection connection = urll.openConnection();
            connection.connect();

            int contentLength = connection.getContentLength();

            File apkFile = new File(getExternalCacheDir(), "update.apk");
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            OutputStream outputStream = new FileOutputStream(apkFile);

            byte data[] = new byte[1024];
            long bytesRead = 0;
            int count;
            while ((count = inputStream.read(data)) != -1) {
                bytesRead += count;
                outputStream.write(data, 0, count);

                int progress = (int) (bytesRead * 100 / contentLength);
                long currentTime = System.currentTimeMillis();
                if (progress > savedProgress && currentTime - 200 > lastTick) {
                    savedProgress = progress;
                    lastTick = currentTime;
                    notifier.onProgressChange(progress);
                }
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            notifier.onDownloadFinished(getUriCompat(this, apkFile));
        }
        catch (IOException e) {
            e.printStackTrace();
            notifier.onDownloadError(url);
        }
    }

    private Uri getUriCompat(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        else
            return Uri.fromFile(file);
    }
}
