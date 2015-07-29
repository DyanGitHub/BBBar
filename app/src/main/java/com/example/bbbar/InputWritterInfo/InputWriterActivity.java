package com.example.bbbar.InputWritterInfo;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbar.uis.SpinerEditText;
import com.example.bbar.uis.SpinerListDialog;
import com.example.bbbar.R;
import com.example.bbbar.Util.CheckDoubleClickUtil;
import com.example.bbbar.Util.CommonUtil;
import com.example.bbbar.Util.ImageLoaderHelper;
import com.example.bbbar.Util.ParamUtil;
import com.example.bbbar.Util.TakeInImageUtil;
import com.example.bbbar.Util.TimeUtil;
import com.example.bbbar.sqlUtil.BuyGood;
import com.example.bbbar.sqlUtil.DbBBar;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class InputWriterActivity extends Activity {
	private Activity activity=this;
	//用来区分不同功能
	private int kind;//1 商品录入 2成交录入  3补货
	public static int BUY_INPUT =1;
	public static int SELL_INPUT =2;
	public static int ADD_INPUT =3;
	
	private TextView title,submit,buy_date;
	private SpinerEditText name,type,copy,num;
	private ImageView photoImage,dbimageinfo,back;
	//data相关 --录入  本次进入页面的数据和相关历史
	private ArrayList<String> names=new ArrayList<String>();
	private ArrayList<String> types=new ArrayList<String>();
	private ArrayList<String> copys=new ArrayList<String>();
	private ArrayList<String> nums=new ArrayList<String>();
	private TakeInImageUtil takeInImageUtil=new TakeInImageUtil(activity);
	private String imagePath=null;
	//数据库操作
	private DbBBar db;
	private SQLiteDatabase writeDatabase;
	private SQLiteDatabase readDatabase;
	private static int RECORD_NOEXIST=-1;
	private ImageLoaderHelper imageLoader=new ImageLoaderHelper();
	private File cacheDir; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atv_inputwriter);
		db=new DbBBar(activity);
		writeDatabase=db.getWritableDatabase();
		readDatabase=db.getReadableDatabase();
		initdata();
		initView();
		cacheDir=StorageUtils.getOwnCacheDirectory(this, ParamUtil.IMAGElOADERCACHE);
	}
	void initdata()
	{
		kind=getIntent().getIntExtra("kind", -1);
	}
	public void initView()
	{
		back=(ImageView)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		title=(TextView)findViewById(R.id.title);	
		photoImage=(ImageView) findViewById(R.id.photoImage);
		photoImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(kind==SELL_INPUT||kind==ADD_INPUT)
				{
					Toast.makeText(activity,"本模块不支持图片选择，请使用右侧记录进行选择", Toast.LENGTH_SHORT).show();
				    return;
				}
				photoImage.setEnabled(false);
				takeInImageUtil.doPickPhotoAction();
			}
		});
		dbimageinfo=(ImageView)findViewById(R.id.dbimageinfo);
		dbimageinfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showdbImages();
			}
		});
		name=(SpinerEditText) findViewById(R.id.name);
		type=(SpinerEditText) findViewById(R.id.type);
		copy=(SpinerEditText) findViewById(R.id.copy);
		num=(SpinerEditText) findViewById(R.id.num);
		buy_date=(TextView) findViewById(R.id.buy_date);
		buy_date.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DatePickerDialog(activity, new OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						// TODO Auto-generated method stub
						buy_date.setText(String.format("%s年%s月%s日", year,monthOfYear+1,dayOfMonth));
					}
				}, 2012, 5, 20).show();;
			}
		});
		submit=(TextView)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//判断是否连击，连击不响应
				if (CheckDoubleClickUtil.isFastDoubleClick()) {
//					System.out.println("连击不响应");
					return;
				}
				//图片判断
				if(isNullString(imagePath))
				{
					Toast.makeText(activity,"必须上传图片", Toast.LENGTH_SHORT).show();
					return;	
				}
				System.out.println("图片路径："+imagePath);
				//文字判断
				String nameString =name.getText().toString();
				String typeString =type.getText().toString();
				String copyString =copy.getText().toString();
				String numString =num.getText().toString();
				String buydateString=buy_date.getText().toString();
				if(kind==SELL_INPUT)
				{
					buydateString="不显示";
				}
				if(isNullString(nameString)||isNullString(typeString)||isNullString(copyString)||isNullString(numString)||isNullString(buydateString)||(buydateString.equalsIgnoreCase("进货日期")))
				{
					Toast.makeText(activity,"漏填了些东西o", Toast.LENGTH_SHORT).show();
				}
				else if(!isCopyFit(copyString))
				{
					Toast.makeText(activity,"价格格式有误", Toast.LENGTH_SHORT).show();
				}
				else
				{
//					System.out.println("提交内容："+nameString+":"+typeString+":"+copyString+":"+numString+":"+buydateString);
					boolean isSuccess = false;
					long result=-1;
					if (kind == BUY_INPUT) {
						//判断数据记录是否存在
						if(isExsitBuygoods( imagePath, typeString)!=null)
						{
							Toast.makeText(activity,"录入失败：该记录已存在", Toast.LENGTH_SHORT).show();
							return;
						}
						// 提交数据库，给出结果提示Toast
						ContentValues values = new ContentValues();
						long id = System.currentTimeMillis();
						values.put("_id", id);
						values.put("buydate", buydateString);
						values.put("currentdate", TimeUtil.getYearAndMonth(id));
						values.put("imagepath", imagePath);
						values.put("goodname", nameString);
						values.put("goodtype", typeString);
						values.put("goodcost", copyString);
						values.put("goodnum", Integer.valueOf(numString));
						values.put("lastnum", Integer.valueOf(numString));
						result = writeDatabase.insert(ParamUtil.TN_BUY,
								null, values);
						
					}
					else
						if(kind==SELL_INPUT||kind==ADD_INPUT)
						{
							BuyGood good=isExsitBuygoods( imagePath, typeString);
							if(good==null)
							{
								Toast.makeText(activity,"录入失败：该款式货品不存在", Toast.LENGTH_SHORT).show();
								return;
							}
							long bid=good.getId();
							if(bid<=0)
							{
								Toast.makeText(activity,"录入失败：数据库数据核对失败", Toast.LENGTH_SHORT).show();
								return;
							}
							System.out.println("查询到的货品编号："+bid);
							ContentValues values = new ContentValues();
							long id = System.currentTimeMillis();
							values.put("_id", id);
							values.put("bid", bid);
							values.put("currentdate", TimeUtil.getYearAndMonth(id));

							values.put("price", copyString);
							values.put("num", Integer.valueOf(numString));
							if(kind==ADD_INPUT)
							{
								values.put("buydate", buydateString);
								result = writeDatabase.insert(ParamUtil.TN_ADD,
										null, values);
								if(result!=-1)
								{
									ContentValues valuesupdate=new ContentValues();
									valuesupdate.put("lastnum", good.getLastnum()+Integer.valueOf(numString));
									result=writeDatabase.update(ParamUtil.TN_BUY, valuesupdate, "_id=?", new String[]{bid+""});
								}
							}else
							{
								if(good.getLastnum()<Integer.valueOf(numString))
								{
									Toast.makeText(activity, "售出量高于目前所有货物数量",
											Toast.LENGTH_SHORT).show();
									return;
								}
								result = writeDatabase.insert(ParamUtil.TN_SELL,
										null, values);
								if(result!=-1)
								{
									ContentValues valuesupdate=new ContentValues();
									valuesupdate.put("lastnum", good.getLastnum()-Integer.valueOf(numString));
									result=writeDatabase.update(ParamUtil.TN_BUY, valuesupdate, "_id=?", new String[]{bid+""});
								}
							}
						}					
					if (result == -1)
						isSuccess = false;
					else
						isSuccess = true;
					if (!isSuccess) {
						Toast.makeText(activity, "数据库添加数据出错",
								Toast.LENGTH_SHORT).show();
						return;
					}
					if (kind == BUY_INPUT) {
						// 当前页历史记录   （成交录入与补货录入不保存提交记录只显示某图对应的数据库数据）
						CommonUtil.ignoreDoubleAdd(names, nameString);
						CommonUtil.ignoreDoubleAdd(types, typeString);
						CommonUtil.ignoreDoubleAdd(copys, copyString);
						CommonUtil.ignoreDoubleAdd(nums, numString);
						refreshSpinerEditText();
					}
					Toast.makeText(activity, "录入成功", Toast.LENGTH_SHORT).show();		
				}
			}
		});
		if (kind == BUY_INPUT)
		{
			title.setText("商品录入");
			copy.setHint("成本");
			buy_date.setVisibility(View.VISIBLE);	
		}
		if (kind == SELL_INPUT)
		{
			title.setText("成交录入");
			copy.setHint("成交价");
			buy_date.setVisibility(View.GONE);
		}
		if (kind ==ADD_INPUT)
		{
			title.setText("补货录入");
			copy.setHint("成本");
			buy_date.setVisibility(View.VISIBLE);	
		}
	}
	//查询货品记录是否存在
	BuyGood isExsitBuygoods(String imagepath,String typeString)
	{
//		System.out.println(imagepath);
//		System.out.println(typeString);
		long result=RECORD_NOEXIST;
		Cursor c = readDatabase.query(ParamUtil.TN_BUY,
				new String[] { "_id","buydate","currentdate","imagepath","goodname", "goodtype","goodcost","goodnum", "lastnum"},
				"imagepath=? and goodtype=?", new String[] {
						imagePath, typeString }, null, null,
				null);
		while (c.moveToNext()) {
			BuyGood good=new BuyGood();
			good.setId(c.getLong(0));
			good.setBuydate(c.getString(1));
			good.setCurrentdate(c.getString(2));
			good.setImagepath(c.getString(3));
			good.setGoodname(c.getString(4));
			good.setGoodtype(c.getString(5));
			good.setGoodcost(c.getString(6));
			good.setGoodnum(c.getInt(7));
			good.setLastnum(c.getInt(8));
			return good;
		}
		return null;	
	}
	//更新自定义组件的数据源
	void refreshSpinerEditText()
	{
		name.refreshSpinerData(names);
		type.refreshSpinerData(types);
		copy.refreshSpinerData(copys);
		num.refreshSpinerData(nums);
	}
	//展示数据库中录入过的数据图片--选择器中-默认显示降序图片,其他组件设置点击图片对应的最后一个记录对应的数据
	    //定义对应的组件控制handler
	private Handler imageControlHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0)
			{
				List<BuyGood> gooddata=(List<BuyGood>) msg.getData().getSerializable("gooddata");
				String path=msg.getData().getString("path");
				imagePath=path;
				if(gooddata==null||path==null)
					return;
				List<String> datelist=new ArrayList<String>();
				for(BuyGood good:gooddata)
				{
					String pathg=good.getImagepath();
					if(pathg.equalsIgnoreCase(path))
					{
						CommonUtil.ignoreDoubleAdd(names, good.getGoodname());
						CommonUtil.ignoreDoubleAdd(types, good.getGoodtype());
						CommonUtil.ignoreDoubleAdd(copys, good.getGoodcost());
						CommonUtil.ignoreDoubleAdd(nums, good.getLastnum()+"");
						CommonUtil.ignoreDoubleAdd(datelist, good.getBuydate());
					}
				}
				refreshSpinerEditText();
				imageLoader.showLocalImage(activity, cacheDir, imagePath, photoImage, null, 0);
				name.setText(names.get(names.size()-1));
				type.setText(types.get(types.size()-1));
				copy.setText(copys.get(copys.size()-1));
				num.setText(nums.get(nums.size()-1));
				buy_date.setText(datelist.get(datelist.size()-1));
			}
		};
	};
	protected void showdbImages() {
		// TODO Auto-generated method stub
		Cursor c=readDatabase.query(ParamUtil.TN_BUY, new String[]{"imagepath","goodname","goodtype","goodcost","lastnum","buydate"},null ,null, null, null, " _id ");
		List<String> imagedata=new ArrayList<String>();
		List<BuyGood> gooddata=new ArrayList<BuyGood>();
		while(c.moveToNext())
		{
			
			String path=c.getString(0);
			CommonUtil.ignoreDoubleAdd(imagedata, path);
			BuyGood good=new BuyGood();
			good.setImagepath(path);
			good.setGoodname(c.getString(1));
			good.setGoodtype(c.getString(2));
			good.setGoodcost(c.getString(3));
			good.setLastnum(c.getInt(4));
			good.setBuydate(c.getString(5));
			gooddata.add(good);
		}
		if(imagedata.size()==0)
		{
			Toast.makeText(activity,"数据库中尚无记录", Toast.LENGTH_SHORT).show();
			return;
		}	
    	new SpinerListDialog(activity).showSpinerList(imagedata,gooddata,imageControlHandler);
	}
	// 调用系统默认相机相册程序的返回结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != RESULT_OK) {
			photoImage.setEnabled(true);
			return;
		}
		imagePath=takeInImageUtil.onActivityResult(requestCode, resultCode, data);
		if (imagePath != null) {
			imageLoader.showLocalImage(activity, cacheDir, imagePath, photoImage, null, 0);
		}
		photoImage.setEnabled(true);
	}
    //判断文本是否无效
	private boolean isNullString(String testString) {
//		String testString=testView.getText().toString();
		if (testString == null || testString.equals("")
				|| testString.equals("null")) {
			return true;
		}
		return false;
	}
	//判断价格栏输入是否有效
	private  boolean isCopyFit(String copyString)
	{
	    
		char firstChar=copyString.charAt(0);//不为.
		int pointfirst=copyString.indexOf(".");

		int pointlast=copyString.lastIndexOf(".");

		if((firstChar!='.')&&(pointfirst==pointlast))
		{
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		writeDatabase.close();
		readDatabase.close();
	}
}
