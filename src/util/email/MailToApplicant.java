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
	/** �ʼ�����������*/
	private List<String> receiver;
	/** �ʼ�����������*/
	private List<String> receiveMailAccount;
	private boolean debug = true;
	private String path = "";


	private List<String> files;
	//�ɹ��ļ������嵥
	private List<String> fileName;
	//ʧ�ܵ��ļ��嵥
	private List<FailBean> failFiles;

//	//�ɹ�������嵥
//	private List<String> items;
//	//ʧ�ܵ�����嵥
//	private List<FailBean> itemFailFiles;


	public static  final String ITEM_TYPE = "�������";
	public static  final String FILE_TYPE = "�ļ�����";

	public MailToApplicant() {
		props = loader.getProperties();
		session = Session.getInstance(props);
		session.setDebug(debug);
	}

	public boolean release(String type) {
		try {
			message = createMimeMessage(session,type, props.getProperty("myEmailAccount"), receiveMailAccount);
			// 4. ���� Session ��ȡ�ʼ��������
			transport = session.getTransport();
			// 5. ʹ�� �����˺� �� ���� �����ʼ�������, ������֤����������� message �еķ���������һ��, ���򱨴�
			//
			// PS_01: �ɰܵ��жϹؼ��ڴ�һ��, ������ӷ�����ʧ��, �����ڿ���̨�����Ӧʧ��ԭ��� log,
			// ��ϸ�鿴ʧ��ԭ��, ��Щ����������᷵�ش������鿴�������͵�����, ���ݸ����Ĵ���
			// ���͵���Ӧ�ʼ��������İ�����վ�ϲ鿴����ʧ��ԭ��
			//
			// PS_02: ����ʧ�ܵ�ԭ��ͨ��Ϊ���¼���, ��ϸ������:
			// (1) ����û�п��� SMTP ����;
			// (2) �����������, ����ĳЩ���俪���˶�������;
			// (3) ���������Ҫ�����Ҫʹ�� SSL ��ȫ����;
			// (4) �������Ƶ��������ԭ��, ���ʼ��������ܾ�����;
			// (5) ������ϼ��㶼ȷ������, ���ʼ���������վ���Ұ�����
			// 452698545
			// PS_03: ��ϸ��log, ���濴log, ����log, ����ԭ����log��˵����
			transport.connect(props.getProperty("mail.smtp.host"), props.getProperty("myEmailAccount"),
					props.getProperty("myEmailPassword"));
			// transport.connect(myEmailAccount, myEmailPassword);
			// 6. �����ʼ�, �������е��ռ���ַ, message.getAllRecipients()
			// ��ȡ�������ڴ����ʼ�����ʱ��ӵ������ռ���,
			// ������, ������
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			MessageBox.post("�ʼ�����ʧ�ܣ������ռ�������:" + receiveMailAccount, "�ʼ�����ʧ��", MessageBox.ERROR);
			return false;
		}
		return true;
	}

	/**
	 * ����һ��ֻ�����ı��ļ��ʼ�
	 *
	 * @param session
	 *            �ͷ����������ĻỰ
	 * @param sendMail
	 *            ����������
	 * @param receiveMail
	 *            �ռ�������
	 * @return
	 * @throws Exception
	 */
	public MimeMessage createMimeMessage(Session session,String type, String sendMail, List<String> receiveMail) throws Exception {
		// 1. ����һ���ʼ�
		MimeMessage message = new MimeMessage(session);

		// 2. From: �����ˣ��ǳ��й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸��ǳƣ�
		message.setFrom(new InternetAddress(sendMail, "��������Դ�������޹�˾", "UTF-8"));

		// 3. To: �ռ��ˣ��������Ӷ���ռ��ˡ����͡����ͣ�
		//message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "XX???", "GB2312"));
        //����ռ���
		StringBuffer buffer = new StringBuffer();
		for(String str :receiveMail){
			buffer.append(str+",");
		}
		message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(buffer.toString()));
		message.setSubject("���ݷ���", "UTF-8");
		StringBuilder sb = new StringBuilder();

		sb.append(getHeadModel(type));
		sb.append(getSuccContent(fileName));
		sb.append(getFailContent(failFiles,type));
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
		sb.append("TC���ݷ���");
		return sb;
	}

	/**
	 *
	 * @return
	 */
	private StringBuilder addDate() {
		Calendar cal = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append(cal.get(Calendar.YEAR) + "��" + (cal.get(Calendar.MONTH) + 1) + "��" + cal.get(Calendar.DATE) + "��");
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
			throw new Exception("û���ʼ�������");
		for (String string : receiver) {
			sb.append(string + "��");
		}
		sb.append("	��");
		return sb;
	}

	public static void main(String[] args) {
		MailToApplicant mailUtil = new MailToApplicant();
		ArrayList<String> receiver = new ArrayList<>();
		receiver.add("John");
		receiver.add("Suphei");
		ArrayList<String> applicant = new ArrayList<>();
		applicant.add("zhit876@163.com");
		applicant.add("87636959@qq.com");
		ArrayList<String> fileName = new ArrayList<>();
		fileName.add("sdas");
		fileName.add("www");
//		Map<String,List<String>> map = new HashMap<>();
//		List<String> list = new ArrayList<>();
//		list.add("mmpa");
//		list.add("shibai a");
//		list.add("sadasd");
//		map.put("�ļ��ϴ���ftpʧ��",list);

		List itemList = new ArrayList();
		itemList.add("aaaaaaaaaaaa");
		itemList.add("ssssssss");

		Map<String,List<String>> itemMap = new HashMap<>();
		List<String> itemL = new ArrayList<>();
		itemL.add("1111111111");
		itemL.add("222222222");
		itemMap.put("�ļ��ϴ���ftpʧ��",itemL);
//		ArrayList<String> files = new ArrayList<>();
//		files.add("sdads");
		mailUtil.setReceiver(receiver);
		mailUtil.setReceiveMailAccount(applicant);
		mailUtil.setFileName(fileName);
//		mailUtil.setFailFiles(map);
//		mailUtil.setItems(itemList);
//		mailUtil.setItemFailFiles(itemMap);
//		mailUtil.setFiles(files);
		try {
			mailUtil.release("111");
		} catch (Exception e) {
			e.printStackTrace();
		}
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

}
