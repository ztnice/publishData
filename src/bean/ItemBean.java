package bean;

/**
 * Item 对象
 */
public class ItemBean {
	/** 零件号 */
	private String item_id;
	/** 零件名称 */
	private String item_name;
	/** 版本号 */
	private String itemRevision;
	/** Catia数模 */
	private Integer Catia_digifax;
	/** JT数模 */
	private Integer JT_digifax;
	/** CGR数模 */
	private Integer CGR_digifax;
	/** Catia图纸 */
	private Integer Catia_blueprint;
	/** CAD图纸 */
	private Integer CAD_blueprint;
	/** 其他 */
	private Integer others;
	/** 发放状态值 0(审批)/ 1(撤销)/ 2(生效)*/
	private Integer state;
	/**流程编号*/
	private String processNum;

	public ItemBean() {
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getItemRevision() {
		return itemRevision;
	}

	public void setItemRevision(String itemRevision) {
		this.itemRevision = itemRevision;
	}

	public Integer getCatia_digifax() {
		return Catia_digifax;
	}

	public void setCatia_digifax(Integer catia_digifax) {
		Catia_digifax = catia_digifax;
	}

	public Integer getJT_digifax() {
		return JT_digifax;
	}

	public void setJT_digifax(Integer jT_digifax) {
		JT_digifax = jT_digifax;
	}

	public Integer getCGR_digifax() {
		return CGR_digifax;
	}

	public void setCGR_digifax(Integer cGR_digifax) {
		CGR_digifax = cGR_digifax;
	}

	public Integer getCatia_blueprint() {
		return Catia_blueprint;
	}

	public void setCatia_blueprint(Integer catia_blueprint) {
		Catia_blueprint = catia_blueprint;
	}

	public Integer getCAD_blueprint() {
		return CAD_blueprint;
	}

	public void setCAD_blueprint(Integer cAD_blueprint) {
		CAD_blueprint = cAD_blueprint;
	}

	public Integer getOthers() {
		return others;
	}

	public void setOthers(Integer others) {
		this.others = others;
	}

	public String getProcessNum() {
		return processNum;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public void setProcessNum(String processNum) {
		this.processNum = processNum;
	}
}
