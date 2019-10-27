//package Project2;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * This program implements a RIP based router which can interact and exchange data
 * with similar routers.
 *
 * @author Aashish Thakur(at1948@rit.edu)
 * @version 1.0
 */
public class Main implements Runnable{

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

    //Constructor
    public Main(int portNum, int nodeNum, String broadCastIP, int type) {
        this.portNum = portNum;
        this.nodeNum = nodeNum;
        this.broadCastIP = broadCastIP;
        this.type = type;
    }

    /**
     * Listener thread receives the bytes and checks if any changes have
     * to be done to current routing table.
     *
     */
    public void listner(){
        try {
            byte[] buffer = new byte[504];
            int recNodeNum;
            MulticastSocket ms = new MulticastSocket(portNum);
            InetAddress group = InetAddress.getByName(broadCastIP);
            ms.joinGroup(group);

            while (true){
                DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
                ms.receive(dp);
                data = dp.getData();

                //Check the received byte array RIP packet for changes.
                for (int i=0; i<data.length;i++){
                    recNodeNum= convertVals.getValue(data[i+6]);
                    // No source
                    if (recNodeNum==0){
                        break;
                    }else{
                        //Skip if self data.
                        if (recNodeNum==nodeNum){
                            i+=22;
                        }else{
                            //Make changes and increment.
                            tableChanges(1,i);
                            i+=22;
                        }
                    }

                }


                if(data[0]==-29){
                    break;
                }
            }

            //End session
            ms.leaveGroup(group);
            ms.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Synchronized access to the Routing table for read, write and deletion.
     *
     * @param optype                0 = Read, 1 = Write, 2 = Deletion
     * @param i                     index
     */
    public synchronized void tableChanges(int optype,int i){
        // Read operation
        if (optype==0){
            msg = designRIPPacket.convertToByte(routingList);
        }else if(optype==1){
            updated = designRIPPacket.updateList(routingList,data,i);
        }else if (optype==2){
            updated = designRIPPacket.deleteRouter(routingList,i);
        }


        //If updates then print.

        if (updated){
            System.out.println("New start\n");
            for (int ind = 0; ind < routingList.size();ind++){
                if (routingList.get(ind).gethopCount() != INFINITY) {
                    System.out.println("Source " + routingList.get(ind).nodenum);
                    System.out.println("Destination " + routingList.get(ind).destination);
                    System.out.println("IP " + routingList.get(ind).getIp());
                    System.out.println("Next Hop " + routingList.get(ind).getNextHop());
                    System.out.println("Hop count " + routingList.get(ind).gethopCount());

                }
            }
            System.out.println("End\n");
            updated=false;
        }

    }

    /**
     * Broadcast the packets to all the neighbours.
     *
     */
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
     * Thread to check if the neighbouring routers are still reachable. If not,
     * then delete their entry.
     *
     */
    public void testRouterOutOfReach(){
        long currTime=0;
        int currHop ;
        while (true){
            for (int range = 0; range<routingList.size();range++){

                currHop=routingList.get(range).gethopCount();
                System.out.print("");
                if (currHop==1){
                    currTime = System.currentTimeMillis();
                    if (currTime-routingList.get(range).changeTime >10000){
                        // Delete from router by making the distance as Infinite i.e. 16.
                        // or remove entry.
                        tableChanges(2,range);
                    }

                }
            }
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
                routingList.add(new routingData(address,0,
                        nodeVal,nodeVal,System.currentTimeMillis()));
                //Sending thread
                Thread client=new Thread(new Main(520,nodeVal,
                        "230.230.230.230",0));
                client.start();
                //Receiving Thread
                Thread server =new Thread(new Main(520,nodeVal,
                        "230.230.230.230",1));
                server.start();
                //Checking thread.
                Thread routerCheck =new Thread(new Main(520,nodeVal,
                        "230.230.230.230",2));
                routerCheck.start();

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
            listner();
        }else if (this.type==1){
            while (true) {
                sendPackets();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else if (this.type==2){
//            System.out.println("Initiate checks");
            testRouterOutOfReach();
        }
    }
}


