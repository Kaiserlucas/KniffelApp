package com.example.kniffel.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.example.kniffel.GUI.activities.Notifiable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BluetoothClient implements BluetoothConnection, Runnable {

    private static final UUID uuid = new UUID(0x0cb3f25a, 0xa93a1fd9);
    private static final String serviceName = "KniffelApp";
    private Thread connectThread = new Thread(this);
    private BluetoothSocket socket;
    private BluetoothAdapter bluetoothAdapter;
    private final Notifiable activity;

    public BluetoothClient(Notifiable activity) throws IOException, InterruptedException {
        this.activity = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Make sure the bluetooth adapter works and is turned on
        if (bluetoothAdapter == null) {
            throw new IOException();
        } else if(!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        socket = null;

        connectThread.start();

    }

    @Override
    public DataInputStream getDataInputStream() throws IOException {
        return new DataInputStream(socket.getInputStream());
    }

    @Override
    public DataOutputStream getDataOutputStream() throws IOException {
        return new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public String getName() throws IOException {
        return socket.getRemoteDevice().getName();
    }

    @Override
    public void run() {
        boolean notConnected = true;
        do {
            bluetoothAdapter.startDiscovery();
            try {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if(notConnected){
                            socket = device.createRfcommSocketToServiceRecord(uuid);
                            try {
                                socket.connect();
                                notConnected = false;
                            } catch(Exception e) {
                                //Just a timeout on establishing a connection
                                //Doesn't need to be handled.
                            }
                        }
                    }
                }

            } catch (IOException e) {
                //Something went wrong with the bluetooth adapter itself?
                connectThread.interrupt();
            }
        } while(notConnected);
        //Sends notification that the connection was established successfully
        activity.onNotify();
    }
}
