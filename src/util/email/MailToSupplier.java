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
	/** �����ʼ��� */
	private List<String> receiver;
	/** ���뷢���� */
	private List<String> applicant;

	// �����˵� ���� �� ���루�滻Ϊ�Լ�����������룩
	// PS: ĳЩ���������Ϊ���������䱾������İ�ȫ�ԣ��� SMTP �ͻ��������˶������루�е������Ϊ����Ȩ�롱��,
	// ���ڿ����˶������������, ����������������ʹ������������루��Ȩ�룩��

	/**�ʼ������������ַ*/
	private List<String> receiveMailAccount;
	private boolean debug = true;
	private String path = "ftp://10.0.8.231/root/...";

	private List<String> files;
	//�����嵥
	private List<String> fileNames;
	//�����嵥
	private List<String> itemNames;

    public static  final String ITEM_TYPE = "�������";
    public static  final String FILE_TYPE = "�ļ�����";

	public MailToSupplier() {
		props = loader.getProperties();
		session = Session.getInstance(props);
		session.setDebug(debug); // ����Ϊdebugģʽ, ���Բ鿴��ϸ�ķ��� log
		// 4. ���� Session ��ȡ�ʼ��������
	}

	public boolean release(String type) {
		try {
			message = createMimeMessage(session, type,props.getProperty("myEmailAccount"), receiveMailAccount);

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
			// 7. �ر�����
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
//		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "XX�û�", "UTF-8"));

		StringBuffer buffer = new StringBuffer();
		for(String str :receiveMail){
			buffer.append(str+",");
		}
		message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(buffer.toString()));
		// 4. Subject: �ʼ����⣨�����й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸ı��⣩
		message.setSubject("���ݷ���", "UTF-8");

		// 5. Content: �ʼ����ģ�����ʹ��html��ǩ���������й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸ķ������ݣ�
		// message.setContent("XX�û����, ����ȫ��5��, ��������,<br/>---------\n
		// ��������ٵ�һ�ꡣ����", "text/html;charset=UTF-8");
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
		// 6. ���÷���ʱ��

		message.setSentDate(new Date());

		// 7. ��������
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
	 * ��ӷ�����
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
	 * �������
	 * 
	 * @return
	 */
	private StringBuilder addDate() {
		Calendar cal = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append(cal.get(Calendar.YEAR) + "��" + (cal.get(Calendar.MONTH) + 1) + "��" + cal.get(Calendar.DATE) + "��");
		return sb;
	}

	/** ��ӹ���·�� */
	private StringBuilder addSharePath() {
		StringBuilder sharePath = new StringBuilder();
		sharePath.append(this.getPath());
		return sharePath;
	}

	/***
	 * ��ӹ�Ӧ��
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
		sb.append("	�ã�");
		return sb;
	}

	public static void main(String[] args) {
		MailToSupplier mailUtil = new MailToSupplier();
		ArrayList<String> receiver = new ArrayList<>();
		receiver.add("John");
		receiver.add("Suphei");
		ArrayList<String> applicant = new ArrayList<>();
		applicant.add("����");
		applicant.add("����");
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
