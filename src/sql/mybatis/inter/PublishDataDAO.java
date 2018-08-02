package sql.mybatis.inter;

import bean.BaseDataBean;
import bean.DocumentBean;
import bean.ItemBean;
import sql.dbdo.*;

import java.util.List;
import java.util.Map;

/**
 * Created by haozt on 2018/5/30
 */
public interface PublishDataDAO {

    List<HzTempItemRecord> getHzTempItemRecordList(HzTempItemRecord record);


    List<HzTempDocumentRecord> getHzTempDocumentList(HzTempDocumentRecord record);


    List<TCInfodbaRecord> getTCInfodbaRecordListWithType(TCInfodbaRecord record);


    List<TCInfodbaRecord> getTCInfodbaRecordList(TCInfodbaRecord record);

    int insertBaseData(HzTempMainRecord record);

    int insertItemBeanList(List<HzTempItemRecord> itemRecords);

    int insertDocumentBeanList(List<HzTempDocumentRecord> documentRecords);

    /**
     * 更新发布时间
     * @param processNum
     * @return
     */
    int updatePublishTime(String processNum);

    HzTempMainRecord getHzTempMainRecord(String processNum);

    HzSupplyRecord getHzSupplyRecord(Map map);


    int updateItemChangeEffectTime(String itemId);

    int updateDocumentEffectTime(String documentId);
}
