import java.util.Timer;
import java.util.TimerTask;
/**
 * Write a description of class Client here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Client
{
    /**
     * Method ClientApp helps to send the info from client to server
     * 
     * @param Comms object
     * @throws exception
     */
    static void clientApp(Comms comms) throws Exception
    {
        String sendChar,recieveChar=null,checkString=null;
        // This server App sends typed characters to client.
        System.out.println("Enter File Name to check");
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
            int i,j=1;
            byte[] data = new byte[100];
            Timer timer;
           
         do
         {
             long start = System.nanoTime();
             
             if(j==1)
                 {
                  j=0;   
                    }
                  else if(j==0)
                  {
                      j=1;
                    }
                 
           String fcs="0";
           recieveChar = comms.readString().toString();
           recieveChar = recieveChar.trim();
           recieveChar=recieveChar.replace("\n","");
           String check=recieveChar;
           if(recieveChar.equals("EOT"))
           {
               System.out.println("File saved in Client successfully");
               break;
            }
           recieveChar= recieveChar.replaceAll("[0-9]","");
           i=recieveChar.length();           
           recieveChar=recieveChar.substring(3,i-3);
           //System.out.println(recieveChar);
           //writer.println(recieveChar);
           String checkSum="stx"+j+recieveChar+fcs+"etx";
           data=checkSum.getBytes("UTF-8");
           Calculate_checksum c = new Calculate_checksum();
           long s = c.checksum(true, data, data.length, null);
           fcs = Long.toString(s);
           fcs="stx"+j+recieveChar+fcs+"etx";
           System.out.println("Client Frame:"+fcs);
           long end = System.nanoTime();
           if(fcs.equals(check)&&(((end - start) / 1000000) < 1 ))
           {
               comms.writeString("ACK"); 
           }
           else 
           {
               comms.writeString("NACK");
           } 
        
        
         }while (recieveChar!=null);
     
            
    }  
     
}
