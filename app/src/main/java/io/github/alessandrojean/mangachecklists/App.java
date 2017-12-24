package io.github.alessandrojean.mangachecklists;

import android.app.Application;
import android.content.Context;

import com.evernote.android.job.JobManager;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import io.github.alessandrojean.mangachecklists.acra.CrashReportActivity;
import io.github.alessandrojean.mangachecklists.acra.CrashReportSenderFactory;
import io.github.alessandrojean.mangachecklists.data.notification.Notifications;
import io.github.alessandrojean.mangachecklists.data.updater.UpdaterJob;

/**
 * Created by Desktop on 22/12/2017.
 */

@ReportsCrashes(mailTo = "alessandrojean@gmail.com",
                mode = ReportingInteractionMode.DIALOG,
                reportDialogClass = CrashReportActivity.class
                // TODO: implement HTML email.
                //reportSenderFactoryClasses = CrashReportSenderFactory.class
)
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        setupAcra();
        setupJobManager();
        setupNotificationChannels();
    }

    private void setupAcra() {
        ACRA.init(this);
    }

    private void setupJobManager() {
        JobManager.create(this).addJobCreator(tag -> {
            if (tag.equals(UpdaterJob.TAG))
                return new UpdaterJob();
            else
                return null;
        });
    }

    private void setupNotificationChannels() {
        Notifications.createChannels(this);
    }
}
