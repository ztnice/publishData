package execute;

import bean.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haozt on 2018/05/31
 */
public class Test {
    public static void main(String[] args){
        PublishData data = new PublishData();
        Supplier supplier = new Supplier();
        supplier.setOutCpnyCode("123131");
        supplier.setOutCpnyAccepter("张三");
        supplier.setOutCpnyEmail("zhit876@163.com");
        BaseDataBean b  = new BaseDataBean();
        b.setProcessNum("3413大ccda11");
        b.setAppDept("mmpa,donot in");
        b.setApplicant("sadas");
        b.setApplicantPhone("12324234");
        b.setProvideType("das");
        b.setAppDept("sada");
        b.setSupplier(supplier);
        b.setApplicantEmail("zhit876@163.com");

        List<ItemBean> list = new ArrayList<>();
        ItemBean bean = new ItemBean();
        bean.setCAD_blueprint(1);
        bean.setOthers(1);
        bean.setCatia_blueprint(1);
        bean.setJT_digifax(0);
        bean.setCatia_digifax(1);
        bean.setCGR_digifax(1);
        bean.setItem_name("两件测试 ");
        bean.setItem_id("005621");
        bean.setItemRevision("A");
        bean.setProcessNum(b.getProcessNum());
        list.add(bean);
        List<DocumentBean> beans = new ArrayList<>();
        DocumentBean documentBean = new DocumentBean();
        documentBean.setDocument_id("005621");
        documentBean.setDocument_name("文件测试111");
        documentBean.setDocumentRevision("A");
        documentBean.setProcessNum(b.getProcessNum());
        beans.add(documentBean);

        DocumentBean documentBean1 = new DocumentBean();
        documentBean1.setDocument_id("005621");
        documentBean1.setDocument_name("文件测试2222");
        documentBean1.setDocumentRevision("B");
        documentBean1.setProcessNum(b.getProcessNum());
        beans.add(documentBean1);
        data.publishBaseData(b);
       data.publishDocumentBean(beans);
        Result result = data.publishItemBean(list);
        System.out.println(result.getErrMsg());
    }
}
