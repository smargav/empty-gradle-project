package com.healthengagements.home.utils;

import android.app.enterprise.kioskmode.KioskMode;
import android.content.Context;

/**
 * Created by Amit S on 01/09/17.
 */

public class KioskUtil {
    public static void enterKioskMode(Context context) {
        KioskMode kioskModeService = KioskMode.getInstance(context);
        kioskModeService.hideStatusBar(true);
        kioskModeService.hideSystemBar(true);
        kioskModeService.enableKioskMode(context.getPackageName());
    }

    public static boolean isEnabled(Context context) {
        KioskMode kioskModeService = KioskMode.getInstance(context);
        return kioskModeService.isKioskModeEnabled();
    }

    public static void disableKioskMode(Context context) {
        KioskMode kioskModeService = KioskMode.getInstance(context);
        kioskModeService.hideStatusBar(false);
        kioskModeService.hideSystemBar(false);
        kioskModeService.disableKioskMode();

    }
}
