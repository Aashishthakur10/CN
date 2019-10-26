//package Project2;

import java.util.ArrayList;

public class designRIPPacket {

    public static byte[] convertToByte(ArrayList<routingData> routingTab) {
        byte[] buffer = new byte[504];
        int i, k;
        String[] ops;
        k = 6;
        //Main data
//        try {

        for (i = 0; i < routingTab.size(); i++) {
            buffer[k] = (byte) routingTab.get(i).nodenum;
            buffer[++k] = (byte) routingTab.get(i).destination;
            ops = routingTab.get(i).getIp().split("[.]+");
            for (int j = 0; j < 4; j++) {
                buffer[++k] = (byte) Integer.parseInt(ops[j]);
            }
            for (int j = 0; j < 4; j++) {
                buffer[++k] = (byte) Integer.parseInt(ops[j]);
            }
            k += 4;
            buffer[k] = (byte) routingTab.get(i).hopCount;
            k += 4;
        }

//        }catch (Exception e){
//            System.out.println(k);
//            e.printStackTrace();
//        }
        return buffer;

    }

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
//        try {


        for (index = 0; index < routingTab.size(); index++) {

            if (data[i + 6] == routingTab.get(index).destination) {
                routerFound1 = true;
                index1=index;
            }
            if (data[i + 7] == routingTab.get(index).destination) {
                routerFound2 = true;
                index2=index;
            }
            if (routerFound1 && routerFound2){
                break;
            }
        }
//        System.out.println("router 1 found: "+routerFound1);
//        System.out.println("router 2 found: "+routerFound2);
        index = index1;
        for (int range = 0; range < 2; range++ ){
            if (routerFound1) {
                if (!((routingTab.get(index)).gethopCount() == 1)) {
                    for (int j = i + 6; j < i + 24; j++) {
                        routingTab.get(index).setNodenum(convertVals.getValue(data[j]));
                        routingTab.get(index).setDestination(convertVals.getValue(data[++j]));
                        ip = convertVals.getValue(data, ++j, j = j + 3, ".");
                        routingTab.get(index).setIp(ip);
                        j += 4;
                        routingTab.get(index).setSubnet(ip + "/24");
                        j += 4;
                        routingTab.get(index).sethopCount(1);
                        routingTab.get(index).setNextHop(0);
                        j += 4;
                        updated=true;
                    }

                }
                if (routerFound2){
//                    System.out.println(convertVals.getValue(data[20]));
                    if (routingTab.get(index2).hopCount-1 > convertVals.getValue(data[20])){
                        for (int j = i + 6; j < i + 24; j++) {
                            recNodeNum = convertVals.getValue(data[j]);
                            destination = convertVals.getValue(data[++j]);
                            ip = convertVals.getValue(data, ++j, j += 3, ".");
                            j += 4;
                            j += 4;
                            hop = convertVals.getValue(data[++j]);
                            j+=3;
                            routingTab.get(index2).setNextHop(recNodeNum);
                            routingTab.get(index2).sethopCount(hop+1);
                            routingTab.get(index2).setIp(ip);
                            routingTab.get(index2).setDestination(destination);
                        }
                        updated=true;
                    }
                    routerFound2 = true;
                }else{
                    for (int j = i + 6; j < i + 24; j++) {
                        recNodeNum = convertVals.getValue(data[j]);
                        destination = convertVals.getValue(data[++j]);
                        ip = convertVals.getValue(data, ++j, j += 3, ".");
                        j += 4;
                        j += 4;
                        hop = convertVals.getValue(data[++j]);
                        j+=3;
                        routingTab.add(new routingData(ip, hop + 1,
                                routingTab.get(0).getNodenum(), destination));
                        routingTab.get(routingTab.size()-1).setNextHop(recNodeNum);
                    }
                    updated=true;
                    routerFound1 = true;
                }
            } else {

                for (int j = i + 6; j < i + 24; j++) {
                    recNodeNum = convertVals.getValue(data[j]);
                    System.out.println("source "+ recNodeNum);
                    destination = convertVals.getValue(data[++j]);
                    System.out.println("dest "+destination);
                    ip = convertVals.getValue(data, ++j, j += 3, ".");
                    System.out.println("ip "+ip);
                    j += 4;
                    j += 4;
                    hop = convertVals.getValue(data[++j]);
                    j+=3;
//                    hop = 1;
                    System.out.println("hop = "+hop);
                    routingTab.add(new routingData(ip, hop + 1,
                            routingTab.get(0).getNodenum(), recNodeNum));
                    System.out.println(routingTab.get(routingTab.size()-1).getIp());
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


}
