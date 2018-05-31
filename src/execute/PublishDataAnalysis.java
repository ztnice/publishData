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
 *      * 解析零件清单 记录错误信息 用于发送邮件通知 记录查询到的信息 用于上传至ftp
 *      * 解析文件清单 记录错误信息 用于发送邮件通知 记录查询到的信息 用于上传至ftp
 *      * 成功的文件清单需要记录 失败的也需要记录 发邮件用 都写成一个list吧
 *      * 最后将数据保存到数据库
 *      * 给申请人发送邮件通知 需要记录邮件发送成功与否 核对申请人邮箱地址 等
 */
public class PublishDataAnalysis {

    /**
     * ftp上传文件清单
     */
    private List<FtpDocumentUploadBean> documentUploadBeans;

    /**
     * ftp上传的零件清单
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
     * 获取发放零件清单信息并记录查询到的零件信息
     * @param items
     * @return
     */
    public  ItemResultBean getItemsInfoAndRecordThem(List<ItemBean> items){
        //计数器，记录已发布或者不存在的item
        //解析零件清单
        this.publishDataService = new PublishDataServiceImpl();
        this.itemUploadBeans = new ArrayList<>();
        this.failMsg = new ArrayList<>();
        List<FtpItemUploadBean> ftpItemUploadBeanList = new ArrayList<>();
        ItemResultBean resultBean = new ItemResultBean();
        if(null != items && items.size()>0){
            for(ItemBean item:items){
                FailBean failBean = new FailBean();
                boolean isRepeat = publishDataService.itemRepeat(item.getItem_id(),item.getItemRevision(),item.getProcessNum());
                //中间表中已存在记录，表示已发布该零件，管理员维护则可以重新发放
                if(isRepeat){
                    failBean.setName(item.getItem_name());
                    failBean.setFailMsg("零件已发布：零件号"+item.getItem_id()+",版本号："+item.getItemRevision());
                    this.failMsg.add(failBean);
                    this.itemErrorCount++;
                }else{
                    //记录信息，上传至ftp用
                    FtpItemUploadBean ftpItemUploadBean = new FtpItemUploadBean();
                    //item下面有其他类型的传入
                    List<TypeBean> types = new ArrayList<>();
                    if(Integer.valueOf(1).equals(item.getCAD_blueprint())){
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("H9_AutoCAD");
                        typeBean.setName("CAD图纸");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCatia_blueprint())){//catia图纸
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("");
                        typeBean.setName("catia图纸");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCGR_digifax())){
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("");
                        typeBean.setName("CGR数模");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCatia_digifax())){//catia数模
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
                    //其他这一情况暂时先不考虑
//					if(item.getOthers() ==1){
//						types.add("");
//					}

                    List<FindDataInfoBean> itemInfoBeans = new ArrayList<>();
                    StringBuffer buffer = new StringBuffer();
                    //item下面没有传入 类型值
                    if(types == null || types.size()==0){
                        List<FindDataInfoBean> itemInfoBeanList = publishDataService.getItemInfoInTC(item.getItem_id(),item.getItemRevision(),null);
                        if (itemInfoBeanList != null && itemInfoBeanList.size() > 0) {
                            for (FindDataInfoBean findItemInfoBean : itemInfoBeanList) {
                                //添加查询到的信息，用于上传ftp
                                itemInfoBeans.add(findItemInfoBean);
                            }
                        } else {
                            failBean.setName(item.getItem_name());
                            failBean.setFailMsg(item.getItem_name()+"不存在！");
                            this.failMsg.add(failBean);
                            this.itemErrorCount++;
                        }
                    }else{
                        for(TypeBean type:types){
                            //到tc表中进行查询
                            List<FindDataInfoBean> itemInfoBeanList = publishDataService.getItemInfoInTC(item.getItem_id(),item.getItemRevision(),type.getNameTC());
                            if (itemInfoBeanList != null && itemInfoBeanList.size() > 0) {
                                for (FindDataInfoBean findItemInfoBean : itemInfoBeanList) {
                                    //添加查询到的信息，用于上传ftp
                                    itemInfoBeans.add(findItemInfoBean);
                                }
                            } else {
                                failBean.setName(item.getItem_name());
                                failBean.setFailMsg(item.getItem_name()+"下的"+type.getName() + "信息不存在!");
                                this.failMsg.add(failBean);
                                this.itemErrorCount++;
                            }
                        }
                    }
                    //记录tc中查询到的零件清单信息，用于上传至ftp
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
        //只有全部零件清单都存在或者未发布，才将清单值上传至ftp
        return  resultBean;
    }



    /**
     * 获取发放的文件清单信息 并记录查询到的文件信息
     * @param documentBeans
     * @return
     */
    public DocumentResultBean getDocumentsInfoAndRecordThem(List<DocumentBean> documentBeans){
        this.documentUploadBeans = new ArrayList<>();
        this.failMsg = new ArrayList<>();
        List<FtpDocumentUploadBean> ftpDocumentUploadBeans = new ArrayList<>();
        DocumentResultBean documentResult = new DocumentResultBean();
        if(null !=documentBeans && documentBeans.size()>0){
            for(DocumentBean documentBean :documentBeans){
                FtpDocumentUploadBean ftpDocumentUploadBean = new FtpDocumentUploadBean();
                ftpDocumentUploadBean.setDocument_id(documentBean.getDocument_id());
                ftpDocumentUploadBean.setDocument_name(documentBean.getDocument_name());
                ftpDocumentUploadBean.setDocumentRevision(documentBean.getDocumentRevision());
                DocumentResultBean resultBean = new DocumentResultBean();
                boolean isRepeat = publishDataService.documentRepeat(documentBean.getDocument_id(),documentBean.getDocumentRevision(),documentBean.getProcessNum());
                if(isRepeat){
                    FailBean failBean = new FailBean();
                    failBean.setName(documentBean.getDocument_name());
                    failBean.setFailMsg("文件已发布！文件号："+documentBean.getDocument_id()+",版本号："+documentBean.getDocumentRevision());
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
                        failBean.setFailMsg("文件不存在！文件号："+documentBean.getDocument_id()+",版本号："+documentBean.getDocumentRevision());
                        this.documentErrorCount++;
                    }
                }
                documentResult.setFailBeans(this.failMsg);
            }
            if(this.documentErrorCount == 0){
                this.documentUploadBeans = ftpDocumentUploadBeans;
            }
        }
        //只有当所有的文件清单都未发布或者都存在，才将这些文件清单上传至ftp

        return documentResult;
    }


