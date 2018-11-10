package com.core.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VDocument {
	private String name ;//索引视图名称
	private String path;//索引存放路径
	private String indexkey;//维护索引主键信息
	private String dbname;//数据源信息
	private String indexview;//维护索引同步视图
	private String indextable;//索引同步表
	private List<VField> fieldlist= new ArrayList<VField>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<VField> getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(List<VField> fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getIndexkey() {
		return indexkey;
	}
	public void setIndexkey(String indexkey) {
		this.indexkey = indexkey;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getIndexview() {
		return indexview;
	}
	public void setIndexview(String indexview) {
		this.indexview = indexview;
	}
	public String getIndextable() {
		return name+"_TIME$_TEMP";
	}
	public void setIndextable(String indextable) {
		this.indextable = indextable;
	}

}
