package com.ziya05.ScaleDataImport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

public class ScaleRTFReader {
	
	public ScaleRTFReader() {
		
	}
	
	public static String read(String filePath) throws IOException, BadLocationException {
		String result = null;
        File file = new File(filePath);

        DefaultStyledDocument styledDoc = new DefaultStyledDocument();
        // 创建文件输入流
        InputStream streamReader = new FileInputStream(file);
        new RTFEditorKit().read(streamReader, styledDoc, 0);
        //以 ISO-8859-1的编码形式获取字节byte[], 并以 GBK 的编码形式生成字符串
        result = new String(styledDoc.getText(0, styledDoc.getLength()).getBytes("ISO8859-1"),"GBK");

        return result;
	}
}
