/**
 * Created by Bob on 2016-01-19.
 */
import javafx.util.converter.ByteStringConverter;

import java.net.Socket;
import java.io.*;
import java.util.Arrays;


public class FTPClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9876);

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
        byte[] packet = new byte[1024];
        reader.read(packet);
        System.out.println(new String(packet, "UTF-8"));
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String userInput = in.readLine();

        while(!userInput.equals("quit")) {

            writer.writeBytes(userInput + "\r\n");
            packet = new byte[1024];
            byte[] x = new byte[3];
            int lenx= reader.read(x);
            if(! new String(x).equals(new String( "303".getBytes())))
            {
                System.out.println(new String(packet, "UTF-8")+"|");
                System.out.println("start to get file from server "+ lenx +" vs " + "303".getBytes().length);
                File fileOnServer=new File(userInput+"copy");
                java.io.FileOutputStream fout = new FileOutputStream(fileOnServer);
                long start=System.currentTimeMillis();
                while(true){
                    packet = new byte[1024];
                    int len = reader.read(packet);
                    System.out.println(new String(packet, "UTF-8"));

                    fout.write(packet, 0, len);

                    if(len<1024){
                        fout.close();
                        System.out.println("end");
                        long end=System.currentTimeMillis();
                        long filelength=fileOnServer.length();
                        System.out.println("file length:"+filelength);
                        long timeinterval=end-start;
                        System.out.println("time took to download this file is:"+timeinterval);
                        System.out.println("please enter next file name");
                        break;
                    }
                }

            }
            else{
                System.out.println("error cannot find file");
                System.out.println("please enter next file name");
            }
            userInput = in.readLine();
        }
        socket.close();
    }
}