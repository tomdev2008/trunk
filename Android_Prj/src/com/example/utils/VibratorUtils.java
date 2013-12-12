package com.example.utils;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.SparseArray;

import com.example.activity.R;

/**
 *@author Administrator
 */
public class VibratorUtils {
	public static final int SFINGER_FLING_SOUND= 0;
	public static final int SFINGER_DCLICK_SOUND = 1;
	public static final int TFINGER_FLING_LEFT_SOUND = 2;
	public static final int TFINGER_FLING_RIGHT_SOUND = 3;
	public static final int TFINGER_FLING_UP_SOUND= 4;
	public static final int TFINGER_FLING_DOWN_SOUND =5;
	public static final int TFINGER_CLICK_POPOUT_SOUND= 6;
	public static final int TFINGER_CLICK_POPIN_SOUND = 7;
	public static final int INPUT_MODE_TAPUP_SOUND = 8;
	public static final int INPUT_PWD_MODE_TAPUP_SOUND= 9;
	public static final int FINGER_FLING_END_SOUND=10;
	public static final int COUNT_DOWN_SOUND=11;
	public static final int COUNT_DOWN_END_SOUND=12;
	
	private static SoundPool soundPool; 
	private static Vibrator mVibrator;//获取振动器
	private static AudioManager audioManager;
    private static SparseArray<Object> vibratorArray; 
    private static VibratorUtils vibratorUtils = new VibratorUtils();
    
    
    public static VibratorUtils getInstance(Context context) {
    	if (soundPool==null) {
    		soundPool = new SoundPool(9, AudioManager.STREAM_MUSIC, 0); 
        	mVibrator = (Vibrator)context.
        			getSystemService(Service.VIBRATOR_SERVICE);
        	audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE); 
        	vibratorArray = new SparseArray<Object>();
        	vibratorArray.put(SFINGER_FLING_SOUND, soundPool.load(context,  
                    R.raw.count_down_end_sound, 1));
		}
    	return vibratorUtils;
    }
    
    public void vibrate(int sound,boolean vibrate) { 
    	int type =Preferences.getNotification();
    	Logger.log("vibrate", type+"/"+sound);
    	if (type == 1) {
    		playSound(sound);
    	} else if (type ==2) {
    		vibrate(vibrate);
    	} else if (type ==-1) {
    		playSound(sound);
    		vibrate(vibrate);
    	}
    } 
    
    private void playSound(int sound) {
        //返回当前AlarmManager最大音量 
        float audioMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
        //返回当前AudioManager对象的音量 
        float audioCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
        float volumnRatio = audioCurrentVolume/audioMaxVolume+30; 
        soundPool.play( 
                (Integer) vibratorArray.get(sound), //播放的音乐Id  
                volumnRatio, //左声道
                volumnRatio, //右声道
                10, //优先级，0为最低
                0, //循环次数，0无不循环，1无永远循
                1);//回放速度，0.5-2.0之间为正常
    }
    
    private void vibrate(boolean vibrate) {
    	if (vibrate) 
    		mVibrator.vibrate(new long[] { 0, 18, 0, 10 }, -1);//通过设置震动的时间大小使我们可以感觉到震动的差异
    }
    
    private void timerVibrate(boolean longVibrate) {
    	if (longVibrate) 
    		mVibrator.vibrate(new long[] { 0, 50, 0, 50 }, -1);//通过设置震动的时间大小使我们可以感觉到震动的差异
    	else
    		vibrate(true);
    }
    
    public void freeVibrator(){
    	if (vibratorArray!=null) {
    		vibratorArray.clear();
    		vibratorArray=null;
		}
    	if (soundPool!=null) {
    		soundPool.autoPause();
    		soundPool=null;
		}
    	if (mVibrator!=null) {
    		mVibrator.cancel();
    		mVibrator=null;
		}
    	if (audioManager!=null) {
    		audioManager.unloadSoundEffects();
    		audioManager=null;
		}
    }
}
