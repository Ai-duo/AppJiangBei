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
      /*  final String in1 = "DMGD WXNo1 2018-11-12 16:23 1111111111111000000000000000001111111111111111111111000000000000000110000000000000000000000000000001100000000000000000000000000000000000000 242 4 242 6 174 11 1607 233 3 166 12 1601 0 119 124 1611 118 1622 120 123 1611 120 1622 84 89 1611 84 1622 135 137 136 136 138 141 143 11 23////////////////////00000000000011000000000000 244 5 *4T";
        final  String in = "DMSD WXNo1 2018-11-12 16:23 111111110 29 49 36 48 12 30 98 122 *66";
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getElements(in1);
               *//* try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getTuRangInfo(in);*//*
            }
        }, 0, 5000);*/
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
    }

    Timer timer, timer1, timer2;
    int index1 =0;

    public void changFragment() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                if (index1 ==40000) index1 = 0;
                int result = index1 % 4;
                EventBus.getDefault().post(result);
                if (result == 0) {
                    transaction.replace(R.id.contenter, firstFragment);
                    transaction.commit();
                } else if (result == 1) {
                    transaction.replace(R.id.contenter, secondFragment);
                    transaction.commit();
                } else if (result == 2) {
                    transaction.replace(R.id.contenter, thirdFragment);
                    transaction.commit();
                } else if (result == 3) {

                    transaction.replace(R.id.contenter, fourthFragment);
                    transaction.commit();
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (result == 4) {
                    transaction.replace(R.id.contenter, fifthFragment);
                    transaction.commit();
                }else if (result == 5) {
                    transaction.replace(R.id.contenter, imgFragment);
                    transaction.commit();
                    try {
                        Thread.sleep(25*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (result == 6) {
                    transaction.replace(R.id.contenter, videoFragment);
                    transaction.commit();
                    try {
                        Thread.sleep(204*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

               index1++;
            }
        }, 0, 6 * 1000);

    }



    public void updateInfo(){
        timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
               // final Request live = new Request.Builder().url("http://115.220.4.68:8081/qxdata/QxService.svc/getnewzdzhourdata/K2160").build();
                Request zhish = new Request.Builder().url("http://115.220.4.68:8081/qxdata/QxService.svc/gettszsybdata/K2160").build();

                Request wea = new Request.Builder().url("http://115.220.4.68:8081/qxdata/QxService.svc/getdayybdata/K2160").build();
                Request seven = new Request.Builder().url("http://115.220.4.68:8081/qxdata/QxService.svc/get7dayybdata/K2160").build();

               /* client.newCall(live).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        if (TextUtils.isEmpty(body) || body.length() < 5) return;
                        Gson gson = new Gson();
                        Live lives = gson.fromJson(body, Live.class);
                        EventBus.getDefault().post(lives);
                    }
                });*/
                client.newCall(zhish).enqueue(new Callback() {
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

                });
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
        Log.i("TAG", "?????????" + index.toString());
       mainBinding.setUpdate(index.time);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateWea(Wea index) {
        Log.i("TAG", "?????????" + index.toString());
        fourthFragment.updateText(index.wea_txt1);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateSeven(SevenWea index) {
        Log.i("TAG", "?????????" + index.toString());
        thirdFragment.updateInfo(index);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateZhishu(Zhishu index) {
        Log.i("TAG", "?????????" + index.toString());
        secondFragment.updateInfo(index);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateLive(Live live) {
        mainBinding.setLive(live);
        firstFragment.updateInfo(live);
    }

    public TimerTask dates;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy???M???dd??? HH:mm");

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
        Log.i("TAG", "?????????" + bean);
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
                    Log.i("TAG_uart", "????????????uart======================");
                } while (null == uart);
                try {
                    //??????/dev/ttyMT2???????????????/dev/s3c2410_serial3
                    uart.read(port, new IUartListener.Stub() {
                        @Override
                        public void onReceive(BytesData data) throws RemoteException {
                            Log.i("TAG_uart", "========?????????????????????===========");

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
    //??????
    String fx = "--";//1
    //??????
    String fs = "--";//2
    //??????
    String js = "--";//13
    //??????
    String wd = "--";
    //???????????????
    String max_wd = "--";
    //???????????????
    String min_wd = "--";
    //??????
    String sd = "--";//21
    //???????????????
    String min_sd = "--";
    //??????
    String qy = "--";//26
    //?????????
    String njd = "--";//26
    //????????????
    String gcyw = "--";
    //??????5cm
    String dw5 = "--";
    //??????10cm
    String dw10 = "--";
    //??????15cm
    String dw15 = "--";
    //??????20cm
    String dw20 = "--";

    //??????5cm
    String  sf5 = "--";
    //??????10cm
    String sf10 = "--";
    //??????15cm
    String sf20 = "--";
    //??????20cm
    String sf30 = "--";

    static String WEA;
    String port = "/dev/ttyMT3";//,/dev/ttysWK2   /dev/ttyMT3 /dev/ttysWK0
    //??????????????????,????????????????????????sendCount???currentCount???????????????????????????????????????
    int sendCount = 0, currentCount = -1;
    public void getElements(String info) {
        //builder.delete(0, builder.length());
        //????????????PM2.5??????
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
            //??????
            String date = infoss[2];
            Log.i("TAG", "??????:" + date);
            //??????
            String time = infoss[3];
            Log.i("TAG", "??????:" + time);
            int count = getCharCount(infoss[4]);
            int qc = 5;
            if (infoss[5].length() >= 4) {
                qc = 6;
            }
            try {
                if (infoss[4].charAt(0) == '1') {
                    String fx1 = infoss[qc + index.get(0)];
                    Log.i("TAG", "??????:" + fx1);
                    if (isNum(fx1)) {
                        float f = Float.valueOf(fx1);
                        if ((f >= 0 && f < 12.25) || (f > 348.76 && f <= 360)) {
                            fx = "???";
                        } else if (f > 12.26 && f < 33.75) {//22.5
                            fx = "????????????";
                        } else if (f > 33.76 && f < 56.25) {
                            fx = "??????";
                        } else if (f > 56.25 && f < 78.75) {
                            fx = "????????????";
                        } else if (f > 78.75 && f < 101.25) {
                            fx = "???";
                        } else if (f > 101.25 && f < 123.75) {
                            fx = "????????????";
                        } else if (f > 123.76 && f < 146.25) {
                            fx = "??????";
                        } else if (f > 146.26 && f < 168.75) {
                            fx = "????????????";
                        } else if (f > 168.75 && f < 191.25) {
                            fx = "???";
                        } else if (f > 191.25 && f < 213.75) {
                            fx = "????????????";
                        } else if (f > 213.75 && f < 236.25) {
                            fx = "??????";
                        } else if (f > 236.25 && f < 258.75) {
                            fx = "????????????";
                        } else if (f > 258.75 && f < 281.25) {
                            fx = "???";
                        } else if (f > 281.25 && f < 303.75) {
                            fx = "????????????";
                        } else if (f > 303.75 && f < 326.25) {
                            fx = "??????";
                        } else if (f > 326.25 && f < 348.75) {
                            fx = "????????????";
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("TAG_", "?????????????????????");
            }
            try {
                if (infoss[4].charAt(1) == '1') {
                    String fs1 = infoss[qc + index.get(1)];
                    Log.i("TAG", "??????:" + fs1);
                    if (!isNum(fs1)) {
                        //fs = "";
                    } else {
                        fs = Float.parseFloat(infoss[qc + index.get(1)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("TAG_", "?????????????????????");
            }

            try {
                if (infoss[4].charAt(12) == '1') {
                    String js1 = infoss[qc + index.get(2)];
                    Log.i("TAG", "??????:" + js1);
                    if (!isNum(js1)) {
                        // js = "";
                    } else {
                        js = Float.parseFloat(infoss[qc + index.get(2)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("TAG_", "?????????????????????");
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
                    Log.i("TAG", "??????:" + wd);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("TAG_", "?????????????????????");
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
                    Log.i("TAG", "???????????????:" + max_wd);
                }
            } catch (Exception e) {
                Log.i("TAG_", "?????????????????????");
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
                    Log.i("TAG", "???????????????:" + min_wd);
                }
            } catch (Exception e) {
                Log.i("TAG_", "?????????????????????");
            }
            try {
                if (infoss[4].charAt(20) == '1') {
                    String sd1 = infoss[qc + index.get(6)];
                    Log.i("TAG", "??????:" + sd1);
                    if (!isNum(sd1)) {
                        //sd = "";
                    } else {
                        sd = sd1;
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "?????????????????????");
            }
            try {
                if (infoss[4].charAt(21) == '1') {
                    String min_sd1 = infoss[qc + index.get(7)];
                    Log.i("TAG", "????????????:" + min_sd1);
                    if (!isNum(min_sd1)) {
                        //sd = "";
                    } else {
                        min_sd = min_sd1;
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "???????????????????????????");
            }
            try {
                if (infoss[4].charAt(25) == '1') {
                    String qy1 = infoss[qc + index.get(8)];
                    Log.i("TAG", "??????:" + qy1);
                    if (!isNum(qy1)) {
                        //qy = "";
                    } else {
                        qy = Float.parseFloat(infoss[qc + index.get(8)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "?????????????????????");
            }
            try {
                if (infoss[4].charAt(41) == '1') {
                    String gc = infoss[qc + index.get(10)];
                    Log.i("TAG", "????????????:" + gc);
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
                Log.i("TAG_", "????????????");
            }
            try {
                if (infoss[4].charAt(35) == '1') {
                    String gc = infoss[qc + index.get(9)];
                    Log.i("TAG", "??????5cm:" + gc);
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
                Log.i("TAG_", "????????????5cm?????????");
            }
            try {
                if (infoss[4].charAt(45) == '1') {
                    String gc = infoss[qc + index.get(11)];
                    Log.i("TAG", "??????10cm:" + gc);
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
                Log.i("TAG_", "????????????10cm?????????");
            }
            try {
                if (infoss[4].charAt(46) == '1') {
                    String gc = infoss[qc + index.get(12)];
                    Log.i("TAG", "??????15cm:" + gc);
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
                Log.i("TAG_", "????????????15cm?????????");
            }
            try {
                if (infoss[4].charAt(47) == '1') {
                    String gc = infoss[qc + index.get(13)];
                    Log.i("TAG", "??????20cm:" + gc);
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
                Log.i("TAG_", "????????????20cm?????????");
            }

            try {
                if (infoss[4].charAt(55) == '1') {
                    //?????????
                    String njd1 = "";
                    njd1 = infoss[qc + index.get(14)];
                    Log.i("TAG", "?????????:" + njd1);
                    if (!isNum(njd1)) {
                        //njd = "12345";
                    } else {
                        njd = njd1;
                    }

                }
            } catch (Exception e) {
                Log.i("TAG_", "????????????????????????");
            }

            // LiveDataBus.getInstance().setElements(new Elements(1, date, time, wd,  sd, gcyw,dw5,dw10,dw15,dw20));
           // LiveDataBus.getInstance().setElements(new Elements( date, time, wd, fx,fs,js));
            EventBus.getDefault().post(new Live(date+" "+time,wd+"???",sd+"%",fs+"m/s",fx,js+"mm",qy+"pha"));
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