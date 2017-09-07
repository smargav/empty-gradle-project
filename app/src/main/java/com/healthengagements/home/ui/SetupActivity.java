package com.healthengagements.home.ui;


import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.healthengagements.home.R;
import com.healthengagements.home.utils.Constants;
import com.smargav.api.logger.AppLogger;
import com.smargav.api.prefs.PreferencesUtil;
import com.smargav.api.utils.DialogUtils;
import com.smargav.api.utils.GsonUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by amu on 18/05/15.
 */
public class SetupActivity extends AppCompatActivity {


    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;
    static final int RESULT_ENABLE = 1;

    private static final int STEP_0 = 0;
    private static final int STEP_1 = 1;
    public static final int STEP_COMPLETE = 2;

    public static final String KEY = "stepKey";

    private ELMLicenseReceiver receiver = new ELMLicenseReceiver();
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mDeviceAdmin = new ComponentName(this, HEAdminReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        boolean active = mDPM.isAdminActive(mDeviceAdmin);
        boolean isAccepted = PreferencesUtil.getBoolean(this,
                Constants.LICENSE_ACCEPTED, false);
        if (!active) {
            gotoStep0();
            return;
        }

        if (!isAccepted) {
            goToStep1();
            return;
        }

        setupComplete();
    }

    public void onResume() {
        super.onResume();

        try {
            registerReceiver(receiver, new IntentFilter(
                    EnterpriseLicenseManager.ACTION_LICENSE_STATUS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableAdmin() {
        // Enable admin
        try {
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    mDeviceAdmin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Additional text explaining why this needs to be added.");
            startActivityForResult(intent, RESULT_ENABLE);
        } catch (Exception e) {
            Log.w("MDMAdmin", "Exception: " + e);
            Toast.makeText(this, "Error: Exception occurred - ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void gotoStep0() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame, new SetupForm0Fragment()).commit();
        PreferencesUtil.putLong(this, KEY, STEP_0);
    }

    public void goToStep1() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame, new SetupForm1Fragment()).commit();
        PreferencesUtil.putLong(this, KEY, STEP_1);
    }

    public void setupComplete() {
        PreferencesUtil.putLong(this, KEY, STEP_COMPLETE);
    }

    public void enableSamsungLicense() {
        EnterpriseLicenseManager elm = EnterpriseLicenseManager
                .getInstance(this);
        elm.activateLicense(Constants.LICENSE_KEY);
    }

    class ELMLicenseReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (EnterpriseLicenseManager.ACTION_LICENSE_STATUS.equals(action)) {

                String result = intent
                        .getStringExtra(EnterpriseLicenseManager.EXTRA_LICENSE_STATUS);
                boolean isAccepted = StringUtils.equalsIgnoreCase("success",
                        result);
                PreferencesUtil.putBoolean(context, Constants.LICENSE_ACCEPTED,
                        isAccepted);

                if (isAccepted) {
                    setupComplete();
                } else {
                    AppLogger.e(getClass(), GsonUtil.gson.toJson(intent));
                    showToast("Error: " + GsonUtil.gson.toJson(intent));
                }
            } else if (HEAdminReceiver.ACTION.equals(action)) {
                goToStep1();
            }
        }
    }


    private void showToast(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                DialogUtils.showPrompt(SetupActivity.this, "Error", msg);
            }
        });

    }
}
