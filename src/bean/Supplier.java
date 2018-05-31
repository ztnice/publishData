package bean;

public class Supplier {
	private String outCpnyName;/** 外部发放-单位名称 */
	private String outCpnyCode;/** 外部发放-单位代码 */
	private String outCpnyAccepter;/** 外部发放-接收人 */
	private String outCpnyEmail;/** 外部发放-邮箱地址 */
	
	public Supplier() {
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
		this.outCpnyCode = outCpnyCode;
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
		this.outCpnyEmail = outCpnyEmail;
	}
}
