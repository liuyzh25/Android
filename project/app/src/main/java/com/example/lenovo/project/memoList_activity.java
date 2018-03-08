package com.example.lenovo.project;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class memoList_activity extends AppCompatActivity{
    private TextView add_memo;
    private ListView list_memo;
    //数据库
    private myDB database = new myDB(this, "SQL.db", null, 7);
    private ArrayList<Map<String,Object>> data_memo=new ArrayList<>();
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            Bundle b=data.getExtras();
            if(b!=null){
                Map<String,Object> temp=new HashMap<>();
                temp.put("time",b.getString("time"));
                if(b.getString("event").length()>=9){
                    temp.put("event",b.getString("event").substring(0,9)+"...");
                }else{
                    temp.put("event",b.getString("event"));
                }
                if(b.getInt("position")>0){
                    data_memo.set(b.getInt("position")-1,temp);
                }else{
                    data_memo.add(temp);
                }
                simpleAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_memo);
        add_memo=findViewById(R.id.add_memo);
        list_memo=findViewById(R.id.list_memo);

        SQLiteDatabase db=database.getWritableDatabase();
        Cursor cursor=db.query("memory",null,null,null,null,null,null);
        while(cursor.moveToNext()){
            String time=cursor.getString(cursor.getColumnIndex("time"));
            String event=cursor.getString(cursor.getColumnIndex("event"));
            Map<String,Object> temp=new HashMap<>();
            temp.put("time",time);
            if(event.length()>=9){
                temp.put("event",event.substring(0,9)+"...");
            }else{
                temp.put("event",event);
            }
            data_memo.add(temp);
        }
        cursor.close();db.close();
        simpleAdapter=new SimpleAdapter(this,data_memo,R.layout.memo_info,new String[]{"event","time"},new int[]{R.id.event,R.id.memo_time});
        list_memo.setAdapter(simpleAdapter);
        list_memo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b=new Bundle();
                SQLiteDatabase db=database.getWritableDatabase();
                Cursor cursor=db.query("memory",null,null,null,null,null,null);
                while(cursor.moveToNext()){
                    String time=cursor.getString(cursor.getColumnIndex("time"));
                    String event=cursor.getString(cursor.getColumnIndex("event"));
                    Map<String,Object> temp=new HashMap<>();
                    temp.put("time",time);
                    if(event.length()>=9){
                        temp.put("event",event.substring(0,9)+"...");
                    }else{
                        temp.put("event",event);
                    }
                    if(data_memo.get(position).equals(temp)){
                        b.putString("event",event);
                        b.putString("time",time);
                        b.putString("details",cursor.getString(cursor.getColumnIndex("details")));
                        b.putInt("position",position);
                        break;
                    }
                }
                cursor.close();db.close();
                Intent intent =new Intent(memoList_activity.this,memoActivity.class);
                intent.putExtras(b);
                startActivityForResult(intent,5);
            }
        });
        /****************删除******************/
        list_memo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase db=database.getWritableDatabase();
                Cursor cursor=db.query("memory",null,null,null,null,null,null);
                while(cursor.moveToNext()){
                    String time=cursor.getString(cursor.getColumnIndex("time"));
                    String event=cursor.getString(cursor.getColumnIndex("event"));
                    Map<String,Object> temp=new HashMap<>();
                    temp.put("time",time);
                    if(event.length()>=9){
                        temp.put("event",event.substring(0,9)+"...");
                    }else{
                        temp.put("event",event);
                    }
                    if(data_memo.get(position).equals(temp)){
                        SQLiteDatabase db1=database.getWritableDatabase();
                        db1.delete("memory","time = ?",new String[]{time});
                        Toast.makeText(memoList_activity.this, event+"事项已被删除", Toast.LENGTH_SHORT).show();
                        db1.close();
                        break;
                    }
                }
                cursor.close();db.close();
                data_memo.remove(position);
                simpleAdapter.notifyDataSetChanged();
                return true;
            }
        });
        /**************添加***************/
        add_memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(memoList_activity.this,memoActivity.class);
                startActivityForResult(intent,4);
            }
        });

    }
}
