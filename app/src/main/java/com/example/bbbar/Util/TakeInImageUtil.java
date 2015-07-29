package com.example.bbbar.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.bbbar.R;

/*������ߴ�����л�ȡͼƬ*/
public class TakeInImageUtil {
	private Context context;
	/*���յ���Ƭ�洢λ��*/  
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    public static   File mCurrentPhotoFile;//��������յõ���ͼƬ 
    /*������ʶ�������๦�ܵ�activity ����startActivityForResult */  
    public  static final int CAMERA_WITH_DATA = 3023;  
  
    /*������ʶ����gallery��activity ����startActivityForResult*/  
    public   static  final int PHOTO_PICKED_WITH_DATA = 3021; 
    private static final File PHOTO_THUMB_DIR=new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/thumb");
    //����File PHOTO_THUMB_DIR=new File(Environment.getExternalStorageDirectory(),"DCIM/Camera/thumb");
    /*
     * ѡ����ʾ����
     * */ 
    public TakeInImageUtil(Context context)
    {
    	this.context=context;
    }
	public    void doPickPhotoAction() {  
        // Wrap our context to inflate list items using correct theme  
        final Context dialogContext = new ContextThemeWrapper(context,  
                android.R.style.Theme_Light);  
        String cancel=context.getString(R.string.back);  
        String[] choices;  
        choices = new String[2];  
        choices[0] = context.getString(R.string.take_photo);  //����  
        choices[1] = context.getString(R.string.pick_photo);  //�������ѡ��  
        final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,  
                android.R.layout.simple_list_item_1, choices);  
      
        final AlertDialog.Builder builder = new AlertDialog.Builder(  
                dialogContext);  
//        builder.setTitle(R.string.attachToContact); 
        //����ѡ��  -1 Ĭ�����ѡ��Ϊ-1����
        builder.setSingleChoiceItems(adapter, -1,  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();  
                        switch (which) {  
                        case 0:{  
                            String status=Environment.getExternalStorageState();  
                            if(status.equals(Environment.MEDIA_MOUNTED)){//�ж��Ƿ���SD��  
                                doTakePhoto(context);// �û�����˴��������ȡ  
                            }  
                            else{  
                                showToast(context.getString(R.string.nosdcard),context);  
                            }  
                            break;  
                              
                        }  
                        case 1:  
                            doPickPhotoFromGallery(context);// �������ȥ��ȡ  
                            break;  
                        }  
                    }  
                });  
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {  
      
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
            }  
              
        });  
        builder.create().show();  
    }  
	public void showToast(String text,Context  context)
	{
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
/** 
* ���ջ�ȡͼƬ 
*  
*/  
protected void doTakePhoto(Context context) {  
    try {  
        // Launch camera to take photo for selected contact  
        PHOTO_DIR.mkdirs();// ������Ƭ�Ĵ洢Ŀ¼  
        mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// �����յ���Ƭ�ļ�����  
        final Intent intent = getTakePickIntent(mCurrentPhotoFile);  
        Activity activity=(Activity) context;
        activity.startActivityForResult(intent, CAMERA_WITH_DATA);  
        
    } catch (ActivityNotFoundException e) {  
        Toast.makeText(context,context.getString(R.string.photoPickerNotFoundText) ,  
                Toast.LENGTH_LONG).show();  
    }  
}  
  
public static Intent getTakePickIntent(File f) {  
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);  
    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));  
   
    return intent;  
}  
  
/** 
* �õ�ǰʱ���ȡ�õ�ͼƬ���� 
*  
*/  
private String getPhotoFileName() {  
    Date date = new Date(System.currentTimeMillis());  
    SimpleDateFormat dateFormat = new SimpleDateFormat(  
            "'IMG'_yyyy-MM-dd HH-mm-ss");  //时间格式带：号，部分手机调试会报错java.io.IOException: open failed: EINVAL (Invalid argument)
    return dateFormat.format(date) + ".png";  
}  
  
// ����Gallery����  
protected void doPickPhotoFromGallery(Context context) {  
    try {  
        // Launch picker to choose photo for selected contact  
        final Intent intent = getPhotoPickIntent(); 
        Activity activity=(Activity) context;
        activity.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);  
    } catch (ActivityNotFoundException e) {  
        Toast.makeText(context, R.string.photoPickerNotFoundText1,  
                Toast.LENGTH_LONG).show();  
    }  
}  
  
