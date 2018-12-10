import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiEmailServer
{
    public static void main(String[] args)
                            throws IOException
    {
        ServerSocket serverSocket = null;
        final int PORT = 1234;
        Socket client;
        ClientHandler handler;
        // test for first git commit
        try
        {
        	
            serverSocket = new ServerSocket(PORT);
        }
        catch (IOException ioEx)
        {
            System.out.println("\nUnable to set up port!");
            System.exit(1);
        }

        System.out.println("\nServer running...\n");
        
        do
        {
           client = serverSocket.accept();
           handler = new ClientHandler(client);
           handler.start();
        }while(true);
    }
}

class ClientHandler extends Thread
{
    private Socket client;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private String username;
    private static ArrayList<String> userConnections = new ArrayList<String>();
    private static ConcurrentLinkedDeque<String> emails = new ConcurrentLinkedDeque<String>();
    
    public ClientHandler(Socket socket) throws IOException
    {
        client = socket;
        outStream = new ObjectOutputStream(client.getOutputStream());
        outStream.flush();
        inStream = new ObjectInputStream(client.getInputStream());
        username = inStream.readUTF();
        System.out.println("\nNew client accepted " + username);
        userConnections.add(username);
    }

    
    public void run()
    {	
    	
        String sendRead = "";
        
		try 
		{
			sendRead = inStream.readUTF();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
        while (!sendRead.equals("quit"))
        {
            System.out.println("\n" + username + " "
                                + sendRead + "ing mail...");
            
            if(sendRead.equals("send"))
            {
                String recipient = "";
                
				try 
				{
					recipient = inStream.readUTF();
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
                try 
                {
					doSend(recipient, username, inStream, emails);
				} 
                catch(Exception e) 
                {
					e.printStackTrace();
				}           
            }
        	

            if(sendRead.equals("read"))
            {
                try 
                {
					doRead(inStream, outStream, username);
				} 
                catch (Exception e) 
                {
					e.printStackTrace();
				}            
            }
            
            
            if(sendRead.equals("trash"))
            {
                try 
                {
					doMoveToTrash(inStream, username);
				} 
                catch (Exception e) 
                {
					e.printStackTrace();
				}
            }
            
            
            try 
            {
				sendRead = inStream.readUTF();
			} 
            catch (IOException e) 
            {
				e.printStackTrace();
			}
			
        }
        try
        {
            client.close();
        }
        catch(IOException ioEx)
        {
            System.out.println("Error closing socket");
            ioEx.printStackTrace();
        }
    }
   
    
    private static void doSend(String recipient, String username, 
     ObjectInputStream inStream, ConcurrentLinkedDeque<String> emails)
    
    {    	
    	String message = "";
    	
		try 
		{
			message = inStream.readUTF();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		String email = recipient + username + ": " + message;
		emails.add(email);
    }
    
    private static void doRead(ObjectInputStream inStream, ObjectOutputStream outStream, String username)
    {   		
   		
    	int noOfEmails = 0;
    	
   		for(int i = 0; i < emails.size(); i++)
   		{
   			String x = emails.peekFirst();
   			if(x.contains(username))
   			{
   				noOfEmails++;
   			}
   		}
   		
   		System.out.println(noOfEmails);
   		
   		try 
   		{
			outStream.writeInt(noOfEmails);
			outStream.flush();
		} 
   		catch (IOException e) 
   		{
			e.printStackTrace();
		}    	
   		
        System.out.println("\nSending " + noOfEmails + " message(s).\n");
        
        
        for(int i = 0; i <userConnections.size(); i++) // for all users in the array
        {
            	String client = userConnections.get(i);
            	if(client.equals(username))
            	{            			
            		while(!emails.isEmpty()) // loop through the mailbox
            		{
            			String checkmail = emails.pollFirst();

            			if(checkmail.contains(username))
            			{
            				
            				String msg = checkmail.substring(checkmail.indexOf(username) 
            						+ username.length());
            			
            				try 
            				{
            					if(msg.contains(".png"))
            					{
            						outStream.writeUTF(msg);
            						outStream.flush();      
        			        	 				
            						String newMsg = msg.substring(msg.indexOf("C"));
        						
            						FileInputStream fileIn = 
													new FileInputStream(newMsg);

            						long fileLen =  (new File(newMsg)).length();
 
            						int intFileLen = (int)fileLen;
						     
            						byte[] byteArray = new byte[intFileLen];

            						fileIn.read(byteArray);
								
            						fileIn.close();

            						outStream.writeObject(byteArray);
            						outStream.flush();
            					}
                			
            					else if (msg.contains(".au"))
            					{	
            						outStream.writeUTF(msg);
            						outStream.flush();                					

            						String newMsg = msg.substring(msg.indexOf("C"));
        						
            						FileInputStream fileIn = 
													new FileInputStream(newMsg);

            						long fileLen =  (new File(newMsg)).length();
 
            						int intFileLen = (int)fileLen;
						     
            						byte[] byteArray = new byte[intFileLen];

            						fileIn.read(byteArray);
								
            						fileIn.close();

            						outStream.writeObject(byteArray);
            						outStream.flush();
            					}	
                			
            					else if (msg.contains(".txt"))
            					{
            						outStream.writeUTF(msg);
            						outStream.flush();       
        						
            						String newMsg = msg.substring(msg.indexOf("C"));
        						
            						FileReader fr = new FileReader(newMsg);
            						char[] charArray = new char[(int) newMsg.length()];				
            						fr.read(charArray, 0, newMsg.length());
            						fr.close();
        						
            						outStream.writeInt(newMsg.length());
            						outStream.flush();
            						for(int x = 0; x < newMsg.length(); x++)
            						{
            							outStream.writeChar(charArray[x]);
            						}
            						outStream.flush();
        						
            						FileInputStream fileIn = 
													new FileInputStream(newMsg);

            						long fileLen =  (new File(newMsg)).length();
 
            						int intFileLen = (int)fileLen;								
            						byte[] byteArray = new byte[intFileLen];
            						fileIn.read(byteArray);
            						fileIn.close();
							
            						outStream.writeObject(byteArray);
            						outStream.flush();        					
            					}
                			
            					else
            					{
            						outStream.writeUTF(msg);
            						outStream.flush();
            					}
            				} 
            				catch (IOException e) 
            				{
            					e.printStackTrace();
            				}
            		}
            	}
            }
        }
    }
 
    
    private static void doMoveToTrash(ObjectInputStream inStream, 
    						String username)
    {
    	String email = "";
    	
		try 
		{
			email = inStream.readUTF();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}   
		
		emails.removeFirstOccurrence(email);
    }
}