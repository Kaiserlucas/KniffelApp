package com.example.kniffel.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.example.kniffel.GUI.activities.HostGameActivity;
import com.example.kniffel.GUI.activities.Notifiable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;


public class BluetoothServer implements BluetoothConnection, Runnable {

    public static final UUID uuid = new UUID(0x0cb3f25a, 0xa93a1fd9);
    public static final String serviceName = "KniffelApp";
    private Thread acceptThread = new Thread(this);
    private BluetoothSocket socket;
    private BluetoothServerSocket serverSocket;
    private BluetoothAdapter bluetoothAdapter;
    private final Notifiable activity;

    public BluetoothServer(Notifiable activity) throws IOException, InterruptedException {
        this.activity = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Make sure the bluetooth adapter works and is turned on
        if (bluetoothAdapter == null) {
            throw new IOException();
        } else if(!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        socket = null;

        acceptThread.start();
    }

    @Override
    public DataInputStream getDataInputStream() throws IOException {
        if(socket == null) {
            return null;
        } else {
            return new DataInputStream(socket.getInputStream());
        }
    }

    @Override
    public DataOutputStream getDataOutputStream() throws IOException {
        if(socket == null) {
            return null;
        } else {
            return new DataOutputStream(socket.getOutputStream());
        }
    }

    @Override
    public String getName() throws IOException {
        return socket.getRemoteDevice().getName();
    }

    @Override
    public void run() {
        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(serviceName, uuid);
            socket = serverSocket.accept();
            serverSocket.close();
        } catch(IOException e) {
            //Something went wrong
            acceptThread.interrupt();
        }
        //Sends notification that the connection was established successfully
        activity.onNotify();

    }
}
