package execute;

import bean.*;
import org.apache.log4j.Logger;
import service.PublishDataService;
import service.impl.PublishDataServiceImpl;
import sql.dbdo.HzSupplyRecord;
import sql.dbdo.HzTempDocumentRecord;
import sql.dbdo.HzTempItemRecord;
import sql.dbdo.HzTempMainRecord;
import sql.mybatis.impl.PublishDataDAOImpl;
import sql.mybatis.inter.PublishDataDAO;

import java.util.*;

/**
 * Created by haozt on 2018/5/29
 *
 */
public class PublishData {
    private static Logger logger = Logger.getLogger(PublishData.class);

    private BaseDataBean baseDataBean;
    private PublishDataService publishDataService;

    private PublishDataAnalysis analysis;

    /**
     * 数据发放 全部数据 保留接口 OA端
     * @param baseDataBean
     * @param items
     * @param documents
     * @return
     */
    public Result publishData(BaseDataBean baseDataBean, List<ItemBean> items, List<DocumentBean> documents) {
        return null;
    }

    /**
     * 发生设变时 自动触发流程进行数据发放 TC端
     * @param
     * @return
     */
    public OperateResult publishDataFromTcChange(String[] ddddd ){
        List<SupplySendInfo> supplySendInfos = new ArrayList<>();
        OperateResult operateResult = new OperateResult();
        PublishDataDAO publishDataDAO = new PublishDataDAOImpl();
        try {
            for(String itemInfo :ddddd){
                String itemId = itemInfo.split("/")[0];
                String itemRevision = itemInfo.split("/")[1];
                String itemType = itemInfo.split("/")[2];
                //找出发放的基本信息  零件清单信息  文件清单信息不会涉及设变
                List<HzTempMainRecord> hzTempMainRecords = new ArrayList<>();
//                List<HzTempDocumentRecord> documentRecords = null;
                List<HzTempItemRecord> hzTempItemRecords = null;
                Set<String> processNums = new HashSet<>();
//                if("D".equals(itemType)){//文件
//                    HzTempDocumentRecord documentQuery = new HzTempDocumentRecord();
//                    documentQuery.setDocumentId(itemId);
//                    documentRecords = publishDataDAO.getHzTempDocumentList(documentQuery);
//                    if(documentRecords == null || documentRecords.size() ==0){
//                        logger.error("未找到设变数据发放的文件清单信息！");
//                    }else {
//                        for(HzTempDocumentRecord record :documentRecords){
//                            processNums.add((String)record.getBelProcessNum());
//                        }
//                        String processNum = (String)documentRecords.get(0).getBelProcessNum();
//                        hzTempMainRecord = publishDataDAO.getHzTempMainRecord(processNum);
//                    }
//                }

                if("P".equals(itemType)){//零件
                    HzTempItemRecord itemQuery = new HzTempItemRecord();
                    itemQuery.setItemId(itemId);
                    hzTempItemRecords = publishDataDAO.getHzTempItemRecordList(itemQuery);
                    if(hzTempItemRecords == null || hzTempItemRecords.size() ==0){
                        logger.error("未找到设变数据发放的零件清单信息！");
                    }else {
                        for(HzTempItemRecord record :hzTempItemRecords){
                            processNums.add((String)record.getBelProcessNum());
                        }
                    }
                }

                if(hzTempItemRecords!=null && hzTempItemRecords.size()>0){
                    //查询之前有没有发放过对应的变更数据，有则跳过 无则新增数据到表
                    HzTempItemRecord itemQuery = new HzTempItemRecord();
                    itemQuery.setItemId(itemId);
                    itemQuery.setItemRevision(itemRevision);
                    itemQuery.setChangeFlag(1);
                    List<HzTempItemRecord> records = publishDataDAO.getHzTempItemRecordList(itemQuery);
                    if(records == null || records.size() == 0){
                        List<HzTempItemRecord> itemRecords = new ArrayList<>();
                        for(HzTempItemRecord hzTempItemRecord :hzTempItemRecords){
                            hzTempItemRecord.setItemRevision(itemRevision);
                            hzTempItemRecord.setChangeFlag(1);
                            itemRecords.add(hzTempItemRecord);
                        }
                        publishDataDAO.insertItemBeanList(itemRecords);
                        records = itemRecords;
                    }
                    hzTempItemRecords = records;
                }

//                if(documentRecords!= null && documentRecords.size()>0){
//                    List<HzTempDocumentRecord> documentRecordList = new ArrayList<>();
//                    HzTempDocumentRecord documentQuery = new HzTempDocumentRecord();
//                    documentQuery.setDocumentId(itemId);
//                    documentQuery.setDocumentRevision(itemRevision);
//                    documentQuery.setChangeFlag(1);
//                    List<HzTempDocumentRecord> records = publishDataDAO.getHzTempDocumentList(documentQuery);
//                    if(records == null || records.size() == 0){
//                        for(HzTempDocumentRecord hzTempDocumentRecord :documentRecords){
//                            hzTempDocumentRecord.setDocumentRevision(itemRevision);
//                            hzTempDocumentRecord.setChangeFlag(1);
//                            documentRecordList.add(hzTempDocumentRecord);
//                        }
//                        publishDataDAO.insertDocumentBeanList(documentRecordList);
//                    }
//                }

                for(String s :processNums){
                    HzTempMainRecord tempMainRecord = publishDataDAO.getHzTempMainRecord(s);
                    hzTempMainRecords.add(tempMainRecord);
                }

                if(hzTempItemRecords == null || hzTempMainRecords.size() == 0){
                    operateResult.setErrCode(1001L);
                    operateResult.setErrMsg("未找到变更基本数据信息！");
                    logger.error("未找到变更基本数据信息");
                    return operateResult;
                }

                //发邮件 通知 和上传ftp服务器
                publishDataService = new PublishDataServiceImpl();
                analysis = new PublishDataAnalysis();

                FtpUploadResultBean ftpUploadResultBean = new FtpUploadResultBean();

                FtpUploadResultBean ftpUploadResultBean1 = null;


                Set<SupplySendInfo> supplies = new HashSet<>();

                List<ApplicantSendInfo> applicators = new ArrayList<>();

                HzTempItemRecord itemQuery = new HzTempItemRecord();
                itemQuery.setItemId(itemId);
                itemQuery.setItemRevision(itemRevision);
                itemQuery.setChangeFlag(1);
                hzTempItemRecords = publishDataDAO.getHzTempItemRecordList(itemQuery);
                List<FailBean> failFileNames = new ArrayList<>();
                List<String> successFileNames = new ArrayList<>();
                if(hzTempItemRecords!=null && hzTempItemRecords.size()>0){
                    Map<HzTempMainRecord,List<ItemBean>> map = new HashMap<>();

                    for(HzTempMainRecord record:hzTempMainRecords){
                        List<ItemBean> list = new ArrayList<>();
                        for(HzTempItemRecord hzTempItemRecord:hzTempItemRecords){
                            if(record.getProcessNum().equals(hzTempItemRecord.getBelProcessNum())){
                                ItemBean itemBean = new ItemBean();
                                itemBean.setProcessNum((String)hzTempItemRecord.getBelProcessNum());
                                itemBean.setItem_id(hzTempItemRecord.getItemId());
                                itemBean.setItem_name(hzTempItemRecord.getItemName());
                                itemBean.setItemRevision(hzTempItemRecord.getItemRevision());
                                itemBean.setCAD_blueprint(hzTempItemRecord.getCadBlueprint());
                                itemBean.setCatia_blueprint(hzTempItemRecord.getCatiaBlueprint());
                                itemBean.setCGR_digifax(hzTempItemRecord.getCgrDigifax());
                                itemBean.setJT_digifax(hzTempItemRecord.getJtDigifax());
                                itemBean.setCatia_digifax(hzTempItemRecord.getCatiaDigifax());
                                list.add(itemBean);
                            }
                        }
                        map.put(record,list);

                    }

                    for(Map.Entry<HzTempMainRecord,List<ItemBean>> entry :map.entrySet()){
                        if(entry.getValue()==null ||entry.getValue().size()==0){
                            continue;
                        }
                        analysis = new PublishDataAnalysis();
                        ItemResultBean itemResultBean = analysis.getItemsInfoFromTCAndRecordThem(entry.getValue());
                        //外部发放
                        if("外部发放".equals(entry.getKey().getProvideType())){
                            ftpUploadResultBean1 = analysis.upLoadItemListToFTP(itemResultBean,null,entry.getKey().getOutCpnyCode());


                        }else {
                            ftpUploadResultBean1 = analysis.upLoadItemListToFTP(itemResultBean,entry.getKey().getInDept(),null);

                        }
                        List<String> success = new ArrayList<>();
                        List<FailBean> fail  = new ArrayList<>();
                        if(ftpUploadResultBean1 != null){
                            for(String s :ftpUploadResultBean1.getSuccessList()){
                                success.add(s);
                                successFileNames.add(s);
                            }
                            for(FailBean failBean :ftpUploadResultBean1.getFailList()){
//                                if(!"success".equals(failBean.getFailMsg())){
//                                    continue;
//                                }
                                fail.add(failBean);
                                failFileNames.add(failBean);
                            }
                        }

                        ApplicantSendInfo applicantSendInfo = new ApplicantSendInfo();
                        SupplySendInfo supplySendInfo = new SupplySendInfo();
                        applicantSendInfo.setEmail(entry.getKey().getApplicantEmail());
                        applicantSendInfo.setName(entry.getKey().getApplicant());
                        applicantSendInfo.setFailList(fail);
                        applicantSendInfo.setSuccessList(success);

                        if("内部发放".equals(entry.getKey().getProvideType())){
                            supplySendInfo.setSupplyName(entry.getKey().getInAccepter());
                            supplySendInfo.setSupplyEmail(entry.getKey().getInEmail());
                            supplySendInfo.setSupplyCode(entry.getKey().getInDept());
                            applicantSendInfo.setSupplyCode(entry.getKey().getInDept());
                        }
                        if("外部发放".equals(entry.getKey().getProvideType())){
                            supplySendInfo.setSupplyName(entry.getKey().getOutCpnyAccepter());
                            supplySendInfo.setSupplyEmail(entry.getKey().getOutCpnyEmail());
                            supplySendInfo.setSupplyCode(entry.getKey().getOutCpnyCode());
                            applicantSendInfo.setSupplyCode(entry.getKey().getOutCpnyCode());
                        }
                        applicantSendInfo.setProcessNum(entry.getKey().getProcessNum());
                        supplySendInfo.setApplyName(entry.getKey().getApplicant());
                        supplySendInfo.setFailList(fail);
                        supplySendInfo.setSuccessList(success);
                        applicators.add(applicantSendInfo);
                        supplies.add(supplySendInfo);
                        supplySendInfos.add(analysis.getSupplySendInfo());
                    }

                }



                //文件清单
//                List<DocumentBean> documentBeans = new ArrayList<>();
//                HzTempDocumentRecord documentQuery = new HzTempDocumentRecord();
//                documentQuery.setDocumentId(itemId);
//                documentQuery.setDocumentRevision(itemRevision);
//                documentQuery.setChangeFlag(1);
//                documentRecords = publishDataDAO.getHzTempDocumentList(documentQuery);
//                if(documentRecords != null && documentRecords.size()>0){
//                    for(HzTempDocumentRecord hzTempDocumentRecord:documentRecords){
//                        DocumentBean documentBean = new DocumentBean();
//                        documentBean.setDocument_id(hzTempDocumentRecord.getDocumentId());
//                        documentBean.setDocument_name(hzTempDocumentRecord.getDocumentName());
//                        documentBean.setDocumentRevision(hzTempDocumentRecord.getDocumentRevision());
//                        documentBean.setProcessNum((String)hzTempDocumentRecord.getBelProcessNum());
//                        documentBeans.add(documentBean);
//                    }
//
//                    DocumentResultBean documentResultBean = analysis.getDocumentsInfoFromTCAndRecordThem(documentBeans);
//
//                    //外部发放
//                    if("外部发放".equals(hzTempMainRecord.getProvideType())){
//                        ftpUploadResultBean2 = analysis.upLoadDocumentListToFTP(documentResultBean,null,hzTempMainRecord.getOutCpnyCode());
//                    }else {
//                        ftpUploadResultBean2 = analysis.upLoadDocumentListToFTP(documentResultBean,hzTempMainRecord.getInDept(),null);
//
//                    }
//                }

                ftpUploadResultBean.setFailList(failFileNames);
                ftpUploadResultBean.setSuccessList(successFileNames);
                //申请人
                for(ApplicantSendInfo applicantSendInfo :applicators){
                    EmailBean emailBean1 = new EmailBean();
                    List<String> l1 = new ArrayList<>();
                    List<String> l2 = new ArrayList<>();
                    l1.add(applicantSendInfo.getName());
                    l2.add(applicantSendInfo.getEmail());

                    emailBean1.setApplicators(l1);
                    emailBean1.setApplicatorMails(l2);
                    FtpUploadResultBean f = new FtpUploadResultBean();
                    f.setFailList(applicantSendInfo.getFailList());
                    f.setSuccessList(applicantSendInfo.getSuccessList());
                    for(SupplySendInfo info:supplySendInfos){
                        if(info !=null){
                            if(applicantSendInfo.getSupplyCode().equals(info.getSupplyCode())){
                                emailBean1.setFtpPath(info.getSupplyPath());
                                break;
                            }
                        }
                    }
                    boolean allSuccess = true;
                   for(FailBean failBean :f.getFailList()){
                        if(!"success".equals(failBean.getFailMsg())){
                            allSuccess = false;
                           break;
                        }
                   }
                   if(allSuccess){
                       boolean b = analysis.sendMessage(f,emailBean1,applicantSendInfo.getProcessNum());
                       if(!b){
                           logger.error("邮件发送失败，请核对申请人邮箱！"+emailBean1.getApplicatorMails());
                       }
                       publishDataDAO.updateItemChangeEffectTime(itemId,itemRevision,applicantSendInfo.getProcessNum());
                   }
                }

                //供应商
                for(SupplySendInfo supplySendInfo :supplies){
                    EmailBean emailBean1 = new EmailBean();
                    List<String> l1 = new ArrayList<>();
                    List<String> l2 = new ArrayList<>();
                    List<String> l3 = new ArrayList<>();
                    l1.add(supplySendInfo.getSupplyName());
                    l2.add(supplySendInfo.getSupplyEmail());
                    l3.add(supplySendInfo.getApplyName());
                    emailBean1.setSupplies(l1);
                    emailBean1.setSupplyMails(l2);
                    emailBean1.setApplicators(l3);
                    for(SupplySendInfo info:supplySendInfos){
                        if(info!=null){
                            if(supplySendInfo.getSupplyCode().equals(info.getSupplyCode())){
                                emailBean1.setFtpPath(info.getSupplyPath());
                                break;
                            }
                        }
                    }

                    FtpUploadResultBean f = new FtpUploadResultBean();
                    f.setSuccessList(supplySendInfo.getSuccessList());
                    f.setFailList(supplySendInfo.getFailList());
                    boolean allSuccess = true;
                    for(FailBean failBean :f.getFailList()){
                        if(!"success".equals(failBean.getFailMsg())){
                            allSuccess = false;
                            break;
                        }
                    }
                    if(allSuccess){
                        boolean b = analysis.sendMessage(f,emailBean1,null);
                        if(!b){
                            logger.error("邮件发送失败，请核对收件人邮箱！"+emailBean1.getSupplyMails());
                        }
                    }
                }
                boolean success = true;
                for(FailBean failBean:ftpUploadResultBean.getFailList()){
                    if(!failBean.getFailMsg().equals("success")){
                        logger.error("设变数据发放未全部成功,详细信息请查阅邮件!");
                        success = false;
                        break;
                    }
                }

                if(success){
                    //更新发放时间
                    if("D".equals(itemType)){
//                        publishDataDAO.updateDocumentEffectTime(itemId);
                        logger.error("设变数据发放成功!");
                    }else if("P".equals(itemType)){
                        logger.error("设变数据发放成功!");
                    }else {
                        logger.error("设变数据发放失败!");
                    }
                }
                logger.error("设变数据发放结束！");
                return OperateResult.getSuccessResult();
            }
        }catch (Exception e){
            logger.error("设变数据发放失败！");
            return OperateResult.getFailResult();
        }
        logger.error("数据发放设变参数传递错误！");
        return OperateResult.getFailResult();
    }


