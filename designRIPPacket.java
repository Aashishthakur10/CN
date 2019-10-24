//package Project2;

import java.util.ArrayList;

public class designRIPPacket {

    public static byte[] convertToByte(ArrayList<routingData> routingTab) {
        byte[] buffer = new byte[504];
        int i, k;
        String[] ops;
        k = 6;

        //router data
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
        return buffer;

    }

    public static void updateList(
            ArrayList<routingData> routingTab, byte[] data, int i) {
        int index;
        String ip;
        boolean routerFound1 = false;
        boolean updatesDone = false;

        for (index = 0; index < routingTab.size(); index++) {

            if (data[i + 6] == routingTab.get(index).destination) {
                routerFound1 = true;
                break;
            }
        }
        while (!updatesDone) {
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
                        j += 4;
                    }

                }

            updatesDone = true;
            } else {
                int destination = 0, recNodeNum = 0, hop = 0;
                System.out.println(data.length+ " val of i "+ i+24);
                for (int j = i + 6; j < i + 24; j++) {
//                    System.out.println(data[j]);
                    recNodeNum = convertVals.getValue(data[j]);
                    System.out.println("source "+ recNodeNum);
                    destination = convertVals.getValue(data[++j]);
                    System.out.println("dest "+destination);
                    ip = convertVals.getValue(data, ++j, j += 3, ".");
                    System.out.println("ip "+ip);
                    j += 4;
                    j += 4;
//                    hop = Integer.parseInt(convertVals.getValue(data, ++j, j += 3, ""));
                    hop = convertVals.getValue(data[++j]);
                    j+=3;
//                    hop = 1;
                    System.out.println("hop = "+hop);
                    routingTab.add(new routingData(ip, hop + 1,
                            routingTab.get(0).getNodenum(), recNodeNum));
//                    System.out.println("index: "+j);
                    System.out.println(routingTab.get(routingTab.size()-1).getIp());

                }
                if (recNodeNum == destination) {
                    updatesDone = true;
                }
                routerFound1 = true;

            }
        }
//        return
    }


}
