package com.example.lenovo.project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class alarmList_Activity extends AppCompatActivity {
    private myDB database = new myDB(this, "SQL.db", null, 7);
    private SimpleAdapter simpleAdapter;
    private ArrayList<Map<String,Object>> alarm_dataList=new ArrayList<>();
    private ListView listView_alarm;
    private TextView add_alarm;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            Bundle b=data.getExtras();
            if(b!=null &&( b.getString("type")!=null  && b.getString("type").equals("insert"))){//添加栏
                Map<String,Object> temp=new LinkedHashMap<>();
                String hour=(b.getInt("hour")<10)?"0"+b.getInt("hour"):""+b.getInt("hour");
                String minute=(b.getInt("minute")<10)?"0"+b.getInt("minute"):""+b.getInt("minute");
                String time=hour+":"+minute;
                String repeats=b.getString("repeatTimes");
                temp.put("time",time);
                temp.put("repeats",repeats);
                if(b.getString("state")!=null && b.getString("state").equals("checked")){
                    temp.put("state","开");
                }else if(b.getString("state").equals("")){
                    temp.put("state","关");
                }
                alarm_dataList.add(temp);
                simpleAdapter.notifyDataSetChanged();
            }else if(b!=null && b.getString("type").equals("update")){
                Map<String,Object> temp=new LinkedHashMap<>();
                String hour=(b.getInt("hour")<10)?"0"+b.getInt("hour"):""+b.getInt("hour");
                String minute=(b.getInt("minute")<10)?"0"+b.getInt("minute"):""+b.getInt("minute");
                String time=hour+":"+minute;
                String repeats=b.getString("repeatTimes");
                temp.put("time",time);
                temp.put("repeats",repeats);
                if(b.getString("state").equals("checked")){
                    temp.put("state","开");
                }else if(b.getString("state").equals("")){
                    temp.put("state","关");
                }
                int position=b.getInt("position");
                alarm_dataList.set(position,temp);
                simpleAdapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_alarm);
        getID();
        SQLiteDatabase db=database.getWritableDatabase();
        final Cursor cursor=db.query("alarm",null,null,null,null,null,null);
        while(cursor.moveToNext()){
            String hour=formal_time(cursor,"hour");
            String minute= formal_time(cursor,"minute");
            String time=hour+":"+minute;
            String repeats=cursor.getString(cursor.getColumnIndex("repeatTimes"));
            String state=cursor.getString(cursor.getColumnIndex("state"));
            Map<String,Object> temp=new HashMap<>();
            temp.put("time",time);//时间
            temp.put("repeats",repeats);//重复次数
            if(state.equals("checked")){
                temp.put("state","开");
            }else{
                temp.put("state","关");
            }
            alarm_dataList.add(temp);
        }
        cursor.close();
        simpleAdapter=new SimpleAdapter(this,alarm_dataList,R.layout.item_alarm,new String[]{"time","repeats","state"},new int[]{R.id.time,R.id.repeat,R.id.state});
        listView_alarm.setAdapter(simpleAdapter);
        listView_alarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(alarmList_Activity.this,alarmActivity.class);
                Bundle b=new Bundle();
                b.putString("operation","update");
                b.putInt("len_edit",position);//位置
                intent.putExtras(b);
                startActivityForResult(intent,1);
            }
        });
        listView_alarm.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //更新数据库数据
                SQLiteDatabase db=database.getWritableDatabase();
                Cursor cursor1=db.query("alarm",null,null,null,null,null,null);
                while(cursor1.moveToNext()){
                    String hour=formal_time(cursor1,"hour");
                    String minute= formal_time(cursor1,"minute");
                    String time=hour+":"+minute;
                    String repeats=cursor1.getString(cursor1.getColumnIndex("repeatTimes"));
                    String state=cursor1.getString(cursor1.getColumnIndex("state"));
                    Map<String,Object> temp=new HashMap<>();
                    temp.put("time",time);//时间
                    temp.put("repeats",repeats);//重复次数
                    if(state.equals("checked")){
                        temp.put("state","开");
                    }else{
                        temp.put("state","关");
                    }
                    if(alarm_dataList.get(position).equals(temp)){
                        db.delete("alarm", "alarm_name = ?", new String[]{cursor1.getString(cursor1.getColumnIndex("alarm_name"))});
                        db.close();
                        break;
                    }
                }
                cursor1.close();
                alarm_dataList.remove(position);
                simpleAdapter.notifyDataSetChanged();
                return true;
            }
        });

        add_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(alarmList_Activity.this,alarmActivity.class);
                Bundle b=new Bundle();
                b.putString("operation","insert");
                int adapter_size=simpleAdapter.getCount();b.putInt("len_add",adapter_size);//适配器数目的长度
                intent.putExtras(b);
                startActivityForResult(intent,2);
            }
        });

    }
    public void getID(){
        listView_alarm=(ListView)findViewById(R.id.listView_alarm);
        add_alarm=(TextView)findViewById(R.id.add_alarm);
    }
    public String formal_time(Cursor cursor,String str){
        return (cursor.getInt(cursor.getColumnIndex(str))<10)?"0"+cursor.getInt(cursor.getColumnIndex(str)):""+cursor.getInt(cursor.getColumnIndex(str));
    }
}
