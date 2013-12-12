package com.example.activity;

import com.example.adapter.TestAdapter;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class TestActivity extends ExampleActivity implements OnClickListener,OnScrollListener {
	private TestAdapter testAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
		ListView listView=(ListView) findViewById(R.id.lv_test);
		listView.setAdapter(new TestAdapter(this,getImages()));
	}
	
	private String[] getImages(){
		String[] URLS = {
			"http://cms.csdnimg.cn/article/201309/18/52391d0194a1c.jpg",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-01.png",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-02.png",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-03.png",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-04.png",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-05.png",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-06.png",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-07.png",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-08.png",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-09.png",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-10.png",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-11.png",
			"http://10.0.4.223:8080/WebLogin/images/chocolate_hearts-12.png",
			"http://10.0.4.223:8080/WebLogin/images/img0.jpg",
			"http://10.0.4.223:8080/WebLogin/images/img1.jpg",
			"http://10.0.4.223:8080/WebLogin/images/img2.jpg",
			"http://10.0.4.223:8080/WebLogin/images/img3.jpg",
			"http://10.0.4.223:8080/WebLogin/images/img4.jpg",
			"http://10.0.4.223:8080/WebLogin/images/img5.jpg",
			"http://10.0.4.223:8080/WebLogin/images/img6.jpg",
			"http://10.0.4.223:8080/WebLogin/images/img7.jpg",
			"http://10.0.4.223:8080/WebLogin/images/img8.jpg",
			"http://10.0.4.223:8080/WebLogin/images/img9.jpg",
			"http://10.0.4.223:8080/WebLogin/images/img10.jpg",
			"http://10.0.4.223:8080/WebLogin/images/img11.jpg",
			"http://comic.sinaimg.cn/2011/0824/U5237P1157DT20110824161051.jpg",
			"http://lh4.ggpht.com/_7V85eCJY_fg/TBpXudG4_PI/AAAAAAAAPEE/8cHJ7G84TkM/s144-c/20100530_120257_0273-Edit-2.jpg" };
		return URLS;
	}

	@Override
	public void onClick(View arg0) {
//		requester.login(this, getBundle("123", "123456"));
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING:
			testAdapter.setLoadingBusy(true);
			break;
		case OnScrollListener.SCROLL_STATE_IDLE:
			testAdapter.setLoadingBusy(false);
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			testAdapter.setLoadingBusy(false);
			break;
		default:
			break;
		}
		testAdapter.notifyDataSetChanged();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}
	
}
