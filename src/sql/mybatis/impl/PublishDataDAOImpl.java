package sql.mybatis.impl;

import bean.BaseDataBean;
import bean.DocumentBean;
import bean.ItemBean;
import sql.dbdo.HzTempDocumentRecord;
import sql.dbdo.HzTempItemRecord;
import sql.dbdo.HzTempMainRecord;
import sql.dbdo.TCInfodbaRecord;
import sql.mybatis.BaseDAO;
import sql.mybatis.inter.PublishDataDAO;

import java.util.List;

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
        return super.insert("PublishDataDAOImpl_insertItemBeanList",itemRecords);
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
}
