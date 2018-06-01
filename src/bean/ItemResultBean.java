package bean;

import java.util.List;

/**
 * Created by haozt on 2018/4/11.
 */
public class ItemResultBean {

    private List<FtpItemUploadBean> ftpItemUploadBeans;

    private List<FailBean> failBeans;

    public List<FtpItemUploadBean> getFtpItemUploadBeans() {
        return ftpItemUploadBeans;
    }

    public void setFtpItemUploadBeans(List<FtpItemUploadBean> ftpItemUploadBeans) {
        this.ftpItemUploadBeans = ftpItemUploadBeans;
    }

    public List<FailBean> getFailBeans() {
        return failBeans;
    }

    public void setFailBeans(List<FailBean> failBeans) {
        this.failBeans = failBeans;
    }
}
