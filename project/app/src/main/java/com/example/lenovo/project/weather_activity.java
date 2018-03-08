package com.example.lenovo.project;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class weather_activity extends AppCompatActivity {
    //  定义我们需要用到的 WebService 地址
    private static final String url = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";
    //  定义消息类型
    private static final int UPDATE_CONTENT = 123;
    //  初始化对象
    EditText searchET;
    Button searchBTN;
    TextView city_name,time;
    LinearLayout LL,layout;
    TextView lowestTem,tem,wet,airQ,wind;
    ListView listview;
    RecyclerView recyclerView;
    TextView five_weather;
    //  设置时间对象，用以判断二次查询间隔是否小于600ms
    long Time1 = -1000;
    long Time2 = -1000;
    LinearLayout weather_main;
    SimpleAdapter myAdapter;
    WeatherAdapter myAdapter2;

    ArrayList<Map<String, Object>> data_list;
    ArrayList<Weather> weather_list = new ArrayList<>();

    String searchCity = "";
    String thelastCity = "";
    String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getID();
        //   ListView 的设置
        data_list = new ArrayList<>();
        myAdapter = new SimpleAdapter(this, data_list, R.layout.item, new String[]{"theType", "theContent"},
                new int[]{R.id.theType, R.id.theContent});
        listview.setAdapter(myAdapter);

        //   RecylerView 的设置
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        //设置不可见
        five_weather.setVisibility(View.GONE);
        weather_main.getBackground().setAlpha(250);
        //  搜索按钮点击事件
        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Time2 = Time1;
                Time1 = (new Date()).getTime();
                if (Time1 - Time2 < 600) {
                    Toast.makeText(weather_activity.this, "您的点击速度过快，两次查询间隔须小于600ms", Toast.LENGTH_SHORT).show();
                    return;
                }
                searchCity = searchET.getText().toString();
                if (searchCity.equals("")) {
                    Toast.makeText(weather_activity.this, "查询栏输入为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (searchCity.equals(thelastCity)) return;
                LL.setVisibility(View.VISIBLE);
                listview.setVisibility(View.VISIBLE);

                //  判断是否有可用网络：使用 ConnectivityManager 获取手机所有连接管理对象，使用 manager 获取 NetworkInfo 对象，最后判断当前网络状态是否为连接状态即可。
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if ((networkInfo == null) || !networkInfo.isConnected()) {
                    Toast.makeText(weather_activity.this, "当前没有可用网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                data_list.clear();
                myAdapter.notifyDataSetChanged();
                sendRequestWithHttpURLConnection();
            }
        });
    }

    //  http 请求需要开启子线程，然后由子线程执行请求，所以我们之前所写代码都是在子线程中完成的，并且使用 XmlPullParser 进行解析从而得到我们想要的数
    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //  使用 HttpURLConnection 新建一个 http 连接，新建一个 URL 对象，打开连接即可，并且设置访问方法以及时间设置
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) ((new URL(url).openConnection()));
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setRequestMethod("POST");

                    //  将我们需要请求的字段以流的形式写入 connection 之中，这一步相当于将需要的参数提交到网络连接，并且请求网络数据（类似于 html 中的表单操作，将 post 数据提交到服务器）
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes("theCityCode=" + URLEncoder.encode(searchCity, "utf-8") + "&theUserID=");

                    //  网页获取 xml 转化为字符串
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }

                    //  Message消息传递
                    Message message = new Message();
                    message.what = UPDATE_CONTENT;
                    message.obj = parseXMLWithPull(response.toString());
                    handler.sendMessage(message);
                } catch (Exception e) {

                    e.printStackTrace();
                } finally {
                    //  关闭 connection
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    //  子线程中不能直接修改 UI 界面，需要 handler 进行UI 界面的修改
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_CONTENT:
                    data_list.clear();
                    myAdapter.notifyDataSetChanged();
                    List<String> data = (List<String>) message.obj;
                    temp = data.get(0);
                    if (temp.contains("查询结果为空")) {
                        Toast.makeText(weather_activity.this, "当前输入城市不存在，请重新输入", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (temp.contains("高速访问")) {
                        Toast.makeText(weather_activity.this, "您的点击速度过快，两次查询间隔须小于600ms~", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (temp.contains("规定数量")) {
                        Toast.makeText(weather_activity.this, "免费用户24小时内访问超过规定数量50次啦~", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (data.size() >= 2) {
                        weather_main.getBackground().setAlpha(20);
                        LL.setVisibility(View.VISIBLE);
                        layout.setVisibility(View.VISIBLE);
                        city_name.setText(data.get(1));
                        thelastCity = data.get(1);
                        temp = data.get(3);
                        int spaceIndex = temp.indexOf(" ");
                        temp = temp.substring(spaceIndex + 1) + "更新";
                        time.setText(temp);
                        temp = data.get(4);

                        //  不断的将所需要的字符串进行分割处理，以用来更新 UI 界面
                        StringTokenizer stringTokenizer = new StringTokenizer(temp, "：；:;");
                        stringTokenizer.nextToken();
                        temp = stringTokenizer.nextToken();
                        if (temp.contains("暂无实况")) {
                            lowestTem.setText("暂无实况");
                            temp = data.get(5);
                            stringTokenizer = new StringTokenizer(temp, "：；:;");
                            stringTokenizer.nextToken();
                            temp = stringTokenizer.nextToken();
                            airQ.setText(temp);
                            Map<String, Object> map = new HashMap<>();
                            map.put("theType", "");
                            map.put("theContent", data.get(6));
                            data_list.add(map);
                            myAdapter.notifyDataSetChanged();
                        } else {
                            temp = stringTokenizer.nextToken();
                            lowestTem.setText(temp);
                            stringTokenizer.nextToken();
                            temp = stringTokenizer.nextToken();
                            wind.setText(temp);
                            stringTokenizer.nextToken();
                            temp = "湿度：" + stringTokenizer.nextToken();
                            wet.setText(temp);
                            temp = data.get(5);
                            stringTokenizer = new StringTokenizer(temp, "。");
                            stringTokenizer.nextToken();
                            temp = stringTokenizer.nextToken();
                            airQ.setText(temp);
                            temp = data.get(6);
                            stringTokenizer = new StringTokenizer(temp, ":：.。\n");
                            while (stringTokenizer.hasMoreElements()) {
                                Map<String, Object> map = new HashMap<>();
                                temp = stringTokenizer.nextToken();
                                map.put("theType", temp);
                                if (stringTokenizer.hasMoreElements()) {
                                    temp = stringTokenizer.nextToken();
                                    map.put("theContent", temp);
                                    data_list.add(map);
                                }
                            }
                            myAdapter.notifyDataSetChanged();

                            //  RecyclerView的数据获取和设置
                            weather_list.clear();
                            String first_day = (data).get(7);
                            String[] tag_first = first_day.split("[ ]");
                            String second_day = (data).get(12);
                            String[] tag_second = second_day.split("[ ]");
                            String third_day = (data).get(17);
                            String[] tag_third = third_day.split("[ ]");
                            String fourth_day = (data).get(22);
                            String[] tag_fourth = fourth_day.split("[ ]");
                            String fif_day = (data).get(27);
                            String[] tag_fif = fif_day.split("[ ]");

                            //  设置三个字符串组将五天的天气信息添加到其中
                            String[] date = new String[]{tag_first[0], tag_second[0], tag_third[0], tag_fourth[0], tag_fif[0]};
                            String[] weather_d = new String[]{tag_first[1], tag_second[1], tag_third[1], tag_fourth[1], tag_fif[1]};
                            String[] tempurature = new String[]{(data).get(8), (data).get(13),
                                    (data).get(18), (data).get(23), (data).get(28)};

                            //  按照顺序将五天的信息添加进 weather_list 中去
                            for (int i = 0; i < 5; i++) {
                                Weather temp2 = new Weather(date[i], weather_d[i], tempurature[i]);
                                temp2.date = date[i];
                                temp2.weather_description = weather_d[i];
                                temp2.temperature = tempurature[i];
                                weather_list.add(temp2);
                            }
                            myAdapter2 = new WeatherAdapter(weather_activity.this, weather_list);
                            recyclerView.setAdapter(myAdapter2);
                            myAdapter2.notifyDataSetChanged();
                            five_weather.setVisibility(View.VISIBLE);
                        }
                        tem.setText(data.get(8));
                    } else {

                    }
                    break;
                default:
                    break;
            }
        }
    };
    public void getID(){
        searchET = (EditText) findViewById(R.id.searchET);
        searchBTN = (Button) findViewById(R.id.search);
        listview = (ListView) findViewById(R.id.list);
        LL = (LinearLayout) findViewById(R.id.LL);
        layout = (LinearLayout) findViewById(R.id.layout);
        weather_main=findViewById(R.id.weather_main);
        city_name = (TextView) findViewById(R.id.city_name);
        time = (TextView) findViewById(R.id.time);
        lowestTem = (TextView) findViewById(R.id.lowestTem);
        tem = (TextView) findViewById(R.id.tem);
        wet = (TextView) findViewById(R.id.wet);
        airQ = (TextView) findViewById(R.id.airQ);
        wind = (TextView) findViewById(R.id.wind);
        five_weather = (TextView)findViewById(R.id.five_weather);
    }
    public ArrayList<String> parseXMLWithPull(String xml) throws XmlPullParserException, IOException {
        //  首先获取 XmlPullParser 对象实例，然后设置需要解析的字符串，最后按照 tag 逐个获取所需要的 string
        //  获取实例
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();

        //  设置所需要解析的string
        parser.setInput(new StringReader(xml));

        int eventType = parser.getEventType();
        ArrayList<String> list = new ArrayList<>();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("string".equals(parser.getName())) {
                        String str = parser.nextText();
                        list.add(str);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return list;
    }
}




