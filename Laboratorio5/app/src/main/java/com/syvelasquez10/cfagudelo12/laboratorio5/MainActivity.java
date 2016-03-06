package com.syvelasquez10.cfagudelo12.laboratorio5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button tcpButton = (Button) findViewById(R.id.tcpButton);
        tcpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tcpStart();
            }
        });

        Button udpButton = (Button) findViewById(R.id.udpButton);
        udpButton.setOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(View view) {
                udpStart();
            }
        });
    }

    public void tcpStart(){
        Intent intent = new Intent(this, TCPActivity.class);
        startActivity(intent);
    }

    public void udpStart(){
        Intent intent = new Intent(this, UDPActivity.class);
        startActivity(intent);
    }
}
