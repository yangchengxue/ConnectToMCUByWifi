package jason.tcpdemo.funcs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import jason.tcpdemo.R;

public class newView extends View {

    boolean mShowText;
    int mTextPos;
    // 设置画笔变量
    Paint mPaint1;

    //构造方法，有了构造方法才能允许在xml中使用这个控件
    public newView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes( attrs, R.styleable.newView, 0, 0);
        try {
            mShowText = a.getBoolean(R.styleable.newView_showText, false);
            mTextPos = a.getInteger(R.styleable.newView_labelPosition, 0);
        }finally {
            a.recycle();
        }
        init();

    }

    // 画笔初始化
    private void init() {

        // 创建画笔
        mPaint1 = new Paint ();
        // 设置画笔颜色为蓝色
        mPaint1.setColor(Color.BLUE);
        // 设置画笔宽度为10px
        mPaint1.setStrokeWidth(5f);
        //设置画笔模式为填充
        mPaint1.setStyle(Paint.Style.FILL);

    }



    public boolean isShowText() {
        return mShowText;
    }

    public void setShowText(boolean showText) {
        mShowText = showText;
        invalidate();  //自定义控件的属性发生改变后，控件的样子也可能发生改变，这种情况下就需要调用invalidate方法，让系统去调用view的onDraw重新绘制。
        requestLayout();//同样的，控件的属性改变可能导致控件所占的大小和形状发生改变，就需要调用requestLayout来请求获取一个新的布局位置。
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    }

    // 复写onDraw()进行绘制
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        // 获取控件的高度和宽度
        int width = getWidth();
        int height = getHeight();

        // 设置圆的半径 = 宽,高最小值的2分之1
        int r = Math.min(width, height)/2;

        // 画出圆（蓝色）
        // 圆心 = 控件的中央,半径 = 宽,高最小值的2分之1
        canvas.drawCircle(width/2,height/2,r,mPaint1);

    }


}
