package com.movcode.rafael.ptotosensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.movcode.rafael.ptotosensor.Connection.BluetoothConnection;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main extends AppCompatActivity {


    BluetoothAdapter adapter;
    ListView lv ;
    ArrayAdapter<String> list_device_name;
    Button btnsend,btnconnect;
    EditText msg;
    TextView tvrecmsg;
    final List<BluetoothDevice> devices = new ArrayList<>();
    BluetoothConnection bluetoothConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = BluetoothAdapter.getDefaultAdapter();

        btnsend = (Button) findViewById(R.id.btn_send);
        btnconnect = (Button) findViewById(R.id.btnconnect);
        msg = (EditText) findViewById(R.id.msg);
        tvrecmsg = (TextView) findViewById(R.id.tvrecmsg);

        list_device_name = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,0);

        lv = (ListView) findViewById(R.id.lv_devices);

        lv.setAdapter(list_device_name);


        if (adapter.isEnabled()){
            init();
        }else{
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0x1);
        }


        btnconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.connect(devices);
            }
        });


        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.broadcast(msg.getText().toString());
            }
        });
    }



    private void init(){
        Set<BluetoothDevice> paireds = adapter.getBondedDevices();
        if (paireds.size()>0){

            for(BluetoothDevice device : paireds){
                devices.add(adapter.getRemoteDevice(device.getAddress()));
            }

            bluetoothConnection = new BluetoothConnection(adapter, handler);
            bluetoothConnection.openServer();
        }
    }



    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch(message.what){
                case Helper.ST_STATUS:
                    MakeText(message.obj.toString());
                    break;

                case Helper.LISTCONNECTIONS:
                    list_device_name.add(message.obj.toString());
                    break;

                case Helper.RECMSG:
                    tvrecmsg.setText(message.obj.toString());
                    break;
            }
            return false;
        }
    });




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != 0){
            init();
        }
    }




    private void MakeText(String msg){
        Toast.makeText(this, msg , Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.disable();
        bluetoothConnection.Close();
    }
}
