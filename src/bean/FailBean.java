package bean;

/**
 * Created by haozt on 2018/05/31
 * 用于记录失败的对象名称和失败原因
 */
public class FailBean {
    private String name;
    private String failMsg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }
}
