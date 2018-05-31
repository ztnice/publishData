package util.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Apache commons-net ����һ�ѣ�����FTP�ͻ��˹������ĺ��ò�
 * 
 */
public class FtpTest {
	public static void main(String[] args) {
		testUpload();
		// testDownload();
	}

	/**
	 * FTP�ϴ������ļ����� 1.���ӷ��� 2.ȷ����Ҫ�ϴ����ļ� 3.ָ�����ڷ������˴�ŵ�λ��
	 */
	public static void testUpload() {
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			ftpClient.connect("10.0.8.231");
			ftpClient.login("admin", "admin");
			File srcFile = new File(
					"C:\\Users\\Administrator\\Desktop\\commons-net-3.6-bin\\commons-net-3.6\\RELEASE-NOTES.txt");
			fis = new FileInputStream(srcFile);
			// �����ϴ�Ŀ¼
			File path = new File("/hozon/text");
			System.out.println(path.getPath());
			ftpClient.changeWorkingDirectory(path.getPath());
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("GBK");
			// �����ļ����ͣ������ƣ�
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.storeFile("RELEASE-NOTES-sss.txt", fis);
			System.out.println("�ϴ����");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP�ͻ��˳���", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("�ر�FTP���ӷ����쳣��", e);
			}
		}
	}

	/**
	 * FTP���ص����ļ�����
	 */
	public static void testDownload() {
		FTPClient ftpClient = new FTPClient();
		FileOutputStream fos = null;

		try {
			ftpClient.connect("10.0.8.231");
			ftpClient.login("admin", "admin");

			String remoteFileName = "/lanjie/pic/girl.jpg";
			fos = new FileOutputStream("c:/down.jpg");

			ftpClient.setBufferSize(1024);
			// �����ļ����ͣ������ƣ�
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(remoteFileName, fos);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP�ͻ��˳���", e);
		} finally {
			IOUtils.closeQuietly(fos);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("�ر�FTP���ӷ����쳣��", e);
			}
		}
	}
}