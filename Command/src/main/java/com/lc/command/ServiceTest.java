package com.lc.command;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServiceTest {

    public static void main(String[] args) throws IOException {


//        ServerSocket socket = new ServerSocket(8888);
//        System.out.println(""+socket.getLocalSocketAddress());
//        while (true){
//            Socket cilent = socket.accept();
//            System.out.println(""+cilent.toString());
//
//        }

        int i = 13155;

        byte[] bytes = intToByteArray(i);


        byte[] s = "video".getBytes();

        String ss = new java.lang.String(s);

        System.out.println("s lenght:"+s.length+",ss:"+ss);

        System.out.println(""+bytes[0]+","+bytes[1]+","+bytes[2]+","+bytes[3]);
        int b = byteArrayToInt(bytes);

        System.out.println(b);

    }



    /**

     * int到byte[] 由高位到低位

     * @param i 需要转换为byte数组的整行值。

     * @return byte数组 */

    public static byte[] intToByteArray(int i) {

        byte[] result = new byte[4];

        result[0] = (byte)((i >> 24) & 0xFF);

        result[1] = (byte)((i >> 16) & 0xFF);

        result[2] = (byte)((i >> 8) & 0xFF);

        result[3] = (byte)(i & 0xFF);

        return result;

    }

    /**

     * byte[]转int

     * @param bytes 需要转换成int的数组

     * @return int值

     */

    public static int byteArrayToInt(byte[] bytes) {

        int value=0;

        for(int i = 0; i < 4; i++) {

            int shift= (3-i) * 8;

            value +=(bytes[i] & 0xFF) << shift;

        }

        return value;

    }




}
