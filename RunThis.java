import java.io.*;
import java.util.*;
import java.lang.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class RunThis
{
    public static void main(String[] args) throws Exception
    {
        Comms comms;
        boolean isServer;  // Server or Client?
        String serverName; // Host name of the server to connect to
        int outOfError;     // Error on transfer per character is 1 : outOfError
        // Create an InputStreamReader for reading characters from byte stream System.in
        InputStreamReader inStreamReader = new InputStreamReader (System.in);
        // Create a BufferedReader for reading entire lines of text from the InputStreamReader
        BufferedReader inputReader = new BufferedReader (inStreamReader);
        // Ask the user if this is the server
        System.out.print ("Is this the server (yes/no)? ");
        if (inputReader.readLine ().equalsIgnoreCase ("yes"))
        {
            isServer = true;
        }
        else
        {
            isServer = false;
        }
        // Ask the user for the host name of the server
        if (isServer )
        {
            System.out.println ("The error rate is 1:N characters. N=0 will produce no errors, 2=50%, 10=10%, 20=5%");
            outOfError = Keyboard.readInt("Please enter N: ");
            System.out.println("\nWaiting for connection with client...");
            serverName = " ";
        }
        else
        {
            System.out.print ("Please enter the host name of server: ");
            serverName = inputReader.readLine ();
            outOfError = 0;
        }
        // Create communications object
        try
        {
            comms = new Comms (serverName, 2569, isServer, 9600, outOfError);
        }
        catch (IOException e)
        {
            System.out.println ("An I/O error occurred.");
            comms = null;
        }
        // Establish connection
        // * If this is the server then comms.connect () waits for a client to
        //   connect.
    // * If this is the client then comms.connect () tries to connect to the
    //   server.
    if ((comms != null) && (comms.connect ()))
    {
        System.out.println ("Connected.");
        if (comms.isServer)
        {
            Server s= new Server();
            s.serverApp(comms);
        }else
        {
            Client c = new Client();
            c.clientApp(comms);
        }
        System.out.print("Transfer complete!");
    }
    else
    {
        System.out.println ("Connection Failed.");
    }                  

    }
}

/*
 * Check sum class to calculate the check sum of the file and data.
 */
class Calculate_checksum {
    public Long checksum(Boolean data, byte[] b, int length, String file) throws IOException {
        if (data) {
            Checksum checksum = new CRC32();
            checksum.update(b, 0, length);
            return checksum.getValue();
        } else {
            CRC32 crc32 = new CRC32();
            int count;
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            while ((count = in.read()) != -1) {
                crc32.update(count);
            }
            in.close();
            return crc32.getValue();
        }
    }
}

