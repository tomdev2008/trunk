package com.example.down;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;

import com.example.app.ExampleApp;
import com.example.imp.IHttpCallBack;
import com.example.utils.FileUtils;
import com.example.utils.Logger;
import com.example.utils.Messager;

public class Downloader {
	private final static int THREADCOUNT = 3;

	private static Downloader downloader;
	private static DownloadHelper dHelper;
	private static Map<DownloadThread, String> mThreads;
	private static Context context=ExampleApp.getInstance();

	public static Downloader getInstance(){
		if (downloader==null) {
			downloader=new Downloader();
		}
		if (dHelper==null) {
			dHelper = new DownloadHelper(context);
		}
		return downloader;
	}
	/**
	 * 多线程下载
	 * 
	 * @param path
	 *            下载路径
	 * @param thCount
	 *            需要开启多少个线程
	 * @throws Exception
	 */
	public int download(IHttpCallBack iCallBack, String downUrl)
			throws Exception {
		URL url = new URL(downUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置超时时间
		setRequestProperty(conn,downUrl);
		conn.connect();
		int fileLength = 0;
		if (conn.getResponseCode() == 200) {
			fileLength = conn.getContentLength();
			String fileName = downUrl.substring(downUrl.lastIndexOf("/") + 1);
			File downFile = new File(FileUtils.getDownPath(), fileName);
			Logger.log(this, "download",
					downFile.getName() + "/" + downFile.length());
			if (downFile.exists()) {
				boolean delete = downFile.delete();
				Logger.log(this, "download delete", delete);
			}
			RandomAccessFile accessFile = new RandomAccessFile(downFile, "rwd");
			accessFile.setLength(fileLength);
			accessFile.close();
			conn.disconnect();
			// 计算每个线程下载的字节数
			int partLen = (fileLength % THREADCOUNT) == 0 ? fileLength
					/ THREADCOUNT : fileLength / THREADCOUNT + 1;
			for (int i = 0; i < THREADCOUNT; i++) {
				new DownloadThread(iCallBack,downFile,downUrl,partLen, i).start();
			}
		} else {
			iCallBack.onError(downUrl, Messager.MSG_SERVICE_UNAVAILABLE);
		}
		return fileLength;
	}

	private final class DownloadThread extends Thread {
		private int threadId;
		private int completed;
		private int partLength;
		private int startPos, endPos;
		
		private File downFile;
		private String downUrl;
		private boolean isPause;
		private IHttpCallBack iCallBack;

		public DownloadThread(IHttpCallBack iCallBack,File downFile,String downUrl, int partLength,
				int threadId) {
			this.iCallBack = iCallBack;
			this.downFile=downFile;
			this.downUrl=downUrl;
			this.threadId = threadId;
			this.partLength = partLength;
		}
		
		public boolean isPause() {
			return isPause;
		}

		public void setPause(boolean isPause) {
			this.isPause = isPause;
		}
		/**
		 * 写入操作
		 */
		public void run() {
			// 判断上次是否有未完成任务
			CompletedEntity completedEntity = dHelper.getCompleted(
					String.valueOf(threadId), downUrl);
			if (completedEntity != null) {
				// 如果有, 读取当前线程已下载量
				completed = completedEntity.getCompleted();
				startPos = threadId * partLength + completed;
			} else {
				// 如果没有, 则创建一个新记录存入
				startPos = threadId * partLength;
				dHelper.setCompleted(threadId, completed, downUrl);
			}
			endPos = (threadId + 1) * partLength - 1;
			Logger.log(this, "run", startPos + "/" + endPos);
			try {
				URL url = new URL(downUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(60 * 1000); // 连接超时3分钟
				conn.setRequestProperty("Range", "bytes=" + startPos + "-"
						+ endPos);
				RandomAccessFile accessFile = new RandomAccessFile(downFile,
						"rwd");
				accessFile.seek(startPos);
				InputStream iStream = conn.getInputStream();
				byte[] buf = new byte[1024*5];
				int length;
				while ((length = iStream.read(buf)) != -1) {
					completed += length;
					accessFile.write(buf, 0, length);
					iCallBack.handleMessage(Messager.getMessage(
							Messager.MSG_DOWNLOAD, length, downUrl));
					dHelper.updateCompleted(threadId, completed, downUrl);
					Logger.log(this, "downing", threadId + "===" + completed
							+ "===" + endPos);
					if (isPause()) {
						break;
					}
				}
				iStream.close();
				accessFile.close();
				conn.disconnect();
				Logger.log(this, "down over", threadId + "downUrl");
				if (!isPause()) {
					dHelper.deleteCompleted(threadId, downUrl);
				}

			} catch (IOException e) {
				e.printStackTrace();
				iCallBack.onError(downUrl, -1);
			} finally {
			}
		}
	}

	private void setRequestProperty(HttpURLConnection conn,String downUrl) {
		conn.setConnectTimeout(5 * 1000);
		try {
			conn.setRequestMethod("GET");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		conn.setRequestProperty(
				"Accept","image/gif, image/jpeg, image/pjpeg, image/pjpeg, " +
				"application/x-shockwave-flash, application/xaml+xml, " +
				"application/vnd.ms-xpsdocument, application/x-ms-xbap, " +
				"application/x-ms-application, application/vnd.ms-excel, " +
				"application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Referer", downUrl);
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty(
				"User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; " +
				".NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; " +
				".NET CLR 3.5.30729)");
		conn.setRequestProperty("Connection", "Keep-Alive");
	}

	// 暂停下载
	public void pause(String downUrl) {
		for (Entry<DownloadThread, String> entry:mThreads.entrySet()) {
			if (entry.getValue().equals(downUrl)) {
				entry.getKey().setPause(true);
			}
		}
	}

}