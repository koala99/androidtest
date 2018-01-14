package com.android.hook;

import android.view.View;


public class OnClickListenerProxy implements View.OnClickListener{
    private View.OnClickListener object;
    private HookListenerContract.OnClickListener mlistener;

    public OnClickListenerProxy(View.OnClickListener object, HookListenerContract.OnClickListener listener){
        this.object = object;
        this.mlistener = listener;
    }

    @Override
    public void onClick(View v) {
        if(mlistener != null) mlistener.doInListener(v);
        if(object != null) object.onClick(v);
    }
}

