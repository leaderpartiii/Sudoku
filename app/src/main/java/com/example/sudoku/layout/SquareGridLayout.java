package com.example.sudoku.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridLayout;

public class SquareGridLayout extends GridLayout {

    public SquareGridLayout(Context context) {
        super(context);
    }

    public SquareGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    protected void onMeasure(int widthSpec, int heightSpec) {
//        super.onMeasure(widthSpec, heightSpec);
//        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
//        setMeasuredDimension(size, size);
//    }
}
