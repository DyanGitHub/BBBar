package com.example.bbbar.sqlUtil;

public class SellGood {
	private long id;
	private long bid;//购入信息id
	private String  currentdate;//录入时间
	private String price;//成交价格
	private int num;//成交数量
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getBid() {
		return bid;
	}
	public void setBid(long bid) {
		this.bid = bid;
	}
	public String getCurrentdate() {
		return currentdate;
	}
	public void setCurrentdate(String currentdate) {
		this.currentdate = currentdate;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
}
