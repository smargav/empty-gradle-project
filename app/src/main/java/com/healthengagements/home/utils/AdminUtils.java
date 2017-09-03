package com.healthengagements.home.utils;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.RestrictionPolicy;
import android.app.enterprise.kioskmode.KioskMode;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.view.WindowManager;

import static android.content.Context.BLUETOOTH_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by Amit S on 01/09/17.
 */

public class AdminUtils {

    public static void initAdminRestrictions(Activity context) {
        EnterpriseDeviceManager mEDM = new EnterpriseDeviceManager(context);
        RestrictionPolicy restrictionPolicy = mEDM.getRestrictionPolicy();
        restrictionPolicy.allowStatusBarExpansion(false);

        KioskMode kioskModeService = KioskMode.getInstance(context);

        kioskModeService.wipeRecentTasks();

        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        KeyguardManager km = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("name");
        kl.disableKeyguard();

        BluetoothManager manager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        manager.getAdapter().enable();


    }

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
