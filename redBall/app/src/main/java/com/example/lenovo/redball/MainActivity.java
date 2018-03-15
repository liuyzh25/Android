package com.example.lenovo.redball;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView[] n={(TextView) findViewById(R.id.n1),(TextView)findViewById(R.id.n2),(TextView)findViewById(R.id.n3),(TextView)findViewById(R.id.n4),(TextView)findViewById(R.id.n5),(TextView)findViewById(R.id.n6),(TextView)findViewById(R.id.n7),(TextView)findViewById(R.id.n8),(TextView)findViewById(R.id.n9),(TextView)findViewById(R.id.n10),(TextView)findViewById(R.id.n11),
                (TextView)findViewById(R.id.n12),(TextView)findViewById(R.id.n13),(TextView)findViewById(R.id.n14),(TextView)findViewById(R.id.n15),(TextView)findViewById(R.id.n16),(TextView)findViewById(R.id.n17),(TextView)findViewById(R.id.n18),(TextView)findViewById(R.id.n19),(TextView)findViewById(R.id.n20),(TextView)findViewById(R.id.n21),(TextView)findViewById(R.id.n22),
                (TextView)findViewById(R.id.n23),(TextView)findViewById(R.id.n24),(TextView)findViewById(R.id.n25),(TextView)findViewById(R.id.n26),(TextView)findViewById(R.id.n27),(TextView)findViewById(R.id.n28),(TextView)findViewById(R.id.n29),(TextView)findViewById(R.id.n30),(TextView)findViewById(R.id.n31),(TextView)findViewById(R.id.n32),(TextView)findViewById(R.id.n33)};
        final TextView[] red={(TextView)findViewById(R.id.red1),(TextView)findViewById(R.id.red2),(TextView)findViewById(R.id.red3),(TextView)findViewById(R.id.red4),(TextView)findViewById(R.id.red5),(TextView)findViewById(R.id.red6)};
        final TextView[] b={(TextView)findViewById(R.id.b1),(TextView)findViewById(R.id.b2),(TextView)findViewById(R.id.b3),(TextView)findViewById(R.id.b4),(TextView)findViewById(R.id.b5),(TextView)findViewById(R.id.b6),(TextView)findViewById(R.id.b7),(TextView)findViewById(R.id.b8),(TextView)findViewById(R.id.b9),(TextView)findViewById(R.id.b10),(TextView)findViewById(R.id.b11),(TextView)findViewById(R.id.b12),(TextView)findViewById(R.id.b13),
                (TextView)findViewById(R.id.b14),(TextView)findViewById(R.id.b15) };
        final TextView blue1=(TextView)findViewById(R.id.blue1);
        Button start=(Button) findViewById(R.id.start);Button reset=(Button)findViewById(R.id.reset);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<33;i++){
                    n[i].setBackgroundResource(R.drawable.circle);
                    if(i<15){
                        b[i].setBackgroundResource(R.drawable.circle);
                    }
                    if(i<6){
                        red[i].setText("***");
                        red[i].setBackgroundResource(R.drawable.circle);
                    }
                }
                blue1.setText("***");
                blue1.setBackgroundResource(R.drawable.circle);
                ArrayList<String>  red_delete=new ArrayList<String>();
                ArrayList<String> red_ss=new ArrayList<String>();
                ArrayList<String> blue_ss=new ArrayList<String>();
                for(int i=1;i<=33;i++){
                    red_ss.add(""+i);
                }
                for(int i=1;i<=16;i++){
                    blue_ss.add(""+i);
                }
                Random random=new Random();
                while(red_delete.size()<6){
                    //将数组数据打乱
                    for(int i=0;i<red_ss.size();i++){
                        int po=random.nextInt(red_ss.size());
                        if(po!=i){
                            String temp= red_ss.get(i);
                            red_ss.set(i,red_ss.get(po));
                            red_ss.set(po,temp);
                        }
                    }
                    //选出中间那个数字，并拿出
                    int num=red_ss.size();
                    red_delete.add(red_ss.get(num/2));
                    red_ss.remove(num/2);
                }
                for(int i=0;i<16;i++){
                    int po=random.nextInt(16);
                    if(po!=i){
                        String temp= blue_ss.get(i);
                        blue_ss.set(i,blue_ss.get(po));
                        blue_ss.set(po,temp);
                    }
                }
                //选出中间那个数字，并拿出
                red_delete.add(blue_ss.get(7));
                for(int i=0;i<6;i++){
                    n[Integer.valueOf(red_delete.get(i))-1].setBackgroundResource(R.drawable.cirlcle2);
                    red[i].setText(red_delete.get(i));
                    red[i].setBackgroundResource(R.drawable.cirlcle2);
                }
                blue1.setText(red_delete.get(6));
                b[Integer.valueOf(red_delete.get(6))-1].setBackgroundResource(R.drawable.circle3);
                blue1.setBackgroundResource(R.drawable.circle3);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<33;i++){
                    n[i].setBackgroundResource(R.drawable.circle);
                    if(i<15){
                        b[i].setBackgroundResource(R.drawable.circle);
                    }
                    if(i<6){
                        red[i].setText("***");
                        red[i].setBackgroundResource(R.drawable.circle);
                    }
                }
                blue1.setText("***");
                blue1.setBackgroundResource(R.drawable.circle);
            }
        });
    }
}
