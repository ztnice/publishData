package bean;

import java.util.List;

/**
 * Created by haozt on 2018/4/11.
 * 记录文件清单发放 用于发送邮件通知客户 和上传ftp
 */
public class DocumentResultBean {
    private List<FailBean> failBeans;
    private List<FtpDocumentUploadBean> ftpDocumentUploadBeans;

    public List<FailBean> getFailBeans() {
        return failBeans;
    }

    public void setFailBeans(List<FailBean> failBeans) {
        this.failBeans = failBeans;
    }

    public List<FtpDocumentUploadBean> getFtpDocumentUploadBeans() {
        return ftpDocumentUploadBeans;
    }

    public void setFtpDocumentUploadBeans(List<FtpDocumentUploadBean> ftpDocumentUploadBeans) {
        this.ftpDocumentUploadBeans = ftpDocumentUploadBeans;
    }
}
