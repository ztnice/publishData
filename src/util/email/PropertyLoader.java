package util.email;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {
	/** �����ļ����ڵ�ǰ�����漴�ɣ����FTP��������λ�øı��ˣ���Ҫ�������ļ� */
	private final static String filePath = "resource.properties";
	private static String path2 = "C:\\Users\\Administrator\\Desktop\\commons-net-3.6-bin\\resource.properties";
	private Properties Properties;

	public PropertyLoader() {
		try {
			defaultLoader();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PropertyLoader(String filePath) {
		try {
			loader(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Ĭ�������ļ� */
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

	/** �Զ��������ļ� */
	private boolean loader(String filePath) throws IOException {
		// �����ļ�
		Properties = new Properties();
		// ������
		InputStream in = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				System.out.println("û���Ҵ������ļ�" + filePath);
				JOptionPane.showMessageDialog(null, "δ�ҵ������ļ�", "����", JOptionPane.ERROR_MESSAGE);
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
		new PropertyLoader(path2);
	}
}
