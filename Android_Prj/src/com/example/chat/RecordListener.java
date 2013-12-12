package com.example.chat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.example.activity.R;
import com.example.app.ExampleApp;
import com.example.utils.FileUtils;

/**
 * 发送语音消息监听类
 * @author Administrator
 */
public class RecordListener implements OnTouchListener{
	private long startTime;
	private File recordFile;
	private String user_name;
	private Button btn_record;
	private IChatCallBack iCallBack;
	private ChatManager chatManager;
	private MediaPlayer mediaPlayer;
	private MediaRecorder mRecorder;
	
	private RecordThread recordThread;
	private BufferedOutputStream dStream;
	
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public Button getBtn_record() {
		return btn_record;
	}
	public void setBtn_record(Button btn_record) {
		this.btn_record = btn_record;
	}
	public RecordListener(IChatCallBack iCallBack) {
		this.iCallBack=iCallBack;
		chatManager=ChatManager.getInstance(iCallBack);
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		if (!FileUtils.storageState()) {
			return false;
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				setRecordState(false);
				startRecording();
				break;
			case MotionEvent.ACTION_UP:
				setRecordState(true);
				stopRecording();
				break;
		}
		return true;
	}
	
	private void setRecordState(boolean release){
		if (!release) {
			btn_record.setText(R.string.btn_press_voice);
			btn_record.setBackgroundResource(R.drawable.normal_button_blue);
		}else
		{
			btn_record.setText(R.string.btn_normal_voice);
			btn_record.setBackgroundResource(R.drawable.normal_button);
		}
	}
	
	private void startRecording(){
		mediaPlayer = MediaPlayer.create(ExampleApp.getInstance(), R.raw.play_completed);
		mediaPlayer.start();
		recordFile = new File(FileUtils.getVoicePath(),System.currentTimeMillis()+".pcm");
		try {
			if (!recordFile.exists()) {
				recordFile.createNewFile();
			}
			dStream = new  BufferedOutputStream(new FileOutputStream(recordFile));
			Thread.sleep(500);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}  
//		recordDialog.show();
		//开启声音捕捉监听线程
		recordThread = new RecordThread(iCallBack,dStream);
		recordThread.start();
		startTime = System.currentTimeMillis();
	}
	
	private void stopRecording(){
		recordThread.stopRecording();
		mediaPlayer.release();
		
//		recordDialog.dismiss();
		//关闭声音捕捉监听线程
		long endTime = System.currentTimeMillis();
		long recordTime = endTime - startTime;
		if(recordTime < 1000){
			Message message =Message.obtain();
			message.what=-1;
			iCallBack.handleMessage(message);
		}else{
			chatManager.chattingVoice(iCallBack,user_name,recordFile, recordTime);
		}
		clearVarible();
	}
	
	private void clearVarible(){
		startTime=0;
		if(null!= recordFile)
			recordFile.delete();
		if(null!= mRecorder) 
			mRecorder.release();
		if(null!= mediaPlayer) 
			mediaPlayer.release();
		mRecorder=null;
		mediaPlayer = null;
		recordThread=null;
		recordFile.delete();
		if (dStream!=null) {
			try {
				dStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
