package sql.dbdo;

/**
 * Created by haozt on 2018/4/9.
 *  ���ݷ������� ����嵥
 */
public class HzTempItemRecord {
    /**
     *  puid
     */
    private Long id;
    /**
     * ��������
     */
    private Object belProcessNum;
    /**
     *�����
     */
    private String itemId;
    /**
     *�������
     */
    private String itemName;
    /**
     *�汾��
     */
    private String itemRevision;
    /**
     *Catia��ģ
     */
    private Integer catiaDigifax;
    /**
     *JT��ģ
     */
    private Integer jtDigifax;
    /**
     *CGR��ģ
     */
    private Integer cgrDigifax;
    /**
     *Catiaͼֽ
     */
    private Integer catiaBlueprint;
    /**
     *CADͼֽ
     */
    private Integer cadBlueprint;
    /**
     *����
     */
    private Integer others;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getBelProcessNum() {
        return belProcessNum;
    }

    public void setBelProcessNum(Object belProcessNum) {
        this.belProcessNum = belProcessNum;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId == null ? null : itemId.trim();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemRevision() {
        return itemRevision;
    }

    public void setItemRevision(String itemRevision) {
        this.itemRevision = itemRevision;
    }

    public Integer getCatiaDigifax() {
        return catiaDigifax;
    }

    public void setCatiaDigifax(Integer catiaDigifax) {
        this.catiaDigifax = catiaDigifax;
    }

    public Integer getJtDigifax() {
        return jtDigifax;
    }

    public void setJtDigifax(Integer jtDigifax) {
        this.jtDigifax = jtDigifax;
    }

    public Integer getCgrDigifax() {
        return cgrDigifax;
    }

    public void setCgrDigifax(Integer cgrDigifax) {
        this.cgrDigifax = cgrDigifax;
    }

    public Integer getCatiaBlueprint() {
        return catiaBlueprint;
    }

    public void setCatiaBlueprint(Integer catiaBlueprint) {
        this.catiaBlueprint = catiaBlueprint;
    }

    public Integer getCadBlueprint() {
        return cadBlueprint;
    }

    public void setCadBlueprint(Integer cadBlueprint) {
        this.cadBlueprint = cadBlueprint;
    }

    public Integer getOthers() {
        return others;
    }

    public void setOthers(Integer others) {
        this.others = others;
    }
}