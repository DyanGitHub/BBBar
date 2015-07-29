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
        		text.setText(i+":"+paths[i]+"总内存："+getSDTotalSize(paths[i])+"空余内存："+getSDFreeSize(paths[i]));
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
 // 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode != RESULT_OK)  
            return;  
        System.out.println("resultCode:"+resultCode);
        switch (requestCode) {  
            case TakeInImageUtil.PHOTO_PICKED_WITH_DATA: {
            	// 调用Gallery返回的  
                final Bitmap photo = data.getParcelableExtra("data");
                //uri为空
//                System.out.println("path:"+data.getDataString());
                // 下面就是显示照片了  
                //缓存用户选择的图片  保存bitmap
//                byte[]  img = getBitmapByte(photo);  
//                mEditor.setPhotoBitmap(photo);
                //此处可以进行bitmap写到本地的操作，并将uri保存到sqlite中
                image.setImageURI(Uri.parse(takeInImageUtil.saveBitMap(photo,null)));
                break;  
            }  
            case TakeInImageUtil.CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片  
                takeInImageUtil.doCropPhoto(takeInImageUtil.mCurrentPhotoFile);  
                break;  
            }  
        }  
    } 
    /*SDCard相关方法，详见SDCardUtil*/
    //得到所有存储sd的路径
    public void SDCardPathTest()
    {
    	StorageManager sm = (StorageManager) MainActivity.this.getSystemService(Context.STORAGE_SERVICE);
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
    }
    //统计sdcard的大小
    public void SDCardSizeTest() {  
    	  
    	// 取得SDCard当前的状态  
    	String sDcString = android.os.Environment.getExternalStorageState();  
    	  //判断SD卡是否存在
    	if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {  
    	  
    	    // 取得sdcard文件路径  
    	    File pathFile = android.os.Environment  
    	            .getExternalStorageDirectory();  
    	  
    	    long nSDTotalSize = getSDTotalSize(pathFile.getPath())  ;  
    	    //组件上显示
    	    sdcardsize.setText(nSDTotalSize+"MB");
    	     
    	    // 计算 SDCard 剩余大小MB  
    	    long nSDFreeSize = getSDFreeSize(pathFile.getPath()); 
    	    //组件上显示
    	    sdcardsizeleft.setText(nSDFreeSize+"MB");
    	}
    }
    //获取sd总大小
    public long getSDTotalSize(String path)
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
    public long getSDFreeSize(String path)
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
