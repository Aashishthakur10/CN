//package Project3;
import java.io.File;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;

//Clear after getting values.
public class ServerRouter implements Runnable{
    public static int SIZE;
    public static int seq;
    public static byte[] file;
    public static boolean finished=false;
    public static boolean[] recvCheck;
    public int type = 0;
    public static byte[] ackD = new byte[1];
    //chunk static
    final static int chunk = 500;

    public ServerRouter(int type) {
        this.type = type;
    }

    public static int lastChunk;
    public static String destination;
    public static void main(String[] args) {
        Thread listen = new Thread(new ServerRouter(0));
        listen.start();
    }


    // Thread to store values
    public void receiveData(){
        FileOutputStream fos=null;
        try {

            InetAddress hostAdd  = InetAddress.getLocalHost();
            String address = hostAdd.getHostAddress().trim();
            System.out.println("Listening on  "+ address);

            byte[] receive = new byte[513];
            byte[] receive1 = new byte[8];

            sendAndReceive s = new sendAndReceive(63001);
            s.receive(receive1);

            SIZE=getInt(receive1,0);
            lastChunk  = getInt(receive1,4);
            file=new byte[SIZE];
            recvCheck = new boolean[SIZE];

            while (!finished){

                s.receive(receive);
                int ack = receive[12];
                destination = "" + (receive[4] & 0xFF) +"."+ (receive[5] & 0xFF)+"."
                        +(receive[6] & 0xFF)+"."+(receive[7] & 0xFF);
                //Destination
                System.out.println(destination);
                InetAddress dest = InetAddress.getByName(destination);
                if (ack == 1){
                    ackD[0] = 0;
                    sendAndReceive.send(63002,dest,ackD);
                    continue;
                }

                System.out.println(dest+" dest");
                seq = getInt(receive,0);

                int temp = seq;

                int range = (seq == SIZE - lastChunk ? lastChunk : chunk);
//                System.out.println(range);
                if (!recvCheck[temp]) {
                    for (int i = 13; i <= range + 12; i++) {
                        recvCheck[temp] = true;
                        file[temp] = receive[i];
                        temp++;
                    }
                }

                // Send acknowledgement as 1
                ackD[0]=1;
                sendAndReceive.send(63002,dest,ackD);
////                DatagramSocket ds1 = new DatagramSocket(63001);
//                DatagramPacket dp1 = new DatagramPacket(ackD, ackD.length, dest, 63001);
//                ds.send(dp1);
//                System.out.println("sent");

                if (seq+lastChunk==SIZE)
                    break;
            }

            File f= new File("Project3/ans.png");
            fos = new FileOutputStream(f);
            if (!f.exists()) {
                f.createNewFile();
            }

            fos.write(file);
            fos.flush();
            System.out.println("File Written Successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (fos != null)
                {
                    fos.close();
                }
            }
            catch (IOException ioe) {
                System.out.println("Error in closing the Stream");
            }
        }
    }

    public static int getInt(byte[] b, int init)
    {
        return   b[init+3] & 0xFF |
                (b[init+2] & 0xFF) << 8 |
                (b[init+1] & 0xFF) << 16 |
                (b[init] & 0xFF) << 24;
    }

    @Override
    public void run() {
        if (this.type==0){
            receiveData();
        }
    }
}
