package bean;

import java.util.List;
import java.util.Map;

/**
 * Created by haozt on 2018/4/13.
 * �����ݷ����嵥�ϴ���ftp���
 */
public class FtpUploadResultBean {
    //�ʼ�֪ͨ �ɹ�����б� ��ʧ������б� �ɹ��ļ��б� ʧ���ļ��б�
    //ʧ�� ʧ���ļ����� ��ʧ��ԭ��
    /**
     * �ϴ�ftp�ɹ��嵥����
     */
    private List<String> successList;


    /**
     * �ϴ�ftpʧ�ܵ��嵥����
     */
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
}
