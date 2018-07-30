package util.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import sql.dbdo.HzReleaseFileRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class FtpUtil {
	public final static int FTP_CONNECT_ERROR = 0;
	public final static int FTP_UPLOAD_ALL_SUCCESS = 1;
	public final static int FTP_UPLOAD_PART_FAIL = -1;
	public final static int FTP_UPLOAD_ALL_FAIL = -2;
	public final static int FTP_CREATE_FOLDER_FAIL = -3;

	private static FTPClient ftp = new FTPClient();



	public static int uploadMFile(Properties properties, String basePath, String filePath,
									  String filename, InputStream input) {
		/** 全部上传成功 */
		int flag = FTP_UPLOAD_ALL_SUCCESS;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(properties.getProperty("FTP_ADDRESS"), Integer.parseInt(properties.getProperty("FTP_PORT")));// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
			ftp.login(properties.getProperty("FTP_USERNAME"), properties.getProperty("FTP_PASSWORD"));// 登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return FTP_CONNECT_ERROR;
			}
			// 切换到上传目录

				String p = basePath + "/" + filePath;
				if (!ftp.changeWorkingDirectory(p)) {
					// 如果目录不存在创建目录
					// String path=basePath.get(i)
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
				if (!ftp.storeFile(new String(filename.getBytes("GBK"), "iso-8859-1"), input)) {
					flag = FTP_UPLOAD_PART_FAIL;
				}

//				p = p.replace(properties.getProperty("FTP_TRIMBASEPATH"), "");

				input.close();

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

	/**
	 * 上传文件至ftp
	 * @param properties
	 * @param basePath
	 * @param filePath
	 * @param filename
	 * @param input
	 * @return
	 */
	public static int uploadMutilFile1(Properties properties, String basePath, String filePath,
									   String filename, InputStream input) {
		/** 全部上传成功 */
		int flag = FTP_UPLOAD_ALL_SUCCESS;
		ftp.setControlEncoding("UTF-8");
		try {
			int reply;
			ftp.connect(properties.getProperty("FTP_ADDRESS"), Integer.parseInt(properties.getProperty("FTP_PORT")));// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
			ftp.login(properties.getProperty("FTP_USERNAME"), properties.getProperty("FTP_PASSWORD"));// 登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return FTP_CONNECT_ERROR;
			}
			// 切换到上传目录

			String p = basePath+ "/" + filePath;
			if (!ftp.changeWorkingDirectory(p)) {
				// 如果目录不存在创建目录
				// String path=basePath.get(i)
				String pp = new String(p.replace(properties.getProperty("FTP_BASEPATH"), ""));
//					String pp = new String(p.replace(properties.getProperty("FTP_BASEPATH"), "").getBytes("GBK"),
//							"UTF-8");
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
							boolean ps = ftp.changeWorkingDirectory(sb.toString());
							System.out.println(ps);
						}
					}
				}
			}
			// 设置上传文件的类型为二进制类型
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			// 上传文件,如果带中文，需要将中文按照GBK转成ios-8859-1才能成功，并不能转成utf-8（因为utf-8已禁用），ios-8859-1兼容GBK
//			if (!ftp.storeFile(new String(filename), input)) {
//				flag = FTP_UPLOAD_PART_FAIL;
//			}
				if (!ftp.storeFile(new String(filename.getBytes("GBK"), "iso-8859-1"), input)) {
					flag = FTP_UPLOAD_PART_FAIL;
				}
