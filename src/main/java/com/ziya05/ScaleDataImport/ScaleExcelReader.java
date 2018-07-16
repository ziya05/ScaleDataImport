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
import com.ziya05.ScaleDataImport.Bean.FactorMapBean;
import com.ziya05.ScaleDataImport.Bean.GlobalJumpBean;
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
	private List<FactorMapBean> mapLst;
	private List<GlobalJumpBean> groupJumpLst;
	
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
		readMap();
		readGlobalJump();
		
		workbook.close();
		inputStream.close();
	}
	
	private void readScale() {
		String pattern = ".*?\\\\([0-9a-zA-Z]{4})-(.*?)\\.xlsx";
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
			
			int levelCount = getIntCell(row, 3);
			
			Boolean inChart = getIntCell(row, 4) == 1 ? true : false; 
			
			String strInResult = getStringCell(row, 9);
			Boolean inResult = true;
			if (!StringUtils.isNullOrEmpty(strInResult)
					&& Double.parseDouble(strInResult) == 0) {
				inResult = false;
			}
			
			FactorBean bean = new FactorBean();
			bean.setFactorId(factorId);
			bean.setName(factorName);
			bean.setFormula(formula);
			bean.setLevelCount(levelCount);
			bean.setInChart(inChart);
			bean.setInResult(inResult);
			
			System.out.println(factorName + ":" + inChart + ":" + inResult);
			
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
			
			if (row == null) {
				break;
			}
			
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
	
	private void readMap() {
		XSSFSheet sheet = workbook.getSheet("因子转换式");
		if (sheet == null) {
			return;
		}
		
		mapLst = new ArrayList<FactorMapBean>();
		
		int rowNumber = sheet.getLastRowNum();
		for(int i = 1; i <= rowNumber; i++) {
			System.out.println("[Map]当前处理的行号：" + i);
			XSSFRow row = sheet.getRow(i);
			
			if (row == null) {
				break;
			}
			
			String factorName = getStringCell(row, 0);
			if (StringUtils.isNullOrEmpty(factorName)) {
				break;
			}
			
			String name = getStringCell(row, 1);
			String formula = getStringCell(row, 2);
			
			FactorMapBean map = new FactorMapBean();
			map.setFactorName(factorName);
			map.setName(name);
			map.setFormula(this.convertMapFormula(formula));
			
			mapLst.add(map);
		}
	}
	
	private void readGlobalJump() {
		XSSFSheet sheet = workbook.getSheet("题目跳转");
		if (sheet == null) {
			return;
		}
		
		groupJumpLst = new ArrayList<GlobalJumpBean>();
		
		int rowNumber = sheet.getLastRowNum();
		for(int i = 1; i <= rowNumber; i++) {
			System.out.println("[GlobalJump]当前处理的行号：" + i);
			XSSFRow row = sheet.getRow(i);
			
			if (row == null) {
				break;
			}
			
			String name = getStringCell(row, 0);
			if (StringUtils.isNullOrEmpty(name)) {
				break;
			}
			
			int begin = getIntCell(row, 2);
			int end = getIntCell(row, 3);
			int continuous = getIntCell(row, 4);
			int questionCount = getIntCell(row, 5);
			double score = getDoubleCell(row, 6);
			int jumpNo = getIntCell(row, 7);
			
			GlobalJumpBean globalJump = new GlobalJumpBean();
			globalJump.setName(name);
			globalJump.setBegin(begin);
			globalJump.setEnd(end);
			globalJump.setContinuous(continuous);
			globalJump.setQuestionCount(questionCount);
			globalJump.setScore(score);
			globalJump.setJumpNo(jumpNo);
			
			groupJumpLst.add(globalJump);
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
				.replaceAll("CEIL", "Math.ceil")
				.replaceAll("IsMax", "isMax")   //与其他javascript function风格保持一致
				.replaceAll(" Max", " Math.max")  //避免IsMax
				.replaceAll("DoGet", "doGet")
				.replaceAll("LEVEL\\((.*?)\\)", "LEVEL_$1") //替换LEVEL(F1)为LEVEL_F1, 避免与F1冲突
	    		.replaceAll("AND", "&&")
	    		.replaceAll("OR", "||");
		
		formula = convertMap(formula);
		
		formula = convertDoGet(formula);
		
		formula = convertEmbeddedIfThen(formula);

		formula = convertStandardIfThen(formula);
	    
	    formula = formula.replaceAll("基本信息.", "");
	    	    
	    return formula;
	}
		
	private String convertMap(String formula) {
		String reg = "Map\\((.*?),\"(.*?)\"\\)";

		return formula.replaceAll(reg, "Map_$1_$2($1)")
				.replaceAll("Map_F", "Map_FM"); // 避免替换因子分时出错
	}
	
	private String convertDoGet(String formula) {
		String reg = "doGet\\((P\\d+),(\\d+)\\)";
		Pattern p = Pattern.compile(reg);
		
		StringBuffer sbFormula = new StringBuffer();
		
		Matcher m = p.matcher(formula);
		while(m.find()) {
			m.appendReplacement(sbFormula, convertDoGet(m));
		}
		
		m.appendTail(sbFormula);
		return sbFormula.toString();
	}
	
	private String convertDoGet(Matcher m) {
		String item = m.group(1);
		String option = m.group(2);
		
		int oId = Integer.parseInt(option);
		char ch = (char)((int)'A' + oId - 1);
		return String.format("doGet(%s, '%s')", item, ch);
	}
	
	private String convertEmbeddedIfThen(String formula) {
		
		String reg = "\\(If (.*?) Then (.*?);\\)";
		Pattern p = Pattern.compile(reg);
		
		StringBuffer sbFormula = new StringBuffer();

    	Matcher m = p.matcher(formula);
    	while(m.find()) {
    		m.appendReplacement(sbFormula, convertIfThen(m, 1, 2));
    	}
		
    	m.appendTail(sbFormula);
    	return sbFormula.toString();
	}
	
	private String convertStandardIfThen(String formula) {

		String reg = "((If .*? Then .*?;)\\n?)*";
		Pattern p = Pattern.compile(reg);
	    Matcher m =	p.matcher(formula);   
	    
	    if (m.matches()) {
	    	String reg2 = "If (.*?) Then (.*?);";
	    	Pattern p2 = Pattern.compile(reg2);
	    	StringBuilder sbFormula = new StringBuilder();

	    	Matcher m2 = p2.matcher(formula);
	    	while(m2.find()) {
	    		if (sbFormula.length() != 0) {
    				sbFormula.append("+");
    			}
	    		
	    		String data = convertIfThen(m2, 1, 2);
	    		sbFormula.append(data);
	    	}
	    	
	    	return sbFormula.toString();
	    } else {
	    	return formula;
	    }

	}
	
	private String convertIfThen(Matcher matcher, int cIndex, int tIndex) {
		String condition = matcher.group(cIndex) + " ";
		String trueVal = matcher.group(tIndex);
		
		String reg = "基本信息\\.([^0-9]*?)([=><!]+)(([^0-9\\.\\s])*?)([\\s\\n\\)])";
		
		condition = condition.replaceAll(reg, "$1$2'$3'$5")
				.replaceAll("基本信息.", "");  
		
		//基本信息.年龄==15 替换值为数字的 
		// 基本信息.性别==男 AND 基本信息.职业==学生 AND 基本信息.学历==初中 AND 基本信息.年龄 >=15 
		// 性别=='男' AND 职业=='学生' AND 学历=='初中' AND 基本信息.年龄 >=15 
		// 主要为了加单词两边的引号， 等号(或大于，小于号，不等于号)后不能有空格
		
		return "ifElse(" + condition + ", " + trueVal +", 0)";
	}
	
	private String convertMapFormula(String formula) {
		
		StringBuffer result = new StringBuffer();
		Pattern pattern = Pattern.compile("(.*?)\\,(.*?);");
		
		Matcher matcher = pattern.matcher(formula);
		
		while(matcher.find()) {
			String condition = matcher.group(1);
			String r = matcher.group(2);
			
			result.append("if(");
			result.append(condition);
			result.append(")");
			
			result.append(" return ");
			result.append(r);
			result.append(";");

		}
		
		return result.toString();		
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
	
	private int getIntCell(XSSFRow row, int colIndex) {
		XSSFCell cell = row.getCell(colIndex);
		
		if (cell != null) {
			CellType cellType = cell.getCellTypeEnum();
			if (cellType == CellType.STRING) {
				return Integer.parseInt(cell.getStringCellValue());
			}
			
			return (int) cell.getNumericCellValue();
		}
		
		return 0;
	}
	
	private double getDoubleCell(XSSFRow row, int colIndex) {
		XSSFCell cell = row.getCell(colIndex);
		
		if (cell != null) {
			CellType cellType = cell.getCellTypeEnum();
			if (cellType == CellType.STRING) {
				return Double.parseDouble(cell.getStringCellValue());
			}
			
			return (double) cell.getNumericCellValue();
		}
		
		return 0;
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

	
	public List<FactorMapBean> getMapLst() {
		return mapLst;
	}
	
	
	
	public List<GlobalJumpBean> getGroupJumpLst() {
		return groupJumpLst;
	}

	public static void main(String[] args) throws IOException {
		ScaleExcelReader reader = new ScaleExcelReader();
		
		String path = "E:\\projects\\resources\\scale\\spring 2.0\\新增量表\\待添加量表资料20180704\\1202-艾森克人格问卷成人式.xlsx";
		reader.read(path);
		
//		String formula1 = "Q2+Q98+Q120+Q213+Q15+Q24+(If Q28==0 Then 1;)+(If Q47==0 Then 1;)+(If Q60==0 Then 1;)+(If Q73==0 Then 1;)+Q95+Q133+Q178+Q180+Q201+Q217+(If Q230==0 Then 1;)";
//		String formula2 = "If F46>99 Then 120; If F46<1 Then 20; If F46>=1 AND F46<=99 AND 基本信息.性别==男 Then Map(F46,\"Sc男\");If F46>=1 AND F46<=99 AND 基本信息.性别==女 Then Map(F46,\"Sc女\");";
//		String formula3 = "(If Q28==0 Then 1;)+DoGet(P5,1)+(If Q29==0 Then 1;)+DoGet(P32,2)+DoGet(P41,1)+DoGet(P43,5)+DoGet(P52,10)+DoGet(P67,1)+DoGet(P86,1)+DoGet(P104,1)+DoGet(P130,1)+DoGet(P138,1)+DoGet(P142,1)+DoGet(P158,1)+DoGet(P159,1)+DoGet(P182,1)+DoGet(P189,1)+DoGet(P193,1)+DoGet(P236,1)+DoGet(P259,1)+DoGet(P288,1)+DoGet(P290,1)+DoGet(P2,2)+DoGet(P8,2)+DoGet(P9,2)+DoGet(P18,2)+DoGet(P30,2)+DoGet(P36,2)+DoGet(P39,2)+DoGet(P46,2)+DoGet(P51,2)+DoGet(P57,2)+DoGet(P58,2)+DoGet(P64,2)+DoGet(P80,2)+DoGet(P88,2)+DoGet(P89,2)+DoGet(P95,2)+DoGet(P98,2)+DoGet(P107,2)+DoGet(P122,2)+DoGet(P131,2)+DoGet(P145,2)+DoGet(P152,2)+DoGet(P153,2)+DoGet(P154,2)+DoGet(P155,2)+DoGet(P160,2)+DoGet(P178,2)+DoGet(P191,2)+DoGet(P207,2)+DoGet(P208,2)+DoGet(P233,2)+DoGet(P241,2)+DoGet(P242,2)+DoGet(P248,2)+DoGet(P263,2)+DoGet(P270,2)+DoGet(P271,2)+DoGet(P272,2)+DoGet(P285,2)+DoGet(P296,2)";
//		
//		
//		String result = reader.convertFactorFormula(formula3);
//		System.out.println(result);
	}
}
