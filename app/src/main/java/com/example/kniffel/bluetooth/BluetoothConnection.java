package com.example.kniffel.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface BluetoothConnection {

    /**
     * Returns either a single DataInputStream (if this device is not the host) or 1-3 DataInputStreams (if this device is the host)
     * @return DataInputStreams of all known players
     */
    public DataInputStream getDataInputStream() throws IOException;

    /**
     * Returns either a single DataOutputStream (if this device is not the host) or 1-3 DataOutputStreams (if this device is the host)
     * @return DataOutputStreams of all known players
     */
    public DataOutputStream getDataOutputStream() throws IOException;

    /**
     * Returns the name of the connected device
     * @return Name of the connected device
     */
    public String getName();

    /**
     * Interrupts the Thread if it is still running
     */
    public void stopConnection();


}
