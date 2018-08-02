package sql.dbdo;

import java.util.Date;

/**
 * Created by haozt on 2018/4/9.
 */
public class HzTempItemRecord {
    /**
     *  puid
     */
    private Long id;
    /**
     * 所属流程
     */
    private Object belProcessNum;
    /**
     *零件号
     */
    private String itemId;
    /**
     *零件名称
     */
    private String itemName;
    /**
     *版本号
     */
    private String itemRevision;
    /**
     *Catia数模
     */
    private Integer catiaDigifax;
    /**
     *JT数模
     */
    private Integer jtDigifax;
    /**
     *CGR数模
     */
    private Integer cgrDigifax;
    /**
     *Catia图纸
     */
    private Integer catiaBlueprint;
    /**
     *CAD图纸
     */
    private Integer cadBlueprint;
    /**
     *其他
     */
    private Integer others;

    private Integer changeFlag;

    private Date changeEffectTime;

    public Date getChangeEffectTime() {
        return changeEffectTime;
    }

    public void setChangeEffectTime(Date changeEffectTime) {
        this.changeEffectTime = changeEffectTime;
    }

    public Integer getChangeFlag() {
        return changeFlag;
    }

    public void setChangeFlag(Integer changeFlag) {
        this.changeFlag = changeFlag;
    }

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