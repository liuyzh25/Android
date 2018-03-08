package com.example.lenovo.project;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import java.util.Calendar;

public class memoActivity extends AppCompatActivity{
    private TextView event,unsave,save,details,title;
    private myDB database = new myDB(this, "SQL.db", null, 7);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memorandum);
        getID();
        Intent i=getIntent();
        final Bundle data=i.getExtras();
        if(data!=null){
            event.setText(data.getString("event"));
            details.setText(data.getString("details"));
            title.setText("编辑事项");
            unsave.setText("编辑");save.setVisibility(View.GONE);
            event.setFocusableInTouchMode(false);event.setFocusable(false);
            details.setFocusableInTouchMode(false);details.setFocusable(false);
        }
        else{
            title.setText("新建事项");
            unsave.setText("取消");
            event.setFocusableInTouchMode(true);event.setFocusable(true);
            details.setFocusableInTouchMode(true);details.setFocusable(true);
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c=Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                int year=c.get(Calendar.YEAR),month=c.get(Calendar.MONTH)+1,day=c.get(Calendar.DAY_OF_MONTH);
                String  hour=format(c.get(Calendar.HOUR_OF_DAY)),minute=format(c.get(Calendar.MINUTE)),second=format(c.get(Calendar.SECOND));
                String time=year+"/"+format(month)+"/"+format(day)+"  "+hour+":"+minute+":"+second;
                String event_str=event.getText().toString(),details_str=details.getText().toString();
                if(event_str.isEmpty()){
                    event.setError("标题栏不能为空");
                }
                else{
                    ContentValues values=new ContentValues();
                    values.put("event",event_str);
                    values.put("details",details_str);
                    SQLiteDatabase db=database.getWritableDatabase();
                    if(data!=null){//更新
                        db.update("memory",values,"time = ?",new String[]{data.getString("time")});
                        values.clear();
                        values.put("time",time);
                        db.update("memory",values,"event= ? and details = ?",new String[]{event_str,details_str});
                        db.close();
                        Intent in=new Intent();
                        Bundle b=new Bundle();
                        b.putString("time",time);b.putString("event",event_str);b.putString("details",details_str);b.putInt("position",data.getInt("position")+1);
                        in.putExtras(b);setResult(RESULT_OK,in);
                        finish();
                    }else {//添加
                        values.put("time",time);
                        db.insert("memory",null,values);
                        db.close();
                        Intent in=new Intent();
                        Bundle b=new Bundle();
                        b.putString("time",time);b.putString("event",event_str);b.putString("details",details_str);
                        in.putExtras(b);setResult(RESULT_OK,in);
                        finish();
                    }
                }
            }
        });
        unsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data!=null){
                    event.setFocusableInTouchMode(true);event.setFocusable(true);
                    details.setFocusableInTouchMode(true);details.setFocusable(true);
                    details.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager)details.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(details, 0);
                    save.setVisibility(View.VISIBLE);unsave.setVisibility(View.GONE);
                    return ;
                }
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(memoActivity.this);
                alertDialog.setTitle("放弃更改").setPositiveButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("继续编辑",null).create().show();
            }
        });
    }
    public void getID(){
        title=findViewById(R.id.title);
        event=findViewById(R.id.event);
        details=findViewById(R.id.details);
        unsave=findViewById(R.id.unsave);
        save=findViewById(R.id.save);
    }
    public String format(int time){
        return time<10?("0"+time) : (""+time);
    }
}
