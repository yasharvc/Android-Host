package com.tribitgroup.host;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    String msgLog = "";
    TextView ipText;
    HttpServerThread httpServerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipText = findViewById(R.id.ip);

        ipText.setText("IP is : " + Helper.getIPAddress(true));

        httpServerThread = new HttpServerThread();
        httpServerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        httpServerThread.dispose();
    }
}