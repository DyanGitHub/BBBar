package com.example.bbbar;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bbbar.Util.TakeInImageUtil;


public class MainActivity extends ActionBarActivity {
	private TextView sdcardsize;
	private TextView sdcardsizeleft;
	private TextView insdpath,notinsdpath,notinsdsize,notinsdsizeleft;
	private String [] paths=null;
	private LinearLayout pathlayout;
	private ImageView image;
	private TakeInImageUtil takeInImageUtil=new TakeInImageUtil(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sdcardsize=(TextView)findViewById(R.id.sdcardsize);
        sdcardsizeleft=(TextView)findViewById(R.id.sdcardsizeleft);
        insdpath=(TextView)findViewById(R.id.insdpath);
        pathlayout=(LinearLayout)findViewById(R.id.pathlayout);
        SDCardSizeTest();
        SDCardPathTest();
        if(paths!=null)
        {
        	
        	String insdpathString=Environment.getExternalStorageDirectory().getPath();
        	insdpath.setText(insdpathString);
        	for(int i=0;i<paths.length;i++)
        	{
        		TextView text=new TextView(this);
        		text.setText(i+":"+paths[i]+"���ڴ棺"+getSDTotalSize(paths[i])+"�����ڴ棺"+getSDFreeSize(paths[i]));
        		pathlayout.addView(text);
        	}
        	
        	
        }
//        System.out.println("fitpath:"+SDCardUtil.getFitPath(this));
        System.out.println("oncreate================");
        image=(ImageView)findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				takeInImageUtil.doPickPhotoAction();
			}
		});
    }
 // ��Ϊ������Camera��Gally����Ҫ�ж����Ǹ��Եķ������,��������ʱ��������startActivityForResult  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode != RESULT_OK)  
            return;  
        System.out.println("resultCode:"+resultCode);
        switch (requestCode) {  
            case TakeInImageUtil.PHOTO_PICKED_WITH_DATA: {
            	// ����Gallery���ص�  
                final Bitmap photo = data.getParcelableExtra("data");
                //uriΪ��
//                System.out.println("path:"+data.getDataString());
                // ���������ʾ��Ƭ��  
                //�����û�ѡ���ͼƬ  ����bitmap
//                byte[]  img = getBitmapByte(photo);  
//                mEditor.setPhotoBitmap(photo);
                //�˴����Խ���bitmapд�����صĲ���������uri���浽sqlite��
                image.setImageURI(Uri.parse(takeInImageUtil.saveBitMap(photo,null)));
                break;  
            }  
            case TakeInImageUtil.CAMERA_WITH_DATA: {// ��������򷵻ص�,�ٴε���ͼƬ��������ȥ�޼�ͼƬ  
                takeInImageUtil.doCropPhoto(takeInImageUtil.mCurrentPhotoFile);  
                break;  
            }  
        }  
    } 
    /*SDCard��ط��������SDCardUtil*/
    //�õ����д洢sd��·��
    public void SDCardPathTest()
    {
    	StorageManager sm = (StorageManager) MainActivity.this.getSystemService(Context.STORAGE_SERVICE);
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
    }
    //ͳ��sdcard�Ĵ�С
    public void SDCardSizeTest() {  
    	  
    	// ȡ��SDCard��ǰ��״̬  
    	String sDcString = android.os.Environment.getExternalStorageState();  
    	  //�ж�SD���Ƿ����
    	if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {  
    	  
    	    // ȡ��sdcard�ļ�·��  
    	    File pathFile = android.os.Environment  
    	            .getExternalStorageDirectory();  
    	  
    	    long nSDTotalSize = getSDTotalSize(pathFile.getPath())  ;  
    	    //�������ʾ
    	    sdcardsize.setText(nSDTotalSize+"MB");
    	     
    	    // ���� SDCard ʣ���СMB  
    	    long nSDFreeSize = getSDFreeSize(pathFile.getPath()); 
    	    //�������ʾ
    	    sdcardsizeleft.setText(nSDFreeSize+"MB");
    	}
    }
    //��ȡsd�ܴ�С
    public long getSDTotalSize(String path)
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
    public long getSDFreeSize(String path)
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
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	// TODO Auto-generated method stub
    	super.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	System.out.println("onDestroy()");
    	super.onDestroy();
    }
}
