package util.email;


import bean.FailBean;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MailToApplicant {
	private PropertyLoader loader = new PropertyLoader();
	private Session session;
	private MimeMessage message;
	private Transport transport;
	private Properties props;
	/** 邮件接人人姓名*/
	private List<String> receiver;
	/** 邮件接收者邮箱*/
	private List<String> receiveMailAccount;
	private boolean debug = true;
	private String path = "";


	private List<String> files;
	//成功的清单
	private List<String> fileName;
	//失败清单
	private List<FailBean> failFiles;

	private List<String> realFileName;

	public MailToApplicant() {
		props = loader.getProperties();
		session = Session.getInstance(props);
		session.setDebug(debug);
	}

	public boolean release() {
		try {
			message = createMimeMessage(session, props.getProperty("myEmailAccount"), receiveMailAccount);
			// // 4. 根据 Session 获取邮件传输对象
			transport = session.getTransport();
			// 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
			//
			// PS_01: 成败的判断关键在此一句, 如果连接服务器失败, 都会在控制台输出相应失败原因的 log,
			// 仔细查看失败原因, 有些邮箱服务器会返回错误码或查看错误类型的链接, 根据给出的错误
			// 类型到对应邮件服务器的帮助网站上查看具体失败原因。
			//
			// PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
			// (1) 邮箱没有开启 SMTP 服务;
			// (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
			// (3) 邮箱服务器要求必须要使用 SSL 安全连接;
			// (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
			// (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
			// 452698545
			// PS_03: 仔细看log, 认真看log, 看懂log, 错误原因都在log已说明。ilAccount"),
			transport.connect(props.getProperty("mail.smtp.host"), props.getProperty("myEmailAccount"),
					props.getProperty("myEmailPassword"));
			// transport.connect(myEmailAccount, myEmailPassword);
			// 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients()
			// 获取到的是在创建邮件对象时添加的所有收件人,
			// 抄送人, 密送人
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			MessageBox.post("邮件发送失败，请检查收件人邮箱:" + receiveMailAccount, "邮件发送失败", MessageBox.ERROR);
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
		sb.append(getMailContent(realFileName,failFiles));
//		if(failFiles!=null&&failFiles.size()>0)
//		sb.append(getFailContent(failFiles,type));
//		sb.append("</table>");
//		sb.append("</div>");
//		sb.append(getHeadModelAnother(ITEM_TYPE));
//		sb.append(getSuccContent(items));
//		sb.append(getFailContent(itemFailFiles,ITEM_TYPE));
		sb.append(getEndModel());
		System.out.println(sb);
		message.setContent(sb.toString(), "text/html;charset=UTF-8");

		message.setSentDate(new Date());

		message.saveChanges();

		return message;
	}




	private StringBuilder getFailContent(List<FailBean> fileName2,String type) throws IOException, Exception {
		InputStream inputStream = this.getClass().getResourceAsStream("applicantMid");
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			if (line.trim().equals("-----")) {
				sb.append(addSupplier());
				continue;
			}
			if(line.trim().equals("<th></th>")){
				line = "<th>"+type+"</th>";
			}
			sb.append(line);
			sb.append("\n");
		}
		// sb.replace(0, 1, "");
		br.close();
		if(fileName2!=null &&fileName2.size()>0){

			for(int i= 0;i<fileName2.size();i++){
				sb.append("<tr>");
				sb.append("<td>");
				sb.append(fileName2.get(i).getName());
				sb.append("</td>");
				sb.append("<td>");
				sb.append(fileName2.get(i).getFailMsg());
				sb.append("</td>");
				sb.append("</tr>");
			}


		}

//		for (int i = 0; i < fileName2.size(); i++) {
//			sb.append("<tr>");
//			sb.append("<td>");
//			sb.append(fileName2.get(i));
//			sb.append("</td>");
//			sb.append("<td>");
//			sb.append(fileName2.get(i));
//			sb.append("</td>");
//			sb.append("</tr>");
//		}
		return sb;
	}

	/***
	 *
	 * @param fileName
	 * @return
	 */
	private StringBuilder getSuccContent(List<String> fileName) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fileName.size(); i++) {
			sb.append("<tr>");
			sb.append("<td>");
			sb.append(fileName.get(i));
			sb.append("</td>");
			sb.append("</tr>");
		}
		return sb;
	}

	/**
	 * 后期需求更改 邮件样式产生了变更
	 * todo author haozt
	 * @param fileRealName
	 * @param result
	 * @return
	 */
	private StringBuilder getMailContent(List<String> fileRealName,List<FailBean> result) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < result.size(); i++) {
			sb.append("<tr>");
			sb.append("<td>");
			sb.append(result.get(i).getName());
			sb.append("</td>");
			if(fileRealName != null && fileRealName.size() == result.size()){
				sb.append("<td>");
				sb.append(fileRealName.get(i));
				sb.append("</td>");
			}else {
				sb.append("<td>");
				sb.append("-");
				sb.append("</td>");
			}
			sb.append("<td>");
			sb.append(result.get(i).getFailMsg());
			sb.append("</td>");
			sb.append("</tr>");
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
		sb.append("	好");
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
