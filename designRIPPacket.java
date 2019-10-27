//package Project2;

import java.util.ArrayList;

public class designRIPPacket {
    public static final int INFINITY = 16;

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

        System.out.println("packets: "+k);

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
        for (int range = 0; range < 2; range++ ){
            if (routerFound1) {
                if (!((routingTab.get(index)).gethopCount() == 1)) {
                    for (int j = i + 6; j < i + 24; j++) {
                        routingTab.get(index).setNodenum(routingTab.get(index).getNodenum());//6
                        routingTab.get(index).setDestination(convertVals.getValue(data[++j]));//7
                        ip = convertVals.getValue(data, ++j, j = j + 3, ".");//8,9,10,11
                        routingTab.get(index).setIp(ip);
                        j += 4;//12,13,14,15
                        routingTab.get(index).setSubnet(ip + "/24");
                        j += 4;//16,17,18,19
                        routingTab.get(index).sethopCount(1);
                        routingTab.get(index).setNextHop(0);
                        j += 4;//20,21,22,23
                        updated=true;
                    }

                }
                routingTab.get(index).changeTime = System.currentTimeMillis();

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
                                destination,System.currentTimeMillis()));
                        routingTab.get(routingTab.size()-1).setNextHop(recNodeNum);
                        routingTab.get(routingTab.size()-1).changeTime = System.currentTimeMillis();
                    }
                    updated=true;
                    routerFound1 = true;
                }
            } else {

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
                            recNodeNum,System.currentTimeMillis()));
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

    public static boolean deleteRouter(ArrayList<routingData> routingTab, int i){
        if (routingTab.size()<i){
            return false;
        }
//        routingTab.get(i).sethopCount(INFINITY);
        routingTab.remove(i);
        return true;
    }



}
