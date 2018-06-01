package service;

import bean.*;

import java.util.List;

/**
 * Created by haozt on 2018/5/30
 */
public interface PublishDataService {
    /**
     * @param itemId
     * @param itemRevision
     * @return
     */
    public boolean itemRepeat(String itemId,String itemRevision,String belProcessNum);

    /**
     * @param documentId
     * @param documentRevision
     * @return
     */
    public boolean documentRepeat(String documentId,String documentRevision,String belProcessNum);

    /**
     * @param itemId
     * @param itemRevision
     * @param type
     * @return
     */
    public List<FindDataInfoBean> getItemInfoInTC(String itemId, String itemRevision, String type);

    /**
     * @param documentId
     * @param documentRevision
     * @return
     */
    public List<FindDataInfoBean> getDocumentInfoInTC(String documentId, String documentRevision);

    int insertBaseData(BaseDataBean baseDataBean);

    int insertItemBeanList(List<ItemBean> itemBeans);

    int  insertDocumentList(List<DocumentBean> documentBeans);

}
