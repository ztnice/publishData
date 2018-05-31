package util.email;


import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MailToSupplier {
	private PropertyLoader loader = new PropertyLoader();
	private Session session;
	private MimeMessage message;
	private Transport transport;
	private Properties props;
	/** 接收邮件者 */
	private List<String> receiver;
	/** 申请发放者 */
	private List<String> applicant;

	// 发件人的 邮箱 和 密码（替换为自己的邮箱和密码）
	// PS: 某些邮箱服务器为了增加邮箱本身密码的安全性，给 SMTP 客户端设置了独立密码（有的邮箱称为“授权码”）,
	// 对于开启了独立密码的邮箱, 这里的邮箱密码必需使用这个独立密码（授权码）。

	/**邮件接受者邮箱地址*/
	private List<String> receiveMailAccount;
	private boolean debug = true;
	private String path = "ftp://10.0.8.231/root/...";

	private List<String> files;
	//发放清单
	private List<String> fileNames;
	//发放清单
	private List<String> itemNames;

    public static  final String ITEM_TYPE = "零件名称";
    public static  final String FILE_TYPE = "文件名称";

	public MailToSupplier() {
		props = loader.getProperties();
		session = Session.getInstance(props);
		session.setDebug(debug); // 设置为debug模式, 可以查看详细的发送 log
		// 4. 根据 Session 获取邮件传输对象
	}

	public boolean release(String type) {
		try {
			message = createMimeMessage(session, type,props.getProperty("myEmailAccount"), receiveMailAccount);

			// 4. 根据 Session 获取邮件传输对象
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
			// PS_03: 仔细看log, 认真看log, 看懂log, 错误原因都在log已说明。
			transport.connect(props.getProperty("mail.smtp.host"), props.getProperty("myEmailAccount"),
					props.getProperty("myEmailPassword"));
			// transport.connect(myEmailAccount, myEmailPassword);
			// 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients()
			// 获取到的是在创建邮件对象时添加的所有收件人,
			// 抄送人, 密送人
			transport.sendMessage(message, message.getAllRecipients());
			// 7. 关闭连接
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
	public MimeMessage createMimeMessage(Session session,String type, String sendMail, List<String> receiveMail) throws Exception {
		// 1. 创建一封邮件
		MimeMessage message = new MimeMessage(session);

		// 2. From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）
		message.setFrom(new InternetAddress(sendMail, "合众新能源汽车有限公司", "UTF-8"));

		// 3. To: 收件人（可以增加多个收件人、抄送、密送）
//		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "XX用户", "UTF-8"));

		StringBuffer buffer = new StringBuffer();
		for(String str :receiveMail){
			buffer.append(str+",");
		}
		message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(buffer.toString()));
		// 4. Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
		message.setSubject("数据发放", "UTF-8");

		// 5. Content: 邮件正文（可以使用html标签）（内容有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改发送内容）
		// message.setContent("XX用户你好, 今天全场5折, 快来抢购,<br/>---------\n
		// 错过今天再等一年。。。", "text/html;charset=UTF-8");
		StringBuilder sb = new StringBuilder();
		sb.append(getHeadModel(type));
		sb.append(getContent(fileNames));
//        sb.append("</table>");
//        sb.append("</div>");
//		sb.append(getHeadModelAnother(ITEM_TYPE));
//		sb.append(getContent(itemNames));
		sb.append(getEndModel());
		System.out.println(sb);
		message.setContent(sb.toString(), "text/html;charset=UTF-8");
		// 6. 设置发件时间

		message.setSentDate(new Date());

		// 7. 保存设置
		message.saveChanges();

		return message;
	}

	private StringBuilder getContent(List<String> fileNames) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fileNames.size(); i++) {
			sb.append("<tr>");
			sb.append("<td>");
			sb.append(fileNames.get(i));
			sb.append("</td>");
			// sb.append("<td>");
			// sb.append(fileName.get(i));
			// sb.append("</td>");
			sb.append("</tr>");
		}
		return sb;
	}

	public List<String> getReceiver() {
		return receiver;
	}

	public void setReceiver(List<String> receiver) {
		this.receiver = receiver;
	}

	public List<String> getApplicant() {
		return applicant;
	}

	public void setApplicant(List<String> applicant) {
		this.applicant = applicant;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
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

	private StringBuilder getHeadModel(String type) throws Exception {
		InputStream inputStream = this.getClass().getResourceAsStream("receiverHead.txt");
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
		sb.replace(0, 1, "");
		br.close();
		System.out.println(sb);
		return sb;

	}


    private StringBuilder getHeadModelAnother(String type) throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("receiverHead.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            if (line.trim().equals("-----")) {
//                sb.append(addSupplier());
                continue;
            }
            if(line.trim().contains("<th>")){
                line="<th>"+type+"</th>";
            }
            sb.append(line);
            sb.append("\n");
        }
        sb.replace(0, 1, "");
        br.close();
        System.out.println(sb);
        return sb;

    }
	private StringBuilder getEndModel() throws Exception {
		InputStream inputStream = this.getClass().getResourceAsStream("receiverEnd.txt");
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
		sb.replace(0, 1, "");
		br.close();
		System.out.println(sb);
		return sb;
	}

	/**
	 * 添加发送者
	 * 
	 * @return
	 */
	private StringBuilder addSender() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < applicant.size(); i++) {
			if (i > 0) {
				sb.append("," + applicant.get(i));
			} else
				sb.append(applicant.get(i));
		}
		return sb;
	}

	/**
	 * 添加日期
	 * 
	 * @return
	 */
	private StringBuilder addDate() {
		Calendar cal = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append(cal.get(Calendar.YEAR) + "年" + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DATE) + "日");
		return sb;
	}

	/** 添加共享路径 */
	private StringBuilder addSharePath() {
		StringBuilder sharePath = new StringBuilder();
		sharePath.append(this.getPath());
		return sharePath;
	}

	/***
	 * 添加供应商
	 * 
	 * @return
	 * @throws Exception
	 */
	private StringBuilder addSupplier() throws Exception {
		StringBuilder sb = new StringBuilder();
		if (receiver.size() <= 0 || receiver == null)
			throw new Exception("没有邮件接收者");
		for (String string : receiver) {
			sb.append(string + "，");
		}
		sb.append("	好：");
		return sb;
	}

	public static void main(String[] args) {
		MailToSupplier mailUtil = new MailToSupplier();
		ArrayList<String> receiver = new ArrayList<>();
		receiver.add("John");
		receiver.add("Suphei");
		ArrayList<String> applicant = new ArrayList<>();
		applicant.add("张三");
		applicant.add("李四");
		ArrayList<String> fileName = new ArrayList<>();
		fileName.add("sdas");

		List<String> itemLists = new ArrayList<>();
		itemLists.add("111");
		itemLists.add("222");
//		ArrayList<String> files = new ArrayList<>();
//		files.add("sdads");
		List<String> mail = new ArrayList<>();
		mail.add("zhit876163.com");
		mailUtil.setReceiveMailAccount(mail);
		mailUtil.setReceiver(receiver);
		mailUtil.setApplicant(applicant);
		mailUtil.setFileNames(fileName);
		mailUtil.setItemNames(itemLists);
//		mailUtil.setFiles(files);
		try {
			mailUtil.release("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
