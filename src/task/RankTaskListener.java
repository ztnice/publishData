package task;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @Author: haozt
 * @Date: 2018/8/29
 * @Description:
 */
public class RankTaskListener implements ServletContextListener {
    private static final int HOUR = 00;
    private static final int MINUTE = 00;
    private static final int SECOND = 00;
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        new TaskManager().startTask(new RankTask(),
                Integer.valueOf(HOUR), Integer.valueOf(MINUTE), Integer.valueOf(SECOND));
    }

}
