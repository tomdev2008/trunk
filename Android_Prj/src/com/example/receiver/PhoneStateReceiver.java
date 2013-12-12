package com.example.receiver;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.example.app.ExampleApp;
import com.example.utils.Logger;

public class PhoneStateReceiver extends PhoneStateListener {
	public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
	public static final int SIGNAL_STRENGTH_POOR = 1;
	public static final int SIGNAL_STRENGTH_MODERATE = 2;
	public static final int SIGNAL_STRENGTH_GOOD = 3;
	public static final int SIGNAL_STRENGTH_GREAT = 4;
	public static final int NUM_SIGNAL_STRENGTH_BINS = 5;
	private static TelephonyManager telephonyManager = (TelephonyManager) ExampleApp
			.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
	private static String signalLevel;
	public PhoneStateReceiver(Context context) {
		super();
		telephonyManager.listen(this,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		super.onSignalStrengthsChanged(signalStrength);
		Logger.log(this, "onSignalStrengthsChanged", signalStrength);
		if (telephonyManager.getSimState() == 5) {
			// GSM信号强度：Get the GSM Signal Strength,
			// valid values are (0-31, 99) as defined in TS 27.007 8.5
			if (signalStrength.isGsm()) {
				signalLevel = getGsmLevel(signalStrength.getGsmSignalStrength());
			} else {
				int cdmaLevel = getCdmaLevel(signalStrength.getCdmaDbm(),
						signalStrength.getCdmaEcio());
				int evdoLevel = getEvdoLevel(signalStrength.getEvdoDbm(),
						signalStrength.getEvdoSnr());
				if (evdoLevel == SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
					/* We don't know evdo, use cdma */
					signalLevel = cdmaLevel+"格信号";
				} else if (cdmaLevel == SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
					/* We don't know cdma, use evdo */
					signalLevel = evdoLevel+"格信号";
				} else {
					/* We know both, use the lowest level */
					signalLevel = (cdmaLevel < evdoLevel ? cdmaLevel : evdoLevel)+"格信号";
				}
			}
//			speakPhoneState();
			Logger.log(this, "onSignalStrengthsChanged true", signalLevel);
		}
	}

	public void onServiceStateChanged(ServiceState serviceState) {
		super.onServiceStateChanged(serviceState);
		/*
		 * ServiceState.STATE_EMERGENCY_ONLY 仅限紧急呼叫
		 * ServiceState.STATE_IN_SERVICE 信号正常 ServiceState.STATE_OUT_OF_SERVICE
		 * 不在服务区 ServiceState.STATE_POWER_OFF 断电
		 */
		Logger.log(this, "onServiceStateChanged", serviceState.getState());
	}

	public static String getPhoneState(int phoneState) {
		switch (phoneState) {
		case ServiceState.STATE_EMERGENCY_ONLY:
			return "警告手机仅限紧急呼叫";
		case ServiceState.STATE_IN_SERVICE:
			return "信号良好";
		case ServiceState.STATE_OUT_OF_SERVICE:
			return "警告手机脱离服务区";
		default:
			return "警告手机信号未知";
		}
	}

	public static String getSimState(int simState) {
		switch (simState) {
		case 1:
			return "无SIM卡";
		case 2:
			return "SIM PIN码锁定";
		case 3:
			return "SIM PUK码锁定";
		case 4:
			return "网络 PIN码锁定";
		default:
			return "未知状态";
		}
	}

	// GSM/WCDMA
	String getGsmLevel(int asu) {
		int iconLevel;
		if (asu <= 2 || asu == 99)
			iconLevel = 0;
		else if (asu >= 12)
			iconLevel = 4;
		else if (asu >= 8)
			iconLevel = 3;
		else if (asu >= 5)
			iconLevel = 2;
		else
			iconLevel = 1;

		return iconLevel+"格信号";
	}

	// CDMA 1x
	int getCdmaLevel(int cdmaDbm, int cdmaEcio) {
		int levelDbm = 0;
		int levelEcio = 0;

		if (cdmaDbm >= -75)
			levelDbm = 4;
		else if (cdmaDbm >= -85)
			levelDbm = 3;
		else if (cdmaDbm >= -95)
			levelDbm = 2;
		else if (cdmaDbm >= -100)
			levelDbm = 1;
		else
			levelDbm = 0;

		// Ec/Io are in dB*10
		if (cdmaEcio >= -90)
			levelEcio = 4;
		else if (cdmaEcio >= -110)
			levelEcio = 3;
		else if (cdmaEcio >= -130)
			levelEcio = 2;
		else if (cdmaEcio >= -150)
			levelEcio = 1;
		else
			levelEcio = 0;

		return (levelDbm < levelEcio) ? levelDbm : levelEcio;
	}

	// CDMA 1x EV-DO
	private int getEvdoLevel(int evdoDbm, int evdoSnr) {
		int levelEvdoDbm = 0;
		int levelEvdoSnr = 0;

		if (evdoDbm >= -65)
			levelEvdoDbm = 4;
		else if (evdoDbm >= -75)
			levelEvdoDbm = 3;
		else if (evdoDbm >= -90)
			levelEvdoDbm = 2;
		else if (evdoDbm >= -105)
			levelEvdoDbm = 1;
		else
			levelEvdoDbm = 0;

		if (evdoSnr >= 7)
			levelEvdoSnr = 4;
		else if (evdoSnr >= 5)
			levelEvdoSnr = 3;
		else if (evdoSnr >= 3)
			levelEvdoSnr = 2;
		else if (evdoSnr >= 1)
			levelEvdoSnr = 1;
		else
			levelEvdoSnr = 0;
		return (levelEvdoDbm < levelEvdoSnr) ? levelEvdoDbm : levelEvdoSnr;
	}

	public static void speakPhoneState() {
		int simState = telephonyManager.getSimState();
		Logger.log("speakPhoneState", signalLevel + " ; "
				+ simState);
//		if (simState == 5) {
//			TtsSpeakRule.speaking(-1, signalLevel);
//		} else
//			TtsSpeakRule.speaking(-1, getSimState(simState));
	}

	public static String getPhoneState() {
		final int simState = telephonyManager.getSimState();
		Logger.log("getPhoneState", simState + ";"+signalLevel);
		if (simState == 5) {
			return signalLevel;
		} else
			return getSimState(simState);
	}

}