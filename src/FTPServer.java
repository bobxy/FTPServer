import java.io.*;
import java.net.*;
public class FTPServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket=new ServerSocket(9876);
        Socket clientSocket=serverSocket.accept();

        BufferedReader reader=new BufferedReader(new InputStreamReader (clientSocket.getInputStream()));
        System.out.println("Server Launched");
        DataOutputStream writer=new DataOutputStream (clientSocket.getOutputStream());
        byte[] packet=new byte[1024];
        writer.write("Welcome\r\n".getBytes());
        String result=reader.readLine();
        while(!result.equals(null))
        {
            System.out.println("from Bob:"+result);
            File fileOnServer=new File(result);
            if(!fileOnServer.exists())
            {
                String errorMessage="303";
                System.out.println(errorMessage);
                writer.write("303".getBytes());
            }
            else {
                java.io.FileInputStream fin = new FileInputStream(fileOnServer);

                writer.write("200".getBytes());
                while (true) {
                    packet = new byte[1024];
                    int length = fin.read(packet);
                    System.out.println(new String(packet, "UTF-8") + "|");

                    writer.write(packet,0,length);

                    if (length < 1024) {
                        break;
                    }
                }
                fin.close();
                System.out.println("sent finish");
                long filelength=fileOnServer.length();
                System.out.println("File Length:"+filelength);

            }
            result=reader.readLine();
        }
    }
}