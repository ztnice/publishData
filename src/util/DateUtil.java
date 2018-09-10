package util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by haozt on 2018/5/22
 */
public class DateUtil {
    public static long daysDiff(Date firstDay, Date lastDay) {
        if (firstDay == null || lastDay == null) {
            return 0;
        }
        long allDays = (lastDay.getTime() - (firstDay.getTime())) / (1000 * 24 * 60 * 60);

        return allDays;
    }

    public static Long yearDiff(Date firstDay, Date lastDay) {
        if (firstDay == null || lastDay == null)
            return null;
        Calendar first = Calendar.getInstance();
        first.setTime(firstDay);
        Calendar last = Calendar.getInstance();
        last.setTime(lastDay);
        long years = last.get(Calendar.YEAR) - first.get(Calendar.YEAR);
        return years;
    }

    public static String getTodayTextYMD() {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(new Date());

    }
    public static String getTodayTextYM() {
        DateFormat df = new SimpleDateFormat("yyyyMM");
        return df.format(new Date());
    }

    public static String getTodayTextY() {
        DateFormat df = new SimpleDateFormat("yyyy");
        return df.format(new Date());
    }
    public static String getYesterdayTextYMD() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(c.getTime());

    }

    public static Date getYesterday() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }

    public static Date getThiryDaysAgo() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -30);
        return c.getTime();
    }

    public static Date parseDateYMD(String ymd) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            return df.parse(ymd);
        } catch (ParseException e) {
            return getYesterday();
        }
    }

    public static Date addMinutes(Date dt, int minute) {
        Calendar cal = Calendar.getInstance();
        if (dt != null) {
            cal.setTime(dt);
        }
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }

    public static Date addDays(Date dt, int days) {
        Calendar cal = Calendar.getInstance();
        if (dt != null) {
            cal.setTime(dt);
        }
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static Date getToday() {
        return trimTimestamp(new Date());
    }

    public static Date trimTimestamp(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTime();
    }


    public static String FILE_NAME = "MMddHHmmssSSS";
    public static String DEFAULT_PATTERN = "yyyy-MM-dd";
    public static String DIR_PATTERN = "yyyy/MM/dd/";
    public static String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static String TIMES_PATTERN = "HH:mm:ss";
    public static String NOCHAR_PATTERN = "yyyyMMddHHmmss";
    public static String CHINESE_PATTEN = "yyyy年MM月dd日";
    public static String TIMES_STRANGE = "yyyy-MM-dd HH:mm";
    /**
     * 获取当前时间戳
     *
     * @param
     *
     * @return
     */
    public static String formatDefaultFileName() {

        return formatDateByFormat(new Date(), FILE_NAME);
    }

    /**
     * 日期转换为字符串
     *
     * @param date
     *            日期
     * @param format
     *            日期格式
     * @return 指定格式的日期字符串
     */
    public static String formatDateByFormat(Date date, String format) {
        String result = "";
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                result = sdf.format(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 转换为默认格式(yyyy-MM-dd)的日期字符串
     *
     * @param date
     *
     * @return
     */
    public static String formatDefaultDate(Date date) {
        return formatDateByFormat(date, DEFAULT_PATTERN);
    }

    /**
     * 转换为目录格式(yyyy/MM/dd/)的日期字符串
     *
     * @param date
     *
     * @return
     */
    public static String formatDirDate(Date date) {
        return formatDateByFormat(date, DIR_PATTERN);
    }

    /**
     * 转换为完整格式(yyyy-MM-dd HH:mm:ss)的日期字符串
     *
     * @param date
     *
     * @return
     */
    public static String formatTimestampDate(Date date) {
        return formatDateByFormat(date, TIMESTAMP_PATTERN);
    }

    /**
     * 转换为时分秒格式(HH:mm:ss)的日期字符串
     *
     * @param date
     *
     * @return
     */
    public static String formatTimesDate(Date date) {
        return formatDateByFormat(date, TIMES_PATTERN);
    }

    /**
     * 转换为时分秒格式(HH:mm:ss)的日期字符串
     *
     * @param date
     *
     * @return
     */
    public static String formatNoCharDate(Date date) {
        return formatDateByFormat(date, NOCHAR_PATTERN);
    }

    /**
     * 日期格式字符串转换为日期对象
     *
     * @param strDate
     *            日期格式字符串
     * @param pattern
     *            日期对象
     * @return
     */
    public static Date parseDate(String strDate, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Date nowDate = format.parse(strDate);
            return nowDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转换为默认格式(yyyy-MM-dd)日期对象
     *
     * @param date
     *
     * @return
     *
     * @throws Exception
     */
    public static Date parseDefaultDate(String date) {
        return parseDate(date, DEFAULT_PATTERN);
    }

    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(8);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 字符串转换为完整格式(yyyy-MM-dd HH:mm:ss)日期对象
     *
     * @param date
     *
     * @return
     *
     * @throws Exception
     */
    public static Date parseTimesTampDate(String date) {
        return parseDate(date, TIMESTAMP_PATTERN);
    }

    /**
     * 获得当前时间
     *
     * @return
     */
    public static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * 获取当前时间之前或之后几分钟 minute
     *
     * @return
     */
    public static Date getTimeByMinute(int minute) {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, minute);

        return calendar.getTime();

    }

    /**
     * sql Date 转 util Date
     *
     * @param date
     *            java.sql.Date日期
     * @return java.util.Date
     */
    public static Date parseUtilDate(java.sql.Date date) {
        return date;
    }

    /**
     * util Date 转 sql Date
     *
     * @param date
     *            java.sql.Date日期
     * @return
     */
    public static java.sql.Date parseSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * 获取年份
     *
     * @param date
     *
     * @return
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取月份
     *
     * @param date
     *
     * @return
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取星期
     *
     * @param date
     *
     * @return
     */
    public static int getWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        dayOfWeek = dayOfWeek - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        return dayOfWeek;
    }

    public static String getWeekStr(Date date) {
        String[] weeks = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 得到本周的起始时间
     * @param currentDate
     * @return
     */
    public static Date getBeginDateofThisWeek(Date currentDate){
        Calendar current = Calendar.getInstance();
        current.setTime(currentDate);
        int dayOfWeek  = current.get(Calendar.DAY_OF_WEEK);

        if(dayOfWeek==1){ //如果是星期天，星期一则往前退6天
            current.add(Calendar.DAY_OF_MONTH, -6);
        }else{
            current.add(Calendar.DAY_OF_MONTH, 2-dayOfWeek);
        }

        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        return current.getTime();
    }
    /**
     * 得到本周的起始时间
     * @param currentDate
     * @return
     */
    public static Date getBeginDateofThisMonth(Date currentDate){
        Calendar current = Calendar.getInstance();
        current.setTime(currentDate);
        current.set(Calendar.DAY_OF_MONTH, 1);
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);
        return current.getTime();
    }

    public static Date getLastDayOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return calendar.getTime();
    }

    //获取当月第一天日期
    public static Date getFirstDayOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
//		int year = calendar.get(Calendar.YEAR);
//		int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 给定的时间再加上指定月份数
     *
     * @author wei suicun
     * @param dt
     * @param months
     * @return
     */
    public static Date getAddMonthDate(Date dt, int months) {

        if (dt == null)
            dt = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.add(Calendar.MONTH, months);

        return cal.getTime();
    }

    /**
     * 获取日期(多少号)
     *
     * @param date
     *
     * @return
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前时间(小时)
     *
     * @param date
     *
     * @return
     */
    public static int getHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前时间(分)
     *
     * @param date
     *
     * @return
     */
    public static int getMinute(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    /**
     * 获取当前时间(秒)
     *
     * @param date
     *
     * @return
     */
    public static int getSecond(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.SECOND);
    }

    /**
     * 获取当前毫秒
     *
     * @param date
     *
     * @return
     */
    public static long getMillis(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    /**
     * 日期增加
     *
     * @param date
     *            Date
     *
     * @param day
     *            int
     *
     * @return Date
     */
    public static Date addDate(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getMillis(date) + ((long) day) * 24 * 3600 * 1000);
        return c.getTime();
    }

    /**
     * 日期相减(返回天数)
     *
     * @param date
     *            Date
     *
     * @param date1
     *            Date
     *
     * @return int 相差的天数
     */
    public static int diffDate(Date date, Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / (24 * 3600 * 1000));
    }

    /**
     * 日期相减(返回秒值)
     *
     * @param date
     *            Date
     * @param date1
     *            Date
     * @return int
     * @author
     */
    public static Long diffDateTime(Date date, Date date1) {
        return (Long) ((getMillis(date) - getMillis(date1)) / 1000);
    }

    /**
     *  日期相减(返回分钟值)
     * @param date
     * @param date1
     * @return
     */
    public static long diffDateMinute(Date date, Date date1) {
        return (long) ((getMillis(date) - getMillis(date1)) / (1000 * 60 ));
    }

    private static String[] randomValues = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b",
            "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "u", "t", "s", "o", "x", "v", "p", "q", "r",
            "w", "y", "z" };

    public static String getRandomNumber(int lenght) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < lenght; i++) {
            Double number = Math.random() * (randomValues.length - 1);
            str.append(randomValues[number.intValue()]);
        }
        return str.toString();
    }

    /**
     * 生成账号
     *
     * @param acount
     * @return
     */
    public static String nextAcounnt(String acount) {
        String newAcc = "";
        if (Integer.parseInt(acount) < 10000) {
            Integer newAc = Integer.parseInt(acount) + 1;
            if (newAc < 1000) {
                int count = String.valueOf(newAc).length();
                if (count == 1) {
                    newAcc = "000" + newAc;
                } else if (count == 2) {
                    newAcc = "00" + newAc;
                } else if (count == 3) {
                    newAcc = "0" + newAc;
                }
            } else {
                newAcc = String.valueOf(newAc);
            }
        } else {
            newAcc = acount;
        }
        return newAcc;
    }

    public static boolean isNumeric1(String str) {
        if (str != null && !"".equals(str) && str.length() <= 9) {
            Pattern pattern = Pattern.compile("[0-9]*");
            return pattern.matcher(str).matches();
        } else {
            return false;
        }
    }

    /**
     * 生成流水号
     *
     * @param t
     *            流水号位数
     * @return 流水号
     */
    public static String getSequenceNumber(int t) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String str = sdf.format(d);
        String haomiao = String.valueOf(System.nanoTime());
        str = str + haomiao.substring(haomiao.length() - 6, haomiao.length());
        return str.substring(str.length() - t, str.length());
    }

    /**
     * 获得本日星期数,星期一:1,星期日:7
     * 如果传入null则默认为本日
     * @return
     */
    public static int getDayOfWeek(Calendar calendar){
        int today;
        if(calendar!=null){
            today=calendar.get(Calendar.DAY_OF_WEEK);
        }else{
            today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        }
        if(today==1)
            return 7;
        else
            return today-1;
    }

    /**
     * 获取指定时间一周的第一天
     * @return
     */
    public static Date getFirstDayOfWeek(Date date){
        if(date == null){
            date = getToday();
        }
        int week = getWeek(date);

        return addDays(date,1-week);
    }

    /**
     * 判断2个日期是不是同一天(不考虑时分秒)
     *
     * @param dt1
     * @param dt2
     * @return
     */
    public static boolean isSameDay(Date dt1, Date dt2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dt1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(dt2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获得某天的截至时刻23:59:59
     *
     * @param date
     * @return
     */
    public static Date getDayEnd(Date date) {

        if (date == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 获得某天的开始时间
     *
     * @param date
     * @return
     */
    public static Date getDayStart(Date date) {

        if (date == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 计算两个日期间相隔的秒数
     *
     * @param d1
     *            日期1
     * @param d2
     *            日期2
     * @return
     */
    public static long getSecondBetweem(Date d1, Date d2) {

        return (d1.getTime() - d2.getTime()) / 1000;
    }

    /**
     * 将秒数转换为时分秒的格式,不足一小时   小时不显示
     * 如 传入56 --> 00:56
     * @param time
     * @return
     */
    public static String coverSecondToHMS(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}
