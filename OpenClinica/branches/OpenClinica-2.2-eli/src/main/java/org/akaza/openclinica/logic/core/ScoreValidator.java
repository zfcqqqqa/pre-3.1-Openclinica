/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 

package org.akaza.openclinica.logic.core;

/**
 * Provides some validation methods for scoring. 
 * 
 * Here are some definitions:
 * <ul>
 * <li>'term' is one string segment. It can be an expression, a formula, one argument of a formula, 
 * a variable, a numbers.
 * <li>'expression' is a math expression contains arithmetic operators, formulae, variables, numbers.
 * <li>'formula' contains arguments.
 * <li>'argument' may be an expression, a variable, a number
 * </ul>
 * 
 * 
 * @author ywang (Jan. 2008)
 */

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;


public class ScoreValidator {
	private Locale locale;
	private ResourceBundle resexception;
	
	public ScoreValidator(Locale locale) {
		this.locale = locale;
		this.resexception = ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions", locale);
	}
	
	public void setLocale(Locale l) {
		this.locale = l;
	}
	public Locale getLocale() {
		return this.locale;
	}
	
	/**
	 * Return true is an expression has valid syntax. 
	 * For decode function, now, only validate the very first variable.  
	 * 
	 * @param expression
	 * @param errors
	 * @return
	 */
	public boolean isValidExpression(String expression, StringBuffer errors, ArrayList<String> allVariables) {
		if(expression == null || expression.length() < 1){
			//errors.append("Expression is empty" + "; ");
			errors.append(resexception.getString("expression_is_empty") + "; ");
			return false;
		}
		
		// process the prefix
		String exp=expression;
		if (exp.startsWith("func:")) {
		    exp = exp.substring(5).trim();
		}
		//get rid of space in exp
		exp = exp.replace(" ", "");
		exp = exp.replaceAll("##", ",");

		String token = "";
		String finalExpression = "";
		ScoreUtil.Info info = new ScoreUtil.Info();
		info.pos = 0;
		info.level = 0;
		StringBuffer err = new StringBuffer ();
		char contents[] = exp.toCharArray(); 
		int length = exp.length();
		char tempnext = info.pos<(contents.length-1) ? contents[info.pos+1] : ' ';
		if(!isValidSign(contents[0], tempnext)) {
			if(!isValidExpStart(contents[0])) {
				//errors.append("Expression can not start with" + " " + contents[0] + "; ");
				errors.append(resexception.getString("expression_cannot_start_with") + " " + contents[0] + "; ");
			}
		}
		if(!isValidExpEnd(contents[length-1])) {
			//errors.append("Expression can not end with" + " " + contents[length-1] + "; ");
			errors.append(resexception.getString("expression_cannot_end_with") + " " + contents[0] + "; ");
		}
				
		while(info.pos < contents.length){
			char c = contents[info.pos];
			char next = info.pos<(contents.length-1) ? contents[info.pos+1] : ' ';
			//we ignore spaces
			if(c == ' '){
				//do nothing
			}else if(ScoreUtil.isOperator(c)){
				if(!noCommaEnds(token)) {
					//errors.append(token + " should not contain comma" +"; ");
					errors.append(token + " " + resexception.getString("should_not_contain_comma") + "; ");
				}
				token = token.trim();
				if(token.length()>0 && !isNumber(token) && !allVariables.contains(token)) {
					allVariables.add(token);
				}
				finalExpression += token + c;
				token = "";
			}else if(c == '('){
				if(!noCommaEnds(token)) {
					//errors.append(token + " should not contain comma" +"; ");
					errors.append(token + " " + resexception.getString("should_not_contain_comma") + "; ");
				}
				if(isSupportedFunc(token)){
					err.delete(0, err.length());
					if(!isValidFunction(contents,info,token,err,allVariables)) {
						errors.append(err);
					}
					//fake a result to carry on syntax check
					finalExpression += "0";
				}else{
					if(token.length()>1) {
						//errors.append(token + " " + "is not a valid function or has not been supported yet" +"; ");
						errors.append(token + " " + resexception.getString("function_is_invalid_or_not_supported") + "; ");
						//carry on syntax check
						err.delete(0, err.length());
						ArrayList<String> variables = new ArrayList<String>();
						if(!isValidFunction(contents, info, token, err, variables)) {
							errors.append(err);
						}
						//fake a function result to carry on syntax check
						finalExpression += "0";
					} else {
						//just append it then
						info.level++;
						finalExpression += token + c;
					}
				}
				token = "";
			} else if(c == ')'){
				if(!noCommaEnds(token)) {
					//errors.append(token + " should not contain comma" +"; ");
					errors.append(token + " " + resexception.getString("should_not_contain_comma") + "; ");
				}
				token = token.trim();
				if(token.length()>0 && !isNumber(token) && !allVariables.contains(token)) {
					allVariables.add(token);
				}
				finalExpression += token + c;
				token = "";
				info.level--;
			}else if(c == ','){
				//errors.append("One comma is invalid"+"; ");
				errors.append(resexception.getString("one_comma_invalid") + "; ");
				token = "";
				//this should not happen in this method since getFunctionValue()
				//is taking care of of the function
			}else{
				//otherwise, we just append this character to the token
				token += c;
			}
		
			char nextnext = info.pos<(contents.length-2) ? contents[info.pos+2] : ' ';
			boolean isNextSign = isValidSign(next, nextnext);
			if(!isNextSign) { 
				if(!isValidOrder(c, next)) {
					//errors.append("The character"+" " + c + " " + "should not be followed by the character " + next + "; ");
					errors.append(resexception.getString("the_character") + " " + c + " " + resexception.getString("should_not_followed_by_character") + " " + next + "; ");
				}
			}
			info.pos++;
		}
		
		if(!noCommaEnds(token)) {
			//errors.append(token + " should not contain comma" +"; ");
			errors.append(token + " " + resexception.getString("should_not_contain_comma") + "; ");
		}
		
		if(info.level != 0){
			//errors.append("Expression"+" " + exp + " "+"is invalid because parenthesises are not correctly paired"+"; ");
			errors.append(resexception.getString("expression")+" " + exp + " "+ resexception.getString("is_invalid_because_wrong_paired_parenthesises")+"; ");
		}
		finalExpression += token;
		
		if(errors != null && errors.length()>1)	return false;
		
		return true;
	}
	
