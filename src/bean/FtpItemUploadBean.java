package bean;

import java.util.List;

/**
 * Created by haozt on 2018/4/11.
 * ��Ҫ�ϴ�FTP�����
 */
public class FtpItemUploadBean {
    /** ����� */
    private String item_id;
    /** ������� */
    private String item_name;
    /** �汾�� */
    private String itemRevision;
    /**
     * һ����� �����Ӧ�� catia��ģ��jt��ģ��cadͼֽ��
     */
    private List<FindDataInfoBean> itemInfoBeanList;

    public List<FindDataInfoBean> getItemInfoBeanList() {
        return itemInfoBeanList;
    }

    public void setItemInfoBeanList(List<FindDataInfoBean> itemInfoBeanList) {
        this.itemInfoBeanList = itemInfoBeanList;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItemRevision() {
        return itemRevision;
    }

    public void setItemRevision(String itemRevision) {
        this.itemRevision = itemRevision;
    }
}
