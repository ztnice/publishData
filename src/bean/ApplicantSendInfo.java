package bean;

import java.util.List;

/**
 * @Author: haozt
 * @Date: 2018/8/2
 * @Description:
 */
public class ApplicantSendInfo {

    private String name;

    private String email;

    private String processNum;


    private List<String> successList;

    private List<FailBean> failList;

    private String supplyCode;

    public String getSupplyCode() {
        return supplyCode;
    }

    public void setSupplyCode(String supplyCode) {
        this.supplyCode = supplyCode;
    }

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

    public String getProcessNum() {
        return processNum;
    }

    public void setProcessNum(String processNum) {
        this.processNum = processNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
