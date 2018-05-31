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
 *      * ��������嵥 ��¼������Ϣ ���ڷ����ʼ�֪ͨ ��¼��ѯ������Ϣ �����ϴ���ftp
 *      * �����ļ��嵥 ��¼������Ϣ ���ڷ����ʼ�֪ͨ ��¼��ѯ������Ϣ �����ϴ���ftp
 *      * �ɹ����ļ��嵥��Ҫ��¼ ʧ�ܵ�Ҳ��Ҫ��¼ ���ʼ��� ��д��һ��list��
 *      * ������ݱ��浽���ݿ�
 *      * �������˷����ʼ�֪ͨ ��Ҫ��¼�ʼ����ͳɹ���� �˶������������ַ ��
 */
public class PublishDataAnalysis {

    /**
     * ftp�ϴ��ļ��嵥
     */
    private List<FtpDocumentUploadBean> documentUploadBeans;

    /**
     * ftp�ϴ�������嵥
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
     * ��ȡ��������嵥��Ϣ����¼��ѯ���������Ϣ
     * @param items
     * @return
     */
    public  ItemResultBean getItemsInfoAndRecordThem(List<ItemBean> items){
        //����������¼�ѷ������߲����ڵ�item
        //��������嵥
        this.publishDataService = new PublishDataServiceImpl();
        this.itemUploadBeans = new ArrayList<>();
        this.failMsg = new ArrayList<>();
        List<FtpItemUploadBean> ftpItemUploadBeanList = new ArrayList<>();
        ItemResultBean resultBean = new ItemResultBean();
        if(null != items && items.size()>0){
            for(ItemBean item:items){
                FailBean failBean = new FailBean();
                boolean isRepeat = publishDataService.itemRepeat(item.getItem_id(),item.getItemRevision(),item.getProcessNum());
                //�м�����Ѵ��ڼ�¼����ʾ�ѷ��������������Աά����������·���
                if(isRepeat){
                    failBean.setName(item.getItem_name());
                    failBean.setFailMsg("����ѷ����������"+item.getItem_id()+",�汾�ţ�"+item.getItemRevision());
                    this.failMsg.add(failBean);
                    this.itemErrorCount++;
                }else{
                    //��¼��Ϣ���ϴ���ftp��
                    FtpItemUploadBean ftpItemUploadBean = new FtpItemUploadBean();
                    //item�������������͵Ĵ���
                    List<TypeBean> types = new ArrayList<>();
                    if(Integer.valueOf(1).equals(item.getCAD_blueprint())){
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("H9_AutoCAD");
                        typeBean.setName("CADͼֽ");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCatia_blueprint())){//catiaͼֽ
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("");
                        typeBean.setName("catiaͼֽ");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCGR_digifax())){
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("");
                        typeBean.setName("CGR��ģ");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getCatia_digifax())){//catia��ģ
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("");
                        typeBean.setName("Catia��ģ");
                        types.add(typeBean);
                    }
                    if(Integer.valueOf(1).equals(item.getJT_digifax())){
                        TypeBean typeBean = new TypeBean();
                        typeBean.setNameTC("");
                        typeBean.setName("JT��ģ");
                        types.add(typeBean);
                    }
                    //������һ�����ʱ�Ȳ�����
//					if(item.getOthers() ==1){
//						types.add("");
//					}

                    List<FindDataInfoBean> itemInfoBeans = new ArrayList<>();
                    StringBuffer buffer = new StringBuffer();
                    //item����û�д��� ����ֵ
                    if(types == null || types.size()==0){
                        List<FindDataInfoBean> itemInfoBeanList = publishDataService.getItemInfoInTC(item.getItem_id(),item.getItemRevision(),null);
                        if (itemInfoBeanList != null && itemInfoBeanList.size() > 0) {
                            for (FindDataInfoBean findItemInfoBean : itemInfoBeanList) {
                                //��Ӳ�ѯ������Ϣ�������ϴ�ftp
                                itemInfoBeans.add(findItemInfoBean);
                            }
                        } else {
                            failBean.setName(item.getItem_name());
                            failBean.setFailMsg(item.getItem_name()+"�����ڣ�");
                            this.failMsg.add(failBean);
                            this.itemErrorCount++;
                        }
                    }else{
                        for(TypeBean type:types){
                            //��tc���н��в�ѯ
                            List<FindDataInfoBean> itemInfoBeanList = publishDataService.getItemInfoInTC(item.getItem_id(),item.getItemRevision(),type.getNameTC());
                            if (itemInfoBeanList != null && itemInfoBeanList.size() > 0) {
                                for (FindDataInfoBean findItemInfoBean : itemInfoBeanList) {
                                    //��Ӳ�ѯ������Ϣ�������ϴ�ftp
                                    itemInfoBeans.add(findItemInfoBean);
                                }
                            } else {
                                failBean.setName(item.getItem_name());
                                failBean.setFailMsg(item.getItem_name()+"�µ�"+type.getName() + "��Ϣ������!");
                                this.failMsg.add(failBean);
                                this.itemErrorCount++;
                            }
                        }
                    }
                    //��¼tc�в�ѯ��������嵥��Ϣ�������ϴ���ftp
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
        //ֻ��ȫ������嵥�����ڻ���δ�������Ž��嵥ֵ�ϴ���ftp
        return  resultBean;
    }



