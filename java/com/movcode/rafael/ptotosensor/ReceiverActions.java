package com.movcode.rafael.ptotosensor;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * Created by rafael on 30/11/2017.
 */

public class ReceiverActions extends BroadcastReceiver {

    Main main;
    Handler handler;
    public ReceiverActions(Handler mHandler,Main mActivity)  {
        handler = mHandler;
        main = mActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())){


            int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


            if (state == BluetoothDevice.BOND_BONDED){
//                main.adapterDevices.add(device.getName());
//                main.listAdressDevice.add(device.getAddress());
            }

            if (state == BluetoothDevice.BOND_NONE){
//                main.adapterDevices.remove(device.getName());
            }

        }

    }
}
