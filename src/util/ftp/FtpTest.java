package util.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Apache commons-net 试用一把，看看FTP客户端工具做的好用不
 * 
 */
public class FtpTest {
	public static void main(String[] args) {
		testUpload();
		// testDownload();
	}

	/**
	 * FTP上传单个文件测试 1.连接服务 2.确定你要上传的文件 3.指定你在服务器端存放的位置
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
			// 设置上传目录
			File path = new File("/hozon/text");
			System.out.println(path.getPath());
			ftpClient.changeWorkingDirectory(path.getPath());
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("GBK");
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.storeFile("RELEASE-NOTES-sss.txt", fis);
			System.out.println("上传完成");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * FTP下载单个文件测试
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
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(remoteFileName, fos);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fos);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}
}