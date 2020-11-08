package com.example.kniffel.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface BluetoothConnection {

    //There can be one or multiple connections depending on whether this device is the host of the game or not
    //The host can have multiple open connections at once, which are all saved here

    /**
     * Returns either a single DataInputStream (if this device his not the host) or 1-3 DataInputStreams (if this device is the host)
     * @return DataInputStreams of all known players
     */
    public DataInputStream getDataInputStream() throws IOException;

    /**
     * Returns either a single DataOutputStream (if this device his not the host) or 1-3 DataOutputStreams (if this device is the host)
     * @return DataOutputStreams of all known players
     */
    public DataOutputStream getDataOutputStream() throws IOException;

    /**
     * Returns the name of the connected device
     * @return Name of the connected device
     * @throws IOException
     */
    public String getName() throws IOException;


}
