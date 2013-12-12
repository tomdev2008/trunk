package com.example.httper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.os.Message;

import com.example.entity.FormEntity;
import com.example.imp.IHttpCallBack;
import com.example.utils.Logger;
import com.example.utils.Messager;

public class HttpHandler {

	public static final String CONTENT_TYPE = "text/xml; charset=utf-8";
	public static final String CONTENT_TYPE1 = "application/x-www-form-urlencoded";
	public static final String BOUNDARY = "---------------------------7da2137580612";
	public static final String CONTENT_TYPE2 = "multipart/form-data; boundary="
			+ BOUNDARY;

	public static final String REQ_URL = "http://www.test.com";
	public static final String CONN_ENCODE = "UTF-8";

	public static final int HTTP_LOGIN = 0;
	public static final int HTTP_REGISTER = 1;
	public static final int HTTP_UPLOAD = 2;
	public static final int HTTP_DOWNLOAD = 3;
	public static final int CONN_TIMEOUT = 30000;

	public static void handleMessage(Message msg) {
//		if (!NetworkReceiver.getWifiState()) {
//			return;
//		}
		final int what = msg.what;
		Logger.log("HttpHandler", "handleMessage", what);
		final IHttpCallBack iCallback = (IHttpCallBack) msg.obj;
		final Bundle bundle = msg.getData();
		switch (what) {
		case HTTP_LOGIN:
			login(what, iCallback, bundle);
			break;
		case HTTP_REGISTER:
			register(what, iCallback, bundle);
			break;
		default:
			break;
		}
	}

	public static void login(int what, IHttpCallBack iCallback, Bundle bundle) {
		Logger.log("login", iCallback);
		executeHttpClient(what, iCallback, bundle);
	}

	public static void register(int what, IHttpCallBack iCallback, Bundle bundle) {
		Logger.log("register", iCallback);
		executePost(what, iCallback, bundle);
	}

