package jason.tcpdemo.funcs;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import jason.tcpdemo.R;

public class Activity2 extends AppCompatActivity{

    private TestTwoService service = null;

    private boolean isBind = false;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBind = true;
            TestTwoService.MyBinder myBinder = (TestTwoService.MyBinder)binder;
            service = myBinder.getService();
            Log.i("Kathy", "ActivityB - onServiceConnected");
            int num = service.getRandomNumber();
            Log.i("Kathy", "ActivityB - getRandomNumber = " + num);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            Log.i("Kathy", "ActivityB - onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //单击了“bindService”按钮
                Intent intent = new Intent(Activity2.this, TestTwoService.class);
                intent.putExtra("from", "ActivityB");
                Log.i("Kathy", "----------------------------------------------------------------------");
                Log.i("Kathy", "ActivityB 执行 bindService");
                bindService(intent, conn, BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //单击了“unbindService”按钮
                if(isBind){
                    Log.i("Kathy", "----------------------------------------------------------------------");
                    Log.i("Kathy", "ActivityB 执行 unbindService");
                    unbindService(conn);
                }
            }
        });

        findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //单击了“Finish”按钮
                Log.i("Kathy", "----------------------------------------------------------------------");
                Log.i("Kathy", "ActivityB 执行 finish");
                finish();
            }
        });

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("Kathy", "ActivityB - onDestroy");
    }


}
