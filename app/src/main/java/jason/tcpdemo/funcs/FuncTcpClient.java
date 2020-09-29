package jason.tcpdemo.funcs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jason.tcpdemo.R;
import jason.tcpdemo.coms.TcpClient;


/**
 * Created by ycx36 on 2017-07-24.
 */

public class FuncTcpClient extends Activity {
    @SuppressLint("StaticFieldLeak")
    public static Context context ;
    private Button btnStartClient;
    private Button btnCloseClient;
    private Button bt_send_auto;
    private Button bt_send;
    private TextView et_1,et_2,et_3,et_4;
    private EditText editClientPort,editClientIp,et_send;
    private static TcpClient tcpClient = null;
    private MyBtnClicker myBtnClicker = new MyBtnClicker();
    private final MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private int ifAutoSend = 0; //0关闭 1开启
    ExecutorService exec = Executors.newCachedThreadPool();

    private class MyBtnClicker implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_tcpClientConn:   //点击“连接”按钮
//                    tcpClient = new TcpClient(editClientIp.getText().toString(),getPort(editClientPort.getText().toString()));//IP地址和端口号传入tcpClient
                    tcpClient = new TcpClient("192.168.1.1",8080);//IP地址和端口号传入tcpClient
                    exec.execute(tcpClient); //创建一个缓存线程池给tcpClient
                    break;
                case R.id.btn_tcpClientClose:  //”断开“按钮
                    tcpClient.closeSelf();  //断开连接
                    break;
                case R.id.btn_tcpCleanClientSend:  //”清空“按钮
//                    et_wd.setText("");
//                    et_ph.setText("");
//                    et_tds.setText("");
//                    et_op.setText("");
                   //et_cs.setText("");
                    break;
                case R.id.bt_send:
                    fas(et_send.getText().toString());
                    break;
                case R.id.bt_send_auto:
                    if (ifAutoSend == 0){
                        showAddNewTagDialog();
                    } else if (ifAutoSend == 1) {
                        ifAutoSend = 0;
                        bt_send.setVisibility(View.VISIBLE);
                        bt_send_auto.setText("自动发送");
                    }
                    break;
            }
        }
    }

    //发送数据
    public void fas(final String aa){
        if (!aa.equals("")){
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    if (tcpClient != null) tcpClient.send(aa);
                    else sendBroadcast("sendMsgFalse");
                }
            });
        } else {
            Toast.makeText(FuncTcpClient.this,"输入不能为空",Toast.LENGTH_SHORT).show();
        }

    }


    int flag = 1;
    Queue<String> queue = new LinkedList<>();

    //自定义类MyHandler
    @SuppressLint("HandlerLeak")
    private class MyHandler extends android.os.Handler{
        private WeakReference<FuncTcpClient> mActivity;

        MyHandler(FuncTcpClient activity){
            mActivity = new WeakReference<FuncTcpClient>(activity);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {  //从消息队列中取出消息
            if (mActivity != null){
                switch (msg.what){
                    case 0:
                        Toast.makeText(FuncTcpClient.this,"连接失败",Toast.LENGTH_SHORT).show(); //连接失败
                        break;
                    case 1:
//                        et_cs.append(msg.obj.toString() + "\n"); //设置接收到的参数
                        String response = msg.obj.toString();
                        et_1.setText(convertParam(response)[0]);
                        et_2.setText(convertParam(response)[1]);
                        et_3.setText(convertParam(response)[2]);
                        et_4.setText(convertParam(response)[3]);
//                        if (flag <= 5) {
//                            queue.offer(response);
//                            et_cs.append(response);
//                            flag++;
//                        } else {
//                            queue.poll();
//                            et_cs.setText("");
//                            for (int i = 0; i < 4; i++) {
//                                String x = queue.poll();
//                                et_cs.append(x);
//                                queue.offer(x);
//                            }
//                            queue.offer(response);
//                            et_cs.append(response);
//                        }
                        break;
                    case 3:
                        Toast.makeText(FuncTcpClient.this,"连接成功",Toast.LENGTH_SHORT).show();
                        btnStartClient.setVisibility(View.GONE);
                        btnCloseClient.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        Toast.makeText(FuncTcpClient.this,"已成功断开连接",Toast.LENGTH_SHORT).show();
                        btnCloseClient.setVisibility(View.GONE);
                        btnStartClient.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        Toast.makeText(FuncTcpClient.this,"发送成功",Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        Toast.makeText(FuncTcpClient.this,"发送失败，请检查是否连接成功",Toast.LENGTH_SHORT).show();
                        break;
                    case 110: //成功设置自动发送
                        bt_send.setVisibility(View.GONE);
                        bt_send_auto.setText("点击停止");
                        break;
                }
            }
        }
    }

    //广播接收者
    private class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            if ("tcpClientReceiver".equals(mAction)) {
                String msg = intent.getStringExtra("tcpClientReceiver");
                Message message = Message.obtain();
                if (msg.equals("connectFalse")) {
                    message.what = 0;
                } else if (msg.equals("connectSucceed")) {
                    message.what = 3;
                } else if (msg.equals("disConnectSucceed")) {
                    message.what = 4;
                } else if (msg.equals("sendMsgSucceed")) {
                    message.what = 5;
                } else if (msg.equals("sendMsgFalse")) {
                    message.what = 6;
                }else { //接收到的从服务端传递过来的数据
                    message.what = 1;
                }
                message.obj = msg;
                myHandler.sendMessage(message);
            }
        }
    }

    int i = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tcp_client);
        context = this;
        init();
        bindReceiver();
