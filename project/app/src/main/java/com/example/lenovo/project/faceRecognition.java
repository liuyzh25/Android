package com.example.lenovo.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.aip.face.AipFace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
public class faceRecognition  extends AppCompatActivity{
    private Button getImage,detect,Camera;
    private ImageView myPhoto;
    private Bitmap myBitmapImage=null;
    private String ImagePath=null;
    private TextView detect_tip;
    private String face_resultNum=null,face_age=null,face_gender=null,face_race=null,face_beauty=null,face_expression=null;
    private Uri imageUri;
    private int PHOTO_ALBUM=1,CAMERA=2,CROP_PHOTO=3;
    private JSONObject res=null;
    private Bitmap bitmapSmall;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //从相册中选择的图片
        if(requestCode==PHOTO_ALBUM){
            if(data!=null){
                Uri uri=data.getData();
                Cursor cursor=getContentResolver().query(uri,null,null,null,null);
                cursor.moveToNext();
                ImagePath=cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                cursor.close();
                resizePhoto();
                myPhoto.setImageBitmap(myBitmapImage);
                Log.i("图片路径",ImagePath);
            }
        }
        //相机拍摄的图片
        else if(requestCode==CAMERA){
            try{
                resizePhoto();
                myPhoto.setImageBitmap(myBitmapImage);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //调整图片的比例，使其大小小于1M,能够显示在手机屏幕上
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
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_recognition);
        readRequest();
        getImage=(Button)findViewById(R.id.getImage);
        myPhoto=(ImageView)findViewById(R.id.myPhoto);
        detect=(Button)findViewById(R.id.detect); Camera=(Button)findViewById(R.id.Camera);
        detect_tip=(TextView)findViewById(R.id.detect_tip);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Intent.ACTION_PICK);
                in.setType("image/*");
                startActivityForResult(in,PHOTO_ALBUM);
            }
        });
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage=new File(Environment.getExternalStorageDirectory()+File.separator+"face.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                imageUri=Uri.fromFile(outputImage);
                ImagePath=outputImage.getAbsolutePath();
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,CAMERA);
            }
        });
        detect_tip.setVisibility(View.GONE);
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res=null;
                detect_tip.setVisibility(View.VISIBLE);
                detect_tip.setText("识别中...");
               if(myBitmapImage==null){
                   myBitmapImage=BitmapFactory.decodeResource(getResources(),R.mipmap.face2);
                   bitmapSmall=Bitmap.createBitmap(myBitmapImage,0,0,myBitmapImage.getWidth(),myBitmapImage.getHeight());
               }
               else{
                   int degree=getPicRotate(ImagePath);
                   Matrix m=new Matrix();
                   m.setRotate(degree);
                   bitmapSmall=Bitmap.createBitmap(myBitmapImage,0,0,myBitmapImage.getWidth(),myBitmapImage.getHeight(),m,true);
               }
               //将图片由路径转为二进制数据流
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                //图片转数据流
                bitmapSmall.compress(Bitmap.CompressFormat.JPEG,100,stream);
                final byte[] arrays=stream.toByteArray();
                //网络申请调用函数进行人脸识别
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String,String> options=new HashMap<>();
                        options.put("face_fields","age,gender,race,beauty,expression,type");
                        AipFace client=new AipFace("10734368","6cvleSFbyRIRHzhijfYrHZFj","SDnCUfrtH0lgrK01HgTe2ZRLNsmCx5xy");
                        client.setConnectionTimeoutInMillis(2000);
                        client.setSocketTimeoutInMillis(6000);
                        res=client.detect(arrays,options);
                        try{
                            Message message = Message.obtain();
                            message.what = 1;
                            message.obj = res;
                            handler.sendMessage(message);
                        }catch (Exception e){
                            e.printStackTrace();
                            Message message = Message.obtain();
                            message.what = 2;
                            handler.sendMessage(message);
                        }
                    }
                }).start();
            }
        });
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                JSONObject res=(JSONObject) msg.obj;
                face_resultNum=res.optString("result_num");
                if(Integer.parseInt(face_resultNum)>=1) {
                    try {
                        JSONArray js = new JSONArray(res.optString("result"));
                        face_age = js.optJSONObject(0).optString("age");
                        face_gender = js.optJSONObject(0).optString("gender");
                        if (face_gender.equals("female")) {
                            face_gender = "女";
                        } else {
                            face_gender = "男";
                        }
                        face_race = js.optJSONObject(0).optString("race");
                        if (face_race.equals("yellow")) {
                            face_race = "黄种人";
                        } else if (face_race.equals("white")) {
                            face_race = "白种人";
                        } else if (face_race.equals("black")) {
                            face_race = "黑种人";
                        }else if(face_race.equals("arabs")){
                            face_race = "阿拉伯人";
                        }
                        int express = Integer.parseInt(js.optJSONObject(0).optString("expression"));
                        if (express == 0) {
                            face_expression = "无";
                        } else if (express == 1) {
                            face_expression = "微笑";
                        } else {
                            face_expression = "大笑";
                        }
                        face_beauty = js.optJSONObject(0).optString("beauty");
                        double  beauty=Math.ceil(Double.parseDouble(face_beauty)+25);
                        if(beauty>=100){
                            beauty=99.0;
                        }
                        else if(beauty<70){
                            beauty+=10;
                        }
                        else if(beauty>80 && beauty<90){
                            beauty+=5;
                        }
                        else if(beauty>=90 && beauty<95){
                            beauty+=2;
                        }
                        face_beauty=String.valueOf(beauty);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    detect_tip.setVisibility(View.GONE);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(faceRecognition.this);
                    String[] mItems = {"性别：" + face_gender, "年龄：" + face_age, "肤色：" + face_race, "颜值：" + face_beauty, "笑容：" + face_expression};
                    alertDialog.setTitle("人脸识别报告").setItems(mItems, null).create().show();
                }else{
                    detect_tip.setVisibility(View.VISIBLE);
                    detect_tip.setText("图片不够清晰，请重新选择");
                }
            }else{
                detect_tip.setVisibility(View.VISIBLE);
                detect_tip.setText("图片不够清晰，请重新选择");
            }
        }
    };
    public int getPicRotate(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
