package com.example.bbbar.sqlUtil;

public class BuyGood {
	private long id;
	private String buydate;//购入时间
	private String currentdate;//录入时间
	private String imagepath;//图片路径
	private String goodname;//商品名
	private String goodtype;//商品规格
	private String goodcost;//成本
	private int goodnum;//数量
	private int lastnum;//滞留量
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getBuydate() {
		return buydate;
	}
	public void setBuydate(String buydate) {
		this.buydate = buydate;
	}
	public String getCurrentdate() {
		return currentdate;
	}
	public void setCurrentdate(String currentdate) {
		this.currentdate = currentdate;
	}
	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	public String getGoodname() {
		return goodname;
	}
	public void setGoodname(String goodname) {
		this.goodname = goodname;
	}
	public String getGoodtype() {
		return goodtype;
	}
	public void setGoodtype(String goodtype) {
		this.goodtype = goodtype;
	}
	public String getGoodcost() {
		return goodcost;
	}
	public void setGoodcost(String goodcost) {
		this.goodcost = goodcost;
	}
	public int getGoodnum() {
		return goodnum;
	}
	public void setGoodnum(int goodnum) {
		this.goodnum = goodnum;
	}
	public int getLastnum() {
		return lastnum;
	}
	public void setLastnum(int lastnum) {
		this.lastnum = lastnum;
	}
	
}
