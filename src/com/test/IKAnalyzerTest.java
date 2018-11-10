package com.test;


import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IKAnalyzerTest {
	
	public static void main(String[] args) {
		String keyWord = "甲级资质";
		//String text = "关于“池州铜冠花园供电工程招标公告” 投标人资格要求有关内容调整的通知 项目编号 所属地区 铜陵市 项目名称 关于“池州铜冠花园供电工程招标公告” 投标人资格要求有关内容调整的通知 发布时间 2015年11月18日 截止时间 2015年11月23日 关于“池州铜冠花园供电工程招标公告” 投标人资格要求有关内容调整的通知 各潜在投标人：经招标人研究决定，2015年11月12日发布的池州铜冠花园供电工程招标公告中三、投标人资格要求“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有电监会颁发电力设施许可证承装、承修、承试类五级（含）以上资质；投标人必须为取得池州市供电公司施工许可备案的企业”现改为“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有国家能源局华东电监局颁发电力设施许可证承装、承修、承试类五级（含）以上资质”。 以上内容特此通知。";
		String keywords = "10_电力工程 施工 总承包三级";
		String text = "投标人资格要求“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有电监会颁发电力设施许可证承装、承修、承试类五级（含）以上资质；投标人必须为取得池州市供电公司施工许可备案的企业”现改为“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有国家能源局华东电监局颁发电力设施许可证承装、承修、承试类五级（含）以上资质”。 以上内容特此通知。";
		String text1 = "具有独立法人资格及相应技术、经济能力和良好的信誉电力工程施工总承包三级（含）以上资质，同时具有电监会颁发电力设施许可证承装、承修、承试类五级（含）以上资质；投标人必须为取得池州市供电公司施工许可备案的企业”现改为“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有国家能源局华东电监局颁发电力设施许可证承装、承修、承试类五级（含）以上资质”。 以上内容特此通知。";
		String text2 ="广东曦达工程咨询有限公司";//"广州电力工程监理有限公司";
		String text3 ="供应商须具备建筑装修装饰工程专业承包贰级及以上资质";
		String text4 = "本次招标要求投标人须凡具有市政资质丙级以上（含丙级）或水利资质丙级以上（含丙级）的监理企业，类似业绩，并在人员、设备、资金等方面具有相应的能力，项目总监须具备市政建筑专业注册监理工程师执业资格，且未担任其他在施建设工程项目的项目总监。";
		String text5 ="须具备独立法人资格，具有公路工程施工总承包叁级（含叁级）及以上资质或公路养护工程二类乙级（含乙级）及以上从业资质，";
		//创建IKAnalyzer中文分词对象
		String text7 = "房屋建筑工程施工总承包企业一级资质；";
		String text6 = "2012年欧洲杯四强赛";
		IKAnalyzer analyzer = new IKAnalyzer();
		// 使用智能分词
		analyzer.setUseSmart(true);
		// 打印分词结果
		try {
			printAnalysisResult(analyzer, "管理办法");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打印出给定分词器的分词结果
	 * 
	 * @param analyzer
	 *            分词器
	 * @param keyWord
	 *            关键词
	 * @throws Exception
	 */
	private static void printAnalysisResult(Analyzer analyzer, String keyWord)
			throws Exception {
		System.out.println("["+keyWord+"]分词效果如下");
		TokenStream tokenStream = analyzer.tokenStream("content",
				new StringReader(keyWord));
		tokenStream.addAttribute(CharTermAttribute.class);
		int i =0;
		while (tokenStream.incrementToken()) {
			CharTermAttribute charTermAttribute = tokenStream
					.getAttribute(CharTermAttribute.class);
			i++;
			System.out.println(i+"序号："+charTermAttribute.toString());

		}
	}
}