//        final String s = "ph: 3.4_tds:   5.1_do:  4.5_t: 8.8";
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            et_1.setText(convertParam(s)[0]);
//                            et_2.setText(convertParam(s)[1]);
//                            et_3.setText(convertParam(s)[2]);
//                            et_4.setText(convertParam(s)[3]);
//                        }
//                    });
//
//                }
//            }
//        }).start();
    }

    //初始化控件
    private void init(){
        btnStartClient =  findViewById(R.id.btn_tcpClientConn);
        btnCloseClient =  findViewById(R.id.btn_tcpClientClose);
        bt_send_auto =  findViewById(R.id.bt_send_auto);
        Button btn_tcpCleanClientSend = findViewById(R.id.btn_tcpCleanClientSend);
        bt_send = findViewById(R.id.bt_send);
        editClientPort =  findViewById(R.id.edit_tcpClientPort);
        editClientIp = findViewById(R.id.edit_tcpClientIp);
        et_send = findViewById(R.id.et_send);
        et_1 =  findViewById(R.id.et_1);
        et_2 = findViewById(R.id.et_2);
        et_3 =  findViewById(R.id.et_3);
        et_4 =  findViewById(R.id.et_4);
       // et_cs =  findViewById(R.id.et_cs);

        btnStartClient.setOnClickListener(myBtnClicker);
        btnCloseClient.setOnClickListener(myBtnClicker);
        bt_send_auto.setOnClickListener(myBtnClicker);
        btn_tcpCleanClientSend.setOnClickListener(myBtnClicker);
        bt_send.setOnClickListener(myBtnClicker);
    }

    private void bindReceiver(){
        IntentFilter intentFilter = new IntentFilter("tcpClientReceiver");
        registerReceiver(myBroadcastReceiver,intentFilter);
    }

    //获取端口号
    private int getPort(String msg){
        if (msg.equals("")){
            msg = "8080";
        }
        return Integer.parseInt(msg);
    }

    //定义一个发送广播的方法
    private void sendBroadcast(String value){
        Intent intent = new Intent();
        intent.setAction("tcpClientReceiver");
        intent.putExtra("tcpClientReceiver",value);
        FuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
    }

    //提示对话框
    private void showAddNewTagDialog(){
        View view = LayoutInflater.from(FuncTcpClient.this).inflate(R.layout.dialog_addnewtag, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(FuncTcpClient.this);//创建一个提示对话框的构造者
        builder.setTitle("输入时间间隔(秒)(必须为正整数)");  //设置对话框的标题
        builder.setView(view); // 设置显示的view
        final EditText et_input = view.findViewById(R.id.et_input);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String tag = et_input.getText().toString();
                if (!tag.equals("")){ //如果输入的内容不为空
                    Message message = Message.obtain();
                    message.what = 110;
                    myHandler.sendMessage(message);
                    ifAutoSend = 1;
                    exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            while (ifAutoSend == 1) {
                                if (tcpClient != null) tcpClient.send(et_send.getText().toString());
                                else sendBroadcast("sendMsgFalse");
                                try {
                                    Thread.sleep(Integer.valueOf(tag) * 1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(FuncTcpClient.this,"设置失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置反面的按钮，输入new DialogInterface.OnClickListener()对自动弹出方法。
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private String[] convertParam(String s){
        String ph = s.substring(3,7);
        String tds = s.substring(12,18);
        String d = s.substring(22,27);
        String t = s.substring(30,34);
        String[] res = new String[4];
        res[0] = ph;
        res[1] = tds;
        res[2] = d;
        res[3] = t;
        return res;
    }

    /**
     * HashMap是一种以键值对存储数据的数据结构，在jdk1.8之前，HashMap是以数组＋链表实现的，在1.8之后，以数组＋链表＋红黑树实现的。
     * 在进行数据存储的时候，通过hash函数对数据的key计算，得到对应的哈希值，然后通过hash值和数组容量计算得到这个数据在数组中的下标。
     * 使用不同的key计算hash值的时候，可能会得到相同的哈希值，这时候就会产生哈希碰撞，从而导致下标冲突，对于下标冲突的数据，就会在该下标出以链表的形式存放数据。
     *
     * hash函数流程，如果key为null，则返回0，否则，对key进行hashCode()计算，得到的值与其右移16位的值异或，然后返回异或的结果。异或的目的是为什么减少哈希碰撞。
     * index函数，通过hash值和数组容量-1的值按位与得到。
     * HashMap的容量一定是2的n次幂，因为在计算下标的时候，需要计算hash值和数组容量的大小-1的值按位与，在容量为2的n次幂的情况下，
     * 能保证容量-1的值的二进制除第一位以外全为1，在进行按位与的时候，能使hash值充分散列，这个能最大程度减少哈希碰撞。
     *
     * 负载因子0.75，如果数组的被占用达到百分之75，就会进行数组扩容，扩容的大小为原来的两倍。扩容需要重新计算下标，在多线程情况下扩容可能会导致死循环。jkd1.8之前。
     *
     * */
}
