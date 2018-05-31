package util.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		FtpPropertyLoader loader = new FtpPropertyLoader();
		Properties properties = loader.getProperties();
		String path = "C:\\db_extract.xml";
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("�������ļ�" + file.getName());
			return;
		}
		InputStream input = new FileInputStream(new File(path));
		int i = FtpUtil.uploadMutilFile1(properties,properties.getProperty("FTP_BASEPATH"), "hozon", file.getName(), input);
		if (i==1) {
			System.out.println("�ϴ��ɹ�");
		} else {
			System.out.println("�ϴ�ʧ��");
		}
	}
}
