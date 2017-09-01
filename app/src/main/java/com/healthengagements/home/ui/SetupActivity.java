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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.healthengagements.home.R;
import com.healthengagements.home.utils.Constants;
import com.smargav.api.asynctasks.ProgressAsyncTask;
import com.smargav.api.prefs.PreferencesUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by amu on 18/05/15.
 */
public class SetupActivity extends AppCompatActivity {
    private String key = "009AA6E64349E1AF73EADA06681CF278A9F593F9A28CB499F4742CC7A0590A638E878F976E9019B2B8107828D4E9A260CD5AC75D2038F2DEACC9AD8DA6F1153D";
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;
    static final int RESULT_ENABLE = 1;

    private static final int STEP_0 = 0;
    private static final int STEP_1 = 1;
    public static final int STEP_COMPLETE = 2;

    public static final String KEY = "stepKey";

    private ELMLicenseReceiver receiver = new ELMLicenseReceiver();

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
        new ProgressAsyncTask<Void, Integer>(this) {
            @Override
            public void onPostExecute(Integer result) {
                super.onPostExecute(result);
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                EnterpriseLicenseManager elm = EnterpriseLicenseManager
                        .getInstance(ctx);
                elm.activateLicense(key);
                return SUCCESS;
            }
        }.execute();

    }

    class ELMLicenseReceiver extends BroadcastReceiver {
        void showToast(Context context, CharSequence msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

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
                }
            } else if (HEAdminReceiver.ACTION.equals(action)) {
                goToStep1();
            }
        }
    }
}
