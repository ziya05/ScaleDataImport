package com.ziya05.ScaleDataImport;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ziya05.ScaleDataImport.Bean.ScaleBean;
import com.mysql.cj.util.StringUtils;
import com.ziya05.ScaleDataImport.Bean.FactorBean;
import com.ziya05.ScaleDataImport.Bean.GroupBean;
import com.ziya05.ScaleDataImport.Bean.LevelBean;
import com.ziya05.ScaleDataImport.Bean.OptionBean;
import com.ziya05.ScaleDataImport.Bean.QuestionBean;
import com.ziya05.ScaleDataImport.Bean.RelationBean;

public class ScaleExcelReader {
	
	private String filePath;
	private XSSFWorkbook workbook = null;
	
	private ScaleBean scaleBean;
	private List<QuestionBean> questionLst;
	private List<FactorBean> factorLst;
	private List<GroupBean> groupLst;
	private List<LevelBean> levelLst;
	private List<RelationBean> relationLst;
	
	public ScaleExcelReader() {
		
	} 
	
	public void read(String filePath) throws IOException {
		this.filePath = filePath;
		InputStream inputStream = new FileInputStream(filePath);
		workbook = new XSSFWorkbook(inputStream);
		
		readScale();		
		readQuestion();
		readFactor();
		
		for (FactorBean factor : factorLst) {
			System.out.println("因子： " + factor.getName() + " -> " + factor.getFormula());
		}
		 
		readGroup();
		readLevel();
		readRelation();
		
		workbook.close();
		inputStream.close();
	}
	
	private void readScale() {
		String pattern = ".*?(\\d*)-(.*)\\.xlsx";
		Pattern r = Pattern.compile(pattern);
		
		Matcher m = r.matcher(this.filePath);
		if (m.find()) {
			scaleBean = new ScaleBean();
			scaleBean.setScaleNumber(m.group(1));
			scaleBean.setScaleName(m.group(2));
			scaleBean.setDescription("");
		} else {
			throw new IllegalArgumentException("文件名格式不正确！");
		}
		
		System.out.println("scaleNumber 为：" + scaleBean.getScaleNumber());
		System.out.println("scaleName 为：" + scaleBean.getScaleName());
		
		System.out.println("读取量表基本信息结束！");
	}
	
	private void readQuestion() {
		XSSFSheet sheet = workbook.getSheet("题目信息");
		if (sheet == null) {
			throw new IllegalArgumentException("没有题目信息页！");
		}
		
		questionLst = new ArrayList<QuestionBean>();
		
		int rowNumber = sheet.getLastRowNum();
		for (int i = 2; i <= rowNumber; i++) {
			System.out.println("[Question]当前处理的行号：" + i);
			XSSFRow row = sheet.getRow(i);
			
			QuestionBean bean = new QuestionBean();
			
			XSSFCell cell = row.getCell(0);
			if (cell == null) {
				break;
			}
			
			int questionId = new Double(cell.getNumericCellValue()).intValue();
			String title = row.getCell(5).getStringCellValue();
			
			if (StringUtils.isNullOrEmpty(title)) {
				break;
			}
			
			bean.setQuestionId(questionId);
			bean.setTitle(title);
			
			int questionType = new Double(row.getCell(3).getNumericCellValue()).intValue();
			bean.setQuestionType(questionType);
			
			questionLst.add(bean);
			
			List<OptionBean> items = new ArrayList<OptionBean>();
			bean.setOptionItems(items);
			
			int itemSize = new Double(row.getCell(2).getNumericCellValue()).intValue();
			String strScores = row.getCell(6).getStringCellValue().replaceAll("，", ",");
			String strNexts = row.getCell(7).getStringCellValue().replaceAll("，", ",");
			
			String[] scores = strScores.split(",");
			String[] nexts = strNexts.split(",");
			
			if (itemSize != scores.length 
					|| itemSize != nexts.length) {
				System.out.print("选项个数有问题");
				System.out.println("选项个数为：" + itemSize);
				System.out.println("获取到的分数字符串为：" + strScores + "; 分解之后的个数为：" + scores.length);
				System.out.println("获取到的跳转字符串为：" + strScores + "; 分解之后的个数为：" + scores.length);
			}
			
			for(int j = 0; j < itemSize; j++) {
				double score = Double.parseDouble(scores[j]);
				int next = Integer.parseInt(nexts[j]);
				
				char c = (char)((int)'A' + j);
				String optionId = Character.toString(c);
				String content = getStringCell(row, 8 + j);
				
				OptionBean item = new OptionBean();
				item.setOptionId(optionId);
				item.setContent(content);
				item.setScore(score);
				item.setNext(next);
				
				items.add(item);
			}
		}
		
		System.out.println("读取到的题目个数为：" + questionLst.size()) ;
		System.out.println("读取问题信息结束！");
	}

