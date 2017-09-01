package com.healthengagements.home.model;

import android.graphics.drawable.Drawable;

import java.util.Date;

public class AppInfo {

	private String appName;
	private String pkgName;
	private transient Drawable appIcon;
	private Date lastUpdated;
	private String versionName;
	private boolean markForUpdate;

	public AppInfo(String packageName) {
		pkgName = packageName;
	}

	public String toString() {
		return pkgName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pkgName == null) ? 0 : pkgName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppInfo other = (AppInfo) obj;
		if (pkgName == null) {
			if (other.pkgName != null)
				return false;
		} else if (!pkgName.equals(other.pkgName))
			return false;
		return true;
	}

	public boolean isMarkForUpdate() {
		return markForUpdate;
	}

	public void setMarkForUpdate(boolean markForUpdate) {
		this.markForUpdate = markForUpdate;
	}

}
