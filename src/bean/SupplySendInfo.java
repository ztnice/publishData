package bean;

import java.util.List;

/**
 * @Author: haozt
 * @Date: 2018/8/2
 * @Description:
 */
public class SupplySendInfo {

    private String supplyName;

    private String supplyEmail;

    private String supplyPath;

    private String applyName;

    private String supplyCode;

    private List<String> successList;



    private List<FailBean> failList;

    public List<String> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<String> successList) {
        this.successList = successList;
    }

    public List<FailBean> getFailList() {
        return failList;
    }

    public void setFailList(List<FailBean> failList) {
        this.failList = failList;
    }

    public String getSupplyCode() {
        return supplyCode;
    }

    public void setSupplyCode(String supplyCode) {
        this.supplyCode = supplyCode;
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public String getSupplyName() {
        return supplyName;
    }

    public void setSupplyName(String supplyName) {
        this.supplyName = supplyName;
    }

    public String getSupplyEmail() {
        return supplyEmail;
    }

    public void setSupplyEmail(String supplyEmail) {
        this.supplyEmail = supplyEmail;
    }

    public String getSupplyPath() {
        return supplyPath;
    }

    public void setSupplyPath(String supplyPath) {
        this.supplyPath = supplyPath;
    }
}