    /**
     * 从tc端进行数据发放
     * @param
     * @return
     */
    public OperateResult publishDataFromTc(String[] processNums){
        logger.error("数据发放开始！");
        OperateResult operateResult = new OperateResult();
        PublishDataDAO publishDataDAO = new PublishDataDAOImpl();
        //根据流程编号获取文件清单 零件清单 更新基本信息之发放时间 发邮件通知人和上传服务ftp
        try {
            for(String processNum :processNums){
                //查询发放的基本数据
                HzTempMainRecord baseDataRecord = publishDataDAO.getHzTempMainRecord(processNum);
                if(baseDataRecord == null){
                    operateResult.setErrCode(1001L);
                    operateResult.setErrMsg("基本信息未填写！");
                    logger.error("未找到数据发放基本信息");
                    return operateResult;
                }
                //查询要发放的零部件清单
                HzTempItemRecord record = new HzTempItemRecord();
                record.setBelProcessNum(processNum);

                List<HzTempItemRecord> hzTempItemRecords = publishDataDAO.getHzTempItemRecordList(record);
                if(hzTempItemRecords == null || hzTempItemRecords.size() == 0){
                    logger.error("数据发放，无零件清单发放！");
                }
                //查询要发放的文件清单
                HzTempDocumentRecord documentRecord = new HzTempDocumentRecord();
                documentRecord.setBelProcessNum(processNum);
                List<HzTempDocumentRecord> hzTempDocumentRecords = publishDataDAO.getHzTempDocumentList(documentRecord);
                if(hzTempDocumentRecords == null || hzTempDocumentRecords.size() == 0){
                    logger.error("数据发放，无文件清单发放！");
                }
                //发邮件 通知 和上传ftp服务器
                publishDataService = new PublishDataServiceImpl();
                analysis = new PublishDataAnalysis();
                List<ItemBean> itemBeans = new ArrayList<>();

                FtpUploadResultBean ftpUploadResultBean = new FtpUploadResultBean();
                List<String> successFileNames = new ArrayList<>();

                List<FailBean> failFileNames = new ArrayList<>();
                FtpUploadResultBean ftpUploadResultBean1 = null;

                FtpUploadResultBean ftpUploadResultBean2 = null;

                EmailBean emailBean = new EmailBean();

                List<String> supplies = new ArrayList<>();
                List<String> supplyEmails = new ArrayList<>();
                if(hzTempItemRecords!=null && hzTempItemRecords.size()>0){
                    for(HzTempItemRecord hzTempItemRecord:hzTempItemRecords){
                        ItemBean itemBean = new ItemBean();
                        itemBean.setProcessNum((String)hzTempItemRecord.getBelProcessNum());
                        itemBean.setItem_id(hzTempItemRecord.getItemId());
                        itemBean.setItem_name(hzTempItemRecord.getItemName());
                        itemBean.setItemRevision(hzTempItemRecord.getItemRevision());
                        itemBean.setCAD_blueprint(hzTempItemRecord.getCadBlueprint());
                        itemBean.setCatia_blueprint(hzTempItemRecord.getCatiaBlueprint());
                        itemBean.setCGR_digifax(hzTempItemRecord.getCgrDigifax());
                        itemBean.setJT_digifax(hzTempItemRecord.getJtDigifax());
                        itemBean.setCatia_digifax(hzTempItemRecord.getCatiaDigifax());
                        itemBean.setOthers(hzTempItemRecord.getOthers());
                        itemBeans.add(itemBean);
                    }

                    ItemResultBean itemResultBean = analysis.getItemsInfoFromTCAndRecordThem(itemBeans);

                    //外部发放
                    if("外部发放".equals(baseDataRecord.getProvideType())){
                        ftpUploadResultBean1 = analysis.upLoadItemListToFTP(itemResultBean,null,baseDataRecord.getOutCpnyCode());

                    }else {
                        ftpUploadResultBean1 = analysis.upLoadItemListToFTP(itemResultBean,baseDataRecord.getInDept(),null);

                    }
                }




                //文件清单
                List<DocumentBean> documentBeans = new ArrayList<>();
                if(hzTempDocumentRecords != null && hzTempDocumentRecords.size()>0){
                    for(HzTempDocumentRecord hzTempDocumentRecord:hzTempDocumentRecords){
                        DocumentBean documentBean = new DocumentBean();
                        documentBean.setDocument_id(hzTempDocumentRecord.getDocumentId());
                        documentBean.setDocument_name(hzTempDocumentRecord.getDocumentName());
                        documentBean.setDocumentRevision(hzTempDocumentRecord.getDocumentRevision());
                        documentBean.setProcessNum((String)hzTempDocumentRecord.getBelProcessNum());
                        documentBeans.add(documentBean);
                    }

                    DocumentResultBean documentResultBean = analysis.getDocumentsInfoFromTCAndRecordThem(documentBeans);


                    //外部发放
                    if("外部发放".equals(baseDataRecord.getProvideType())){
                        ftpUploadResultBean2 = analysis.upLoadDocumentListToFTP(documentResultBean,null,baseDataRecord.getOutCpnyCode());
                    }else {
                        ftpUploadResultBean2 = analysis.upLoadDocumentListToFTP(documentResultBean,baseDataRecord.getInDept(),null);

                    }
                }

                if(ftpUploadResultBean1 != null){
                    for(String s :ftpUploadResultBean1.getSuccessList()){
                        successFileNames.add(s);
                    }

                    for(FailBean failBean :ftpUploadResultBean1.getFailList()){
                        failFileNames.add(failBean);
                    }
                }


                if(ftpUploadResultBean2 != null){
                    for(String s :ftpUploadResultBean2.getSuccessList()){
                        successFileNames.add(s);
                    }


                    for(FailBean failBean :ftpUploadResultBean2.getFailList()){
                        failFileNames.add(failBean);
                    }
                }

                ftpUploadResultBean.setFailList(failFileNames);
                ftpUploadResultBean.setSuccessList(successFileNames);
                List<String> applicators = new ArrayList<>();
                applicators.add(baseDataRecord.getApplicant());

                List<String> applicatorEmails = new ArrayList<>();
                applicatorEmails.add(baseDataRecord.getApplicantEmail());

                if("内部发放".equals(baseDataRecord.getProvideType())){
                    supplies.add(baseDataRecord.getInAccepter());
                    supplyEmails.add(baseDataRecord.getInEmail());
                }

                if("外部发放".equals(baseDataRecord.getProvideType())){
                    supplies.add(baseDataRecord.getOutCpnyAccepter());//
                    supplyEmails.add(baseDataRecord.getOutCpnyEmail());
                }
                emailBean.setSupplies(supplies);//
                emailBean.setSupplyMails(supplyEmails);//
                emailBean.setApplicatorMails(applicatorEmails);//申请人地址
                emailBean.setApplicators(applicators);//申请人

                boolean b = analysis.sendMessage(ftpUploadResultBean,emailBean,baseDataRecord.getProcessNum());
                boolean success = true;
                for(FailBean failBean:ftpUploadResultBean.getFailList()){
                    if(!failBean.getFailMsg().equals("success")){
                        logger.error("数据发放失败,详细信息请查阅邮件!");
                        success = false;
                        break;
                    }
                }

                if(b){
                    //更新发放时间
                    if(success){
                        publishDataDAO.updatePublishTime(processNum);
                        logger.error("数据发放成功!");
                    }
                    return OperateResult.getSuccessResult();
                }else {
                    logger.error("邮件发送失败!请核对申请人地址："+emailBean.getApplicatorMails()+"；请核对收件人地址："+emailBean.getSupplyMails());
                    return OperateResult.getFailResult();
                }

            }
        }catch (Exception e){
            logger.error("数据发放失败!");
           return OperateResult.getFailResult();
        }
        logger.error("未接收到数据发放相对应的参数!");
        return OperateResult.getFailResult();

    }

