package com.movcode.rafael.ptotosensor.Connection;

/**
 * Created by rafael on 08/12/2017.
 */


import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.movcode.rafael.ptotosensor.Helper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class BluetoothConnected  implements Runnable{
    public final String devName;
    private DataInputStream dis;
    private DataOutputStream dos;
    private final Handler handler;
    BluetoothSocket socket;
    private boolean status_send_msg = true;

    public BluetoothConnected(final BluetoothSocket bluetoothSocket, Handler handler)
    {
        devName = bluetoothSocket.getRemoteDevice().getName();
        socket = bluetoothSocket;
        this.handler = handler;
        try
        {
            dis = new DataInputStream(bluetoothSocket.getInputStream());
            dos = new DataOutputStream(bluetoothSocket.getOutputStream());
        }
        catch(IOException e){}
    }


    public void run()
    {
        String msg;
        try
        {
            while ((msg = dis.readUTF()) != "")
            {
                Helper.SendToActivity(handler, Helper.RECMSG, msg.toString());
            }

        }
        catch (IOException e){

        }
    }


    public void sendMessage(final String msg)
    {
        status_send_msg = true;
        try
        {
            dos.writeUTF(msg);
            dos.flush();
            status_send_msg = false;
        }
        catch (IOException e){}
    }


    public void Close(){
        try{
            socket.close();
            dis.close();
            dos.close();
        }catch(Exception e){

        }
    }
}
