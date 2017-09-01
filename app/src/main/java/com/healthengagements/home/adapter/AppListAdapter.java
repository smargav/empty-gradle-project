package com.healthengagements.home.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.healthengagements.home.R;
import com.healthengagements.home.model.AppInfo;
import com.smargav.api.logger.AppLogger;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private List<AppInfo> appsList;
    private Activity activity;

    public AppListAdapter(Activity activity, List<AppInfo> devicesList) {
        this.appsList = devicesList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return appsList.size();
    }

    @Override
    public Object getItem(int position) {
        return appsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {

        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.app_item, null);
            AppInfo d = (AppInfo) getItem(position);
            holder.appInfo = d;
            holder.name = (TextView) convertView.findViewById(R.id.appItem);
            holder.lastUpdated = (TextView) convertView
                    .findViewById(R.id.lastUpdated);
            holder.versionName = (TextView) convertView
                    .findViewById(R.id.versionName);
            holder.appIcon = (ImageView) convertView.findViewById(R.id.appIcon);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AppInfo d = (AppInfo) getItem(position);
        holder.appInfo = d;

        String appName = holder.appInfo.getAppName();
        String pkgName = holder.appInfo.getPkgName();
        if (StringUtils.isBlank(appName)) {
            holder.name.setText(pkgName);
        } else {
            holder.name.setText(appName);
        }

        // name
        if (holder.appInfo.getAppIcon() == null) {
            holder.appIcon.setImageDrawable(activity.getResources()
                    .getDrawable(R.mipmap.ic_launcher));
        } else {
            holder.appIcon.setImageDrawable(holder.appInfo.getAppIcon());
        }

        String date = "Last Updated: Unknown";
        if (holder.appInfo.getLastUpdated() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM, dd yyyy");
            date = "Last Updated: "
                    + formatter.format(holder.appInfo.getLastUpdated());
        }
        holder.lastUpdated.setText(date);

        AppLogger.d(
                getClass(),
                "Update? " + appName + " == "
                        + holder.appInfo.isMarkForUpdate());
        holder.versionName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                null, null);

        String versionUrl = "v" + holder.appInfo.getVersionName();
        holder.versionName.setText(versionUrl);

        convertView.setTag(holder);

        return convertView;
    }

    public static class ViewHolder {
        public AppInfo appInfo;
        public TextView name, lastUpdated, versionName;
        public int iconId;
        public ImageView appIcon;
    }
}
