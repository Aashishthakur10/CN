//package Project3;

import java.io.*;
import java.net.*;

public class manager {
    //Contains the actual file
    static byte[] source;
    //chunk static
    final static int chunk = 500;

    public static void main(String[] args) {
        if (args.length==2) {
            FileInputStream content = null;
            String fname = args[0];
            InetAddress hostAdd  = null;
            try {
                hostAdd = InetAddress.getLocalHost();
                //Destination
                InetAddress ip = InetAddress.getByName(args[1]);
                content = new FileInputStream(new File(fname));
                source = content.readAllBytes();
                Thread client = new Thread(new ClientRouter(0,args[1],source,ip,content));
                client.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String address = hostAdd.getHostAddress().trim();
        }else if (args.length==0){
            Thread server = new Thread(new ServerRouter(0));
            server.start();
        }else{
            System.out.println("Faulty parameters");
            System.exit(1);
        }
    }


}
