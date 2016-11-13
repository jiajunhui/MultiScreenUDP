package com.jiajunhui.multiscreen_udp;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiajunhui.udp_service.EventContants;
import com.jiajunhui.udp_service.UDPEvent;
import com.jiajunhui.udp_service.UDPService;
import com.xapp.jjh.xui.activity.TopBarActivity;
import com.xapp.jjh.xui.inter.MenuType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends TopBarActivity {

    private TextView mTvState;
    private TextView mTvReceiveInput;
    private StringBuffer sb = new StringBuffer();

    @Override
    public View getContentView(LayoutInflater layoutInflater, ViewGroup container) {
        return inflate(R.layout.activity_main);
    }

    @Override
    public void findViewById() {
        mTvState = findView(R.id.tv_state);
        mTvReceiveInput = findView(R.id.tv_receive_input);
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        setMenuType(MenuType.TEXT,R.string.menu_main);
        setTopBarTitle("MultiScreenUDP");
        setSwipeBackEnable(false);
        setNavigationVisible(false);
        startService();
    }

    @Override
    public void onMenuClick() {
        super.onMenuClick();
        sb = new StringBuffer("");
        mTvReceiveInput.setText(sb.toString());
    }

    @Subscribe
    public void onEvent(UDPEvent event){
        switch (event.getEventCode()){
            case EventContants.EVENT_CODE_DEVICE_AUTH:
                mTvState.setText("已连接:" + event.getDeviceInfo());
                break;

            case EventContants.EVENT_CODE_INPUT:
                sb.append(event.getEventMessage()).append("\n");
                mTvReceiveInput.setText(sb.toString());
                break;

            case EventContants.EVENT_CODE_DIALOG:
                showDialogSingleButton(event.getEventMessage(),null);
                break;
        }
    }

    private void startService() {
        Intent intent = new Intent(this, UDPService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
