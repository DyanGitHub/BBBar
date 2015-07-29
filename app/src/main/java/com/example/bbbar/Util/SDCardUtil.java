package com.example.bbbar.Util;

import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
/*1、获取剩余空间最大的存储路径；
 * 2、获取某某存储路径的总容量；
 * 3、获取某某存储路径的剩余空间*/
public class SDCardUtil {
	//获取合适的存储路径--取剩余内存最大的存储设备路径
	public static String getFitPath(Context context)
	{
		
		//sdcard状态
		String sdstate=Environment.getExternalStorageState();
		if(sdstate.equals(Environment.MEDIA_MOUNTED))
		{
			String initpath=Environment.getExternalStorageDirectory().getPath();
			System.out.println(initpath);
			String[] paths=SDCardPathTest(context);
			//升序排序
			for(int i=0;i<paths.length-1;i++)
			{
				for(int j=0;j<paths.length-i-1;j++)
				{
					if(getSDFreeSize(paths[j])>getSDFreeSize(paths[j+1]))
					{
						String temp=null;
						temp=paths[j];
						paths[j]=paths[j+1];
						paths[j+1]=temp;
					}
				}
			}
			for(int i=0;i<paths.length;i++)
			{
				System.out.println(paths[i]);
				System.out.println(getSDTotalSize((paths[i])));
			}
			return paths[paths.length-1];
			
		}
		return null;
	}
	//得到所有存储sd的路径
    public static String[] SDCardPathTest(Context context)
    {
    	String [] paths=null;
    	StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
    	// 获取sdcard的路径：外置和内置
    	try {
    	paths = (String[])sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);

    	} catch (IllegalArgumentException e) {
    	// TODO Auto-generated catch block
    	e.printStackTrace();
    	} catch (IllegalAccessException e) {
    	// TODO Auto-generated catch block
    	e.printStackTrace();
    	} catch (InvocationTargetException e) {
    	// TODO Auto-generated catch block
    	e.printStackTrace();
    	} catch (NoSuchMethodException e) {
    	// TODO Auto-generated catch block
    	e.printStackTrace();
    	}
    	return paths;
    }
  //获取sd总大小
    public static long getSDTotalSize(String path)
    {
    	android.os.StatFs statfs = new android.os.StatFs(path);  
  	  
	    // 获取SDCard上BLOCK总数  
	    long nTotalBlocks = statfs.getBlockCount();  
	  
	    // 获取SDCard上每个block的SIZE  
	    long nBlocSize = statfs.getBlockSize();  
	  
	    // 获取可供程序使用的Block的数量  
	    long nAvailaBlock = statfs.getAvailableBlocks();  
	  
	    // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)  
	    long nFreeBlock = statfs.getFreeBlocks();  
	  
	    // 计算SDCard 总容量大小MB  
	    long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;  
    	return nSDTotalSize;
    }
    //获取sd空余大小
    public static long getSDFreeSize(String path)
    {
    	android.os.StatFs statfs = new android.os.StatFs(path);  
  	  
	    // 获取SDCard上BLOCK总数  
	    long nTotalBlocks = statfs.getBlockCount();  
	  
	    // 获取SDCard上每个block的SIZE  
	    long nBlocSize = statfs.getBlockSize();  
	  
	    // 获取可供程序使用的Block的数量  
	    long nAvailaBlock = statfs.getAvailableBlocks();  
	  
	    // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)  
	    long nFreeBlock = statfs.getFreeBlocks();  
	  
	    // 计算SDCard 总容量大小MB  
	    long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024; 
    	return nSDFreeSize;
    }
}
