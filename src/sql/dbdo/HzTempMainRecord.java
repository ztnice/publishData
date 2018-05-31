package sql.dbdo;

/**
 * Created by haozt on 2018/4/9.
 * 数据对象 数据发放流程
 */
public class HzTempMainRecord {
    /**
     * 标题
     */
    private String title;
    /**
     * 流程编号
     */
    private String processNum;
    /**
     * 申请部门
     */
    private String appDept;

    /**
     * 车型代码
     */
    private String vehCode;
    /**
     *  申请人
     */
    private String applicant;
    /**
     * 申请人邮箱
     */
    private String applicantEmail;
    /**
     * 申请人电话
     */
    private String applicantPhone;
    /**
     * 发放类型
     */
    private String provideType;
    /**
     *  内部发放-接收人
     */
    private String inAccepter;
    /**
     *  内部发放-接收部门
     */
    private String inDept;
    /**
     *  外部发放-单位名称
     */
    private String outCpnyName;
    /**
     *  外部发放-单位代码
     */
    private String outCpnyCode;
    /**
     *  外部发放-接收人
     */
    private String outCpnyAccepter;
    /**
     *  外部发放-邮箱地址
     */
    private String outCpnyEmail;
    /**
     *  项目经理意见
     */
    private String proManagerSugge;
    /**
     *  项目总监意见
     */
    private String proInspectorSugge;

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
        this.applicantEmail = applicantEmail == null ? null : applicantEmail.trim();
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone == null ? null : applicantPhone.trim();
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

    public String getOutCpnyName() {
        return outCpnyName;
    }

    public void setOutCpnyName(String outCpnyName) {
        this.outCpnyName = outCpnyName;
    }

    public String getOutCpnyCode() {
        return outCpnyCode;
    }

    public void setOutCpnyCode(String outCpnyCode) {
        this.outCpnyCode = outCpnyCode == null ? null : outCpnyCode.trim();
    }

    public String getOutCpnyAccepter() {
        return outCpnyAccepter;
    }

    public void setOutCpnyAccepter(String outCpnyAccepter) {
        this.outCpnyAccepter = outCpnyAccepter;
    }

    public String getOutCpnyEmail() {
        return outCpnyEmail;
    }

    public void setOutCpnyEmail(String outCpnyEmail) {
        this.outCpnyEmail = outCpnyEmail == null ? null : outCpnyEmail.trim();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}