package com.example.lenovo.project;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.aip.ocr.AipOcr;
import com.example.lenovo.project.utils.TransCallBack;
import com.example.lenovo.project.utils.TransResult;
import com.example.lenovo.project.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.utils.Exceptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class translate_activity extends AppCompatActivity {
    private EditText input;
    private TextView output;
    private Button start,getImage,camera;
    private Spinner translate_A,translate_B;
    private String translated="zh",AfterTranslate="zh";
    private String q="";
    private String appId="20180104000112092",key="1JcvpfMqUxPH2Ed_D8Mr";
    private String salt="1345660288",sign="";
    private String ImagePath=null;
    private Bitmap myBitmapImage;
    private ImageView imageView;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(data!=null){
                Uri uri=data.getData();
                Cursor cursor=getContentResolver().query(uri,null,null,null,null);
                cursor.moveToNext();
                ImagePath=cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                resizePhoto();
                imageView.setImageBitmap(myBitmapImage);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AipOcr client=new AipOcr("10734368","6cvleSFbyRIRHzhijfYrHZFj","SDnCUfrtH0lgrK01HgTe2ZRLNsmCx5xy");
                        client.setConnectionTimeoutInMillis(2000);
                        client.setSocketTimeoutInMillis(6000);
                        JSONObject jsonObject=client.basicGeneral(ImagePath,new HashMap<String, String>());
                        try{
                            Message message=Message.obtain();
                            message.obj=jsonObject;
                            message.what=1;
                            handler.sendMessage(message);
                        }catch (Exception e){
                            e.printStackTrace();
                            Message message=Message.obtain();
                            message.obj=e;
                            message.what=2;
                            handler.sendMessage(message);
                        }
                    }
                }).start();
            }
        }
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                try {
                    JSONObject res = (JSONObject) msg.obj;
                    JSONArray js = new JSONArray(res.optString("words_result"));
                    int len=js.length();
                    String s="";
                    for(int i=0;i<len;i++){
                        s+=js.optJSONObject(i).getString("words")+"\n";
                    }
                    input.setText(s);
                }catch (Exception e){
                    e.printStackTrace();
                    input.setText(e.toString());
                }
            }
        }
    };
    public void resizePhoto(){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;//返回图片宽高信息
        BitmapFactory.decodeFile(ImagePath,options);
        //让图片小于1024
        double radio=Math.max(options.outWidth*1.0d/1024f,options.outHeight*1.0d/1024f);
        options.inSampleSize=(int)Math.ceil(radio);//向上取整倍数
        options.inJustDecodeBounds=false;//显示图片
        myBitmapImage=BitmapFactory.decodeFile(ImagePath,options);
    }
    void readRequest(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(translate_activity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        getID();
        translate_B.setSelection(1,true);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translated = getLanguage_Type(translate_A.getSelectedItem().toString());
                AfterTranslate = getLanguage_Type(translate_B.getSelectedItem().toString());
                dealWithField();
                OkHttpUtils.get()
                        .url("http://api.fanyi.baidu.com/api/trans/vip/translate")
                        .addParams("q", q)
                        .addParams("from", translated)
                        .addParams("to", AfterTranslate)
                        .addParams("appid", appId)
                        .addParams("salt", salt).addParams("sign", sign).build()
                        .execute(new TransCallBack() {
                            @Override
                            public void onError(okhttp3.Call call, Exception e, int id) {
                                output.setText("网络出错啦！");
                            }
                            @Override
                            public void onResponse(TransResult response, int id) {
                                if(input.getText().length()>0) {
                                    output.setText(response.getTrans_result().get(0).getDst());
                                }
                            }
                        });
            }
        });
    }
    void getID(){
        input=(EditText)findViewById(R.id.input);
        output=(TextView) findViewById(R.id.output);
        start=(Button)findViewById(R.id.start);
        getImage=(Button)findViewById(R.id.getImage);camera=(Button)findViewById(R.id.camera);
        imageView=(ImageView)findViewById(R.id.imageView);
        translate_A=(Spinner)findViewById(R.id.translate_A);
        translate_B=(Spinner)findViewById(R.id.translate_B);
    }
    private void dealWithField() {
        String str = input.getText().toString();
        if (str.length() > 0) {
            q = str;
        }
        q = Utils.toUtf8(q);
        String lian = appId + q + salt + key;
        sign = Utils.encrypt(lian);
        q = Utils.toURLDecoded(q);
        translated = Utils.toURLDecoded(translated);
        AfterTranslate = Utils.toURLDecoded(AfterTranslate);
    }
    public String getLanguage_Type(String type){
        if(type.equals("中文")){
            return "zh";
        }else if(type.equals("英语")){
            return "en";
        }else if(type.equals("粤语")){
            return "yue";
        }else if(type.equals("文言文")){
            return "wyw";
        }else if(type.equals("日语")){
            return "jp";
        }else if(type.equals("韩语")){
            return "kor";
        }else if(type.equals("法语")){
            return "fra";
        }else if(type.equals("西班牙语")){
            return "spa";
        }else if(type.equals("泰语")){
            return "th";
        }else if(type.equals("阿拉伯语")){
            return "ara";
        }else if(type.equals("俄语")){
            return "ru";
        }else if(type.equals("葡萄牙语")){
            return "pt";
        }else if(type.equals("德语")){
            return "de";
        }else if(type.equals("繁体中文")){
            return "cht";
        }
        return "auto";
    }
}
