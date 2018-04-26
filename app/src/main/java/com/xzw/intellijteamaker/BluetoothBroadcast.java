package com.xzw.intellijteamaker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by 7212519 on 2018/3/31.
 */

public class BluetoothBroadcast extends BroadcastReceiver {

    UpdateUiListener updateUiListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        switch (action){
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                //Toast.makeText(context , "蓝牙设备已连接" , Toast.LENGTH_SHORT).show();
                updateUiListener.updateUI("设备已连接");
                break;

            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                updateUiListener.updateUI("正在搜索...");
                break;

            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE ,0)==BluetoothAdapter.STATE_DISCONNECTED){
                    updateUiListener.updateUI("未找到设备");
                }

                break;

            case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                int connectedState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,0);
                switch (connectedState){
                    case BluetoothAdapter.STATE_CONNECTED:
                        updateUiListener.updateUI("设备已连接");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        updateUiListener.updateUI("正在连接...");
                        break;
                    case BluetoothAdapter.STATE_DISCONNECTED:
                        updateUiListener.updateUI("连接已断开");
                        break;
                }

                break;

            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                Toast.makeText(context , "蓝牙设备断开连接" , Toast.LENGTH_SHORT).show();
                updateUiListener.updateUI("等待连接");
                break;
            //上面的两个链接监听，其实也可以BluetoothAdapter实现，修改状态码即可
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState){
                    case BluetoothAdapter.STATE_OFF:
                        //Toast.makeText(context , "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        //Toast.makeText(context , "蓝牙已开启"  , Toast.LENGTH_SHORT).show();
                        break;
                }
                break;


            default:
                break;
        }

    }

    public void SetOnUpdateUIListener(UpdateUiListener updateUiListener){
        this.updateUiListener = updateUiListener;
    }
}