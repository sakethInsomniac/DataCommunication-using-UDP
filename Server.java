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

import java.nio.ByteBuffer;

/**
 * Write a description of class Server here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Server
{
    /**
     * Method ServerApp helps to check the file existing
     * 
     * @param Comms object
     * @throws exception
     */     
    static void serverApp(Comms comms) throws Exception
    {
   String recieveChar;
    byte[] dup; 
    String sendChar;
     System.out.print("\nFile Status: ");
    
       recieveChar = comms.readString().toString();
    
       recieveChar=recieveChar.replace("\n","");
       System.out.println(recieveChar);
       File f=new File(recieveChar);
       ArrayList<String> listFile= new ArrayList<String>();
       if(f.exists())
       {
           
           System.out.println(recieveChar+" exists \n"); 
           FileReader fileReader = new FileReader(f);
           BufferedReader sc = new BufferedReader(fileReader);
           String line=sc.readLine();
           while (line  != null)
           {
               listFile.add(line);
               line=sc.readLine();
           }
           PrintWriter writer = new PrintWriter("client.txt", "UTF-8");
           //String line;
           String t;
           Long s;
           byte[] data = new byte[100];
           int i=1;
            for(int j=0;j<listFile.size();j++)
             {
                 if(j%2==0)
                 {
                  i=0;   
                    }
                  else 
                  {
                      i=1;
                    }
                String fcs="0";
                StringBuffer stringBuffer = new StringBuffer();    
                StringBuffer outBuffer = new StringBuffer(); 
                stringBuffer.append("stx");
                stringBuffer.append(i);
                //System.out.println(listFile.get(j));
                stringBuffer.append(listFile.get(j).toString());
                stringBuffer.append(fcs);
                stringBuffer.append("etx");
                String checkSumString=stringBuffer.toString();
                //System.out.println(checkSumString);
                data=checkSumString.getBytes("UTF-8");
                //System.out.println(data);
                Calculate_checksum c = new Calculate_checksum();
                s = c.checksum(true, data, data.length, null);
                fcs = Long.toString(s);
                //t=Long.toString(s);
                //System.out.println(fcs);
                //System.out.println(line);
                fcs="stx"+i+listFile.get(j)+fcs+"etx";  
                comms.writeString(fcs); 
                System.out.println("Server Frame:"+fcs);
                recieveChar = comms.readString().toString();
                recieveChar=recieveChar.replace("\n","");
                if(recieveChar.equals("ACK"))
                {
                    writer.println(listFile.get(j));
                    System.out.println("Token:"+recieveChar+"\n");
                }
                else
                {
                 System.out.println("Token:"+recieveChar);
                 System.out.println("Resending the data\n");
                 j--;   
                }
                
                //System.out.println(recieveChar);
                
              }
           fileReader.close();   
           comms.writeString("EOT");
           writer.close();
       }
      
     else
     {
         System.out.println(recieveChar+" doesn't exists \n"); 
     }
     
     
    }
    
 /*
 * Check sum class to calculate the check sum of the file and data.
 */
    public static Long checksum(Boolean data, byte[] b, int length, String file) throws IOException {
        if (data) {
            Checksum checksum = new CRC8();
            checksum.update(b, 0, length);
            return checksum.getValue();
        } else {
            CRC8 crc8 = new CRC8();
            int count;
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            while ((count = in.read()) != -1) {
                crc8.update(count);
            }
            in.close();
            return crc8.getValue();
        }
    }
    
    
}