    /**
     * 将零件清单上传ftp
     * @param itemResultBean
     * @return
     */
    public FtpUploadResultBean upLoadItemListToFTP(ItemResultBean itemResultBean) {
        FtpUploadResultBean ftpUploadResultBean = new FtpUploadResultBean();
        // 上传ftp成功零件清单
        List<String> itemBeansSuccess = new ArrayList<>();
        // 上传ftp失败的文件清单
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

            //把零件清单上传
            //上传ftp失败的信息 需要发送失败邮件通知用户
            for (FtpItemUploadBean itemUploadBean : beans) {
                String remoteFilePath = "/hozon/" + itemUploadBean.getItem_name();
                int itemCount = 0;
                List<FindDataInfoBean> list = itemUploadBean.getItemInfoBeanList();
                List<String> itemBeanList = new ArrayList<>();
                itemBeanList.add(itemUploadBean.getItem_name());
                for (FindDataInfoBean itemInfoBean : list) {
                    //本地卷路径
                    FailBean failBean = new FailBean();
                    String volumePath = itemInfoBean.getPwntPathName();
                    String psdPathName = itemInfoBean.getPsdPathName();
                    //文件名称
                    String fileName = itemInfoBean.getPoriginalFileName();
                    String realFile = itemInfoBean.getpFileName();

                    String filePath = volumePath + "\\" + psdPathName + "\\" + realFile;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        //这里不应该抛异常
                        failBean.setFailMsg("上传ftp失败，" + fileName + "不存在!");
                        failBean.setName(itemUploadBean.getItem_name());
                        failBeans.add(failBean);
                        itemCount++;
                        continue;
//                        throw new RuntimeException(fileName+"不存在！");
                    }
                    InputStream input = new FileInputStream(new File(filePath));

                    int isSuccess = FtpUtil.uploadMutilFile1(properties, properties.getProperty("FTP_BASEPATH"), remoteFilePath, fileName, input);
                    if (isSuccess != 1) {
                        failBean.setName(itemUploadBean.getItem_name());
                        failBean.setFailMsg(fileName + "上传至ftp失败,网络错误！");
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
     * 将文件清单上传
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
                    //本地卷路径
                    String volumePath = findDataInfoBean.getPwntPathName();
                    String psdPathName = findDataInfoBean.getPsdPathName();
                    //文件名称
                    String fileName = findDataInfoBean.getPoriginalFileName();
                    String realFile = findDataInfoBean.getpFileName();

                    String filePath = volumePath+"\\"+psdPathName+"\\"+realFile;
                    File file = new File(filePath);
                    if(!file.exists()){
                        FailBean failBean = new FailBean();
                        failBean.setName(documentUploadBean.getDocument_name());
                        failBean.setFailMsg("上传ftp失败，文件不存在!"+fileName);
                        documentFailBeans.add(failBean);
                        documentCount++;
                        continue;
                    }
                    InputStream input = new FileInputStream(new File(filePath));
                    int isSuccess = FtpUtil.uploadMutilFile1(properties,properties.getProperty("FTP_BASEPATH"),ftpFilePath, fileName, input);
                    if(isSuccess!=1){
                        FailBean failBean = new FailBean();
                        failBean.setFailMsg(fileName+"上传ftp失败,网络错误");
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
     * 发送邮件
     * 邮件需要 给申请人 和供应商发送
     * @param resultBean
     * @param emailBean
     * @return
     */
    public boolean sendMessage(FtpUploadResultBean resultBean,EmailBean emailBean,String type){

        List<FailBean> failBeans = resultBean.getFailList();
        List<String> success= resultBean.getSuccessList();

        //发放清单 邮件发送给申请人
        MailToApplicant mailToApplicant = new MailToApplicant();
        mailToApplicant.setFileName(success);
        mailToApplicant.setFailFiles(failBeans);
        //接收人为申请人
        mailToApplicant.setReceiver(emailBean.getApplicators());
        mailToApplicant.setReceiveMailAccount(emailBean.getApplicatorMails());


        // 零件清单发送给供应商
        MailToSupplier mailToSupplier = new MailToSupplier();
        mailToSupplier.setFileNames(success);
        //接收人为供应商
        mailToSupplier.setReceiver(emailBean.getSupplies());
        mailToSupplier.setReceiveMailAccount(emailBean.getSupplyMails());
        //邮件末尾需要显示申请人信息
        mailToSupplier.setApplicant(emailBean.getApplicators());
        try{
            boolean mailToApplication = mailToApplicant.release(type);
            if(!mailToApplication){
                throw new Exception("邮件发送失败，请检查收件人邮箱");
            }
            boolean mailToSupp = mailToSupplier.release(type);
            if(!mailToSupp){
                throw new Exception("邮件发送失败，请检查收件人邮箱");
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
