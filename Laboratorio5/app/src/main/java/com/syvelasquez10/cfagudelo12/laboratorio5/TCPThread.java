package com.syvelasquez10.cfagudelo12.laboratorio5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPThread extends Thread {

    private final String IP = "192.168.0.5";

    private final int PORT = 8080;

    private PrintWriter escritor;

    private BufferedReader lector;

    private Socket socket;

    private int idTCP;

    private String ip;

    public TCPThread(int id, String ip) {
        idTCP=id;
        this.ip=ip;
    }

    public void run() {
        try {
            socket = new Socket(IP, PORT);
            escritor = new PrintWriter(socket.getOutputStream(), true);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            escritor.println("HELLO");
            String in = lector.readLine();
            if (!in.equals("GO")) {
                TCPActivity.mRequestingLocationUpdates = false;
            }
            while (TCPActivity.mRequestingLocationUpdates) {
                Thread.sleep(1000);
                escritor.println( ip + "," + idTCP + "," +TCPActivity.latitude + "," + TCPActivity.longitude + "," + TCPActivity.altitude + "," + TCPActivity.speed);
            }
            escritor.println("STOP");
            escritor.close();
            lector.close();
            TCPActivity.backEnabled=true;
        } catch (Exception e) {
            TCPActivity.backEnabled=true;
            System.err.println("Exception: " + e.getMessage());
        }
    }
}