// ��װ����Gallery��intent  
public  Intent getPhotoPickIntent() {  
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);  
    intent.setType("image/*"); //���������ȷ��Ҫѡ�������ΪͼƬ 
    intent.putExtra("crop", "true"); //�����˲������ͻ���òü�����������ã��ͻ������ü��Ĺ��̡� 
    //����4����ʾ���ǲü������ʽ
    intent.putExtra("aspectX", 1); //����ǲü�ʱ��� �ü���� X ����ı����� 
    intent.putExtra("aspectY", 1);  //ͬ��Y ����ı���. (ע�⣺ aspectX, aspectY ������ֵ����ҪΪ �����������һ��Ϊ���������ͻᵼ�±���ʧЧ��)
  //����aspectX �� aspectY �󣬲ü���ᰴ����ָ���ı������֣��Ŵ���С��������ġ������ָ������ô �ü���Ϳ�����������ˡ�
    intent.putExtra("outputX", 200);  //�������ݵ�ʱ��� X ���ش�С
    intent.putExtra("outputY", 200);//���ص�ʱ�� Y �����ش�С
  //��������ֵ������֮��ᰴ������ֵ����һ��Bitmap, ����ֵ�������bitmap�ĺ�������������ֵ������ü���ͼ����������ֵ�����ϣ���ô�հײ����Ժ�ɫ��䡣
    intent.putExtra("noFaceDetection", true); // �Ƿ�ȥ���沿��⣬ �������Ҫ�ض��ı���ȥ�ü�ͼƬ����ô���һ��Ҫȥ������Ϊ�����ƻ����ض��ı�����
    intent.putExtra("return-data", true);  //�Ƿ�Ҫ����ֵ�� һ�㶼Ҫ���ҵ�һ�������ˣ�����ȡ�ÿ�ֵ���壡
//    //��������ʾ�ü�ͼ���治��
//    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile( new File(PHOTO_THUMB_DIR, getPhotoFileName())));
    return intent;  
}    
  
public void doCropPhoto(File f) {  
    try {
    	
    	//�ֻ��������Ƭ��Щ���ͻ������ת �账��
    	int degree=readPictureDegree(f.getPath());
    	System.out.println("degree:"+degree);
    	Bitmap currentBitmap=MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(f));
    	//������ת�ָ����bitmap����δ�޸�ͬһ����ַ
    	Bitmap bitmap=rotaingImageView(degree,currentBitmap);
    	saveBitMap(bitmap, f);
    	
        // ����galleryȥ���������Ƭ  
        final Intent intent = getCropImageIntent(Uri.fromFile(f));  
       ( (Activity)context).startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);  
    } catch (Exception e) {  
    	e.printStackTrace();
//        Toast.makeText(context, R.string.photoPickerNotFoundText+"iii",  
//                Toast.LENGTH_LONG).show();  
    }  
}  
  
/**  
* Constructs an intent for image cropping. ����ͼƬ��������  
*/  
public  Intent getCropImageIntent(Uri photoUri) {  
    Intent intent = new Intent("com.android.camera.action.CROP");  
    intent.setDataAndType(photoUri, "image/*");  
    intent.putExtra("crop", "true");  
    intent.putExtra("aspectX", 1);  
    intent.putExtra("aspectY", 1);  
    intent.putExtra("outputX", 200);  
    intent.putExtra("outputY", 200);  
    intent.putExtra("return-data", true);  
    return intent;  
}  
//����ü���õ���bitmap������
   public String saveBitMap(Bitmap bitmap,File file)
   {  
	   boolean isNull=true;
	   if(file==null){
	   //PHOTO_THUMB_DIR ��Ϊ��Ŀ¼
	   if(!PHOTO_THUMB_DIR.exists())
	   {
		   //Ŀ¼�����ڴ���Ŀ¼
		   PHOTO_THUMB_DIR.mkdirs();
	   }
	    file =new File(PHOTO_THUMB_DIR ,getPhotoFileName() );
		if (!file.exists()) {
			//�ļ������ڴ����ļ�
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}}
	   else
	   {
		   isNull=false;
	   }
       FileOutputStream fOut = null;
       try {
               fOut = new FileOutputStream(file);
       } catch (FileNotFoundException e) {
               e.printStackTrace();
       }
       bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
       String path=file.getPath();
       if(isNull){
       comp(bitmap);}
       try {
               fOut.flush();
       } catch (IOException e) {
               e.printStackTrace();
       }
       try {
               fOut.close();
       } catch (IOException e) {
               e.printStackTrace();
       }
       System.out.println(path);
       return path;
   }
   	/**
	 * ��תͼƬ
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
       //��תͼƬ ����
		Matrix matrix = new Matrix();;
       matrix.postRotate(angle);
       System.out.println("angle2=" + angle);
       // �����µ�ͼƬ
       Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
       		bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
	/**
	 * ��ȡͼƬ���ԣ���ת�ĽǶ�
	 * @param path ͼƬ����·��
	 * @return degree��ת�ĽǶ�
	 */
   public static int readPictureDegree(String path) {
       int degree  = 0;
       try {
               ExifInterface exifInterface = new ExifInterface(path);
               int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
               switch (orientation) {
               case ExifInterface.ORIENTATION_ROTATE_90:
                       degree = 90;
                       break;
               case ExifInterface.ORIENTATION_ROTATE_180:
                       degree = 180;
                       break;
               case ExifInterface.ORIENTATION_ROTATE_270:
                       degree = 270;
                       break;
               }
       } catch (IOException e) {
               e.printStackTrace();
       }
       return degree;
   }
