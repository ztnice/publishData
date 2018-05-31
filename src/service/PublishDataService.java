package service;

import bean.*;

import java.util.List;

/**
 * Created by haozt on 2018/5/30
 */
public interface PublishDataService {
    /**
     *  中间表查询零件清单是否已发放
     * @param itemId
     * @param itemRevision
     * @return
     */
    public boolean itemRepeat(String itemId,String itemRevision,String belProcessNum);

    /**
     * 中间表查询文件清单是否已发放
     * @param documentId
     * @param documentRevision
     * @return
     */
    public boolean documentRepeat(String documentId,String documentRevision,String belProcessNum);

    /**
     * tc表中查询零件清单信息
     * @param itemId
     * @param itemRevision
     * @param type
     * @return
     */
    public List<FindDataInfoBean> getItemInfoInTC(String itemId, String itemRevision, String type);

    /**
     * tc表中查询文件清单信息
     * @param documentId
     * @param documentRevision
     * @return
     */
    public List<FindDataInfoBean> getDocumentInfoInTC(String documentId, String documentRevision);

    int insertBaseData(BaseDataBean baseDataBean);

    int insertItemBeanList(List<ItemBean> itemBeans);

    int  insertDocumentList(List<DocumentBean> documentBeans);

}
