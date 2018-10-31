import java.io.*;
import java.util.*;
import java.lang.*;

public class CommChar
{
 
    /**
     * Method ServerApp helps to send the info from server to client
     * 
     * @param Comms object
     * @throws exception
     */
 
    static void serverApp(Comms comms) throws Exception
    {
        // This server App sends typed characters to client.
      String sendChar;   
        int temp;
      System.out.println ("\nEnter characters (quit to exit):");
      do
      {
           sendChar = Keyboard.readString();
           comms.writeString(sendChar); 
            if(comms.socket.isOutputShutdown())//checks for the socket connection of the client and acknowledges whether it is recieved or not.
            {
                 System.out.println("NAK");
            }
            else
            {
                System.out.println("ACK");
            }          
            
      }
      while (!sendChar.equals("quit")); 
      
    }  
      
    /**
     * Method clientApp helps to write the info 
     * 
     * @param Comms object
     * @throws exception
     */       
    static void clientApp(Comms comms) throws Exception
    {
        PrintWriter writer = new PrintWriter("Text.txt", "UTF-8");
     // This client App receives the characters.
     String recieveChar;
     System.out.print("\nCharacters received:");
     do
     {
       recieveChar = comms.readString().toString();
       
       System.out.print(recieveChar);
       recieveChar=recieveChar.replace("\n","");
       writer.println(recieveChar);
     }
     while (!"quit".equals(recieveChar)); 
        writer.close();
    }


            
    


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
            serverApp(comms);
        }else
        {
            clientApp(comms);
        }
        System.out.print("Transfer complete!");
    }
    else
    {
        System.out.println ("Connection Failed.");
    }                  

    }
}

