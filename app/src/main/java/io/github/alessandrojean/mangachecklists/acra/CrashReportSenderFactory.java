package io.github.alessandrojean.mangachecklists.acra;

import android.content.Context;
import android.support.annotation.NonNull;

import org.acra.config.ACRAConfiguration;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderFactory;

/**
 * Class copied from F-Droid Client repository.
 * Available at: https://gitlab.com/fdroid/fdroidclient
 */

public class CrashReportSenderFactory implements ReportSenderFactory {
    @NonNull
    @Override
    public ReportSender create(@NonNull Context context, @NonNull ACRAConfiguration acraConfiguration) {
        return new CrashReportSender(acraConfiguration);
    }
}