	public boolean isValidFunction(char[] contents, ScoreUtil.Info info, String func, StringBuffer errors, ArrayList<String> allVariables) {
		int originalLevel = info.level;
		info.pos++;
		info.level++;
		String token = "";
		//the currentExpression is in fact representing the current argument.
		//Here, each argument is treated individually, one at a time.
		String currentExpression = "";
		int argCount = 0;
		StringBuffer err = new StringBuffer();
		ArrayList<String> funcVariables = new ArrayList<String>();
		while(info.pos < contents.length){
			char c = contents[info.pos];
			char next = info.pos<(contents.length-1) ? contents[info.pos+1] : ' '; 
			if(c == ')'){
				if(!noCommaEnds(token)) { 
					//errors.append(token + " should not contain comma" +"; ");
					errors.append(token + " " + resexception.getString("should_not_contain_comma") + "; ");
				}
				info.level--;
				//end of the function, marked by the equal level
				if(info.level == originalLevel){
					currentExpression += token;
					err.delete(0, err.length());
					if(!isValidArgument(currentExpression, err, funcVariables)) {
						//errors.append("One argument of formula '" + func + "' is invalid; ");
						errors.append(err);
					}
					token = "";
					break;
				}else{
					//end of an expression, just store them in the current argument
					currentExpression += token + c;
				}
				token = "";
			} else if(c == '('){
				if(!noCommaEnds(token)) { 
					//errors.append(token + " should not contain comma" +"; ");
					errors.append(token + " " + resexception.getString("should_not_contain_comma") + "; ");
				}
				//again, it is either the start of a function or an expression
				if(token != null && isSupportedFunc(token)){
					err.delete(0, err.length());
					ArrayList<String> variables = new ArrayList<String>();
					if(!isValidFunction(contents, info, token, err, variables)) {
						errors.append(err);
					}
					if(variables.size()>0) {
						if(token.equalsIgnoreCase("decode") && !allVariables.contains(variables.get(0))) 
							allVariables.add(variables.get(0));
						else { 
							for(String s : variables) {
								if(s.length()>0 && !allVariables.contains(s)) {
									allVariables.add(s);
								}
							}
						}
					}
					//fake a function result to store in the current argument and carry on syntax checking
					currentExpression += "0";
				}//if it is the start of an expression
				else {
					if(token.length()>1) {
						//errors.append(token + " is not a valid function or has not been supported yet"+"; ");
						errors.append(token + " " + resexception.getString("function_is_invalid_or_not_supported") + "; ");
						//carry on syntax check
						err.delete(0, err.length());
						ArrayList<String> variables = new ArrayList<String>();
						if(!isValidFunction(contents, info, token, err, variables)) {
							errors.append(err);
						}
						//fake a function result to carry on syntax check
						currentExpression += "0";
						if(variables.size()>0) {
							for(String s : variables) {
								if(s.length()>0 && !allVariables.contains(s)) {
									allVariables.add(s);
								}
							}
						}
					} else {
						info.level++;
						currentExpression += token + c;
					}
				}
				token = "";
			}//end of an argument
			else if(c == ','){
				if(!noCommaEnds(token)) { 
					//errors.append(token + " should not contain comma" +"; ");
					errors.append(token + " " + resexception.getString("should_not_contain_comma") + "; ");
				}
				token = token.trim();
				if(token.length()>0 && !isNumber(token) && !funcVariables.contains(token)) {
					funcVariables.add(token);
				}
				currentExpression += token;
				err.delete(0, err.length());
				if(!isValidArgument(currentExpression, err, funcVariables)) {
					errors.append(err);
				} 
				++argCount;
				token = "";
				currentExpression = "";
			} else if(ScoreUtil.isOperator(c)){
				if(!noCommaEnds(token)) { 
					//errors.append(token + " should not contain comma" +"; ");
					errors.append(token + " " + resexception.getString("should_not_contain_comma") + "; ");
				}
				token = token.trim();
				if(token.length()>0 && !isNumber(token) && !funcVariables.contains(token)) {
					funcVariables.add(token);
				}
				currentExpression += token + c;
				token = "";
			}else{
				if(c != ' '){
					token += c;
				}
			}

			char nextnext = info.pos<(contents.length-2) ? contents[info.pos+2] : ' ';
			boolean isNextSign = isValidSign(next, nextnext);
			if(!isNextSign) { 
				if(!isValidOrder(c, next)) {
					//errors.append("One character"+" " + c + " "+"has been followed by a wrong character"+" " + next + "; ");
					errors.append(resexception.getString("the_character") + " " + c + " " + resexception.getString("should_not_followed_by_character") + " " + next + "; ");
				}
			}
			
			info.pos++;
		}
		++argCount;
		
		if(isTwoArgs(func) && argCount != 2) {
			//errors.append("Function"+" " + func + "() should have 2 arguments only"+"; ");
			errors.append(resexception.getString("function") + " " + func + " " + resexception.getString("should_have_2_arguments_only") + "; ");
		}
		
		if(func.equalsIgnoreCase("decode")) {
			String s = funcVariables.get(0).trim();
			if(s.length()>0 && !allVariables.contains(s)) {
				allVariables.add(funcVariables.get(0));
			}
		}else { 
			for(String s : funcVariables) {
				if(s.length()>0 && !allVariables.contains(s)) {
					allVariables.add(s);
				}
			}
		}
		
		
		if(errors != null && errors.length()>1)	return false;
		
		return true;
	}
	
