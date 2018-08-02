package execute;

/**
 * @Author: haozt
 * @Date: 2018/8/1
 * @Description:
 */
public class Test {
    public static void main(String[] a){
        PublishData publishData = new PublishData();
        publishData.publishDataFromTcChange(new String[]{"000769/01/P"});
    }
}
