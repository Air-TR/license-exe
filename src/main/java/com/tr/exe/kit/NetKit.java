package com.tr.exe.kit;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class NetKit {

    public static void main(String[] args) {
        System.out.println(ipReachable("192.168.3.159"));
    }

    /**
     * @param ip 要检查的 ip 地址
     * @return
     */
    public static Boolean ipReachable(String ip) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            return inetAddress.isReachable(3000); // 3000 毫秒
        } catch (UnknownHostException e) {
            System.out.println("无效的IP地址");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取本地网卡信息
     * @return
     * @throws UnknownHostException
     */
    public static InetAddress getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    /**
     * 获取 IP（内网）
     */
    public static String getIpAddress() {
        try {
            return getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    /**
     * 获取 Mac 地址
     */
    public static String getMacAddress() {
        try {
            InetAddress ipAddress = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ipAddress);
            byte[] macAddressBytes = networkInterface.getHardwareAddress();

            StringBuilder macAddress = new StringBuilder();
            for (int i = 0; i < macAddressBytes.length; i++) {
                macAddress.append(String.format("%02X%s", macAddressBytes[i], (i < macAddressBytes.length - 1) ? "-" : ""));
            }
            return macAddress.toString();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>获取Mac地址</p>
     *
     * @return List<String> Mac地址
     * @throws Exception 默认异常
     */
    public static List<String> getMacAddressList() throws Exception {
        /** 获取所有网络接口 */
        List<InetAddress> inetAddresses = getLocalAllInetAddress();
        if (!inetAddresses.isEmpty()) {
            return inetAddresses.stream().map(e -> getMacByInetAddress(e)).distinct().collect(Collectors.toList());
        }
        return null;
    }

    /**
     * <p>获取当前服务器所有符合条件的网络地址</p>
     *
     * @return List<InetAddress> 网络地址列表
     * @throws Exception 默认异常
     */
    public static List<InetAddress> getLocalAllInetAddress() throws Exception {
        List<InetAddress> result = new ArrayList<>(4);
        // 遍历所有的网络接口
        for (Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements(); ) {
            NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
            // 在所有的接口下再遍历IP
            for (Enumeration addresses = ni.getInetAddresses(); addresses.hasMoreElements(); ) {
                InetAddress address = (InetAddress) addresses.nextElement();
                //排除LoopbackAddress、SiteLocalAddress、LinkLocalAddress、MulticastAddress类型的IP地址
                if (!address.isLoopbackAddress() /*&& !inetAddr.isSiteLocalAddress()*/
                        && !address.isLinkLocalAddress() && !address.isMulticastAddress()) {
                    result.add(address);
                }
            }
        }
        return result;
    }

    /**
     * <p>获取某个网络地址对应的Mac地址</p>
     *
     * @param inetAddr 网络地址
     * @return String Mac地址
     */
    public static String getMacByInetAddress(InetAddress inetAddr) {
        try {
            byte[] mac = NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    stringBuilder.append("-");
                }
                /** 将十六进制byte转化为字符串 */
                String temp = Integer.toHexString(mac[i] & 0xff);
                if (temp.length() == 1) {
                    stringBuilder.append("0").append(temp);
                } else {
                    stringBuilder.append(temp);
                }
            }
            return stringBuilder.toString().toUpperCase();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLocalHostName() {
        try {
            return getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    /**
     * 获取所有网卡信息
     *
     * @return list
     * @throws SocketException SocketException
     */
    public static List<InetAddress> getInetAddressList() throws SocketException {
        List<InetAddress> retList = new ArrayList<>();
        Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
            Enumeration address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress inetAddress = (InetAddress) address.nextElement();
                String netAdpterName = "未知网卡";
                String netIp = null;
                // 外网IP
                if (!inetAddress.isSiteLocalAddress() && !inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(":") == -1) {
                    netAdpterName = "外网-地址";
                    netIp = inetAddress.getHostAddress();
                    // 内网IP
                } else if (inetAddress.isSiteLocalAddress() && !inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(":") == -1) {
                    netAdpterName = "内网-地址";
                    netIp = inetAddress.getHostAddress();
                }
                if (netIp == null) continue;
                retList.add(inetAddress);
            }
        }
        return retList;
    }

}
