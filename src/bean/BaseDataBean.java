package bean;

/**
 * 基本清单信息
 */
public class BaseDataBean {
	private Supplier supplier;
	private String title;/** 标题 */
	private String processNum;/** 流程编号 */
	private String appDept;/** 申请部门 */
	private String vehCode;/** 车型代码 */
	private String applicant;/** 申请人 */
	private String applicantEmail;/** 申请人邮箱 */
	private String applicantPhone;/** 申请人手机号 */
	private String provideType;/** 发放类型 */
	private String inAccepter;/** 内部发放-接收人 */
	private String inDept;/** 内部发放-接收部门 */
	private String proManagerSugge;/** 项目经理意见 */
	private String proInspectorSugge;/** 项目总监意见 */

	public BaseDataBean() {
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProcessNum() {
		return processNum;
	}

	public void setProcessNum(String processNum) {
		this.processNum = processNum;
	}

	public String getAppDept() {
		return appDept;
	}

	public void setAppDept(String appDept) {
		this.appDept = appDept;
	}

	public String getVehCode() {
		return vehCode;
	}

	public void setVehCode(String vehCode) {
		this.vehCode = vehCode;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public String getApplicantEmail() {
		return applicantEmail;
	}

	public void setApplicantEmail(String applicantEmail) {
		this.applicantEmail = applicantEmail;
	}

	public String getApplicantPhone() {
		return applicantPhone;
	}

	public void setApplicantPhone(String applicantPhone) {
		this.applicantPhone = applicantPhone;
	}

	public String getProvideType() {
		return provideType;
	}

	public void setProvideType(String provideType) {
		this.provideType = provideType;
	}

	public String getInAccepter() {
		return inAccepter;
	}

	public void setInAccepter(String inAccepter) {
		this.inAccepter = inAccepter;
	}

	public String getInDept() {
		return inDept;
	}

	public void setInDept(String inDept) {
		this.inDept = inDept;
	}

	public String getProManagerSugge() {
		return proManagerSugge;
	}

	public void setProManagerSugge(String proManagerSugge) {
		this.proManagerSugge = proManagerSugge;
	}

	public String getProInspectorSugge() {
		return proInspectorSugge;
	}

	public void setProInspectorSugge(String proInspectorSugge) {
		this.proInspectorSugge = proInspectorSugge;
	}
}
