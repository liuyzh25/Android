package com.example.lenovo.project;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class passeditActivity extends AppCompatActivity{
    //选取的信息点
    private CheckBox info_name,info_sex,info_account,info_phone,info_id,info_birth;
    private RadioButton level_num,level_letter,num_letter;
    private EditText account_type,account;
    private ImageView pass_get,pass_save;
    //读取个人信息
    private SharedPreferences pref;
    //智能生成的密码
    private StringBuilder passWord=new StringBuilder();
    private TextView passWord_str;
    //信息是否完善
    private boolean info_tag=true;
    //信息点是否选择
    private boolean checked_tag=false;
    //数据库
    private myDB database = new myDB(this, "SQL.db", null, 7);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_add);
        getID();
        pass_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref= PreferenceManager.getDefaultSharedPreferences(passeditActivity.this);
                pref=getSharedPreferences("data",MODE_PRIVATE);
                String account_type_str=account_type.getText().toString();
                String account_str=account.getText().toString();
                info_tag=true;checked_tag=false;
                //判断信息点是否被选中
                CheckBox[] checkBoxes={info_name,info_sex,info_account,info_phone,info_id,info_birth};
                for(int i=0;i<6;i++){
                    if(checkBoxes[i].isChecked()){
                        checked_tag=true;
                    }
                }
                if(account_type_str.isEmpty()){
                    account_type.setError("账号类型不能为空");
                }else if(account_str.isEmpty()){
                    account.setError("账号不能为空");
                }//开始根据个人信息生成密码
                else {
                    passWord=new StringBuilder();
                    /**********************仅有数字*****************************/
                    if(level_num.isChecked()){
                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(passeditActivity.this);
                        String mItems[]={"4","6(如银行密码)","大于6"};
                        alertDialog.setTitle("选择密码长度").setSingleChoiceItems(mItems, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0) {//密码长度为4
                                    if (info_birth.isChecked()) {
                                        if (birth_pw().length() == 4) {
                                            passWord.append(birth_pw());
                                        }
                                    }else if (info_id.isChecked()) {
                                        if(pref.getString("stu_id","").isEmpty()){
                                            info_tag=false;
                                        }else {
                                            passWord.append(id_pw());
                                        }
                                    }else if (info_phone.isChecked()) {
                                        if(pref.getString("phone","").isEmpty()){
                                            info_tag=false;
                                        }else {
                                            passWord.append(phone_pw());
                                        }
                                    }else {
                                        Random rd = new Random();
                                        for (int i = 0; i < 4; i++) {
                                            passWord.append(rd.nextInt(10));
                                        }
                                    }
                                }else if(which==1){//密码长度为6
                                    if(info_birth.isChecked()){
                                        if(birth_pw().length()==6){
                                            passWord.append(birth_pw());
                                        }
                                    }else if(info_id.isChecked()){
                                        if(pref.getString("stu_id","").isEmpty()){
                                            info_tag=false;
                                        }else {
                                            passWord.append(id_pw());
                                            Random rd = new Random();
                                            for (int i = 0; i < 2; i++) {
                                                passWord.append(rd.nextInt(10));
                                            }
                                        }
                                    }else if (info_phone.isChecked()) {
                                        if(pref.getString("phone","").isEmpty()){
                                            info_tag=false;
                                        }else {
                                            passWord.append(phone_pw());
                                            Random rd = new Random();
                                            for (int i = 0; i < 2; i++) {
                                                passWord.append(rd.nextInt(10));
                                            }
                                        }
                                    } else {
                                        Random rd = new Random();
                                        for (int i = 0; i < 6; i++) {
                                            passWord.append(rd.nextInt(10));
                                        }
                                    }
                                }else if(which==2){//密码长度为>6
                                    //出生年月
                                    if(info_birth.isChecked()){
                                        passWord.append(birth_pw());
                                    }
                                    //学号
                                    if(info_id.isChecked()) {
                                        if (pref.getString("stu_id", "").isEmpty()) {
                                            info_tag = false;
                                        } else {
                                            passWord.append(id_pw());
                                        }
                                    }
                                    //电话
                                    if(info_phone.isChecked()) {
                                        if (pref.getString("phone", "").isEmpty()) {
                                            info_tag = false;
                                        } else {
                                            passWord.append(phone_pw());
                                        }
                                    }
                                    //补充
                                    if(passWord.length()<7){
                                        Random rd = new Random();
                                        int len=rd.nextInt(4);
                                        for (int i = 0; i < 7+len; i++) {
                                            passWord.append(rd.nextInt(10));
                                        }
                                    }
                                }
                                passWord_str.setText("密码："+passWord);
                                dialog.dismiss();
                            }
                        }).create().show();
                    }
                    /********************仅有字母*********************/
                    else if(level_letter.isChecked()){
                        //性别组合
                        if(info_sex.isChecked()){
                            if(pref.getString("sex","").isEmpty()){
                                info_tag=false;
                            }else{
                                if(pref.getString("sex","").equals("男")){
                                    String[] man_gp={"#","%"};
                                    Random rd=new Random();
                                    passWord.append(man_gp[rd.nextInt(2)]);
                                }
                                else if(pref.getString("sex","").equals("女")){
                                    String[] man_gp={"&","*"};
                                    Random rd=new Random();
                                    passWord.append(man_gp[rd.nextInt(2)]);
                                }
                            }
                        }
                        //账号类型组合
                        if(info_account.isChecked()){
                            passWord.append(account_pw(account_type_str));
                        }
                        //名字组合
                        if(info_name.isChecked()){
                            if(pref.getString("name","").isEmpty()){
                                info_tag=false;
                            }else{
                                passWord.append(name_pw());
                            }
                        }
                        //补充
                        passWord.append("_");
                        Random rd=new Random();
                        for(int i=0;i<rd.nextInt(4);i++){
                            passWord.append((char)(rd.nextInt(26)+'a'));
                        }
                    }
                    /*******************默认是字母+数字***************************/
                    else {
                        //性别组合
                        if(info_sex.isChecked()){
                            if(pref.getString("sex","").isEmpty()){
                                info_tag=false;
                            }else{
                                if(pref.getString("sex","").equals("男")){
                                    String[] man_gp={"#","%"};
                                    Random rd=new Random();
                                    passWord.append(man_gp[rd.nextInt(2)]);
                                }
                                else if(pref.getString("sex","").equals("女")){
                                    String[] man_gp={"&","*"};
                                    Random rd=new Random();
                                    passWord.append(man_gp[rd.nextInt(2)]);
                                }
                            }
                        }
                        //账号类型组合
                        if(info_account.isChecked()){
                            passWord.append(account_pw(account_type_str));
                        }
                        //名字组合
                        if(info_name.isChecked()){
                            if(pref.getString("name","").isEmpty()){
                                info_tag=false;
                            }else{
                                passWord.append(name_pw());
                            }
                        }
                        //出生年月
                        if(info_birth.isChecked()){
                            passWord.append(birth_pw());
                        }
                        //学号
                        if(info_id.isChecked()) {
                            if (pref.getString("stu_id", "").isEmpty()) {
                                info_tag = false;
                            } else {
                                passWord.append(id_pw());
                            }
                        }
                        //电话
                        if(info_phone.isChecked()) {
                            if (pref.getString("phone", "").isEmpty()) {
                                info_tag = false;
                            } else {
                                passWord.append(phone_pw());
                            }
                        }
                        Random random=new Random();
                        if(random.nextInt(3)==0){//1/3概率出现零
                            passWord.append(0);
                        }else if(random.nextInt(3)==1){
                            passWord.append(888);
                        }
                    }
                    /**********************以上为密码的生成过程**********************/
                    passWord_str.setText("密码："+passWord);
                    if(!info_tag){
                        Snackbar snackbar=Snackbar.make(v,"您的个人信息未完善，会影响密码的生成",Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundResource(R.color.black);
                        snackbar.setActionTextColor(getResources().getColor(R.color.color1));
                        snackbar.setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                        snackbar.show();
                    }
                    else if(!checked_tag){
                        Snackbar snackbar=Snackbar.make(v,"您未选择任何信息点，会影响密码的生成",Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundResource(R.color.black);
                        snackbar.setActionTextColor(getResources().getColor(R.color.color1));
                        snackbar.setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                        snackbar.show();
                    }
                }
            }
        });
        pass_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passWord.toString().isEmpty()){
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(passeditActivity.this);
                    alertDialog.setTitle("密码还未生成，是否要退出").setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setPositiveButton("继续",null).create().show();
                }else{
                    String account_type_str=account_type.getText().toString();
                    String account_str=account.getText().toString();
                    //写入数据库
                    SQLiteDatabase db=database.getWritableDatabase();
                    ContentValues values=new ContentValues();
                    values.put("type",account_type_str);
                    values.put("number",account_str);
                    values.put("password",passWord.toString());
                    db.insert("passWord",null,values);
                    //把数据返回
                    Intent intent=new Intent();
                    Bundle b=new Bundle();
                    b.putString("password",passWord.toString());
                    b.putString("account_type",account_type_str);
                    b.putString("account",account_str);
                    intent.putExtras(b);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }
    public void getID(){
        info_name=findViewById(R.id.info_name);
        info_account=findViewById(R.id.info_account);
        info_sex=findViewById(R.id.info_sex);
        info_phone=findViewById(R.id.info_phone);
        info_id=findViewById(R.id.info_id);
        info_birth=findViewById(R.id.info_birth);
        level_num=findViewById(R.id.level_num);
        level_letter=findViewById(R.id.level_letter);
        num_letter=findViewById(R.id.num_letter);
        account_type=findViewById(R.id.account_type);
        account=findViewById(R.id.account);
        passWord_str=findViewById(R.id.passWord_str);
        pass_get=findViewById(R.id.pass_get);
        pass_save=findViewById(R.id.pass_save);
    }
    public String getPinYin(String hanzi) {// 输入汉字返回拼音的通用方法函数。
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(hanzi);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                if (HanziToPinyin.Token.PINYIN == token.type) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }
        return sb.toString();
    }
    public String format(int date){
        if(date<10){
            return "0"+date;
        }else {
            return date+"";
        }
    }
    //1.生成账号类型的三种
    public String account_pw(String account_type_str){
        boolean isPinyin=true;
        String[] account_gp={"","",""};
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(account_type_str);
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                StringBuilder sb = new StringBuilder();
                if (HanziToPinyin.Token.PINYIN == token.type) {
                    isPinyin=false;
                    sb.append(token.target);
                    account_gp[0]+=sb.substring(0,1);//截取首字母，小写
                    account_gp[1]+=sb.substring(0,1);//截取首字母，大写
                    account_gp[2]+=sb;//截取所有字母
                } else {
                    sb.append(token.source);
                    account_gp[2]+=sb;
                }
            }
        }
        account_gp[0]=account_gp[0].toLowerCase();
        account_gp[1]=account_gp[1].toUpperCase();
        //随机获取
        if(isPinyin){
            return account_gp[2];
        }else{
            Random rd=new Random();
            return account_gp[rd.nextInt(3)];
        }
    }
    //2.生成4种名字组合类型
    public String name_pw(){
        String[] name_gp={"","","",""};
        boolean isPinyin=true;
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(pref.getString("name",""));
        if (tokens != null && tokens.size() > 0) {
            int num=0;
            for (HanziToPinyin.Token token : tokens) {
                StringBuilder sb = new StringBuilder();
                if (HanziToPinyin.Token.PINYIN == token.type) {//汉字
                    isPinyin=false;
                    sb.append(token.target);
                    if(num==0){
                        name_gp[0]+=sb;//截取姓的全部字母
                        name_gp[1]+=sb;//截取姓的全部字母以及名字的首字母
                    }else{
                        name_gp[1]+=sb.substring(0,1);
                    }
                    name_gp[2]+=sb.substring(0,1);//截取每个字的首字母
                    name_gp[3]+=sb;
                } else {//字符
                    sb.append(token.source);
                    name_gp[3]+=sb;
                }
                num++;
            }
        }
        name_gp[0]=name_gp[0].toLowerCase();name_gp[1]=name_gp[1].toLowerCase();name_gp[2]=name_gp[2].toLowerCase();
        //随机获取
        if(isPinyin){
            return name_gp[3];
        }else{
            Random rd=new Random();
            return name_gp[rd.nextInt(4)];
        }
    }
    //3.出生年月、4种
    public String birth_pw(){
        String[] birth_gp={"","","",""};
        birth_gp[0]+=((pref.getInt("year",1900)+"").substring(2,4)+format(pref.getInt("month",1)));
        birth_gp[1]+=(format(pref.getInt("month",1))+format(pref.getInt("day",1)));
        birth_gp[2]+=pref.getInt("year",1900);
        birth_gp[3]+=((pref.getInt("year",1900)+"").substring(2,4)+format(pref.getInt("month",1))+format(pref.getInt("day",1)));
        //随机获取
        Random rd=new Random();
        return birth_gp[rd.nextInt(4)];
    }
    //4.学号、两种
    public String id_pw(){
        String[] id_gp={"",""};
        String id=pref.getString("stu_id","");
        id_gp[0]+=id.substring(0,4);id_gp[1]+=id.substring(4,8);
        //随机获取
        Random rd=new Random();
        return id_gp[rd.nextInt(2)];
    }
    //5.电话、一种
    public String phone_pw(){
        String phone_number=pref.getString("phone","");
        return phone_number.substring(phone_number.length()-4,phone_number.length());
    }
}
