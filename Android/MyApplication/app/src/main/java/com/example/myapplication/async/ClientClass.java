package com.example.myapplication.async;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientClass extends Thread
{
    private BluetoothDevice device;
    private BluetoothSocket socket;
    Handler handler;

    private static final String TAG = "ClientClass";

    private AtomicBoolean running = new AtomicBoolean(false);

    boolean isRunning() {
        return running.get();
    }

    public void stop_th() {
        running.set(false);
        sendReceive.stop_th();
    }

    SendReceive sendReceive;

    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    private static final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @SuppressLint("MissingPermission")
    public ClientClass (BluetoothDevice device1, Handler handler)
    {
        device=device1;
        this.handler = handler;
        try {
            socket=device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void run()
    {
            try {
                socket.connect();
                Message message= Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);
                sendReceive = new SendReceive(socket, handler);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }

    }
}
