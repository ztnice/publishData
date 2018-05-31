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


	/**
	 * �ϴ��ļ���ftp
	 * @param properties
	 * @param basePath
	 * @param filePath
	 * @param filename
	 * @param input
	 * @return
	 */
	public static int uploadMutilFile1(Properties properties, String basePath, String filePath,
									  String filename, InputStream input) {
		/** ȫ���ϴ��ɹ� */
		int flag = FTP_UPLOAD_ALL_SUCCESS;
		ftp.setControlEncoding("UTF-8");
		try {
			int reply;
			ftp.connect(properties.getProperty("FTP_ADDRESS"), Integer.parseInt(properties.getProperty("FTP_PORT")));// ����FTP������
			// �������Ĭ�϶˿ڣ�����ʹ��ftp.connect(host)�ķ�ʽֱ������FTP������
			ftp.login(properties.getProperty("FTP_USERNAME"), properties.getProperty("FTP_PASSWORD"));// ��¼
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return FTP_CONNECT_ERROR;
			}
			// �л����ϴ�Ŀ¼

				String p = basePath+ "/" + filePath;
				if (!ftp.changeWorkingDirectory(p)) {
					// ���Ŀ¼�����ڴ���Ŀ¼
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
				// �����ϴ��ļ�������Ϊ����������
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				// �ϴ��ļ�,��������ģ���Ҫ�����İ���GBKת��ios-8859-1���ܳɹ���������ת��utf-8����Ϊutf-8�ѽ��ã���ios-8859-1����GBK
				if (!ftp.storeFile(new String(filename), input)) {
					flag = FTP_UPLOAD_PART_FAIL;
				}
//				if (!ftp.storeFile(new String(filename.getBytes("GBK"), "iso-8859-1"), input)) {
//					flag = FTP_UPLOAD_PART_FAIL;
//				}
				p = p.replace(properties.getProperty("FTP_TRIMBASEPATH"), "");

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

	/******** �������550�����޷���������в鿴�ļ�����Ҫ����utf-8 *******/
	/**
	 * Description: ��FTP�������ϴ��ļ�
	 * 
	 * @param host
	 *            FTP������host
	 * @param port
	 *            FTP�������˿�
	 * @param username
	 *            FTP��¼�˺�
	 * @param password
	 *            FTP��¼����
	 * @param basePath
	 *            FTP����������Ŀ¼
	 * @param filePath
	 *            FTP�������ļ����·������������ڴ�ţ�/2015/01/01���ļ���·��ΪbasePath+filePath
	 * @param filename
	 *            �ϴ���FTP�������ϵ��ļ���
	 * @param input
	 *            ������
	 * @return �ɹ�����true�����򷵻�false
	 */
	public static boolean uploadFile(String host, int port, String username, String password, String basePath,
			String filePath, String filename, InputStream input) {
		boolean result = false;
		ftp.setControlEncoding("UTF-8");
		try {
			int reply;
			ftp.connect(host, port);// ����FTP������
			// �������Ĭ�϶˿ڣ�����ʹ��ftp.connect(host)�ķ�ʽֱ������FTP������
			ftp.login(username, password);// ��¼
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return result;
			}
			// �л����ϴ�Ŀ¼
			if (!ftp.changeWorkingDirectory(basePath + filePath)) {
				// ���Ŀ¼�����ڴ���Ŀ¼
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
			// �����ϴ��ļ�������Ϊ����������
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			// �ϴ��ļ�,��������ģ���Ҫ�����İ���GBKת��ios-8859-1���ܳɹ���������ת��utf-8����Ϊutf-8�ѽ��ã���ios-8859-1����GBK
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
	 * �ϴ��ļ���FTP������
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
			ftp.connect(host, port);// ����FTP������
			// �������Ĭ�϶˿ڣ�����ʹ��ftp.connect(host)�ķ�ʽֱ������FTP������
			ftp.login(username, password);// ��¼
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
			input.close();  //�ر�������
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
	 * û�޸Ĺ�������
	 * 
	 * Description: ��FTP�������ϴ��ļ��������ϴ��������������ڲ��ر�
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
	 *            FTP����������Ŀ¼������Ŀ¼����filePath���ļ�·������һ��
	 * @param itemRecord
	 * @param trimPath2
	 * @param date_path
	 * @param filePath���ͻ���Ŀ¼basePath����һ��
	 *            FTP�������ļ����·������������ڴ�ţ�/2015/01/01���ļ���·��ΪbasePath+filePath
	 * @param filename���ͻ���Ŀ¼basePath���ļ�·��filePath����һ��
	 *            �ϴ���FTP�������ϵ��ļ���
	 * @param input,����һ��
	 *            ������
	 * @return �ɹ�����true�����򷵻�false
	 */
	@Deprecated
	public static int uploadMutilFile(Properties properties, ArrayList<String> basePath, ArrayList<String> filePath,
			ArrayList<String> filename, ArrayList<InputStream> input, ArrayList<HzReleaseFileRecord> itemRecord,
			String trimPath2, String date_path) {
		/** ȫ���ϴ��ɹ� */
		int flag = FTP_UPLOAD_ALL_SUCCESS;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(properties.getProperty("FTP_ADDRESS"), Integer.parseInt(properties.getProperty("FTP_PORT")));// ����FTP������
			// �������Ĭ�϶˿ڣ�����ʹ��ftp.connect(host)�ķ�ʽֱ������FTP������
			ftp.login(properties.getProperty("FTP_USERNAME"), properties.getProperty("FTP_PASSWORD"));// ��¼
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return FTP_CONNECT_ERROR;
			}
			// �л����ϴ�Ŀ¼
			for (int i = 0; i < basePath.size(); i++) {
				String p = basePath.get(i) + "/" + filePath.get(i);
				if (!ftp.changeWorkingDirectory(p)) {
					// ���Ŀ¼�����ڴ���Ŀ¼
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
				// �����ϴ��ļ�������Ϊ����������
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				// �ϴ��ļ�,��������ģ���Ҫ�����İ���GBKת��ios-8859-1���ܳɹ���������ת��utf-8����Ϊutf-8�ѽ��ã���ios-8859-1����GBK
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
	 * Description: ��FTP�����������ļ�
	 * 
	 * @param host
	 *            FTP������hostname
	 * @param port
	 *            FTP�������˿�
	 * @param username
	 *            FTP��¼�˺�
	 * @param password
	 *            FTP��¼����
	 * @param remotePath
	 *            FTP�������ϵ����·��
	 * @param fileName
	 *            Ҫ���ص��ļ���
	 * @param localPath
	 *            ���غ󱣴浽���ص�·��
	 * @return
	 */
	public static boolean downloadFile(String host, int port, String username, String password, String remotePath,
			String fileName, String localPath) {
		boolean result = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(host, port);
			// �������Ĭ�϶˿ڣ�����ʹ��ftp.connect(host)�ķ�ʽֱ������FTP������
			ftp.login(username, password);// ��¼
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return result;
			}
			ftp.changeWorkingDirectory(remotePath);// ת�Ƶ�FTP������Ŀ¼
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
	 * ѭ���л�Ŀ¼
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
	 * ����Ŀ¼
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
	 * ��������㼶Ŀ¼
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