package execute;

import bean.*;
import service.PublishDataService;
import service.impl.PublishDataServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haozt on 2018/5/29
 *
 *      * 1.��ѯ��Ӧ�������Ϣ����¼������Ϣ ���ڷ��ʼ�֪ͨ�ͻ� �������+ԭ��
 *      * 2.��¼��ѯ���������Ϣ  �����ϴ�ftp
 *      * 3.��ѯ��Ӧ���ļ���Ϣ����¼������Ϣ ���ڷ����ʼ����ͻ�
 *      * 4.��¼��ѯ�����ļ���Ϣ  �����ϴ�ftp
 *      * 5.ʧ�ܣ���ʧ�ܵ���Ϣ���ظ�oa������
 *      * 6.�ɹ�����2��4����¼���嵥��Ϣ�ϴ���ftp������
 *      * 7.���������ݷ�����Ϣ���������ݿ�
 *      * 8.�������ˣ���Ӧ�̷��ͳɹ��ʼ�֪ͨ
 *
 */
public class PublishData {

    private BaseDataBean baseDataBean;
    private PublishDataService publishDataService;

    private PublishDataAnalysis analysis;
    /**
     * ���ݷ������� ��Ϊһ���ӿ�
     * @param baseDataBean
     * @param items
     * @param documents
     * @return
     */
    public Result publishData(BaseDataBean baseDataBean, List<ItemBean> items, List<DocumentBean> documents) {
        return null;
    }

    /**
     * ������Ϣ����
     * @param baseDataBean
     * @return
     */
    public Result publishBaseData(BaseDataBean baseDataBean){
        this.baseDataBean = baseDataBean;
        Result result = new Result();
        publishDataService = new PublishDataServiceImpl();
        int i =publishDataService.insertBaseData(baseDataBean);
        if(i>0){
            result.setErrMsg("�������ݷ��ųɹ���");
            result.setErrCode(1001);
            result.setSuccess(true);
        }else{
            result.setSuccess(false);
            result.setErrCode(1002);
            result.setErrMsg("�������ݷ���ʧ��");
        }
        return  result;
    }

    /**
     * ����嵥����
     * @param itemBean
     * @return
     */
    public Result publishItemBean(List<ItemBean> itemBean){
        Result result = new Result();
        publishDataService = new PublishDataServiceImpl();
        analysis = new PublishDataAnalysis();
        ItemResultBean itemResultBean = analysis.getItemsInfoAndRecordThem(itemBean);
        FtpUploadResultBean ftpUploadResultBean = analysis.upLoadItemListToFTP(itemResultBean);
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

            emailBean.setApplicatorMails(applicatorEmails);//����������
            emailBean.setSupplyMails(supplyEmails);//��Ӧ������
            emailBean.setApplicators(applicators);//������
            emailBean.setSupplies(supplies);//��Ӧ��

            boolean success = analysis.sendMessage(ftpUploadResultBean,emailBean,"�������");
            if(!success){
                result.setErrMsg("����嵥����ʧ�ܣ���˶��ռ��������ַ��");
                result.setErrCode(1002);
                result.setSuccess(false);
            }else{
                result.setSuccess(true);
                result.setErrCode(1001);
                result.setErrMsg("����嵥���ųɹ���������ʼ���");
            }
        }else{
            result.setSuccess(false);
            result.setErrCode(1002);
            result.setErrMsg("����嵥����ʧ�ܣ��������磡");
        }


        return result;
    }

    /**
     * �ļ��嵥����
     * @param documentBean
     * @return
     */
    public Result publishDocumentBean(List<DocumentBean> documentBean){
        Result result = new Result();
        publishDataService = new PublishDataServiceImpl();
        analysis = new PublishDataAnalysis();
        DocumentResultBean documentResultBean = analysis.getDocumentsInfoAndRecordThem(documentBean);
        FtpUploadResultBean ftpUploadResultBean = analysis.upLoadDocumentListToFTP(documentResultBean);
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

            emailBean.setApplicatorMails(applicatorEmails);//����������
            emailBean.setSupplyMails(supplyEmails);//��Ӧ������
            emailBean.setApplicators(applicators);//������
            emailBean.setSupplies(supplies);//��Ӧ��
            boolean success = analysis.sendMessage(ftpUploadResultBean,emailBean,"�ļ�����");
            if(!success){
                result.setErrMsg("�ļ��嵥����ʧ�ܣ���˶��ռ��������ַ��");
                result.setErrCode(1002);
                result.setSuccess(false);
            }else{
                result.setSuccess(true);
                result.setErrCode(1001);
                result.setErrMsg("�ļ��嵥���ųɹ���������ʼ���");
            }
        }else{
            result.setSuccess(false);
            result.setErrCode(1002);
            result.setErrMsg("�ļ��嵥����ʧ�ܣ��������磡");
        }
        return result;
    }


}
