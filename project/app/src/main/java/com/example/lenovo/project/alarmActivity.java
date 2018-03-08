package com.example.lenovo.project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

public class alarmActivity extends AppCompatActivity{
    private TextView unsave,save,repeatTimes,music;
    private Switch state;
    private TimePicker timePicker;
    private TextView alarm_name;
    private Integer getHour=0,getMinute=0;
    private String repeat_str="只响一次";
    private myDB database = new myDB(this, "SQL.db", null, 7);
    private AlarmManager alarmManager;
    private Spinner problemNum;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_edit);
        getID();
        Intent i=getIntent();
        final Bundle data=i.getExtras();
        final String updateOrinsert=data.getString("operation");
        final String alarm_name_str_add="闹钟"+(data.getInt("len_add")+1);
        final String alarm_name_str_edit="闹钟"+(data.getInt("len_edit")+1);
        /***********************还原*************************/
        SQLiteDatabase db=database.getWritableDatabase();
        Cursor cursor=db.query("alarm",null,null,null,null,null,null);
        while(cursor.moveToNext()){
            String db_name=cursor.getString(cursor.getColumnIndex("alarm_name"));
            //说明是编辑栏的，不是添加栏的
            if(alarm_name_str_edit.equals(db_name) && updateOrinsert!=null && updateOrinsert.equals("update")){
                alarm_name.setText(alarm_name_str_edit);
                repeatTimes.setText(cursor.getString(cursor.getColumnIndex("repeatTimes")));
                repeat_str=cursor.getString(cursor.getColumnIndex("repeatTimes"));
                timePicker.setCurrentHour(cursor.getInt(cursor.getColumnIndex("hour")));
                timePicker.setCurrentMinute(cursor.getInt(cursor.getColumnIndex("minute")));
                if(cursor.getString(cursor.getColumnIndex("problemNum")).equals("10")){
                    problemNum.setSelection(0,true);
                }else if(cursor.getString(cursor.getColumnIndex("problemNum")).equals("15")){
                    problemNum.setSelection(1,true);
                }else if(cursor.getString(cursor.getColumnIndex("problemNum")).equals("20")){
                    problemNum.setSelection(2,true);
                }else if(cursor.getString(cursor.getColumnIndex("problemNum")).equals("25")){
                    problemNum.setSelection(3,true);
                }
                if(cursor.getString(cursor.getColumnIndex("state")).equals("checked")){
                    state.setChecked(true);
                }else{
                    state.setChecked(false);
                }
                break;
            }
        }
        cursor.close();
        //添加栏
        if(updateOrinsert!=null && updateOrinsert.equals("insert")){
            alarm_name.setText(alarm_name_str_add);
            repeatTimes.setText(repeat_str);
        }
        /************************编辑**********************************/
        getHour=timePicker.getCurrentHour();
        getMinute=timePicker.getCurrentMinute();
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                getHour=hourOfDay;
                getMinute=minute;
            }
        });
        repeatTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater=LayoutInflater.from(alarmActivity.this);
                View layout=inflater.inflate(R.layout.alarm_dialog_repeats,null);
                final RadioButton[] radioButtons={layout.findViewById(R.id.once),layout.findViewById(R.id.everyday),layout.findViewById(R.id.Mon_Fri),
                        layout.findViewById(R.id.Sat_Sun),layout.findViewById(R.id.optional)};
                final String[] str={"只响一次","每天","周一到周五","周六、周日","自定义"};
                final AlertDialog.Builder alertDialog=new AlertDialog.Builder(alarmActivity.this);
                alertDialog.setView(layout).setNegativeButton("取消",null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i=0;i<5;i++){
                            if(radioButtons[i].isChecked()){
                                repeat_str=str[i];
                                repeatTimes.setText(repeat_str);
                                break;
                            }
                        }
                    }
                }).create().show();
            }
        });

       save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               SQLiteDatabase db=database.getWritableDatabase();
               ContentValues values=new ContentValues();
               values.put("hour",getHour);
               values.put("minute",getMinute);
               values.put("repeatTimes",repeat_str);
               Intent intent=new Intent();
               Bundle b=new Bundle();
               b.putInt("hour",getHour);b.putInt("minute",getMinute);b.putString("repeatTimes",repeat_str);
               if(state.isChecked()){
                   values.put("state","checked");
                   b.putString("state","checked");
               }else{
                   values.put("state","");
                   b.putString("state","");
               }
               values.put("problemNum",problemNum.getSelectedItem().toString());
               if(updateOrinsert.equals("insert")) {
                   values.put("alarm_name",alarm_name_str_add);
                   db.insert("alarm", null, values);
                   b.putString("type","insert");
                   intent.putExtras(b);
                   setResult(RESULT_OK,intent);
               }else if(updateOrinsert.equals("update")){
                   db.update("alarm",values,"alarm_name = ?",new String[]{alarm_name_str_edit});
                   b.putInt("position",data.getInt("len_edit"));//通过id来确认位置
                   b.putString("type","update");
                   intent.putExtras(b);
                   setResult(RESULT_OK,intent);
               }
               db.close();
               /*****************开启闹钟服务******************/
               alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
               Intent intent1=new Intent(alarmActivity.this,RepeatingAlarm.class);
               intent1.setAction("com.example.alarm");
               Bundle problems=new Bundle();problems.putString("problemNum",problemNum.getSelectedItem().toString());
               intent1.putExtras(problems);
               PendingIntent pi=PendingIntent.getBroadcast(alarmActivity.this,getHour*100+getMinute,intent1,0);
               long firstTime = SystemClock.elapsedRealtime(); // 开机之后到现在的运行时间(包括睡眠时间)
               long systemTime = System.currentTimeMillis();
               if(state.isChecked()){
                   Calendar c= Calendar.getInstance();
                   c.setTimeInMillis(System.currentTimeMillis());
                   // 这里时区需要设置一下，不然会有8个小时的时间差
                   c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                   c.set(Calendar.HOUR_OF_DAY,getHour);
                   c.set(Calendar.MINUTE,getMinute);
                   c.set(Calendar.SECOND, 0);c.set(Calendar.MILLISECOND, 0);
                   // 选择的定时时间
                   long selectTime = c.getTimeInMillis();
                   // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
                   if(systemTime > selectTime) {
                       c.add(Calendar.DAY_OF_MONTH, 1);
                       selectTime = c.getTimeInMillis();
                   }
                   // 计算现在时间到设定时间的时间差
                   long time = selectTime - systemTime;
                   firstTime += time;
                   if(repeat_str.equals("只响一次")){
                       alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,firstTime,pi);

                   }else if(repeat_str.equals("每天")){
                       //此处有些不准确
                       alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firstTime,24*60*60*1000,pi);
                   }
               }else{
                   alarmManager.cancel(pi);
               }
               finish();
           }
       });
        unsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(alarmActivity.this);
                alertDialog.setTitle("放弃更改").setNegativeButton("否",null).setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
            }
        });
    }
    void getID(){
        unsave=findViewById(R.id.unsave);//x
        save=(TextView)findViewById(R.id.save);//勾
        repeatTimes=(TextView)findViewById(R.id.repeatTimes);//重复次数
        music=(TextView)findViewById(R.id.music);//铃声
        alarm_name=findViewById(R.id.alarm_name);//闹钟名
        timePicker=(TimePicker)findViewById(R.id.timePicker);//时间设置
        state=(Switch) findViewById(R.id.state);//开关
        problemNum=(Spinner)findViewById(R.id.problemNum);
    }
}
