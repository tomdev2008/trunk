package com.example.view;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.activity.R;

public class TitleLayout {
	private static TitleLayout titleLayout;
	private TextView tv_title;
	private Button btn_left,btn_right;
	
	public TextView getTv_title() {
		return tv_title;
	}

	public Button getBtn_left() {
		return btn_left;
	}

	public Button getBtn_right() {
		return btn_right;
	}

	public static TitleLayout getInstance(){
		if (titleLayout==null) {
			titleLayout=new TitleLayout();
		}
		return titleLayout;
	}
	
	public void createTitle(Activity activity,int title){
		tv_title=(TextView) activity.findViewById(R.id.tv_title);
		tv_title.setText(title);
	}
	
	public void createTitle(Activity activity,boolean showLeft,boolean showRight){
		tv_title=(TextView) activity.findViewById(R.id.tv_title);
		if (showLeft) {
			btn_left=(Button) activity.findViewById(R.id.btn_left);
			btn_left.setVisibility(View.VISIBLE);
			btn_left.setOnClickListener((OnClickListener) activity);
		}
		if (showRight) {
			btn_right=(Button) activity.findViewById(R.id.btn_right);
			btn_right.setVisibility(View.VISIBLE);
			btn_right.setOnClickListener((OnClickListener) activity);
		}
	}
	
	public void setTitle(int title){
		tv_title.setText(title);
	}
	
	public void setLeftButton(int text){
		btn_left.setText(text);
	}
	
	public void setRightButton(int text){
		btn_right.setText(text);
	}
}
