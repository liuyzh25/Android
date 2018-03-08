package com.example.lenovo.project;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class game_info_activity extends AppCompatActivity {
    final String[] problems={"百米赛跑中，你超过了第二名，你是第几名？","61-27=？","一斤铁和一斤棉花哪个重？","牛顿第三定律的内容是？","以下哪个地方未曾发生过核泄漏？","澳大利亚的首都是？","经典游戏“扫雷”的初级难度下总共有几颗雷？","四大文明古国不包括？","以下短语意思不是“许多”的是？","心理学上用来形容“越怕糟糕的事情，事情越容易发生”的术语是？","以下哪个物质是有机物？","“苟利国家生死以”下一句是？","《西风颂》是哪位诗人的作品？","全国统一采用的北京时间是哪个时区？","获得性免疫缺陷综合症英文缩写是？","中山大学校训中的错字是？","计算机打开“运行”对话框的快捷键组合是？","世界第二高峰是？","以下不可能出现的天文现象是？","我国第一艘服役航母叫什么？","2010年亚运会的举办地是？"
            ,"三国时期“卧龙 凤雏 幼麟 冢虎”不包括以下哪位？", "逍遥津之战的魏军主帅是？", "蜀国的“五虎上将”不包括以下哪位？", "魏国的“五子良将”不包括以下哪位？", "三国时期的诸葛三杰，“蜀得其龙、吴得其虎、魏得其狗”，其中不包括哪位？", "曹操兵败赤壁之后，叹曰:“若奉孝在,不使孤至此!”其中“奉孝”指的是？", "“蜀中无大将，____作先锋”，此处横线应为？", "东吴名将黄盖，字____？", "三国时期，曾先后在董卓、曹操麾下效力的是？", "东吴的“四大都督”不包括以下哪位？", "三国时期蜀国马良和马超什么关系？", " 歇后语“____进曹营，一言不发。”横线处应为？", "三国时期赤壁大战中献上“连环计”的是以下哪位？",
        "刘备迎娶吴国孙夫人。孙夫人是孙权的什么亲人？", "三国时期知名的姐妹花“大小乔”是哪国人？", "诸葛族人在三国时期扮演了重要角色。诸葛瑾和诸葛恪是什么关系？", "以下哪位谋士被曹操赐死？", "曹操的爱将典韦在哪件事中阵亡？", "东吴著名将领陆逊的成名之战是？", "曹仁，字____？",
            "哪个月有28天？","什么池没有水","灰姑娘的水晶鞋是什么跟的鞋","什么睡得最早，起得最早","黑色的马叫黑马，白色的马叫白马，那黑白的马叫","疯狗不会干嘛","有一个人仅仅不会干两件事，那这两件事是什么","什么食物永远不会变坏","什么动物有32个大脑","1999年《时代》杂志评选的20世纪风云人物第一名是","半斤半两的半斤在古代是多少两","一个时辰等于多少分钟","欧洲土地面积第二大的国家是","诺贝尔文学奖是什么时候开始评选的","东晋十六国不包括哪国","秦始皇的父亲是？","中国历史上当了三次皇帝的是谁","历史上从哪个领袖开始使用世袭制","黑黑一堵墙，镶在正中央，讲课要靠它，作画最便当。（打一文体用品）"
            ,"十八子，打一字","关云长嫁女，打一成语","02468,打一成语","什么布永远剪不断","《天龙八部》中谁练成了六脉神剑","《天龙八部》中谁的武功天下第一","《倚天屠龙》张无忌没有练过哪门武功","金庸小说中哪门武功可以吸收他人内力","《侠客行》中主人公石破天和石清是什么关系","《神雕侠侣》中杨过的手臂是谁砍断的","《神雕侠侣》中一见杨过误终身，遍寻四十年不得后出家为尼的是谁"};
    final String[]title={"题目一","题目二","题目三","题目四","题目五","题目六","题目七","题目八","题目九","题目十","题目十一","题目十二","题目十三","题目十四","题目十五","题目十六","题目十七","题目十八","题目十九","题目二十","题目二十一","题目二十二","题目二十三","题目二十四","题目二十五"};
    final String[]choice1={"第一名","24","铁重","作用力等于反作用力","前苏联切尔诺贝利","悉尼","5","古巴比伦","a great many of","博萨德法则","五氯化磷","随风直到夜郎西","雪莱","东六区","AIDS","明","Win+R","珠穆朗玛峰","日全食","山东号","多哈","司马懿", "张辽", "黄忠", "许褚", "诸葛亮", "荀彧", "关平", "公绩", "贾诩", "吕蒙", "叔侄", "徐庶", "黄盖", "亲女儿", "蜀国", "父子", "戏志才", "夷陵之战", "荆州之战", "子孝"
           ,"八月","粪池","高跟鞋","狗","害羞的斑马","拐弯跑","飞天","蚕蛹","鼠妇","爱因斯坦","八两","60分钟","乌克兰","1951","南燕","秦庄襄王","刘禅","启","白板","季","没有正确答案","举世无双","麻布","段正淳","萧峰","乾坤大挪移","小无相功","没有关系","被雕咬断的","小龙女" };
    final String[]choice2={"第二名","34","棉花重","任何物体都保持平衡状态，直到外力迫使它改变状态。","美国三里岛","堪培拉","10","古印度","lots of","巴纳姆效应","四氯化碳","夜班梦中惊坐起","拜伦","东七区","ACIS","审","Win+E","洛子峰","日环食","辽宁号","雅加达"
            ,"姜维", "曹真", "魏延", "乐进", "诸葛瞻", "程昱", "张嶷", "公台", "于禁", "鲁肃", "兄弟", "关羽", "蒋干", "妹妹", "吴国", "叔侄", "荀彧", "宛城之战", "濮阳之战", "子桓",
            "二月","水池","坡跟鞋","牛","奥巴马","咬人","这也不会，那也不会","蜂蜜","怎么可能","罗斯福","五两","120分钟","法国","1900","北燕","秦昭襄王","明英宗","舜","粉笔","李","关门大吉","无独有偶","油布","段誉","段誉","九阳神功","太玄经","叔侄","郭芙砍断的","郭芙"
            };
    final String[]choice3={"第三名","44","一样重","合外力等于质量和加速度的乘积","日本福岛","布里斯班","15","古希腊","several of","破窗效应","四氯化钛","千载谁堪伯仲间","济慈","东八区","AMES","慎","Win+W","干城章嘉峰","月全食","天津号","广州"
            ,"周瑜", "于禁", "马超", "于禁", "诸葛诞", "戏志才", "廖化", "公明", "庞德", "陆逊", "父子", "蒋干", "周瑜", "侄女", "魏国", "兄弟", "郭嘉", "赤壁之战", "合肥之战", "子建",
            "每个月都有","电池","平跟鞋","猪","天马","乱叫","隐身","橄榄","蚯蚓","毛泽东","十两","30分钟","德国","1950","东燕","秦惠文王","溥仪","禹","黑板","科","关关雎鸠","巧夺天工","瀑布","鸠摩智","虚竹","一阳指","九阳神功","父子","自断的","郭襄"};
    final String[]choice4={"任何名次都有可能","46","都有可能","以上都是","法国昂布瓦斯","铂斯","20","古埃及","a number of","墨菲定律","三氯化铝","岂因祸福避趋之","歌德","东九区","ALAS","笃","Win+Q","乔戈里峰","月环食","青岛号","杜尚别"
            ,"庞统", "步骘", "赵云", "张郃", "诸葛瑾", "郭嘉", "姜维", "公覆", "荀彧", "黄盖", "没有关系", "姜维", "庞统", "养女儿", "都不是", "没关系", "贾诩", "汉中之战", "夷陵之战", "子文",
            "十二月","瑶池","中跟鞋","太阳","草泥马","乱跑","写字","枸杞","水蛭","甘地","十六两","90分钟","西班牙","1901","西燕","秦孝文王","李渊","黄帝","书桌","籽","班门弄斧","美女如云","吕布","段正明","扫地僧","圣火令","北冥神功","朋友","金轮大王砍断的","程英"
            };
    final String[]answer={"B","B","C","A","D","B","B","C","C","D","B","D","A","C","A","A","A","D","D","B","C"
            ,"C", "A", "B","A", "B", "D", "C", "D", "A", "D", "D", "A", "D", "B", "B", "A", "B", "B", "D", "A",
            "C","C","C","D","A","A","B","B","D","A","A","B","A","D","C","A","C","A","C","B","B","B","C","B","D","C","D","C","B","C"
    };
    int score=0;//对的题数
    double radio_accuracy=0;
    final ArrayList<String> man_answer_str=new ArrayList<>();
    int [][]man_answer=new int[71][4];
    int []list=new int [75];
    private int set_problems=10;
    private int problems_num=0;
    int finish_jindu = 0;
    int error = 0;
    private boolean flag_checked=false;
    TextView choice1_id ;
    TextView choice2_id ;
    TextView choice3_id ;
    TextView choice4_id;
    private MediaPlayer mediaPlayer;
    //答不对题，不给饭吃
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(radio_accuracy<80){
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_info);
        //关掉其他的音乐文件
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this,R.raw.alarm);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        //创建一个闹钟提醒的对话框,点击确定关闭铃声与页面
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(game_info_activity.this);
        alertDialog.setTitle("闹钟").setMessage("小猪小猪快起床~")
                .setPositiveButton("去关闭闹铃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent in = getIntent();
                        Bundle data = in.getExtras();
                        if (data != null) {
                            set_problems = Integer.parseInt(data.getString("problemNum"));
                        }//设置题目数量
                        for(int i=0;i<set_problems;i++){
                            man_answer_str.add("");
                        }
                        final TextView title_id = findViewById(R.id.title_problem);
                        final TextView problem_id = findViewById(R.id.problem);
                        choice1_id = findViewById(R.id.first);
                        choice2_id = findViewById(R.id.second);
                        choice3_id = findViewById(R.id.third);
                        choice4_id = findViewById(R.id.fourth);
                        final TextView last_id = findViewById(R.id.last);
                        final TextView next_id = findViewById(R.id.next);
                        final TextView submit_id = findViewById(R.id.submit);
                        //获取n个不同的随机数
                        int count=0;
                        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
                        for (int i = 0; ; i++) {
                            Random ran = new Random();
                            int x;
                            x = ran.nextInt(71);
                            if (!map.containsValue(x)) {
                                map.put(i, x);
                                list[count++] = x;
                                if(count==set_problems) break;
                            }
                        }
                        title_id.setText(title[0]);
                        problem_id.setText(problems[list[0]]);
                        choice1_id.setText("A." + choice1[list[0]]);
                        choice2_id.setText("B." + choice2[list[0]]);
                        choice3_id.setText("C." + choice3[list[0]]);
                        choice4_id.setText("D." + choice4[list[0]]);
                        update();getManAnswer();
                        next_id.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (problems_num >= 0 && problems_num <= set_problems-2) {//0-8
                                    getManAnswer();//进入下一题时，把当下的题给更新了
                                    problems_num = problems_num + 1;
                                    choice1_id.setBackgroundResource(0);
                                    choice2_id.setBackgroundResource(0);
                                    choice3_id.setBackgroundResource(0);
                                    choice4_id.setBackgroundResource(0);
                                    title_id.setText(title[problems_num]);
                                    problem_id.setText(problems[list[problems_num]]);
                                    choice1_id.setText("A." + choice1[list[problems_num]]);
                                    choice2_id.setText("B." + choice2[list[problems_num]]);
                                    choice3_id.setText("C." + choice3[list[problems_num]]);
                                    choice4_id.setText("D." + choice4[list[problems_num]]);
                                    if (man_answer_str.get(problems_num).equals("A")){
                                        choice1_id.setBackgroundResource(R.drawable.button_decoration);
                                    } else if (man_answer_str.get(problems_num).equals("B")) {
                                        choice2_id.setBackgroundResource(R.drawable.button_decoration);
                                    } else if (man_answer_str.get(problems_num).equals("C")) {
                                        choice3_id.setBackgroundResource(R.drawable.button_decoration);
                                    } else if (man_answer_str.get(problems_num).equals("D")) {
                                        choice4_id.setBackgroundResource(R.drawable.button_decoration);
                                    }
                                    update();
                                    getManAnswer();
                                    if(flag_checked==true){
                                        TextView daan=findViewById(R.id.answer);submit_id.setText("返回");
                                        choice1_id.setTextColor(getResources().getColor(R.color.gray2));choice2_id.setTextColor(getResources().getColor(R.color.gray2));choice3_id.setTextColor(getResources().getColor(R.color.gray2));choice4_id.setTextColor(getResources().getColor(R.color.gray2));
                                        if(answer[list[problems_num]]=="A"){
                                            choice1_id.setTextColor(getResources().getColor(R.color.red));
                                            daan.setText("正确答案："+"A");
                                        }
                                        else if(answer[list[problems_num]]=="B"){
                                            choice2_id.setTextColor(getResources().getColor(R.color.red));
                                            daan.setText("正确答案："+"B");
                                        }
                                        else if(answer[list[problems_num]]=="C"){
                                            choice3_id.setTextColor(getResources().getColor(R.color.red));
                                            daan.setText("正确答案："+"C");
                                        }
                                        else if(answer[list[problems_num]]=="D"){
                                            choice4_id.setTextColor(getResources().getColor(R.color.red));
                                            daan.setText("正确答案："+"D");
                                        }
                                    }
                                }
                                else{
                                    Snackbar snackbar = Snackbar.make(v, "这已经是最后一道题了", Snackbar.LENGTH_SHORT);
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
                        });
                        last_id.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (problems_num >= 1 && problems_num <= set_problems-1) {//1-9
                                    getManAnswer();//返回上一题时，把当下的题给更新了
                                    problems_num = problems_num - 1;
                                    title_id.setText(title[problems_num]);
                                    problem_id.setText(problems[list[problems_num]]);
                                    choice1_id.setText("A." + choice1[list[problems_num]]);
                                    choice2_id.setText("B." + choice2[list[problems_num]]);
                                    choice3_id.setText("C." + choice3[list[problems_num]]);
                                    choice4_id.setText("D." + choice4[list[problems_num]]);
                                    if (man_answer_str.get(problems_num).equals("A")){
                                        choice1_id.setBackgroundResource(R.drawable.button_decoration);
                                        choice2_id.setBackgroundResource(0);
                                        choice3_id.setBackgroundResource(0);
                                        choice4_id.setBackgroundResource(0);
                                    } else if (man_answer_str.get(problems_num).equals("B")) {
                                        choice2_id.setBackgroundResource(R.drawable.button_decoration);
                                        choice1_id.setBackgroundResource(0);
                                        choice3_id.setBackgroundResource(0);
                                        choice4_id.setBackgroundResource(0);
                                    } else if (man_answer_str.get(problems_num).equals("C")) {
                                        choice3_id.setBackgroundResource(R.drawable.button_decoration);
                                        choice2_id.setBackgroundResource(0);
                                        choice1_id.setBackgroundResource(0);
                                        choice4_id.setBackgroundResource(0);
                                    } else if (man_answer_str.get(problems_num).equals("D")) {
                                        choice4_id.setBackgroundResource(R.drawable.button_decoration);
                                        choice1_id.setBackgroundResource(0);
                                        choice2_id.setBackgroundResource(0);
                                        choice3_id.setBackgroundResource(0);
                                    }
                                    update();
                                    getManAnswer();
                                    if(flag_checked==true){
                                        submit_id.setText("返回");
                                        TextView daan=findViewById(R.id.answer);
                                        choice1_id.setTextColor(getResources().getColor(R.color.gray2));choice2_id.setTextColor(getResources().getColor(R.color.gray2));choice3_id.setTextColor(getResources().getColor(R.color.gray2));choice4_id.setTextColor(getResources().getColor(R.color.gray2));
                                        if(answer[list[problems_num]]=="A"){
                                            choice1_id.setTextColor(getResources().getColor(R.color.red));
                                            daan.setText("正确答案："+"A");
                                        }
                                        else if(answer[list[problems_num]]=="B"){
                                            choice2_id.setTextColor(getResources().getColor(R.color.red));
                                            daan.setText("正确答案："+"B");
                                        }
                                        else if(answer[list[problems_num]]=="C"){
                                            choice3_id.setTextColor(getResources().getColor(R.color.red));
                                            daan.setText("正确答案："+"C");
                                        }
                                        else if(answer[list[problems_num]]=="D"){
                                            choice4_id.setTextColor(getResources().getColor(R.color.red));
                                            daan.setText("正确答案："+"D");
                                        }
                                    }
                                }
                                else{
                                    Snackbar snackbar = Snackbar.make(v, "这已经是第一道题了", Snackbar.LENGTH_SHORT);
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
                        });
                        submit_id.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(flag_checked){
                                    Intent intent_return=new Intent();Bundle b=new Bundle();
                                    b.putInt("score",score);b.putInt("problems",set_problems);
                                    intent_return.putExtras(b);setResult(RESULT_OK,intent_return);
                                    finish();
                                }
                                if (problems_num + 1 < set_problems) {
                                    Snackbar snackbar = Snackbar.make(v, "您未完成全部题目", Snackbar.LENGTH_SHORT);
                                    snackbar.getView().setBackgroundResource(R.color.black);
                                    snackbar.setActionTextColor(getResources().getColor(R.color.color1));
                                    snackbar.setAction("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    });
                                    snackbar.show();
                                } else {
                                    getManAnswer();
                                    score = 0;
                                    for (int i = 0; i < set_problems; i++) {
                                        if (man_answer_str.get(i).equals(answer[list[i]])) {//计算正确率
                                            score = score + 1;
                                        }
                                        if (man_answer_str.get(i) != "") {
                                            finish_jindu++;
                                            if(!man_answer_str.get(i).equals(answer[list[i]])){
                                                error++;
                                            }
                                        }
                                    }
                                    radio_accuracy = ((double) score) / ((double) set_problems) * 100;
                                    AlertDialog.Builder alerDialog = new AlertDialog.Builder(game_info_activity.this);
                                    if(radio_accuracy<80){
                                        String[] mitems = { "答对题数：" + score, "正确率：" + radio_accuracy + "%"};
                                        alerDialog.setTitle("您答对的题数不满足要求").setItems(mitems,null).setPositiveButton("重做", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                                mediaPlayer.stop(); mediaPlayer.release();
                                                Intent in=new Intent(game_info_activity.this,game_info_activity.class);
                                                startActivity(in);
                                            }
                                        }).create();
                                    }else{
                                        mediaPlayer.stop();
                                        mediaPlayer.release();
                                        alerDialog.setTitle("恭喜你过关了，开始新的一天吧！").setNegativeButton("查看答案", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                flag_checked = true;submit_id.setText("返回");
                                                title_id.setText(title[0]);problems_num=0;
                                                problem_id.setText(problems[list[0]]);
                                                choice1_id.setText("A." + choice1[list[0]]);
                                                choice2_id.setText("B." + choice2[list[0]]);
                                                choice3_id.setText("C." + choice3[list[0]]);
                                                choice4_id.setText("D." + choice4[list[0]]);
                                                if (man_answer_str.get(problems_num).equals("A")){
                                                    choice1_id.setBackgroundResource(R.drawable.button_decoration);
                                                    choice2_id.setBackgroundResource(0);
                                                    choice3_id.setBackgroundResource(0);
                                                    choice4_id.setBackgroundResource(0);
                                                } else if (man_answer_str.get(problems_num).equals("B")) {
                                                    choice2_id.setBackgroundResource(R.drawable.button_decoration);
                                                    choice1_id.setBackgroundResource(0);
                                                    choice3_id.setBackgroundResource(0);
                                                    choice4_id.setBackgroundResource(0);
                                                } else if (man_answer_str.get(problems_num).equals("C")) {
                                                    choice3_id.setBackgroundResource(R.drawable.button_decoration);
                                                    choice2_id.setBackgroundResource(0);
                                                    choice1_id.setBackgroundResource(0);
                                                    choice4_id.setBackgroundResource(0);
                                                } else if (man_answer_str.get(problems_num).equals("D")) {
                                                    choice4_id.setBackgroundResource(R.drawable.button_decoration);
                                                    choice1_id.setBackgroundResource(0);
                                                    choice2_id.setBackgroundResource(0);
                                                    choice3_id.setBackgroundResource(0);
                                                }
                                                TextView daan=findViewById(R.id.answer);
                                                choice1_id.setTextColor(getResources().getColor(R.color.gray2));choice2_id.setTextColor(getResources().getColor(R.color.gray2));choice3_id.setTextColor(getResources().getColor(R.color.gray2));choice4_id.setTextColor(getResources().getColor(R.color.gray2));
                                                if(answer[list[problems_num]]=="A"){
                                                    choice1_id.setTextColor(getResources().getColor(R.color.red));
                                                    daan.setText("正确答案："+"A");
                                                }
                                                else if(answer[list[problems_num]]=="B"){
                                                    choice2_id.setTextColor(getResources().getColor(R.color.red));
                                                    daan.setText("正确答案："+"B");
                                                }
                                                else if(answer[list[problems_num]]=="C"){
                                                    choice3_id.setTextColor(getResources().getColor(R.color.red));
                                                    daan.setText("正确答案："+"C");
                                                }
                                                else if(answer[list[problems_num]]=="D"){
                                                    choice4_id.setTextColor(getResources().getColor(R.color.red));
                                                    daan.setText("正确答案："+"D");
                                                }
                                            }
                                        }).setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }).create();

                                    }
                                    alerDialog.setCancelable(false);
                                    alerDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                            if(keyCode==KeyEvent.KEYCODE_SEARCH){
                                                return true;
                                            }else{
                                                return false;
                                            }
                                        }
                                    });
                                    alerDialog.show();
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_SEARCH){
                    return true;
                }else {
                    return false;
                }
            }
        });
        alertDialog.show();
    }
        private void update() {
            choice1_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(flag_checked==false) {
                        choice1_id.setBackgroundResource(R.drawable.button_decoration);
                        man_answer[problems_num][0] = 1;
                        man_answer[problems_num][1] = 0;
                        man_answer[problems_num][2] = 0;
                        man_answer[problems_num][3] = 0;
                        choice2_id.setBackgroundResource(0);
                        choice3_id.setBackgroundResource(0);
                        choice4_id.setBackgroundResource(0);
                    }
                }
            });
            choice2_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(flag_checked==false) {
                        choice2_id.setBackgroundResource(R.drawable.button_decoration);
                        man_answer[problems_num][0] = 0;
                        man_answer[problems_num][1] = 1;
                        man_answer[problems_num][2] = 0;
                        man_answer[problems_num][3] = 0;
                        choice1_id.setBackgroundResource(0);
                        choice3_id.setBackgroundResource(0);
                        choice4_id.setBackgroundResource(0);
                    }
                }
            });
            choice3_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(flag_checked==false) {
                        choice3_id.setBackgroundResource(R.drawable.button_decoration);
                        man_answer[problems_num][0] = 0;
                        man_answer[problems_num][1] = 0;
                        man_answer[problems_num][2] = 1;
                        man_answer[problems_num][3] = 0;
                        choice1_id.setBackgroundResource(0);
                        choice2_id.setBackgroundResource(0);
                        choice4_id.setBackgroundResource(0);
                    }
                }
            });
            choice4_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(flag_checked==false) {
                        choice4_id.setBackgroundResource(R.drawable.button_decoration);
                        man_answer[problems_num][0] = 0;
                        man_answer[problems_num][1] = 0;
                        man_answer[problems_num][2] = 0;
                        man_answer[problems_num][3] = 1;
                        choice1_id.setBackgroundResource(0);
                        choice2_id.setBackgroundResource(0);
                        choice3_id.setBackgroundResource(0);
                    }
                }
            });
        }
        void getManAnswer(){
            //选出答题者的答案
            if(man_answer[problems_num][0]==1){
                man_answer_str.set(problems_num,"A");
            }
            else if(man_answer[problems_num][1]==1){
                man_answer_str.set(problems_num,"B");
            }
            else if(man_answer[problems_num][2]==1){man_answer_str.set(problems_num,"C");}
            else if(man_answer[problems_num][3]==1){man_answer_str.set(problems_num,"D");}
        }
}
