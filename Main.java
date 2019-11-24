//package Project3;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Main implements Runnable {
    //For those which weren't received
    ArrayList<Integer> pendingVals = new ArrayList<>();

    //chunk static
    final static int chunk = 500;
    int type = 0;
    //how much data is being sent over the network
    static byte[] send = new byte[501];
    //Contains the actual file
    static byte[] source;
    //sequence num
    static int seq = 0;
    //initdata sent
    public static boolean initSuccessful = false;
    //Completed the transfer
    public static boolean done = false;
    int lastChunk = 0;
    public String destination;
    public static String address;


    public Main(int type, int lastChunk, String destination) {
        this.type = type;
        this.lastChunk = lastChunk;
        this.destination = destination;
    }

    public static void main(String[] args) {
        FileInputStream content = null;

        try {
            //Do init config
            if (args.length == 2) {
                //Get file name.
                String fname = args[0];
                ByteBuffer bb = ByteBuffer.allocate(8);
                InetAddress hostAdd  = InetAddress.getLocalHost();
                address = hostAdd.getHostAddress().trim();

                //Destination
                InetAddress ip = InetAddress.getByName(args[1]);
                content = new FileInputStream(new File(fname));
                source = content.readAllBytes();
                //Last chunk
                int lastChunk = init(source);
                DatagramSocket ds0 = new DatagramSocket();
                bb.putInt(source.length).putInt(lastChunk);
                byte[] size = bb.array();

                //Init connection
                DatagramPacket dp = new DatagramPacket(size, size.length, ip, 63001);
                ds0.send(dp);

                //successfully sent check introduction needed
                initSuccessful = true;

                Thread client = new Thread(new Main(0, lastChunk,args[1]));
                client.start();
                Thread checkArrayList = new Thread(new Main(1, lastChunk,args[1]));
                checkArrayList.start();
                if (done)
                    content.close();
            } else {
                System.err.println("File name or Server IP not given");
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int init(byte[] source) {
        int len = source.length;
        int actualDivs = len / chunk;
        //Calculate what should be the chunk size for the final byte vals
        return len - chunk * actualDivs;

    }

    public void checkforVals() {
        while (true) {
            if (initSuccessful) {
                try {
                    DatagramSocket ds = new DatagramSocket(63002);
                    DatagramPacket dp = null;
                    InetAddress hostAdd = InetAddress.getLocalHost();
                    String address = hostAdd.getHostAddress().trim();
                    System.out.println("Listening on  " + address);
                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void sendingThread() {
        try {
            DatagramSocket ds = new DatagramSocket();
            InetAddress ip = InetAddress.getLocalHost();


            if (initSuccessful) {
                //Need to change
                while (seq < source.length) {
                    if (pendingVals.isEmpty()) {
                        intToBytes(send,seq,0);
//                        send[4]=
//                        intToBytes(send,);
                    } else {
                        System.out.println("Convert into 4 bytes to make this work");
                    }
                    int range = (seq == source.length - lastChunk ? lastChunk : chunk);
                    for (int i = 1; i <= range; i++) {
                        send[i] = source[seq];
                        seq++;
                    }
//                System.out.println(seq==source.length-lastChunk?lastChunk-1:chunk);
                    DatagramPacket dp = new DatagramPacket(send, send.length, ip, 63001);
                    ds.send(dp);
                }
                done = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //thread 1 for sending data
        if (this.type == 0) {
            sendingThread();
        }//thread 2 for checking if any packet was dropped
        else if (this.type == 1) {
            checkforVals();
        }
    }


    private static void intToBytes(byte[] input, int data, int start) {
        input[start] = (byte) ((data >> 24) & 0xff);
        input[start+1] = (byte) ((data >> 16) & 0xff);
        input[start+2] = (byte) ((data >> 8) & 0xff);
        input[start+3] = (byte) ((data >> 0) & 0xff);


    }
}

