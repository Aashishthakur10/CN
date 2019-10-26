//package Project2;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Main implements Runnable{

    //    public static DatagramSocket ds;
    // Port, node numbers with broadcast ip.
    int portNum;
    int nodeNum;
    String broadCastIP;
    static boolean updated=false;
    int type;
    static byte []data;
    static byte[] msg;
    static ArrayList<routingData> routingList = new ArrayList<>();
    public static final int INFINITY = 16;

    public Main(int portNum, int nodeNum, String broadCastIP, int type) {
        this.portNum = portNum;
        this.nodeNum = nodeNum;
        this.broadCastIP = broadCastIP;
        this.type = type;
    }


    public void listner(){
        try {
            byte[] buffer = new byte[504];
            int recNodeNum = 0;
            MulticastSocket ms = new MulticastSocket(portNum);
            InetAddress group = InetAddress.getByName(broadCastIP);
            ms.joinGroup(group);

            while (true){
                DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
                ms.receive(dp);
                data = dp.getData();
                for (int i=0; i<data.length;i++){
                    recNodeNum=data[i+6];
                    // No source
                    if (recNodeNum==0){
                        break;
                    }else{
                        if (recNodeNum==nodeNum){
                            i+=26;
                        }else{
                            tableChanges(1,i);
                            i+=26;
                        }
                    }

                }
                if(data[0]==-29){
                    break;
                }
            }

            ms.leaveGroup(group);
            ms.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void tableChanges(int optype,int i){
        // Read operation
        if (optype==0){
            msg = designRIPPacket.convertToByte(routingList);
        }else{
            updated = designRIPPacket.updateList(routingList,data,i);
//            System.out.println(updated);

        }

    }

    public void sendPackets(){
        try {
            DatagramSocket ds = new DatagramSocket();
            InetAddress destIP = InetAddress.getByName(broadCastIP);

            tableChanges(0,0);
            DatagramPacket dp = new DatagramPacket(msg,msg.length,destIP,portNum);
            ds.send(dp);
            ds.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * The main program. Calls the functions for sending and receiving data
     * for a Main in a multi-threaded environment.
     *
     */
    public static void main(String[] args) {
        if(args.length>0 && Integer.parseInt(args[0]) > 0){
            //Node number
            int nodeVal = Integer.parseInt(args[0]);
            try {
                // Host ip
                InetAddress hostAdd  = InetAddress.getLocalHost();
                String address = hostAdd.getHostAddress().trim();
                System.out.println("Address is "+ address);
                routingList.add(new routingData(address,0,nodeVal,nodeVal));

                Thread client=new Thread(new Main(63001,nodeVal,
                        "230.230.230.230",0));
                client.start();
                Thread server =new Thread(new Main(63001,nodeVal,
                        "230.230.230.230",1));
                server.start();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            while (true)
                ;
        }else{
            System.err.println("Please specify the id number which is greater than 0, exiting");
            System.exit(1);
        }

    }


    @Override
    public void run() {
        if (this.type==0){
//            while (true){
            listner();
            if (!updated){
                System.out.println("oops");
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
            }else {
                //Trigger updates
//                    System.out.println("print size" + routingList.size());
                for (int ind = 0; ind < routingList.size();ind++){
                    System.out.println("Source "+routingList.get(ind).nodenum);
                    System.out.println("Destination "+routingList.get(ind).destination);
                    System.out.println("IP "+routingList.get(ind).getIp());
                    System.out.println("Next Hop "+routingList.get(ind).getNextHop());
                    System.out.println("Hop count "+routingList.get(ind).gethopCount());
                }
                updated=false;
            }

        }else if (this.type==1){
            while (true) {
                sendPackets();
            }
        }
    }
}


