package execute;

import bean.*;
import service.PublishDataService;
import service.impl.PublishDataServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haozt on 2018/5/29
 *
 *      * 1.查询对应的零件信息，记录错误信息 用于发邮件通知客户 零件名称+原因
 *      * 2.记录查询到的零件信息  用于上传ftp
 *      * 3.查询对应的文件信息，记录错误信息 用于发送邮件给客户
 *      * 4.记录查询到的文件信息  用于上传ftp
 *      * 5.失败：将失败的信息返回给oa调用者
 *      * 6.成功：将2，4步记录的清单信息上传至ftp服务器
 *      * 7.将所有数据发放信息保存至数据库
 *      * 8.给申请人，供应商发送成功邮件通知
 *
 */
public class PublishData {

    private BaseDataBean baseDataBean;
    private PublishDataService publishDataService;

    private PublishDataAnalysis analysis;
    /**
     * 数据发放整理 作为一个接口
     * @param baseDataBean
     * @param items
     * @param documents
     * @return
     */
    public Result publishData(BaseDataBean baseDataBean, List<ItemBean> items, List<DocumentBean> documents) {
        return null;
    }

    /**
     * 基本信息发放
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
            result.setErrMsg("基本数据发放失败");
        }
        return  result;
    }

    /**
     * 零件清单发放
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

            emailBean.setApplicatorMails(applicatorEmails);//申请人邮箱
            emailBean.setSupplyMails(supplyEmails);//供应商邮箱
            emailBean.setApplicators(applicators);//申请人
            emailBean.setSupplies(supplies);//供应商

            boolean success = analysis.sendMessage(ftpUploadResultBean,emailBean,"零件名称");
            if(!success){
                result.setErrMsg("零件清单发放失败，请核对收件人邮箱地址！");
                result.setErrCode(1002);
                result.setSuccess(false);
            }else{
                result.setSuccess(true);
                result.setErrCode(1001);
                result.setErrMsg("零件清单发放成功，请查收邮件！");
            }
        }else{
            result.setSuccess(false);
            result.setErrCode(1002);
            result.setErrMsg("零件清单发放失败，请检查网络！");
        }


        return result;
    }

    /**
     * 文件清单发放
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

            emailBean.setApplicatorMails(applicatorEmails);//申请人邮箱
            emailBean.setSupplyMails(supplyEmails);//供应商邮箱
            emailBean.setApplicators(applicators);//申请人
            emailBean.setSupplies(supplies);//供应商
            boolean success = analysis.sendMessage(ftpUploadResultBean,emailBean,"文件名称");
            if(!success){
                result.setErrMsg("文件清单发放失败，请核对收件人邮箱地址！");
                result.setErrCode(1002);
                result.setSuccess(false);
            }else{
                result.setSuccess(true);
                result.setErrCode(1001);
                result.setErrMsg("文件清单发放成功，请查收邮件！");
            }
        }else{
            result.setSuccess(false);
            result.setErrCode(1002);
            result.setErrMsg("文件清单发放失败，请检查网络！");
        }
        return result;
    }


}
