package com.example.lenovo.image_identify;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.imageclassify.AipImageClassify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private ImageView foodImage,capture;
    private View scan;
    private TextView selections ;
    String imagePath=null;
    public static final String APP_ID = "10675026";
    public static final String API_KEY = "1iMR3Il9PL4KzUHbpyhshCix";
    public static final String SECRET_KEY = "b7ncbQ67peyij7bf7qomnaBiieakmf31";
    private Bitmap myBitmap=null;
    private final static int DISH=1,CAR=2,ANIMAL=3,PLANT=4,ERROR=0;
    private byte[] arrays;
    private JSONObject res;
    private int po=0;
    private Boolean tag=false;
    private TranslateAnimation ani;
    private String face_result=null,face_age=null,face_gender=null,face_race=null,face_beauty=null,face_expression=null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(data!=null){
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToNext();
                imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                cursor.close();
            }
        }
        if(requestCode==1 || requestCode==2){
            handle_photo();
            foodImage.setImageBitmap(myBitmap);
        }
    }
    public void resizePhoto(){
        if(imagePath!=null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;//返回图片宽高信息
            BitmapFactory.decodeFile(imagePath, options);
            //让图片小于1024
            double radio = Math.max(options.outWidth * 1.0d / 1024f, options.outHeight * 1.0d / 1024f);
            options.inSampleSize = (int) Math.ceil(radio);//向上取整倍数
            options.inJustDecodeBounds = false;//显示图片
            myBitmap = BitmapFactory.decodeFile(imagePath, options);
        }
    }
    public void handle_photo(){
        //调整宽高比例，便于imageView控件显示
        resizePhoto();
        //调整角度，防止图片在某些手机（如三星）显示时歪斜
        if(imagePath!=null) {
            int degree = getPicRotate(imagePath);
            Matrix m = new Matrix();
            m.setRotate(degree);
            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), m, true);
        }
        //质量压缩图片
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);//此时并不是压缩，而是把压缩的数据存入bytearrayOutputStream
        int quality=100;
        while(byteArrayOutputStream.toByteArray().length/1024>4000){//小于4M
            quality-=10;
            myBitmap.compress(Bitmap.CompressFormat.JPEG,quality,byteArrayOutputStream);
        }
        myBitmap.compress(Bitmap.CompressFormat.JPEG,quality,byteArrayOutputStream);
        arrays=byteArrayOutputStream.toByteArray();
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ani.cancel();
            try {
                switch (msg.what) {
                    case DISH:
                        JSONObject res = (JSONObject) msg.obj;
                        JSONArray jsonArray = new JSONArray(res.optString("result"));
                        String name = "", calorie = "", probability = "";
                        name = jsonArray.optJSONObject(0).optString("name");
                        calorie = jsonArray.optJSONObject(0).optString("calorie");
                        probability = jsonArray.optJSONObject(0).optString("probability");
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        String[] mitems = {"名称：" + name, "卡路里：" + calorie, "可能性：" + probability};
                        alertDialog.setTitle("菜品识别报告").setItems(mitems, null).create().show();
                        break;
                    case CAR:
                        JSONObject res2 = (JSONObject) msg.obj;
                        JSONArray jsonArray2 = new JSONArray(res2.optString("result"));
                        String name2 = jsonArray2.optJSONObject(0).optString("name") ;
                        String score2= jsonArray2.optJSONObject(0).optString("score");
                        String[] mitems2={"名称："+name2,"可能性："+score2};
                        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(MainActivity.this);
                        alertDialog2.setTitle("车型识别报告").setItems(mitems2, null).create().show();
                        break;
                    case ANIMAL:
                        JSONObject res3 = (JSONObject) msg.obj;
                        JSONArray jsonArray3 = new JSONArray(res3.optString("result"));
                        String name3 = jsonArray3.optJSONObject(0).optString("name") ;
                        String score3= jsonArray3.optJSONObject(0).optString("score");
                        String[] mitems3={"名称："+name3,"可能性："+score3};
                        AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(MainActivity.this);
                        alertDialog3.setTitle("动物识别报告").setItems(mitems3, null).create().show();
                        break;
                    case PLANT:
                        JSONObject res4 = (JSONObject) msg.obj;
                        JSONArray jsonArray4 = new JSONArray(res4.optString("result"));
                        String name4 = jsonArray4.optJSONObject(0).optString("name") ;
                        String score4= jsonArray4.optJSONObject(0).optString("score");
                        String[] mitems4={"名称："+name4,"可能性："+score4};
                        AlertDialog.Builder alertDialog4 = new AlertDialog.Builder(MainActivity.this);
                        alertDialog4.setTitle("植物识别报告").setItems(mitems4, null).create().show();
                        break;
                    case 5:
                        JSONObject res5=(JSONObject) msg.obj;
                        face_result=res5.optString("result_num");
                        if(Integer.parseInt(face_result)>=1) {
                            try {
                                JSONArray js = new JSONArray(res5.optString("result"));
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
                            AlertDialog.Builder alertDialog5 = new AlertDialog.Builder(MainActivity.this);
                            String[] mItems5 = {"性别：" + face_gender, "年龄：" + face_age, "肤色：" + face_race, "颜值：" + face_beauty, "笑容：" + face_expression};
                            alertDialog5.setTitle("人脸识别报告").setItems(mItems5, null).create().show();
                        }else{
                            AlertDialog.Builder alertDialog5 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog5.setTitle("人脸识别报告").setMessage("图片不够清晰，请重新选择").create().show();
                        }
                        break;
                    case 123:
                        capture.setVisibility(View.GONE);
                        break;
                    default:
                        Exception e = (Exception)msg.obj;
                        AlertDialog.Builder alertDialog5 = new AlertDialog.Builder(MainActivity.this);
                        alertDialog5.setTitle("识别错误报告").setMessage(e.toString()).create().show();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
                AlertDialog.Builder alertDialog5 = new AlertDialog.Builder(MainActivity.this);
                alertDialog5.setTitle("识别报告").setMessage("图片不够清晰，无法识别").create().show();
            }
        }
    };
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        if(action==0) {//0为按下，1为放开，2为一直按着
            if (tag) {
                capture.setVisibility(View.VISIBLE);
                Message message = Message.obtain();
                message.what = 123;
                handler.sendMessageDelayed(message, 6000);
            } else {
                capture.setVisibility(View.GONE);
            }
            tag = !tag;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getID();
        selections.setText("<菜品识别>");
        selections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
                final String[] mItems={"菜品识别","车型识别","动物识别","植物识别","人脸识别"};
                alertDialog.setItems(mItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        addLineAni();
                        po=which;
                        selections.setText("<"+mItems[which]+">");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(which==4){
                                    HashMap<String,String> options=new HashMap<>();
                                    options.put("face_fields","age,gender,race,beauty,expression,type");
                                    AipFace client=new AipFace("10734368","6cvleSFbyRIRHzhijfYrHZFj","SDnCUfrtH0lgrK01HgTe2ZRLNsmCx5xy");
                                    client.setConnectionTimeoutInMillis(2000);
                                    client.setSocketTimeoutInMillis(6000);
                                    try{
                                        res=client.detect(arrays,options);
                                        Message message = Message.obtain();
                                        message.what = 5;
                                        message.obj = res;
                                        handler.sendMessage(message);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        Message message = Message.obtain();
                                        message.what = ERROR;
                                        message.obj = e;
                                        handler.sendMessage(message);
                                    }
                                }else {
                                    AipImageClassify client = new AipImageClassify(APP_ID, API_KEY, SECRET_KEY);
                                    client.setConnectionTimeoutInMillis(2000);
                                    client.setSocketTimeoutInMillis(6000);
                                    try {
                                        int what = detect(po);
                                        Message message = Message.obtain();
                                        message.what = what;
                                        message.obj = res;
                                        handler.sendMessage(message);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Message message = Message.obtain();
                                        message.what = ERROR;
                                        message.obj = e;
                                        handler.sendMessage(message);
                                    }
                                }
                            }
                        }).start();
                    }
                }).create().show();
            }
        });
        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag) {
                    capture.setVisibility(View.VISIBLE);
                    Message message = Message.obtain();
                    message.what = 123;
                    handler.sendMessageDelayed(message, 6000);
                }else{
                    capture.setVisibility(View.GONE);
                }
                tag=!tag;
            }
        });
        capture.setVisibility(View.GONE);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
                String[] mitems={"从相册中选择","拍照"};
                alertDialog.setTitle("选择图片方式").setItems(mitems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            Intent intent1=new Intent(Intent.ACTION_PICK);
                            intent1.setType("image/*");
                            startActivityForResult(intent1,1);
                        }else if(which==1){
                            Intent intent2=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file=new File(Environment.getExternalStorageDirectory()+File.separator+"food.jpg");
                            try{
                                if(file.exists()){
                                    file.delete();
                                }
                                file.createNewFile();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                            intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            imagePath=file.getAbsolutePath();
                            startActivityForResult(intent2,2);
                        }
                    }
                }).create().show();
            }
        });
    }
    public void addLineAni(){
        int height=foodImage.getHeight();
        scan.setVisibility(View.VISIBLE);
        double time=height/0.5;
        ani=new TranslateAnimation(0,0,0,height);
        ani.start();
        ani.setDuration((int)time);
        ani.setRepeatCount(Animation.INFINITE);
        scan.startAnimation(ani);
    }
    void getID(){
        foodImage=(ImageView)findViewById(R.id.foodImage);
        capture=(ImageView)findViewById(R.id.capture) ;
        selections=(TextView) findViewById(R.id.select);
        scan=(View) findViewById(R.id.scan);
    }
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
    public  int detect(int position){
        AipImageClassify client=new AipImageClassify(APP_ID,API_KEY,SECRET_KEY);
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(6000);
        if(imagePath==null) {
            myBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.food);
            handle_photo();
            res = client.dishDetect(arrays, new HashMap<String, String>());
            return 1;
        }else {
            if (position == 0) {
                res = client.dishDetect(arrays, new HashMap<String, String>());
                return 1;
            } else if (position == 1) {
                res = client.carDetect(arrays, new HashMap<String, String>());
                return 2;
            } else if (position == 2) {
                res = client.animalDetect(arrays, new HashMap<String, String>());
                return 3;
            } else if (position == 3) {
                res = client.plantDetect(arrays, new HashMap<String, String>());
                return 4;
            }
        }
        return 1;
    }
}
