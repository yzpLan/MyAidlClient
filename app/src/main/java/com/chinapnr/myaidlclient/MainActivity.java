package com.chinapnr.myaidlclient;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.chinapnr.myaidlservice.deviceInfo.AidlDeviceInfo;
import com.chinapnr.myaidlservice.deviceService.AidlDeviceService;
import com.chinapnr.myaidlservice.pboc.AidlPBOC;
import com.chinapnr.myaidlservice.pinpad.AidlPinpad;
import com.chinapnr.myaidlservice.printer.AidlPrinter;
import com.chinapnr.myaidlservice.printer.AidlPrinterListener;

public class MainActivity extends AppCompatActivity {
    private boolean isConnected = false;
    private AidlDeviceService aidlDeviceService;
    private AidlDeviceInfo aidlDeviceInfo;
    private AidlPBOC aidlPBOC;
    private AidlPinpad aidlPinpad;
    private AidlPrinter aidlPrinter;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("TEST", "连接服务");
            aidlDeviceService = AidlDeviceService.Stub.asInterface(iBinder);
            try {
                aidlDeviceInfo = AidlDeviceInfo.Stub.asInterface(aidlDeviceService.getDeviceInfo());
                aidlPBOC = AidlPBOC.Stub.asInterface(aidlDeviceService.getPBOC());
                aidlPinpad = AidlPinpad.Stub.asInterface(aidlDeviceService.getPinpad());
                aidlPrinter = AidlPrinter.Stub.asInterface(aidlDeviceService.getPrinter());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("TEST", "服务断开" + componentName.getClassName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void bind(View view) {
        String action = "com.yzp.aidl.service";
        String packageName = "com.chinapnr.myaidlservice";
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setPackage(packageName);
        isConnected = bindService(intent, connection, Service.BIND_AUTO_CREATE);

    }

    public void unBind(View view) {
        if (isConnected) {
            unbindService(connection);
            isConnected = false;
        }
    }

    public void sendMessage(View view) {
        try {
            if (aidlDeviceService != null){
                aidlPrinter.print("ssssssss", new AidlPrinterListener.Stub() {
                    @Override
                    public void onSuccess() throws RemoteException {
                        Log.i("TEST","打印结束");
                    }
                });
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getMessage(View view) {
        try {
            if (aidlDeviceService != null) {
                String csn = aidlDeviceInfo.getCSN();
                String sn = aidlDeviceInfo.getSN();
                String vid = aidlDeviceInfo.getVID();
                String vName = aidlDeviceInfo.getVName();
                Log.i("TEST", csn);
                Log.i("TEST", sn);
                Log.i("TEST", vid);
                Log.i("TEST", vName);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isConnected) {
            unbindService(connection);
            isConnected = false;
        }
    }
}