package util.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class FtpUtil extends FTPFile {

	private static Logger logger = Logger.getLogger(FtpUtil.class);

	public final static int FTP_CONNECT_ERROR = 0;
	public final static int FTP_UPLOAD_ALL_SUCCESS = 1;
	public final static int FTP_UPLOAD_PART_FAIL = -1;
	public final static int FTP_UPLOAD_ALL_FAIL = -2;
	public final static int FTP_CREATE_FOLDER_FAIL = -3;


	public static int uploadMFile(Properties properties, String basePath, String[] filePaths,
									  String filename, InputStream[] input) {

		/** 全部上传成功 */
		int flag = FTP_UPLOAD_ALL_SUCCESS;
		FTPClient ftp = initConnectFtp();
		try {
			// 切换到上传目录
			String p ="";
			for(int i = 0;i<filePaths.length;i++){
				 p= basePath +"/" +filePaths[i];
				if (!ftp.changeWorkingDirectory(p)) {
					// 如果目录不存在创建目录
					String pp = new String(p.replace(properties.getProperty("FTP_BASEPATH"), "").getBytes("GBK"),
							"iso-8859-1");
					String[] dirs = (pp.split("/"));
					StringBuilder sb = new StringBuilder();
					for (String dir : dirs) {
						sb.append("/" + dir);
						if (null == sb || "/".equals(sb))
							continue;
						if (!ftp.changeWorkingDirectory(sb.toString())) {
							if (!ftp.makeDirectory(sb.toString())) {
								flag = FTP_CREATE_FOLDER_FAIL;
							} else {
								ftp.changeWorkingDirectory(sb.toString());
							}
						}
					}
				}
				// 设置上传文件的类型为二进制类型
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				// 上传文件,如果带中文，需要将中文按照GBK转成ios-8859-1才能成功，并不能转成utf-8（因为utf-8已禁用），ios-8859-1兼容GBK
				boolean b = ftp.storeFile(new String(filename.getBytes("GBK"), "iso-8859-1"),input[i]);
				if (!b) {
					flag = FTP_UPLOAD_PART_FAIL;
				}
				input[i].close();
			}
			ftp.logout();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return flag;
	}


	public static int createFolder(String basePath, String filePath) {
		int flag = FTP_UPLOAD_ALL_SUCCESS;
		FTPClient ftp = initConnectFtp();
		try {
			// 切换到上传目录
			if(ftp!=null){
				String p ="测试文件夹/"+basePath +"/" +filePath;
				if (!ftp.changeWorkingDirectory(p)) {
					// 如果目录不存在创建目录
					String pp = new String(p.replace(basePath, "").getBytes("GBK"),
							"iso-8859-1");
					String[] dirs = (pp.split("/"));
					StringBuilder sb = new StringBuilder();
					for (String dir : dirs) {
						sb.append("/" + dir);
						if (null == sb || "/".equals(sb))
							continue;
						if (!ftp.changeWorkingDirectory(sb.toString())) {
							if (!ftp.makeDirectory(sb.toString())) {
								flag = FTP_CREATE_FOLDER_FAIL;
							} else {
								ftp.changeWorkingDirectory(sb.toString());
							}
						}
					}
				}
				ftp.logout();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return flag;
	}


	/**
	 * 初始化连接
	 * @return
	 */
	public static FTPClient initConnectFtp(){
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			FtpPropertyLoader loader = new FtpPropertyLoader();
			Properties properties = loader.getProperties();
			ftp.connect(properties.getProperty("FTP_ADDRESS"), Integer.parseInt(properties.getProperty("FTP_PORT")));// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
			ftp.login(properties.getProperty("FTP_USERNAME"), properties.getProperty("FTP_PASSWORD"));// 登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				logger.error("FTP服务器无法连接，请检查网络！");
				return null;
			}
			return ftp;
		}catch (Exception e){
			return null;
		}
	}


	/**
	 * 删除文件夹及目录
	 * @param pathName
	 * @return
	 */
	public static boolean removeDirectoryALLFile(String pathName) {
		FTPClient ftp = initConnectFtp();
		try {
			if(ftp!=null){
				FTPFile[] files = ftp.listFiles(pathName);
				boolean flag = true;
				if (null != files && files.length > 0) {
					for (FTPFile file : files) {
						if (file.isDirectory()) {
							removeDirectoryALLFile(pathName + "/" + file.getName());
							// 切换到父目录，不然删不掉文件夹
							ftp.changeWorkingDirectory(pathName.substring(0, pathName.lastIndexOf("/")));
							flag = ftp.removeDirectory(pathName);
							if(!flag){
								break;
							}
						} else {
							flag = ftp.deleteFile(pathName + "/" + file.getName());
							if (!flag) {
								break;
							}
						}
					}
					if(!flag){
						return false;
					}
				}
				ftp.changeWorkingDirectory(pathName.substring(0, pathName.lastIndexOf("/")));
				if(ftp.removeDirectory(pathName)){
					return true;
				}else{
					return flag;
				}
			}else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
	}

//	public static void main(String[] a) throws Exception{
//		FtpPropertyLoader loader = new FtpPropertyLoader();
//		Properties properties = loader.getProperties();
//		String path = "E:/Siemens/ftp/root/hozon/suppliers/CS2-CS2/2018-09-06 14-36-29";
//		FTPClient ftpClient = initConnectFtp();
//
//		String p ="/suppliers/CS3-CS3/2018-09-10 13-55-30";
//		boolean s = removeDirectoryALLFile(p);
//		boolean b = ftpClient.deleteFile(p);
//		boolean b  = removeAll(p);
//		deleteFile(pa);
//		createFolder(properties,properties.getProperty("FTP_BASEPATH"),"bb/cc");
//		System.out.println(s);
//		createFolder("11","cs3-cs2");
//	}
}