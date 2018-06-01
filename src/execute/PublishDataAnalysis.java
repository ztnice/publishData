package execute;

import bean.*;
import service.PublishDataService;
import service.impl.PublishDataServiceImpl;
import util.email.MailToApplicant;
import util.email.MailToSupplier;
import util.ftp.FtpPropertyLoader;
import util.ftp.FtpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by haozt on 2018/05/31
 *
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

    /**
     * ftp上传文件清单
     */
    private List<FtpDocumentUploadBean> documentUploadBeans;

    /**
     * ftp上传零件清单
     */
    private List<FtpItemUploadBean> itemUploadBeans;

    private List<FailBean> failMsg;

    private List<String> successMsg;

    private PublishDataService publishDataService;

    private int itemErrorCount;

    private int documentErrorCount;

    public PublishDataAnalysis(){

    }

    public PublishDataAnalysis(List<FailBean> failMsg,List<String> successMsg){
        this.failMsg = failMsg;
        this.successMsg = successMsg;
    }
    public PublishDataAnalysis(ArrayList<FtpDocumentUploadBean> uploadBeans){
        this.documentUploadBeans = uploadBeans;
    }

    public PublishDataAnalysis(List<FtpItemUploadBean> uploadBeans){
        this.itemUploadBeans = uploadBeans;
    }
    /**
     * 记录item数据 失败邮件通知   成功传FTP
     * @param items
     * @return
     */
    public  ItemResultBean getItemsInfoAndRecordThem(List<ItemBean> items){
        this.publishDataService = new PublishDataServiceImpl();
        this.itemUploadBeans = new ArrayList<>();
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
     * @param documentBeans
     * @return
     */
    public DocumentResultBean getDocumentsInfoAndRecordThem(List<DocumentBean> documentBeans){
        this.documentUploadBeans = new ArrayList<>();
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
                this.documentUploadBeans = ftpDocumentUploadBeans;
            }
            documentResult.setFtpDocumentUploadBeans(ftpDocumentUploadBeans);
        }
        return documentResult;
    }


    /**
     * @param itemResultBean
     * @return
     */
    public FtpUploadResultBean upLoadItemListToFTP(ItemResultBean itemResultBean) {
        FtpUploadResultBean ftpUploadResultBean = new FtpUploadResultBean();
        List<String> itemBeansSuccess = new ArrayList<>();
        List<FailBean> failBeans = new ArrayList<>();
        List<FtpItemUploadBean> beans = itemResultBean.getFtpItemUploadBeans();
        try {
            FtpPropertyLoader loader = new FtpPropertyLoader();
            Properties properties = loader.getProperties();
            if (beans == null || beans.size() == 0) {
                ftpUploadResultBean.setSuccessList(itemBeansSuccess);
                ftpUploadResultBean.setFailList(itemResultBean.getFailBeans());
                return ftpUploadResultBean;
            }

            for (FtpItemUploadBean itemUploadBean : beans) {
                String remoteFilePath = "/hozon/" + itemUploadBean.getItem_name();
                int itemCount = 0;
                List<FindDataInfoBean> list = itemUploadBean.getItemInfoBeanList();
                List<String> itemBeanList = new ArrayList<>();
                itemBeanList.add(itemUploadBean.getItem_name());
                for (FindDataInfoBean itemInfoBean : list) {
                    FailBean failBean = new FailBean();
                    String volumePath = itemInfoBean.getPwntPathName();
                    String psdPathName = itemInfoBean.getPsdPathName();
                    String fileName = itemInfoBean.getPoriginalFileName();
                    String realFile = itemInfoBean.getpFileName();

                    String filePath = volumePath + "\\" + psdPathName + "\\" + realFile;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        failBean.setFailMsg("零件上传FTP失败" + fileName + "不存在");
                        failBean.setName(itemUploadBean.getItem_name());
                        failBeans.add(failBean);
                        itemCount++;
                        continue;
//                        throw new RuntimeException(fileName+"�����ڣ�");
                    }
                    InputStream input = new FileInputStream(new File(filePath));

                    int isSuccess = FtpUtil.uploadMutilFile1(properties, properties.getProperty("FTP_BASEPATH"), remoteFilePath, fileName, input);
                    if (isSuccess != 1) {
                        failBean.setName(itemUploadBean.getItem_name());
                        failBean.setFailMsg(fileName + "零件上传FTP失败，网络错误");
                        itemCount++;
                    }
                }
                if (itemCount == 0) {
                    itemBeansSuccess.add(itemUploadBean.getItem_name());
                }
            }
            ftpUploadResultBean.setSuccessList(itemBeansSuccess);
            ftpUploadResultBean.setFailList(failBeans);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return ftpUploadResultBean;
    }

    /**
     * @param documentResultBean
     * @return
     */
    public FtpUploadResultBean upLoadDocumentListToFTP(DocumentResultBean documentResultBean){
        FtpUploadResultBean ftpUploadResultBean = new FtpUploadResultBean();
        List<String> documentSuccess = new ArrayList<>();
        List<FailBean> documentFailBeans = new ArrayList<>();
        List<FtpDocumentUploadBean> documentUploadBeans = documentResultBean.getFtpDocumentUploadBeans();
        if(documentUploadBeans==null || documentUploadBeans.size()==0){
            ftpUploadResultBean.setFailList(documentResultBean.getFailBeans());
            ftpUploadResultBean.setSuccessList(null);
            return ftpUploadResultBean;
        }
        try{
            FtpPropertyLoader loader = new FtpPropertyLoader();
            Properties properties = loader.getProperties();
            for(FtpDocumentUploadBean documentUploadBean :documentUploadBeans){
                String ftpFilePath = "/hozon/"+documentUploadBean.getDocument_name();
                int documentCount = 0;
                List<FindDataInfoBean> findDataInfoBeans = documentUploadBean.getDocumentInfoBeanList();
                List<String> documentFailedList = new ArrayList<>();
                documentFailedList.add(documentUploadBean.getDocument_name());
                for(FindDataInfoBean findDataInfoBean : findDataInfoBeans){
                    String volumePath = findDataInfoBean.getPwntPathName();
                    String psdPathName = findDataInfoBean.getPsdPathName();
                    String fileName = findDataInfoBean.getPoriginalFileName();
                    String realFile = findDataInfoBean.getpFileName();

                    String filePath = volumePath+"\\"+psdPathName+"\\"+realFile;
                    File file = new File(filePath);
                    if(!file.exists()){
                        FailBean failBean = new FailBean();
                        failBean.setName(documentUploadBean.getDocument_name());
                        failBean.setFailMsg("文件上传FTP失败，文件不存在!"+fileName);
                        documentFailBeans.add(failBean);
                        documentCount++;
                        continue;
                    }
                    InputStream input = new FileInputStream(new File(filePath));
                    int isSuccess = FtpUtil.uploadMutilFile1(properties,properties.getProperty("FTP_BASEPATH"),ftpFilePath, fileName, input);
                    if(isSuccess!=1){
                        FailBean failBean = new FailBean();
                        failBean.setFailMsg(fileName+"上传FTP失败，网络错误！");
                        failBean.setName(documentUploadBean.getDocument_name());
                        documentCount++;
                    }
                }
                if(documentCount==0){
                    documentSuccess.add(documentUploadBean.getDocument_name());
                }
            }
            ftpUploadResultBean.setSuccessList(documentSuccess);
            ftpUploadResultBean.setFailList(documentFailBeans);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ftpUploadResultBean;
    }


    /**
     * @param resultBean
     * @param emailBean
     * @return
     */
    public boolean sendMessage(FtpUploadResultBean resultBean,EmailBean emailBean,String type){

        List<FailBean> failBeans = resultBean.getFailList();
        List<String> success= resultBean.getSuccessList();

        MailToApplicant mailToApplicant = new MailToApplicant();
        mailToApplicant.setFileName(success);
        mailToApplicant.setFailFiles(failBeans);

        mailToApplicant.setReceiver(emailBean.getApplicators());
        mailToApplicant.setReceiveMailAccount(emailBean.getApplicatorMails());


        MailToSupplier mailToSupplier = new MailToSupplier();
        mailToSupplier.setFileNames(success);

        mailToSupplier.setReceiver(emailBean.getSupplies());
        mailToSupplier.setReceiveMailAccount(emailBean.getSupplyMails());
        mailToSupplier.setApplicant(emailBean.getApplicators());
        try{
            boolean mailToApplication = mailToApplicant.release(type);
            if(!mailToApplication){
                throw new Exception("邮件发送失败，请核对收件人地址");
            }
            boolean mailToSupp = mailToSupplier.release(type);
            if(!mailToSupp){
                throw new Exception("邮件发送失败，请核对收件人地址");
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
