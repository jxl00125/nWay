package com.test;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
class Rule{
	static int id=0;
	int ruleid;
	int flagid;
	int elementid;
	String rule;
	String type;
	
	public Rule(int a, int b, String c, String d) {
		// TODO Auto-generated constructor stub
		this.ruleid=id++;
		this.flagid=a;
		this.elementid=b;
		this.rule=c;
		this.type=d;
	}
}
public class TestDemo {
	public static void p(Object o){
        System.out.println(o);
    }
	//							电话						机构							名字					
	static String elements[]={"[0-9](?:[0-9#\\-*]*)","[\u4e00-\u9fa5]*?公司","[\u4e00-\u9fa5\\.a-zA-Z]{1,15}"};
	static Rule[] rules= new Rule[]{
		new Rule(0, 1, "(?:招标|)代理机构(?:：|:|\\s)+(ELEMENT(?:[、,，\\s]ELEMENT)*)\\s","1"),
		new Rule(1, 2, "联系人(?:：|:|\\s)+(ELEMENT(?:[、,，\\s]ELEMENT)*)\\s","1")	,
		new Rule(2, 0, "(?:联系|)电话(?:：|:|\\s)+(ELEMENT(?:[、,，\\s]ELEMENT)*)\\D","1")
	};
	public static void main(String args[]){

		//需要对文字进行预处理，去掉因为去除格式产生的?等符号
		String resourceString = "" +
				"北京市丰台区市政市容管理委员会丰台区道路交通设施完善工程招标公告中化国际招标有限责任公司（招标" +
				"代理机构）受北京市丰台区市政市容管理委员会（招标人）的委托，就如下项目进行国内公开招标，邀请合格的投标人提" +
				"交密封的投标文件。 1.?项目名称：丰台区道路交通设施完善工程 2.?项目编号：0747-1561SITCN319 3.?招标人名称：北京市丰台" +
				"区市政市容管理委员会 4.?招标人地址：北京市丰台区文体路2号 5.?招标人联系方式：010-83669826 6.?本项目资金来源：北京市财" +
				"政资金 7.?本项目预算：人民币4,952,896.71元。 8.?本次招标内容为：丰台区道路交通设施完善工程。其中： 第一标段：招标控制价为" +
				"1,833,325.58元；包括大灰厂东路东段等。 第二标段：招标控制价为1,941,794.82元；包括文体路等。 第三标段：招标控制价为1,176" +
				",262.86元；包括方庄地区等。 详见招标文件。 9.?合格投标人的资格要求： （1）?具有独立承担民事责任的能力； （2）?具有良好的商" +
				"业信誉和健全的财务会计制度； （3）?具有履行合同所必须的设备和专业技术能力； （4）?有依法缴纳税收和社会保障资金的良好记录；" +
				" （5）?参加此采购活动前三年内，在经营活动中没有重大违法记录； （6）?投标人在人员、设备、资金等方面具有相应的" +
				"施工能力，并须具备下列资质之一： 具有建设部门核发的市政公用工程施工总承包三级（含）以上企业资质，且项目负责人具备市" +
				"政公用工程专业贰级（含以上）专业注册建造师执业资格。或， 具有国家行政主管部门核发的公路工程(交通工程专业)—交通安全设施" +
				"施工企业资质，且项目负责人具备公路工程专业贰级（含以上）专业注册建造师执业资格。 （7）?项目负责人须具备安全生产考核合" +
				"格证书（B本），且不得担任其他在施建设工程项目的项目经理； （8）?投标人具有有效的安全生产许可证； （9）?本次招标不接受联合" +
				"体投标； （10）?投标人必须从中化国际招标有限责任公司购买招标文件并登记备案； （11）?符合法律、法规规定的其他条件。 10.?投标" +
				"报名 凡有意参加投标者，请于2015年9月16日至2015年9月23?日，每天上午8:30至11:30时，下午13:30至16:30时（北京时间，下同）" +
				"，在北京市复兴门外大街A2号中化大厦20层（详细地址）报名。报名时需携带相关证明资料： （1）企业营业执照副本原件及复印件加盖公" +
				"章； （2）企业资质证书副本原件及复印件加盖公章； （3）企业安全生产许可证副本原件及复印件加盖公章； （4）拟派项目经理建造师注" +
				"册证书及安全B本原件及复印件加盖公章； （5）法定代表人授权委托书及被委托人的身份证原件及复印件加盖公章。 11.?招标文件购买： " +
				"（1）?发售时间：2015年9月16日至9月23日，每天上午8:30至11:30时，下午13:30至16:30时（北京时间），节假日除外。 （2）?文" +
				"件售价：人民币贰佰元整（200元）。如需邮寄加收100元邮寄费，招标文件售后不退。 （3）?报名同时可购买招标文件。 12.?投标截止时" +
				"间：所有投标文件应于2015年10月9日下午13:30前递交至北京复兴门外大街A2号中化大厦19层第六会议室。迟到的投标文件将被视为无效" +
				"投标文件拒绝接收。 13.?开标时间及地点：兹定于2015年10月9日下午13:30在北京市复兴门外大街A2号中化大厦19层第六会议室公开开标" +
				"，届时请投标人派授权代表出席开标仪式。 14.?评标办法和标准：综合评分法，投标报价60分，施工组织设计40分。 招标代理机构：中化" +
				"国际招标有限责任公司 地址：北京复兴门外大街A2号中化大厦（邮编：100045） 业务联系人：李程、蔡庆飞 电话：010-59368927 传真" +
				"：010-59369782 电子邮件：licheng02@sinochem.com 购买招标文件账号： 户名：中化国际招标有限责任公司 开户银行：中国工商银" +
				"行北京长安支行 账号（人民币）：0200003319250001750 中化国际招标有限责任公司 ???2015年9月16日" + "234@sina.com  ";
	
		ArrayList<String> company= extrationAll(resourceString, "1");
//		for(String item:company){
//			p(item);
//		}

	}
	
	public static ArrayList<String> extrationAll(String resource , String type){
		ArrayList<String> result = new ArrayList<String>();
		int i;
		for(i=0;i<rules.length;i++){
			if(rules[i].type.equals(type))
				result.addAll(extration(resource, rules[i]));
		}
		return result;
	}
	
	public static ArrayList<String> extration(String resource , Rule ruleObj){
		ArrayList<String> result = new ArrayList<String>();

		int flagID=ruleObj.flagid;
		int elementID=ruleObj.elementid;
		String rule =ruleObj.rule.replace("ELEMENT", elements[elementID]);
		System.out.println(rule);
		Pattern p=Pattern.compile(rule);
		Matcher m=p.matcher(resource);
		while(m.find()){
			int count=m.groupCount();
			for(int i=1;i<=count;i++){
				String str=m.group(i);
				p(str);
				String bString=new String(elements[elementID]);
				
				String eleRule="("+elements[elementID]+")";
				//p(eleRule);
				Pattern p2=Pattern.compile(eleRule);
				Matcher m2=p2.matcher(str);
				while(m2.find()){
					p(m2.group());
					result.add(m2.group());

				}
			}
		
		}

		return result;
	}
}