    /**
     * 基本数据
     * @param baseDataBean
     * @return
     */
    public Result publishBaseData(BaseDataBean baseDataBean){
        this.baseDataBean = baseDataBean;
        Result result = new Result();
        publishDataService = new PublishDataServiceImpl();
        int i =publishDataService.insertBaseData(baseDataBean);
        if(i>0){
            result.setErrMsg("基本数据发放成功！");
            result.setErrCode(1001);
            result.setSuccess(true);
        }else{
            result.setSuccess(false);
            result.setErrCode(1002);
            result.setErrMsg("基本数据发放失败！");
        }
        return  result;
    }

    /**
     * 零件清单
     * @param itemBean
     * @return
     */
    public Result publishItemBean(List<ItemBean> itemBean){
        Result result = new Result();
        publishDataService = new PublishDataServiceImpl();
        analysis = new PublishDataAnalysis();
        ItemResultBean itemResultBean = analysis.getItemsInfoAndRecordThem(itemBean);
        FtpUploadResultBean ftpUploadResultBean = analysis.upLoadItemListToFTP(itemResultBean,null,null);
        int i = publishDataService.insertItemBeanList(itemBean);
        if(i>0){
            EmailBean emailBean = new EmailBean();
            List<String> applicators = new ArrayList<>();
            applicators.add(baseDataBean.getApplicant());
            List<String> supplies = new ArrayList<>();
            supplies.add(baseDataBean.getSupplier().getOutCpnyAccepter());
            List<String> applicatorEmails = new ArrayList<>();
            applicatorEmails.add(baseDataBean.getApplicantEmail());
            List<String> supplyEmails = new ArrayList<>();
            supplyEmails.add(baseDataBean.getSupplier().getOutCpnyEmail());

            emailBean.setApplicatorMails(applicatorEmails);//
            emailBean.setSupplyMails(supplyEmails);//
            emailBean.setApplicators(applicators);//
            emailBean.setSupplies(supplies);//

            boolean success = analysis.sendMessage(ftpUploadResultBean,emailBean,null);
            if(!success){
                result.setErrMsg("零件清单发放失败，请核对收件人邮箱地址");
                result.setErrCode(1002);
                result.setSuccess(false);
            }else{
                result.setSuccess(true);
                result.setErrCode(1001);
                result.setErrMsg("零件清单发放成功！请查看邮件");
            }
        }else{
            result.setSuccess(false);
            result.setErrCode(1002);
            result.setErrMsg("零件清单发放失败，网络错误！");
        }


        return result;
    }