	public boolean isValidArgument(String term, StringBuffer errors, ArrayList<String> allVariables) {
		if(isNumber(term)) {
			return true;
		} else if(isExpression(term)) {
			return isValidExpression(term, errors, allVariables);
		} else {
			//if(!isNumber(term)) allVariables.add(term);
			term = term.trim();
			if(term.length()>0 && !allVariables.contains(term)) {
				allVariables.add(term);
			}
			return true;
		}
	}
	
	/**
	 * Return true if an expression does not start with arithmetic operators, ')', ',', '.'
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isValidExpStart(char ch) {
		return !((ScoreUtil.isOperator(ch) || ch==')' || ch ==',' || ch == '.'));
	}
	
	/**
	 * Return true if an expression does not end with arithmetic operators, '(', ',', '.'
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isValidExpEnd(char ch) {
		return !((ScoreUtil.isOperator(ch) || ch=='(' || ch==',' || ch=='.'));
	}
	
	/**
	 * Function, variable, argument can not start and/or end with '.'
	 * 
	 * <br>This method only checks '.'
	 * 
	 * @param term
	 * @return
	 */
	public static boolean noCommaEnds(String term) {
		return !((term.startsWith(".") || term.endsWith(".")));
	}
	
	/**
	 * This method can be used when a character can possibly be a sign. It checks next following character.
	 * Return true if the target character is a sign. No space exists between two characters.
	 * 
	 * @param ch
	 * @param next
	 * @return
	 */
	public static boolean isValidSign(char ch, char next) {
		if(ch=='-' || ch=='+') {
			if(!ScoreUtil.isOperator(next) && next != ')' && next != ',') {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return false if current character has been followed by a illegal character,
	 * e.g., it will return false if ',' has been followed by ')'
	 * 
	 * <p>This method only checks + - * / ( ) and , 
	 * <p>+ and - are operators instead of signs
	 * <p>No space between two characters
	 * 	  
	 * @param curr
	 * @param next
	 * @return
	 */
	public static boolean isValidOrder(char curr, char next) {
		if(curr=='(') {
			if(next==')' || ScoreUtil.isOperator(next) || next==',') return false;			
		} else if(curr==')') {
			if(next != ',' && next != ')' && !ScoreUtil.isOperator(next) && next != ' ')	return false;
		} else if(curr==',') {
			if(next==',' || ScoreUtil.isOperator(next) || next==')') return false;
		} else if(ScoreUtil.isOperator(curr)) {
			if(next==')' || next==',' || ScoreUtil.isOperator(next))	return false;
		}
		return true;
	}
	
	/**
	 * Return true if a string matches those formulae which have been supported for scoring calculation:
	 * <br>avg(), max(), min(), median(), pow(), stdev(), sum
	 *   
	 * @param token
	 * @return
	 */
	public static boolean isCalcFuncName(String token) {
		return token.equalsIgnoreCase("sum") || token.equalsIgnoreCase("avg") || token.equalsIgnoreCase("min") ||
				token.equalsIgnoreCase("max") || token.equalsIgnoreCase("median") || token.equalsIgnoreCase("pow") ||
				token.equalsIgnoreCase("stdev");
	}
	
	/**
	 * Return true if a string matches one name of functions that have been supported.
	 * 
	 * @param token
	 * @return
	 */
	public static boolean isSupportedFunc(String token) {
		return token.equalsIgnoreCase("sum") || token.equalsIgnoreCase("avg") || token.equalsIgnoreCase("min") ||
		token.equalsIgnoreCase("max") || token.equalsIgnoreCase("median") || token.equalsIgnoreCase("pow") ||
		token.equalsIgnoreCase("stdev") || token.equalsIgnoreCase("decode");
	}
	
	/**
	 * Return true if a string contains at least one of those characters: 
	 * '+', '-', '*', '/', '(', ')', ','
	 * 
	 * @param term
	 * @return
	 */
	public static boolean isExpression(String term) {
		return (term.contains("+") || term.contains("-") || term.contains("*") || term.contains("/")
				|| term.contains("(") || term.contains(")") || term.contains(",")); 
	}
	
	/**
	 * Return true if a function belongs to below supported function(s) which allow(s) only two arguments:
	 * <li> pow()
	 * 
	 * 
	 * @param functionName
	 * @return
	 */
	public static boolean isTwoArgs(String functionName) {
		return functionName.equalsIgnoreCase("pow");
	}
	
	public static boolean isNumber(String variable) {
		try {
			Double d = Double.parseDouble(variable);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
}