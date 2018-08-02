package sql.dbdo;

import java.util.Date;

public class HzTempDocumentRecord {
    private Long id;

    private Object belProcessNum;

    private String documentId;

    private String documentName;

    private String documentRevision;

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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentRevision() {
        return documentRevision;
    }

    public void setDocumentRevision(String documentRevision) {
        this.documentRevision = documentRevision;
    }
}