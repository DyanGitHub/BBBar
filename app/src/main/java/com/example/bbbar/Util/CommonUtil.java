package com.example.bbbar.Util;

import java.util.Iterator;
import java.util.List;

import com.example.bbbar.StatisticsInfo.StatisticsIndexActivity.RecordAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;



public class CommonUtil {
	//防重复添加list
	public static  void ignoreDoubleAdd(List<String> list,String s)
	{
		if (list == null||s==null)
			return;
		Iterator< String> iter=list.iterator();
		while(iter.hasNext())
		{
			String is=iter.next();
			if(is.equalsIgnoreCase(s))
			{
				iter.remove();
			}
		}
		list.add(s);
	}
	/**
     * 动态设置ListView的高度
     * @param listView
     */
     public static void setListViewHeightBasedOnChildren(ListView listView ) {
         if( listView == null ) return;

         RecordAdapter listAdapter = (RecordAdapter) listView.getAdapter();
         if ( listAdapter == null) {
             // pre-condition
             return;
         }

         int totalHeight = 0;
         for ( int i = 0; i < listAdapter.getCount(); i++) {
//        	 System.out.println("======:"+listAdapter.getCount());
             View listItem = listAdapter.getView( i, null, listView);
             listItem.measure(0, 0);
             totalHeight += listItem.getMeasuredHeight();
         }

         ViewGroup.LayoutParams params = listView.getLayoutParams();
         params. height = totalHeight + (listView.getDividerHeight() * (listAdapter .getCount() - 1));
         listView.setLayoutParams(params );
     }
}
