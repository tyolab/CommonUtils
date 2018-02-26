package au.com.tyo.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 26/2/18.
 */

public class NetUtils {

    public static List<InetAddress> checkAcitiveInLocalNetwork(String[] args) throws SocketException {
        List ips = new ArrayList();
        Enumeration nis = NetworkInterface.getNetworkInterfaces();
        while(nis.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) nis.nextElement();
            Enumeration ias = ni.getInetAddresses();
            while (ias.hasMoreElements()) {
                InetAddress ia = (InetAddress) ias.nextElement();
                ips.add(ia);
            }

        }
        return ips;
    }
}
