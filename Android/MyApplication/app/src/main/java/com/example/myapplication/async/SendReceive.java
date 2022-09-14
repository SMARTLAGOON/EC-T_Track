package com.example.myapplication.async;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class SendReceive extends Thread
{
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    Handler handler;

    static final int STATE_MESSAGE_RECEIVED=5;

    public SendReceive (BluetoothSocket socket, Handler handler)
    {
        this.handler = handler;
        bluetoothSocket=socket;
        InputStream tempIn=null;
        OutputStream tempOut=null;

        try {
            tempIn=bluetoothSocket.getInputStream();
            tempOut=bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputStream=tempIn;
        outputStream=tempOut;
        running.set(true);
    }
    private static final String TAG = "SendReceive";

    private AtomicBoolean running = new AtomicBoolean(false);

    boolean isRunning() {
        return running.get();
    }

    public void stop_th() {

        try {
            bluetoothSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        running.set(false);
    }


    public void run()
    {
        byte[] buffer=new byte[1024];
        int bytes;
        while (running.get())
        {
            try {
                String msg = "6q4SVdGd3832";
                write(msg.getBytes());
                bytes=inputStream.read(buffer);
                String tempMsg=new String(buffer,0,bytes);
                handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                Thread.sleep(1500);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                stop_th();
            }
        }
    }

    public void write(byte[] bytes)
    {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
