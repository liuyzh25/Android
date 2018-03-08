package com.example.lenovo.project;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.Inflater;

public class passwordActivity extends AppCompatActivity {
    private ImageView edit;
    private FloatingActionButton add;
    private EditText name,phone,ID;
    private DatePicker datePicker;
    private Button save;
    private RadioGroup sex;
    private RadioButton mail,femail;
    private boolean save_flag=false;//初始不可编辑
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ListView list_pass,search_list_pass;
    private SimpleAdapter simpleAdapter,simpleAdapter_search;
    private ArrayList<Map<String,Object>> data_pass=new ArrayList<>();
    //pass_edit的EditText
    private EditText name_edit,account_edit,password_edit;
    private Button getPass;
    //随机生成密码
    private String pass_random="";
    //数据库
    private myDB database = new myDB(this, "SQL.db", null, 7);
    //搜索
    private SearchView searchView;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){//先判断resultCode，再获取data.getExtras，不然容易出现崩溃
            Bundle b=data.getExtras();
            if(b!=null){
                Map<String,Object> temp=new LinkedHashMap<>();
                temp.put("name",b.getString("account_type"));
                temp.put("account",b.get("account"));
                temp.put("password",b.getString("password"));
                data_pass.add(temp);
                simpleAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_pass);
        edit= findViewById(R.id.edit);
        add=findViewById(R.id.add_pass);
        list_pass=findViewById(R.id.list_pass);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_view();
            }
        });
        /**************遍历数据库中所有密码******************/
        SQLiteDatabase db=database.getWritableDatabase();
        Cursor cursor=db.query("passWord",null,null,null,null,null,null);
        while(cursor.moveToNext()){
            String name_str=cursor.getString(cursor.getColumnIndex("type"));//账号类型
            String account_str=cursor.getString(cursor.getColumnIndex("number"));
            String password_str=cursor.getString(cursor.getColumnIndex("password"));
            Map<String,Object> temp=new LinkedHashMap<>();
            temp.put("name",name_str);
            temp.put("account",account_str);
            temp.put("password",password_str);
            data_pass.add(temp);
        }
        cursor.close();db.close();
        simpleAdapter=new SimpleAdapter(this,data_pass,R.layout.pass_info,new String[]{"name","account","password"},new int[]{R.id.name,R.id.account,R.id.password});
        list_pass.setAdapter(simpleAdapter);

        /***************搜索*****************/
        search_list_pass=findViewById(R.id.search_list_pass);search_list_pass.setVisibility(View.GONE);
        searchView=findViewById(R.id.search_pass);
        searchView.setQueryHint("输入查询词");searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    list_pass.setVisibility(View.VISIBLE);
                    search_list_pass.setVisibility(View.GONE);
                }
                else{
                    list_pass.setVisibility(View.GONE);
                    search_list_pass.setVisibility(View.VISIBLE);
                    ArrayList<Map<String,Object>> search_data_pass=new ArrayList<>();
                    SQLiteDatabase db=database.getWritableDatabase();
                    Cursor cursor=db.query("passWord",null,null,null,null,null,null);
                    while(cursor.moveToNext()){
                        String name_str=cursor.getString(cursor.getColumnIndex("type"));//账号类型
                        String account_str=cursor.getString(cursor.getColumnIndex("number"));
                        String password_str=cursor.getString(cursor.getColumnIndex("password"));
                        if(name_str.contains(newText) || account_str.contains(newText) || password_str.contains(newText) ){
                            Map<String,Object> temp=new LinkedHashMap<>();
                            temp.put("name",name_str);
                            temp.put("account",account_str);
                            temp.put("password",password_str);
                            search_data_pass.add(temp);
                        }
                    }
                    cursor.close();db.close();
                    simpleAdapter_search=new SimpleAdapter(passwordActivity.this,search_data_pass,R.layout.pass_info,new String[]{"name","account","password"},new int[]{R.id.name,R.id.account,R.id.password});
                    search_list_pass.setAdapter(simpleAdapter_search);
                }
                return false;
            }
        });
        /********************更改*********************/
        list_pass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder UpdateDialog=new AlertDialog.Builder(passwordActivity.this);
                LayoutInflater inflater = LayoutInflater.from(passwordActivity.this);
                View layout_update = inflater.inflate(R.layout.pass_edit, null);
                getEditLayoutID(layout_update);
                //仅密码可编辑
                account_edit.setFocusable(false);account_edit.setFocusableInTouchMode(false);
                name_edit.setFocusable(false);name_edit.setFocusableInTouchMode(false);
                getPass.setVisibility(View.GONE);
                //显示
                SQLiteDatabase db=database.getWritableDatabase();
                Cursor cursor=db.query("passWord",null,null,null,null,null,null);
                while(cursor.moveToNext()){
                    String name_str=cursor.getString(cursor.getColumnIndex("type"));//账号类型
                    String account_str=cursor.getString(cursor.getColumnIndex("number"));//账号/名
                    String password_str=cursor.getString(cursor.getColumnIndex("password"));
                    Map<String,Object> temp=new LinkedHashMap<>();
                    temp.put("name",name_str);
                    temp.put("account",account_str);
                    temp.put("password",password_str);
                    if(data_pass.get(position).equals(temp)){
                        name_edit.setText(name_str);
                        account_edit.setText(account_str);
                        password_edit.setText(password_str);
                        break;
                    }
                }
                cursor.close();db.close();
                //更改
                UpdateDialog.setView(layout_update).setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db=database.getWritableDatabase();
                        String password_edit_str=password_edit.getText().toString();
                        ContentValues values=new ContentValues();
                        values.put("password",password_edit_str);
                        db.update("passWord",values,"type= ? and number= ?",new String[]{name_edit.getText().toString(),account_edit.getText().toString()});
                        db.close();
                        Map<String,Object> temp=new LinkedHashMap<>();
                        temp.put("name",name_edit.getText().toString());
                        temp.put("account",account_edit.getText().toString());
                        temp.put("password",password_edit_str);
                        data_pass.set(position,temp);
                        simpleAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("放弃修改",null).create().show();
            }
        });
        /********************删除*********************/
        list_pass.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder DeleteDialog=new AlertDialog.Builder(passwordActivity.this);
                DeleteDialog.setTitle("确定删除").setIcon(android.R.drawable.ic_menu_delete).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db=database.getWritableDatabase();
                        Cursor cursor=db.query("passWord",null,null,null,null,null,null);
                        while(cursor.moveToNext()){
                            String name_str=cursor.getString(cursor.getColumnIndex("type"));//账号类型
                            String account_str=cursor.getString(cursor.getColumnIndex("number"));
                            String password_str=cursor.getString(cursor.getColumnIndex("password"));
                            Map<String,Object> temp=new LinkedHashMap<>();
                            temp.put("name",name_str);
                            temp.put("account",account_str);
                            temp.put("password",password_str);
                            if(data_pass.get(position).equals(temp)){
                                db.delete("passWord","type = ? and number = ?",new String[]{name_str,account_str});
                                Toast.makeText(passwordActivity.this,"'账号类型:"+name_str+",账号:"+account_str+"'已被删除", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        cursor.close();db.close();
                        data_pass.remove(position);
                        simpleAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(passwordActivity.this,"已放弃删除", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
                return true;
            }
        });
        /********************添加*********************/
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(passwordActivity.this);
                String[] mItems={"手动输入","随机生成","智能生成"};
                alertDialog.setTitle("密码生成类型").setItems(mItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if(which==0){
                            //手动输入
                            dialog.dismiss();
                            final AlertDialog.Builder alertDialog1=new AlertDialog.Builder(passwordActivity.this);
                            LayoutInflater inflater = LayoutInflater.from(passwordActivity.this);
                            final View layout_edit = inflater.inflate(R.layout.pass_edit, null);
                            getEditLayoutID(layout_edit);
                            getPass.setVisibility(View.GONE);
                            alertDialog1.setView(layout_edit).setNegativeButton("取消",null).setPositiveButton("保存", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name_str=name_edit.getText().toString();
                                    String account_str=account_edit.getText().toString();
                                    String password_str=password_edit.getText().toString();
                                    Map<String,Object> temp=new LinkedHashMap<>();
                                    temp.put("name",name_str);
                                    temp.put("account",account_str);
                                    temp.put("password",password_str);
                                    data_pass.add(temp);
                                    simpleAdapter.notifyDataSetChanged();
                                    //写入数据库
                                    SQLiteDatabase db=database.getWritableDatabase();
                                    ContentValues values=new ContentValues();
                                    values.put("type",name_str);
                                    values.put("number",account_str);
                                    values.put("password",password_str);
                                    db.insert("passWord",null,values);
                                    db.close();
                                }
                            }).create().show();

                        }else if(which==1){
                            dialog.dismiss();
                            final AlertDialog.Builder alertDialog2=new AlertDialog.Builder(passwordActivity.this);
                            LayoutInflater inflater = LayoutInflater.from(passwordActivity.this);
                            final View layout_get = inflater.inflate(R.layout.pass_edit, null);
                            getEditLayoutID(layout_get);
                            password_edit.setFocusableInTouchMode(false);password_edit.setFocusable(false);
                            //随机生成密码
                            getPass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pass_random="";
                                    String name_str=name_edit.getText().toString();
                                    String account_str=account_edit.getText().toString();
                                    if(name_str.isEmpty()){
                                        name_edit.setError("账号类型不能为空");
                                    }else if(account_str.isEmpty()){
                                        account_edit.setError("用户名不能为空");
                                    }else{
                                        Random random=new Random();
                                        pass_random+=(""+(char)(random.nextInt(26)+'A')+((char)(random.nextInt(26)+'a'))+(char)(random.nextInt(26)+'a'));
                                        for(int i=0;i<random.nextInt(4)+4;i++){
                                            pass_random+=(random.nextInt(10)+"");
                                        }
                                        String[] str_special={"#","%","&","*"};
                                        pass_random+=(str_special[random.nextInt(4)]);
                                        password_edit.setText(pass_random);
                                    }
                                }
                            });
                            alertDialog2.setView(layout_get).setNegativeButton("取消",null).setPositiveButton("保存", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name_str=name_edit.getText().toString();
                                    String account_str=account_edit.getText().toString();
                                    Map<String,Object> temp=new LinkedHashMap<>();
                                    temp.put("name",name_str);
                                    temp.put("account",account_str);
                                    temp.put("password",pass_random);
                                    data_pass.add(temp);
                                    simpleAdapter.notifyDataSetChanged();
                                    //写入数据库
                                    SQLiteDatabase db=database.getWritableDatabase();
                                    ContentValues values=new ContentValues();
                                    values.put("type",name_str);
                                    values.put("number",account_str);
                                    values.put("password",pass_random);
                                    db.insert("passWord",null,values);
                                    db.close();
                                }
                            }).create().show();

                        }else if(which==2){
                            //智能生成密码
                            Intent intent=new Intent(passwordActivity.this,passeditActivity.class);
                            startActivityForResult(intent,3);
                        }
                    }
                }).setIcon(android.R.drawable.btn_star_big_on).create().show();
            }
        });
    }
    public void info_view(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(passwordActivity.this);
        LayoutInflater inflater = LayoutInflater.from(passwordActivity.this);
        final View layout = inflater.inflate(R.layout.user_info, null);
        getID(layout);
        pref= PreferenceManager.getDefaultSharedPreferences(passwordActivity.this);
        pref=getSharedPreferences("data",MODE_PRIVATE);
        name.setText(pref.getString("name",""));
        if(pref.getString("sex","").equals("女")){
            femail.setChecked(true);
            mail.setChecked(false);
        }else {
            mail.setChecked(true);
            femail.setChecked(false);
        }
       datePicker.updateDate(pref.getInt("year", 1996),pref.getInt("month", 10)-1,pref.getInt("day",24));
        phone.setText(pref.getString("phone",""));
        ID.setText(pref.getString("stu_id",""));
        editable(false);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(save_flag) {
                    String name_str = name.getText().toString();
                    String phone_str = phone.getText().toString();
                    String ID_str = ID.getText().toString();
                    if(phone_str.length()>0 && phone_str.length()<6){
                        phone.setError("电话格式不正确");
                    }else if(ID_str.length()>0 && ID_str.length()!=8){
                        ID.setError("学号格式不正确");
                    }else {
                        editor = getSharedPreferences("data", MODE_PRIVATE).edit();//开始编辑
                        if(mail.isChecked()){
                            editor.putString("sex","男");
                        }else if(femail.isChecked()){
                            editor.putString("sex","女");
                        }
                        editor.putString("name",name_str);
                        editor.putInt("year",datePicker.getYear());
                        editor.putInt("month",datePicker.getMonth()+1);
                        editor.putInt("day",datePicker.getDayOfMonth());
                        editor.putString("phone",phone_str);
                        editor.putString("stu_id",ID_str);
                        editor.apply();
                        editable(false);
                        save.setText("编辑");
                    }
                }
                else{
                    editable(true);
                    name.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager)name.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(name, name.getText().toString().length()-1);
                    save.setText("保存");
                }
                save_flag=!save_flag;
            }
        });
        alertDialog.setView(layout).create().show();
    }
    public void getID(View view){
        name=view. findViewById(R.id.name);
        phone=view. findViewById(R.id.phone);
        ID= view.findViewById(R.id.ID);
        mail=view. findViewById(R.id.mail);
        femail= view.findViewById(R.id.femail);
        sex=view.findViewById(R.id.sex);
        datePicker=view.findViewById(R.id.datePicker);
        save=view. findViewById(R.id.save);
    }
    public void editable(boolean flag){
        name.setFocusable(flag);name.setFocusableInTouchMode(flag);
        phone.setFocusable(flag);phone.setFocusableInTouchMode(flag);
        ID.setFocusable(flag);ID.setFocusableInTouchMode(flag);
        mail.setClickable(flag);femail.setClickable(flag);
        datePicker.setEnabled(flag);
    }
    public void getEditLayoutID(View view){
        name_edit=view.findViewById(R.id.name_edit);
        account_edit=view.findViewById(R.id.account_edit);
        password_edit=view.findViewById(R.id.password_edit);
        getPass=view.findViewById(R.id.getPass);
    }
}
