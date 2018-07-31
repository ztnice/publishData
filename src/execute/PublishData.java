package execute;

import bean.*;
import org.apache.log4j.Logger;
import service.PublishDataService;
import service.impl.PublishDataServiceImpl;
import sql.dbdo.HzTempDocumentRecord;
import sql.dbdo.HzTempItemRecord;
import sql.dbdo.HzTempMainRecord;
import sql.mybatis.impl.PublishDataDAOImpl;
import sql.mybatis.inter.PublishDataDAO;

import java.io.FileWriter;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
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
     * 数据发放 全部数据 保留接口
     * @param baseDataBean
     * @param items
     * @param documents
     * @return
     */
    public Result publishData(BaseDataBean baseDataBean, List<ItemBean> items, List<DocumentBean> documents) {
        return null;
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
                        supplies.add(baseDataRecord.getOutCpnyAccepter());//
                        supplyEmails.add(baseDataRecord.getOutCpnyEmail());

                        ftpUploadResultBean2 = analysis.upLoadDocumentListToFTP(documentResultBean,null,baseDataRecord.getOutCpnyCode());
                    }else {
                        ftpUploadResultBean2 = analysis.upLoadDocumentListToFTP(documentResultBean,baseDataRecord.getInDept(),null);

                    }
                }

                for(String s :ftpUploadResultBean1.getSuccessList()){
                    successFileNames.add(s);
                }

                for(String s :ftpUploadResultBean2.getSuccessList()){
                    successFileNames.add(s);
                }

                for(FailBean failBean :ftpUploadResultBean1.getFailList()){
                    failFileNames.add(failBean);
                }
                for(FailBean failBean :ftpUploadResultBean2.getFailList()){
                    failFileNames.add(failBean);
                }

                ftpUploadResultBean.setFailList(failFileNames);
                ftpUploadResultBean.setSuccessList(successFileNames);
                List<String> applicators = new ArrayList<>();
                applicators.add(baseDataRecord.getApplicant());

                List<String> applicatorEmails = new ArrayList<>();
                applicatorEmails.add(baseDataRecord.getApplicantEmail());

                supplies.add(baseDataRecord.getInAccepter());
                supplyEmails.add(baseDataRecord.getInEmail());
                emailBean.setSupplies(supplies);//
                emailBean.setSupplyMails(supplyEmails);//

                emailBean.setApplicatorMails(applicatorEmails);//申请人地址
                emailBean.setApplicators(applicators);//申请人

                boolean b = analysis.sendMessage(ftpUploadResultBean,emailBean,null);
                if(b){
                    logger.error("数据发放成功,详细信息请查阅邮件!");
                    return OperateResult.getSuccessResult();
                }else {
                    logger.error("邮件发送失败!请核对申请人地址："+emailBean.getApplicatorMails()+"；请核对收件人地址："+emailBean.getSupplyMails());
                    return OperateResult.getFailResult();
                }

            }
        }catch (Exception e){
            logger.error(OperateResult.getFailResult());
           return OperateResult.getFailResult();
        }
        logger.error(OperateResult.getSuccessResult());
        return OperateResult.getSuccessResult();

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

            boolean success = analysis.sendMessage(ftpUploadResultBean,emailBean,"零件名称");
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
            boolean success = analysis.sendMessage(ftpUploadResultBean,emailBean,"文件名称");
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
