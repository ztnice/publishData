package sql.dbdo;

/**
 * Created by haozt on 2018/4/17.
 * tc�еı�
 */
public class TCInfodbaRecord {
    private String puid;
    /**
     * id
     */
    private String itemId;
    /**
     *�汾
     */
    private String itemRevision;
    /**
     *����
     */
    private String itemType;
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

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemRevision() {
        return itemRevision;
    }

    public void setItemRevision(String itemRevision) {
        this.itemRevision = itemRevision;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

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