//			p = p.replace(properties.getProperty("FTP_TRIMBASEPATH"), "");

			input.close();

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

	/******** 如果发生550错误无法在浏览器中查看文件，需要设置utf-8 *******/
	/**
	 * Description: 向FTP服务器上传文件
	 *
	 * @param host
	 *            FTP服务器host
	 * @param port
	 *            FTP服务器端口
	 * @param username
	 *            FTP登录账号
	 * @param password
	 *            FTP登录密码
	 * @param basePath
	 *            FTP服务器基础目录
	 * @param filePath
	 *            FTP服务器文件存放路径。例如分日期存放：/2015/01/01。文件的路径为basePath+filePath
	 * @param filename
	 *            上传到FTP服务器上的文件名
	 * @param input
	 *            输入流
	 * @return 成功返回true，否则返回false
	 */
	public static boolean uploadFile(String host, int port, String username, String password, String basePath,
									 String filePath, String filename, InputStream input) {
		boolean result = false;
		ftp.setControlEncoding("UTF-8");
		try {
			int reply;
			ftp.connect(host, port);// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
			ftp.login(username, password);// 登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return result;
			}
			// 切换到上传目录
			if (!ftp.changeWorkingDirectory(basePath + filePath)) {
				// 如果目录不存在创建目录
				String[] dirs = filePath.split("/");
				String tempPath = basePath;

				for (String dir : dirs) {
					if (null == dir || "".equals(dir))
						continue;
					tempPath += "/" + dir;
					if (!ftp.changeWorkingDirectory(tempPath)) {
						if (!ftp.makeDirectory(tempPath)) {
							return result;
						} else {
							ftp.changeWorkingDirectory(tempPath);
						}
					}
				}
			}
			// 设置上传文件的类型为二进制类型
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			// 上传文件,如果带中文，需要将中文按照GBK转成ios-8859-1才能成功，并不能转成utf-8（因为utf-8已禁用），ios-8859-1兼容GBK
			if (!ftp.storeFile(new String(filename.getBytes("GBK"), "iso-8859-1"), input)) {
				return result;
			}
			input.close();
			ftp.logout();
			result = true;
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
		return result;
	}






	/**
	 * 上传文件到FTP服务器
	 * @param fileName
	 * @param
	 * @param input
	 * @return
	 */
	public static boolean upload(String host, int port, String username, String password,String filePath,String fileName,InputStream input){
		boolean result = false;
		ftp.setControlEncoding("UTF-8");
		String path ="/"+filePath;
		try {
			int reply;
			ftp.connect(host, port);// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
			ftp.login(username, password);// 登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return result;
			}
			if (!ftp.changeWorkingDirectory(path)) {
				mkdirs(path);
			}
//			cd(path);
			ftp.setBufferSize(1024);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			result = ftp.storeFile(fileName, input);
			input.close();  //关闭输入流
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
		return result;
	}
	/**
	 * 没修改过，慎用
	 *
	 * Description: 向FTP服务器上传文件，批量上传，输入流会在内部关闭
	 *
	 * @param
	 *
	 * @param
	 * @param
	 * @param
	 *
	 * @param
	 *
	 * @param basePath
	 *            FTP服务器基础目录，基础目录长度filePath和文件路径长度一致
	 * @param itemRecord
	 * @param trimPath2
	 * @param date_path
	 * @param filePath，和基础目录basePath长度一致
	 *            FTP服务器文件存放路径。例如分日期存放：/2015/01/01。文件的路径为basePath+filePath
	 * @param filename，和基础目录basePath、文件路径filePath长度一致
	 *            上传到FTP服务器上的文件名
	 * @param input,长度一致
	 *            输入流
	 * @return 成功返回true，否则返回false
	 */
	@Deprecated
	public static int uploadMutilFile(Properties properties, ArrayList<String> basePath, ArrayList<String> filePath,
									  ArrayList<String> filename, ArrayList<InputStream> input, ArrayList<HzReleaseFileRecord> itemRecord,
									  String trimPath2, String date_path) {
		/** 全部上传成功 */
		int flag = FTP_UPLOAD_ALL_SUCCESS;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(properties.getProperty("FTP_ADDRESS"), Integer.parseInt(properties.getProperty("FTP_PORT")));// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
			ftp.login(properties.getProperty("FTP_USERNAME"), properties.getProperty("FTP_PASSWORD"));// 登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return FTP_CONNECT_ERROR;
			}
			// 切换到上传目录
			for (int i = 0; i < basePath.size(); i++) {
				String p = basePath.get(i) + "/" + filePath.get(i);
				if (!ftp.changeWorkingDirectory(p)) {
					// 如果目录不存在创建目录
					// String path=basePath.get(i)
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
								boolean ps = ftp.changeWorkingDirectory(sb.toString());
								System.out.println(ps);
							}
						}
					}
				}
				// 设置上传文件的类型为二进制类型
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				// 上传文件,如果带中文，需要将中文按照GBK转成ios-8859-1才能成功，并不能转成utf-8（因为utf-8已禁用），ios-8859-1兼容GBK
				if (!ftp.storeFile(new String(filename.get(i).getBytes("GBK"), "iso-8859-1"), input.get(i))) {
					flag = FTP_UPLOAD_PART_FAIL;
				}
				itemRecord.get(i).setFileName(filename.get(i));
				p = p.replace(properties.getProperty("FTP_TRIMBASEPATH"), "");
				itemRecord.get(i).setFilePath(p);
				input.get(i).close();
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

	/**
	 * Description: 从FTP服务器下载文件
	 *
	 * @param host
	 *            FTP服务器hostname
	 * @param port
	 *            FTP服务器端口
	 * @param username
	 *            FTP登录账号
	 * @param password
	 *            FTP登录密码
	 * @param remotePath
	 *            FTP服务器上的相对路径
	 * @param fileName
	 *            要下载的文件名
	 * @param localPath
	 *            下载后保存到本地的路径
	 * @return
	 */
	public static boolean downloadFile(String host, int port, String username, String password, String remotePath,
									   String fileName, String localPath) {
		boolean result = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(host, port);
			// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
			ftp.login(username, password);// 登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return result;
			}
			ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
			FTPFile[] fs = ftp.listFiles();
			for (FTPFile ff : fs) {
				if (ff.getName().equals(fileName)) {
					File localFile = new File(localPath + "/" + ff.getName());

					OutputStream is = new FileOutputStream(localFile);
					ftp.retrieveFile(ff.getName(), is);
					is.close();
				}
			}

			ftp.logout();
			result = true;
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
		return result;
	}
	/**
	 * 循环切换目录
	 * @param dir
	 * @return
	 */
	public static boolean cd(String dir){
		boolean stat = true;
		try {
			String[] dirs = dir.split("/");
			if(dirs.length == 0){
				return ftp.changeWorkingDirectory(dir);
			}

			stat = ftp.changeToParentDirectory();
			for(String dirss : dirs){
				stat = stat && ftp.changeWorkingDirectory(dirss);
			}

			stat = true;
		} catch (IOException e) {
			stat = false;
		}
		return stat;
	}

	/***
	 * 创建目录
	 * @param dir
	 * @return
	 */
	public static boolean mkdir(String dir){
		boolean stat = false;
		try {
			ftp.changeToParentDirectory();
			ftp.makeDirectory(dir);
			stat = true;
		} catch (IOException e) {
			stat = false;
		}
		return stat;
	}

	/***
	 * 创建多个层级目录
	 * @param dir dong/zzz/ddd/ewv
	 * @return
	 */
	public static boolean mkdirs(String dir){
		String[] dirs = dir.split("/");
		if(dirs.length == 0){
			return false;
		}
		boolean stat = false;
		try {
			ftp.changeToParentDirectory();
			for(String dirss : dirs){
				ftp.makeDirectory(dirss);
				ftp.changeWorkingDirectory(dirss);
			}

			ftp.changeToParentDirectory();
			stat = true;
		} catch (IOException e) {
			stat = false;
		}
		return stat;
	}


}