package execute;

import bean.*;
import org.apache.log4j.Logger;
import service.PublishDataService;
import service.impl.PublishDataServiceImpl;
import sql.dbdo.HzSupplyRecord;
import util.email.MailToApplicant;
import util.email.MailToSupplier;
import util.ftp.FtpPropertyLoader;
import util.ftp.FtpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by haozt on 2018/05/31
 *          OA 端发放  TC 端发放
 *          * 1.查询对应的零件信息，记录错误信息
 *          * 2.记录查询到的零件信息
 *          * 3.查询对应的文件信息，记录错误信息
 *          * 4.记录查询到的文件信息
 *          * 5.失败：将失败的信息返回给oa调用者
 *          * 6.成功：将2，4步记录的清单信息上传至ftp服务器
 *          * 7.将所有数据发放信息保存至数据库
 *          * 8.给申请人，供应商发送成功邮件通知
 *
 */
public class PublishDataAnalysis {

    private static Logger logger = Logger.getLogger(PublishDataAnalysis.class);

    private List<FailBean> failMsg;


    private PublishDataService publishDataService;

    private int itemErrorCount;

    private int itemErrorCountForTC;

    private int documentErrorCount;

    private int documentErrorCountForTC;

    private String path="";
    public static final String FTP_PATH = "ftp://10.1.0.231";
    private Date publishDate = new Date();

    private SupplySendInfo supplySendInfo;

    public PublishDataAnalysis(){

    }

    public   SupplySendInfo getSupplySendInfo(){
        return supplySendInfo;
    }
    public void setSupplySendInfo(SupplySendInfo supplySendInfo){
        this.supplySendInfo = supplySendInfo;
    }
    /**
     * 记录item数据 失败邮件通知   成功传FTP OA 端发放
     * @param items
     * @return
     */
    public  ItemResultBean getItemsInfoAndRecordThem(List<ItemBean> items){
        this.publishDataService = new PublishDataServiceImpl();
        this.failMsg = new ArrayList<>();
        List<FtpItemUploadBean> ftpItemUploadBeanList = new ArrayList<>();
        ItemResultBean resultBean = new ItemResultBean();
        if(null != items && items.size()>0){
            for(ItemBean item:items){
                FailBean failBean = new FailBean();
                boolean isRepeat = publishDataService.itemRepeat(item.getItem_id(),item.getItemRevision(),item.getProcessNum());
                //查询是否已发放
                if(isRepeat){
                    failBean.setName(item.getItem_name());
                    failBean.setFailMsg("零件已发布！零件号"+item.getItem_id()+",版本号"+item.getItemRevision());
                    this.failMsg.add(failBean);
                    this.itemErrorCount++;
                }else{
                    //上传FTP结果集
                    FtpItemUploadBean ftpItemUploadBean = new FtpItemUploadBean();
                    List<TypeBean> types = new ArrayList<>();
                    if(Integer.valueOf(1).equals(item.getCAD_blueprint())){
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("H9_AutoCAD");
                        typeBean.setName("CAD图纸ֽ");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCatia_blueprint())){//catiaͼֽ
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("");
                        typeBean.setName("catia图纸ֽ");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCGR_digifax())){
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("");
                        typeBean.setName("CGR数模");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCatia_digifax())){//catia��ģ
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("");
                        typeBean.setName("Catia数模");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getJT_digifax())){
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("");
                        typeBean.setName("JT数模");
                        types.add(typeBean);
                    }
                    //其他这一情况暂时不考虑
//					if(item.getOthers() ==1){
//						types.add("");
//					}

