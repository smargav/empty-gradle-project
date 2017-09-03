package com.healthengagements.home.ui;

import android.app.enterprise.ApplicationPolicy;
import android.app.enterprise.EnterpriseDeviceManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.healthengagements.home.R;
import com.healthengagements.home.adapter.AppListAdapter;
import com.healthengagements.home.model.AppInfo;
import com.healthengagements.home.utils.PackageUtil;
import com.smargav.api.asynctasks.ProgressAsyncTask;
import com.smargav.api.utils.DialogUtils;

/**
 * Created by Amit S on 30/08/17.
 */

public class SystemAppsDeleteActivity extends AppCompatActivity {

    private ApplicationPolicy appPolicy;
    private EnterpriseDeviceManager mEDM;
    private ListView listView;
    private AppInfo selectedAppInfo;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_list);
        mEDM = new EnterpriseDeviceManager(this);
        appPolicy = mEDM.getApplicationPolicy();
        initList();
    }

    private void initList() {
        listView = (ListView) findViewById(R.id.appsListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                confirmDelete(((AppListAdapter.ViewHolder) view.getTag()).appInfo);
            }
        });
        AppListAdapter adapter = new AppListAdapter(this, PackageUtil.getPackagesList(this, true));
        listView.setAdapter(adapter);
    }

    private void confirmDelete(final AppInfo appInfo) {
        DialogUtils.showPrompt(this, "Confirm", "Are you sure you want to delete the App " + appInfo.getAppName(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    deletePackage(appInfo);
                }
            }
        }, new String[]{"Yes", "No"});

    }

    private class DeleteAppTask extends ProgressAsyncTask<Void, Integer> {
        public DeleteAppTask(Context ctx) {
            super(ctx);
        }

        @Override
        public void onPostExecute(Integer result) {
            super.onPostExecute(result);
            AppListAdapter adapter = new AppListAdapter(SystemAppsDeleteActivity.this, PackageUtil.getPackagesList(ctx, false));
            listView.setAdapter(adapter);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            appPolicy.setDisableApplication(selectedAppInfo.getPkgName());
            appPolicy.setApplicationInstallationDisabled(selectedAppInfo.getPkgName());
            PackageUtil.getPackagesList(ctx, true);
            return FAILED;
        }
    }

    private void deletePackage(AppInfo appInfo) {

        this.selectedAppInfo = appInfo;
        //appPolicy.uninstallApplication(appInfo.getPkgName(), false);
        new DeleteAppTask(this).execute();

    }
}
