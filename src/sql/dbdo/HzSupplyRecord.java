package sql.dbdo;

import java.util.Date;

/**
 * Created by haozt on 2018/07/30
 */
public class HzSupplyRecord {
    private Long suppliersId;

    private String suppliersName;

    private String suppliersCode;

    private Date createDate;

    private String createUser;

    private String outCpnyAccepter;

    private String outCpnyEmail;

    private String outCpnyFtpPath;

    public Long getSuppliersId() {
        return suppliersId;
    }

    public void setSuppliersId(Long suppliersId) {
        this.suppliersId = suppliersId;
    }

    public String getSuppliersName() {
        return suppliersName;
    }

    public void setSuppliersName(String suppliersName) {
        this.suppliersName = suppliersName;
    }

    public String getSuppliersCode() {
        return suppliersCode;
    }

    public void setSuppliersCode(String suppliersCode) {
        this.suppliersCode = suppliersCode;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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

    public String getOutCpnyFtpPath() {
        return outCpnyFtpPath;
    }

    public void setOutCpnyFtpPath(String outCpnyFtpPath) {
        this.outCpnyFtpPath = outCpnyFtpPath;
    }
}
