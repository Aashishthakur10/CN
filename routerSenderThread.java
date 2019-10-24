//package Project2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
/**
 * This is used to send the data to any listening router using multicasting.
 * Using threads, we advertise our node number and share the ip address.
 *
 */
public class routerSenderThread implements Runnable{
//    public static DatagramSocket ds;
    // Port, node numbers with broadcast ip.
    int portNum;
    int nodeNum;
    String broadCastIP;

    /**
     *Aasign the port number and ip on which the broadcast is done.
     *
     * @param portNum
     * @param nodeNum
     * @param broadCastIP
     */
    public routerSenderThread(int portNum, int nodeNum, String broadCastIP) {
        this.portNum = portNum;
        this.nodeNum = nodeNum;
        this.broadCastIP = broadCastIP;
    }

    public void sendPackets(){
        try {
            DatagramSocket ds = new DatagramSocket();
            InetAddress destIP = InetAddress.getByName(broadCastIP);

            byte[] msg = (""+nodeNum).getBytes();
            DatagramPacket dp = new DatagramPacket(msg,msg.length,destIP,portNum);

            ds.send(dp);
            ds.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try{
            while (true){
                sendPackets();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
