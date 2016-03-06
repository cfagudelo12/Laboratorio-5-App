package com.syvelasquez10.cfagudelo12.laboratorio5;

import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPThread extends Thread {
    private final String IP = "192.168.0.5";

    private final int PORT = 9000;

    private DatagramSocket socket;

    private int idUDP;

    private String ip;

    public UDPThread(int id, String ip) {
        idUDP=id;
        this.ip=ip;
    }

    public void run() {
        try {
            socket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(IP);
            while (UDPActivity.mRequestingLocationUpdates) {
                Thread.sleep(1000);
                String message = ip+","+idUDP+","+ UDPActivity.latitude + "," + UDPActivity.longitude + "," + UDPActivity.altitude + "," + UDPActivity.speed;
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
                socket.send(sendPacket);
            }
            socket.close();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}
