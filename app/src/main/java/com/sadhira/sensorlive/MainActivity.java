package com.sadhira.sensorlive;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
//
//import com.dropbox.sync.android.DbxFile;
//import com.dropbox.sync.android.DbxFileSystem;
//import com.dropbox.sync.android.DbxPath;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_ENABLE_BT = 1;
    private ArrayAdapter<String> mArrayAdapter;
    private ListView listViewPairedDevices = null;
    private ListView listViewAvailDevices = null;
    final List<String> mAddress = new ArrayList<String>();
    private BroadcastReceiver mReceiver;
    String adaddress = null;
    BluetoothDevice device = null;
    private BluetoothAdapter mBluetoothAdapter;

    protected Button mConnect;

    private BluetoothSocket btSocket = null;
    OutputStream mmOutputStream = null;
    InputStream mmInputStream = null;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    //private BluetoothAdapter mBluetoothAdapter; // object to btoot with
    public static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");//
    //    DbxPath logPath = new DbxPath("/test.txt"); // log to file variables
//    DbxFileSystem logDbFx = null;
//    DbxFile logFile = null;

    private GoogleApiClient client;
    volatile boolean stopWorker = false;
    Thread workerThread;
    int readBufferPosition = 0;
    byte[] readBuffer;
    int counter;
    //OutputStream mmOutputStream = null;
    //InputStream mmInputStream = null;
    int testing = 0;
    BluetoothDevice gDevice;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnect = (Button) findViewById(R.id.connect);



// -> Setting Up Bluetooth
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "BT Available", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

// -> Finding Device (Yacine)
        Toast.makeText(getApplicationContext(), "List Start", Toast.LENGTH_SHORT).show();
        listViewPairedDevices = (ListView) findViewById(R.id.listViewPairedDevices);
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1); // bt
        String hey = "00:18:96:B0:02:87";
        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();

        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {

                Toast.makeText(getApplicationContext(), "Looping", Toast.LENGTH_SHORT).show();
                // Add the name and address to an array adapter to show in a
                // ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mAddress.add(device.getAddress()); // the address array

            }
        } else {
            Toast.makeText(getApplicationContext(), "No Devices", Toast.LENGTH_SHORT).show();
            mArrayAdapter.add("No Devices");
        }


        //   -> Discovering Devices Yac
        mBluetoothAdapter.startDiscovery();


// Create a BroadcastReceiver for ACTION_FOUND
        mReceiver = new BroadcastReceiver() {
            @SuppressLint("NewApi")
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a
                    // ListView
                    mArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
                    mAddress.add(device.getAddress()); // the address array
                    //device.createBond(); iffy solvent
                    //	adatest.createBond();//***********************************************

                    //	Log.d("contents of device", adatest.toString());
                }
            }
        };

        listViewPairedDevices.setAdapter(mArrayAdapter); //updates listview

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister
        // during onDestroy

        // making a clickable list
        listViewPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                TextView temp = (TextView) arg1;
                Toast.makeText(MainActivity.this, temp.getText(),
                        Toast.LENGTH_SHORT).show();
                String surgery = temp.getText().toString();
                //String secondline = surgery.substring(22);
                String secondline = mAddress.get(arg2);
                adaddress = mAddress.get(arg2);
                Log.d("commencing surgery", surgery);
                Log.d("first round", adaddress);

                BluetoothDevice letssee = mBluetoothAdapter.getRemoteDevice(adaddress);
                device = letssee;
                letssee.createBond();


            }

        });

        //connects
        mConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "connect pressed",Toast.LENGTH_SHORT).show();  // attempting to connect
                Connect();
            }

        });





