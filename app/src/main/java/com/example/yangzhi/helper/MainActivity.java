package com.example.yangzhi.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;


import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private WebView mWebView;
    private WebSettings mWebSetting;
    private String webUrl;
    private int pageNum;
    private String webPage;
    private ProgressDialog progressDialog;
    private int addNum;
    private long days;
    private String usageDays;
    private int usageIntDays;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //获取一个系统时间
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        //声明这个webview
        mWebView = (WebView)findViewById(R.id.mwebView);
        mWebSetting = mWebView.getSettings();
        mWebSetting.setJavaScriptEnabled(true);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1",
                    "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            notificationManager.createNotificationChannel(channel);
        }


        //存入取出数据



        //存入数据
        Map<String,String> map = new HashMap<String,String>();
        map.put("date","660");
        map.put("days","1");
        saveSettingNote(MainActivity.this, "date_info",map);

        //取出数据
        webPage = getSettingNote(MainActivity.this,"date_info","date");
        //取出数据days
        usageDays=getSettingNote(MainActivity.this,"date_info","days");
        pageNum = Integer.parseInt(webPage);
        usageIntDays = Integer.parseInt(usageDays);

        //计算时间天数差
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date endDate = new Date(System.currentTimeMillis());

        try
        {
            Date beginDate = df.parse("2018-06-26 00:00:00");
            String t=df.format(new Date());
            Date endDate1 = df.parse(t);
            long diff = endDate.getTime() - beginDate.getTime();//这样得到的差值是微秒级别

            days = diff / (1000 * 60 * 60 * 24);

        }
        catch (Exception e)
        {
        }
        //计算需要增加的天数页面

        addNum = (int)days/7;

        pageNum = pageNum+addNum;

        webUrl = "https://bookfere.com/post/"+pageNum+".html";





        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                showProgress("客官请稍等，内容马上呈现");//开始加载动画
            }



            //屏蔽页面里其他内容
            @Override
            public void onPageFinished(WebView view, String url) {
                removeProgress();
                view.loadUrl("javascript:(function(){" +
                                "document.getElementsByClassName('con-bottom')[0].style.display='none'; "+
                                "document.getElementsByClassName('relate-content')[0].style.display='none'; "+
                                "document.getElementsByClassName('navigation post-navigation')[0].style.display='none'; "+
                                "document.getElementsByClassName('entry-meta entry-footer')[0].style.display='none'; "+
                                "document.getElementById('site-navigation').style.display = 'none';"+
                                "document.getElementsByClassName('site-branding')[0].style.display='none'; "+
                                "document.getElementsByClassName('con-top clear')[0].style.display='none'; "+
                                "document.getElementById('site-tools').style.display = 'none';"+
                                "document.getElementById('secondary').style.display = 'none';"+
                                "document.getElementsByClassName('store-recommend-bottom')[0].style.display='none'; "+
                                "document.getElementsByClassName('current_nav')[0].style.display='none'; "+
                                "document.getElementsByClassName('shareto')[0].style.display='none'; "+
                                "document.getElementById('respond').style.display = 'none';"+
                                "document.getElementsByClassName('site-info')[0].style.display='none';"+
                        "})()");
            }



        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "打卡成功！！已经使用"+usageIntDays+"天了", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mWebView.loadUrl(webUrl);


            sendNotification();

    }




// 显示过度动画
    public void showProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);//设置点击不消失
        }
        if (progressDialog.isShowing()) {
            progressDialog.setMessage(message);
        } else {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }


    public void removeProgress(){
        if (progressDialog==null){
            return;
        }
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }




        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //点击返回上一页面而不退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //销毁这个webView
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
    //把需要保存的数据保存到map中去
    public static void saveSettingNote(Context context, String filename , Map<String, String> map) {
        SharedPreferences.Editor note = context.getSharedPreferences(filename, Context.MODE_PRIVATE).edit();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            note.putString(entry.getKey(), entry.getValue());
        }
        note.commit();
    }

    //取出map中的数据
    public static String getSettingNote(Context context,String filename ,String dataname) {
        SharedPreferences read = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return read.getString(dataname, null);
    }

  //广播内容
  public void sendNotification() {
      NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      Notification notification = new NotificationCompat.Builder(this, "1")
              .setContentTitle("DAY提醒您！！")
              .setContentText("十一点记得碎叫O！！")
              .setWhen(System.currentTimeMillis())
              .setSmallIcon(R.drawable.remind)
              .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.remind))
              .setAutoCancel(true)
              .build();
      manager.notify(1, notification);
  }



    }
