package au.com.tyo.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import au.com.tyo.utils.MathUtils;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 26/2/18.
 */

public class NetUtils {

    public interface NetworkScanListener {
        void onScanning(String what);
    }

    /**
     *
     * @return
     * @throws SocketException
     */
    public static List<InetAddress> checkAcitiveInLocalNetwork() throws SocketException {
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

    /**
     *
     * @param port
     * @return
     */
    public static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(false);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 1);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isHostListeningOn(String host, int port) {
        return isHostListeningOn(host, port, 200);
    }

    public static boolean isHostListeningOn(String host, int port, int timeout) {
        Socket s = null;
        boolean ret = false;
        try {
            s = new Socket();
            s.setSoTimeout(timeout);
            s.connect(new InetSocketAddress(host, port), timeout);
            ret = true;
        }
        catch (Exception e) {
        }
        finally {
            if(s != null)
                try {
                    s.close();
                }
                catch(Exception e){}
        }
        return ret;
    }

    /**
     *
     * @return
     */
    public static List getPeerIPs() {
        List list = null;
        InetAddress localhost = null;
        try {
            list = new ArrayList();
            localhost = InetAddress.getLocalHost();
            int netmaskLength = getNetmaskLength(localhost);
            byte[] netmask = getNetmaskByLength(netmaskLength);
            byte[] ip = localhost.getAddress();

            for (int i = 1; i <= 254; i++) {
                ip[3] = (byte)i;
                byte[] target = getMaskedIp(ip, netmask);
                if (isSameIp(ip, target))
                    list.add(target);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static List getHostIPs() throws SocketException {
        List<byte[]> ips = new ArrayList();
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                if (!i.getHostAddress().equals("127.0.0.1"))
                    ips.add((i.getAddress()));
            }
        }
        return ips;
    }

    public static List getPeerIPsFast() {
        List list = null;
        try {
            List<byte[]> ips = getHostIPs();

            for (byte[] ip : ips) {
                list = getPeerIPsFast(list, ip);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List getPeerIPsFast(byte[] ip) {
        return getPeerIPsFast(null, ip);
    }

    public static List getPeerIPsFast(List list, byte[] ip) {
        if (null == list)
            list = new ArrayList();

        for (int i = 1; i <= 254; i++) {
            if (i == ip[3])
                continue;

            byte[] newIp = new byte[4];
            System.arraycopy(ip, 0, newIp, 0, 4);
            newIp[3] = (byte) i;

            list.add(newIp);
        }
        return list;
    }

    public static int getNetmaskLength(InetAddress localhost) {
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByInetAddress(localhost);
        } catch (SocketException e) {
            return -1;
        }
        return networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
    }

    /**
     * TODO
     *
     * @param len
     * @return
     */
    public static byte[] getNetmaskByLength(int len) {
        byte[] netmask = new byte[4];
        int count = len;
        for (int i = 0; i < 4 && count > 0; ++i) {
            netmask[i] = (byte) 255;
            count -= 8;
        }
        return netmask;
    }

    public static byte[] getMaskedIp(byte[] ip, byte[] netmask) {
        byte[] target = new byte[4];
        for (int i = 0; i < 4; ++i)
            target[i] = (byte) (ip[0] & netmask[i]);
        return target;
    }

    public static boolean isSameIp(byte[] ip, byte[] ip2) {
        if (null !=ip && null !=ip2 && ip.length == 4 && ip2.length == 4)
            return ip[0] == ip2[0] &&
                    ip[1] == ip2[1] &&
                    ip[2] == ip2[2] &&
                    ip[3] == ip2[3];
        return false;
    }

    public static String ipToString(byte[] ip) {
        return String.format("%d.%d.%d.%d",
                MathUtils.toUnsignedInt(ip[0]),
                MathUtils.toUnsignedInt(ip[1]),
                MathUtils.toUnsignedInt(ip[2]),
                MathUtils.toUnsignedInt(ip[3]));
    }

    public static List scanLocalNetwork(List<byte[]> list, int port, NetworkScanListener networkScanListener) {
        return scanLocalNetwork(list, port, null, false);
    }

    public static List scanLocalNetwork(List<byte[]> list, int port, NetworkScanListener networkScanListener, boolean hitThenStop) {
        if (null != list) {
            List newList = new ArrayList();
            for (int i = 0; i < list.size(); ++i) {
                String ip = ipToString(list.get(i));

                if (networkScanListener != null)
                    networkScanListener.onScanning(ip);

                if (isHostListeningOn(ip, port)) {
                    newList.add(ip);

                    if (hitThenStop)
                        break;
                }
            }
            return newList;
        }
        return null;
    }

    public static byte[] toIP(int ip) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (ip & 0xff);
        bytes[1] = (byte) (ip >> 8 & 0xff);
        bytes[2] = (byte) (ip >> 16 & 0xff);
        bytes[3] = (byte) (ip >> 24 & 0xff);
        return bytes;
    }
}
