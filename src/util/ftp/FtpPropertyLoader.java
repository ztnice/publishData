package util.ftp;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FtpPropertyLoader {
	/** 属性文件放在当前包下面即可，如果FTP服务器的位置改变了，需要改属性文件 */	private final static String filePath = "resource.properties";
	private static String path2 = "C:\\Users\\Administrator\\Desktop\\commons-net-3.6-bin\\resource.properties";
	private Properties Properties;

	public FtpPropertyLoader() {
		try {
			defaultLoader();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FtpPropertyLoader(String filePath) {
		try {
			loader(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 默认属性文件 */
	private boolean defaultLoader() throws IOException {
		Properties = new Properties();
		InputStream in = null;
		try {
			in = this.getClass().getResourceAsStream(filePath);
			Properties.load(in);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return false;
	}

	/** 自定义属性文件 */
	private boolean loader(String filePath) throws IOException {
		//  属性文件
		Properties = new Properties();
		// 输入流
		InputStream in = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				System.out.println("没有找打属性文件" + filePath);
				JOptionPane.showMessageDialog(null, "未找到属性文件", "错误", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			in = new FileInputStream(file);
			Properties.load(in);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return false;
	}

	public Properties getProperties() {
		return Properties;
	}

	public static void main(String[] args) {
		new FtpPropertyLoader(path2);
	}
}
