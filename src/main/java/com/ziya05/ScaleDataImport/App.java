package com.ziya05.ScaleDataImport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;

import com.ziya05.ScaleDataImport.Bean.QuestionBean;
import com.ziya05.ScaleDataImport.Bean.RelationBean;
import com.ziya05.ScaleDataImport.Bean.ScaleBean;
import com.ziya05.ScaleDataImport.Bean.FactorBean;
import com.ziya05.ScaleDataImport.Bean.GroupBean;
import com.ziya05.ScaleDataImport.Bean.LevelBean;
import com.ziya05.ScaleDataImport.Bean.FactorMapBean;
import com.ziya05.ScaleDataImport.Bean.GlobalJumpBean;
import com.ziya05.ScaleDataImport.Bean.OptionBean;

/**
 * Hello world!
 *
 */
public class App 
{
	
    public static void main( String[] args ) 
    		throws IllegalArgumentException, IllegalAccessException, 
    		ClassNotFoundException, SQLException, IOException, BadLocationException
    {
    	final String scaleNumber = "1804";
    	//String dirPath = "E:\\projects\\resources\\scale\\量表资料\\量表资料\\";
    	String dirPath = "E:\\projects\\resources\\scale\\spring 2.0\\新增量表\\待添加量表资料20180704\\";
    	File f = new File(dirPath);
        File[] files = f.listFiles(new FilenameFilter(){

			public boolean accept(File file, String name) {
				if (name.contains("~")) {
					return false;
				} 
				
				return name.contains(scaleNumber);
			}
        	
        });
        
        String mainFile = null;
        String descFile = null;
        
        for (File file : files) {
        	String fileName = file.getName();
        	if (fileName.endsWith(".xlsx")) {
        		mainFile = file.getPath();
        	} else if (fileName.endsWith(".rtf")) {
        		descFile = file.getPath();
        	}
        }
        
        if (mainFile == null || descFile == null) {
        	throw new FileNotFoundException("主文件或者描述文件没找到！");
        }
        
        System.out.println(mainFile);
        System.out.println(descFile);

        
    	try
    	{
    		importData(mainFile, descFile);
    		
    	} catch(IllegalArgumentException e) {
    		//delete();    		
    		throw e;
    	} catch(IllegalAccessException e) {
    		//delete();    		
    		throw e;
    		
    	} catch(ClassNotFoundException e) {
    		//delete();    		
    		throw e;
    	} catch(SQLException e) {
    		//delete();    		
    		throw e;
    	} catch(IOException e) {
    		//delete();    		
    		throw e;
    	} 
    	
    }
    
    private static void delete() throws ClassNotFoundException, SQLException {
    	MySqlDao dao = new MySqlDao();
		int scaleId =  dao.getLastestScaleId();
		//dao.deleteScale(scaleId);
    }
    
    private static void importData(String filePath, String descPath) throws IOException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException, SQLException, BadLocationException {
    	
    	MySqlDao dao = new MySqlDao();
    	
        System.out.println( "Hello World!" );
        
        ScaleExcelReader reader = new ScaleExcelReader();
        reader.read(filePath);
        
//        if (filePath.length() < 10000) {
//        	return;
//        }
        
        ScaleBean scaleBean = reader.getScaleBean();
        String scaleDescription = ScaleRTFReader.read(descPath);
        scaleBean.setDescription(scaleDescription);
        
        List<QuestionBean> questionLst = reader.getQuestionLst();
        scaleBean.setQuestionCount(questionLst.size());
        
        int scaleId = dao.insert(scaleBean);

        System.out.println("量表基础信息导入成功， id为：" + scaleId);
        
        
        for(QuestionBean question : questionLst) {
        	question.setScaleId(scaleId);
        	
        	//dao.insert(question);
        	
        	for(OptionBean option : question.getOptionItems()) {
        		option.setScaleId(scaleId);
        		option.setQuestionId(question.getQuestionId());
        		
        		//dao.insert(option);
        	}
        }
        
        dao.batchInsertQuestion(questionLst);
        
        //dao.batchInsert(questionLst);
        
        System.out.println("量表问题和选项导入成功!");
        
        List<FactorBean> factorLst = reader.getFactorLst();
        for(FactorBean factor : factorLst) {
        	if (factor.getFormula().equals("∑")) {
        		StringBuilder sb = new StringBuilder();
        		for(QuestionBean question : questionLst) {
        			sb.append("Q" +question.getQuestionId() + "+");
        		}
        		
        		factor.setFormula(sb.toString().substring(0, sb.length() - 1));
        	}
        	
        	factor.setScaleId(scaleId);
        	dao.insert(factor);
        }
        System.out.println("量表因子导入成功!");
        
        List<GroupBean> groupLst = reader.getGroupLst();
        for(GroupBean group : groupLst) {
        	System.out.println(group.getName() + ":" + group.getGroupId() + ": " + group.getFormula());
        	group.setScaleId(scaleId);
        	dao.insert(group);
        }
        System.out.println("量表团体导入成功!");
        
        Map<Integer, String> factorMap = dao.getMap(factorLst.get(0).getClass(), "where scaleId = " + scaleId);
        Map<String, Integer> factorMapNew = reverse(factorMap);
        
        List<LevelBean> levelLst = reader.getLevelLst();
        for (LevelBean level : levelLst) {
        	level.setScaleId(scaleId);
        	int factorId = factorMapNew.get(level.getFactorName());
        	level.setFactorId(factorId);
        	
        	dao.insert(level);
        }
        System.out.println("量表等级信息导入成功！");
        
        Map<Integer, String> groupMap = dao.getMap(groupLst.get(0).getClass(), "where scaleId = " + scaleId);
        Map<String, Integer> groupMapNew = reverse(groupMap);
        
        List<RelationBean> relationLst = reader.getRelationLst();
        for(RelationBean relation : relationLst) {
        	int factorId = factorMapNew.get(relation.getFactorName());
        	int groupId = groupMapNew.get(relation.getGroupName());
        	
        	relation.setScaleId(scaleId);
        	relation.setFactorId(factorId);
        	relation.setGroupId(groupId);
        	dao.insert(relation);
        }
        System.out.println("量表关系导入成功！");
        
        List<FactorMapBean> mapLst = reader.getMapLst();
        if (mapLst != null) {
	        for(FactorMapBean map : mapLst) {
	        	int factorId = factorMapNew.get(map.getFactorName());
	        	
	        	map.setScaleId(scaleId);
	        	map.setFactorId(factorId);
	        	dao.insert(map);
	        }
	        System.out.println("量表因子转换式导入成功！");
        }
        
        List<GlobalJumpBean> globalJumpLst = reader.getGroupJumpLst();
        if (globalJumpLst != null) {
        	for(GlobalJumpBean globalJump : globalJumpLst) {
        		globalJump.setScaleId(scaleId);
        		dao.insert(globalJump);
        	}
        	
        	System.out.println("题目跳转导入成功！");
        }
        
        System.out.println("量表导入成功！ [" + scaleBean.getScaleName() + "]");
    }
    
    private static Map<String, Integer> reverse(Map<Integer, String> map) {
    	Map<String, Integer> nm = new HashMap<String, Integer>();
    	for(Map.Entry<Integer, String> kv : map.entrySet()) {
    		nm.put(kv.getValue(), kv.getKey());
    	}
    	
    	return nm;
    }
    
}
