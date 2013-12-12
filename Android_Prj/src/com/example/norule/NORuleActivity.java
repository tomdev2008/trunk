package com.example.norule;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.example.activity.R;
import com.example.cache.ImageLoader;
import com.example.imp.IHttpCallBack;
import com.example.norule.NORuleView.OnScrollListener;
import com.example.utils.Logger;
import com.example.utils.Messager;

public class NORuleActivity extends Activity implements 
	OnScrollListener,OnTouchListener,IHttpCallBack{
	private final static int LOAD_IMG_LIMIT=1000;
	
	private int item_width;
	private int scroll_height;
	private int current_page = 0;
	private int loaded_count = 0;
	private int page_count = 30;
	private int column_count =3;

	
	private ImageLoader imgLoader;
	
	private int[] topIndex=new int[3];
	/**
	 * 表示当前瀑布流最底部的三张图的索引 
	 * 比如第一列有21张图 索引为20 第二列有19张
	 *  索引为18 第三列有22张 索引为21
	 */
	private int[] bottomIndex=new int[3];
	
	/**
	 * 每列当前图片的索引,相当于临时变量 
	 * 比如第一列的第9张图 lineIndex[0]=9;
	 */
	private int[] lineIndex=new int[3]; 
	
	/**
	 *  每列总共的高度 当前列加入10张图时,
	 *  为10张图的总高度
	 */
	private int[] columnHeight=new int[3];
	/**
	 * 存放column_count个LinearLayout
	 *  每个LinearLayout表示一列
	 */
	private ArrayList<LinearLayout> lRuleItems;
	
	/**
	 * 存column_count列数组 存放每列每张图的总高度 
	 * 比如第一列第一张图高90  总高度为90、第一列第2张图高120 总高度为210... 
	 */
	private SparseArray<Integer>[] sArrayHeight ;
	
	private final static	 String[] URLS = {
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
			"http://comic.sinaimg.cn/2011/0824/U5237P1157DT20110824161051.jpg"};
	
	private NORuleView noRuleView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		noRuleView=new NORuleView(this);
		setContentView(R.layout.norule_layout);
		initVarible();
		generateLayout();
	}

	@SuppressLint("UseSparseArrays")
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void initVarible(){
		imgLoader=new ImageLoader(getApplicationContext());
		Display display =getWindowManager().getDefaultDisplay();
		item_width = display.getWidth() / column_count;// 根据屏幕大小计算每列大小
		lRuleItems = new ArrayList<LinearLayout>();
		sArrayHeight = new SparseArray[column_count];
		for (int i = 0; i < column_count; i++) {
			lineIndex[i] = -1;bottomIndex[i] = -1;
			sArrayHeight[i] = new SparseArray<Integer>();
		}
	}
	
	/**
	 * 生成无规则布局
	 */
	private void generateLayout() {
		Logger.log(this,"generateLayout", noRuleView);
		LinearLayout ll_norule = (LinearLayout)findViewById(R.id.ll_norule);
		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					item_width, LayoutParams.WRAP_CONTENT);
			itemLayout.setPadding(2, 2, 2, 2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setLayoutParams(itemParam);
			ll_norule.addView(itemLayout);
			lRuleItems.add(itemLayout);
		}
		// 第一次加载
		addItemToContainer(current_page, page_count);
		noRuleView.setOnScrollListener(this);
	}

	/**
	 * @param pageIndex
	 * @param pageCount
	 */
	private void addItemToContainer(int pageIndex, int pageCount) {
		Logger.log(this, "addItemToContainer", pageIndex+"/"+pageCount);
		Random rand = new Random();
		int currentIndex = pageIndex * pageCount;
		for (int i = currentIndex; i < pageCount * (pageIndex + 1)
				&& i < LOAD_IMG_LIMIT; i++) {
			loaded_count++;
			int r = rand.nextInt(URLS.length);
			addImage(URLS[r],(int) Math.ceil(loaded_count / (double) column_count));
		}

	}

	/**
	 * @param filename
	 * @param rowIndex
	 * @param id
	 */
	private void addImage(String imgUrl, int rowIndex) {
		Logger.log(this, "addImage", imgUrl+"/"+rowIndex);
		NORuleItem itemRuleItem= new NORuleItem(getApplicationContext());
		itemRuleItem.setRowIndex(rowIndex);
		itemRuleItem.setItemWidth(item_width);
		itemRuleItem.setImgUrl(imgUrl);
		itemRuleItem.loadImage(imgLoader);
	}

	/**
	 * @param array
	 * @return
	 */
	private int getMinValue(int[] array) {
		Logger.log(this, "getMinValue", array);
		int m = 0;
		int length = array.length;
		for (int i = 0; i < length; ++i) {
			if (array[i] < array[m]) {
				m = i;
			}
		}
		return m;
	}


	@Override
	public void onTop() {
		// 滚动到最顶端
		Logger.log(this, "onTop");
	}

	@Override
	public void onScroll() {
		Logger.log(this, "onScroll");
	}

	@Override
	public void onBottom() {
		// 滚动到最低端
		Logger.log(this, "onBottom");
		addItemToContainer(++current_page, page_count);
	}

	@Override
	public void onAutoScroll(int l, int t, int oldl, int oldt) {
		scroll_height =noRuleView.getMeasuredHeight();
		Logger.log(this, "onAutoScroll" + scroll_height);

		if (t > oldt) {// 向下滚动
			if (t > 2 * scroll_height) {// 超过两屏幕后
				for (int k = 0; k < column_count; k++) {
					if (sArrayHeight[k].get(Math.min(bottomIndex[k] + 1,
							lineIndex[k])) <= t + 3 * scroll_height) {// 最底部的图片位置小于当前t+3*屏幕高度

						((NORuleItem) lRuleItems.get(k).getChildAt(
								Math.min(1 + bottomIndex[k],
										lineIndex[k]))).loadImage(imgLoader);

						bottomIndex[k] = Math.min(1 + bottomIndex[k],
								lineIndex[k]);

					}
					Logger.log("NORuleActivity",
							"onAutoScroll/" + topIndex[k] + "  footIndex:"
									+ bottomIndex[k] + "  headHeight:"
									+ sArrayHeight[k].get(topIndex[k]));
					if (sArrayHeight[k].get(topIndex[k]) < t - 2
							* scroll_height) {// 未回收图片的最高位置<t-两倍屏幕高度
						topIndex[k]++;
						Logger.log("MainActivity", "recycle,k:" + k
								+ " headindex:" + topIndex[k]);

					}
				}

			}
		} else {// 向上滚动
			if (t > 2 * scroll_height) {// 超过两屏幕后
				for (int k = 0; k < column_count; k++) {
					LinearLayout localLinearLayout = lRuleItems
							.get(k);
					if (sArrayHeight[k].get(bottomIndex[k]) > t + 3
							* scroll_height) {
						bottomIndex[k]--;
					}
					if (sArrayHeight[k].get(Math.max(topIndex[k] - 1, 0)) >= t
							- 2 * scroll_height) {
						((NORuleItem) localLinearLayout.getChildAt(Math
								.max(-1 + topIndex[k], 0))).loadImage(imgLoader);
						topIndex[k] = Math.max(topIndex[k] - 1, 0);
					}
				}
			}
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		Logger.log("NORuleActivity", "handleMessage", msg.what);
		switch (msg.what) {
		case 1:
			NORuleItem noRuleItem = (NORuleItem) msg.obj;
			int columnIndex = getMinValue(columnHeight);
			noRuleItem.setColumnIndex(columnIndex);
			lineIndex[columnIndex]++;
			columnHeight[columnIndex] +=  msg.arg2;
			lRuleItems.get(columnIndex).addView(noRuleItem);
			sArrayHeight[columnIndex].put(lineIndex[columnIndex],
					columnHeight[columnIndex]);
			bottomIndex[columnIndex] = lineIndex[columnIndex];
			break;
		case 2:
			// getScrollY得到当前滑块位置和顶部的差值
			// 得到scroll在手机屏幕上的高度.最大为手机屏幕高度
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int scrollY= noRuleView.getScrollY();
			int viewHeight=noRuleView.getHeight();
			int measuredHeight=noRuleView.getMeasuredHeight();
			if (measuredHeight - 20 <= scrollY+ viewHeight) {
				onBottom();
			} else if (scrollY == 0) {
				onTop();
			} else {
				// 发现调用方没有实现
				onScroll();
			}
			break;
		default:
			break;
		}
		
		return false;
	}

	@Override
	public void onError(Object what, int errorCode) {
		Logger.log("NORuleActivity", "onError",what);
	}

	@Override
	public boolean onTouch(View paramView, MotionEvent event) {
		Logger.log("NORuleActivity", "onTouch",event.getAction());
		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:// 用户弹出界面后，200毫秒通过调用层滑动发生改变
				handleMessage(Messager.getMessage(2,200));
				break;
			default:
				break;
		}
		return false;
	}

}
