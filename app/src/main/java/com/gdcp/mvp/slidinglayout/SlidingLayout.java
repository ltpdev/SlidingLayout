package com.gdcp.mvp.slidinglayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
@CoordinatorLayout.DefaultBehavior(SlidingCardBehavior.class)
public class SlidingLayout extends FrameLayout{
    private int headerHeight;
    public SlidingLayout(@NonNull Context context) {
        this(context,null);
    }

    public SlidingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlidingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
         LayoutInflater.from(getContext()).inflate(R.layout.widget_sliding,this);
         RecyclerView rvContent= (RecyclerView) findViewById(R.id.rv_content);
         TextView tvCategory = (TextView) findViewById(R.id.tv_category);
        List<String>strings=new ArrayList<>();
        for (int i = 0; i <30; i++) {
            strings.add("名字"+i);
        }
         SimpleAdapter simpleAdapter=new SimpleAdapter(context,strings);
         rvContent.setLayoutManager(new LinearLayoutManager(context));
         rvContent.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
         rvContent.setAdapter(simpleAdapter);
         TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.SlidingLayout);
         tvCategory.setText(typedArray.getText(R.styleable.SlidingLayout_sliding_text));
         tvCategory.setBackgroundColor(typedArray.getColor(R.styleable.SlidingLayout_sliding_background,0));
         typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w!=oldw&&h!=oldh){
            headerHeight=findViewById(R.id.tv_category).getMeasuredHeight();
        }
    }

    public int getHeaderHeight(){
        return headerHeight;
    }
}
