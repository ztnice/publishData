package bean;

import java.util.List;

/**
 * Created by haozt on 2018/4/11.
 * 将要上传的文件清单
 */
public class FtpDocumentUploadBean {
    /** 文件号 */
    private String document_id;
    /** 文件名称 */
    private String document_name;
    /** 文件号 */
    private String documentRevision;
    /**
     * 一个文件号 版本号下面 可能有多个文件
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
