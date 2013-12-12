package com.example.chat;

import java.io.BufferedOutputStream;
import java.io.IOException;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.example.utils.Messager;

public final class RecordThread extends Thread {
	
	// 到达该值之后 触发事件
	private final static int BLOW_BOUNDARY = 30; 
	// 录制缓冲大小
	private int bufferSize; 
	private boolean isPressing;
	private IChatCallBack iCallBack;
	private AudioRecord audioRecord;
	private BufferedOutputStream output;
	

	public RecordThread(IChatCallBack iCallBack, BufferedOutputStream output) {
		this.output = output;
		this.iCallBack = iCallBack;
		bufferSize = AudioRecord.getMinBufferSize(ChatManager.SAMPLE_RATE_IN_HZ,
				ChatManager.CHANNEL_CONFIG,ChatManager. AUDIO_FORMAT);
		// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
		// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				ChatManager.SAMPLE_RATE_IN_HZ,ChatManager.CHANNEL_CONFIG,
				ChatManager.AUDIO_FORMAT, bufferSize);
	}

	public void run() {
		int total = 0,number = 0;
		long time = 0,curTime=0,endTime = 0;
		audioRecord.startRecording();
		byte[] buffer = new byte[bufferSize];
		try {
			while (isPressing) {
				number++;
				curTime = System.currentTimeMillis();
				int r = audioRecord.read(buffer, 0, bufferSize);// 读取到的数据
				int v = 0;
				for (int i = 0; i < buffer.length; i++) {
					v += Math.abs(buffer[i]);// 取绝对值，因为可能为负
				}
				output.write(buffer, 0, r);
				int value = Integer.valueOf(v / r);// 算得当前所有值的平均值
				total = total + value;
				endTime = System.currentTimeMillis();
				time = time + (endTime - curTime);
				// 如果间隔时间大于500毫秒或者次数多于5次，才处理音频数据
				if (time >= 500 || number > 5) {
					int valume = total / number;
					total = 0;time = 0;number = 0;
					// 声音的大小达到一定的值
					if (valume > BLOW_BOUNDARY) {
						// 发送消息通知到界面 触发动画
						iCallBack.handleMessage(Messager.getMessage(4, valume));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
				audioRecord.stop();
				audioRecord.release();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopRecording() {
		isPressing = false;
		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
