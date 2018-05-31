package bean;

import java.util.List;

/**
 * Created by haozt on 2018/4/13.
 * 发送邮件属性
 * 暂时就写这几个属性吧 后期有需求继续加
 * 发件人 这里都是合众 固定的 直接从配置文件中读取
 * 邮件的接收方有 申请人和供应商
 */
public class EmailBean {
    /**
     * 邮件接收方 供应商
     */
    private List<String> supplies;
    /**
     * 发放申请人 申请人
     */
    private List<String> applicators;

    /**
     * 供应商邮箱地址
     */
    private List<String> supplyMails;
    /**
     * 申请人邮箱地址
     */
    private List<String> applicatorMails;

    public List<String> getSupplies() {
        return supplies;
    }

    public void setSupplies(List<String> supplies) {
        this.supplies = supplies;
    }

    public List<String> getApplicators() {
        return applicators;
    }

    public void setApplicators(List<String> applicators) {
        this.applicators = applicators;
    }

    public List<String> getSupplyMails() {
        return supplyMails;
    }

    public void setSupplyMails(List<String> supplyMails) {
        this.supplyMails = supplyMails;
    }

    public List<String> getApplicatorMails() {
        return applicatorMails;
    }

    public void setApplicatorMails(List<String> applicatorMails) {
        this.applicatorMails = applicatorMails;
    }
}
