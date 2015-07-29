package com.example.bbbar.sqlUtil;

import com.example.bbbar.Util.ParamUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbBBar extends SQLiteOpenHelper {
	public DbBBar(Context context) {
		super(context, "dbbbar", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//创建数据库内部的结构，如表
		//执行一个sql语句
		db.execSQL("create table  IF NOT EXISTS buygoods(" 
		+ "_id Integer   not null ,"
		+ "buydate text not null," //进货日期
		+ "currentdate text not null,"//录入日期
		+ "imagepath text not null,"
		+ "goodname text not null,"
		+ "goodtype text not null,"
		+ "goodcost text not null,"//只记录首次进货价，盈亏计算是需要结合补货价格 有先算入首次进货量
		+ "goodnum integer ,"
		+ "lastnum integer )"//包含补货数量
		);
		db.execSQL("create table  IF NOT EXISTS sellgoods(" 
		+ "_id Integer    not null,"
		+ "bid Integer  ,"	//商品表的id
		+ "currentdate text not null,"
		+ "price text not null,"
		+ "num integer )"
		);
		db.execSQL("create table  IF NOT EXISTS addgoods(" 
		+ "_id Integer    not null,"
		+ "bid Integer  ,"	//商品表的id
		+ "currentdate text not null,"
		+ "price text not null,"
		+ "buydate text not null,"
		+ "num integer )"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        //数据库升级时使用
		db.execSQL("DROP TABLE IF EXISTS " + ParamUtil.TN_SELL);  
		db.execSQL("DROP TABLE IF EXISTS " + ParamUtil.TN_BUY);  
		onCreate(db);
	}

}
