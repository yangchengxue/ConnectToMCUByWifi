package jason.tcpdemo.funcs;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;


import jason.tcpdemo.R;

public class Activity1 extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        fragment2 rightFragment = (fragment2) getFragmentManager().findFragmentById(R.id.right_fragment);
        rightFragment.fun();

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment22 fragment22 = new fragment22();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager. beginTransaction();
                transaction.replace(R.id.right_layout, fragment22);
                transaction.commit();
            }
        });

        /**
         * 1 创建待添加的碎片实例
         * 2 获取FragmentManager，在activity中可以直接调用getFragmentManager方法得到
         * 3 开启一个事务，通过调用FragmentManager的beginTransaction方法开启。
         * 4 向容器内加入碎片， 一般使用replace方法实现，该方法需要传入容器的id和碎片实例。
         * 5 提交事务，调用commit方法完成。
         *
         * */
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment2 fragment2 = new fragment2();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.right_layout, fragment2);
                transaction.commit();
            }
        });


    }

}
