package bean;

import java.util.List;
import java.util.Map;

/**
 * Created by haozt on 2018/4/13.
 * 将数据发放清单上传至ftp结果
 */
public class FtpUploadResultBean {
    //邮件通知 成功零件列表 和失败零件列表 成功文件列表 失败文件列表
    //失败 失败文件名称 和失败原因
    /**
     * 上传ftp成功清单名称
     */
    private List<String> successList;


    /**
     * 上传ftp失败的清单名称
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
