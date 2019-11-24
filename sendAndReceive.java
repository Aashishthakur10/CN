//package Project3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class sendAndReceive {

    int thisPort;
    DatagramSocket ds;

    sendAndReceive(int port){
        thisPort = port;
        try {
            ds = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }
    public static void send(int portNum, InetAddress ia, byte[] buffer){

        try {
            DatagramSocket ds = new DatagramSocket();
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length, ia, portNum);
            ds.send(dp);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive(byte[] buffer){

        try {
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            ds.receive(dp);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
