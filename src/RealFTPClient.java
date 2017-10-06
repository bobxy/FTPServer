/**
 * Created by Bob on 2016-01-26.
 */
import java.net.Socket;
import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;


public class RealFTPClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(args[0], Integer.parseInt(args[1]));

        DataInputStream reader = new DataInputStream(socket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
        byte[] packet = new byte[1024];
        //reader.read(packet);
        //System.out.println(new String(packet, "UTF-8"));
        String reply = bufferedReader.readLine();
        System.out.println(reply);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String userInput = in.readLine();

        while(!userInput.equals("quit")) {
            if(userInput.substring(0,4).equalsIgnoreCase("USER")){
                //String username = userInput.substring(5);
                writer.writeBytes(userInput+"\r\n");
                //packet = new byte[1024];
                //reader.read(packet);
                //System.out.println(new String(packet, "UTF-8"));
                String resp = bufferedReader.readLine();
                System.out.println(resp);

                userInput = in.readLine();

                continue;
            }
            else if(userInput.substring(0,4).equalsIgnoreCase("SYST")){
                writer.writeBytes(userInput.substring(0,4) + "\r\n");
                //packet = new byte[1024];
                //reader.read(packet);
                String resp = bufferedReader.readLine();
                System.out.println(resp);
                userInput = in.readLine();
                continue;
            }
            else if(userInput.substring(0,3).equalsIgnoreCase("GET")){
                final String filename = userInput.substring(4);
                writer.writeBytes("EPSV\r\n");
                String resp = bufferedReader.readLine();
                System.out.println(resp);
                StringTokenizer st = new StringTokenizer(resp.substring(3), "|.()");
                st.nextToken();
                int portnum = Integer.parseInt(st.nextToken());
                System.out.println(""+portnum);
                final Socket datasocket = new Socket(args[0], portnum);
                writer.writeBytes("RETR " + filename + "\r\n");
                resp = bufferedReader.readLine();
                System.out.println(resp);
                if(resp.substring(0,3).equals("150")) {


                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                DataInputStream readerdata = new DataInputStream(datasocket.getInputStream());
                                File fileOnServer = new File("copy_" + filename);
                                java.io.FileOutputStream fout = new FileOutputStream(fileOnServer);
                                long start = System.currentTimeMillis();
                                while (true) {
                                    byte[] packet = new byte[1024];
                                    int len = readerdata.read(packet);

                                    //System.out.println(new String(packet, "UTF-8"));
                                    //System.out.println("in while: " +datasocket.isConnected() +" and " + len);
                                    if (len > 0) {
                                        fout.write(packet, 0, len);
                                    }
                                    if (len <= 0) {
                                        //System.out.println("fout close");
                                        fout.close();
                                        datasocket.close();

                                        System.out.println("end");
                                        long end = System.currentTimeMillis();
                                        long filelength = fileOnServer.length();
                                        System.out.println("file length:" + filelength);
                                        long timeinterval = end - start;
                                        System.out.println("time took to download this file is:" + timeinterval);

                                        break;
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
                resp = bufferedReader.readLine();
                System.out.println(resp);
                userInput = in.readLine();
                continue;
            }

            writer.writeBytes(userInput + "\r\n");
            packet = new byte[1024];
            byte[] x = new byte[3];
            int lenx= reader.read(x);
            if(! new String(x).equals(new String( "303".getBytes())))
            {
                reader.read(packet);
                int portnum = Integer.parseInt(new String(packet).substring(0,4));
                //System.out.println(new String(packet, "UTF-8")+"|");
                //System.out.println("start to get file from server "+ lenx +" vs " + "303".getBytes().length);
                final String finalUserInput = userInput;




            }
            else{
                System.out.println("error cannot find file");
                System.out.println("please enter next file name");
            }
            System.out.println("please enter next file name");
            userInput = in.readLine();
        }
        writer.writeBytes(userInput + "\r\n");
        socket.close();
    }
}