                    List<FindDataInfoBean> itemInfoBeans = new ArrayList<>();
                    //item
                    if(types == null || types.size()==0){
                        List<FindDataInfoBean> itemInfoBeanList = publishDataService.getItemInfoInTC(item.getItem_id(),item.getItemRevision(),null);
                        if (itemInfoBeanList != null && itemInfoBeanList.size() > 0) {
                            for (FindDataInfoBean findItemInfoBean : itemInfoBeanList) {
                                itemInfoBeans.add(findItemInfoBean);
                            }
                        } else {
                            failBean.setName(item.getItem_name());
                            failBean.setFailMsg(item.getItem_name()+"不存在");
                            this.failMsg.add(failBean);
                            this.itemErrorCount++;
                        }
                    }else{
                        for(TypeBean type:types){
                            List<FindDataInfoBean> itemInfoBeanList = publishDataService.getItemInfoInTC(item.getItem_id(),item.getItemRevision(),type.getNameTC());
                            if (itemInfoBeanList != null && itemInfoBeanList.size() > 0) {
                                for (FindDataInfoBean findItemInfoBean : itemInfoBeanList) {
                                    itemInfoBeans.add(findItemInfoBean);
                                }
                            } else {
                                failBean = new FailBean();
                                failBean.setName(item.getItem_name());
                                failBean.setFailMsg(item.getItem_name()+"下的"+type.getName() + "不存在");
                                this.failMsg.add(failBean);
                                this.itemErrorCount++;
                            }
                        }
                    }
                    ftpItemUploadBean.setItemInfoBeanList(itemInfoBeans);
                    ftpItemUploadBean.setItem_id(item.getItem_id());
                    ftpItemUploadBean.setItem_name(item.getItem_name());
                    ftpItemUploadBean.setItemRevision(item.getItemRevision());
                    ftpItemUploadBeanList.add(ftpItemUploadBean);
                }
            }
            resultBean.setFailBeans(this.failMsg);
            if(this.itemErrorCount == 0){
                resultBean.setFtpItemUploadBeans(ftpItemUploadBeanList);
            }
        }
        return  resultBean;
    }




    /**
     * 记录item数据 失败邮件通知   成功传FTP TC端发放
     * @param items
     * @return
     */
    public  ItemResultBean getItemsInfoFromTCAndRecordThem(List<ItemBean> items){
        this.publishDataService = new PublishDataServiceImpl();
        this.failMsg = new ArrayList<>();
        List<FtpItemUploadBean> ftpItemUploadBeanList = new ArrayList<>();
        ItemResultBean resultBean = new ItemResultBean();
        if(null != items && items.size()>0){
            for(ItemBean item:items){
                FailBean failBean = new FailBean();
                    //上传FTP结果集
                    FtpItemUploadBean ftpItemUploadBean = new FtpItemUploadBean();
                    List<TypeBean> types = new ArrayList<>();
                    if(Integer.valueOf(1).equals(item.getCAD_blueprint())){
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("H9_AutoCAD");
                        typeBean.setName("CAD图纸ֽ");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCatia_blueprint())){//catiaͼֽ
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("CATDrawing");
                        typeBean.setName("catia图纸ֽ");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCGR_digifax())){
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("CATCache");
                        typeBean.setName("CGR数模");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCatia_digifax())){//catia
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("CATPart,CATProduct");
                        typeBean.setName("Catia数模");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getJT_digifax())){
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("DirectModel");
                        typeBean.setName("JT数模");
                        types.add(typeBean);
                    }
                    //其他这一情况暂时不考虑
