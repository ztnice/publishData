package execute;

import bean.*;
import sql.dbdo.HzTempDocumentRecord;
import sql.dbdo.HzTempItemRecord;
import sql.dbdo.HzTempMainRecord;
import sql.mybatis.impl.PublishDataDAOImpl;
import sql.mybatis.inter.PublishDataDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haozt on 2018/05/31
 */
public class Test {
    public static void main(String[] args){
//        PublishData data = new PublishData();
//        Supplier supplier = new Supplier();
//        supplier.setOutCpnyCode("123131");
//        supplier.setOutCpnyAccepter("张三");
//        supplier.setOutCpnyEmail("zhit876@163.com");
//        BaseDataBean b  = new BaseDataBean();
//        b.setProcessNum("3413大ccda11");
//        b.setAppDept("mmpa,donot in");
//        b.setApplicant("sadas");
//        b.setApplicantPhone("12324234");
//        b.setProvideType("das");
//        b.setAppDept("sada");
//        b.setSupplier(supplier);
//        b.setApplicantEmail("zhit876@163.com");
//
//        List<ItemBean> list = new ArrayList<>();
//        ItemBean bean = new ItemBean();
//        bean.setCAD_blueprint(1);
//        bean.setOthers(1);
//        bean.setCatia_blueprint(1);
//        bean.setJT_digifax(0);
//        bean.setCatia_digifax(1);
//        bean.setCGR_digifax(1);
//        bean.setItem_name("两件测试 ");
//        bean.setItem_id("005621");
//        bean.setItemRevision("A");
//        bean.setProcessNum(b.getProcessNum());
//        list.add(bean);
//        List<DocumentBean> beans = new ArrayList<>();
//        DocumentBean documentBean = new DocumentBean();
//        documentBean.setDocument_id("005621");
//        documentBean.setDocument_name("文件测试111");
//        documentBean.setDocumentRevision("A");
//        documentBean.setProcessNum(b.getProcessNum());
//        beans.add(documentBean);
//
//        DocumentBean documentBean1 = new DocumentBean();
//        documentBean1.setDocument_id("005621");
//        documentBean1.setDocument_name("文件测试2222");
//        documentBean1.setDocumentRevision("B");
//        documentBean1.setProcessNum(b.getProcessNum());
//        beans.add(documentBean1);
//        data.publishBaseData(b);
//       data.publishDocumentBean(beans);
//        long a = System.currentTimeMillis();
        PublishDataDAO publishDataDAO = new PublishDataDAOImpl();
        HzTempMainRecord hzTempMainRecord = new HzTempMainRecord();
        hzTempMainRecord.setAppDept("111");
        hzTempMainRecord.setApplicant("222");
        hzTempMainRecord.setApplicantEmail("222");
        hzTempMainRecord.setInAccepter("222");
        hzTempMainRecord.setInDept("222");
        hzTempMainRecord.setApplicantPhone("222");
        hzTempMainRecord.setApplicantEmail("222");
        hzTempMainRecord.setInAccepter("222");
        hzTempMainRecord.setOutCpnyAccepter("222");
        hzTempMainRecord.setOutCpnyCode("222");
        hzTempMainRecord.setOutCpnyEmail("222");
        hzTempMainRecord.setOutCpnyName("222");
        hzTempMainRecord.setProcessNum("");
        hzTempMainRecord.setProInspectorSugge("222");
        hzTempMainRecord.setProManagerSugge("222");
        hzTempMainRecord.setProvideType("222");
        hzTempMainRecord.setTitle("222");
        hzTempMainRecord.setVehCode("222");
        int i =publishDataDAO.insertBaseData(hzTempMainRecord);
//
//        List<HzTempItemRecord> records = new ArrayList<>();
//        for(int j = 0;j<100;j++){
//            HzTempItemRecord hzTempItemRecord = new HzTempItemRecord();
//            hzTempItemRecord.setItemId("006521");
//            hzTempItemRecord.setItemRevision("A");
//            hzTempItemRecord.setCadBlueprint(1);
//            hzTempItemRecord.setCatiaBlueprint(1);
//            hzTempItemRecord.setCatiaDigifax(1);
//            hzTempItemRecord.setCgrDigifax(1);
//            hzTempItemRecord.setJtDigifax(1);
//            hzTempItemRecord.setOthers(1);
//            hzTempItemRecord.setBelProcessNum("222");
//            hzTempItemRecord.setItemName("222");
//            records.add(hzTempItemRecord);
//        }
//        int i = publishDataDAO.insertItemBeanList(records);
//        System.out.println(i);
//
//        List<HzTempDocumentRecord> documentRecords = new ArrayList<>();
//        for(int k =0;k<100;k++){
//            HzTempDocumentRecord hzTempDocumentRecord = new HzTempDocumentRecord();
//            hzTempDocumentRecord.setDocumentId("006521");
//            hzTempDocumentRecord.setDocumentRevision("A");
//            hzTempDocumentRecord.setDocumentName("哈哈哈哈");
//            hzTempDocumentRecord.setBelProcessNum("222");
//            documentRecords.add(hzTempDocumentRecord);
//        }
//        int m  = publishDataDAO.insertDocumentBeanList(documentRecords);
//        System.out.println(m);
//        long b = System.currentTimeMillis();
//        System.out.println((b-a)+"ms");
//        Result result = data.publishItemBean(list);
//        System.out.println(result.getErrMsg());
//       int i =  publishDataDAO.updatePublishTime(null);
        System.out.println(i);
    }
}
