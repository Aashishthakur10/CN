//package Project2;

import java.util.ArrayList;


/**
 * Used to either add, remove a router to the routing table, also used to convert the routing table
 * into a RIP packet.
 *
 * @author Aashish Thakur(at1948@rit.edu)
 * @version 1.0
 */
public class designRIPPacket {
    public static final int INFINITY = 16;

    /**
     * Converts the values of a given router into a RIP packet
     *
     *
     * @param routingTab                Routing Table
     * @return                          RIP packet
     */
    public static byte[] convertToByte(ArrayList<routingData> routingTab) {
        byte[] buffer = new byte[504];
        int i, k;
        String[] ops;
        for (i = 0,k=6; i < routingTab.size(); i++,k+=6) {
            buffer[k] = (byte) routingTab.get(i).nodenum;//6
            buffer[++k] = (byte) routingTab.get(i).destination;//7
            //8,9,10,11
            ops = routingTab.get(i).getIp().split("[.]+");
            for (int j = 0; j < 4; j++) {
                buffer[++k] = (byte) Integer.parseInt(ops[j]);
            }
            //12,13,14,15
            for (int j = 0; j < 3; j++) {
                buffer[++k] = (byte) 255;
            }
            buffer[++k]=0;
            //16,17,18,19
            k += 4;
            //20
            buffer[++k] = (byte) routingTab.get(i).hopCount;
            //21,22,23
            k += 3;
        }

        return buffer;

    }


    /**
     * Updates the arraylist which maintains the values for a given router
     *
     *
     * @param routingTab                Routing Table
     * @param data                      RIP packet data
     * @param i                         Index
     * @return                          True if list updated.
     */

    public static boolean updateList(
            ArrayList<routingData> routingTab, byte[] data, int i) {
        int index;
        int index1=0;
        int index2=0;
        String ip;
        boolean updated = false;
        boolean routerFound1 = false;
        boolean routerFound2 = false;
        int destination = 0, recNodeNum = 0, hop = 0;

        // Check if the entries already exist in the table.
        for (index = 0; index < routingTab.size(); index++) {

            if (convertVals.getValue(data[i + 6]) == routingTab.get(index).destination) {
                routerFound1 = true;
                index1=index;
            }
            if (convertVals.getValue(data[i + 7]) == routingTab.get(index).destination) {
                routerFound2 = true;
                index2=index;
            }
            if (routerFound1 && routerFound2){
                break;
            }
        }

        index = index1;
        //Loop over and update the table depending on the
        //presence of the entries.
        for (int range = 0; range < 2; range++ ){
            if (routerFound1) {
                // If hop count is more than 1, then change.
                if (routingTab.get(index).gethopCount() > 1) {
                    for (int j = i + 6; j < i + 24; j++) {
                        routingTab.get(index).setNodenum(routingTab.get(index).getNodenum());//6
                        routingTab.get(index).setDestination(convertVals.getValue(data[++j]));//7
                        ip = convertVals.getValue(data, ++j, j = j + 3, ".");//8,9,10,11
                        routingTab.get(index).setIp(ip);
                        j += 4;//12,13,14,15
//                        routingTab.get(index).setSubnet(ip + "/24");
                        j += 4;//16,17,18,19
                        routingTab.get(index).sethopCount(1);
                        routingTab.get(index).setNextHop(0);
                        j += 4;//20,21,22,23
                        updated=true;
                    }

                }

                //update time
                routingTab.get(index).changeTime = System.currentTimeMillis();

                //If router destination is found, check if the hop count is less
                // than current value, if so, update.
                if (routerFound2){
                    if (routingTab.get(index2).hopCount-1 > convertVals.getValue(data[i+20])){
                        for (int j = i + 6; j < i + 24; j++) {
                            recNodeNum = convertVals.getValue(data[j]);//6
                            destination = convertVals.getValue(data[++j]);//7
                            ip = convertVals.getValue(data, ++j, j += 3, ".");//8,9,10,11
                            j += 4;//12,13,14,15
                            j += 4;//16,17,18,19
                            hop = convertVals.getValue(data[++j]);//20
                            j+=3;//21,22,23
                            routingTab.get(index2).setNextHop(recNodeNum);
                            routingTab.get(index2).sethopCount(hop+1);
                            routingTab.get(index2).setIp(ip);
                            routingTab.get(index2).setDestination(destination);
                            routingTab.get(index2).changeTime = System.currentTimeMillis();
                        }
                        updated=true;
                    }
                    routerFound2 = true;
                }else{
                    //If Router not present, then add the router.
                    for (int j = i + 6; j < i + 24; j++) {
                        recNodeNum = convertVals.getValue(data[j]);//6
                        destination = convertVals.getValue(data[++j]);//7
                        ip = convertVals.getValue(data, ++j, j += 3, ".");//8,9,10,11
                        j += 4;//12,13,14,15
                        j += 4;//16,17,18,19
                        hop = convertVals.getValue(data[++j]);//20
                        j+=3;//21,22,23
                        routingTab.add(new routingData(ip, hop + 1,
                                routingTab.get(index).getNodenum(),
                                destination,System.currentTimeMillis(),routingTab.get(index).getSubnet()));
                        routingTab.get(routingTab.size()-1).setNextHop(recNodeNum);
                        routingTab.get(routingTab.size()-1).changeTime = System.currentTimeMillis();
                    }
                    updated=true;
                    routerFound1 = true;
                    routerFound2 = true;
                }
            } else {
                //If neighbour not found first, add it and then check for its destination.
                for (int j = i + 6; j < i + 24; j++) {
                    recNodeNum = convertVals.getValue(data[j]);//6
                    destination = convertVals.getValue(data[++j]);//7
                    ip = convertVals.getValue(data, ++j, j += 3, "."); //8,9,10,11
                    j += 4;//12,13,14,15
                    j += 4;//16,17,18,19
                    hop = convertVals.getValue(data[++j]);//20
                    j+=3;//21,22,23
                    routingTab.add(new routingData(ip, hop + 1,
                            routingTab.get(0).getNodenum(),
                            recNodeNum,System.currentTimeMillis(),routingTab.get(index).getSubnet()));
                    routingTab.get(routingTab.size()-1).changeTime = System.currentTimeMillis();
                    updated = true;

                }
                if (recNodeNum == destination) {
                    break;
                }
                routerFound1 = true;

            }
        }
        return updated;
    }

    /**
     * If router not responding, delete entry.
     *
     *
     * @param routingTab            Routing Table
     * @param i                     Index for which entry has to be removed.
     * @return                      True if entry removal successful.
     */
    public static boolean deleteRouter(ArrayList<routingData> routingTab, int i){
        if (routingTab.size()<i){
            return false;
        }
        routingTab.remove(i);
        return true;
    }



}
