package com.example.bbbar.Util;

public class CheckDoubleClickUtil {
	/*判断是否重复点击*/
	    public static  long lastClickTime;  
	    public  static boolean isFastDoubleClick() {  
	        long time = System.currentTimeMillis();  //时间的表达格式为当前计算机时间和GMT时间(格林威治时间)1970年1月1号0时0分0秒所差的毫秒数。
	        long timeD = time - lastClickTime;  
//	        System.out.println("time"+time);
//	        System.out.println("lastClickTime:"+lastClickTime);
	        lastClickTime = time;     
	        if ( 0 < timeD && timeD < 2000) {     
	            return true;     
	        }     
	        return false;     
	    }  
}
