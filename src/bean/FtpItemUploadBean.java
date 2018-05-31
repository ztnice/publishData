package bean;

import java.util.List;

/**
 * Created by haozt on 2018/4/11.
 * 将要上传FTP的零件
 */
public class FtpItemUploadBean {
    /** 零件号 */
    private String item_id;
    /** 零件名称 */
    private String item_name;
    /** 版本号 */
    private String itemRevision;
    /**
     * 一个零件 下面对应有 catia数模，jt数模，cad图纸等
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
