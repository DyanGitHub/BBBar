package com.example.bbbar.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.bbbar.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

/**
 * Used to select a local photo
 * 
 * @author huanghai
 * 
 */
public class PhotoSelUtil {
	public final int IMAGE_REQUEST_CODE = 0;
	public final int CAMERA_REQUEST_CODE = 1;
	public final int RESIZE_REQUEST_CODE = 2;
	private final String IMAGE_FILE_NAME = "header.jpg";
	private String imgString;// 图片的base64String
	private Bitmap photo;
	private Activity mContext;
	private int getType;// 0 需裁剪 1不需裁剪
	private int resizeHeight;

	public static String SDPATH = "";//保存的路径

	public String getImgString() {
		String temp = imgString;
		imgString = null;
		return temp;
	}

	public Bitmap getBitmap() {
		return photo;
	}

	public void setGetType(int getType) {
		this.getType = getType;
	}

	public void showPhoto(final Activity mActivity, String title) {
		mContext = mActivity;
		SDPATH = Environment.getExternalStorageDirectory().getPath()
				+ "/BDCity/Image/";
		Context dialogContext = new ContextThemeWrapper(mContext,
				android.R.style.Theme_Light);
		String cancel = mContext.getString(R.string.back);
		String[] choices;
		choices = new String[2];
		choices[0] = mContext.getString(R.string.take_photo); // 拍照
		choices[1] = mContext.getString(R.string.pick_photo); // 从相册中选择
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		 builder.setTitle(title);
		// 单项选择； -1 默认序号选择为-1的项
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0: {
							doTakePhoto(mActivity);// 用户点击了从照相机获取
							break;

						}
						case 1:
							doPickPhotoFromGallery(mActivity);// 从相册中去获取
							break;
						}
					}
				});
		builder.setNegativeButton(cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		builder.create().show();

	}
	/**
	 * 从相册中选取
	  * @author Sxf  
	  * @param @param mActivity
	  * @return void
	  * @date 2015年7月9日 上午10:51:32
	 */
	protected void doPickPhotoFromGallery(Activity mActivity) {
		// TODO Auto-generated method stub
		Intent intentPick = new Intent(Intent.ACTION_PICK, null);
		intentPick.setDataAndType(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		mContext.startActivityForResult(intentPick, IMAGE_REQUEST_CODE);
	}
	/**
	 * 拍照
	  * @author Sxf  
	  * @param @param mActivity
	  * @return void
	  * @date 2015年7月9日 上午10:51:57
	 */
	protected void doTakePhoto(Activity mActivity) {
		// TODO Auto-generated method stub
		if (isSdcardExisting()) {
			Intent cameraIntent = new Intent(
					"android.media.action.IMAGE_CAPTURE");
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
//			cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			mContext.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
		} else {
//			TakeInImageUtil.showToast(mContext.getString(R.string.nosdcard),
//					mContext);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		} else {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				if (getType == 0) {
					resizeImage(data.getData());
				} else {
					if (getType == 2) {
						resizeHeight = 800;
					} else {
						resizeHeight = 400;
					}
					getBitMap(data.getData());
				}
				break;
			case CAMERA_REQUEST_CODE:
				if (isSdcardExisting()) {
					if (getType == 0) {
						resizeImage(getImageUri());
					} else {
						if (getType == 2) {
							resizeHeight = 800;
						} else {
							resizeHeight = 400;
						}
						getPzBitMap(getImagePath());
					}
				} else {
					Toast.makeText(mContext, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}
				break;

			case RESIZE_REQUEST_CODE:
				if (data != null) {
					showResizeImage(data);
				}
				break;
			}
		}

	}

	private boolean isSdcardExisting() {
		final String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	private void resizeImage(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		if (mContext != null) {
			mContext.startActivityForResult(intent, RESIZE_REQUEST_CODE);
		}
	}

	public void showResizeImage(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			photo = extras.getParcelable("data");
			saveBitmap(photo);
			// bitmapToBase64(photo);
		}
	}

	/**
	 * bitmap转为base64
	 * 
	 * @param bitmap
	 * @return
	 */
	public void bitmapToBase64(Bitmap bitmap) {

		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				imgString = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int imgNum = 101;

	public String DICM = "";

	public Uri getImageUri() {
		try {
			createSDDir("");
			if (getType == 0) {
				return Uri.fromFile(new File(Environment
						.getExternalStorageDirectory().getPath()
						+ "/BDCity/Image/", IMAGE_FILE_NAME));
			} else {
				imgNum++;
				DICM = getPhotoFileName();
				return Uri.fromFile(new File(Environment
						.getExternalStorageDirectory().getPath()
						+ "/BDCity/Image/", DICM));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// 获取系统当前的具体时间
	// private String getDate() {
	// Calendar c = Calendar.getInstance();
	// String year = String.valueOf(c.get(Calendar.YEAR));
	// String month = String.valueOf(c.get(Calendar.MONTH));
	// String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
	// String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
	// String mins = String.valueOf(c.get(Calendar.MINUTE));
	// StringBuffer sbBuffer = new StringBuffer();
	// sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"+ mins);
	// return sbBuffer.toString();
	// }

	public String getImagePath() {
		return Environment.getExternalStorageDirectory().getPath()
				+ "/BDCity/Image/" + DICM;

	}

	private void getBitMap(Uri uri) {
		String fPath = uri2filePath(uri); // 转化为路径
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		int be = (int) (options.outHeight / (float) resizeHeight);
		if (be <= 0)
			be = 1;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inSampleSize = be;
		photo = BitmapFactory.decodeFile(fPath, options);
		saveBitmap(photo);
		// bitmapToBase64(photo);
	}

	private void getPzBitMap(String path) {
		// TODO Auto-generated method stub
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		int be = (int) (options.outHeight / (float) resizeHeight);
		if (be <= 0)
			be = 1;
		options.inJustDecodeBounds = false;
		options.inSampleSize = be;
		photo = BitmapFactory.decodeFile(path, options);
		saveBitmap(photo);
		// bitmapToBase64(photo);
	}

	private String uri2filePath(Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = mContext.getContentResolver().query(uri, proj, null,
				null, null);
		if (cursor == null)
			return "";
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(index);
		cursor.close();
		return path;
	}

	/**
	 * 设置照片的名字
	 * 
	 * @author Sxf
	 * @date 2015-3-23 下午5:03:09
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'bdcity'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	/**
	 * 
	 * @author Sxf
	 * @date 2015-3-23 下午5:03:39
	 */
	public void saveBitmap(Bitmap bm) {
		try {
			if (!isFileExist("")) {
				createSDDir("");
			}
			if (TextIsNUll(DICM)) {
				DICM = getPhotoFileName();
			}
			File f = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/BDCity/Image/", DICM);
			if (f.exists()) {
				f.delete();
			}
			bm = comp(bm);
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			Log.e("", "已经保存");
			imgString = f.getPath();
			photo = bm;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断字符串是否为空
	 * 
	 * @author Sxf
	 * @date 2014-12-9 上午11:33:35
	 */
	public static boolean TextIsNUll(String str) {
		if (str == null) {
			return true;
		} else {
			if (TextUtils.isEmpty(str) || "null".equals(str)) {
				return true;
			}
		}
		return false;
	}
	
	public File createSDDir(String dirName) throws IOException {
		File dir = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/BDCity/Image/" + dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
		}
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/BDCity/Image/" + fileName);
		file.isFile();
		return file.exists();
	}

	/**
	 * 图片按比例大小压缩方法（根据路径图片压缩）
	 * 
	 * @author Sxf
	 * @date 2015-3-23 下午3:46:56
	 */
	public Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 图片按比例大小压缩方法（根据Bitmap图片压缩）
	 * 
	 * @author Sxf
	 * @date 2015-3-23 下午2:55:21
	 */
	public Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 图片质量压缩
	 * 
	 * @author Sxf
	 * @date 2015-3-23 下午2:54:32
	 */
	public Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 获取圆角位图的方法
	 * 
	 * @author Sxf
	 * @date 2015-3-23 下午2:54:32
	 * @param bitmap
	 *            需要转化成圆角的位图
	 * @param pixels
	 *            圆角的度数，数值越大，圆角越大
	 * @return 处理后的圆角位图
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		if (bitmap != null) {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = pixels;
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
			return output;
		}
		return null;
	}
}
