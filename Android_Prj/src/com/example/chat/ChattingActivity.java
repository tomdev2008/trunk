package com.example.chat;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.activity.R;
import com.example.entity.UserEntity;
import com.example.utils.Expression;

public class ChattingActivity extends BaseActivity implements IChatCallBack,
		OnClickListener, OnItemClickListener, OnTouchListener {
	// 表情的数量
	public final static int EXPRESS_COUNTS = 107;
	
	private ListView lv_chattings;
	private EditText et_chatInput;
	private ImageView iv_record;
	private Button btn_send, btn_record;
	private ImageButton ib_convert, ib_express;

	private View record_view;
	private ViewPager viewPager;
	private LayoutInflater inflater;

	private FrameLayout fl_express;
	private RelativeLayout rl_express;
	private LinearLayout ll_convert, ll_record;

	private UserEntity userEntity;
	private ChatManager chatManager;
	private ChattingAdapter chattingAdapter;

	private ArrayList<ChatEntity> lChatEntities; // 保存所有消息内容
	private int imageIds[] = Expression.getExpressRcIds(107); // 保存所有表情资源的id

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatting_layout);
		createTitle(R.string.app_name, true, true);
		setLeftButton(R.string.click_back);
		setRightButton(R.string.login);
		initVariable();
		createChatView();
		createInputView();
		createRecordView();
	}

	private void initVariable() {
		inflater = this.getLayoutInflater();
		userEntity = (UserEntity) getIntent()
				.getSerializableExtra("userEntity");
		lChatEntities = new ArrayList<ChatEntity>();
		chatManager = ChatManager.getInstance(this);
	}

	private void createChatView() {
		lv_chattings = (ListView) findViewById(R.id.lv_chatting);
		chattingAdapter = new ChattingAdapter(this, userEntity, lChatEntities);
		lv_chattings.setAdapter(chattingAdapter);
		lv_chattings.setOnTouchListener(this);
		lv_chattings.setOnItemClickListener(this);
	}

	private void createInputView() {
		fl_express = (FrameLayout) findViewById(R.id.fl_express);
		rl_express = (RelativeLayout) findViewById(R.id.rl_express);
		ll_record = (LinearLayout) findViewById(R.id.ll_record);
		ll_convert = (LinearLayout) findViewById(R.id.ll_convert);
		btn_send = (Button) findViewById(R.id.btn_send);
		ib_convert = (ImageButton) findViewById(R.id.ib_convert);
		ib_express = (ImageButton) findViewById(R.id.ib_express);
		et_chatInput = (EditText) findViewById(R.id.et_chatInput);
		viewPager = (ViewPager) findViewById(R.id.vp_express);
		btn_record = (Button) findViewById(R.id.btn_record);
		btn_record.setOnTouchListener(new RecordListener(this));
		btn_send.setOnClickListener(this);
		ib_convert.setOnClickListener(this);
		ib_express.setOnClickListener(this);
	}

	private void createRecordView() {
		record_view = inflater.inflate(R.layout.record_layout, null);
		iv_record = (ImageView) record_view.findViewById(R.id.iv_record);
	}

	/**
	 * 显示表情对话框
	 * 
	 * @param view
	 */
	@SuppressWarnings("deprecation")
	public void showFaceWindow(View view) {
		// 判断软键盘是否打开
		hideSoftInput(view);
		// 显示表情对话框
		fl_express.setVisibility(View.VISIBLE);
		// 获取屏幕当前分辨率
		Display currDisplay = getWindowManager().getDefaultDisplay();
		int displayWidth = currDisplay.getWidth();
		// 获得表情图片的宽度/高度
		Bitmap express = BitmapFactory.decodeResource(getResources(),
				R.drawable.f000);
		int headWidth = express.getWidth();
		int headHeight = express.getHeight();
		final int colmns = displayWidth / headWidth > 7 ? 7 : displayWidth
				/ headWidth; // 每页显示的列数
		final int rows = 170 / headHeight > 3 ? 3 : 170 / headHeight; // 每页显示的行数
		final int pageItemCount = colmns * rows; // 每页显示的条目数
		// 计算总页数
		int totalPage = EXPRESS_COUNTS % pageItemCount == 0 ? EXPRESS_COUNTS
				/ pageItemCount : EXPRESS_COUNTS / pageItemCount + 1;
		final List<View> listView = new ArrayList<View>();
		for (int index = 0; index < totalPage; index++) {
			listView.add(getPagerItem(index, colmns, pageItemCount));
		}
		rl_express.removeAllViews();
		for (int i = 0; i < totalPage; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setId(i + 1);
			if (i == 0) {
				imageView.setBackgroundResource(R.drawable.d2);
			} else {
				imageView.setBackgroundResource(R.drawable.d1);
			}
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			layoutParams.bottomMargin = 20;
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			if (i != 0) {
				layoutParams.addRule(RelativeLayout.ALIGN_TOP, i);
				layoutParams.addRule(RelativeLayout.RIGHT_OF, i);
			}
			rl_express.addView(imageView, layoutParams);
		}
		// 填充viewPager的适配器
		viewPager.setAdapter(new PagerAdapter() {
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			public int getCount() {
				return listView.size();
			}

			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(listView.get(position));
			}

			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(listView.get(position));
				return listView.get(position);
			}
		});
		viewPager.setOnPageChangeListener(new PageChangeListener());
	}

	private final class PageChangeListener implements OnPageChangeListener {
		private int curIndex = 0;

		public void onPageSelected(int index) {
			rl_express.getChildAt(curIndex)
					.setBackgroundResource(R.drawable.d1);
			rl_express.getChildAt(index).setBackgroundResource(R.drawable.d2);
			curIndex = index;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	}

	private View getPagerItem(final int index, int colums,
			final int pageItemCount) {
		View express_view = inflater.inflate(R.layout.express_gview, null);
		GridView gridView = (GridView) express_view
				.findViewById(R.id.gv_express);
		gridView.setNumColumns(colums);
		gridView.setAdapter(new FacesAdapter(index, pageItemCount, imageIds,
				inflater));
		// 注册监听事件
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int positon, long id) {
				Bitmap bitmap = null;
				int start = index * pageItemCount; // 起始位置
				positon = positon + start;
				bitmap = BitmapFactory.decodeResource(getResources(),
						imageIds[positon]);
				ImageSpan imageSpan = new ImageSpan(bitmap);
				String str = "";
				if (positon < 10) {
					str = "[f00" + positon + "]";
				} else if (positon < 100) {
					str = "[f0" + positon + "]";
				} else {
					str = "[f" + positon + "]";
					;
				}
				SpannableString spannableString = new SpannableString(str);
				spannableString.setSpan(imageSpan, 0, str.length(),
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				et_chatInput.append(spannableString);
				fl_express.setVisibility(View.GONE);
			}
		});
		return express_view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.et_chatInput:
			if (fl_express.getVisibility() == View.VISIBLE) {
				fl_express.setVisibility(View.GONE);
			}
			break;
		case R.id.ib_convert:
			if (ll_record.getVisibility() == 0) {
				ll_convert.setVisibility(View.VISIBLE);
				ll_record.setVisibility(View.GONE);
				ib_convert.setImageResource(R.drawable.d_convert_voice);
			} else {
				ll_convert.setVisibility(View.GONE);
				ll_record.setVisibility(View.VISIBLE);
				ib_convert.setImageResource(R.drawable.d_convert_keyboard);
			}
			break;
		case R.id.ib_express:
			if (fl_express.getVisibility() == View.VISIBLE) {
				fl_express.setVisibility(View.GONE);
			} else {
				showFaceWindow(v);
			}
			break;
		case R.id.btn_send:
			String content = et_chatInput.getText().toString(); // 待发送的消息
			if (content == null || content.equals("")) {
				toast(R.string.tip_input);
				return;
			}
			chatManager.chattingText(userEntity.getUser_name(), content);
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long arg3) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.lv_chatting) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				hideSoftInput(v);
			}
		}
		return false;
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param view
	 */
	public void hideSoftInput(View view) {
		InputMethodManager manager = (InputMethodManager) this
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		if (manager.isActive()) {
			manager.hideSoftInputFromWindow(et_chatInput.getWindowToken(), 0);
		}
	}

	@Override
	public void onError(int errorCode) {
		switch (errorCode) {
		case ChatManager.CHAT_LOGIN_ERROR:
			break;
		case ChatManager.CHAT_CONN_ERROR:
			break;
		case ChatManager.CHAT_CHATTING_ERROR:
			break;
		default:
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case 1:
			ChatEntity chatEntity = (ChatEntity) msg.obj;
			if (userEntity.getUser_name().equals(chatEntity.getChat_target())) {
				// view_loginer.setText(userEntity.getUser_name());
				// view_image.setImageBitmap(chatEntity.getChat_picture());
			}
			et_chatInput.setText("");
			lChatEntities.add(chatEntity);
			chattingAdapter.notifyDataSetChanged();
			lv_chattings.setSelection(lChatEntities.size() - 1);
			break;
		case 2:
			break;
		case 3:
			toast(R.string.record_short);
			break;
		case 4:
			/**
			 * 动态捕捉麦克音量变化，更新UI
			 */
			int volume = (Integer) msg.obj; // 读取到的音量大小
			LevelListDrawable levelDrawable = (LevelListDrawable) iv_record
					.getDrawable();
			levelDrawable.setLevel(volume);
			break;
		}
		return false;
	}
}