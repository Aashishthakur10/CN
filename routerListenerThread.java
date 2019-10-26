//package Project2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * This is used to listen to the Main using multicasting.
 * Using threads, we advertise our node number and share the ip address.
 *
 */
public class routerListenerThread implements Runnable {
    int portNum;
    String broadCastIP;

    /**
     *Assign the port number and ip on which the broadcast is done.
     *
     * @param portNum
     * @param broadCastIP
     */
    public routerListenerThread(int portNum, String broadCastIP) {
        this.portNum = portNum;
        this.broadCastIP = broadCastIP;
    }

    public void listner(){
        try {
            byte[] buffer = new byte[1024];

            MulticastSocket ms = new MulticastSocket(portNum);
            InetAddress group = InetAddress.getByName(broadCastIP);
            ms.joinGroup(group);

            while (true){
                DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
                ms.receive(dp);
                String message=new String(dp.getData(),dp.getOffset(),dp.getLength());
                // give us a way out if needed
                if("EXIT".equals(message)) {
                    System.out.println("No more messages. Exiting : "+message);
                    break;
                }
            }

            ms.leaveGroup(group);
            ms.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void run() {
       try {
           listner();
       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
