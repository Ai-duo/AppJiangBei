package com.add.appxm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.add.appxm.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.xixun.joey.uart.BytesData;
import com.xixun.joey.uart.IUartListener;
import com.xixun.joey.uart.IUartService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.add.appxm.BR.backIndex;
import static com.add.appxm.BR.dm5d;
import static com.add.appxm.BR.dmrd;
import static com.add.appxm.BR.live;
import static com.add.appxm.BR.wea;

public class MainActivity extends FragmentActivity {
    ActivityMainBinding mainBinding;
    FirstFragment firstFragment;
    SecondFragment secondFragment;
    ThirdFragment thirdFragment;
    FourthFragment fourthFragment;
    FifthFragment fifthFragment;
    ImgFragment imgFragment;
    VideoFragment videoFragment;
    OxyFragment oxyFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        updateInfo();
        Log.i("TAG", "onCreate");
        startService();
        initFragment();
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initTask();
        bindCardSystemUartAidl();
        startGetUart();
        changFragment();
    }
    public void bindCardSystemUartAidl() {
        Intent intent = new Intent("xixun.intent.action.UART_SERVICE");
        intent.setPackage("com.xixun.joey.cardsystem");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public void startService() {
        Intent intent = new Intent(this, ElementsService.class);
        startService(intent);
    }

    public void initFragment() {
        firstFragment = new FirstFragment();
        secondFragment = new SecondFragment();
        thirdFragment = new ThirdFragment();
        fourthFragment = new FourthFragment();
        fifthFragment = new FifthFragment();
        imgFragment = new ImgFragment();
        videoFragment = new VideoFragment();
        oxyFragment = new OxyFragment();
    }

    Timer timer, timer1, timer2;
    int index1 =0;
    int count = 30000;
    int c = 3;
    public void changFragment() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                if(fifthFragment.isShow){
                    count = 40000;
                    c = 4;
                }else{
                    count = 30000;
                    c = 3;
                }
                if (index1 ==count) index1 = 0;
                int result = index1 % c;
                EventBus.getDefault().post(result);
                if (result == 0) {
                    transaction.replace(R.id.contenter, firstFragment);
                    transaction.commit();
                } else if (result == 1) {
                    transaction.replace(R.id.contenter, thirdFragment);
                    transaction.commit();
                } else if (result == 2) {
                    transaction.replace(R.id.contenter, oxyFragment);
                    transaction.commit();
                }else if (result == 3) {
                    transaction.replace(R.id.contenter, fifthFragment);
                    transaction.commit();
                }

               index1++;
            }
        }, 0, 10 * 1000);

    }



    public void updateInfo(){
        timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
               // final Request live = new Request.Builder().url("http://115.220.4.68:8081/qxdata/QxService.svc/getnewzdzhourdata/K2160").build();
                Request elememt = new Request.Builder().url("http://61.153.246.242:8888/qxdata/QxService.svc/getnewzdzhourdata/58751").build();
                Request oxy = new Request.Builder().url("http://115.220.4.68:8081/qxdata/QxService.svc/getqxo2data/KL001").build();
                Request wea = new Request.Builder().url("http://61.153.246.242:8888/qxdata/QxService.svc/getdayybdata/58751").build();
                Request alarm = new Request.Builder().url("http://61.153.246.242:8888/qxdata/QxService.svc/getqxalarmdata/58751").build();
                Request seven = new Request.Builder().url("http://61.153.246.242:8888/qxdata/QxService.svc/get7dayybdata/58751").build();

                client.newCall(elememt).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        Log.i("TAG", "body:" + body);
                        if (TextUtils.isEmpty(body) || body.length() < 5) return;
                        Gson gson = new Gson();
                        Live lives = gson.fromJson(body, Live.class);
                        EventBus.getDefault().post(lives);
                    }

                });

                client.newCall(oxy).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        body = body.replace("负氧离子浓度:","");
                        Log.i("TAG", "body:" + body);
                        if (TextUtils.isEmpty(body) || body.length() < 5) return;
                        Gson gson = new Gson();
                        Oxy lives = gson.fromJson(body, Oxy.class);
                        EventBus.getDefault().post(lives);
                    }

                });
                client.newCall(alarm).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        if (TextUtils.isEmpty(body) || body.length() < 5) return;
                        Gson gson = new Gson();
                        Alarm lives = gson.fromJson(body, Alarm.class);
                        EventBus.getDefault().post(lives);
                    }

                });
              /*  client.newCall(zhish).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        if (TextUtils.isEmpty(body) || body.length() < 5) return;
                        Gson gson = new Gson();
                        Zhishu lives = gson.fromJson(body, Zhishu.class);
                        EventBus.getDefault().post(lives);
                    }

                });*/
                client.newCall(wea).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        if (TextUtils.isEmpty(body) || body.length() < 5) return;
                        Gson gson = new Gson();
                        Wea lives = gson.fromJson(body, Wea.class);
                        EventBus.getDefault().post(lives);
                    }

                });
                client.newCall(seven).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        Log.i("TAG","body:"+body);
                        if (TextUtils.isEmpty(body) || body.length() < 5) return;
                        Gson gson = new Gson();
                        SevenWea lives = gson.fromJson(body, SevenWea.class);
                        lives.build();
                        Log.i("TAG",lives.toString());
                        EventBus.getDefault().post(lives);
                    }

                });
            }
        }, 0, 5 * 60 * 1000);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changBackImage(Integer index) {
       // mainBinding.setBackIndex(index);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateTime(UpdateTime  index) {
        Log.i("TAG", "收到：" + index.toString());
       mainBinding.setUpdate(index.time);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateWea(Wea index) {
        Log.i("TAG", "收到：" + index.toString());
        mainBinding.setWea(index);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateSeven(SevenWea index) {
        Log.i("TAG", "收到：" + index.toString());
        thirdFragment.updateInfo(index);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateZhishu(Zhishu index) {
        Log.i("TAG", "收到：" + index.toString());
        secondFragment.updateInfo(index);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateLive(Live live) {
        mainBinding.setLive(live);
        firstFragment.updateInfo(live);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateOxy(Oxy live) {

        oxyFragment.updateInfo(live);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateOxy(Alarm alarm) {
        Log.i("TAG", "收到：预警");
        if(alarm==null)return;
        fifthFragment.updateText(alarm);
    }
    public TimerTask dates;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月dd日 HH:mm");

    public void initTask() {

        dates = new TimerTask() {
            @Override
            public void run() {
                Lunar lunar = new Lunar(Calendar.getInstance());
                EventBus.getDefault().post(dateFormat.format(new Date()) + "\n" + lunar.getAllDate());
            }
        };
        timer1 = new Timer();

        timer1.schedule(dates, 0, 40 * 1000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updataDate(String bean) {
        Log.i("TAG", "时间：" + bean);
        mainBinding.setDate(bean);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    public IUartService uart;
    StringBuffer builder = new StringBuffer();
    boolean start = false;
    boolean dmgd=false,weaf=false,timing=false,bright=false;
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

    Thread thread;
    private boolean openPm = true;

    private void startGetUart() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i("TAG_uart", "正在获取uart======================");
                } while (null == uart);
                try {
                    //监听/dev/ttyMT2，获取数据/dev/s3c2410_serial3
                    uart.read(port, new IUartListener.Stub() {
                        @Override
                        public void onReceive(BytesData data) throws RemoteException {
                            Log.i("TAG_uart", "========获取到串口数据===========");

                            for (byte a : data.getData()) {
                                String s1 = "0x" + Integer.toHexString(a & 0xFF) + " ";
                                char ss = (char) a;
                                Log.i("TAG_uart", "ss:" + ss + ";s1:" + s1);
                                if (ss == 'D') {
                                    dmgd = true;
                                    start = true;
                                    builder.append(ss);
                                } else if (ss == 'W') {
                                    weaf = true;
                                    start = true;
                                    builder.append(ss);
                                } else if (ss == 'O') {
                                    timing = true;
                                    start = true;
                                    builder.append(ss);
                                } else if (ss == 'B') {
                                    bright = true;
                                    start = true;
                                    builder.append(ss);
                                } else if (start) {
                                    start = true;
                                    builder.append(ss);
                                }
                                Log.i("TAG_uart123", builder.toString());
                                if (builder.length() == 1) {
                                    //||!builder.toString().equals("FE")
                                    if (!builder.toString().equals("D") && !builder.toString().equals("W") && !builder.toString().equals("O") && !builder.toString().equals("B")) {
                                        builder.delete(0, builder.length());
                                        dmgd = false;
                                        weaf = false;
                                        timing = false;
                                        bright = false;
                                        start = false;
                                    }
                                }
                                if (builder.length() == 2) {
                                    //||!builder.toString().equals("FE")
                                    if (!builder.toString().equals("DM") && !builder.toString().equals("WE") && !builder.toString().equals("ON") && !builder.toString().equals("BR")) {
                                        builder.delete(0, builder.length());
                                        dmgd = false;
                                        weaf = false;
                                        timing = false;
                                        bright = false;
                                        start = false;
                                    }
                                }
                                if (builder.length() == 4) {
                                    //||!builder.toString().equals("FE")
                                    if (!builder.toString().equals("DMGD") && !builder.toString().equals("WEAF") && !builder.toString().equals("ONOF") && !builder.toString().equals("BRIG")) {
                                        builder.delete(0, builder.length());
                                        dmgd = false;
                                        weaf = false;
                                        timing = false;
                                        bright = false;
                                        start = false;
                                    }
                                }
                                if (((dmgd && 'T' == ss) || (';' == ss && (weaf || timing || bright))) && builder.length() > 4) {
                                    dmgd = false;
                                    weaf = false;
                                    timing = false;
                                    bright = false;
                                    start = false;
                                    Log.i("TAG_uart1234", builder.toString());
                                    getElements(builder.toString());
                                    // OkHttpUploadTool.getInstance().uploadString(UrlList.sitenum, "info:" + builder.toString());
                                    builder.delete(0, builder.length());
                                    Log.i("TAG_uart1234", "END");
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
    ArrayList<Integer> index = new ArrayList<Integer>();
    ArrayList<Integer> index2 = new ArrayList<Integer>();
    public int getCharCount1(String chars) {
        if (TextUtils.isEmpty(chars)) {
            return 0;
        }
        index2.clear();
        char[] chars1 = chars.toCharArray();
        int count = 0;
        for (int j = 0; j < chars1.length; j++) {
            if (chars1[j] == '1') {
                count++;
            }
            if (j == 1 || j == 2 || j == 3 || j == 4 ) {
                index2.add(count - 1);
            }
        }
        return count;
    }
    public int getCharCount(String chars) {
        if (TextUtils.isEmpty(chars)) {
            return 0;
        }
        index.clear();
        char[] chars1 = chars.toCharArray();
        int count = 0;
        for (int j = 0; j < chars1.length; j++) {
            if (chars1[j] == '1') {
                count++;
            }
            if (j == 0 || j == 1 || j == 12 || j == 14 || j == 15 || j == 17 || j == 20 || j == 21 || j == 25 ||j==35||j==41
                    || j == 46 || j == 47 ||j==48|| j == 55) {
                index.add(count - 1);
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
    //冠层叶温
    String gcyw = "--";
    //地温5cm
    String dw5 = "--";
    //地温10cm
    String dw10 = "--";
    //地温15cm
    String dw15 = "--";
    //地温20cm
    String dw20 = "--";

    //水分5cm
    String  sf5 = "--";
    //水分10cm
    String sf10 = "--";
    //水分15cm
    String sf20 = "--";
    //水分20cm
    String sf30 = "--";

    static String WEA;
    String port = "/dev/ttyMT3";//,/dev/ttysWK2   /dev/ttyMT3 /dev/ttysWK0
    //判断是否重启,每十分钟判断一次sendCount与currentCount的值，如果两者相等就重起；
    int sendCount = 0, currentCount = -1;
    public void getElements(String info) {
        //builder.delete(0, builder.length());
        //开始接受PM2.5数据
        Log.i("TAG", "getElements:");

        if (TextUtils.isEmpty(info)) {
            return;
        }
        openPm = false;
        if (info.startsWith("DMGD") && (info.endsWith("F") || info.endsWith("T"))) {

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
            int count = getCharCount(infoss[4]);
            int qc = 5;
            if (infoss[5].length() >= 4) {
                qc = 6;
            }
            try {
                if (infoss[4].charAt(0) == '1') {
                    String fx1 = infoss[qc + index.get(0)];
                    Log.i("TAG", "风向:" + fx1);
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
                e.printStackTrace();
                Log.i("TAG_", "解析风向时出错");
            }
            try {
                if (infoss[4].charAt(1) == '1') {
                    String fs1 = infoss[qc + index.get(1)];
                    Log.i("TAG", "风速:" + fs1);
                    if (!isNum(fs1)) {
                        //fs = "";
                    } else {
                        fs = Float.parseFloat(infoss[qc + index.get(1)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("TAG_", "解析风速时出错");
            }

            try {
                if (infoss[4].charAt(12) == '1') {
                    String js1 = infoss[qc + index.get(2)];
                    Log.i("TAG", "降水:" + js1);
                    if (!isNum(js1)) {
                        // js = "";
                    } else {
                        js = Float.parseFloat(infoss[qc + index.get(2)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("TAG_", "解析雨量时出错");
            }

            try {
                if (infoss[4].charAt(14) == '1') {
                    String wd1 = infoss[qc + index.get(3)];
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
                        if (isNum(infoss[qc + index.get(3)]) && Float.parseFloat(infoss[qc + index.get(3)]) < 580) {
                            wd = Float.parseFloat(infoss[qc + index.get(3)]) / 10 + "";
                        }
                    }
                    Log.i("TAG", "温度:" + wd);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("TAG_", "解析温度时出错");
            }
            try {
                if (infoss[4].charAt(15) == '1') {
                    String max_wd1 = infoss[qc + index.get(4)];
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
                        if (isNum(infoss[qc + index.get(4)]) && Float.parseFloat(infoss[qc + index.get(4)]) < 580) {
                            max_wd = Float.parseFloat(infoss[qc + index.get(4)]) / 10 + "";
                        }
                    }
                    Log.i("TAG", "日最高温度:" + max_wd);
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析温度时出错");
            }
            try {
                if (infoss[4].charAt(17) == '1') {
                    String min_wd1 = infoss[qc + index.get(5)];
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
                        if (isNum(infoss[qc + index.get(5)]) && Float.parseFloat(infoss[qc + index.get(5)]) < 580) {
                            min_wd = Float.parseFloat(infoss[qc + index.get(5)]) / 10 + "";
                        }
                    }
                    Log.i("TAG", "日最低温度:" + min_wd);
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析温度时出错");
            }
            try {
                if (infoss[4].charAt(20) == '1') {
                    String sd1 = infoss[qc + index.get(6)];
                    Log.i("TAG", "湿度:" + sd1);
                    if (!isNum(sd1)) {
                        //sd = "";
                    } else {
                        sd = sd1;
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析湿度时出错");
            }
            try {
                if (infoss[4].charAt(21) == '1') {
                    String min_sd1 = infoss[qc + index.get(7)];
                    Log.i("TAG", "最小湿度:" + min_sd1);
                    if (!isNum(min_sd1)) {
                        //sd = "";
                    } else {
                        min_sd = min_sd1;
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析最小湿度时出错");
            }
            try {
                if (infoss[4].charAt(25) == '1') {
                    String qy1 = infoss[qc + index.get(8)];
                    Log.i("TAG", "气压:" + qy1);
                    if (!isNum(qy1)) {
                        //qy = "";
                    } else {
                        qy = Float.parseFloat(infoss[qc + index.get(8)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析气压时出错");
            }
            try {
                if (infoss[4].charAt(41) == '1') {
                    String gc = infoss[qc + index.get(10)];
                    Log.i("TAG", "冠层叶温:" + gc);
                    if (gc.charAt(0) == '/') {
                        //wd = " ";
                    } else if (gc.charAt(0) == '-') {
                        if (gc.length() <= 3) {
                            gc = gc.substring(1, gc.length());
                            if (isNum(gc) && Float.parseFloat(gc) < 580) {
                                gcyw = "-" + Float.parseFloat(gc) / 10;
                            }
                        }
                    } else {
                        if (isNum(gc) && Float.parseFloat(gc) < 580) {
                            gcyw = Float.parseFloat(gc) / 10 + "";
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "冠层叶温");
            }
            try {
                if (infoss[4].charAt(35) == '1') {
                    String gc = infoss[qc + index.get(9)];
                    Log.i("TAG", "地温5cm:" + gc);
                    if (gc.charAt(0) == '/') {
                        //wd = " ";
                    } else if (gc.charAt(0) == '-') {
                        if (gc.length() <= 3) {
                            gc = gc.substring(1, gc.length());
                            if (isNum(gc) && Float.parseFloat(gc) < 580) {
                                dw5 = "-" + Float.parseFloat(gc) / 10;
                            }
                        }
                    } else {
                        if (isNum(gc) && Float.parseFloat(gc) < 580) {
                            dw5 = Float.parseFloat(gc) / 10 + "";
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析地温5cm时出错");
            }
            try {
                if (infoss[4].charAt(45) == '1') {
                    String gc = infoss[qc + index.get(11)];
                    Log.i("TAG", "地温10cm:" + gc);
                    if (gc.charAt(0) == '/') {
                        //wd = " ";
                    } else if (gc.charAt(0) == '-') {
                        if (gc.length() <= 3) {
                            gc = gc.substring(1, gc.length());
                            if (isNum(gc) && Float.parseFloat(gc) < 580) {
                                dw10 = "-" + Float.parseFloat(gc) / 10;
                            }
                        }
                    } else {
                        if (isNum(gc) && Float.parseFloat(gc) < 580) {
                            dw10 = Float.parseFloat(gc) / 10 + "";
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析地温10cm时出错");
            }
            try {
                if (infoss[4].charAt(46) == '1') {
                    String gc = infoss[qc + index.get(12)];
                    Log.i("TAG", "地温15cm:" + gc);
                    if (gc.charAt(0) == '/') {
                        //wd = " ";
                    } else if (gc.charAt(0) == '-') {
                        if (gc.length() <= 3) {
                            gc = gc.substring(1, gc.length());
                            if (isNum(gc) && Float.parseFloat(gc) < 580) {
                                dw15 = "-" + Float.parseFloat(gc) / 10;
                            }
                        }
                    } else {
                        if (isNum(gc) && Float.parseFloat(gc) < 580) {
                            dw15 = Float.parseFloat(gc) / 10 + "";
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析地温15cm时出错");
            }
            try {
                if (infoss[4].charAt(47) == '1') {
                    String gc = infoss[qc + index.get(13)];
                    Log.i("TAG", "地温20cm:" + gc);
                    if (gc.charAt(0) == '/') {
                        //wd = " ";
                    } else if (gc.charAt(0) == '-') {
                        if (gc.length() <= 3) {
                            gc = gc.substring(1, gc.length());
                            if (isNum(gc) && Float.parseFloat(gc) < 580) {
                                dw20 = "-" + Float.parseFloat(gc) / 10;
                            }
                        }
                    } else {
                        if (isNum(gc) && Float.parseFloat(gc) < 580) {
                            dw20 = Float.parseFloat(gc) / 10 + "";
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析地温20cm时出错");
            }

            try {
                if (infoss[4].charAt(55) == '1') {
                    //能见度
                    String njd1 = "";
                    njd1 = infoss[qc + index.get(14)];
                    Log.i("TAG", "能见度:" + njd1);
                    if (!isNum(njd1)) {
                        //njd = "12345";
                    } else {
                        njd = njd1;
                    }

                }
            } catch (Exception e) {
                Log.i("TAG_", "解析能见度时出错");
            }

            // LiveDataBus.getInstance().setElements(new Elements(1, date, time, wd,  sd, gcyw,dw5,dw10,dw15,dw20));
           // LiveDataBus.getInstance().setElements(new Elements( date, time, wd, fx,fs,js));
            EventBus.getDefault().post(new Live(date+" "+time,wd+"℃",sd+"%",fs+"m/s",fx,js+"mm",qy+"pha"));
            sendCount++;
        }

    }
    public boolean isNum(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) str);
        boolean result = matcher.matches();
        return result;
    }
}