package com.example.bbbar.StatisticsInfo;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter
	{
		public List<View> images;
        public ViewPagerAdapter(List<View> images)
        {
        	this.images=images;
        }
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return Integer.MAX_VALUE;
			if(images!=null)
			{
				return images.size();
			}
			return 0;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			if(images==null||images.size()==0)
			{
				return null;
			}
			System.out.println(position);
			System.out.println("-----ooo:"+images.size());
//			View view= images.get(position%images.size());
			View view= images.get(position);
            if(view.getParent()!=null)
            {
            	((ViewPager) view  
                        .getParent()).removeView(view);  
            }
            container.addView(view);
			return view;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
//			super.destroyItem(container, position, object); 这句必须注释掉
			//滑动后清除此页 此处不写会报错
			if(images==null||images.size()==0)
			{
				return ;
			}
//			View view =images.get(position%images.size());
			View view =images.get(position);
			container.removeView(view);
		}
		
	}