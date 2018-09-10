package task;

import sql.dbdo.SupplyFtpPath;
import sql.mybatis.impl.PublishDataDAOImpl;
import sql.mybatis.inter.PublishDataDAO;
import util.ftp.FtpUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * @Author: haozt
 * @Date: 2018/8/29
 * @Description:
 */
public class RankTask extends TimerTask {
    @Override
    public void run() {
        PublishDataDAO publishDataDAO = new PublishDataDAOImpl();
        List<SupplyFtpPath> list = publishDataDAO.findSupplyFtpPathList();
        List<SupplyFtpPath> list1 = new ArrayList<>();
        if(list != null && list.size()>0){
            for(SupplyFtpPath path:list){
//                long diff = DateUtil.daysDiff(new Date(),path.getPublishTime());
                long diff = (new Date().getTime() - (path.getPublishTime().getTime()))/(1000*60);//分钟
                String p = path.getFtpPath();
                if(diff >= 10){
                    boolean b = FtpUtil.removeDirectoryALLFile(p);
                    if(b){
                        list1.add(path);
                    }
                }
            }
        }
        if(list1 != null && list1.size()>0){
            publishDataDAO.deleteFtpPathList(list1);
        }
    }
}
