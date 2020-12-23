package fr.yncrea.m1_s1project_android.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import fr.yncrea.m1_s1project_android.interfaces.BluetoothConstants;

public class BluetoothService {
    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private final String[] mCommands;
    private AcceptThread mSecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothSecure";
    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final int MAX_LENGTH_MSG = 100;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    public static final int STATE_DISCONNECT = 4;  // now disconnected from the remote device
    public static final int STATE_FAILED = 5;

    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BluetoothService(final String[] commands, final Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        mCommands = commands;
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public synchronized void connect(final BluetoothDevice device) {
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();

        updateUserInterface();
    }

    /**
     * Update UI according to the current state of the chat connection
     */
    private synchronized void updateUserInterface() {
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(BluetoothConstants.MESSAGE_STATE_CHANGE, mState, -1).sendToTarget();
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * //@param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(final BluetoothSocket socket/*, final BluetoothDevice device*/) {
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        /*
        final Message msg = mHandler.obtainMessage(BluetoothConstants.MESSAGE_DEVICE_NAME);
        final Bundle bundle = new Bundle();
        bundle.putString(BluetoothConstants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        */

        updateUserInterface();
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread();
            mSecureAcceptThread.start();
        }

        updateUserInterface();
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#send(String)
     */
    public void send(final String out) {
        // Create temporary object
        final ConnectedThread tmp;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            tmp = mConnectedThread;
        }
        // Perform the write unsynchronized
        tmp.send(out);
    }

    /**
     * Indicate that the connection attempt failed or the connexion was lost
     * and notify the UI Activity.
     */
    private void connectionClosed(final int reason) {
        mState = reason;

        updateUserInterface();

        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }


    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        mState = STATE_NONE;
        // Update UI title
        updateUserInterface();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
            }
            catch (IOException ignored) {}
            mmServerSocket = tmp;
            mState = STATE_LISTEN;
        }

        public void run() {
            AcceptThread.this.setName("AcceptThreadSecure");

            BluetoothSocket socket;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                }
                catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket/*, socket.getRemoteDevice()*/);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                }
                                catch (IOException ignored) {}
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            }
            catch (IOException ignored) {}
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        //private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            //mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            }
            catch (IOException ignored) {}
            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }

        public void run() {
            ConnectThread.this.setName("ConnectThreadSecure");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a successful connection or an exception
                mmSocket.connect();
            }
            catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                }
                catch (IOException ignored) {}
                connectionClosed(STATE_FAILED);
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket/*, mmDevice*/);
        }

        public void cancel() {
            try {
                mmSocket.close();
            }
            catch (IOException ignored) {}
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException ignored) {}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            StringBuilder str = new StringBuilder();
            byte[] buffer;
            int bytes;
            int brackets = 0;
            boolean isBrackets = false;

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    buffer = new byte[MAX_LENGTH_MSG];
                    bytes = mmInStream.read(buffer);

                    if (bytes == 0) continue;

                    String tmp = new String(buffer, 0, bytes);
                    str.append(tmp);

                    for (char c : tmp.toCharArray()) {
                        if (c == '{') {
                            brackets++;
                            isBrackets = true;
                        } else if (c == '}') brackets--;
                    }

                    if (isBrackets && brackets == 0) {
                        Message msg = mHandler.obtainMessage(BluetoothConstants.MESSAGE_RECEIVE);
                        Bundle bundle = new Bundle();
                        bundle.putString(BluetoothConstants.RECEIVE, str.toString());
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                        str = new StringBuilder();
                        isBrackets = false;
                    } else if (!isBrackets) {
                        for (String command : mCommands) {
                            if (str.toString().startsWith(command)) {
                                // if reception time is too short for getting correct id or digit number,
                                // here, simply save string command and continue for doing one more loop.
                                // then, between if and elif, add another elif : commandString != null.
                                // if this condition is true, program will access code below and send message
                                Message msg = mHandler.obtainMessage(BluetoothConstants.MESSAGE_RECEIVE);
                                Bundle bundle = new Bundle();
                                bundle.putString(BluetoothConstants.RECEIVE, str.toString());
                                msg.setData(bundle);
                                mHandler.sendMessage(msg);
                                str = new StringBuilder();
                                isBrackets = false;
                            }
                        }
                    }
                }
                catch (IOException e) {
                    connectionClosed(STATE_DISCONNECT);
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void send(final String buffer) {
            try {
                mmOutStream.write(buffer.getBytes());
            }
            catch (IOException ignored) {}
        }

        public void cancel() {
            try {
                mmSocket.close();
            }
            catch (IOException ignored) {}
        }
    }
}
