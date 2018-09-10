package task;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: haozt
 * @Date: 2018/8/29
 * @Description:
 */
public class TaskManager {
    /**
     * 开启任务
     */

    public void startTask(TimerTask task, int hour, int minute,int second){
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, second);
//        //执行定时任务的时间
//        Date date=calendar.getTime();
//        //为了避免若容器启动的时间晚于定时时间，在重启容器的时候会立刻执行该任务
//        if (date.before(new Date())) {
//            date = this.addDay(date, 1);
//        }
        Timer timer = new Timer();
//        //任务在指定的时间开始进行重复的固定延迟执行
//        timer.schedule(task,date);
        timer.schedule(task,0,10*60*1000);

    }
    // 增加或减少天数
    public Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
}