	private void readFactor() {
		XSSFSheet sheet = workbook.getSheet("因子");
		if (sheet == null) {
			throw new IllegalArgumentException("没有因子信息页！");
		}
		 
		factorLst = new ArrayList<FactorBean>();
		
		int rowNumber = sheet.getLastRowNum();
		for (int i = 1; i <= rowNumber; i++) {
			
			System.out.println("[Factor]当前处理的行号：" + i);
			XSSFRow row = sheet.getRow(i);
			
			XSSFCell cell = row.getCell(0);
			if(cell == null) {
				break;
			}
			
			int factorId = new Double(cell.getNumericCellValue()).intValue();
			if (factorId == 0) {
				break;
			}
			
			String factorName = row.getCell(1).getStringCellValue();
			String formula = convertFactorFormula(row.getCell(2).getStringCellValue().trim());
			
			if (StringUtils.isNullOrEmpty(factorName)) {
				break;
			}
			
			FactorBean bean = new FactorBean();
			bean.setFactorId(factorId);
			bean.setName(factorName);
			bean.setFormula(formula);
			
			factorLst.add(bean);
		}
	}
	
	private void readGroup() {
		XSSFSheet sheet = workbook.getSheet("团体");
		if (sheet == null) {
			throw new IllegalArgumentException("没有团体信息页！");
		}
		
		groupLst = new ArrayList<GroupBean>();
		
		int rowNumber = sheet.getLastRowNum();
		int lastGroupId = 0;
		String groupName = null;
		
		StringBuilder sb = null;
		
		System.out.println("总行数：" + rowNumber);
		for (int i = 1; i <= rowNumber; i++) {
			
			System.out.println("[Group]当前处理的行号：" + i);
			XSSFRow row = sheet.getRow(i);
			
			XSSFCell cell = row.getCell(0);
			if (cell == null) {
				break;
			}
			
			int groupId = new Double(cell.getNumericCellValue()).intValue();		
			
			if (groupId == 0) {	
				break;
			}
			
			if (lastGroupId != groupId) {
			
				if (lastGroupId != 0) {
					sb.append(")");
					GroupBean bean = new GroupBean();
					bean.setGroupId(lastGroupId);
					bean.setName(groupName);
					bean.setFormula(sb.toString());
					groupLst.add(bean);
				}

				sb = new StringBuilder("(");
				lastGroupId = groupId;
				groupName = getStringCell(row, 1);
			}
			
			String inSign = convertOperator(getStringCell(row, 2));
			String outSign = convertOperator(getStringCell(row, 3));	
			
			String kv = getStringCell(row, 4)
					+ convertOperator(getStringCell(row, 5))
					+ convertData(getSqlValue(row, 6));
			
			if (!StringUtils.isNullOrEmpty(inSign)) {
				sb.append(" ");
				sb.append(inSign);
				sb.append(" ");
			}
			
			if (!StringUtils.isNullOrEmpty(outSign)) {
				sb.append(") ");
				sb.append(outSign);
				sb.append(" (");
			}
			
			sb.append(kv);
		}
		
		sb.append(")");
		
		GroupBean bean = new GroupBean();
		bean.setGroupId(lastGroupId);
		bean.setName(groupName);
		bean.setFormula(sb.toString());
		groupLst.add(bean);
		
		System.out.println("团体数量： " + groupLst.size());
	}
	
	private void readLevel() {
		XSSFSheet sheet = workbook.getSheet("结果解释、建议");
		if (sheet == null) {
			throw new IllegalArgumentException("没有结果解释、建议信息页！");
		}
		
		String lastFactorName = "";
		String lastGroupName = "";
		
		levelLst = new ArrayList<LevelBean>();
		int rowNumber = sheet.getLastRowNum();
		for(int i = 2; i <= rowNumber; i++) {
			System.out.println("[Level]当前处理的行号：" + i);
			XSSFRow row = sheet.getRow(i);
			
			String factorName = getStringCell(row, 0);
			if (StringUtils.isNullOrEmpty(factorName)) {
				break;
			}
			
			String groupName = getStringCell(row, 1);
			if (lastFactorName.equals(factorName)
					&& !lastGroupName.equals(groupName)) {
				continue;
			}
			
			lastFactorName = factorName;
			lastGroupName = groupName;
			
			int levelId = new Double(row.getCell(2).getNumericCellValue()).intValue();
			String description = getStringCell(row, 3);
			String advice = getStringCell(row, 4);
			
			LevelBean level = new LevelBean();
			level.setFactorName(factorName);
			level.setLevelId(levelId);
			level.setDescription(description);
			level.setAdvice(advice);
			levelLst.add(level);
		}
	}
	
