package com.example.bbar.uis;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bbbar.R;

public class SpinerEditText extends EditText {
	public Activity context;
    public String defaultValue = "";
    public List<String> datas=null;
    final Drawable imgX = getResources().getDrawable(
            R.drawable.arrow_down);  
 
    public SpinerEditText(Context context) {
        super(context);
        this.context=(Activity)
        		context;
        init();
    }
    public SpinerEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=(Activity)context;
        init();
    }
 
    public SpinerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=(Activity)context;
        init();
    }
    void init() {
 
        // Set bounds of our X button
        imgX.setBounds(0, 0, imgX.getIntrinsicWidth(),
                imgX.getIntrinsicHeight());
 
        // There may be initial text in the field, so we may need to display the
        // button
        manageSpinerButton();
 
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
 
            	SpinerEditText et = SpinerEditText.this;
 
                // Is there an X showing?
                if (et.getCompoundDrawables()[2] == null)
                    return false;
                // Only do this for up touches
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                // Is touch on our spiner button?
                if (event.getX() > et.getWidth() - et.getPaddingRight()
                        - imgX.getIntrinsicWidth()) {
                	//显示spiner列表
                	new SpinerListDialog(context).showSpinerList(datas,SpinerEditText.this);
//                    et.setText("");
//                    SpinerEditText.this.removeSpinerButton();
                }
                return false;
            }
        });

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
 
            	SpinerEditText.this.manageSpinerButton();
            }
 
            @Override
            public void afterTextChanged(Editable arg0) {
            }
 
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }
        });
         
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            	SpinerEditText.this.manageSpinerButton();
            }
        });
    }
    /*
     * 数据导入的同时使得标识可见
     */
    public void refreshSpinerData(List<String> datas)
    {
    	this.datas=datas;
    	hasData=true;
    	manageSpinerButton();
    }
    private  boolean hasData=false;
    void manageSpinerButton() {
        if (this.getText().toString().equals("")||!this.isFocused()||!hasData)
            removeSpinerButton();
        else
            addSpinerButton();
    }
 
    void addSpinerButton() {
        this.setCompoundDrawables(this.getCompoundDrawables()[0],
                this.getCompoundDrawables()[1], imgX,
                this.getCompoundDrawables()[3]);
    }
 
    void removeSpinerButton() {
        this.setCompoundDrawables(this.getCompoundDrawables()[0],
                this.getCompoundDrawables()[1], null,
                this.getCompoundDrawables()[3]);
    }
 
}
