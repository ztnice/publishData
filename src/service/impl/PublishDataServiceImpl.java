package service.impl;

import bean.*;
import org.omg.CORBA.OBJ_ADAPTER;
import service.PublishDataService;
import sql.dbdo.*;
import sql.mybatis.impl.PublishDataDAOImpl;
import sql.mybatis.inter.PublishDataDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haozt on 2018/5/30
 */
public class PublishDataServiceImpl implements PublishDataService {
    private PublishDataDAO publishDataDAO;

    /**
     * @param itemId
     * @param itemRevision
     * @return
     */
    public boolean itemRepeat(String itemId,String itemRevision,String belProcessNum){
        this.publishDataDAO = new PublishDataDAOImpl();
        HzTempItemRecord record = new HzTempItemRecord();
        record.setItemId(itemId);
        record.setItemRevision(itemRevision);
        record.setBelProcessNum(belProcessNum);
        List<HzTempItemRecord> list = publishDataDAO.getHzTempItemRecordList(record);
        if(null == list || list.size()==0){
            return  false;
        }
        return true;
    }

    /**
     * @param documentId
     * @param documentRevision
     * @return
     */
    public boolean documentRepeat(String documentId,String documentRevision,String belProcessNum){
        this.publishDataDAO = new PublishDataDAOImpl();
        HzTempDocumentRecord record = new HzTempDocumentRecord();
        record.setDocumentId(documentId);
        record.setDocumentRevision(documentRevision);
        record.setBelProcessNum(belProcessNum);
        List<HzTempDocumentRecord> list = publishDataDAO.getHzTempDocumentList(record);
        if(null == list || list.size()==0){
            return  false;
        }
        return true;
    }


    /**
     * @param itemId
     * @param itemRevision
     * @param type
     * @return
     */
    public List<FindDataInfoBean> getItemInfoInTC(String itemId, String itemRevision, String type){
        List<FindDataInfoBean> list = new ArrayList<>();
        this.publishDataDAO = new PublishDataDAOImpl();
        TCInfodbaRecord recordQuery = new TCInfodbaRecord();
        recordQuery.setItemId(itemId);
        recordQuery.setItemRevision(itemRevision);
        List<TCInfodbaRecord> recordList =publishDataDAO.getTCInfodbaRecordList(recordQuery);
        if(recordList!=null && recordList.size()>0){
            list =  removeRepeat(recordList);//去重
            List<FindDataInfoBean> dataInfoBeans = new ArrayList<>();
            if(type != null){
                switch (type){
                    case "CATPart,CATProduct":
                        List<String> stringList = new ArrayList<>();
                        stringList.add("CATPart");
                        stringList.add("CATProduct");
                        list.forEach(findDataInfoBean -> {
                            String poriginalFileName = findDataInfoBean.getPoriginalFileName();
                            int i  = poriginalFileName.lastIndexOf(".");
                            if(stringList.contains(poriginalFileName.substring(i+1,poriginalFileName.length()))){
                                dataInfoBeans.add(findDataInfoBean);
                            }
                        });
                        return  dataInfoBeans;
                    case "DirectModel":
                        list.forEach(findDataInfoBean -> {
                            String poriginalFileName = findDataInfoBean.getPoriginalFileName();
                            int i  = poriginalFileName.lastIndexOf(".");
                            if("jt".equals(poriginalFileName.substring(i+1,poriginalFileName.length()))){
                                dataInfoBeans.add(findDataInfoBean);
                            }
                        });
                        return  dataInfoBeans;
                    case "CATCache":
                        list.forEach(findDataInfoBean -> {
                            String poriginalFileName = findDataInfoBean.getPoriginalFileName();
                            int i  = poriginalFileName.lastIndexOf(".");
                            if("cgr".equals(poriginalFileName.substring(i+1,poriginalFileName.length()))){
                                dataInfoBeans.add(findDataInfoBean);
                            }
                        });
                        return  dataInfoBeans;
                    case "CATDrawing":
                        list.forEach(findDataInfoBean -> {
                            String poriginalFileName = findDataInfoBean.getPoriginalFileName();
                            int i  = poriginalFileName.lastIndexOf(".");
                            if("CATDrawing".equals(poriginalFileName.substring(i+1,poriginalFileName.length()))){
                                dataInfoBeans.add(findDataInfoBean);
                            }
                        });
                        return  dataInfoBeans;
                    case "H9_AutoCAD":
                        list.forEach(findDataInfoBean -> {
                            String poriginalFileName = findDataInfoBean.getPoriginalFileName();
                            int i  = poriginalFileName.lastIndexOf(".");
                            if("dwg".equals(poriginalFileName.substring(i+1,poriginalFileName.length()))){
                                dataInfoBeans.add(findDataInfoBean);
                            }
                        });
                        return  dataInfoBeans;
                    default:return list;
                }
            }else {
                return list;
            }

        }
      return null;

    }

