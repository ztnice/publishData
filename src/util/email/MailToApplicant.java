package util.email;


import bean.FailBean;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MailToApplicant {
	private static Logger logger = Logger.getLogger(MailToApplicant.class);
	private PropertyLoader loader = new PropertyLoader();
	private Session session;
	private MimeMessage message ;
	private Transport transport ;
	private Properties props;
	/** 邮件接人人姓名*/
	private List<String> receiver;
	/** 邮件接收者邮箱*/
	private List<String> receiveMailAccount;
//	private boolean debug = true;
	private String path ="";

	private String processNum;

	private List<String> files;
	//成功的清单
	private List<String> fileName;
	//失败清单
	private List<FailBean> failFiles;

	private List<String> realFileName;

	public MailToApplicant() {
		this.props = loader.getProperties();
		this.session = Session.getInstance(props);
		this.session.setDebug(true);
	}

	public boolean release() {
		try {
//			PropertyLoader loader = new PropertyLoader();
//			Properties props = loader.getProperties();
//			Session session = Session.getInstance(props);
			this.message = createMimeMessage(session, props.getProperty("myEmailAccount"), receiveMailAccount);
			this.transport = session.getTransport();
			this.transport.connect(props.getProperty("mail.smtp.host"), props.getProperty("myEmailAccount"),
					props.getProperty("myEmailPassword"));
			this.transport.sendMessage(message, message.getAllRecipients());
			this.transport.close();
		} catch (Exception e) {
			logger.error("邮箱无法连接，请核对网络稍后重试！");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 创建一封只包含文本的简单邮件
	 *
	 * @param session
	 *            和服务器交互的会话
	 * @param sendMail
	 *            发件人邮箱
	 * @param receiveMail
	 *            收件人邮箱
	 * @return
	 * @throws Exception
	 */
	public MimeMessage createMimeMessage(Session session, String sendMail, List<String> receiveMail) throws Exception {
		// 1. 创建一封邮件
		MimeMessage message = new MimeMessage(session);

		// 2. From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）

		message.setFrom(new InternetAddress(sendMail, "合众新能源汽车有限公司", "UTF-8"));

		// 3. To: 收件人（可以增加多个收件人、抄送、密送）
		//message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "XX???", "GB2312"));
		//多个收件人
		StringBuffer buffer = new StringBuffer();
		for(String str :receiveMail){
			buffer.append(str+",");
		}
		message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(buffer.toString()));
		message.setSubject("数据发放", "UTF-8");
		StringBuilder sb = new StringBuilder();

		sb.append(getMailHeadModel());
		sb.append(getMailContent(failFiles));
		sb.append(getEndModel());
		message.setContent(sb.toString(), "text/html;charset=UTF-8");

		message.setSentDate(new Date());

		message.saveChanges();

		return message;
	}




//	private StringBuilder getFailContent(List<FailBean> fileName2,String type) throws IOException, Exception {
//		InputStream inputStream = this.getClass().getResourceAsStream("applicantMid");
//		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//		String line = null;
//		StringBuilder sb = new StringBuilder();
//		while ((line = br.readLine()) != null) {
//			if (line.trim().equals("-----")) {
//				sb.append(addSupplier());
//				continue;
//			}
//			if(line.trim().equals("<th></th>")){
//				line = "<th>"+type+"</th>";
//			}
//			sb.append(line);
//			sb.append("\n");
//		}
//		// sb.replace(0, 1, "");
//		br.close();
//		if(fileName2!=null &&fileName2.size()>0){
//
//			for(int i= 0;i<fileName2.size();i++){
//				sb.append("<tr>");
//				sb.append("<td>");
//				sb.append(fileName2.get(i).getName());
//				sb.append("</td>");
//				sb.append("<td>");
//				sb.append(fileName2.get(i).getFailMsg());
//				sb.append("</td>");
//				sb.append("</tr>");
//			}
//
//
//		}
//
////		for (int i = 0; i < fileName2.size(); i++) {
////			sb.append("<tr>");
////			sb.append("<td>");
////			sb.append(fileName2.get(i));
////			sb.append("</td>");
////			sb.append("<td>");
////			sb.append(fileName2.get(i));
////			sb.append("</td>");
////			sb.append("</tr>");
////		}
//		return sb;
//	}

	/***
	 *
	 * @param fileName
	 * @return
	 */
//	private StringBuilder getSuccContent(List<String> fileName) {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < fileName.size(); i++) {
//			sb.append("<tr>");
//			sb.append("<td>");
//			sb.append(fileName.get(i));
//			sb.append("</td>");
//			sb.append("</tr>");
//		}
//		return sb;
//	}

	/**
	 * 后期需求更改 邮件样式产生了变更
	 * todo author haozt
	 * @param
	 * @param result
	 * @return
	 */
	private StringBuilder getMailContent(List<FailBean> result) {
		StringBuilder sb = new StringBuilder();
		if(result!=null && result.size()>0){
			for (int i = 0; i < result.size(); i++) {
				sb.append("<tr>");
				sb.append("<td>");
				sb.append(result.get(i).getName());
				sb.append("</td>");

				sb.append("<td>");
				if(result.get(i).getRealName() == null){
					sb.append("-");
				}else {
					sb.append(result.get(i).getRealName());
				}
				sb.append("</td>");

				sb.append("<td>");
				sb.append(result.get(i).getFailMsg());
				sb.append("</td>");
				sb.append("</tr>");
			}
		}

		return sb;
	}

	/**
	 * 后期需求更改 邮件样式产生了变更
	 * todo author haozt
	 * @return
	 * @throws Exception
	 */
	private StringBuilder getMailHeadModel ()throws Exception {
		InputStream inputStream = this.getClass().getResourceAsStream("applicantHead");
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			if (line.trim().equals("-----")) {
				sb.append(addSupplier());
				continue;
			}
			if (line.trim().contains("发放结果")) {
				if(processNum != null && processNum!=""){
					line = processNum+"的发放结果：";
					sb.append(line);
				}
				continue;
			}
			sb.append(line);
			sb.append("\n");
		}
		// sb.replace(0, 1, "");
		br.close();
		return sb;

	}

	private StringBuilder getHeadModel(String type) throws Exception {
		InputStream inputStream = this.getClass().getResourceAsStream("applicantHead");
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			if (line.trim().equals("-----")) {
				sb.append(addSupplier());
				continue;
			}
			if(line.trim().contains("<th>")){
				line="<th>"+type+"</th>";
			}
			sb.append(line);

			sb.append("\n");
		}
		// sb.replace(0, 1, "");
		br.close();
		return sb;

	}


	private StringBuilder getHeadModelAnother(String type) throws Exception {
		InputStream inputStream = this.getClass().getResourceAsStream("applicantHead");
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			if (line.trim().equals("-----")) {
//				sb.append(addSupplier());
				continue;
			}
			if(line.trim().contains("<th>")){
				line="<th>"+type+"</th>";

			}
			sb.append(line);
			sb.append("\n");
		}
		// sb.replace(0, 1, "");
		br.close();
		return sb;

	}


	private StringBuilder getEndModel() throws Exception {
		InputStream inputStream = this.getClass().getResourceAsStream("applicantEnd");
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			if (line.trim().equals("--sender--")) {
				sb.append(addSender());
				continue;
			} else if (line.trim().equals("--sharePath--")) {
				sb.append(addSharePath());
				continue;
			} else if (line.trim().equals("--date--")) {
				sb.append(addDate());
				continue;
			}
			sb.append(line);
			sb.append("\n");
		}
		// sb.replace(0, 1, "");
		br.close();
		return sb;
	}

	/**
	 *
	 * @return
	 */
	private StringBuilder addSender() {
		StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < applicant.size(); i++) {
		// if (i > 0) {
		// sb.append("," + applicant.get(i));
		// } else
		// sb.append(applicant.get(i));
		// }
		sb.append("TC数据发放");
		return sb;
	}

	/**
	 *
	 * @return
	 */
	private StringBuilder addDate() {
		Calendar cal = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append(cal.get(Calendar.YEAR) + "年" + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DATE) + "日");
		return sb;
	}

	private StringBuilder addSharePath() {
		StringBuilder sharePath = new StringBuilder();
		sharePath.append(this.getPath());
		return sharePath;
	}

	/***
	 *
	 * @return
	 * @throws Exception
	 */
	private StringBuilder addSupplier() throws Exception {
		StringBuilder sb = new StringBuilder();
		if (receiver.size() <= 0 || receiver == null)
			throw new Exception("没有邮件接收者");
		for (String string : receiver) {
			sb.append(string + ",");
		}
		sb.append("	您好");
		return sb;
	}

	public List<String> getReceiver() {
		return receiver;
	}

	public void setReceiver(List<String> receiver) {
		this.receiver = receiver;
	}



	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public List<String> getFileName() {
		return fileName;
	}

	public void setFileName(List<String> fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public String getProcessNum() {
		return processNum;
	}

	public void setProcessNum(String processNum) {
		this.processNum = processNum;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<String> getReceiveMailAccount() {
		return receiveMailAccount;
	}

	public void setReceiveMailAccount(List<String> receiveMailAccount) {
		this.receiveMailAccount = receiveMailAccount;
	}


	public List<FailBean> getFailFiles() {
		return failFiles;
	}

	public void setFailFiles(List<FailBean> failFiles) {
		this.failFiles = failFiles;
	}

	public List<String> getRealFileName() {
		return realFileName;
	}

	public void setRealFileName(List<String> realFileName) {
		this.realFileName = realFileName;
	}
}
