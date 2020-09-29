package jason.tcpdemo.coms;

import android.content.Intent;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import jason.tcpdemo.funcs.FuncTcpClient;


/**
 * Created by ycx36 on 2017-07-25.
 */

public class TcpClient implements Runnable{
    private String  serverIP = null;
    private int serverPort ;
    private PrintWriter pw;
    private InputStream is;
    private DataInputStream dis;
    private boolean isRun = true;
    private Socket socket = null;
    byte buff[]  = new byte[4096];
    private String rcvMsg;
    private int rcvLen;



    public TcpClient(String ip , int port){
        this.serverIP = ip;
        this.serverPort = port;
    }

    public void closeSelf(){   //断开连接的方法
        isRun = false;
        sendBroadcast("disConnectSucceed");
    }

    public void send(String msg){  //发送数据的方法
        try {
            pw.println(msg);//edittext的内容赋给PrintWriter
            pw.flush();
//            sendBroadcast("sendMsgSucceed");
        }catch (Exception e) {
            sendBroadcast("sendMsgFalse");
        }

    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIP,serverPort);//建立一个socket
            socket.setSoTimeout(5000);
            pw = new PrintWriter(socket.getOutputStream(),true);//将PrintWriter与输出流绑定（PrintWriter中已经有数据，则输出流的数据等于PrintWriter的数据）
            is = socket.getInputStream(); //获取输入流
            dis = new DataInputStream(is);//将输入流存入数据输入流dis
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果socket不为空，则连接成功，并开始接收消息
        if (socket != null) {
            sendBroadcast("connectSucceed");
            while (isRun){  //当处于"连接状态"
                try {
                    rcvLen = dis.read(buff);  //数据输入流的数据存入rcvlen
                    rcvMsg = new String(buff,0,rcvLen,"utf-8");
                    sendBroadcast(rcvMsg); //将接收到的消息发送到主界面

                    if (rcvMsg.equals("QuitClient")){   //服务器要求客户端结束
                        isRun = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            try {
                if (pw != null) pw.close();
                if (is != null) is.close();
                if (dis != null) dis.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { //如果socket为空，则连接失败
            sendBroadcast("connectFalse");
        }

    }

    //定义一个发送广播的方法
    private void sendBroadcast(String value){
        Intent intent = new Intent();
        intent.setAction("tcpClientReceiver");
        intent.putExtra("tcpClientReceiver",value);
        FuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
    }

}
