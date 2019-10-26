//package Project2;

/**
 * Class routingData encapsulates a two-dimensional value of ip address and Hop Count.
 */
public class routingData
{
    private String ip;
    int nodenum;
    int destination;
    int hopCount;
    int nextHop=0;
    String subnet;
    long changeTime;

    public routingData(String IP, int hopCount, int nodenum, int destination, long changeTime){
        this.ip = IP;
        this.hopCount = hopCount;
        this.nodenum = nodenum;
        this.destination = destination;
        this.changeTime = changeTime;
    }

    public int getDestination() {
        return destination;
    }

    public void setNextHop(int nextHop) {
        this.nextHop = nextHop;
    }

    public int getNextHop() {
        return nextHop;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int getNodenum() {
        return nodenum;
    }

    public void setSubnet(String subnet){
        this.subnet = subnet;
    }

    public void setNodenum(int nodenum) {
        this.nodenum = nodenum;
    }

    public String getIp() {
        return ip;
    }

    public int gethopCount() {
        return hopCount;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void sethopCount(int hopCount) {
        this.hopCount = hopCount;
    }
}
