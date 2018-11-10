package com.test.bean;

import java.util.List;

public class PageBean {
	
	private int pagesize;
	private int currentpage;
	private int totalrecord;
	private String searchtype;
	private String processwhere;
	private List<Doc> dos;
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public int getCurrentpage() {
		return currentpage;
	}
	public void setCurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}
	public int getTotalrecord() {
		return totalrecord;
	}
	public void setTotalrecord(int totalrecord) {
		this.totalrecord = totalrecord;
	}
	public List<Doc> getDos() {
		return dos;
	}
	public void setDos(List<Doc> dos) {
		this.dos = dos;
	}
	public String getProcesswhere() {
		return processwhere;
	}
	public void setProcesswhere(String processwhere) {
		this.processwhere = processwhere;
	}
	public String getSearchtype() {
		return searchtype;
	}
	public void setSearchtype(String searchtype) {
		this.searchtype = searchtype;
	}
	
}
