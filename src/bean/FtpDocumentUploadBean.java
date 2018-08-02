package bean;

import java.util.List;

/**
 * Created by haozt on 2018/4/11.
 *
 */
public class FtpDocumentUploadBean {
    /** 文件id */
    private String document_id;
    /** 文件名称 */
    private String document_name;
    /** 文件版本 */
    private String documentRevision;
    /**
     * TC表中查询到的数据
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
