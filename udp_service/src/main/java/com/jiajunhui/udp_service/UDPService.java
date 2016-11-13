package com.jiajunhui.udp_service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Taurus on 16/11/13.
 */

public class UDPService extends Service {

    public final int PORT = 9999;

    private DatagramSocket detectSocket;
    private SocketAddress hostAddress;

    private Timer mTimer;
    private TimerTask mTask;

    private Handler mHandler;
    private DatagramSocket listenDetectSocket;

    private void initUDP() {
        try {
            mHandler = new Handler(Looper.getMainLooper());
            hostAddress = new InetSocketAddress("255.255.255.255",PORT);
            detectSocket = new DatagramSocket(PORT);
            detectSocket.setReuseAddress(false);
            detectSocket.bind(hostAddress);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startUDPBroadcast(){
        closeUDP();
        stopSend();
        initUDP();
        startUDPListener();
        mTask = new TimerTask() {
            @Override
            public void run() {
                sendUDP(getAuthStr());
            }
        };
        mTimer = new Timer(true);
        mTimer.schedule(mTask,0,2000);
    }

    /***
     * broadcast content
     * @return
     */
    private String getAuthStr(){
        UDPEvent basePackage = new UDPEvent();
        basePackage.setEventCode(EventContants.EVENT_CODE_DEVICE_AUTH);
        basePackage.setDeviceInfo(Build.MODEL);
        basePackage.setEventMessage("AUTH");
        return GsonTools.createGsonString(basePackage);
    }

    private void stopSend(){
        if(mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
        if(mTask!=null){
            mTask.cancel();
            mTask = null;
        }
    }

    private void startUDPListener() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    int listenPort = 8888;
                    byte[] buf = new byte[1024];
                    final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    listenDetectSocket = new DatagramSocket(listenPort);
                    while (true && listenDetectSocket!=null) {
                        listenDetectSocket.receive(packet);
                        //receive data
                        final String receiveData = new String(packet.getData(), 0, packet.getLength());
                        if(receiveData!=null){
                            Log.d("UDP_Send","---Auth Success---Host:" + packet.getSocketAddress().toString());
                            final UDPEvent event = GsonTools.changeGsonToBean(receiveData,UDPEvent.class);
                            if(event!=null){
                                event.setSocketAddress(packet.getSocketAddress());
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        EventBus.getDefault().post(event);
                                    }
                                });
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("UDP_Send","---listener_Exception---");
                }
            }
        }.start();

    }

    private void sendUDP(String outMessage){
        try{
            // Send packet thread
            byte[] buf;
            if (TextUtils.isEmpty(outMessage))
                return;
            buf = outMessage.getBytes();
            Log.d("UDP_Send","Send " + outMessage + " to " + hostAddress);
            // Send packet to hostAddress:9999, server that listen
            // 9999 would reply this packet
            DatagramPacket out = new DatagramPacket(buf,
                    buf.length, hostAddress);
            if(detectSocket!=null){
                detectSocket.send(out);
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("UDP_Send","---send_Exception---");
        }
    }

    private void closeUDP(){
        try {
            if(detectSocket!=null){
                stopSend();
                detectSocket.disconnect();
                detectSocket.close();
                detectSocket = null;
            }
            if(listenDetectSocket!=null){
                listenDetectSocket.disconnect();
                listenDetectSocket.close();
                listenDetectSocket = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initService();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initService() {
        if(detectSocket==null || listenDetectSocket==null){
            startUDPBroadcast();
        }
    }

    @Override
    public void onDestroy() {
        closeUDP();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new UDPBinder();
    }

    public class UDPBinder extends Binder {
        public UDPService getService(){
            return UDPService.this;
        }
    }
}
