package bean;

import java.util.List;

/**
 * Created by haozt on 2018/4/13.
 *
 */
public class EmailBean {
    /**
     * �ʼ����շ� ��Ӧ��
     */
    private List<String> supplies;
    /**
     * ���������� ������
     */
    private List<String> applicators;

    /**
     * ��Ӧ�������ַ
     */
    private List<String> supplyMails;
    /**
     * �����������ַ
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
