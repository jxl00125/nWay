package com.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.core.bean.VDocument;
import com.util.ParseXml;

public class TestSearch {
	
	
	@Test
	public void testDelete(){
		String FilePath ="D://trsserver//bxkcdata//v_document"; //vd.getPath();
		// 打开索引文件
		Directory dir = null;
		File file = new File(FilePath);// 这个是索引库
		if (!file.exists()) {
			file.mkdirs();
		}
		IndexWriter writer =null;
		try {
			dir = FSDirectory.open(file);
			IKAnalyzer analyzer = new IKAnalyzer();
			analyzer.setUseSmart(true);
			/* 先是创建索引库在追加索引到索引库，true代表创建，false代表追加 */
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36,
					analyzer);
			//iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			 writer = new IndexWriter(dir, iwc);
			 Term term = new Term("docchannel", "109");
			 writer.deleteDocuments(term);
			 writer.commit();
			 writer.close();
			 
			 

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	public void testSearch(){
		//1、获取检索视图信息
		//2、解析检索xml信息
		//3、拼接查询条件
		//4、执行检索，获取检索结果信息
		//5、组装结果集
		//VDocument vd = ParseXml.docments.get(viewname);
		
		String path ="D://trsserver//bxkcdata1//v_document"; //vd.getPath();
		Directory dir = null;
		File file = new File(path);
		try {
			
			dir = FSDirectory.open(file);
			IndexReader reader =  IndexReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
			Similarity similarity = new DefaultSimilarity();
			Analyzer analyzer = new IKAnalyzer();
			searcher.setSimilarity(similarity);
			TopScoreDocCollector results = TopScoreDocCollector.create(searcher
					.maxDoc(), false);
			String [] fields ={"qcodes"};//索引域
			String KeyWord ="SJZH";//域值
			QueryParser qp = new QueryParser(Version.LUCENE_36, "docchannel", analyzer);
			Query q = qp.parse("52");
			
			MultiFieldQueryParser queryParser  =new MultiFieldQueryParser(Version.LUCENE_36,  fields,  analyzer);
			//TermQuery query = new TermQuery(new Term("doctitle","赵萍"));
			
			//PhraseQuery query = new PhraseQuery();
			//query.add(new Term("doctitle","oracle"));
			//query.add(new Term("doctitle",""));
			//query.add(new Term("doctitle","赵"));
			//query.add(new Term("doctitle","萍"));
			//FuzzyQuery query = new FuzzyQuery(new Term("doctitle","放假"),0.5f,0);
			BooleanQuery booleanQuery = new BooleanQuery();//布尔查询
			//booleanQuery.add(query, BooleanClause.Occur.SHOULD);
			Query query = queryParser.parse(KeyWord);
			 String fieldName = "doctitle";      
			 String queryStr = "石油";       
			searcher.search(query, results);
			TopDocs topDocs = results.topDocs();
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	            int count=0;
	            System.out.println(scoreDocs.length);
	            for(ScoreDoc scDoc : scoreDocs){
	            	Document doc  = searcher.doc(scDoc.doc);
	            	String docid = doc.get("docid");
	            	String title = doc.get("doctitle");
	            	String qcodes = doc.get("qcodes");
	            	String doccontent = doc.get("doccontent");
	            	float score = scDoc.score;
	            	count++;
	            	//if(count>=10)
	            	//	break;
	            	System.out.println("文档id："+docid+" 标题:"+title+"   资质：  "+qcodes+"  相似度："+score);
	            }
	            searcher.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
	}
	
	private BooleanQuery makequery(List<String> sProcessWhere) {
		BooleanQuery booleanQuery = new BooleanQuery();//布尔查询
		try {
			IKAnalyzer analyzer = new IKAnalyzer();
			//analyzer.setUseSmart(false);
		Query nquery =null;
		for (String query : sProcessWhere) {
			System.out.println(sProcessWhere);
			String[] datas = query.split("=");
			String key = datas[0];
			String relation =key.substring(key.length()-1);
			key = key.substring(0,key.length()-1);
			String [] fields = key.split(",");
			MultiFieldQueryParser queryParser  =new MultiFieldQueryParser(Version.LUCENE_36,  fields,  analyzer);
			String value = datas[1];
			System.out.println(fields[0].toString());
			System.out.println(value);
			nquery = queryParser.parse(value);
			if("+".equals(relation)){
				booleanQuery.add(nquery, BooleanClause.Occur.MUST);
			}else if("-".equals(relation)){
				booleanQuery.add(nquery, BooleanClause.Occur.MUST_NOT);
			}else if("*".equals(relation)){
				booleanQuery.add(nquery, BooleanClause.Occur.SHOULD);
			}
		}
		//语义检查
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return booleanQuery;
	}
	
	
	
	public void testSearch_b(){
		String path ="D://trsserver//bxkcdata//v_document"; //vd.getPath();
		Directory dir = null;
		File file = new File(path);
		
		try {
			dir = FSDirectory.open(file);
			IndexReader reader =  IndexReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
			Similarity similarity = new DefaultSimilarity();
			IKAnalyzer analyzer = new IKAnalyzer();
			analyzer.setUseSmart(true);
			searcher.setSimilarity(similarity);
			TopScoreDocCollector results = TopScoreDocCollector.create(searcher
					.maxDoc(), false);
			String [] fields ={"doctitle"};//索引域
			String KeyWord ="\"广州电力工程监理有限公司\"";//域值 (广州电力工程监理有限公司)
			//String KeyWord ="\"广东曦达工程咨询有限公司\"";//域值 (广州电力工程监理有限公司)
			//String KeyWord ="\"中标公告\"";//域值21109
			List<String> sProcessWhere =new ArrayList<String>();
			//sProcessWhere.add("infosource+="+KeyWord);
			//sProcessWhere.add("doctitle+="+KeyWord);
			sProcessWhere.add("doccontent+="+KeyWord);
			
			//sProcessWhere.add("docchannel+="+"52");
			BooleanQuery booleanQuery = makequery(sProcessWhere);
			Filter filter = null;
			
			//filter = new QueryWrapperFilter(new TermQuery(new Term("docchannel", "52")));
			QueryParser qp = new QueryParser(Version.LUCENE_36,  "docchannel",  analyzer);
			Query query = qp.parse("52");
			booleanQuery.add(query, BooleanClause.Occur.MUST);
			
			PhraseQuery pq = new PhraseQuery();
			//pq.add(new Term("doccontent", "广东曦达工程咨询有限公司"));
			//pq.add(new Term("doccontent", "广州"));
		    //pq.add(new Term("doccontent", "电力工程"));
		   // pq.add(new Term("doccontent", "监理"));
		   // pq.add(new Term("doccontent", "有限公司"));
		    pq.setSlop(2);
			searcher.search(booleanQuery, filter,results);
			TopDocs topDocs = results.topDocs();
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			System.out.println(scoreDocs.length);
	            int count=0;
	            for(ScoreDoc scDoc : scoreDocs){
	            	Document doc  = searcher.doc(scDoc.doc);
	            	String docid = doc.get("docid");
	            	String title = doc.get("doctitle");
	            	String doccontent = doc.get("doccontent");
	            	float score = scDoc.score;
	            	count++;
	            	if(count>=10)
	            		break;
	            	System.out.println("文档id："+docid+" 标题:"+title+"   内容：  "+doccontent+"  相似度："+score);
	            }
	            searcher.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	
	
	
	public void TestSearchSpan(){

		 String fieldName = "content";// 检索内容
		 	/**/ String text = "1 投标申请人须是具备建设行政主管部门核发的房屋建筑工程施工总承包三级及以上资质或钢结构工程专业承包企业资质三级及以上资质，及安全生产许可证（副本）原件及复印件的法人或其他组织， 近三年承担过类似工程等 业绩，并在人员、设备、资金等方面具有相应的施工能力。"

					+"2 投标单位拟派出的项目经理或注册建造师须是具备建设行政主管部门核发的 建筑工程 专业 贰级注册建造师执业资格及以上资质。"
					
					+"3 拟派出的项目管理人员，应无在建工程，否则按废标处理；投标单位的项目经理或注册建造师中标后需到本项目招投标监督主管部门办理备案手续。"
					
					+"4 本次招标 不接受 联合体投标。联合体投标的，应满足下列要求：_ --- 。"
					
					+"5 各投标人均可就上述标段中的 壹 个标段投标。"
					
					+"6 入吉企业在我省承揽工程应在中标后七个工作日内办理备案。（详见吉建管〔2007〕17号、吉建管[2011]32号、吉建管〔2014〕6号文件）"
					
					+"7 拒绝列入政府不良行为记录期间的企业或个人投标。";
		 	
		 
		 
		 //String text="1、具有独立法人资格及相应技术、经济能力和良好的信誉，近三年具有类似规模医院装饰工程设计业绩； 2、具备建筑装饰工程设计专项甲级资质。（投标人具备的资质应满足室内装修、室内装饰、建筑外观设计等资质要求） 3、参加设计的项目负责人和主要专业负责人须具有高级工程师资格，且具有类似医院工程设计经验。 4、本项目不接受联合体投标。 （请各潜在投标人必须检查确认自身的资格满足要求后参与投标，否则责任自负）";
		 String text1 = "具有独立法人资格及相应技术、经济能力和良好的信誉电力行业施工总承包资质三级（含）以上资质，同时具有电监会颁发电力设施许可证承装、承修、承试类五级（含）以上资质；投标人必须为取得池州市供电公司施工许可备案的企业”现改为“2、投标人须具备有效的送变电工程专业承包三级（含）以上资质或电力工程施工总承包三级（含）以上资质，同时具有国家能源局华东电监局颁发电力设施许可证承装、承修、承试类五级（含）以上资质”。 以上内容特此通知。";
		 	//String text1 = "电力工程施工总承包三级";
		 	// 实例化IKAnalyzer分词器
		 //String text = "the quick brown fox jumps dog over the lazy ";
		 //Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		 //Analyzer analyzer =  new ChineseAnalyzer();
		 IKAnalyzer analyzer = new IKAnalyzer();//new SimpleAnalyzer(); 
	     //analyzer.setUseSmart(true);
		 Directory directory = null;
		 IndexWriter iwriter = null;
		 IndexSearcher isearcher = null;
		 try {
		 // 建立内存索引对象
		 directory = new RAMDirectory();
		 iwriter = new IndexWriter(directory, analyzer, true,
		 IndexWriter.MaxFieldLength.LIMITED);
		 Document doc = new Document();
		 doc.add(new Field(fieldName, text1, Field.Store.YES,
		 Field.Index.ANALYZED));
		 iwriter.addDocument(doc);
		 iwriter.close();
		 // 实例化搜索器
		 isearcher = new IndexSearcher(directory);// 在索引器中使用IKSimilarity相似度评估器
		 Similarity similarity = new DefaultSimilarity();
		 isearcher.setSimilarity(similarity);
		 
		 //SpanTermQuery quick = new SpanTermQuery(new Term("content","quick"));
	     //SpanTermQuery brown = new SpanTermQuery(new Term("content","brown"));
	     //SpanTermQuery dog = new SpanTermQuery(new Term("content","dog"));
	     //SpanTermQuery quick = new SpanTermQuery(new Term("content","电力工程"));
	     //SpanTermQuery brown = new SpanTermQuery(new Term("content","施工"));
	     SpanTermQuery brown = new SpanTermQuery(new Term("content","电力施工"));
	     SpanTermQuery dog = new SpanTermQuery(new Term("content","总承包"));
	     SpanTermQuery quick1 = new SpanTermQuery(new Term("content","三级"));
	     
		 SpanQuery[] quick_brown_dog=new SpanQuery[]{brown,dog,quick1};
		 
		 int slop = 20;
	     SpanNearQuery query=new SpanNearQuery(quick_brown_dog,slop,false);
	     
	     System.out.println(query.getSlop());
	     PhraseQuery pq = new PhraseQuery();
	     pq.setSlop(16);
	     
	     //pq.add(new Term("content", "法人资格"));
	     //pq.add(new Term("content", "建筑"));
	       
	     pq.add(new Term("content", "电力施工"));
	     pq.add(new Term("content", "总承包"));
	     pq.add(new Term("content", "三级"));
	     //pq.add(new Term("content", "总承包"));
	     
	     //pq.add(new Term("content", "quick"),0);
	     //pq.add(new Term("content", "brown"), 1);
	     //pq.add(new Term("content", "dog"),2);
	     //pq.setSlop(5);
	     
	     TopDocs topDocs = isearcher.search(query, 10);
		 System.out.println("命中：" + topDocs.totalHits);// 输出结果
		 
		 
		 
		 
		 

		 } catch (CorruptIndexException e) {

		 e.printStackTrace();

		 } catch (LockObtainFailedException e) {

		 e.printStackTrace();

		 } catch (IOException e) {

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
}
