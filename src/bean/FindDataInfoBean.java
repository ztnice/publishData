package bean;

/**
 * Created by haozt on 2018/4/12.
 * tc��·���µ��ļ�
 */
public class FindDataInfoBean {
    /**
     * ��·��
     */
    private String pwntPathName;
    /**
     * ��·���µ��ļ�����
     */
    private String pFileName;
    /**
     * ��·���µ��ļ�����·��
     */
    private String psdPathName;
    /**
     * �ļ����ƣ������ϴ���ftp������
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