	private static void executePost(int what, IHttpCallBack iCallback,
			Bundle bundle) {
		InputStreamReader iReader = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(REQ_URL + getReqestParams(bundle));
			connection = (HttpURLConnection) url.openConnection();
			setHttpProperty(connection, "POST", CONTENT_TYPE1);
			DataOutputStream dStream = new DataOutputStream(
					connection.getOutputStream());
			dStream.writeBytes("token=alexzhou");
			dStream.flush();
			dStream.close();
			iReader = new InputStreamReader(connection.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(iReader);
			StringBuffer strBuffer = new StringBuffer();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				strBuffer.append(line);
			}
			iCallback.handleMessage(Messager.getMessage(what,
					strBuffer.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			iCallback.onError(what, -1);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (iReader != null) {
				try {
					iReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 发送xml数据
	 * 
	 * @param path
	 *            请求地址
	 * @param xml
	 *            xml数据
	 * @param encoding
	 *            编码
	 * @return
	 * @throws Exception
	 */
	public static byte[] postXml(String path, String xml, String encoding)
			throws Exception {
		byte[] data = xml.getBytes(encoding);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", CONTENT_TYPE);
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		conn.setConnectTimeout(5 * 1000);
		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		outStream.close();
		if (conn.getResponseCode() == 200) {
			return readStream(conn.getInputStream());
		}
		return null;
	}

	/**
	 * 读取流
	 * 
	 * @param inStream
	 * @return 字节数组
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	public static void executeGet(int what, IHttpCallBack iCallback,
			Bundle bundle) {
		InputStreamReader iReader = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(REQ_URL);
			connection = (HttpURLConnection) url.openConnection();
			iReader = new InputStreamReader(connection.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(iReader);
			StringBuffer strBuffer = new StringBuffer();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				strBuffer.append(line);
			}
			iCallback.handleMessage(Messager.getMessage(what,
					strBuffer.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			iCallback.onError(what, -1);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (iReader != null) {
				try {
					iReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void executeHttpClient(int what, IHttpCallBack iCallback,
			Bundle bundle) {
		BufferedReader reader = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost();
			request.setURI(new URI(REQ_URL));
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					getNameValuePair(bundle), CONN_ENCODE);
			request.setEntity(formEntity);
			HttpResponse response = client.execute(request);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			StringBuffer strBuffer = new StringBuffer("");
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuffer.append(line);
			}
			iCallback.handleMessage(Messager.getMessage(what,
					strBuffer.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			iCallback.onError(what, -1);
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能的上传: <FORM METHOD=POST ACTION="xxx"
	 * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
	 * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
	 * type="file" name="zip"/> </FORM>
	 * 
	 */
	public static void post(IHttpCallBack iCallBack, Bundle bundle) {
		String userName = bundle.getString("userName");
		String uploadUrl = bundle.getString("uploadUrl");
		FormEntity formEntity = (FormEntity) bundle.getSerializable("entity");
		File uploadFile = formEntity.getFormFile();
		Logger.log("post", userName+","+uploadUrl+","+uploadFile.getAbsolutePath());
		// 组装表单字段和文件之前的数据
		StringBuilder sb = new StringBuilder();

		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data; name=\"username\"" + "\r\n");
		sb.append("\r\n");
		sb.append(userName + "\r\n");

		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
				+ uploadFile.getAbsolutePath() + "\"" + "\r\n");
		sb.append("Content-Type:" + formEntity.getFormType() + "\r\n");
		sb.append("\r\n");

		// 文件之前的数据
		try {
			byte[] before = sb.toString().getBytes("utf-8");
			// 文件之后的数据
			byte[] after = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");

			URL url = new URL(uploadUrl);
			// 建立一个Socket链接
			Socket socket = new Socket(url.getHost(), url.getPort());
			OutputStream oStream = socket.getOutputStream();
			// 获得一个输出流（从Android流到web）
			PrintStream pStream = new PrintStream(socket.getOutputStream(), true,
					"utf-8");
			// 写出请求头
			pStream.println("POST " + url.getPath() + " HTTP/1.1");
			pStream.println("Content-Type: " + CONTENT_TYPE2);
			pStream.println("Content-Length: "
					+ String.valueOf(before.length + uploadFile.length()+ after.length));
			InputStream iStream = new FileInputStream(uploadFile);
			byte[] buf = new byte[1024];
			int len;oStream.write(before);
			while ((len = iStream.read(buf)) != -1)
				oStream.write(buf, 0, len);
			oStream.write(after);
			iStream.close();
			oStream.close();
			socket.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 拼凑请求参数
	private static List<NameValuePair> getNameValuePair(Bundle bundle) {
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		Set<String> keySet = bundle.keySet();
		for (String key : keySet) {
			String value = (String) bundle.get(key);
			formParams.add(new BasicNameValuePair(key, value));
		}
		Logger.log("getNameValuePair", formParams);
		return formParams;
	}

	// 拼凑请求参数
	private static String getReqestParams(Bundle bundle) {
		String formParams = "?";
		Set<String> keySet = bundle.keySet();
		for (String key : keySet) {
			String value = (String) bundle.get(key);
			formParams = formParams + key + "=" + value + "&";
		}
		int index = formParams.lastIndexOf("&");
		formParams = formParams.substring(0, index);
		Logger.log("getReqestParams", formParams);
		return formParams;
	}

	private static void setHttpProperty(HttpURLConnection connection,
			String method, String contentType) throws ProtocolException {
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod(method);
		connection.setRequestProperty("Charset", "utf-8");
		connection.setConnectTimeout(CONN_TIMEOUT);
		connection
				.setRequestProperty(
						"Accept",
						"image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
								+ "application/x-shockwave-flash, application/xaml+xml, "
								+ "application/vnd.ms-xpsdocument, application/x-ms-xbap,"
								+ " application/x-ms-application, application/vnd.ms-excel, "
								+ "application/vnd.ms-powerpoint, application/msword, */*");
		connection
				.setRequestProperty(
						"User-Agent",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; "
								+ ".NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; "
								+ ".NET CLR 3.5.30729)");
		connection.setRequestProperty("Accept-Language", "zh-CN");

		/*
		 * @text/xml; charset=utf-8
		 * 
		 * @1"application/x-www-form-urlencoded"
		 * 
		 * @2"multipart/form-data; boundary=" + BOUNDARY
		 */
		connection.setRequestProperty("Content-Type", contentType);
		connection.setRequestProperty("Connection", "Keep-Alive");
	}

}
