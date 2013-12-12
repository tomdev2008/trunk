package com.example.test;

import junit.framework.Test;
import android.test.AndroidTestRunner;

public class TestSqlite extends AndroidTestRunner{

	@Override
	public synchronized void endTest(Test test) {
		super.endTest(test);
	}

	@Override
	public synchronized void startTest(Test test) {
		super.startTest(test);
	}
	
}
