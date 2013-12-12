package com.example.atask;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.AsyncTask;

import com.example.activity.R;
import com.example.utils.Logger;

public class DBInstallTask extends AsyncTask<String, Integer, Integer> {
	private Context context;
	public DBInstallTask(Context context) {
		super();
		this.context=context;
	}

	@Override
	protected Integer doInBackground(String... params) {
		Logger.log(this, "doInBackground", params[0]);
		String dbPathString = params[0];
		int readCount = 0, readSum = 0;
		byte[] buffer = new byte[1024];
		InputStream inputStream;
		OutputStream output;
		try {
			inputStream = context.getResources().openRawResource(R.raw.test); 
			output = new FileOutputStream(dbPathString);
			BufferedInputStream b = new BufferedInputStream(
			inputStream);
			while ((readCount = b.read(buffer)) != -1) {
				// readCount = b.read(buffer);
				output.write(buffer, 0, readCount);
				readSum = readSum + readCount;
				publishProgress(readSum);
			}
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

	}
}