    /**
     * ��ȡ���ŵ��ļ��嵥��Ϣ ����¼��ѯ�����ļ���Ϣ
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
                    failBean.setFailMsg("�ļ��ѷ������ļ��ţ�"+documentBean.getDocument_id()+",�汾�ţ�"+documentBean.getDocumentRevision());
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
                        failBean.setFailMsg("�ļ������ڣ��ļ��ţ�"+documentBean.getDocument_id()+",�汾�ţ�"+documentBean.getDocumentRevision());
                        this.documentErrorCount++;
                    }
                }
                documentResult.setFailBeans(this.failMsg);
            }
            if(this.documentErrorCount == 0){
                this.documentUploadBeans = ftpDocumentUploadBeans;
            }
        }
        //ֻ�е����е��ļ��嵥��δ�������߶����ڣ��Ž���Щ�ļ��嵥�ϴ���ftp

        return documentResult;
    }


    /**
     * ������嵥�ϴ�ftp
     * @param itemResultBean
     * @return
     */
    public FtpUploadResultBean upLoadItemListToFTP(ItemResultBean itemResultBean) {
        FtpUploadResultBean ftpUploadResultBean = new FtpUploadResultBean();
        // �ϴ�ftp�ɹ�����嵥
        List<String> itemBeansSuccess = new ArrayList<>();
        // �ϴ�ftpʧ�ܵ��ļ��嵥
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

            //������嵥�ϴ�
            //�ϴ�ftpʧ�ܵ���Ϣ ��Ҫ����ʧ���ʼ�֪ͨ�û�
            for (FtpItemUploadBean itemUploadBean : beans) {
                String remoteFilePath = "/hozon/" + itemUploadBean.getItem_name();
                int itemCount = 0;
                List<FindDataInfoBean> list = itemUploadBean.getItemInfoBeanList();
                List<String> itemBeanList = new ArrayList<>();
                itemBeanList.add(itemUploadBean.getItem_name());
                for (FindDataInfoBean itemInfoBean : list) {
                    //���ؾ�·��
                    FailBean failBean = new FailBean();
                    String volumePath = itemInfoBean.getPwntPathName();
                    String psdPathName = itemInfoBean.getPsdPathName();
                    //�ļ�����
                    String fileName = itemInfoBean.getPoriginalFileName();
                    String realFile = itemInfoBean.getpFileName();

                    String filePath = volumePath + "\\" + psdPathName + "\\" + realFile;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        //���ﲻӦ�����쳣
                        failBean.setFailMsg("�ϴ�ftpʧ�ܣ�" + fileName + "������!");
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
                        failBean.setFailMsg(fileName + "�ϴ���ftpʧ��,�������");
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
     * ���ļ��嵥�ϴ�
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
                    //���ؾ�·��
                    String volumePath = findDataInfoBean.getPwntPathName();
                    String psdPathName = findDataInfoBean.getPsdPathName();
                    //�ļ�����
                    String fileName = findDataInfoBean.getPoriginalFileName();
                    String realFile = findDataInfoBean.getpFileName();

                    String filePath = volumePath+"\\"+psdPathName+"\\"+realFile;
                    File file = new File(filePath);
                    if(!file.exists()){
                        FailBean failBean = new FailBean();
                        failBean.setName(documentUploadBean.getDocument_name());
                        failBean.setFailMsg("�ϴ�ftpʧ�ܣ��ļ�������!"+fileName);
                        documentFailBeans.add(failBean);
                        documentCount++;
                        continue;
                    }
                    InputStream input = new FileInputStream(new File(filePath));
                    int isSuccess = FtpUtil.uploadMutilFile1(properties,properties.getProperty("FTP_BASEPATH"),ftpFilePath, fileName, input);
                    if(isSuccess!=1){
                        FailBean failBean = new FailBean();
                        failBean.setFailMsg(fileName+"�ϴ�ftpʧ��,�������");
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
     * �����ʼ�
     * �ʼ���Ҫ �������� �͹�Ӧ�̷���
     * @param resultBean
     * @param emailBean
     * @return
     */
    public boolean sendMessage(FtpUploadResultBean resultBean,EmailBean emailBean,String type){

        List<FailBean> failBeans = resultBean.getFailList();
        List<String> success= resultBean.getSuccessList();

        //�����嵥 �ʼ����͸�������
        MailToApplicant mailToApplicant = new MailToApplicant();
        mailToApplicant.setFileName(success);
        mailToApplicant.setFailFiles(failBeans);
        //������Ϊ������
        mailToApplicant.setReceiver(emailBean.getApplicators());
        mailToApplicant.setReceiveMailAccount(emailBean.getApplicatorMails());


        // ����嵥���͸���Ӧ��
        MailToSupplier mailToSupplier = new MailToSupplier();
        mailToSupplier.setFileNames(success);
        //������Ϊ��Ӧ��
        mailToSupplier.setReceiver(emailBean.getSupplies());
        mailToSupplier.setReceiveMailAccount(emailBean.getSupplyMails());
        //�ʼ�ĩβ��Ҫ��ʾ��������Ϣ
        mailToSupplier.setApplicant(emailBean.getApplicators());
        try{
            boolean mailToApplication = mailToApplicant.release(type);
            if(!mailToApplication){
                throw new Exception("�ʼ�����ʧ�ܣ������ռ�������");
            }
            boolean mailToSupp = mailToSupplier.release(type);
            if(!mailToSupp){
                throw new Exception("�ʼ�����ʧ�ܣ������ռ�������");
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
