package com.example.bbbar.StatisticsInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.bbbar.R;
import com.example.bbbar.Util.CommonUtil;
import com.example.bbbar.Util.ImageLoaderHelper;
import com.example.bbbar.Util.ParamUtil;
import com.example.bbbar.sqlUtil.AddGood;
import com.example.bbbar.sqlUtil.BuyGood;
import com.example.bbbar.sqlUtil.DbBBar;
import com.example.bbbar.sqlUtil.SellGood;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class StatisticsIndexActivity extends Activity{
	private Activity activity;
	private ViewPager vp;
	private ViewPagerAdapter adapter;
	private SQLiteDatabase readDatabase;
	private ArrayList<BuyGood> buygoods=new ArrayList<BuyGood>();
	private ArrayList<SellGood> sellgoods=new ArrayList<SellGood>();
	private ArrayList<AddGood> addgoods=new ArrayList<AddGood>();
	public static int BUY_INPUT =1;
	public static int SELL_INPUT =2;
	public static int ADD_INPUT =3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atv_statisticsindex);
		activity=this;
		readDatabase=new DbBBar(activity).getReadableDatabase();
		//获取数据库原始数据
		initData();
		initView();	
	}
	void initView()
	{
		ImageView back=(ImageView)findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		TextView title=(TextView)findViewById(R.id.title);
		title.setText("日统计");
		vp=(ViewPager) findViewById(R.id.vp);
		List<View> viewlist=getDataView();
		adapter=new ViewPagerAdapter(viewlist);
		vp.setAdapter(adapter);	
//		vp.setCurrentItem(viewlist.size()*100);
		System.out.println("-----:"+viewlist.size());
	}
	void initData()
	{
		//buygoods
		Cursor cursor=readDatabase.query(ParamUtil.TN_BUY, new String[]{"_id","buydate","currentdate","imagepath","goodname","goodtype","goodcost","goodnum","lastnum"}, null, null, null, null, null);
		while(cursor.moveToNext())
		{
			BuyGood buygood=new BuyGood();
			buygood.setId(cursor.getLong(0));
			buygood.setBuydate(cursor.getString(1));
			buygood.setCurrentdate(cursor.getString(2));
			buygood.setImagepath(cursor.getString(3));
			buygood.setGoodname(cursor.getString(4));
			buygood.setGoodtype(cursor.getString(5));
			buygood.setGoodcost(cursor.getString(6));
			buygood.setGoodnum(cursor.getInt(7));
			buygood.setLastnum(cursor.getInt(8));
			buygoods.add(buygood);
		}
		//sellgoods
	    cursor=readDatabase.query(ParamUtil.TN_SELL, new String[]{"_id","bid","currentdate","price","num"}, null, null, null, null, null);
	    while(cursor.moveToNext())
	    {
	    	SellGood sellgood=new SellGood() ;
	    	sellgood.setId(cursor.getLong(0));
	    	sellgood.setBid(cursor.getLong(1));
	    	sellgood.setCurrentdate(cursor.getString(2));
	    	sellgood.setPrice(cursor.getString(3));
	    	sellgood.setNum(cursor.getInt(4));
	    	sellgoods.add(sellgood);
	    }
	    //addgoods
	    cursor=readDatabase.query(ParamUtil.TN_ADD, new String[]{"_id","bid","currentdate","price","buydate","num"}, null, null, null, null, null);
	    while(cursor.moveToNext())
	    {
	    	AddGood addgood=new AddGood();
	    	addgood.setId(cursor.getLong(0));
	    	addgood.setBid(cursor.getLong(1));
	    	addgood.setCurrentdate(cursor.getString(2));
	    	addgood.setPrice(cursor.getString(3));
	    	addgood.setBuydate(cursor.getString(4));
	    	addgood.setNum(cursor.getInt(5));
	    	addgoods.add(addgood);
	    }
	}
	//获取滚动页的目标视图
	List<View> getDataView()
	{
		//获取日期数据集
		List<String> currentdates=new ArrayList<String>();
		for(BuyGood buygood:buygoods)
		{
			CommonUtil.ignoreDoubleAdd(currentdates, buygood.getCurrentdate());
		}
		for(SellGood sellgood:sellgoods)
		{
			CommonUtil.ignoreDoubleAdd(currentdates, sellgood.getCurrentdate());
		}
		for(AddGood addgood:addgoods)
		{
			CommonUtil.ignoreDoubleAdd(currentdates, addgood.getCurrentdate());
		}
		//形成视图集
		List<View> viewlist=new ArrayList<View>();
		for(String currentdate:currentdates)
		{
			View view=getLayoutInflater().inflate(R.layout.record_paperitem, null, false);
			ScrollView scrollView=(ScrollView)view.findViewById(R.id.scrollView);
			scrollView.smoothScrollTo(0, 0);
			
			TextView datebar=(TextView) view.findViewById(R.id.datebar);
			datebar.setText(currentdate);
			TextView bangbar=(TextView)view.findViewById(R.id.bangbar);
			
			ListView buyrecordbar=(ListView)view.findViewById(R.id.buyrecordbar);
			ListView sellrecordbar=(ListView)view.findViewById(R.id.sellrecordbar);
			ListView addrecordbar=(ListView)view.findViewById(R.id.addrecordbar);
			View buybar=view.findViewById(R.id.buybar);
			View sellbar=view.findViewById(R.id.sellbar);
			View addbar=view.findViewById(R.id.addbar);
			ArrayList<BuyGood> currentBuyRecords=new ArrayList<BuyGood>();
			for(BuyGood good:buygoods)
			{
				if(good.getCurrentdate().equalsIgnoreCase(currentdate))
				{
					currentBuyRecords.add(good);
				}
			}
			ArrayList<SellGood> currentSellRecords=new ArrayList<SellGood>();
			for(SellGood good:sellgoods)
			{
				if(good.getCurrentdate().equalsIgnoreCase(currentdate))
				{
					currentSellRecords.add(good);
				}
			}
			ArrayList<AddGood> currentAddRecords=new ArrayList<AddGood>();
			for(AddGood good:addgoods)
			{
				if(good.getCurrentdate().equalsIgnoreCase(currentdate))
				{
					currentAddRecords.add(good);
				}
			}
			disShowBar(currentBuyRecords.size(), buybar);
			disShowBar(currentSellRecords.size(), sellbar);
			disShowBar(currentAddRecords.size(), addbar);
			RecordAdapter buyAdapter=new RecordAdapter(BUY_INPUT, currentBuyRecords);
			RecordAdapter sellAdapter=new RecordAdapter(SELL_INPUT, currentSellRecords);
			RecordAdapter addAdapter=new RecordAdapter(ADD_INPUT, currentAddRecords);
			buyrecordbar.setAdapter(buyAdapter);
			sellrecordbar.setAdapter(sellAdapter);
			addrecordbar.setAdapter(addAdapter);
			CommonUtil.setListViewHeightBasedOnChildren(buyrecordbar);
			CommonUtil.setListViewHeightBasedOnChildren(sellrecordbar);
			CommonUtil.setListViewHeightBasedOnChildren(addrecordbar);
			bangbar.setText(getSellResult(currentSellRecords)+"/"+getCostResult(currentSellRecords));
			viewlist.add(view);
		}		
		return viewlist;
	}
	//计算当日成交金
	String getSellResult(ArrayList<SellGood> currentsellgoods)
	{
		double sellresult=0.0;
		if(currentsellgoods!=null)
		{
			for(SellGood good:currentsellgoods)
			{
				sellresult+=(Double.valueOf(good.getPrice()))*good.getNum();
			}
		}
		String sr=sellresult+"";
		sr=sr.substring(0, sr.indexOf(".")+2);
		return sr;
	}
	//计算当日成交对应的成本金--采用平均成本
	String getCostResult(ArrayList<SellGood> currentsellgoods)
	{
		double costresult=0.0;
		if(currentsellgoods!=null)
		{
			for(SellGood good:currentsellgoods)
			{
				BuyGood buygood=getBuyRecord(good.getBid());
				int buynum=buygood.getGoodnum();
				double buycost=Double.valueOf(buygood.getGoodcost());
				int sellnum=good.getNum();
				double addcostall=0.0;
				int addnumall=0;
				ArrayList<AddGood> addbgood = getAddRecord(good.getBid());
				if (addbgood != null) {
					for (AddGood addgood : addbgood) {
						int addnum = addgood.getNum();
						double addcost = Double.valueOf(addgood.getPrice());
						addnumall += addnum;
						addcostall += addnum * addcost;
					}
				}
				costresult+=(buynum*buycost+addcostall)/(buynum+addnumall)*sellnum;	
			}
		}
		String sr=costresult+"";
		sr=sr.substring(0, sr.indexOf(".")+2);
		return sr;
	}
	//隐藏每页中每天对应内容的列表视图
	void disShowBar(int size,View view)
	{
		if(size==0)
		{
			view.setVisibility(View.GONE);
		}
	}
	public class RecordAdapter extends BaseAdapter
	{
		int kind=0;
		ArrayList<BuyGood> bgs=null;
		ArrayList<SellGood> sgs=null;
		ArrayList<AddGood> ags=null;
        public RecordAdapter(int kind,Object objects)
        {
        	this.kind=kind;
        	switch (kind) {
			case 1:
				this.bgs=(ArrayList<BuyGood>)objects;
				
				break;
			case 2:
				this.sgs=(ArrayList<SellGood>)objects;
				
				break;
			case 3:
				this.ags=(ArrayList<AddGood>)objects;
				
				break;
			default:
				break;
			}
        }
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			switch (kind) {
			case 1:
				if(bgs!=null)
					return bgs.size();
				break;
			case 2:
				if(sgs!=null)
					return sgs.size();
				break;
			case 3:
				if(ags!=null)
					return ags.size();
				break;
			default:
				break;
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			switch (kind) {
			case 1:
				if(bgs!=null&&bgs.size()!=0)
					return bgs.get(position);
				break;
			case 2:
				if(sgs!=null&&sgs.size()!=0)
					return sgs.get(position);
				break;
			case 3:
				if(ags!=null&&ags.size()!=0)
					return ags.get(position);
				break;
			default:
				break;
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if((bgs!=null&&bgs.size()!=0)||(sgs!=null&&sgs.size()!=0)||(ags!=null&&ags.size()!=0))
			{
				if(convertView==null)
				{
					convertView=getLayoutInflater().inflate(R.layout.record_item, null,false);				
				}
				ImageView image=(ImageView) convertView.findViewById(R.id.image);
				TextView name=(TextView) convertView.findViewById(R.id.name);
				TextView type=(TextView) convertView.findViewById(R.id.type);
				TextView price=(TextView) convertView.findViewById(R.id.price);
				TextView count=(TextView) convertView.findViewById(R.id.count);
				String imagepath=null;
				String nameString=null;
				String typeString=null;
				String priceString =null;
				String countString =null;
				switch (kind) {
				case 1:
					BuyGood buygood=bgs.get(position);
					imagepath=buygood.getImagepath();
					nameString=buygood.getGoodname();
					typeString=buygood.getGoodtype();
					priceString=buygood.getGoodcost();
					countString=buygood.getGoodnum()+"";
					break;
					
				case 2:
					SellGood sellgood=sgs.get(position);
					BuyGood bg=getBuyRecord(sellgood.getBid());
					imagepath=bg.getImagepath();
					nameString=bg.getGoodname();
					typeString=bg.getGoodtype();
					priceString=sellgood.getPrice();
					countString=sellgood.getNum()+"";
					break;
				case 3:
					AddGood addgood=ags.get(position);
					BuyGood abg=getBuyRecord(addgood.getBid());
					imagepath=abg.getImagepath();
					nameString=abg.getGoodname();
					typeString=abg.getGoodtype();
					priceString=addgood.getPrice();
					countString=addgood.getNum()+"";		
				default:
					break;
				}
				if(imagepath!=null&&nameString!=null&&typeString!=null&&priceString!=null&&countString!=null)
				{
					File cacheDir=StorageUtils.getOwnCacheDirectory(activity, ParamUtil.IMAGElOADERCACHE);
					new ImageLoaderHelper().showLocalImage(activity, cacheDir, imagepath, image, null, 0);
					name.setText(nameString);
					type.setText(typeString);
					price.setText(priceString);
					count.setText(countString);
				}
				return convertView;
			}
			return null;
		}
		
	}
	ArrayList<AddGood> getAddRecord(long bid)
	{
		ArrayList<AddGood> addRecord=new ArrayList<AddGood>();
		for(AddGood good:addgoods)
		{
			if(good.getBid()==bid)
			{
				addRecord.add(good);
			}
		}
		return addRecord;
	}
	BuyGood getBuyRecord(long bid)
	{
		for(BuyGood good:buygoods)
		{
			if(good.getId()==bid)
			{
				return good;
			}
		}
		return null;
	}
}