    /**
     * @param documentId
     * @param documentRevision
     * @return
     */
    public List<FindDataInfoBean> getDocumentInfoInTC(String documentId, String documentRevision){
        this.publishDataDAO = new PublishDataDAOImpl();
        List<String> list = new ArrayList<>();
        list.add("CATPart");
        list.add("CATProduct");
        list.add("jt");
        list.add("cgr");
        list.add("CATDrawing");
        list.add("dwg");
        TCInfodbaRecord recordQuery = new TCInfodbaRecord();
        recordQuery.setItemId(documentId);
        recordQuery.setItemRevision(documentRevision);
        List<FindDataInfoBean> findDataInfoBeans = new ArrayList<>();
        List<TCInfodbaRecord> recordList = publishDataDAO.getTCInfodbaRecordList(recordQuery);

        if(recordList!= null && recordList.size()>0){
            for(int i=0;i<recordList.size();i++){
                boolean isRepeat = false;
                String poriginalName = recordList.get(i).getPoriginalFileName();
                for(int j=i+1;j<recordList.size();j++){
                    if(poriginalName.equals(recordList.get(j).getPoriginalFileName())){
                        isRepeat = true;
                        break;
                    }
                }
                if(!isRepeat){
//                    if(list.contains(poriginalName.split("\\.")[1])){
//                        continue;
//                    }
                    FindDataInfoBean findDataInfoBean = new FindDataInfoBean();
                    findDataInfoBean.setpFileName(recordList.get(i).getpFileName());
                    findDataInfoBean.setPoriginalFileName(poriginalName);
                    findDataInfoBean.setPsdPathName(recordList.get(i).getPsdPathName());
                    findDataInfoBean.setPwntPathName(recordList.get(i).getPwntPathName());
                    findDataInfoBeans.add(findDataInfoBean);
                }
            }
        }
        if(findDataInfoBeans == null || findDataInfoBeans.size()==0){
            return  null;
        }
        return findDataInfoBeans;
    }

    @Override
    public int insertBaseData(BaseDataBean baseDataBean) {
        this.publishDataDAO = new PublishDataDAOImpl();
        HzTempMainRecord record = new HzTempMainRecord();
        try{
            record.setProvideType(baseDataBean.getProvideType());
            record.setApplicantPhone(baseDataBean.getApplicantPhone());
            record.setApplicant(baseDataBean.getApplicant());
            record.setAppDept(baseDataBean.getAppDept());
            record.setApplicantEmail(baseDataBean.getApplicantEmail());
            record.setOutCpnyCode(baseDataBean.getSupplier().getOutCpnyCode());
            record.setInDept(baseDataBean.getInDept());
            record.setOutCpnyEmail(baseDataBean.getSupplier().getOutCpnyEmail());
            record.setInAccepter(baseDataBean.getInAccepter());
            record.setVehCode(baseDataBean.getVehCode());
            record.setTitle(baseDataBean.getTitle());
            record.setProManagerSugge(baseDataBean.getProManagerSugge());
            record.setProInspectorSugge(baseDataBean.getProInspectorSugge());
            record.setOutCpnyName(baseDataBean.getSupplier().getOutCpnyName());
            record.setOutCpnyAccepter(baseDataBean.getSupplier().getOutCpnyAccepter());
            record.setProcessNum(baseDataBean.getProcessNum());
            return publishDataDAO.insertBaseData(record);
        }catch (Exception e){

        }

        return 0;
    }

