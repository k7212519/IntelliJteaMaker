package com.xzw.intellijteamaker;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BlueService extends Service {
    public BlueService() {
    }
    //与MainActivity交互的ACTION
    private static final String BluetoothSearch = "com.xzw.intellijteamaker.action.bluetoothsearch";
    private static final String BluetoothTurnOff = "com.xzw.intellijteamaker.action.bluetoothturnoff";
    private static final String BluetoothTurnOn = "com.xzw.intellijteamaker.action.bluetoothturnon";
    private static final String BluetoothDiscoverable = "com.xzw.intellijteamaker.action.bluetoothDiscoverable";
    private static final String BluetoothSendStringMessage = "com.xzw.intellijteamaker.action.bluetoothSendStringMessage";
    private static final String BluetoothSendIntMessage = "com.xzw.intellijteamaker.action.bluetoothSendIntMessage";

    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public boolean bluetoothFlag = false;
    public OutputStream outputStream = null;
    public InputStream inputStream = null;




    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    private Handler handler;





    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        handler = new Handler();

        //按键响应广播注册
        BroadcastReceiverButtonRegister();
        //蓝牙状态监听广播注册
        BluetoothBroadcastReceiverRegister();


        new Thread(){
            @Override
            public void run() {
                super.run();

                //byte[] buffer = new byte[1];
                int bytes;
                Intent intent = new Intent();
                intent.setAction("com.xzw.intellijteamaker.action.received_message");

                while (true){

                    if (inputStream!=null){


                        //System.out.println("服务线程正在运行");

                        try {
                            // Read from the InputStream
                            bytes = inputStream.read();
                            intent.putExtra("message",bytes);
                            sendBroadcast(intent);
                            Log.i("info","收到了消息"+bytes);

                            // Send the obtained bytes to the UI Activity

                        } catch (IOException e) {
                                break;
                        }
                    }
                }

            }
        }.start();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void turnOnBluetooth(){
        if (!bluetoothAdapter.isEnabled()){
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }
    }

    public void BroadcastReceiverButtonRegister(){
        BroadcastReceiverButton broadcastReceiverButton = new BroadcastReceiverButton();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothSearch);
        intentFilter.addAction(BluetoothTurnOff);
        intentFilter.addAction(BluetoothDiscoverable);
        intentFilter.addAction(BluetoothSendStringMessage);
        intentFilter.addAction(BluetoothSendIntMessage);
        registerReceiver(broadcastReceiverButton,intentFilter);
    }

    public void BluetoothBroadcastReceiverRegister(){
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(bluetoothBroadcastReceiver,intentFilter);
    }

    public void connectToDevice(){
        if(bluetoothDevice.getName()!=null){
            Log.i("name:",bluetoothDevice.getName());
            //根据name自动建立连接
            if (bluetoothDevice.getName().contains("S8")||bluetoothDevice.getName().contains("HC")){
                //多个设备则会自动连接第一个，有待改善
                bluetoothAdapter.cancelDiscovery();
                if(bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    bluetoothDevice.createBond();
                } else if(bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    try {
                        bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                        Log.i("info","获取socket正常");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("error","建立socket异常");
                    }
                    try {
                        bluetoothSocket.connect();
                        bluetoothFlag = true;
                        Log.i("info","建立socket正常");
                    } catch (IOException e) {
                        e.printStackTrace();
                        bluetoothFlag = false;
                        Log.e("error","建立socket异常");
                    }
                }
            }
        }

        if(bluetoothFlag) {
            try {
                outputStream = bluetoothSocket.getOutputStream();
                Log.i("info", "得到输出流成功");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("error","得到输出流失败");
            }
            try {
                inputStream = bluetoothSocket.getInputStream();
                Log.i("info","得到输入流成功");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("error","得到输入流失败");
            }
        }
    }

    public void requestDiscoverable(){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivity(intent);
    }

    public void sendMessage(int message_tag,int message){
        if(outputStream!=null){
            try {
                //outputStream.write(255);
                outputStream.write(message_tag);
                outputStream.write(message);
                //outputStream.write(254);
                Log.i("info","发送"+message_tag+"成功");
                Log.i("info","发送"+message+"成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void sendMessage(int message_tag,String message){
        if (outputStream!=null){
            try {
                //outputStream.write(255);
                outputStream.write(message_tag);
                outputStream.write(message.getBytes("UTF-8"));
                //outputStream.write(254);
                Log.i("info","发送"+message_tag+"成功");
                Log.i("info","发送"+message+"成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class BroadcastReceiverButton extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BluetoothSearch:
                    turnOnBluetooth();
                    break;
                case BluetoothTurnOff:
                    bluetoothAdapter.disable();
                    break;
                case BluetoothTurnOn:
                    turnOnBluetooth();
                    break;
                case BluetoothDiscoverable:
                    requestDiscoverable();
                    break;
                case BluetoothSendStringMessage:
                    sendMessage(intent.getIntExtra("MessageNameStringTag",0),intent.getStringExtra("MessageNameString"));
                    break;
                case BluetoothSendIntMessage:
                    sendMessage(intent.getIntExtra("MessageNameIntTag",0),intent.getIntExtra("MessageNameInt",0));
                    break;

                default:
                    break;
            }
        }
    }

    public class BluetoothBroadcastReceiver extends BroadcastReceiver {
        int count = 0;
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()){
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    //开始搜索了
                    System.out.println("开始搜索");
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    //搜索完成
                    System.out.println("完成搜索");
                    break;

                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    //方便绑定后自动连接
                    bluetoothAdapter.startDiscovery();
                    break;

                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)){
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(context , "蓝牙已开启"  , Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(context , "蓝牙已关闭"  , Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;

                case BluetoothDevice.ACTION_FOUND:
                    //找到了设备
                    bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    ((IntellijTeaMaker)getApplication()).getDevice_address().add(bluetoothDevice.getAddress());
                    ((IntellijTeaMaker)getApplication()).getDevice_name().add(bluetoothDevice.getName());
                    Log.i("address:",bluetoothDevice.getAddress());
                    connectToDevice();
                    break;
                default:
                    break;

            }
        }
    }

}
