package bean;

import java.util.List;

/**
 * Created by haozt on 2018/4/11.
 * ��Ҫ�ϴ����ļ��嵥
 */
public class FtpDocumentUploadBean {
    /** �ļ��� */
    private String document_id;
    /** �ļ����� */
    private String document_name;
    /** �ļ��� */
    private String documentRevision;
    /**
     * һ���ļ��� �汾������ �����ж���ļ�
     */
    private List<FindDataInfoBean> documentInfoBeanList;

    public List<FindDataInfoBean> getDocumentInfoBeanList() {
        return documentInfoBeanList;
    }

    public void setDocumentInfoBeanList(List<FindDataInfoBean> documentInfoBeanList) {
        this.documentInfoBeanList = documentInfoBeanList;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getDocumentRevision() {
        return documentRevision;
    }

    public void setDocumentRevision(String documentRevision) {
        this.documentRevision = documentRevision;
    }

}
