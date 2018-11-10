package com.util;

import java.util.ArrayList;
import java.util.List;

import org.wltea.expression.ExpressionEvaluator;
import org.wltea.expression.datameta.Variable;

public class ExpressTest {
	public static void main(String[] args) {
		
		
		
		String expression ="(10+2)/2>3&&5<3.13123";
		
		String expression1 ="(100 > 10000)?\"总经理审批\":\"部门经理审批\"  ";
		
		List<Variable> variables = new ArrayList<Variable>();
		
		variables.add(Variable.createVariable("申请金额 ", "109"));
		variables.add(Variable.createVariable("a", "10"));
		variables.add(Variable.createVariable("b", "20"));
		variables.add(Variable.createVariable("d", "3"));
		Object result1 = ExpressionEvaluator.evaluate(expression);
		//System.out.println(ExpressionEvaluator.compile("(a+b)/c"));
		Object result = ExpressionEvaluator.evaluate(expression1, variables);
		
		System.out.println(result1);
		System.out.println(result);
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		String s_expression = "((a+b)>c)&&d>c";
		
		String s_e [] = s_expression.split("&&");
		
		System.out.println(s_e[0]);
		
		//第一步检查特殊字符
		
		
		
	}

}