//    public void btButton(View v) {
//
//// -> Discovering Devices
//        // Create a BroadcastReceiver for ACTION_FOUND
//        private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                // When discovery finds a device
//                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                    // Get the BluetoothDevice object from the Intent
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    // Add the name and address to an array adapter to show in a ListView
//                    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//                }
//            }
//        };
//// Register the BroadcastReceiver
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
//
//
//// -> Enabling discoverability
//        Intent discoverableIntent = new
//                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//        startActivity(discoverableIntent);
//
//
//// -> Conecting as a server
//        private class AcceptThread extends Thread {
//            private final BluetoothServerSocket mmServerSocket;
//
//            public AcceptThread() {
//                // Use a temporary object that is later assigned to mmServerSocket,
//                // because mmServerSocket is final
//                BluetoothServerSocket tmp = null;
//                try {
//                    // MY_UUID is the app's UUID string, also used by the client code
//                    tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
//                } catch (IOException e) {
//                }
//                mmServerSocket = tmp;
//            }
//
//            public void run() {
//                BluetoothSocket socket = null;
//                // Keep listening until exception occurs or a socket is returned
//                while (true) {
//                    try {
//                        socket = mmServerSocket.accept();
//                    } catch (IOException e) {
//                        break;
//                    }
//                    // If a connection was accepted
//                    if (socket != null) {
//                        // Do work to manage the connection (in a separate thread)
//                        manageConnectedSocket(socket);
//                        mmServerSocket.close();
//                        break;
//                    }
//                }
//            }
//
//            /**
//             * Will cancel the listening socket, and cause the thread to finish
//             */
//            public void cancel() {
//                try {
//                    mmServerSocket.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//
//// -> Connecting as a client
//        private class ConnectThread extends Thread {
//            private final BluetoothSocket mmSocket;
//            private final BluetoothDevice mmDevice;
//
//            public ConnectThread(BluetoothDevice device) {
//                // Use a temporary object that is later assigned to mmSocket,
//                // because mmSocket is final
//                BluetoothSocket tmp = null;
//                mmDevice = device;
//
//                // Get a BluetoothSocket to connect with the given BluetoothDevice
//                try {
//                    // MY_UUID is the app's UUID string, also used by the server code
//                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//                } catch (IOException e) {
//                }
//                mmSocket = tmp;
//            }
//
//            public void run() {
//                // Cancel discovery because it will slow down the connection
//                mBluetoothAdapter.cancelDiscovery();
//
//                try {
//                    // Connect the device through the socket. This will block
//                    // until it succeeds or throws an exception
//                    mmSocket.connect();
//                } catch (IOException connectException) {
//                    // Unable to connect; close the socket and get out
//                    try {
//                        mmSocket.close();
//                    } catch (IOException closeException) {
//                    }
//                    return;
//                }
//
//                // Do work to manage the connection (in a separate thread)
//                manageConnectedSocket(mmSocket);
//            }
//
//            /**
//             * Will cancel an in-progress connection, and close the socket
//             */
//            public void cancel() {
//                try {
//                    mmSocket.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//    }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @SuppressLint("NewApi")
    protected void Connect() {
        // TODO Auto-generated method stub
        //BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(adaddress);
        Log.d("connect", "Connecting to ... " + device);
        Log.d("connecting address", adaddress);


        mBluetoothAdapter.cancelDiscovery();
        Log.d("discovery canceled", "canceled discoverey");

        try {
            BluetoothDevice mDevice = mBluetoothAdapter.getRemoteDevice(adaddress);
            Method m = mDevice.getClass().getMethod("createRfcommSocket",
                    new Class[] { int.class });
            btSocket = (BluetoothSocket) m.invoke(mDevice, Integer.valueOf(1));
            btSocket.connect();
            Log.d("connect", "Connection made.");
            Toast.makeText(MainActivity.this, "connected smile emoticon:)",Toast.LENGTH_SHORT).show();  // attempting to connect
        } catch (NoSuchMethodException e) {
            Log.d("isuue", e.toString());

        } catch (SecurityException e2) {
            Log.d("isuue", e2.toString());

        } catch (IllegalArgumentException e3) {
            Log.d("isuue", e3.toString());

        } catch (IllegalAccessException e4) {
            Log.d("isuue", e4.toString());

        } catch (InvocationTargetException e5) {
            Log.d("isuue", e5.toString());

        } catch (Exception e6) {
            Log.d("isuue", e6.toString());

        }

        Log.d("socket", btSocket.toString());
        Log.d("the device", btSocket.getRemoteDevice().toString());

/* Here is the part the connection is made, by asking the device to create a RfcommSocket (Unsecure socket I guess), It map a port for us or something like that */

        // 	btSocket.connect();

        Toast.makeText(MainActivity.this, "connection made :)",Toast.LENGTH_SHORT).show();  // attempting to connect
        try {
            mmOutputStream = btSocket.getOutputStream();

            mmInputStream = btSocket.getInputStream();
            //myLabel.setText("Bluetooth Opened");
            beginListenForData();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        /* this is a method used to read what the Arduino says for example when you write Serial.print("Hello world.") in your Arduino code */

    }

    void beginListenForData() {

        final byte delimiter = 10; //This is the ASCII code for a newline character
        Log.d("sigh", "begin's night was entered");
        //Log.d("sigh", handler.toString());

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[16384];
        int readMessageLimit = 512;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        workerThread = new Thread(new Runnable() {
            public void run() {
                Log.d("cool", "entered first run");
                int begin = 0;
                int bytes = 0;
                while(!stopWorker) {
                    try {
                        //int bytesAvailable = mmInputStream.available();
                        /// int bytesAvailable = mmInputStream.read(readBuffer); //trying a blocking call instead
                        //Log.d("bytes challenge", String.valueOf(bytesAvailable));
                        if((readBuffer.length-bytes) <= 38){
                            readBuffer = null;
                            readBuffer = new byte[8192];
                            bytes = 0;
                            Log.d("hawa", "this is new read: "+String.valueOf(mmInputStream.read(readBuffer, bytes, readBuffer.length - bytes)));

                        }
                        Log.d("available", "this is available: "+String.valueOf(mmInputStream.available()));
                        bytes += mmInputStream.read(readBuffer, bytes, readBuffer.length - bytes);
                        testing = bytes;
                        Log.d("Termes", "this is bytes: "+String.valueOf(bytes));
                        Log.d("condition", "this is condition: "+String.valueOf(readBuffer.length-bytes));
                        Log.d("Kam?", "this is buffer: "+readBuffer.toString());

                        // byte[] packetBytes = new byte[bytesAvailable];
                        //   byte[] logArray = new byte[bytesAvailable];
                        //	Log.d("sing", "up to here ok");
                        // System.arraycopy(readBuffer, 0, logArray, 0, bytesAvailable); //test
                        //   Log.d("things", "Read Hex: " + getHex(logArray)+ "Read Dec: "+getDec(logArray));  //test
                        for(int i = begin; i < bytes; i++) {            //int i=0;i<bytesAvailable;i++
                            //   final byte a = packetBytes[i];
                            final byte b = readBuffer[i]; //test


                            if(readBuffer[i] == "#".getBytes()[0]) {
                                //handler.obtainMessage(1, begin, i, readBuffer).sendToTarget();
                                begin = i + 1;
                                if(i == bytes - 1) {
                                    bytes = 0;
                                    begin = 0;
                                }

                            }
                            // Log.d("entering for", "i should be reading"+ b);


                            // Log.d("old for", "i should be reading"+ a);

		              /*  if(b == delimiter) {
		                  byte[] encodedBytes = new byte[readBufferPosition];
		                  System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
		                  final String data = new String(encodedBytes, "US-ASCII");
		                  readBufferPosition = 0;

		                  handler.post(new Runnable() {
		                    public void run() {
		                      myLabel.setText("some thing");
		                    }
		                  });
		                } */

                        }

                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }

                }
            }
        });

        workerThread.start();


    }

//    Handler handler = new Handler(new Handler.Callback()
//    {
//        @Override
//        public boolean handleMessage(Message msg)
//        {
//            byte[] writeBuf = (byte[]) msg.obj;
//            int begin = (int)msg.arg1;
//            int end = (int)msg.arg2;
//            final String data;
//            switch(msg.what) {
//                case 1:
//                    String writeMessage = new String(writeBuf);
//                    writeMessage = writeMessage.substring(begin, end);
//                    Log.d("yakhalasi freska", writeMessage);
//
//                    data = writeMessage;
//
//
//                    try {
//
//                        logFile.appendString(writeMessage);
//                        Log.d("shee2 gameel ", writeMessage);
//                        Log.d("TERMES", "this is final bytes: "+String.valueOf(testing));
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                // your code to update the UI thread here
//                                myLabel.setText(data); //testing display
//                            }
//                        });
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//
//                    break;
//            }
//            return false;      // RETURN VALUE ????
//        }
//    });


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.sadhira.sensorlive/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.sadhira.sensorlive/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
