package com.healthengagements.home.ui;

import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.cocosw.bottomsheet.BottomSheet;
import com.healthengagements.home.R;
import com.healthengagements.home.utils.KioskUtil;
import com.smargav.api.prefs.PreferencesUtil;

/**
 * Created by Amit S on 30/08/17.
 */

public class HomeScreen extends AppCompatActivity {

    private KeyguardManager.KeyguardLock kl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("name");
        kl.disableKeyguard();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PreferencesUtil.getLong(this, SetupActivity.KEY, 0) != SetupActivity.STEP_COMPLETE) {
            startActivity(new Intent(this, SetupActivity.class));
            return;
        }

        if (!KioskUtil.isEnabled(this)) {
            KioskUtil.enterKioskMode(this);
        }

        initScreen();

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
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        KioskUtil.disableKioskMode(this);
//    }
//
//
//    private boolean pressedOnce;
//    private long lastPressTime;
//
//    @Override
//    public void onBackPressed() {
//        if (pressedOnce) {
//            if (System.currentTimeMillis() - lastPressTime < 1000) {
//                KioskUtil.disableKioskMode(this);
//            } else {
//                pressedOnce = false;
//                lastPressTime = 0;
//            }
//        } else {
//            pressedOnce = true;
//            lastPressTime = System.currentTimeMillis();
//        }
//
//    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        askPassword(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

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
            case R.id.disableKiosk:
                KioskUtil.disableKioskMode(this);
                break;
            case R.id.enableKiosk:
                KioskUtil.enterKioskMode(this);
                break;
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
