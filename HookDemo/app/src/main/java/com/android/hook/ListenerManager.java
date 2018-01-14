package com.android.hook;

public class ListenerManager {
    public HookListenerContract.OnClickListener mOnClickListener;

    private ListenerManager() {
    }


    public static ListenerManager create(Builer builer) {
        if (builer == null) {
            return null;
        }
        return builer.build();
    }

    public static class Builer {
        private ListenerManager listenerManager = new ListenerManager();


        public Builer buildOnClickListener(HookListenerContract.OnClickListener onClickListener) {
            listenerManager.mOnClickListener = onClickListener;
            return this;
        }

        public ListenerManager build() {
            return listenerManager;
        }
    }
}
