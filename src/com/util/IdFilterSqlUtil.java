package com.util;

import java.util.Arrays;

public class IdFilterSqlUtil {
	private final static int PREFIX_LEN = 4;// 4=" or ".length();

	/**
	 * 阀值.
	 */
	public final static int THRESHOLD = 5;

	/**
	 * 由于数据库对in操作所能接受的参数个数的一些限制,在数据比较多的时候不能直接拼接SQL完成.
	 * (目前所知,oracle限制为1000,SQLServer的上限为2000). 一般用于WCM对象ID序列的拼接.
	 * 
	 * 主要算法是当个数超出阀值时对值进行排序,然后以闭区间分割.
	 * 
	 * TODO:如果ID序列很稀疏的情况怎么办?(压缩效果有限)
	 * 
	 * @param fieldName
	 *            检索字段名
	 * @param values
	 *            检索的值序列,必须是以逗号','分隔的整数序列.
	 * @return RangeSQL
	 * 
	 */
	public final static RangeSQL make(String fieldName, String values) {
		int[] ids = StringUtil.splitToInt(values, ",");
		StringBuffer buff = new StringBuffer(512);
		if (ids.length <= THRESHOLD) {
			buff.append(fieldName);
			buff.append(" in (").append(values).append(')');
			return new RangeSQL(buff.toString().intern());
		}

		Arrays.sort(ids);
		int[] range = new int[ids.length];
		int index = 0;
		for (int i = 0, len = ids.length; i < len; i++) {
			int current = ids[i];
			int left = current;
			while (i < len - 1
					&& (ids[i + 1] == current + 1 || ids[i + 1] == current)) {
				current = ids[++i];
			}
			if (left == current) {
				buff.append(" or ");
				buff.append(fieldName);
				buff.append("=?");
				range[index++] = left;
			} else {
				buff.append(" or ");
				buff.append(fieldName);
				buff.append(">=? and ");
				buff.append(fieldName);
				buff.append("<=?");
				range[index++] = left;
				range[index++] = current;
			}
		}

		int[] v = new int[index];
		System.arraycopy(range, 0, v, 0, index);
		return new RangeSQL("(" + buff.substring(PREFIX_LEN).intern() + ")", v);
	}

	/**
	 * 与make方法进行相同的处理,所不同的是返回的是完整的语句,不使用占位符.
	 * 
	 */
	public static String makeAsString(String fieldName, String values) {
		int[] ids = StringUtil.splitToInt(values, ",");
		StringBuffer buff = new StringBuffer(512);
		if (ids.length <= THRESHOLD) {
			buff.append(fieldName);
			buff.append(" in (").append(values).append(')');
			return buff.toString().intern();
		}

		Arrays.sort(ids);
		for (int i = 0, len = ids.length; i < len; i++) {
			int current = ids[i];
			int left = current;
			while (i < len - 1
					&& (ids[i + 1] == current + 1 || ids[i + 1] == current)) {
				current = ids[++i];
			}
			if (left == current) {
				buff.append(" or ");
				buff.append(fieldName);
				buff.append("=");
				buff.append(left);
			} else {
				buff.append(" or ");
				buff.append(fieldName);
				buff.append(">=");
				buff.append(left);
				buff.append(" and ");
				buff.append(fieldName);
				buff.append("<=");
				buff.append(current);
			}
		}

		return "(" + buff.substring(PREFIX_LEN).intern() + ")";
	}

	/**
	 * 区间化的SQL.
	 */
	public static class RangeSQL {
		private String sql;
		private int[] values;

		RangeSQL(String sql) {
			this.sql = sql;
		}

		RangeSQL(String sql, int[] values) {
			this.sql = sql;
			this.values = values;
		}

		/**
		 * SQL语句.
		 * 
		 * @return
		 */
		public final String getSql() {
			return sql;
		}

		/**
		 * 值,可能为<code>null</code>
		 * 
		 * @return
		 */
		public final int[] getValues() {
			return values;
		}
		
		
		public static void main(String[] args) {
			
			String str = IdFilterSqlUtil.make("docid", "123,12,234,11,67,100,34580,23,45,67").getSql();
			
			String strs = IdFilterSqlUtil.makeAsString("docid", "123,12,234,11,67,100,34580,23,45,67").toString();
			
			System.out.println(str);
			System.out.println(strs);
			
		}
	}
}