    /**
     * 文件清单
     * @param documentBean
     * @return
     */
    public Result publishDocumentBean(List<DocumentBean> documentBean){
        Result result = new Result();
        publishDataService = new PublishDataServiceImpl();
        analysis = new PublishDataAnalysis();
        DocumentResultBean documentResultBean = analysis.getDocumentsInfoAndRecordThem(documentBean);
        FtpUploadResultBean ftpUploadResultBean = analysis.upLoadDocumentListToFTP(documentResultBean,null,null);
        int i = publishDataService.insertDocumentList(documentBean);
        if(i>0){
            EmailBean emailBean = new EmailBean();
            List<String> applicators = new ArrayList<>();
            applicators.add(baseDataBean.getApplicant());
            List<String> supplies = new ArrayList<>();
            supplies.add(baseDataBean.getSupplier().getOutCpnyAccepter());
            List<String> applicatorEmails = new ArrayList<>();
            applicatorEmails.add(baseDataBean.getApplicantEmail());
            List<String> supplyEmails = new ArrayList<>();
            supplyEmails.add(baseDataBean.getSupplier().getOutCpnyEmail());

            emailBean.setApplicatorMails(applicatorEmails);//申请人地址
            emailBean.setSupplyMails(supplyEmails);//供应商地址
            emailBean.setApplicators(applicators);//申请人
            emailBean.setSupplies(supplies);//供应商
            boolean success = analysis.sendMessage(ftpUploadResultBean,emailBean,null);
            if(!success){
                result.setErrMsg("文件清单发放失败，清检查收件人邮箱地址");
                result.setErrCode(1002);
                result.setSuccess(false);
            }else{
                result.setSuccess(true);
                result.setErrCode(1001);
                result.setErrMsg("文件清单发放成功，请查收邮件");
            }
        }else{
            result.setSuccess(false);
            result.setErrCode(1002);
            result.setErrMsg("文件清单发放失败，网络错误");
        }
        return result;
    }

}
