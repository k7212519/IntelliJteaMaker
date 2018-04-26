package com.xzw.intellijteamaker;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static int receivedFlag = 2;
    private static int count = 0;
    private static int ringtoneCount = 0;
    private int time = 0;
    private Tea tea;
    private TextView mTimer;
    private TextView textView_time;
    private Button mButton;
    private ViewPager mViewPager;
    private Toolbar mToolBar;
    private ImageButton btn_search;
    private ImageButton btn_menu;
    private ImageButton btn_ringtone;
    private TextView connectStatus;
    private ImageButton btn_cup;
    private TextView temperature_text;
    private RelativeLayout relativeLayout;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private Vibrator vibrator;
    private NotificationManager notificationManager;
    private Notification.Builder mBuilder;



    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化ui
        initView();
        initData();
        //打开蓝牙
        turnOnBluetooth();
        //开始蓝牙服务
        startService(new Intent(MainActivity.this,BlueService.class));
        //注册蓝牙状态广播
        BroadcastReceiverRegister();
        BroadcastReceiverUIRegister();

        bluetoothAdapter.startDiscovery();

        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.custom_makeTea));

        mViewPager.setAdapter(mCardAdapter);

        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(2);
        mCardShadowTransformer.enableScaling(true);
        mCardShadowTransformer.SetOnUpdateUIListener(new UpdateUiListener() {
            @Override
            public void updateUI(String string) {

            }

            @Override
            public void updateUI(int integer) {

                Log.i("info",String.valueOf(integer));
                relativeLayout.setBackgroundColor(integer);
            }
        });

        btn_cup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count ++;
                if (count>200){
                    Intent intent = new Intent(MainActivity.this,info.class);
                    startActivity(intent);
                    count = 0;
                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     *
     * @param keyCode 返回键不关闭当前activity
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        Intent intent_btn_action = new Intent();
        vibrator.vibrate(12);
        switch (view.getId()){
            case R.id.search:
                System.out.println("搜索被点击");
                if (!bluetoothAdapter.isEnabled()){
                    turnOnBluetooth();
                }
                bluetoothAdapter.startDiscovery();
                connectStatus.setText("正在搜索...");

                break;
            case R.id.menu:
                System.out.println("菜单被点击");
                showPopupMenu(view);

                //intent_btn_action.setAction("com.xzw.intellijteamaker.action.bluetoothDiscoverable");
                //sendBroadcast(intent_btn_action);
                break;
            case R.id.ringtone:
                Log.i("TAG","提醒被点击了");
                if (ringtoneCount == 0){
                    btn_ringtone.setImageResource(R.drawable.ringtone_shock);
                    ringtoneCount = 1;
                    vibrator.vibrate(1000);
                }else if (ringtoneCount == 1){
                    btn_ringtone.setImageResource(R.drawable.ringtone_no);
                    ringtoneCount = 2;
                }else if (ringtoneCount == 2){
                    btn_ringtone.setImageResource(R.drawable.ringtone);
                    ringtoneCount = 0;
                }
                break;
            case R.id.start_onekey_btn:
                Log.i("TAG","onkey被点击了");
                //sendIntMessage(intent_btn_action,0,1);
                sendIntMessage(intent_btn_action,1,mCardAdapter.getTea().getTemperature());
                //sendIntMessage(intent_btn_action,2,mCardAdapter.getTea().getWashTime());                  洗茶先不用
                sendIntMessage(intent_btn_action,0,mCardAdapter.getTea().getMakeTime());
                //sendIntMessage(intent_btn_action,4,mCardAdapter.getTea().getMakeFrequency());             次数先不用

                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        //mCardShadowTransformer.enableScaling(b);
        //mFragmentCardShadowTransformer.enableScaling(b);
    }

    public void initView(){
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mButton = (Button) findViewById(R.id.cardTypeBtn);
        ((CheckBox) findViewById(R.id.checkBox)).setOnCheckedChangeListener(this);
        mButton.setOnClickListener(this);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        mTimer = (TextView) findViewById(R.id.timer);
        textView_time = (TextView) findViewById(R.id.time_value);
        btn_menu = (ImageButton) findViewById(R.id.menu);
        btn_search = (ImageButton) findViewById(R.id.search);
        btn_ringtone = (ImageButton) findViewById(R.id.ringtone);
        connectStatus = (TextView) findViewById(R.id.connect_status);
        btn_cup = (ImageButton) findViewById(R.id.cup_main);
        temperature_text = (TextView) findViewById(R.id.temperature);
        relativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayout_id);
    }

    public void initData(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(this);
        mBuilder.setContentText("泡茶完成了！")
        .setSmallIcon(R.mipmap.ic_launcher)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
        .setContentTitle("泡茶完成通知");

        vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mCardAdapter = new CardPagerAdapter(this);
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
    }


    public void turnOnBluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()){
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),0);
        }
    }

    public void sendStringMessage(Intent intent,int message_tag,String message){
        intent.setAction("com.xzw.intellijteamaker.action.bluetoothSendStringMessage");
        intent.putExtra("MessageNameString",message);
        intent.putExtra("MessageNameStringTag",message_tag);
        sendBroadcast(intent);
    }

    public void sendIntMessage(Intent intent,int message_tag,int message){
        intent.setAction("com.xzw.intellijteamaker.action.bluetoothSendIntMessage");
        intent.putExtra("MessageNameInt",message);
        intent.putExtra("MessageNameIntTag",message_tag);
        sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果不打开蓝牙则关闭程序
        if(resultCode == RESULT_CANCELED){
            finish();
        }else if (resultCode == RESULT_OK){
            bluetoothAdapter.startDiscovery();
        }
    }

    public void ReceivedMessageManage(int message){

        if(receivedFlag == 2){              //默认值2      0后接收时间 1后接收温度
            if(message == 0){
                receivedFlag = 0;
            }else if (message == 1){
                receivedFlag = 1;
            }else if (message == 2){        //接收到02后的事件
                notificationManager.notify(1, mBuilder.build());
                mCardAdapter.changeViewVisible();
                switch (ringtoneCount){
                    case 0:
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                        break;
                    case 1:
                        long[] pattern = {100, 500, 100, 500,100,500, 100, 500, 100,500,100,500};
                        vibrator.vibrate(pattern,-1);
                        break;
                    case 2:
                        break;
                }
            }
        }else if(receivedFlag == 0){        //接收到00 xx后的事件 时间
            time = message;
            textView_time.setText(String.valueOf(time));
            Log.i("info","接收到的预计时间"+String.valueOf(time));
            mCardAdapter.changeViewInvisible(message);
            receivedFlag = 2;
        }else if(receivedFlag == 1){        //接收到01 xx后的事件  温度
            temperature_text.setText(String.valueOf(message));
            Log.i("info","目标温度："+String.valueOf(message));
            receivedFlag = 2;
        }
    }

    private void showPopupMenu(View view) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
        popupMenu.show();
        // 把控件显示出来
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 控件每一个item的点击事件
                switch (item.getItemId()){
                    case R.id.exit:
                        finish();
                        break;
                    case R.id.about:
                        Intent intent = new Intent(MainActivity.this,about.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // 控件消失时的事件
            }
        });

    }


    /**
     * 注册蓝牙状态监听广播
     */
    public void BroadcastReceiverRegister(){
        BluetoothBroadcast bluetoothBroadcast = new BluetoothBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(bluetoothBroadcast,intentFilter);
        bluetoothBroadcast.SetOnUpdateUIListener(new UpdateUiListener() {
            @Override
            public void updateUI(String string) {
                connectStatus.setText(string);
            }

            public void updateUI(int integer){

            }
        });
    }

    /**
     * UI更新广播注册
     */
    public void BroadcastReceiverUIRegister(){
        BroadcastReceiverUI broadcastReceiverUI = new BroadcastReceiverUI();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.xzw.intellijteamaker.action.received_message");
        registerReceiver(broadcastReceiverUI,intentFilter);
    }




    /**
     * 注册ui更新广播（有待改进）用handler
     */
    public class BroadcastReceiverUI extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = intent.getIntExtra("message",0);
            switch (intent.getAction()){
                case "com.xzw.intellijteamaker.action.received_message":
                    Log.i("info","接收到了接收广播");

                    ReceivedMessageManage(message);
                    //temperature_text.setText(String.valueOf(message));
                    break;
                case "com.xzw.intellijteamaker.action.viewpager_transform":

                    break;
            }
        }
    }
}
