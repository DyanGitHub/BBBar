package com.example.bbar.uis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bbbar.R;
import com.example.bbbar.sqlUtil.BuyGood;

public class SpinerListDialog {
	public static int SPET=-1;
	public static int IMGV=-2;
	Activity context;
	ListView listView;
	AlertDialog dialog;
	public SpinerListDialog(Context context)
	{
		this.context=(Activity)context;
		listView =new ListView(context);
	}
	public void showListDialog(List<String> datas,int type)
	{
		
		SpinerAdapter adapter=new SpinerAdapter(datas,type);
    	LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	listView.setLayoutParams(params);
    	listView.setDivider(new ColorDrawable(Color.parseColor("#dcdcdc")));
    	listView.setDividerHeight(1);
    	listView.setAdapter(adapter);
    	dialog =new AlertDialog.Builder(context).setTitle("请选择").setView(listView).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
			
		}).create();
    	dialog.show();
	}
    public void showSpinerList(final List<String> ascdatas,final SpinerEditText spinerEditText)
    {
    	final List<String> datas=descData(ascdatas);
    	if(datas==null||datas.size()==0)
    	{
    		return ;
    	}
    	showListDialog(datas,SPET);
    	listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				spinerEditText.setText(datas.get(position));
				//选择后隐藏键盘
				((InputMethodManager)spinerEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(
						context.getCurrentFocus()
						.getWindowToken(), 
						InputMethodManager.HIDE_NOT_ALWAYS);
				dialog.dismiss();
			}
		}); 	
    } 
    public void showSpinerList(final List<String> ascdatas,final List<BuyGood> gooddata,final Handler handler)
    {
    	final List<String> datas=descData(ascdatas);
    	if(datas==null||datas.size()==0)
    	{
    		return ;
    	}
    	showListDialog(datas,IMGV);
    	listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
			
				Message msg=Message.obtain();
				Bundle bundle=new Bundle();
				bundle.putString("path", datas.get(position));
				bundle.putSerializable("gooddata", (Serializable)gooddata);
				msg.setData(bundle);
				handler.sendMessage(msg);
				dialog.dismiss();
			}
		}); 
    } 
    //数据源降序处理
    List<String>  descData(List<String> data)
    {
    	if(data==null&&data.size()==0)
    		return null;
    	List<String> descD=new ArrayList<String>();
    	for(int i=data.size()-1;i>=0;i--)
    	{
    		descD.add(data.get(i));
    	}
    	return descD;
    }
    class SpinerAdapter extends BaseAdapter
    {
    	List<String> data =null;
    	int type=0;
        public SpinerAdapter(List<String> data,int type)
        {
        	this.data=data;
        	this.type=type;
        }
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(data!=null)
				return data.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if(data!=null&&data.size()!=0)
				return data.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(data!=null&&data.size()!=0)
			{
				if (type == SPET) {
					if (convertView == null) {
						convertView = context.getLayoutInflater().inflate(
								R.layout.spiner_item_text, parent, false);
					}
					TextView textView = (TextView) convertView
							.findViewById(R.id.itemText);
					textView.setText(data.get(position));
				}
				else
					if(type == IMGV)
					{
						if (convertView == null) {
							convertView = context.getLayoutInflater().inflate(
									R.layout.spiner_item_image, parent, false);
						}
						ImageView image=(ImageView) convertView.findViewById(R.id.itemImage);
						image.setImageURI(Uri.parse(data.get(position)));
					}
				return convertView;
			}
			return null;
		}
    	
    }
}
