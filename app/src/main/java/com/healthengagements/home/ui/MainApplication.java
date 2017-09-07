package com.healthengagements.home.ui;

import android.app.Application;

import com.smargav.api.activities.PermissionsActivity;
import com.smargav.api.logger.AppLogger;

/**
 * Created by Amit S on 06/09/17.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (PermissionsActivity.hasAllRequiredPerms(this)) {
            AppLogger.init(this, "healtmelogs/", 7 * 24 * 60 * 60 * 1000);
        }
    }
}
