package com.movcode.rafael.ptotosensor;

import android.os.Handler;

/**
 * Created by rafael on 05/12/2017.
 */

public  class Helper {

    public static final String UUID = "8703259c-2e8b-47e5-a53d-2a1d15e6d2b6";
    public static final int ST_STATUS = 1;
    public static final int LISTCONNECTIONS = 2;
    public static final int RECMSG = 3;


    public static void SendToActivity(Handler handler, int status, String msg){
        handler.obtainMessage(status, msg).sendToTarget();
    }

}
