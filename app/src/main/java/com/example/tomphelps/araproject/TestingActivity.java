package com.example.tomphelps.araproject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import support_classes.Comms;

public class TestingActivity extends Activity {
    static TextView textView;
    private static final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            byte[] data = (byte[]) msg.obj;
            String hexString = "", binString = "", intString = "";
            for (int i = 1; i < data.length; i++) {
                hexString += String.format("%02X", data[i]) + " ";
                long test = data[i];
                intString += " " + test;
            }
            int val = msg.arg1;
            System.out.println(val);
            textView.append("Int: " + intString + "Hex: " + hexString + "Bin: " + Integer.toBinaryString(val) + "\n");
        }
    };
    Comms comms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        comms = new Comms(this, handler);
        textView = (TextView) findViewById(R.id.testingTextView);
        textView.setText("");
//        Sensor sensor = new Sensor(this,handler);
    }

    public void startChip(View view) {
        try {
            comms.startChip();
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void singleSend(View view) {
        try {
            comms.setFetchInfo((0x50 >> 1), 0x2e, 0xff, 0xff, 0xff);
            comms.startSingleCollection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopChip(View view) {
        try {
            textView.setText("");

//            comms.setFetchInfo((0x50 >> 1), 0x2e, 0xff, 0xff, 0xff);
//            comms.startCollection(20, 20, TimeUnit.MILLISECONDS);
            comms.stopChip();
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.removeMessages(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        comms.stop();
        comms = null;
        handler.removeMessages(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        comms = new Comms(this, handler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_testing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
