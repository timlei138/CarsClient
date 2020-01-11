package com.lc.command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServiceTest {

    public static void main(String[] args) throws IOException {


        ServerSocket socket = new ServerSocket(8888);
        System.out.println(""+socket.getLocalSocketAddress());
        while (true){
            Socket cilent = socket.accept();
            System.out.println(""+cilent.toString());

        }

    }
}