	private void readRelation() {
		XSSFSheet sheet = workbook.getSheet("常模");
		if (sheet == null) {
			throw new IllegalArgumentException("没有常模页！");
		}
		
		relationLst = new ArrayList<RelationBean>();
		int rowNumber = sheet.getLastRowNum();
		for(int i = 2; i <= rowNumber; i++) {
			System.out.println("[Relation]当前处理的行号：" + i);
			XSSFRow row = sheet.getRow(i);
			
			String factorName = getStringCell(row, 0);
			if (StringUtils.isNullOrEmpty(factorName)) {
				break;
			}
			
			String groupName = getStringCell(row, 1);
			String points = getStringCell(row, 2);
			
			RelationBean relation = new RelationBean();
			relation.setFactorName(factorName);
			relation.setGroupName(groupName);
			relation.setPoints(points);
			relationLst.add(relation);
		}
	}
	
	private String convertOperator(String operator) {
		operator = operator.trim().toUpperCase();
		
		if (operator.equals("AND")) {
			return "&&";
		} else if(operator.equals("OR")) {
			return "||";
		} else if(operator.equals("=")) {
			return "==";
		}
		
		return operator;
	}
	
	private String convertData(String data) {
		if (data.equals("'男性'")) {
			return "'男'";
		} else if (data.equals("'女性'")) {
			return "'女'";
		}
		
		return data;
	}
	
	private String convertFactorFormula(String formula) {
		if (formula.equals("∑")) {
    		StringBuilder sb = new StringBuilder();
    		for(QuestionBean question : questionLst) {
    			sb.append("Q" +question.getQuestionId() + "+");
    		}
    		
    		return sb.toString().substring(0, sb.length() - 1);
    	} 
		
		formula = formula.replaceAll("FLOOR", "Math.floor")
				.replaceAll("IsMax", "isMax")   //与其他javascript function风格保持一致
				.replaceAll(" Max", " Math.max")  //避免IsMax
				.replaceAll("LEVEL\\((.*?)\\)", "LEVEL_$1") //替换LEVEL(F1)为LEVEL_F1, 避免与F1冲突
	    		.replaceAll("AND", "&&")
	    		.replaceAll("OR", "||");

		// 匹配 If Q1!=1 Then 1;\nIf Q2!=2 Then 1;...
		String reg = "((If .*? Then .*?;)\\n?)*";
		Pattern p = Pattern.compile(reg);
	    Matcher m =	p.matcher(formula);   
	    
	    if (m.matches()) {
	    	String reg2 = "If (.*?) Then (.*?);";
	    	Pattern p2 = Pattern.compile(reg2);
	    	StringBuilder sbFormula = new StringBuilder();
	    	
	    	String reg3 = "基本信息\\.([^0-9]*?)([=><!]+)(([^0-9\\.\\s])*?)([\\s\\n\\)])";
	    	
	    	Matcher m2 = p2.matcher(formula);
	    	while(m2.find()) {
	    		String condition = m2.group(1) + " ";
	    		String trueVal = m2.group(2);
	    		
	    		if (sbFormula.length() != 0) {
    				sbFormula.append("+");
    			}
	    		
	    		condition = condition.replaceAll(reg3, "$1$2'$3'$5")
	    				.replaceAll("基本信息.", "");  //基本信息.年龄==15 替换值为数字的 
	    		// 基本信息.性别==男 AND 基本信息.职业==学生 AND 基本信息.学历==初中 AND 基本信息.年龄 >=15 
	    		// 性别=='男' AND 职业=='学生' AND 学历=='初中' AND 基本信息.年龄 >=15 
	    		// 主要为了加单词两边的引号， 等号(或大于，小于号，不等于号)后不能有空格
	    		
	    		sbFormula.append("ifElse(" + condition + ", " + trueVal +", 0)");
	    	}
	    	
	    	return sbFormula.toString();
	    }
	    
	    formula = formula.replaceAll("基本信息.", "");
	    	    
	    return formula;
	}
	
	private String getSqlValue(XSSFRow row, int colIndex) {
		XSSFCell cell = row.getCell(colIndex);
		CellType cellType = cell.getCellTypeEnum();
		
		if (cell != null) {
			if (cellType == CellType.STRING) {
				return "'" + cell.getStringCellValue() + "'";
			}
			
			return getStringCell(row, colIndex);
		}
		
		return "''";
	}
	
	private String getStringCell(XSSFRow row, int colIndex) {
		XSSFCell cell = row.getCell(colIndex);
		if (cell != null) {
			CellType cellType = cell.getCellTypeEnum();
			if (cellType == CellType.STRING) {
				return cell.getStringCellValue();
			} else if (cellType == CellType.NUMERIC) {
				double d = cell.getNumericCellValue();
				return new Double(d).toString();
			} else if (cellType == CellType.BLANK) {
				return "";
			}
		}
		
		return "";
	}
	
	public ScaleBean getScaleBean() {
		return scaleBean;
	}

	public List<QuestionBean> getQuestionLst() {
		return questionLst;
	}

	
	public List<FactorBean> getFactorLst() {
		return factorLst;
	}
	
	public List<GroupBean> getGroupLst() {
		return groupLst;
	}

	public List<LevelBean> getLevelLst() {
		return levelLst;
	}

	public List<RelationBean> getRelationLst() {
		return relationLst;
	}
	
	
}
