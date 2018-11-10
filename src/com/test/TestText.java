package com.test;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.sun.xml.internal.ws.util.pipe.StandalonePipeAssembler;
import com.util.StringUtil;

public class TestText {
	
	public static void main(String[] args) {
		
		 String fieldName = "text";// 检索内容
		/* String text = "1 投标申请人须是具备建设行政主管部门核发的房屋建筑工程施工总承包三级及以上资质或钢结构工程专业承包企业资质三级及以上资质，及安全生产许可证（副本）原件及复印件的法人或其他组织， 近三年承担过类似工程等 业绩，并在人员、设备、资金等方面具有相应的施工能力。"

+"2 投标单位拟派出的项目经理或注册建造师须是具备建设行政主管部门核发的 建筑工程 专业 贰级注册建造师执业资格及以上资质。"

+"3 拟派出的项目管理人员，应无在建工程，否则按废标处理；投标单位的项目经理或注册建造师中标后需到本项目招投标监督主管部门办理备案手续。"

+"4 本次招标 不接受 联合体投标。联合体投标的，应满足下列要求：_ --- 。"

+"5 各投标人均可就上述标段中的 壹 个标段投标。"

+"6 入吉企业在我省承揽工程应在中标后七个工作日内办理备案。（详见吉建管〔2007〕17号、吉建管[2011]32号、吉建管〔2014〕6号文件）"

+"7 拒绝列入政府不良行为记录期间的企业或个人投标。";
*/
		 
		 
		 //String text="1、具有独立法人资格及相应技术、经济能力和良好的信誉，近三年具有类似规模医院装饰工程设计业绩； 2、具备建筑装饰工程设计专项甲级资质。（投标人具备的资质应满足室内装修、室内装饰、建筑外观设计等资质要求） 3、参加设计的项目负责人和主要专业负责人须具有高级工程师资格，且具有类似医院工程设计经验。 4、本项目不接受联合体投标。 （请各潜在投标人必须检查确认自身的资格满足要求后参与投标，否则责任自负）";
		 String text = "DX000032投标人资格要求“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有电监会颁发电力设施许可证承装、承修、承试类五级（含）以上资质；投标人必须为取得池州市供电公司施工许可备案的企业”现改为“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有国家能源局华东电监局颁发电力设施许可证承装、承修、承试类五级（含）以上资质”。 以上内容特此通知。";
		 //System.out.println(text.replace("甲级", "**"));
		 // 实例化IKAnalyzer分词器
		IKAnalyzer analyzer = new IKAnalyzer();//new SimpleAnalyzer(); 
		//Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		 //analyzer.setUseSmart(true);
		 //analyzer.setUseSmart(false);
		 Directory directory = null;
		 IndexWriter iwriter = null;
		 IndexSearcher isearcher = null;
		 try {
		 // 建立内存索引对象
		 directory = new RAMDirectory();
		 iwriter = new IndexWriter(directory, analyzer, true,
		 IndexWriter.MaxFieldLength.LIMITED);
		 Document doc = new Document();
		 doc.add(new Field(fieldName, text, Field.Store.YES,
		 Field.Index.ANALYZED_NO_NORMS));
		 iwriter.addDocument(doc);
		 iwriter.close();
		 // 实例化搜索器
		 isearcher = new IndexSearcher(directory);// 在索引器中使用IKSimilarity相似度评估器
		 Similarity similarity = new DefaultSimilarity();
		 isearcher.setSimilarity(similarity);
		 //建筑行业 人防工程 专业 甲级
		 String keyword ="dx000032";//使用QueryParser查询分析器构造Query对象
		 //String keyword ="施工总承包";
		 QueryParser qp =new QueryParser(Version.LUCENE_36,fieldName, analyzer);
		 qp.setDefaultOperator(QueryParser.AND_OPERATOR);
		 //Query query1 = qp.parse(keyword);
		 String keyvalue = "\""+keyword+"\"";
		 Query query1 = qp.parse(keyvalue);
		 
		 TopDocs topDocs = isearcher.search(query1, 1);
		 System.out.println("命中：" + topDocs.totalHits);// 输出结果

		 ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		 for (int i = 0; i < topDocs.totalHits; i++) {
			 Document targetDoc = isearcher.doc(scoreDocs[i].doc);
			String word = targetDoc.get("text");
			System.out.println(word);
			if(word!=null&&!"".equals(word)){
				System.out.println(word.indexOf(keyword));
				int start = word.indexOf(keyword)-50;
				if(start<0){
					start = 0;
				}
				int end = word.indexOf(keyword)+50;
				 
				System.out.println(word.substring(start,end));
			}
			
			//System.out.println("内容：" + targetDoc.get(arg0)+scoreDocs[i].score);
		 }

		 } catch (CorruptIndexException e) {

		 e.printStackTrace();

		 } catch (LockObtainFailedException e) {

		 e.printStackTrace();

		 } catch (IOException e) {

		 e.printStackTrace();

		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		 if (isearcher != null) {

		 try {

		 isearcher.close();

		 } catch (IOException e) {

		 e.printStackTrace();

		 }

		 }

		 if (directory != null) {

		 try {

		 directory.close();

		 } catch (IOException e) {

		 e.printStackTrace();

		 }

		 }

		 }

		 }
		
	

		public  String getBestFragment(String keyWord,String field,
				String word) {
			String c ="";
			try {
				Query query = null;
				if(null==word||"".equals(word))
				return word;
				
				if("doctitle".equals(field)){
					word = StringUtil.truncateStr(word, 120);
				}else if("doccontent".equals(field)){
					word = StringUtil.truncateStr(word, 120);
				}else {
					word =StringUtil.truncateStr(word, 200);
				}
				
				if(keyWord!=null&&!"".equals(keyWord)){
					QueryParser queryParser  =new QueryParser(Version.LUCENE_36,  field,  new IKAnalyzer());
					query = queryParser.parse(keyWord);
					SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
							"%10font color='red'%20", "%10/font%20");
					Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
							new QueryScorer(query));
					highlighter.setTextFragmenter(new SimpleFragmenter(150));
					c = highlighter.getBestFragment(new IKAnalyzer(),field, word);
					
					if(c==null){
						 c = word; 
					 }
				}else{
					c = word;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return c;
		}

}

