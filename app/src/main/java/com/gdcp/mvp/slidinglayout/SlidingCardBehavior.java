package com.gdcp.mvp.slidinglayout;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

public class SlidingCardBehavior extends CoordinatorLayout.Behavior<SlidingLayout> {

    private int mInitialOffset;

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout parent, @NonNull SlidingLayout child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
       int shift=scroll(child,dyUnconsumed,mInitialOffset,mInitialOffset+child.getHeight()-child.getHeaderHeight());
        //控制上边和下边child的移动
        shiftSliding(shift,parent,child);
    }


    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout parent, @NonNull SlidingLayout child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        consumed[1]=scroll(child,dy,mInitialOffset,mInitialOffset+child.getHeight()-child.getHeaderHeight());
        //控制上边和下边child的移动
        shiftSliding(consumed[1],parent,child);
    }

    private void shiftSliding(int shift, CoordinatorLayout parent, SlidingLayout child) {
        if (shift==0){
            return;
        }
        if (shift>0){
            //往上推
            SlidingLayout current=child;
            SlidingLayout card=getPreviousChild(parent,current);
            while (card!=null){
                //现在current 已经移动到一截，但是后面的card没有移动，那么他们有一个重叠高度就是offset
                int offset=getHeaderOverlap(card,current);
                if (offset>0){
                    card.offsetTopAndBottom(-offset);
                }
                current=card;
                card=getPreviousChild(parent,current);
            }
        }else {
            //往下推 推动下面所有卡片，找到下面所有卡片
            SlidingLayout current=child;
            SlidingLayout card=getNextChild(parent,current);
            while (card!=null){
                //现在current 已经移动到一截，但是后面的card没有移动，那么他们有一个重叠高度就是offset
                int offset=getHeaderOverlap(current,card);
               if (offset>0){
                   card.offsetTopAndBottom(offset);
               }
                current=card;
                card=getNextChild(parent,current);
            }
        }
    }

    private int getHeaderOverlap(SlidingLayout above, SlidingLayout below) {
        return above.getTop()+above.getHeaderHeight()-below.getTop();
    }

    private SlidingLayout getNextChild(CoordinatorLayout parent, SlidingLayout child) {
        int selfIndex=parent.indexOfChild(child);
        for (int i = selfIndex+1; i <parent.getChildCount(); i++) {
            View v=parent.getChildAt(i);
            if (v instanceof SlidingLayout){
                return (SlidingLayout) v;
            }
        }
        return null;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull SlidingLayout child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        boolean isVertical = (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != -1;
        return isVertical && child == directTargetChild;
    }

    @Override
    public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull SlidingLayout child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        //当前控件的高度=父容器的高度-上边和下面几个child
        //offset:上边和下面几个child的头部的高度
        int offset = getChildMeasureOffset(parent, child);
        int heightSize = View.MeasureSpec.getSize(parentHeightMeasureSpec) - offset;
        //每一个卡片的高度是变化的;
        child.measure(parentWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.EXACTLY));
        return true;


    }

    private int getChildMeasureOffset(CoordinatorLayout parent, SlidingLayout child) {
        int offset = 0;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view != child && view instanceof SlidingLayout) {
                offset += ((SlidingLayout) view).getHeaderHeight();
            }
        }
        return offset;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull SlidingLayout child, int layoutDirection) {
        //先摆放parent里面的所有子控件
        parent.onLayoutChild(child, layoutDirection);
        //给里面的child做一个偏移
        //拿到上面的child,获取每一个child头部的高度相加
        SlidingLayout previous = getPreviousChild(parent, child);
        if (previous != null) {
            int offset = previous.getTop() + previous.getHeaderHeight();
            child.offsetTopAndBottom(offset);
        }
        mInitialOffset = child.getTop();
        return true;
    }

    private SlidingLayout getPreviousChild(CoordinatorLayout parent, SlidingLayout child) {
        int selfIndex=parent.indexOfChild(child);
        for (int i = selfIndex-1; i >=0 ; i--) {
            View v=parent.getChildAt(i);
            if (v instanceof SlidingLayout){
                return (SlidingLayout) v;
            }
        }
        return null;
    }

    private int scroll(SlidingLayout child,int dy,int miniOffset,int maxOffset){
        //控制自己的移动
        int initialOffset=child.getTop();
        int offset=clamp(initialOffset-dy,miniOffset,maxOffset)-initialOffset;
        child.offsetTopAndBottom(offset);
        //控制上边和下边的child的移动
        return -offset;
    }

    private int clamp(int i,int minOffset,int maxOffset){
        if (i>maxOffset){
            return maxOffset;
        }else if (i<minOffset){
            return minOffset;
        }else {
            return i;
        }
    }
}