//	/**
//	 * ͼƬ��������Сѹ������������BitmapͼƬѹ����---���������ͼƬЧ�������·��Ǹ�����
//	 * 
//	 * @author Sxf
//	 * @date 2015-3-23 ����2:55:21
//	 */
//	public Bitmap comp(String  imageFile) {
//
//		BitmapFactory.Options opts = new BitmapFactory.Options();
//		opts.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(imageFile, opts);
//		             
//		opts.inSampleSize = computeSampleSize(opts, -1, 128*128);       
//		opts.inJustDecodeBounds = false;
//		Bitmap bitmap=null;
//		try {
//			bitmap = BitmapFactory.decodeFile(imageFile, opts);
//		    } catch (OutOfMemoryError err) {
//		    }
//		bitmap=compressImage(bitmap);
//		try {
//			bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(imageFile)));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return bitmap;// ѹ���ñ�����С���ٽ�������ѹ��
//	}
   /**��֤ͼƬ����
	 * ͼƬ��������Сѹ������������BitmapͼƬѹ����
	 * 
	 * @author Sxf
	 * @date 2015-3-23 ����2:55:21
	 */
	public Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// �ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���
			baos.reset();// ����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// ����ѹ��50%����ѹ��������ݴ�ŵ�baos��
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// ��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// ���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
		float hh = 800f;// �������ø߶�Ϊ800f
		float ww = 480f;// �������ÿ��Ϊ480f
		// ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
		int be = 1;// be=1��ʾ������
		if (w > h && w > ww) {// �����ȴ�Ļ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// ����߶ȸߵĻ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// �������ű���
		// ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// ѹ���ñ�����С���ٽ�������ѹ��
	}
	public static int computeSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength,
	            maxNumOfPixels);
	 
	    int roundedSize;
	    if (initialSize <= 8) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }
	 
	    return roundedSize;
	}
	 
	private static int computeInitialSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;
	 
	    int lowerBound = (maxNumOfPixels == -1) ? 1 :
	            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 :
	            (int) Math.min(Math.floor(w / minSideLength),
	            Math.floor(h / minSideLength));
	 
	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }
	 
	    if ((maxNumOfPixels == -1) &&
	            (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}   

	/**
	 * ͼƬ����ѹ��
	 * 
	 * @author Sxf
	 * @date 2015-3-23 ����2:54:32
	 */
	public Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
			baos.reset();// ����baos�����baos
			image.compress(Bitmap.CompressFormat.PNG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��
			options -= 10;// ÿ�ζ�����10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
		return bitmap;
	}

	public  String onActivityResult(int requestCode, int resultCode, Intent data) {
		String path = null;
		switch (requestCode) {
		case TakeInImageUtil.PHOTO_PICKED_WITH_DATA: {

			final Bitmap photo = data.getParcelableExtra("data");
			path = saveBitMap(photo, null);
			break;
		}
		case TakeInImageUtil.CAMERA_WITH_DATA: {
			doCropPhoto(mCurrentPhotoFile);
			break;
		}
		}
		return path;

	}

}
