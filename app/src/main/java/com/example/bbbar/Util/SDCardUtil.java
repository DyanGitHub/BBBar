package com.example.bbbar.Util;

import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
/*1����ȡʣ��ռ����Ĵ洢·����
 * 2����ȡĳĳ�洢·������������
 * 3����ȡĳĳ�洢·����ʣ��ռ�*/
public class SDCardUtil {
	//��ȡ���ʵĴ洢·��--ȡʣ���ڴ����Ĵ洢�豸·��
	public static String getFitPath(Context context)
	{
		
		//sdcard״̬
		String sdstate=Environment.getExternalStorageState();
		if(sdstate.equals(Environment.MEDIA_MOUNTED))
		{
			String initpath=Environment.getExternalStorageDirectory().getPath();
			System.out.println(initpath);
			String[] paths=SDCardPathTest(context);
			//��������
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
	//�õ����д洢sd��·��
    public static String[] SDCardPathTest(Context context)
    {
    	String [] paths=null;
    	StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
    	// ��ȡsdcard��·�������ú�����
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
  //��ȡsd�ܴ�С
    public static long getSDTotalSize(String path)
    {
    	android.os.StatFs statfs = new android.os.StatFs(path);  
  	  
	    // ��ȡSDCard��BLOCK����  
	    long nTotalBlocks = statfs.getBlockCount();  
	  
	    // ��ȡSDCard��ÿ��block��SIZE  
	    long nBlocSize = statfs.getBlockSize();  
	  
	    // ��ȡ�ɹ�����ʹ�õ�Block������  
	    long nAvailaBlock = statfs.getAvailableBlocks();  
	  
	    // ��ȡʣ�µ�����Block������(����Ԥ����һ������޷�ʹ�õĿ�)  
	    long nFreeBlock = statfs.getFreeBlocks();  
	  
	    // ����SDCard ��������СMB  
	    long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;  
    	return nSDTotalSize;
    }
    //��ȡsd�����С
    public static long getSDFreeSize(String path)
    {
    	android.os.StatFs statfs = new android.os.StatFs(path);  
  	  
	    // ��ȡSDCard��BLOCK����  
	    long nTotalBlocks = statfs.getBlockCount();  
	  
	    // ��ȡSDCard��ÿ��block��SIZE  
	    long nBlocSize = statfs.getBlockSize();  
	  
	    // ��ȡ�ɹ�����ʹ�õ�Block������  
	    long nAvailaBlock = statfs.getAvailableBlocks();  
	  
	    // ��ȡʣ�µ�����Block������(����Ԥ����һ������޷�ʹ�õĿ�)  
	    long nFreeBlock = statfs.getFreeBlocks();  
	  
	    // ����SDCard ��������СMB  
	    long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024; 
    	return nSDFreeSize;
    }
}
