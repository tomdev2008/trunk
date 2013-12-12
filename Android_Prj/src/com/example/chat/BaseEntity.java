package com.example.chat;

import android.app.Activity;
import android.app.Service;

/**
 * @author pswell
 *
 */
public class BaseEntity {
	private Object object;
	private boolean enable;
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	
	public void finish(){
		if (object!=null) {
			if (object instanceof Activity) {
				((Activity) object).finish();
			}
			else if (object instanceof Service) {
				((Service) object).onDestroy();
			}
			object=null;
		}
	}
}
