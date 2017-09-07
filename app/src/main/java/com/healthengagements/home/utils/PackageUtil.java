package com.healthengagements.home.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.healthengagements.home.model.AppInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PackageUtil {

    public static ArrayList<AppInfo> packagesList = new ArrayList<AppInfo>();

    public static List<AppInfo> getPackagesList(Context ctx) {

        if (!packagesList.isEmpty()) {
            return packagesList;
        }
        packagesList = new ArrayList<AppInfo>();

        final PackageManager pm = ctx.getPackageManager();
        // get a list of installed apps.
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;

        List<ApplicationInfo> packages = pm
                .getInstalledApplications(flags);

        String thisPackage = ctx.getPackageName();
        for (ApplicationInfo appInfo : packages) {

            if (StringUtils.equalsIgnoreCase(appInfo.packageName, thisPackage)) {
                continue;
            }

            AppInfo pkg = new AppInfo(appInfo.packageName);
            pkg.setLastUpdated(null);
            try {
                PackageInfo packageInfo = pm.getPackageInfo(
                        appInfo.packageName, 0);
                String versionName = packageInfo.versionName;
                if (StringUtils.isNotBlank(versionName)) {
                    versionName = versionName.replaceFirst("\\(.*?\\)", "");
                }
                pkg.setVersionName(versionName);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(packageInfo.lastUpdateTime);
                pkg.setLastUpdated(c.getTime());

            } catch (NameNotFoundException e1) {
                //e1.printStackTrace();
            }
            if (appInfo.labelRes != 0) {
                CharSequence label = null;
                try {
                    label = appInfo.loadLabel(pm);

                    if (label != null && StringUtils.isNotBlank(label.toString())) {
                        pkg.setAppName(label.toString());
                    }
                } catch (Resources.NotFoundException e) {
                    pkg.setAppName(pkg.getPkgName());
                }

            } else {
                pkg.setAppName(pkg.getPkgName());
            }
            if (appInfo.icon != 0) {
                Drawable icon = appInfo.loadIcon(pm);
                if (icon != null) {
                    pkg.setAppIcon(icon);
                }
            }

            // Amit changed this.. Sending only user apps to save multiple
            // iterations!
            if (isAllowedApp(appInfo)) {
                packagesList.add(pkg);
            }

        }
        Collections.sort(packagesList, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo appInfo, AppInfo t1) {
                return appInfo.getAppName().compareTo(t1.getAppName());
            }
        });
        return packagesList;
    }

    public static List<AppInfo> getPackagesList(Context ctx,
                                                boolean forceRefresh) {
        if (forceRefresh) {
            packagesList.clear();
        }
        return getPackagesList(ctx);
    }

    public static List<AppInfo> getUserApps(Context ctx) {
        List<AppInfo> userApps = getPackagesList(ctx);
        Collections.sort(userApps, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo lhs, AppInfo rhs) {
                if (StringUtils.isBlank(lhs.getAppName())) {
                    return 1;
                } else if (StringUtils.isBlank(rhs.getAppName())) {
                    return -1;
                }
                return lhs.getAppName().compareTo(rhs.getAppName());
            }
        });
        return userApps;
    }

    public static List<AppInfo> getUserApps(Context ctx, String filter) {
        List<AppInfo> userApps = getPackagesList(ctx);

        List<AppInfo> filteredApps = new ArrayList<>();

        for (AppInfo app : userApps) {
            if (StringUtils.contains(app.getPkgName(), filter)) {
                filteredApps.add(app);
            }

            if (StringUtils.contains(app.getPkgName(), "com.android.settings")) {
                filteredApps.add(app);
            }
        }


        Collections.sort(filteredApps, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo lhs, AppInfo rhs) {
                if (StringUtils.isBlank(lhs.getAppName())) {
                    return 1;
                } else if (StringUtils.isBlank(rhs.getAppName())) {
                    return -1;
                }
                return lhs.getAppName().compareTo(rhs.getAppName());
            }
        });
        return filteredApps;
    }

    private static boolean isAllowedApp(ApplicationInfo pkgInfo) {

        return pkgInfo.enabled;
//        if (isBlackListed(pkgInfo.packageName)) {
//            return false;
//        }
//        if (isWhiteListed(pkgInfo.packageName)) {
//            return true;
//        }
//        boolean isSystemApp = (((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
//                : false);
//
//        return !isSystemApp;
        //return true;
    }


}
