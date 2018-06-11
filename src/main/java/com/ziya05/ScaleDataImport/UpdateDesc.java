package com.ziya05.ScaleDataImport;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.text.BadLocationException;

public class UpdateDesc {
	public static void main( String[] args ) 
    		throws IllegalArgumentException, IllegalAccessException, 
    		ClassNotFoundException, SQLException, IOException, BadLocationException
    {
		String dirPath = "E:\\projects\\resources\\scale\\量表资料\\量表资料\\1010-贝克焦虑量表-指导语.rtf";
		
		String scaleDescription = ScaleRTFReader.read(dirPath);
		
		
		
		System.out.println(scaleDescription);
    }
	
}
