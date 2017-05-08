package com.lb.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.xuanbo.xuan.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import com.lb.common.util.Log;
  
/** 
 * 1.默认会存储在SDcard里如果没有SDcard会存储在内存中的安装目录下面
 * 2.默认保存7天内的日志,大小为0的日志文件会被删除
 * 3.如果有SDCard的话会将之前内存中的文件拷贝到SDCard中
 * 4.默认文件大小为10M,超过10M则切换新文件,检查频度为60分钟
 * @author Administrator
 */  
public class LogUtil{

    private static final int SDCARD_LOG_FILE_SAVE_DAYS = -3;                       //日志文件的最多保存天数
      
    private String LOG_PATH_MEMORY_DIR;   //日志文件在内存中的路径(日志文件在安装目录中的路径)  
    private String LOG_PATH_SDCARD_DIR;   //日志文件在sdcard中的路径
    private String LOG_SERVICE_LOG_PATH;  //本类产生的日志,记录日志开启信息,该日志不会被删除  
      
    private final int SDCARD_TYPE = 0;        //当前的日志记录类型为存储在SD卡下面  
    private final int MEMORY_TYPE = 1;        //当前的日志记录类型为存储在内存中  
    private int CURR_LOG_TYPE = SDCARD_TYPE;  //当前的日志记录类型
      
    private String CURR_INSTALL_LOG_NAME; //如果当前的日志写在内存中，记录当前的日志文件名称
      
    private String logServiceLogName = "Log.log";//本类产生的日志文件名称  
    private SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//本类输出日志内容的时间格式
    private OutputStreamWriter writer;//本类输出日志句柄

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//日志名称格式
      
    private Process process;
      
    private WakeLock wakeLock;  
      
    private SDStateMonitorReceiver sdStateReceiver; //SDcard状态监测
    private LogTaskReceiver logTaskReceiver;        //日志日期变化监测
    
    public static String logPath;
    
    private Context mContext;
    
    public LogUtil(Context context){
    	this.mContext = context;
    	init();
        register();
        new LogCollectorThread().start();
    } 
    /**
     * 初始化
     */
    private void init(){  
        LOG_PATH_MEMORY_DIR = mContext.getFilesDir().getAbsolutePath() + File.separator + "log";
        LOG_SERVICE_LOG_PATH = LOG_PATH_MEMORY_DIR + File.separator + logServiceLogName;
        LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator  
                            +   mContext.getPackageName() + File.separator + "log";
        
        createLogDir();
        
        try { 
            writer = new OutputStreamWriter(new FileOutputStream( 
                    LOG_SERVICE_LOG_PATH, true)); 
        } catch (FileNotFoundException e) { 
            Log.e(Constants.LOG_TAG, e.getMessage(), e); 
        } 
          
        PowerManager pm = (PowerManager) mContext.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.LOG_TAG);
          
