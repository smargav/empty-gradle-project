package com.healthengagements.home.ui;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.healthengagements.home.R;
import com.healthengagements.home.model.AppInfo;
import com.healthengagements.home.utils.AdminUtils;
import com.healthengagements.home.utils.PackageUtil;
import com.smargav.api.prefs.PreferencesUtil;
import com.smargav.api.utils.BaseAdapter2;
import com.smargav.api.utils.DialogUtils;

import java.util.List;

/**
 * Created by Amit S on 30/08/17.
 */

public class HomeScreen extends AppCompatActivity {

    private KeyguardManager.KeyguardLock kl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PreferencesUtil.getLong(this, SetupActivity.KEY, 0) != SetupActivity.STEP_COMPLETE) {
            startActivity(new Intent(this, SetupActivity.class));
            return;
        }

        if (!isDeviceAdminActive()) {
            PreferencesUtil.remove(this, SetupActivity.KEY);
            startActivity(new Intent(this, SetupActivity.class));
            return;
        }

//        if (!AdminUtils.isEnabled(this)) {
//            AdminUtils.enterKioskMode(this);
//        }
        AdminUtils.initAdminRestrictions(this);

        initScreen();

    }

    private boolean isDeviceAdminActive() {
        ComponentName mDeviceAdmin = new ComponentName(this, HEAdminReceiver.class);
        DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        boolean active = mDPM.isAdminActive(mDeviceAdmin);
        return active;
    }

    private void initScreen() {
        Button hiddenButton = (Button) findViewById(R.id.hiddenButton);
        hiddenButton.setLongClickable(true);
        hiddenButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                askPassword(R.id.hiddenButton);
                return false;
            }
        });


        GridView listView = (GridView) findViewById(R.id.appsGrid);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    AppInfo appInfo = (AppInfo) view.getTag();
                    startActivity(getPackageManager().getLaunchIntentForPackage(appInfo.getPkgName()));
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtils.showPrompt(HomeScreen.this, "Error", "Could not launch App. Please check");
                }
            }
        });
        AppAdapter adapter = new AppAdapter(this, getHEAppsList(), R.layout.item_grid);
        listView.setAdapter(adapter);
    }

    private List<AppInfo> getHEAppsList() {
        return PackageUtil.getUserApps(this, "com.healthengagements");
    }

    private class AppAdapter extends BaseAdapter2<AppInfo> {
        public AppAdapter(Context context, List<AppInfo> objects, int layout) {
            super(context, objects, layout);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            AppInfo info = getItemAt(position);
            ((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(info.getAppIcon());
            ((TextView) convertView.findViewById(R.id.name)).setText(info.getAppName());
            convertView.setTag(info);
            return convertView;
        }
    }


    @Override
    public void onBackPressed() {

    }


    //********** Menu Handler.
    private void askPassword(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText text = new EditText(this);
        text.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        text.setMaxLines(1);
        text.setSingleLine(true);
        builder.setTitle("Enter admin password").setView(text).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //startActivity(new Intent(HomeScreen.this, SystemAppsDeleteActivity.class));
                showBottomSheet();
            }
        }).setNegativeButton("Cancel", null).create().show();
    }

    private void showBottomSheet() {
        new BottomSheet.Builder(this).title("Settings").sheet(R.menu.home).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleAction(which);
            }
        }).show();
    }

    private void handleAction(int which) {
        switch (which) {
//            case R.id.disableKiosk:
//                AdminUtils.disableKioskMode(this);
//                break;
//            case R.id.enableKiosk:
//                AdminUtils.enterKioskMode(this);
//                break;
            case R.id.apps:
                startActivity(new Intent(HomeScreen.this, SystemAppsDeleteActivity.class));
                break;
            case R.id.settings:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;


        }
    }


}
