package sql.dbdo;

import java.util.Date;

/**
 * @Author: haozt
 * @Date: 2018/8/31
 * @Description: 供应商ftp服务地址
 */
public class SupplyFtpPath {
    /**
     * 主键
     */
    private long id;

    /**
     * 供应商ftp上传文件路径
     */
    private String ftpPath;

    /**
     * 数据发放时间
     */
    private Date publishTime;

    /**
     * 状态标识位
     */
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFtpPath() {
        return ftpPath;
    }

    public void setFtpPath(String ftpPath) {
        this.ftpPath = ftpPath;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }
}