    @Override
    public int insertItemBeanList(List<ItemBean> itemBeans) {
        this.publishDataDAO = new PublishDataDAOImpl();
        try{
            List<HzTempItemRecord> records = new ArrayList<>();
            for(ItemBean item: itemBeans){
                HzTempItemRecord itemRecord = new HzTempItemRecord();
                itemRecord.setItemId(item.getItem_id());
                itemRecord.setItemRevision(item.getItemRevision());
                itemRecord.setItemName(item.getItem_name());
                itemRecord.setOthers(item.getOthers());
                itemRecord.setCadBlueprint(item.getCAD_blueprint());
                itemRecord.setCatiaBlueprint(item.getCatia_blueprint());
                itemRecord.setCatiaDigifax(item.getCatia_digifax());
                itemRecord.setCgrDigifax(item.getCGR_digifax());
                itemRecord.setJtDigifax(item.getJT_digifax());
                itemRecord.setBelProcessNum(item.getProcessNum());
                records.add(itemRecord);
            }
            return publishDataDAO.insertItemBeanList(records);

        }catch (Exception e){

        }
        return 0;
    }

    @Override
    public int insertDocumentList(List<DocumentBean> documentBeans) {
        this.publishDataDAO = new PublishDataDAOImpl();
        try {
            List<HzTempDocumentRecord> recordList = new ArrayList<>();
            for(DocumentBean documentBean :documentBeans){
                HzTempDocumentRecord documentRecord = new HzTempDocumentRecord();
                documentRecord.setBelProcessNum(documentBean.getProcessNum());
                documentRecord.setDocumentId(documentBean.getDocument_id());
                documentRecord.setDocumentName(documentBean.getDocument_name());
                documentRecord.setDocumentRevision(documentBean.getDocumentRevision());
                recordList.add(documentRecord);
            }
            return publishDataDAO.insertDocumentBeanList(recordList);
        }catch (Exception e){

        }

        return 0;
    }

    @Override
    public HzSupplyRecord getHzSupplyRecord(String supplyCode) {
        Map<String,Object> map = new HashMap<>();
        map.put("suppliersCode",supplyCode);
        return publishDataDAO.getHzSupplyRecord(map);
    }

    /**
     * @param recordList
     * @return
     */
    public List<FindDataInfoBean> removeRepeat(List<TCInfodbaRecord> recordList){
        List<FindDataInfoBean> list = new ArrayList<>();
        for(int i=0;i<recordList.size();i++){
            boolean isRepeat = false;
            String poriginalName = recordList.get(i).getPoriginalFileName();
            for(int j=i+1;j<recordList.size();j++){
                if(poriginalName.equals(recordList.get(j).getPoriginalFileName())){
                    isRepeat =true;
                    break;
                }
            }
            if(!isRepeat) {
                FindDataInfoBean findItemInfoBean = new FindDataInfoBean();
                findItemInfoBean.setpFileName(recordList.get(i).getpFileName());
                findItemInfoBean.setPoriginalFileName(recordList.get(i).getPoriginalFileName());
                findItemInfoBean.setPsdPathName(recordList.get(i).getPsdPathName());
                findItemInfoBean.setPwntPathName(recordList.get(i).getPwntPathName());
                list.add(findItemInfoBean);
            }
        }
        return list;
    }



}
