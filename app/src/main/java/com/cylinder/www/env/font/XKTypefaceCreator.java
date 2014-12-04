package com.cylinder.www.env.font;


import android.content.Context;

/**
 * Created by hipilee on 2014/11/21.
 */
public class XKTypefaceCreator implements InterfaceTypefaceCreator {
    @Override
    public InterfaceTypeface createTypeface(Context context) {
        return XKTypeface.getInstance(context);
    }
}