//					if(item.getOthers() ==1){
//						types.add("");
//					}

                    List<FindDataInfoBean> itemInfoBeans = new ArrayList<>();
                    //item
                     if(types == null || types.size()==0){
                        List<FindDataInfoBean> itemInfoBeanList = publishDataService.getItemInfoInTC(item.getItem_id(),item.getItemRevision(),null);
                        if (itemInfoBeanList != null && itemInfoBeanList.size() > 0) {
                            for (FindDataInfoBean findItemInfoBean : itemInfoBeanList) {
                                itemInfoBeans.add(findItemInfoBean);
                            }
                            failBean.setName(item.getItem_name());
                            failBean.setFailMsg("success");
                            this.failMsg.add(failBean);
                        } else {
                            failBean.setName(item.getItem_name());
                            failBean.setFailMsg(item.getItem_name()+"不存在");
                            logger.error(item.getItem_name()+"不存在");
                            this.failMsg.add(failBean);
                            this.itemErrorCountForTC++;
                        }
                    }else{
                        for(TypeBean type:types){
                            List<FindDataInfoBean> itemInfoBeanList = publishDataService.getItemInfoInTC(item.getItem_id(),item.getItemRevision(),type.getNameTC());
                            if (itemInfoBeanList != null && itemInfoBeanList.size() > 0) {
                                for (FindDataInfoBean findItemInfoBean : itemInfoBeanList) {
                                    failBean = new FailBean();
                                    failBean.setName(item.getItem_name());
                                    failBean.setFailMsg("success");
                                    failBean.setRealName(findItemInfoBean.getPoriginalFileName());
                                    this.failMsg.add(failBean);
                                    itemInfoBeans.add(findItemInfoBean);
                                }

                            } else {
                                failBean = new FailBean();
                                failBean.setName(item.getItem_name());
                                failBean.setFailMsg(item.getItem_name()+"下的"+type.getName() + "不存在");
                                this.failMsg.add(failBean);
                                logger.error(item.getItem_name()+"下的"+type.getName() + "不存在");
                                this.itemErrorCountForTC++;
                            }
                        }
                    }
                    ftpItemUploadBean.setItemInfoBeanList(itemInfoBeans);
                    ftpItemUploadBean.setItem_id(item.getItem_id());
                    ftpItemUploadBean.setItem_name(item.getItem_name());
                    ftpItemUploadBean.setItemRevision(item.getItemRevision());
                    ftpItemUploadBeanList.add(ftpItemUploadBean);
                }
            }

            resultBean.setFailBeans(this.failMsg);
            if(this.itemErrorCountForTC == 0){
                resultBean.setFtpItemUploadBeans(ftpItemUploadBeanList);
            }
        return  resultBean;
        }


    /**
     * OA端数据发放 记录发放的文件清单信息 用于上传ftp文件信息
     * @param documentBeans
     * @return
     */
    public DocumentResultBean getDocumentsInfoAndRecordThem(List<DocumentBean> documentBeans){
        this.failMsg = new ArrayList<>();
        this.publishDataService = new PublishDataServiceImpl();
        List<FtpDocumentUploadBean> ftpDocumentUploadBeans = new ArrayList<>();
        DocumentResultBean documentResult = new DocumentResultBean();
        if(null !=documentBeans && documentBeans.size()>0){
            for(DocumentBean documentBean :documentBeans){
                FtpDocumentUploadBean ftpDocumentUploadBean = new FtpDocumentUploadBean();
                ftpDocumentUploadBean.setDocument_id(documentBean.getDocument_id());
                ftpDocumentUploadBean.setDocument_name(documentBean.getDocument_name());
                ftpDocumentUploadBean.setDocumentRevision(documentBean.getDocumentRevision());
                boolean isRepeat = publishDataService.documentRepeat(documentBean.getDocument_id(),documentBean.getDocumentRevision(),documentBean.getProcessNum());
                if(isRepeat){
                    FailBean failBean = new FailBean();
                    failBean.setName(documentBean.getDocument_name());
                    failBean.setFailMsg("文件已发布！文件号"+documentBean.getDocument_id()+"，版本号"+documentBean.getDocumentRevision());
                    this.failMsg.add(failBean);
                    this.documentErrorCount++;
                }else{
                    List<FindDataInfoBean> infoBeanList = publishDataService.getDocumentInfoInTC(documentBean.getDocument_id(),documentBean.getDocumentRevision());
                    if(infoBeanList!=null && infoBeanList.size()>0){
                        ftpDocumentUploadBean.setDocumentInfoBeanList(infoBeanList);
                        ftpDocumentUploadBeans.add(ftpDocumentUploadBean);
                    }else{
                        FailBean failBean = new FailBean();
                        failBean.setName(documentBean.getDocument_name());
                        failBean.setFailMsg("文件不存在！文件号"+documentBean.getDocument_id()+",版本号"+documentBean.getDocumentRevision());
                       this.failMsg.add(failBean);
                       this.documentErrorCount++;
                    }
                }
            }
            documentResult.setFailBeans(this.failMsg);
            if(this.documentErrorCount == 0){
                documentResult.setFtpDocumentUploadBeans(ftpDocumentUploadBeans);
            }
        }
        return documentResult;
    }

    /**
     * TC端数据发放 记录发放的文件清单信息 用于上传ftp文件信息
     * @param documentBeans
     * @return
     */
    public DocumentResultBean getDocumentsInfoFromTCAndRecordThem(List<DocumentBean> documentBeans){
        this.failMsg = new ArrayList<>();
        this.publishDataService = new PublishDataServiceImpl();
        List<FtpDocumentUploadBean> ftpDocumentUploadBeans = new ArrayList<>();
        DocumentResultBean documentResult = new DocumentResultBean();
        if(null !=documentBeans && documentBeans.size()>0){
            for(DocumentBean documentBean :documentBeans){
                FtpDocumentUploadBean ftpDocumentUploadBean = new FtpDocumentUploadBean();
                ftpDocumentUploadBean.setDocument_id(documentBean.getDocument_id());
                ftpDocumentUploadBean.setDocument_name(documentBean.getDocument_name());
                ftpDocumentUploadBean.setDocumentRevision(documentBean.getDocumentRevision());
                    List<FindDataInfoBean> infoBeanList = publishDataService.getDocumentInfoInTC(documentBean.getDocument_id(),documentBean.getDocumentRevision());
                    FailBean failBean = new FailBean();
                   if(infoBeanList!=null && infoBeanList.size()>0){
                        ftpDocumentUploadBean.setDocumentInfoBeanList(infoBeanList);
                        ftpDocumentUploadBeans.add(ftpDocumentUploadBean);
                        failBean.setName(documentBean.getDocument_name());
                        failBean.setFailMsg("success");
                        this.failMsg.add(failBean);
                   }else{

                        failBean.setName(documentBean.getDocument_name());
                        failBean.setFailMsg("文件不存在！文件号"+documentBean.getDocument_id()+",版本号"+documentBean.getDocumentRevision());
                        this.failMsg.add(failBean);
                        logger.error("文件不存在！文件号"+documentBean.getDocument_id()+",版本号"+documentBean.getDocumentRevision());
                        this.documentErrorCountForTC++;
                    }
                }
            }
            documentResult.setFailBeans(this.failMsg);
            if(this.documentErrorCountForTC == 0){
                documentResult.setFtpDocumentUploadBeans(ftpDocumentUploadBeans);
            }

        return documentResult;
    }



    /**
     * 上传发放零件清单 到ftp服务器
     * @param itemResultBean
     * @return
     */
    public FtpUploadResultBean upLoadItemListToFTP(ItemResultBean itemResultBean,String dept,String supplyCode) {
        FtpUploadResultBean ftpUploadResultBean = new FtpUploadResultBean();
        List<String> itemBeansSuccess = new ArrayList<>();
        Set<FailBean> failBeans = new HashSet<>();
        List<FtpItemUploadBean> beans = itemResultBean.getFtpItemUploadBeans();
        try {
            FtpPropertyLoader loader = new FtpPropertyLoader();
            Properties properties = loader.getProperties();
            if (beans == null || beans.size() == 0) {
                ftpUploadResultBean.setSuccessList(itemBeansSuccess);
                ftpUploadResultBean.setFailList(itemResultBean.getFailBeans());
                this.itemErrorCountForTC++;
                return ftpUploadResultBean;
            }


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

            HzSupplyRecord hzSupplyRecord = null;
            if(supplyCode != null){
                hzSupplyRecord = publishDataService.getHzSupplyRecord(supplyCode);
                if(hzSupplyRecord == null){
                    logger.error("供应商信息不存在！");
                }
            }
            for (FtpItemUploadBean itemUploadBean : beans) {
                 this.supplySendInfo = new SupplySendInfo();
                String ftpFilePath = "";
                String ftpFilePath2="";
                if(supplyCode == null){
                    if(dept == null){
                        ftpFilePath = "/测试文件夹/hozon/"+sdf.format(publishDate)+"/"+itemUploadBean.getItem_name();
                        path = "/测试文件夹/hozon/"+sdf.format(publishDate);
                    }else {
                        supplySendInfo.setSupplyCode(dept);
                        ftpFilePath = "/测试文件夹/hozon/"+dept+"/"+sdf.format(publishDate)+"/"+itemUploadBean.getItem_name();
                        path="/测试文件夹/hozon/"+dept+"/"+sdf.format(publishDate);
                        supplySendInfo.setSupplyPath(path);
                    }
                }else {
                    if(hzSupplyRecord == null){
                        ftpFilePath = "/测试文件夹/suppliers/"+sdf.format(publishDate)+"/"+itemUploadBean.getItem_name();
                        ftpFilePath2 = "/测试文件夹/hozon/suppliers/"+sdf.format(publishDate)+"/"+itemUploadBean.getItem_name();
                        path = "/测试文件夹/suppliers/"+sdf.format(publishDate);
                    }else {
                        supplySendInfo.setSupplyCode(hzSupplyRecord.getSuppliersCode());
                        if(hzSupplyRecord.getOutCpnyFtpPath() == null){
                            hzSupplyRecord.setOutCpnyFtpPath("/suppliers/"+hzSupplyRecord.getSuppliersCode()+"-"+hzSupplyRecord.getSuppliersName());
                        }
                        ftpFilePath = "/测试文件夹/"+hzSupplyRecord.getOutCpnyFtpPath()+"/"+sdf.format(publishDate)+"/"+itemUploadBean.getItem_name();
                        ftpFilePath2 = "/测试文件夹/hozon/"+hzSupplyRecord.getOutCpnyFtpPath()+"/"+sdf.format(publishDate)+"/"+itemUploadBean.getItem_name();
                        path="/测试文件夹/"+hzSupplyRecord.getOutCpnyFtpPath()+"/"+sdf.format(publishDate);
                        supplySendInfo.setSupplyPath(path);

                    }
                }
                int itemCount = 0;
                List<FindDataInfoBean> list = itemUploadBean.getItemInfoBeanList();

                for (FindDataInfoBean itemInfoBean : list) {
                    FailBean failBean = new FailBean();
                    String volumePath = itemInfoBean.getPwntPathName();
                    String psdPathName = itemInfoBean.getPsdPathName();
                    String fileName = itemInfoBean.getPoriginalFileName();
                    String realFile = itemInfoBean.getpFileName();

                    String filePath = volumePath + "\\" + psdPathName + "\\" + realFile;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        failBean.setFailMsg("零件上传FTP失败!" + fileName + "不存在");
                        failBean.setName(itemUploadBean.getItem_name());
                        failBean.setRealName(fileName);
                        failBeans.add(failBean);
                        logger.error("零件上传FTP失败!" + fileName + "不存在");
                        itemCount++;
                        this.itemErrorCountForTC++;
                        continue;
//                        throw new RuntimeException(fileName+"�����ڣ�");
                    }
                    if(this.itemErrorCountForTC == 0){
                        InputStream input = new FileInputStream(new File(filePath));
                        InputStream inputStream = new FileInputStream(new File(filePath));
                        InputStream[] inputStreams = new InputStream[]{input,inputStream};
                        String[] s = new String[]{ftpFilePath,ftpFilePath2};
//                        FtpUtil.uploadMFile(properties, properties.getProperty("FTP_BASEPATH"), ftpFilePath2, fileName, input);
                        int isSuccess = FtpUtil.uploadMFile(properties, properties.getProperty("FTP_BASEPATH"), s, fileName, inputStreams);
                        if(input!=null || inputStream!=null){
                            input.close();
                            inputStream.close();
                        }
                        if (isSuccess != 1) {
                            failBean.setName(itemUploadBean.getItem_name());
                            failBean.setFailMsg(fileName+"上传FTP失败，网络错误");
                            failBean.setRealName(fileName);
                            failBeans.add(failBean);
                            logger.error(fileName+"上传FTP失败，网络错误");
                            itemCount++;
                            this.itemErrorCountForTC++;
                        }else {
                            failBean.setFailMsg("success");
                            failBean.setName(itemUploadBean.getItem_name());
                            failBean.setRealName(fileName);
                            failBeans.add(failBean);
                        }

                    }

                }
                if (itemCount == 0) {
                    itemBeansSuccess.add(itemUploadBean.getItem_name());
                }
            }
            ftpUploadResultBean.setSuccessList(itemBeansSuccess);
            List<FailBean> list = new ArrayList<>(failBeans);
            ftpUploadResultBean.setFailList(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return ftpUploadResultBean;
    }

    /**
     * 上传ftp的文件清单
     * @param documentResultBean
     * @return
     */
    public FtpUploadResultBean upLoadDocumentListToFTP(DocumentResultBean documentResultBean,String dept,String supplyCode){

        FtpUploadResultBean ftpUploadResultBean = new FtpUploadResultBean();
        List<String> documentSuccess = new ArrayList<>();
        Set<FailBean> documentFailBeans = new HashSet<>();
        List<FtpDocumentUploadBean> documentUploadBeans = documentResultBean.getFtpDocumentUploadBeans();
        if(documentUploadBeans==null || documentUploadBeans.size()==0){
            ftpUploadResultBean.setFailList(documentResultBean.getFailBeans());
            ftpUploadResultBean.setSuccessList(null);
            return ftpUploadResultBean;
        }
        try{
            FtpPropertyLoader loader = new FtpPropertyLoader();
            Properties properties = loader.getProperties();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            HzSupplyRecord hzSupplyRecord = null;
            if(supplyCode != null){
                hzSupplyRecord = publishDataService.getHzSupplyRecord(supplyCode);
                if(hzSupplyRecord == null){
                    logger.error("供应商信息不存在！");
                }
            }
            for(FtpDocumentUploadBean documentUploadBean :documentUploadBeans){
                String ftpFilePath = "";
                String ftpFilePath2 = "";
                this.supplySendInfo = new SupplySendInfo();
                if(supplyCode == null){
                    if(dept == null){
                        ftpFilePath = "/测试文件夹/hozon/"+sdf.format(publishDate)+"/"+documentUploadBean.getDocument_name();
                        path = "/测试文件夹/hozon/"+sdf.format(publishDate);
                    }else {
                        ftpFilePath = "/测试文件夹/hozon/"+dept+"/"+sdf.format(publishDate)+"/"+documentUploadBean.getDocument_name();
                        path = "/测试文件夹/hozon/"+dept+"/"+sdf.format(publishDate);
                        supplySendInfo.setSupplyCode(dept);
                        supplySendInfo.setSupplyPath(path);
                    }
                }else {
                    if(hzSupplyRecord == null){
                        ftpFilePath = "/测试文件夹/suppliers/"+sdf.format(publishDate)+"/"+documentUploadBean.getDocument_name();
                        ftpFilePath2 = "/测试文件夹/hozon/suppliers/"+sdf.format(publishDate)+"/"+documentUploadBean.getDocument_name();
                        path="/测试文件夹/suppliers/"+sdf.format(publishDate);
                    }else {
                        if(hzSupplyRecord.getOutCpnyFtpPath() == null){
                            hzSupplyRecord.setOutCpnyFtpPath("/suppliers/"+hzSupplyRecord.getSuppliersCode()+"-"+hzSupplyRecord.getSuppliersName());
                        }
                        ftpFilePath = "/测试文件夹/"+hzSupplyRecord.getOutCpnyFtpPath()+"/"+sdf.format(publishDate)+"/"+documentUploadBean.getDocument_name();
                        ftpFilePath2 = "/测试文件夹/hozon/"+hzSupplyRecord.getOutCpnyFtpPath()+"/"+sdf.format(publishDate)+"/"+documentUploadBean.getDocument_name();
                        path="/测试文件夹/"+hzSupplyRecord.getSuppliersCode()+"/"+sdf.format(publishDate);
                        supplySendInfo.setSupplyCode(hzSupplyRecord.getSuppliersCode());
                        supplySendInfo.setSupplyPath(path);
                    }
                }
                int documentCount = 0;
                List<FindDataInfoBean> findDataInfoBeans = documentUploadBean.getDocumentInfoBeanList();

                for(FindDataInfoBean findDataInfoBean : findDataInfoBeans){
                    FailBean failBean = new FailBean();
                    String volumePath = findDataInfoBean.getPwntPathName();
                    String psdPathName = findDataInfoBean.getPsdPathName();
                    String fileName = findDataInfoBean.getPoriginalFileName();
                    String realFile = findDataInfoBean.getpFileName();

                    String filePath = volumePath+"\\"+psdPathName+"\\"+realFile;
                    File file = new File(filePath);
                    if(!file.exists()){
                        failBean.setName(documentUploadBean.getDocument_name());
                        failBean.setFailMsg("文件上传FTP失败,"+fileName+"不存在!");
                        failBean.setRealName(fileName);
                        documentFailBeans.add(failBean);
                        logger.error(fileName+"上传FTP失败，文件不存在!");
                        documentCount++;
                        continue;
                    }else {
                        failBean.setFailMsg("success");
                        failBean.setName(documentUploadBean.getDocument_name());
                        failBean.setRealName(fileName);
                        documentFailBeans.add(failBean);
                    }
                    if(documentCount == 0 && this.itemErrorCountForTC == 0){
                        InputStream input = new FileInputStream(new File(filePath));
                        InputStream inputStream = new FileInputStream(new File(filePath));
                        String[] s = new String[]{ftpFilePath,ftpFilePath2};
                        InputStream[] inputStreams = new InputStream[]{input,inputStream};
//                        FtpUtil.uploadMFile(properties,properties.getProperty("FTP_BASEPATH"),s, fileName, input);
                        int  isSuccess = FtpUtil.uploadMFile(properties,properties.getProperty("FTP_BASEPATH"),s, fileName, inputStreams);
                        if(input!=null || inputStream!=null){
                            input.close();
                            inputStream.close();
                        }

                        if(isSuccess!=1){
                            failBean.setFailMsg(fileName+"上传FTP失败，网络错误！");
                            failBean.setName(documentUploadBean.getDocument_name());
                            failBean.setRealName(fileName);
                            documentFailBeans.add(failBean);
                            logger.error(fileName+"上传FTP失败，网络错误！");
                            documentCount++;
                        }else {
                            failBean.setFailMsg("success");
                            failBean.setName(documentUploadBean.getDocument_name());
                            failBean.setRealName(fileName);
                            documentFailBeans.add(failBean);
                        }

                    }

                }
                if(documentCount==0){
                    documentSuccess.add(documentUploadBean.getDocument_name());
                }
            }

            ftpUploadResultBean.setSuccessList(documentSuccess);
            List<FailBean> list = new ArrayList<>(documentFailBeans);
            ftpUploadResultBean.setFailList(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ftpUploadResultBean;
    }


    /**
     * 发送邮件通知相关责任人
     * @param resultBean
     * @param emailBean
     * @return
     */
    public boolean sendMessage(FtpUploadResultBean resultBean,EmailBean emailBean,String processNum){

        List<FailBean> failBeans = resultBean.getFailList();
        List<String> success= resultBean.getSuccessList();

        if(emailBean.getApplicatorMails() != null){
            MailToApplicant mailToApplicant = new MailToApplicant();
            mailToApplicant.setFileName(success);
            mailToApplicant.setFailFiles(failBeans);
            mailToApplicant.setProcessNum(processNum);

            mailToApplicant.setReceiver(emailBean.getApplicators());
            mailToApplicant.setReceiveMailAccount(emailBean.getApplicatorMails());
            if(null != emailBean.getFtpPath() && !"".equals(emailBean.getFtpPath())){
                mailToApplicant.setPath(FTP_PATH+emailBean.getFtpPath());
            }
            try {
                boolean mailToApplication = mailToApplicant.release();
                if(!mailToApplication){
                    logger.error("邮件发送失败，请核对收件人地址"+emailBean.getApplicatorMails());
                    throw new Exception("邮件发送失败，请核对收件人地址");
                }
            }catch (Exception e){
                return false;
            }
        }



        if(emailBean.getSupplyMails() != null){
            MailToSupplier mailToSupplier = new MailToSupplier();
            mailToSupplier.setFileNames(success);

            mailToSupplier.setReceiver(emailBean.getSupplies());
            mailToSupplier.setReceiveMailAccount(emailBean.getSupplyMails());
            mailToSupplier.setApplicant(emailBean.getApplicators());
            mailToSupplier.setPath(FTP_PATH);
            try {
                boolean mailToSupp = mailToSupplier.release();
                if(!mailToSupp){
                    logger.error("邮件发送失败，请核对收件人地址"+emailBean.getSupplyMails());
                    throw new Exception("邮件发送失败，请核对收件人地址");
                }
            }catch (Exception e){
                return false;
            }
        }
        return true;
    }

}
