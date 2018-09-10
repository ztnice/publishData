package sql.mybatis.impl;

import bean.BaseDataBean;
import bean.DocumentBean;
import bean.ItemBean;
import sql.dbdo.*;
import sql.mybatis.BaseDAO;
import sql.mybatis.inter.PublishDataDAO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haozt on 2018/5/30
 */
public class PublishDataDAOImpl extends BaseDAO implements PublishDataDAO {

    @Override
    public List<HzTempItemRecord> getHzTempItemRecordList(HzTempItemRecord record) {
        return super.findForList("PublishDataDAOImpl_getHzTempItemRecordList",record);
    }

    @Override
    public List<HzTempDocumentRecord> getHzTempDocumentList(HzTempDocumentRecord record) {
        return super.findForList("PublishDataDAOImpl_getHzTempDocumentList",record);
    }

    @Override
    public List<TCInfodbaRecord> getTCInfodbaRecordListWithType(TCInfodbaRecord record) {
        return super.findForListToTC("PublishDataDAOImpl_getTCInfodbaRecordListWithType",record);
    }

    @Override
    public List<TCInfodbaRecord> getTCInfodbaRecordList(TCInfodbaRecord record) {
        return super.findForListToTC("PublishDataDAOImpl_getTCInfodbaRecordList",record);
    }

    @Override
    public int insertBaseData(HzTempMainRecord record) {
        return super.insert("PublishDataDAOImpl_insertBaseData",record);

    }

    @Override
    public int insertItemBeanList(List<HzTempItemRecord> itemRecords) {

        int i = super.insert("PublishDataDAOImpl_insertItemBeanList",itemRecords);
        long a =  itemRecords.get(0).getId();
        System.out.println(a);
        return  i ;
    }

    @Override
    public int insertDocumentBeanList(List<HzTempDocumentRecord> documentRecords) {
        return super.insert("PublishDataDAOImpl_insertDocumentBeanList",documentRecords);
    }

    @Override
    public int updatePublishTime(String processNum) {
        return super.update("PublishDataDAOImpl_updatePublishTime",processNum);
    }

    @Override
    public HzTempMainRecord getHzTempMainRecord(String processNum) {
        return (HzTempMainRecord)super.findForObject("PublishDataDAOImpl_getHzTempMainRecord",processNum);
    }

    @Override
    public HzSupplyRecord getHzSupplyRecord(Map map) {
        return (HzSupplyRecord)super.findForObject("PublishDataDAOImpl_getHzSupplyRecord",map);
    }

    @Override
    public int updateHzSupplyRecord (HzSupplyRecord record) {
        return super.update("PublishDataDAOImpl_updateHzSupplyRecord",record);
    }

    @Override
    public int updateItemChangeEffectTime(String itemId,String itemRevision,String processNum) {
        Map<String,Object> map = new HashMap<>();
        map.put("itemId",itemId);
        map.put("itemRevision",itemRevision);
        map.put("processNum",processNum);
       return super.update("PublishDataDAOImpl_updateItemChangeEffectTime",map);
    }

    @Override
    public int insertSupplyFtpPath(SupplyFtpPath ftpPath) {

        return super.insert("PublishDataDAOImpl_insertSupplyFtpPath",ftpPath);
    }

    @Override
    public int deleteFtpPathList(List<SupplyFtpPath> paths) {
        return super.update("PublishDataDAOImpl_deleteFtpPathList",paths);
    }

    @Override
    public List<SupplyFtpPath> findSupplyFtpPathList() {
        return super.findForList("PublishDataDAOImpl_findSupplyFtpPathList",null);
    }

}
