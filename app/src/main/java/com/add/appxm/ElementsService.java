package com.add.appxm;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;


import com.google.gson.Gson;
import com.xixun.joey.uart.BytesData;
import com.xixun.joey.uart.IUartListener;
import com.xixun.joey.uart.IUartService;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ElementsService extends Service {

    public IUartService uart;
    StringBuffer builder = new StringBuffer();
    boolean start1 = false,start2 = false,start3 = false,start4 = false;
    boolean dmgd = false, dm1d = false, dmrd = false, dm5d = false;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            uart = IUartService.Stub.asInterface(iBinder);
            Log.i("TAG_uart", "================ onServiceConnected ====================");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("TAG_uart", "================== onServiceDisconnected ====================");
            uart = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG_Service", "服务开启");
        //  weaTimer();
        startVideoDownload();
        bindCardSystemUartAidl();
        startGetUart();
        final String in = "DMGD WXNo1 2021-11-06 11:06 1111111111111011111111111111110000000000000000000000000111000000001110000000000000000000000000001111100000000000000000000000000000000000000 4 0 9 2 8 2 1104 0 0 39 49 1104 0 232 234 1101 232 1103 186 64 63 1101 182 160 10138 10140 1101 10138 1106 / / / / 2 06000000020000 / / / 0 0 *3C\n" +
                "*13\n" +
                "T";
        final String in1 ="DM1D WXNo1 2021-11-06 11:06 000000000000000000000000000000000000000000000000000011111111110011001100000000000000 217 217 217 1101 217 1101 20 20 39 39 92 92 79 79 *DA\n" +
                "*C0\n" +
                "T";
        final String in2 ="DMRD WXNo1 2021-11-06 11:06 111100000000000000000000000000000011110000000000000 720 26 720 1101 812 33 996 1101 *8E\n" +
                "*6C\n" +
                "T";
        final String in3 ="DM5D WXNo1 2021-11-06 11:06 111111111111111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000 221 224 1101 219 1105 186 / / / / 198 / / / / *4B\n" +
                "*22\n" +
                "T";


      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //restartApp();
                // reboot();
                // LiveDataBus.getInstance().getElementsMutableLiveData().postValue(new Elements("金华气象", "2020-11-04 16:35", "12", "23", "23", "56", "56", "54", "124"));
            }
        }).start();*/
    }
    Timer downloadTimer;
    private String videoinfo;
    private void startVideoDownload(){
        downloadTimer = new Timer();
        downloadTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                OkHttpClient client  = new OkHttpClient();
                Request request = new Request.Builder().url("http://115.220.4.68:8081/qxdata/QxService.svc/playvideo").build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        if(TextUtils.isEmpty(body)||body.length()<5){
                            return;
                        }
                        if(body==videoinfo){
                            return;
                        }
                        videoinfo = body;
                        Log.i("TAG","body:"+body);
                        Gson gson = new Gson();
                        Videos videos = gson.fromJson(body,Videos.class);
                    }
                });
            }
        },0,3*60*1000);
    }
    public boolean startDownload(OkHttpClient client,List<VideoUrl> videos,String name,String vurl){
        boolean flg = false;
        for(VideoUrl url : videos){
            String[] urls = url.vdurl.split("/");
            String urlname = urls[urls.length-1];
            File file = new File(getFilesDir()+File.separator+urlname);
            if(file.exists()){

            }else{
                Request request  = new Request.Builder().url(vurl).build();
                try {
                    Response response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        response.body().byteStream();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flg;
    }
    public void startGetUart() {
        startDMGD();
        startDM1D();
        startDMRD();
        startDM5D();
    }

    Thread thread,thread1,thread2,thread3;
    private boolean openPm = false;
    StringBuffer dmgdBuffer = new StringBuffer();
    StringBuffer dm1dBuffer = new StringBuffer();
    StringBuffer dmrdBuffer = new StringBuffer();
    StringBuffer dm5dBuffer = new StringBuffer();
    String DMGD ="TAG_DMGD";
    String DMRD ="TAG_DMrD";
    String DM1D ="TAG_DM1D";
    String DM5D ="TAG_DM5D";
    private void startDMGD() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(DMGD, "正在获取uart======================");
                } while (null == uart);
                try {
                    //监听/dev/ttyMT2，获取数据/dev/s3c2410_serial3
                    uart.read(port, new IUartListener.Stub() {
                        @Override
                        public void onReceive(BytesData data) throws RemoteException {
                            Log.i(DMGD, "========获取到串口数据===========");
                            for (byte a : data.getData()) {
                                String s1 = "0x" + Integer.toHexString(a & 0xFF) + " ";
                                char ss = (char) a;
                                Log.i(DMGD, "ss:" + ss + ";s1:" + s1);
                                if (start1) {
                                    start1 = true;
                                    dmgdBuffer.append(ss);
                                } else if (ss == 'D') {
                                    dmgd = true;
                                    start1 = true;
                                    dmgdBuffer.append(ss);
                                }
                                Log.i(DMGD, dmgdBuffer.toString());
                                if (dmgdBuffer.length() == 1) {
                                    if (!dmgdBuffer.toString().equals("D")) {
                                        dmgdBuffer.delete(0, dmgdBuffer.length());
                                        dmgd = false;
                                        start1 = false;
                                    }
                                } else if (dmgdBuffer.length() == 2) {
                                    //||!builder.toString().equals("FE")
                                    if (!dmgdBuffer.toString().equals("DM")) {
                                        dmgdBuffer.delete(0, dmgdBuffer.length());
                                        dmgd = false;
                                        start1 = false;
                                    }
                                }else if (dmgdBuffer.length() == 3) {
                                    //||!builder.toString().equals("FE")
                                    if (!dmgdBuffer.toString().equals("DMG")) {
                                        dmgdBuffer.delete(0, dmgdBuffer.length());
                                        dmgd = false;
                                        start1 = false;
                                    }
                                }else if (dmgdBuffer.length() == 4) {
                                    //||!builder.toString().equals("FE")
                                    if (!dmgdBuffer.toString().equals("DMGD")) {
                                        dmgdBuffer.delete(0, dmgdBuffer.length());
                                        dmgd = false;
                                        start1 = false;
                                    }
                                }
                                if (dmgdBuffer.length() > 4 && (dmgd && dmgdBuffer.substring(dmgdBuffer.length() - 1).equals("T"))) {
                                    dmgd = false;
                                    start1 = false;
                                    Log.i(DMGD, dmgdBuffer.toString());
                                    getDMGD(dmgdBuffer.toString());
                                    dmgdBuffer.delete(0, dmgdBuffer.length());
                                }

                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void startDM1D() {
        thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(DM1D, "正在获取uart======================");
                } while (null == uart);
                try {
                    //监听/dev/ttyMT2，获取数据/dev/s3c2410_serial3
                    uart.read(port, new IUartListener.Stub() {
                        @Override
                        public void onReceive(BytesData data) throws RemoteException {
                            Log.i(DM1D, "========获取到串口数据===========");
                            for (byte a : data.getData()) {
                                String s1 = "0x" + Integer.toHexString(a & 0xFF) + " ";
                                char ss = (char) a;
                                Log.i(DM1D, "ss:" + ss + ";s1:" + s1);
                                if (start2) {
                                    start2 = true;
                                    dm1dBuffer.append(ss);
                                } else if (ss == 'D') {
                                    dm1d = true;
                                    start2 = true;
                                    dm1dBuffer.append(ss);
                                }
                                Log.i(DM1D, dm1dBuffer.toString());
                                if (dm1dBuffer.length() == 1) {
                                    if (!dm1dBuffer.toString().equals("D")) {
                                        dm1dBuffer.delete(0, dm1dBuffer.length());
                                        dm1d = false;
                                        start2 = false;
                                    }
                                } else if (dm1dBuffer.length() == 2) {
                                    //||!builder.toString().equals("FE")
                                    if (!dm1dBuffer.toString().equals("DM")) {
                                        dm1dBuffer.delete(0, dm1dBuffer.length());
                                        dm1d = false;
                                        start2 = false;
                                    }
                                }else if (dm1dBuffer.length() == 3) {
                                    if (!dm1dBuffer.toString().equals("DM1")) {
                                        dm1dBuffer.delete(0, dm1dBuffer.length());
                                        dm1d = false;
                                        start2 = false;
                                    }
                                } else if (dm1dBuffer.length() == 4) {
                                    //||!builder.toString().equals("FE")
                                    if (!dm1dBuffer.toString().equals("DM1D")) {
                                        dm1dBuffer.delete(0, dm1dBuffer.length());
                                        dm1d = false;
                                        start2 = false;
                                    }
                                }
                                if (dm1dBuffer.length() > 4 && (dm1d && dm1dBuffer.substring(dm1dBuffer.length() - 1).equals("T"))) {
                                    dm1d = false;
                                    start2 = false;
                                    Log.i(DM1D, dm1dBuffer.toString());
                                    getDM1D(dm1dBuffer.toString());
                                    dm1dBuffer.delete(0, dm1dBuffer.length());
                                }

                            }
                        }

                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
    }

    private void startDMRD() {
        thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(DMRD, "正在获取uart======================");
                } while (null == uart);
                try {
                    //监听/dev/ttyMT2，获取数据/dev/s3c2410_serial3
                    uart.read(port, new IUartListener.Stub() {
                        @Override
                        public void onReceive(BytesData data) throws RemoteException {
                            Log.i(DMRD, "========获取到串口数据===========");
                            for (byte a : data.getData()) {
                                String s1 = "0x" + Integer.toHexString(a & 0xFF) + " ";
                                char ss = (char) a;
                                Log.i(DMRD, "ss:" + ss + ";s1:" + s1);
                                if (start3) {
                                    start3 = true;
                                    dmrdBuffer.append(ss);
                                } else if (ss == 'D') {
                                    dmrd = true;
                                    start3 = true;
                                    dmrdBuffer.append(ss);
                                }
                                Log.i(DMRD,  dmrdBuffer.toString());
                                if ( dmrdBuffer.length() == 1) {
                                    if (! dmrdBuffer.toString().equals("D")) {
                                        dmrdBuffer.delete(0,  dmrdBuffer.length());
                                        dmrd = false;
                                        start3 = false;
                                    }
                                } else if ( dmrdBuffer.length() == 2) {
                                    //||!builder.toString().equals("FE")
                                    if (! dmrdBuffer.toString().equals("DM")) {
                                        dmrdBuffer.delete(0,  dmrdBuffer.length());
                                        dmrd = false;
                                        start3 = false;
                                    }
                                }else  if ( dmrdBuffer.length() == 3) {
                                    if (! dmrdBuffer.toString().equals("DMR")) {
                                        dmrdBuffer.delete(0,  dmrdBuffer.length());
                                        dmrd = false;
                                        start3 = false;
                                    }
                                } else if ( dmrdBuffer.length() == 4) {
                                    //||!builder.toString().equals("FE")
                                    if (! dmrdBuffer.toString().equals("DMRD")) {
                                        dmrdBuffer.delete(0,  dmrdBuffer.length());
                                        dmrd = false;
                                        start3 = false;
                                    }
                                }
                                if ( dmrdBuffer.length() > 4 && ( dmrd &&  dmrdBuffer.substring( dmrdBuffer.length() - 1).equals("T"))) {
                                    dmrd = false;
                                    start3 = false;
                                    Log.i(DMRD,  dmrdBuffer.toString());
                                    getDMRD( dmrdBuffer.toString());
                                    dmrdBuffer.delete(0,  dmrdBuffer.length());
                                }

                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        thread2.start();
    }

    private void startDM5D() {
        thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(DM5D, "正在获取uart======================");
                } while (null == uart);
                try {
                    //监听/dev/ttyMT2，获取数据/dev/s3c2410_serial3
                    uart.read(port, new IUartListener.Stub() {
                        @Override
                        public void onReceive(BytesData data) throws RemoteException {
                            Log.i(DM5D, "========获取到串口数据===========");
                            for (byte a : data.getData()) {
                                String s1 = "0x" + Integer.toHexString(a & 0xFF) + " ";
                                char ss = (char) a;
                                Log.i(DM5D, "ss:" + ss + ";s1:" + s1);
                                if (start4) {
                                    start4 = true;
                                    dm5dBuffer.append(ss);
                                } else if (ss == 'D') {
                                    dm5d = true;
                                    start4 = true;
                                    dm5dBuffer.append(ss);
                                }
                                Log.i(DM5D, dm5dBuffer.toString());
                                if (dm5dBuffer.length() == 1) {
                                    if (!dm5dBuffer.toString().equals("D")) {
                                        dm5dBuffer.delete(0, dm5dBuffer.length());
                                        dm5d = false;
                                        start4 = false;
                                    }
                                } else if (dm5dBuffer.length() == 2) {
                                    //||!builder.toString().equals("FE")
                                    if (!dm5dBuffer.toString().equals("DM")) {
                                        dm5dBuffer.delete(0, dm5dBuffer.length());
                                        dm5d = false;
                                        start4 = false;
                                    }
                                }else if (dm5dBuffer.length() == 3) {
                                    //||!builder.toString().equals("FE")
                                    if (!dm5dBuffer.toString().equals("DM5")) {
                                        dm5dBuffer.delete(0, dm5dBuffer.length());
                                        dm5d = false;
                                        start4 = false;
                                    }
                                }else if (dm5dBuffer.length() == 4) {
                                    //||!builder.toString().equals("FE")
                                    if (!dm5dBuffer.toString().equals("DM5D")) {
                                        dm5dBuffer.delete(0, dm5dBuffer.length());
                                        dm5d = false;
                                        start4 = false;
                                    }
                                }
                                if (dm5dBuffer.length() > 4 && (dm5d && dm5dBuffer.substring(dm5dBuffer.length() - 1).equals("T"))) {
                                    dm5d = false;
                                    start4 = false;
                                    Log.i(DM5D, dm5dBuffer.toString());
                                    getDM5D(dm5dBuffer.toString());
                                    dm5dBuffer.delete(0, dm5dBuffer.length());
                                }

                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        thread3.start();
    }

    Timer weaTimer;
/*

    private void weaTimer() {
        weaTimer = new Timer();
        weaTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                String result = null;
                Request request = new Request.Builder()
                        .url(UrlList.dayurl)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        result = response.body().string();
                        JSONObject json = new JSONObject(result);
                        String Info = json.optString("DATE");
                        String dayinfo = json.optString("wea_txt1");
                        // site_name = json.optString("wea_logo");
                        if (!TextUtils.isEmpty(Info)) {
                            EventBus.getDefault().post(dayinfo);
                            LiveDataBus.getInstance().setWeaInfo(dayinfo);
                            try {
                                Thread.sleep(60 * 60 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.i("TAG", "获取天气数据失败");
                    }
                } catch (Exception e) {
                    // Log.i("TAG", "网络异常");
                }

            }
        }, 0, 3000);
    }
*/

    String s2 = "";
    ArrayList<Byte> byteArrayList = new ArrayList<>();
    static int ii = 0;
    boolean PmFlag = false;

    public void bindCardSystemUartAidl() {
        Intent intent = new Intent("xixun.intent.action.UART_SERVICE");
        intent.setPackage("com.xixun.joey.cardsystem");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    ArrayList<Integer> dmgdIndex = new ArrayList<Integer>();
    ArrayList<Integer> dm1dIndex = new ArrayList<Integer>();
    ArrayList<Integer> dm5dIndex = new ArrayList<Integer>();
    ArrayList<Integer> dmrdIndex = new ArrayList<Integer>();
    public int getDMGDCharCount(String chars) {
        if (TextUtils.isEmpty(chars)) {
            return 0;
        }
        dmgdIndex.clear();
        char[] chars1 = chars.toCharArray();
        int count = 0;
        for (int j = 0; j < chars1.length; j++) {
            if (chars1[j] == '1') {
                count++;
            }
            if (j == 0 || j == 1 || j == 12 || j == 14 || j == 15 || j == 17 || j == 20 || j == 21 || j == 25 || j == 55) {
                dmgdIndex.add(count - 1);
            }
        }
        return count;
    }
    //水温度52 水盐度58 导电率 60 水溶氧 64 ph 68

    public int getDM1DCharCount(String chars) {
        if (TextUtils.isEmpty(chars)) {
            return 0;
        }
        dm1dIndex.clear();
        char[] chars1 = chars.toCharArray();
        int count = 0;
        for (int j = 0; j < chars1.length; j++) {
            if (chars1[j] == '1') {
                count++;
            }
            if (j ==52 || j == 58 || j == 60 || j == 64 || j == 68) {
                dm1dIndex.add(count - 1);
            }
        }
        return count;
    }
    //总辐射 0 紫外线34
    public int getDMRDCharCount(String chars) {
        if (TextUtils.isEmpty(chars)) {
            return 0;
        }
        dmrdIndex.clear();
        char[] chars1 = chars.toCharArray();
        int count = 0;
        for (int j = 0; j < chars1.length; j++) {
            if (chars1[j] == '1') {
                count++;
            }
            if (j == 0||j == 34 ) {
                dmrdIndex.add(count - 1);
            }
        }
        return count;
    }
    //黑球 0，湿球温度 5 人体 10
    public int getDM5DCharCount(String chars) {
        if (TextUtils.isEmpty(chars)) {
            return 0;
        }
        dm5dIndex.clear();
        char[] chars1 = chars.toCharArray();
        int count = 0;
        for (int j = 0; j < chars1.length; j++) {
            if (chars1[j] == '1') {
                count++;
            }
            if (j == 0 ||j == 5 || j == 10) {
                dm5dIndex.add(count - 1);
            }
        }
        return count;
    }
    //风向
    String fx = "--";//1
    //风速
    String fs = "--";//2
    //降水
    String js = "--";//13
    //温度
    String wd = "--";
    //日最大温度
    String max_wd = "--";
    //日最小温度
    String min_wd = "--";
    //湿度
    String sd = "--";//21
    //日最小湿度
    String min_sd = "--";
    //气压
    String qy = "--";//26
    //能见度
    String njd = "--";//26
    static String WEA;
    String port = "/dev/ttyMT3";//,/dev/ttysWK2   /dev/ttyMT3
    //判断是否重启,每十分钟判断一次sendCount与currentCount的值，如果两者相等就重起；
    int sendCount = 0, currentCount = -1;
    //j ==52 || j == 58 || j == 60 || j == 64 || j == 68
    //水温度52 水盐度58 导电率 60 水溶氧 64 ph 68
    String swd,syd,ddl,sry,ph;
   public void getDM1D(String info){
       if (TextUtils.isEmpty(info)) {
           return;
       }
       if (info.startsWith("DM1D") && (info.endsWith("F") || info.endsWith("T"))) {

           String[] infoss = info.split(" ");
           for (int i = 0; i < infoss.length; i++) {
               Log.i("TAG_uart", i + ":" + infoss[i]);
           }
           //日期
           String date = infoss[2];
           Log.i("TAG", "日期:" + date);
           //时间
           String time = infoss[3];
           Log.i("TAG", "时间:" + time);
           int count = getDM1DCharCount(infoss[4]);
           int qc = 5;
           if (infoss[5].length() >= 4) {
               qc = 6;
           }
           try {
               if (infoss[4].charAt(52) == '1') {
                   String swd1 = infoss[qc + dm1dIndex.get(0)];
                   Log.i("TAG", "swd:" + swd1);

                   if (!isNum(swd1)) {
                       //fs = "";
                   } else {
                       swd = Float.parseFloat(infoss[qc + dm1dIndex.get(0)]) / 10 + "";
                   }
               }
           } catch (Exception e) {
               Log.i("TAG_", "解析swd时出错");
           }
           try {
               if (infoss[4].charAt(58) == '1') {
                   String syd1 = infoss[qc + dm1dIndex.get(1)];
                   Log.i("TAG", "syd:" + syd1);

                   if (!isNum(syd1)) {
                       //fs = "";
                   } else {
                       syd = Double.parseDouble(infoss[qc + dm1dIndex.get(1)]) / 100 + "";
                   }
               }
           } catch (Exception e) {
               Log.i("TAG_", "解析风速时出错");
           }

           try {
               if (infoss[4].charAt(60) == '1') {
                   String syd1 = infoss[qc + dm1dIndex.get(2)];
                   Log.i("TAG", "ddl:" + syd1);

                   if (!isNum(syd1)) {
                       //fs = "";
                   } else {
                       ddl = Double.parseDouble(infoss[qc + dm1dIndex.get(2)]) / 100 + "";
                   }
               }
           } catch (Exception e) {
               Log.i("TAG_", "解析雨量时出错");
           }

           try {

                   if (infoss[4].charAt(64) == '1') {
                       String syd1 = infoss[qc + dm1dIndex.get(3)];
                       Log.i("TAG", "sry:" + syd1);

                       if (!isNum(syd1)) {
                           //fs = "";
                       } else {
                           sry = Float.parseFloat(infoss[qc + dm1dIndex.get(3)]) / 10 + "";
                       }
                   }

           } catch (Exception e) {
               Log.i("TAG_", "解析温度时出错");
           }
           try {

                   if (infoss[4].charAt(68) == '1') {
                       String syd1 = infoss[qc + dm1dIndex.get(4)];
                       Log.i("TAG", "ph:" + syd1);

                       if (!isNum(syd1)) {
                           //fs = "";
                       } else {
                           ph = Float.parseFloat(infoss[qc + dm1dIndex.get(4)]) / 10 + "";
                       }
                   }

           } catch (Exception e) {
               Log.i("TAG_", "解析温度时出错");
           }

            EventBus.getDefault().post(new Dm1d(swd,syd,ddl,sry,ph));
       }

   }
    //总辐射 0 紫外线34
    String zfs,zwx;
    public void getDMRD(String info){
        if (TextUtils.isEmpty(info)) {
            return;
        }
        if (info.startsWith("DMRD") && (info.endsWith("F") || info.endsWith("T"))) {

            String[] infoss = info.split(" ");
            for (int i = 0; i < infoss.length; i++) {
                Log.i(DMRD, i + ":" + infoss[i]);
            }
            //日期
            String date = infoss[2];
            Log.i(DMRD, "日期:" + date);
            //时间
            String time = infoss[3];
            Log.i(DMRD, "时间:" + time);
            int count = getDMRDCharCount(infoss[4]);
            int qc = 5;
            if (infoss[5].length() >= 4) {
                qc = 6;
            }
            try {
                if (infoss[4].charAt(0) == '1') {
                    String swd1 = infoss[qc + dmrdIndex.get(0)];
                    Log.i(DMRD, "zfs:" + swd1);

                    if (!isNum(swd1)) {
                        //fs = "";
                    } else {
                        zfs = Float.parseFloat(infoss[qc + dmrdIndex.get(0)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                Log.i(DMRD, "解析swd时出错");
            }
            try {
                if (infoss[4].charAt(34) == '1') {
                    String syd1 = infoss[qc + dmrdIndex.get(1)];
                    Log.i(DMRD, "zwx:" + syd1);

                    if (!isNum(syd1)) {
                        //fs = "";
                    } else {
                        zwx = Float.parseFloat(infoss[qc + dmrdIndex.get(1)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                Log.i(DMRD, "解析风速时出错");
            }


            // LiveDataBus.getInstance().setElements(new Elements("语溪小学气象站", date, time, wd, max_wd, min_wd, sd, min_sd, fx, fs, js, qy, njd));
            EventBus.getDefault().post(new Dmrd(zfs,zwx));

        }

    }
    //黑球 0，湿球温度 5 人体 10
    String hqwd,sqwd,rtgz;
    public void getDM5D(String info){
        if (TextUtils.isEmpty(info)) {
            return;
        }
        if (info.startsWith("DM5D") && (info.endsWith("F") || info.endsWith("T"))) {

            String[] infoss = info.split(" ");
            for (int i = 0; i < infoss.length; i++) {
                Log.i(DM5D, i + ":" + infoss[i]);
            }
            //日期
            String date = infoss[2];
            Log.i(DM5D, "日期:" + date);
            //时间
            String time = infoss[3];
            Log.i(DM5D, "时间:" + time);
            int count = getDM5DCharCount(infoss[4]);
            int qc = 5;
            if (infoss[5].length() >= 4) {
                qc = 6;
            }
            try {
                if (infoss[4].charAt(0) == '1') {
                    String swd1 = infoss[qc + dm5dIndex.get(0)];
                    Log.i(DM5D, "hqwd:" + swd1);
                   /* if (!isNum(swd1)) {
                        //fs = "";
                    } else {
                        hqwd = Float.parseFloat(infoss[qc + dm5dIndex.get(0)]) / 10 + "";
                    }*/
                    if (swd1.charAt(0) == '/') {
                        //wd = " ";
                    } else if (swd1.charAt(0) == '-') {
                        if (swd1.length() <= 3) {
                            swd1 = swd1.substring(1, swd1.length());
                            if (isNum(swd1) && Float.parseFloat(swd1) < 580) {
                                hqwd = "-" + Float.parseFloat(swd1) / 10;
                            }
                        }
                    } else {
                        if (isNum(infoss[qc + dm5dIndex.get(0)]) && Float.parseFloat(infoss[qc + dm5dIndex.get(0)]) < 580) {
                            hqwd = Float.parseFloat(infoss[qc + dm5dIndex.get(0)]) / 10 + "";
                        }
                    }
                }
            } catch (Exception e) {
                Log.i(DM5D, "解析swd时出错");
            }
            try {
                if (infoss[4].charAt(5) == '1') {
                    String syd1 = infoss[qc + dm5dIndex.get(1)];
                    Log.i(DM5D, "syd:" + syd1);
                    if (syd1.charAt(0) == '/') {
                        //wd = " ";
                    } else if (syd1.charAt(0) == '-') {
                        if (syd1.length() <= 3) {
                            syd1 = syd1.substring(1, syd1.length());
                            if (isNum(syd1) && Float.parseFloat(syd1) < 580) {
                                sqwd = "-" + Float.parseFloat(syd1) / 10;
                            }
                        }
                    } else {
                        if (isNum(infoss[qc + dm5dIndex.get(1)]) && Float.parseFloat(infoss[qc + dm5dIndex.get(1)]) < 580) {
                            sqwd = Float.parseFloat(infoss[qc + dm5dIndex.get(1)]) / 10 + "";
                        }
                    }
                  /*  if (!isNum(syd1)) {
                        //fs = "";
                    } else {
                        sqwd = Float.parseFloat(infoss[qc + dm5dIndex.get(1)]) / 10 + "";
                    }*/
                }
            } catch (Exception e) {
                Log.i(DM5D, "解析风速时出错");
            }

            try {
                if (infoss[4].charAt(10) == '1') {
                    String syd1 = infoss[qc + dm5dIndex.get(2)];
                    Log.i(DM5D, "rtgz:" + syd1);
                    if (syd1.charAt(0) == '/') {
                        //wd = " ";
                    } else if (syd1.charAt(0) == '-') {
                        if (syd1.length() <= 3) {
                            syd1 = syd1.substring(1, syd1.length());
                            if (isNum(syd1) && Float.parseFloat(syd1) < 580) {
                                rtgz = "-" + Float.parseFloat(syd1) / 10;
                            }
                        }
                    } else {
                        if (isNum(infoss[qc + dm5dIndex.get(2)]) && Float.parseFloat(infoss[qc + dm5dIndex.get(2)]) < 580) {
                            rtgz = Float.parseFloat(infoss[qc + dm5dIndex.get(2)]) / 10 + "";
                        }
                    }
                  /*  if (!isNum(syd1)) {
                        //fs = "";
                    } else {
                        rtgz = Float.parseFloat(infoss[qc + dm5dIndex.get(2)]) / 10 + "";
                    }*/
                }
            } catch (Exception e) {
                Log.i(DM5D, "解析雨量时出错");
            }



            // LiveDataBus.getInstance().setElements(new Elements("语溪小学气象站", date, time, wd, max_wd, min_wd, sd, min_sd, fx, fs, js, qy, njd));
            EventBus.getDefault().post(new Dm5d(rtgz,sqwd,hqwd));

        }

    }
    public void getDMGD(String info) {

        Log.i(DMGD, "getElements:");
        if (TextUtils.isEmpty(info)) {
            return;
        }
        if (info.startsWith("DMGD") && (info.endsWith("F") || info.endsWith("T"))) {

            String[] infoss = info.split(" ");
            for (int i = 0; i < infoss.length; i++) {
                Log.i(DMGD, i + ":" + infoss[i]);
            }
            //日期
            String date = infoss[2];
            Log.i(DMGD, "日期:" + date);
            //时间
            String time = infoss[3];
            Log.i(DMGD, "时间:" + time);
            int count = getDMGDCharCount(infoss[4]);
            int qc = 5;
            if (infoss[5].length() >= 4) {
                qc = 6;
            }
            try {
                if (infoss[4].charAt(0) == '1') {
                    String fx1 = infoss[qc + dmgdIndex.get(0)];
                    Log.i(DMGD, "风向:" + fx1);
                    if (isNum(fx1)) {
                        float f = Float.valueOf(fx1);
                        if ((f >= 0 && f < 12.25) || (f > 348.76 && f <= 360)) {
                            fx = "北";
                        } else if (f > 12.26 && f < 33.75) {//22.5
                            fx = "北偏东北";
                        } else if (f > 33.76 && f < 56.25) {
                            fx = "东北";
                        } else if (f > 56.25 && f < 78.75) {
                            fx = "东偏东北";
                        } else if (f > 78.75 && f < 101.25) {
                            fx = "东";
                        } else if (f > 101.25 && f < 123.75) {
                            fx = "东偏东南";
                        } else if (f > 123.76 && f < 146.25) {
                            fx = "东南";
                        } else if (f > 146.26 && f < 168.75) {
                            fx = "南偏东南";
                        } else if (f > 168.75 && f < 191.25) {
                            fx = "南";
                        } else if (f > 191.25 && f < 213.75) {
                            fx = "南偏西南";
                        } else if (f > 213.75 && f < 236.25) {
                            fx = "西南";
                        } else if (f > 236.25 && f < 258.75) {
                            fx = "西偏西南";
                        } else if (f > 258.75 && f < 281.25) {
                            fx = "西";
                        } else if (f > 281.25 && f < 303.75) {
                            fx = "西偏西北";
                        } else if (f > 303.75 && f < 326.25) {
                            fx = "西北";
                        } else if (f > 326.25 && f < 348.75) {
                            fx = "北偏西北";
                        }
                    }
                }
            } catch (Exception e) {
                Log.i(DMGD, "解析风向时出错");
            }
            try {
                if (infoss[4].charAt(1) == '1') {
                    String fs1 = infoss[qc + dmgdIndex.get(1)];
                    Log.i(DMGD, "风速:" + fs1);
                    if (!isNum(fs1)) {
                        //fs = "";
                    } else {
                        fs = Float.parseFloat(infoss[qc + dmgdIndex.get(1)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                Log.i(DMGD, "解析风速时出错");
            }

            try {
                if (infoss[4].charAt(12) == '1') {
                    String js1 = infoss[qc + dmgdIndex.get(2)];
                    Log.i(DMGD, "降水:" + js1);
                    if (!isNum(js1)) {
                        // js = "";
                    } else {
                        js = Float.parseFloat(infoss[qc + dmgdIndex.get(2)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                Log.i(DMGD, "解析雨量时出错");
            }

            try {
                if (infoss[4].charAt(14) == '1') {
                    String wd1 = infoss[qc + dmgdIndex.get(3)];
                    if (wd1.charAt(0) == '/') {
                        //wd = " ";
                    } else if (wd1.charAt(0) == '-') {
                        if (wd1.length() <= 3) {
                            wd1 = wd1.substring(1, wd1.length());
                            if (isNum(wd1) && Float.parseFloat(wd1) < 580) {
                                wd = "-" + Float.parseFloat(wd1) / 10;
                            }
                        }
                    } else {
                        if (isNum(infoss[qc + dmgdIndex.get(3)]) && Float.parseFloat(infoss[qc + dmgdIndex.get(3)]) < 580) {
                            wd = Float.parseFloat(infoss[qc + dmgdIndex.get(3)]) / 10 + "";
                        }
                    }
                    Log.i(DMGD, "温度:" + wd);
                }
            } catch (Exception e) {
                Log.i(DMGD, "解析温度时出错");
            }
            try {
                if (infoss[4].charAt(15) == '1') {
                    String max_wd1 = infoss[qc + dmgdIndex.get(4)];
                    if (max_wd1.charAt(0) == '/') {
                        //wd = " ";
                    } else if (max_wd1.charAt(0) == '-') {
                        if (max_wd1.length() <= 3) {
                            max_wd1 = max_wd1.substring(1, max_wd1.length());
                            if (isNum(max_wd1) && Float.parseFloat(max_wd1) < 580) {
                                max_wd = "-" + Float.parseFloat(max_wd1) / 10;
                            }
                        }
                    } else {
                        if (isNum(infoss[qc + dmgdIndex.get(4)]) && Float.parseFloat(infoss[qc + dmgdIndex.get(4)]) < 580) {
                            max_wd = Float.parseFloat(infoss[qc + dmgdIndex.get(4)]) / 10 + "";
                        }
                    }
                    Log.i(DMGD, "日最高温度:" + max_wd);
                }
            } catch (Exception e) {
                Log.i(DMGD, "解析温度时出错");
            }
            try {
                if (infoss[4].charAt(17) == '1') {
                    String min_wd1 = infoss[qc + dmgdIndex.get(5)];
                    if (min_wd1.charAt(0) == '/') {
                        //wd = " ";
                    } else if (min_wd1.charAt(0) == '-') {
                        if (min_wd1.length() <= 3) {
                            min_wd1 = min_wd1.substring(1, min_wd1.length());
                            if (isNum(min_wd1) && Float.parseFloat(min_wd1) < 580) {
                                min_wd = "-" + Float.parseFloat(min_wd1) / 10;
                            }
                        }
                    } else {
                        if (isNum(infoss[qc + dmgdIndex.get(5)]) && Float.parseFloat(infoss[qc + dmgdIndex.get(5)]) < 580) {
                            min_wd = Float.parseFloat(infoss[qc + dmgdIndex.get(5)]) / 10 + "";
                        }
                    }
                    Log.i(DMGD, "日最低温度:" + min_wd);
                }
            } catch (Exception e) {
                Log.i(DMGD, "解析温度时出错");
            }
            try {
                if (infoss[4].charAt(20) == '1') {
                    String sd1 = infoss[qc + dmgdIndex.get(6)];
                    Log.i(DMGD, "湿度:" + sd1);
                    if (!isNum(sd1)) {
                        //sd = "";
                    } else {
                        sd = sd1;
                    }
                }
            } catch (Exception e) {
                Log.i(DMGD, "解析湿度时出错");
            }
            try {
                if (infoss[4].charAt(21) == '1') {
                    String min_sd1 = infoss[qc + dmgdIndex.get(7)];
                    Log.i("TAG", "湿度:" + min_sd1);
                    if (!isNum(min_sd1)) {
                        //sd = "";
                    } else {
                        min_sd = min_sd1;
                    }
                }
            } catch (Exception e) {
                Log.i(DMGD, "解析湿度时出错");
            }
            try {
                if (infoss[4].charAt(25) == '1') {
                    String qy1 = infoss[qc + dmgdIndex.get(8)];
                    Log.i(DMGD, "气压:" + qy1);
                    if (!isNum(qy1)) {
                        //qy = "";
                    } else {
                        qy = Float.parseFloat(infoss[qc + dmgdIndex.get(8)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                Log.i(DMGD, "解析气压时出错");
            }
            try {
                if (infoss[4].charAt(55) == '1') {
                    //能见度
                    String njd1 = "";
                    njd1 = infoss[qc + dmgdIndex.get(9)];
                    Log.i(DMGD, "能见度:" + njd1);
                    if (!isNum(njd1)) {
                        //njd = "12345";
                    } else {
                        njd = njd1;
                    }

                }
            } catch (Exception e) {
                Log.i(DMGD, "解析能见度时出错");
            }
            // LiveDataBus.getInstance().setElements(new Elements("语溪小学气象站", date, time, wd, max_wd, min_wd, sd, min_sd, fx, fs, js, qy, njd));
            EventBus.getDefault().post(new Dmgd(wd,sd,fs,fx,qy,js,njd));
            sendCount++;
        }

    }

    private Timer timerListener;


    public boolean isNum(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) str);
        boolean result = matcher.matches();
        return result;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unbindService(conn);
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        super.onDestroy();
    }

    public String stringToGbk(String string) throws Exception {
        byte[] bytes = new byte[string.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            byte high = Byte.parseByte(string.substring(i * 2, i * 2 + 1), 16);
            byte low = Byte.parseByte(string.substring(i * 2 + 1, i * 2 + 2), 16);
            bytes[i] = (byte) (high << 4 | low);
        }
        String result = new String(bytes, "gbk");
        return result;
    }

    private void restartApp() {
        Log.i("TAG_Service", "重启app");
       /* Intent activity = new Intent(getApplicationContext(), MainActivity.class);
        activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(activity);*/
        Intent i = getApplicationContext().getPackageManager()
                .getLaunchIntentForPackage(getApplicationContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
        // android.os.Process.killProcess(Process.myPid());
    }

   /* private void reboot() {
        Log.i("TAG_", "reboot（）");
        new Thread(new Runnable() {
            @Override
            public void run() {
                CardService cardService = null;
                try {
                    cardService = (CardService) new AidlUtils
                            .Builder(getApplicationContext())
                            .setAction("com.xixun.joey.aidlset.SettingsService")
                            .setToPackageName("com.xixun.joey.cardsystem")
                            .setClazz(CardService.class)
                            .build()
                            .getObject();
                    while (cardService == null) {
                        Thread.sleep(1000);
                        Log.i("TAG_", "cardService == null");
                    }
                    try {
                        Log.i("TAG_", "一秒后重启");
                        cardService.reboot(1);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/
}