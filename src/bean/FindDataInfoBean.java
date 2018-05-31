package bean;

/**
 * Created by haozt on 2018/4/12.
 * tc卷路径下的文件
 */
public class FindDataInfoBean {
    /**
     * 卷路径
     */
    private String pwntPathName;
    /**
     * 卷路径下的文件名称
     */
    private String pFileName;
    /**
     * 卷路径下的文件名称路径
     */
    private String psdPathName;
    /**
     * 文件名称，用于上传到ftp服务器
     */
    private String poriginalFileName;

    public String getPwntPathName() {
        return pwntPathName;
    }

    public void setPwntPathName(String pwntPathName) {
        this.pwntPathName = pwntPathName;
    }

    public String getpFileName() {
        return pFileName;
    }

    public void setpFileName(String pFileName) {
        this.pFileName = pFileName;
    }

    public String getPsdPathName() {
        return psdPathName;
    }

    public void setPsdPathName(String psdPathName) {
        this.psdPathName = psdPathName;
    }

    public String getPoriginalFileName() {
        return poriginalFileName;
    }

    public void setPoriginalFileName(String poriginalFileName) {
        this.poriginalFileName = poriginalFileName;
    }
}