        CURR_LOG_TYPE = getCurrLogType();
        Log.i(Constants.LOG_TAG, "LogService onCreate");
    }
    /**
     * 注册广播接收器
     */
    private void register(){  
        IntentFilter sdCarMonitorFilter = new IntentFilter();
        sdCarMonitorFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        sdCarMonitorFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        sdCarMonitorFilter.addDataScheme("file");
        sdStateReceiver = new SDStateMonitorReceiver();
        mContext.registerReceiver(sdStateReceiver, sdCarMonitorFilter);
          
        IntentFilter logTaskFilter = new IntentFilter();
        logTaskFilter.addAction(Intent.ACTION_DATE_CHANGED);
        logTaskReceiver = new LogTaskReceiver();
        mContext.registerReceiver(logTaskReceiver,logTaskFilter);
    }  
      
      
      
    /** 
     * 获取当前应存储在内存中还是存储在SDCard中 
     * @return 
     */  
    public int getCurrLogType(){  
        if (!Environment.getExternalStorageState().equals(  
                Environment.MEDIA_MOUNTED)) {  
            return MEMORY_TYPE;
        }else{  
            return SDCARD_TYPE;
        }  
    }
  
    /** 
     * 日志收集 
     * 1.清除日志缓存  
     * 2.杀死应用程序已开启的Logcat进程防止多个进程写入一个日志文件 
     * 3.开启日志收集进程  
     * 4.处理日志文件 
     *   移动 OR 删除 
     */  
    public class LogCollectorThread extends Thread {  
          
        public LogCollectorThread(){  
            super("LogCollectorThread");
            Log.d(Constants.LOG_TAG, "LogCollectorThread is create");
        }  
          
          
        public void run() {
            try {  
                wakeLock.acquire(); //唤醒手机  
                  
                clearLogCache();
                  
                List<String> orgProcessList = getAllProcess();
                List<ProcessInfo> processInfoList = getProcessInfoList(orgProcessList);
                killLogcatProc(processInfoList);
                  
                createLogCollector();
                  
                Thread.sleep(1000);//休眠，创建文件，然后处理文件，不然该文件还没创建，会影响文件删除  
                  
                handleLog();
                  
                wakeLock.release(); //释放  
            } catch (Exception e) {  
            	Log.e(Constants.LOG_TAG, e.getMessage(), e); 
                recordLogServiceLog(Log.getStackTraceString(e));
            }  
        }  
    }  
      
    /** 
     * 每次记录日志之前先清除日志的缓存, 不然会在两个日志文件中记录重复的日志 
     */  
    private void clearLogCache() {  
        Process proc = null;
        List<String> commandList = new ArrayList<String>();
        commandList.add("logcat");
        commandList.add("-c");
        try {  
            proc = Runtime.getRuntime().exec(  
                    commandList.toArray(new String[commandList.size()]));
            StreamConsumer errorGobbler = new StreamConsumer(proc  
                    .getErrorStream());
  
            StreamConsumer outputGobbler = new StreamConsumer(proc  
                    .getInputStream());
  
            errorGobbler.start();
            outputGobbler.start();
            if (proc.waitFor() != 0) {  
                Log.e(Constants.LOG_TAG, " clearLogCache proc.waitFor() != 0");
                recordLogServiceLog("clearLogCache clearLogCache proc.waitFor() != 0");
            }  
        } catch (Exception e) {  
            Log.e(Constants.LOG_TAG, "clearLogCache failed", e);
            recordLogServiceLog("clearLogCache failed");
        } finally {  
            try {  
                proc.destroy();
            } catch (Exception e) {  
                Log.e(Constants.LOG_TAG, "clearLogCache failed", e);
                recordLogServiceLog("clearLogCache failed");
            }  
        }  
    }  
  
    /** 
     * 关闭由本程序开启的logcat进程： 
     * 根据用户名称杀死进程(如果是本程序进程开启的Logcat收集进程那么两者的USER一致) 
     * 如果不关闭会有多个进程读取logcat日志缓存信息写入日志文件 
     *  
     * @param allProcList 
     * @return 
     */  
    private void killLogcatProc(List<ProcessInfo> allProcList) {  
        if(process != null){  
            process.destroy();
        }  
        String packName = mContext.getPackageName();
        String myUser = getAppUser(packName, allProcList);
        for (ProcessInfo processInfo : allProcList) {  
            if (processInfo.name.toLowerCase().equals("logcat")  
                    && processInfo.user.equals(myUser)) {  
                android.os.Process.killProcess(Integer.parseInt(processInfo.pid));
            }  
        }  
    }  
  
    /** 
     * 获取本程序的用户名称 
     *  
     * @param packName 
     * @param allProcList 
     * @return 
     */  
    private String getAppUser(String packName, List<ProcessInfo> allProcList) {  
        for (ProcessInfo processInfo : allProcList) {  
            if (processInfo.name.equals(packName)) {  
                return processInfo.user;
            }  
        }  
        return null;
    }  
  
    /** 
     * 根据ps命令得到的内容获取PID，User，name等信息 
     *  
     * @param orgProcessList 
     * @return 
     */  
    private List<ProcessInfo> getProcessInfoList(List<String> orgProcessList) {  
        List<ProcessInfo> procInfoList = new ArrayList<ProcessInfo>();
        for (int i = 1; i < orgProcessList.size(); i++) {  
            String processInfo = orgProcessList.get(i);
            String[] proStr = processInfo.split(" ");
            // USER PID PPID VSIZE RSS WCHAN PC NAME  
            // root 1 0 416 300 c00d4b28 0000cd5c S /init  
            List<String> orgInfo = new ArrayList<String>();
            for (String str : proStr) {  
                if (!"".equals(str)) {  
                    orgInfo.add(str);
                }  
            }  
            if (orgInfo.size() == 9) {  
                ProcessInfo pInfo = new ProcessInfo();
                pInfo.user = orgInfo.get(0);
                pInfo.pid = orgInfo.get(1);
                pInfo.ppid = orgInfo.get(2);
                pInfo.name = orgInfo.get(8);
                procInfoList.add(pInfo);
            }  
        }  
        return procInfoList;
    }  
  
    /** 
     * 运行PS命令得到进程信息 
     *  
     * @return 
     *          USER PID PPID VSIZE RSS WCHAN PC NAME 
     *          root 1 0 416 300 c00d4b28 0000cd5c S /init 
     */  
    private List<String> getAllProcess() {  
        List<String> orgProcList = new ArrayList<String>();
        Process proc = null;
        try {  
            proc = Runtime.getRuntime().exec("ps");
            StreamConsumer errorConsumer = new StreamConsumer(proc  
                    .getErrorStream());
  
            StreamConsumer outputConsumer = new StreamConsumer(proc  
                    .getInputStream(), orgProcList);
  
            errorConsumer.start();
            outputConsumer.start();
            if (proc.waitFor() != 0) {  
                Log.e(Constants.LOG_TAG, "getAllProcess proc.waitFor() != 0");
                recordLogServiceLog("getAllProcess proc.waitFor() != 0");
            }  
        } catch (Exception e) {  
            Log.e(Constants.LOG_TAG, "getAllProcess failed", e);
            recordLogServiceLog("getAllProcess failed");
        } finally {  
            try {  
                proc.destroy();
            } catch (Exception e) {  
                Log.e(Constants.LOG_TAG, "getAllProcess failed", e);
                recordLogServiceLog("getAllProcess failed");
            }  
        }  
        return orgProcList;
    }  
      
    /** 
     * 开始收集日志信息 
     */  
    public void createLogCollector() {
        String logFileName = sdf.format(new Date()) + ".log";// 日志文件名称  
        List<String> commandList = new ArrayList<String>();
        commandList.add("logcat");
        commandList.add("-f");
        commandList.add(genLogPath());
        commandList.add("-v");
        commandList.add("time");
        commandList.add("-s");
        commandList.add(mContext.getPackageName() + ":D SMACK:FileDebugger:D chat:D "+mContext.getString(R.string.app_name)+":W");
        try {  
            process = Runtime.getRuntime().exec(  
                    commandList.toArray(new String[commandList.size()]));
            recordLogServiceLog("start collecting the log,and log name is:"+logFileName);
        } catch (Exception e) {  
            Log.e(Constants.LOG_TAG, "CollectorThread == >" + e.getMessage(), e);
            recordLogServiceLog("CollectorThread == >" + e.getMessage());
        }  
    }  
      
    /** 
     * 根据当前的存储位置得到日志的绝对存储路径 
     * @return 
     */  
    public String genLogPath(){  
        createLogDir();
        String logFileName = sdf.format(new Date()) + ".log";// 日志文件名称
        CURR_INSTALL_LOG_NAME = logFileName;
        if(CURR_LOG_TYPE == MEMORY_TYPE){
            Log.d(Constants.LOG_TAG, "Log stored in memory, the path is:"+LOG_PATH_MEMORY_DIR + File.separator + logFileName);
            logPath = LOG_PATH_MEMORY_DIR + File.separator + logFileName;
            return logPath;
        }else{
            Log.d(Constants.LOG_TAG, "Log stored in SDcard, the path is:"+LOG_PATH_SDCARD_DIR + File.separator + logFileName);
            logPath = LOG_PATH_SDCARD_DIR + File.separator + logFileName;
            return logPath;
        }  
    }  
      
    /** 
     * 处理日志文件 
     * 1.如果日志文件存储位置切换到内存中，删除除了正在写的日志文件 
     *   并且部署日志大小监控任务，控制日志大小不超过规定值 
     * 2.如果日志文件存储位置切换到SDCard中，删除7天之前的日志，移 
     *     动所有存储在内存中的日志到SDCard中，并将之前部署的日志大小 
     *   监控取消 
     */  
    public void handleLog(){
        if(CURR_LOG_TYPE == MEMORY_TYPE){
            deleteMemoryExpiredLog();
        }else{  
            moveLogfile();
            deleteSDcardExpiredLog();
        }  
    }
      
    /** 
     * 检查日志文件是否为当日
     * 如果非当日则重新开启一个日志收集进程 
     */  
    private void checkLog(){  
        if(CURR_INSTALL_LOG_NAME != null && !"".equals(CURR_INSTALL_LOG_NAME)){
            String path = LOG_PATH_MEMORY_DIR + File.separator + CURR_INSTALL_LOG_NAME;
            if(CURR_LOG_TYPE == SDCARD_TYPE){
            	path = LOG_PATH_SDCARD_DIR + File.separator + CURR_INSTALL_LOG_NAME;
            }
            File file = new File(path);
            if(!file.exists()){
            	Log.d(Constants.LOG_TAG, "The log is not exist, then create new one");
            	new LogCollectorThread().start();
            	return;
            }  
            Log.d(Constants.LOG_TAG, "checkLog() ==> The size of the log is too big?");
            if(file.getName().substring(0, 8).compareTo(sdf.format(new Date()).substring(0,8)) != 0){
            	Log.d(Constants.LOG_TAG, "The log is not today!");
                new LogCollectorThread().start();
            }
        }  
    }  
      
    /** 
     * 创建日志目录 
     */  
    private void createLogDir() {  
        File file = new File(LOG_PATH_MEMORY_DIR);
        boolean mkOk;
        if (!file.isDirectory()) {  
            mkOk = file.mkdirs();
            if (!mkOk) {  
                mkOk = file.mkdirs();
            }  
        }
          
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
            file = new File(LOG_PATH_SDCARD_DIR);
            if (!file.isDirectory()) {  
                mkOk = file.mkdirs();
                if (!mkOk) {  
                    recordLogServiceLog("move file failed,dir is not created succ");
                    return;
                }  
            }  
        }  
    }  
      
    /** 
     * 将日志文件转移到SD卡下面 
     */  
    private void moveLogfile() {  
        if (!Environment.getExternalStorageState().equals(  
                Environment.MEDIA_MOUNTED)) {
            return;
        }  
        File file = new File(LOG_PATH_SDCARD_DIR);
        if (!file.isDirectory()) {  
            boolean mkOk = file.mkdirs();
            if (!mkOk) {
                return;
            }  
        }  
  
        file = new File(LOG_PATH_MEMORY_DIR);
        if (file.isDirectory()) {  
            File[] allFiles = file.listFiles();
            if(allFiles!=null){
            for (File logFile : allFiles) {  
                String fileName = logFile.getName();
                if (logServiceLogName.equals(fileName)) {  
                    continue;
                }
                boolean isSucc = copy(logFile, new File(LOG_PATH_SDCARD_DIR  
                            + File.separator + fileName));
                if (isSucc) {  
                    logFile.delete();
                }  
            }  
            }
        }  
    }  
  
    /** 
     * 删除SD卡下过期的日志 
     */  
    private void deleteSDcardExpiredLog() {  
        File file = new File(LOG_PATH_SDCARD_DIR);
        if (file.isDirectory()) {  
            File[] allFiles = file.listFiles();
            for (int i=0;i<allFiles.length;i++) {
                File _file =  allFiles[i];
                if (logServiceLogName.equals(_file.getName()) ||  _file.getName().equals(CURR_INSTALL_LOG_NAME)) {  
                    continue;
                }
                if(_file.length() == 0){
                	_file.delete();
                	Log.d(Constants.LOG_TAG, "delete 0size log success,the log path is:"+_file.getAbsolutePath());
                	continue;
                }
                Calendar cal = Calendar.getInstance();
                if(_file.getName().substring(0, 8).compareTo(sdf.format(cal.getTime()).substring(0, 8)) > 0){
                	_file.delete();
                	Log.d(Constants.LOG_TAG, "delete future log success,the log path is:"+_file.getAbsolutePath());
                	continue;
                }
                cal.add(Calendar.DATE, SDCARD_LOG_FILE_SAVE_DAYS);
                if(_file.getName().substring(0, 8).compareTo(sdf.format(cal.getTime()).substring(0, 8)) < 0){
                	_file.delete();
                	Log.d(Constants.LOG_TAG, "delete expired log success,the log path is:"+_file.getAbsolutePath());
                }
            }  
        }  
    }  
  
      
    /** 
     * 删除内存中的过期日志，删除规则： 
     * 除了当前的日志和离当前时间最近的日志保存其他的都删除 
     */  
    private void deleteMemoryExpiredLog(){  
        File file = new File(LOG_PATH_MEMORY_DIR);
        if (file.isDirectory()) {  
            File[] allFiles = file.listFiles();
            for (int i=0;i<allFiles.length;i++) {
                File _file =  allFiles[i];
                if (logServiceLogName.equals(_file.getName()) ||  _file.getName().equals(CURR_INSTALL_LOG_NAME)) {  
                    continue;
                }
                if(_file.length() == 0){
                	_file.delete();
                	Log.d(Constants.LOG_TAG, "delete 0size log success,the log path is:"+_file.getAbsolutePath());
                	continue;
                }
                Calendar cal = Calendar.getInstance();
                if(_file.getName().substring(0, 8).compareTo(sdf.format(cal.getTime()).substring(0, 8)) > 0){
                	_file.delete();
                	Log.d(Constants.LOG_TAG, "delete expired log success,the log path is:"+_file.getAbsolutePath());
                	continue;
                }
                cal.add(Calendar.DATE, SDCARD_LOG_FILE_SAVE_DAYS);
                if(_file.getName().substring(0, 8).compareTo(sdf.format(cal.getTime()).substring(0, 8)) < 0){
                	_file.delete();
                	Log.d(Constants.LOG_TAG, "delete expired log success,the log path is:"+_file.getAbsolutePath());
                }
            }  
        }  
    }  
      
    /** 
     * 拷贝文件 
     * @param source 
     * @param target 
     * @return 
     */  
    private boolean copy(File source, File target) {  
        FileInputStream in = null;
        FileOutputStream out = null;
        try {  
            if(!target.exists()){  
                boolean createSucc = target.createNewFile();
                if(!createSucc){  
                    return false;
                }  
            }  
            in = new FileInputStream(source);
            out = new FileOutputStream(target, true);
            byte[] buffer = new byte[8*1024];
            int count;
            while ((count = in.read(buffer)) != -1) {  
                out.write(buffer, 0, count);
            }  
            return true;
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, e.getMessage(), e);
            recordLogServiceLog("copy file fail");
            return false;
        } finally{  
            try {  
                if(in != null){  
                    in.close();
                }  
                if(out != null){  
                    out.close();
                }  
            } catch (IOException e) {  
                Log.e(Constants.LOG_TAG, e.getMessage(), e);
                recordLogServiceLog("copy file fail");
                return false;
            }  
        }  
          
    }  
  
    /** 
     * 记录日志服务的基本信息 防止日志服务有错，在LogCat日志中无法查找  
     * 此日志名称为Log.log 
     *  
     * @param msg 
     */  
    public void recordLogServiceLog(String msg) {  
        if (writer != null) {
            try {  
                Date time = new Date();
                writer.write(myLogSdf.format(time) + " : " + msg);
                writer.write("\n");
                writer.flush();
            } catch (IOException e) {  
                Log.e(Constants.LOG_TAG, e.getMessage(), e);
            }  
        }  
    }
    /**
     * 进程信息实体
     * @author Administrator
     */
    class ProcessInfo {  
        public String user;
        public String pid;
        public String ppid;
        public String name;
  
          
        public String toString() {  
            String str = "user=" + user + " pid=" + pid + " ppid=" + ppid  
                    + " name=" + name;
            return str;
        }  
    }  
    /**
     * 流信息读取线程
     * @author Administrator
     */
    class StreamConsumer extends Thread {  
        InputStream is;
        List<String> list;
  
        StreamConsumer(InputStream is) {  
            this.is = is;
        }  
  
        StreamConsumer(InputStream is, List<String> list) {  
            this.is = is;
            this.list = list;
        }  
  
        public void run() {  
            try {  
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {  
                    if (list != null) {  
                        list.add(line);
                    }  
                }  
            } catch (IOException ioe) {  
            	Log.e(Constants.LOG_TAG, ioe.getMessage(), ioe); 
            }  
        }  
    }  
      
    /** 
     * 监控SD卡状态 
     * @author Administrator 
     * 
     */  
    class SDStateMonitorReceiver extends BroadcastReceiver{  
        public void onReceive(Context context, Intent intent) {  
              
            if(Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())){   //存储卡被卸载  
                if(CURR_LOG_TYPE == SDCARD_TYPE){  
                    Log.d(Constants.LOG_TAG, "SDcar is UNMOUNTED");
                    CURR_LOG_TYPE = MEMORY_TYPE;
                    new LogCollectorThread().start();
                }
            }else{                                                          //存储卡被挂载  
                if(CURR_LOG_TYPE == MEMORY_TYPE){  
                    Log.d(Constants.LOG_TAG, "SDcar is MOUNTED");
                    CURR_LOG_TYPE = SDCARD_TYPE;
                    new LogCollectorThread().start();
                }  
            }
        }
    }
      
    /** 
     * 日志任务接收 
     * 切换日志，监控日志大小 
     * @author Administrator 
     * 
     */  
    class LogTaskReceiver extends BroadcastReceiver{  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();
            if(Intent.ACTION_DATE_CHANGED.equals(action)){  
                checkLog();
            }
        }  
    }
    /**
     * 对象回收
     */
    public void finalize() {
        recordLogServiceLog("LogService finalize");
        mContext.unregisterReceiver(sdStateReceiver);
        mContext.unregisterReceiver(logTaskReceiver);
        if (writer != null) {  
            try {  
                writer.close();
            } catch (IOException e) {  
            	Log.e(Constants.LOG_TAG, e.getMessage(), e); 
            }  
        }  
        if (process != null) {  
            process.destroy();
        }
        
        try {
			super.finalize();
		} catch (Throwable e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e); 
		}
    }
}  