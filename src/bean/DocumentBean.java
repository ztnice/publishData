package bean;

/**
 * 文件清单属性
 */
public class DocumentBean {
	/** 文件号 */
	private String document_id;
	/** 文件名称 */
	private String document_name;
	/** 文件版本号 */
	private String documentRevision;
	/** 发放状态值 0(审批)/ 1(撤销)/ 2(生效)*/
	private String state;
	/**流程编号*/
	private String processNum;

	public DocumentBean() {
		super();
	}

	public String getDocument_id() {
		return document_id;
	}

	public void setDocument_id(String document_id) {
		this.document_id = document_id;
	}

	public String getDocument_name() {
		return document_name;
	}

	public void setDocument_name(String document_name) {
		this.document_name = document_name;
	}

	public String getDocumentRevision() {
		return documentRevision;
	}

	public void setDocumentRevision(String documentRevision) {
		this.documentRevision = documentRevision;
	}

	public String getProcessNum() {
		return processNum;
	}

	public void setProcessNum(String processNum) {
		this.processNum = processNum;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
