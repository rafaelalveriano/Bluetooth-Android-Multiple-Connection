package com.movcode.rafael.ptotosensor.Connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.movcode.rafael.ptotosensor.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by rafael on 08/12/2017.
 */

public class BluetoothConnection {
    private final BluetoothAdapter adapter;
    private final ArrayList<BluetoothConnected> openSocket = new ArrayList<>();
    private final ArrayList<UUID> uuidsList = new ArrayList<>();
    private final Handler handler;

    Server server;
    ConnectClient client;


    public BluetoothConnection(BluetoothAdapter adapter, Handler mHandler) {
        this.adapter = adapter;
        handler = mHandler;

        uuidsList.add(UUID.fromString(Helper.UUID));
        uuidsList.add(UUID.fromString(Helper.UUID));
    }



    public void openServer(){
        server = new Server();
        new Thread(server).start();
    }

    public void connect(final List<BluetoothDevice> devices) {
        client = new ConnectClient(devices);
        new Thread(client).start();
    }

    public void broadcast(final String msg) {
        for (BluetoothConnected connected : openSocket)
            connected.sendMessage(msg);
    }


    //Server
    private class Server implements Runnable {
        private int slave_number;
        private BluetoothConnected bluetoothConnected;
        private BluetoothServerSocket serverSocket;
        private BluetoothSocket bluetoothSocket;

        @Override
        public void run() {
            Helper.SendToActivity(handler,Helper.ST_STATUS,"Aguardando conexão...");
            for (slave_number = 0; slave_number < uuidsList.size(); slave_number++) {
                try {
                    Helper.SendToActivity(handler,Helper.ST_STATUS,"Socket iniciado!");
                    serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord("BLUETOOTH_PICONET", uuidsList.get(slave_number));
                    bluetoothSocket = serverSocket.accept();
                    serverSocket.close();
                    bluetoothConnected = new BluetoothConnected(bluetoothSocket, handler);
                    Helper.SendToActivity(handler,Helper.LISTCONNECTIONS,"Cliente: "+bluetoothSocket.getRemoteDevice().getName());
                    Helper.SendToActivity(handler,Helper.ST_STATUS,"Socket Fechado!");
                    new Thread(bluetoothConnected).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


        public void closeServer() throws Exception{
            if (bluetoothSocket.isConnected() ){
                bluetoothSocket.close();
                bluetoothConnected.Close();
                Helper.SendToActivity(handler,Helper.ST_STATUS,"Conexão Fechada!");
            }
        }

    }





    //Client
    private class ConnectClient implements Runnable
    {
        private BluetoothSocket tempSocket;
        private BluetoothConnected connection;
        private ArrayList<UUID> uuidAvailable = new ArrayList<>(uuidsList);
        private final List<BluetoothDevice> deviceList;

        public ConnectClient(List<BluetoothDevice> devices)
        {
            deviceList = devices;
        }

        @Override
        public void run()
        {
            connectToDevice();
        }

        private void connectToDevice()
        {
            for(BluetoothDevice device: deviceList)
                for (int uuidIndex = 0; uuidIndex < uuidAvailable.size(); uuidIndex++) {
                    try {
                        tempSocket = device.createInsecureRfcommSocketToServiceRecord(uuidAvailable.get(uuidIndex));
                        tempSocket.connect();
                        connection = new BluetoothConnected(tempSocket, handler);
                        openSocket.add(connection);
                        new Thread(connection).start();
                        Helper.SendToActivity(handler,Helper.LISTCONNECTIONS,"Servidor: "+tempSocket.getRemoteDevice().getName());
                        uuidAvailable.remove(uuidIndex);
                        break;
                    } catch (IOException e) {
                    }
                }
        }


        public void closeClient() throws Exception{
            if (tempSocket.isConnected() ){
                tempSocket.close();
                connection.Close();
                Helper.SendToActivity(handler,Helper.ST_STATUS,"Conexão Fechada!");
            }
        }
    }


    public void Close(){
        try{
            server.closeServer();
            client.closeClient();
        }catch(Exception e){
        }
    }

}


