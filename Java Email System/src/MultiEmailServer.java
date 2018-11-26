import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.sql.*;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.*;

public class MultiEmailServer
{
    public static void main(String[] args)
                            throws IOException
    {
        ServerSocket serverSocket = null;
        final int PORT = 1234;
        Socket client;
        ClientHandler handler;
        
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
    private static String email;
    private static int emailID; 
    private static int noOfEmails;
    private static ArrayList<Integer> emailIDs = new ArrayList<Integer>();
    private static ConcurrentLinkedDeque<String> emails;
    
    public ClientHandler(Socket socket) throws IOException
    {
        client = socket;
        outStream = new ObjectOutputStream(client.getOutputStream());
        outStream.flush();
        inStream = new ObjectInputStream(client.getInputStream());
        username = inStream.readUTF();
        System.out.println("\nNew client accepted " + username);
        userConnections.add(username);
        
        try
        {
        	getDBConnection();
        }
        catch(PropertyVetoException e)
        {
        	e.printStackTrace();
        }
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
					doSend(recipient, username, inStream);
				} 
                catch (PropertyVetoException e) 
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
                catch (PropertyVetoException e) 
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
                catch (PropertyVetoException e) 
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
    
    private static ComboPooledDataSource getDBConnection() throws PropertyVetoException
    {
    	ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setJdbcUrl("jdbc:mysql://localhost/emailclients");
		cpds.setUser("root");
		cpds.setPassword("c0ll13");	
		return cpds;
    }
    
    private static void doSend(String recipient, String username, 
     ObjectInputStream inStream) throws PropertyVetoException
    
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
		
		email = username + ": " + message;
		
		Connection connection = null;
   		Connection connection2 = null;
   		PreparedStatement statement = null;
   		PreparedStatement statement2 = null;
   		ResultSet resultSet = null;
   		
   		
   		try
   		{
   			ComboPooledDataSource dSource = ClientHandler.getDBConnection();
   			connection = dSource.getConnection();
   			String select = "SELECT emailID FROM emails";
   			statement = connection.prepareStatement(select);
   			resultSet = statement.executeQuery();
   			
   			while(resultSet.next())
   			{
   				emailIDs.add(resultSet.getInt(1));
   			}
   			connection.close();
   		}
   		catch(SQLException sqlEx)
   		{
   			sqlEx.printStackTrace();
   		}
   		
   		for(int i = 0; i < emailIDs.size(); i++)
   		{
   			int j = emailIDs.get(i);
   			if(j == emailID)
   			{
   				emailID++;
   			}
   		}
   		
   		try
   		{
   			ComboPooledDataSource dataSource = ClientHandler.getDBConnection();
   			connection2 = dataSource.getConnection();
   			String insert = "INSERT INTO emails (emailID, recipient, email)" + " VALUES(?, ?, ?)";
   			statement2 = connection2.prepareStatement(insert);
  			statement2.setInt(1, emailID);
  			statement2.setString(2, recipient);
  			statement2.setString(3, email);
   			statement2.execute();
   		    connection2.close();
   		}
   		catch(SQLException sqlEx)
   		{
   			sqlEx.printStackTrace();
   		}
    }
    
    private static void doRead(ObjectInputStream inStream, ObjectOutputStream outStream, String username) throws PropertyVetoException
    {
    	Connection connection = null;
   		PreparedStatement statement = null;
   		ResultSet resultSet = null;
   		emails = new ConcurrentLinkedDeque<String>();
		
   		System.out.println(emails.size());
   		try 
   		{
   			ComboPooledDataSource dataSource = ClientHandler.getDBConnection();
   			connection = dataSource.getConnection();
   			String select = "SELECT * FROM emails WHERE recipient = '" + username +"'" 
   					+ "ORDER BY emailID DESC";
   			statement = connection.prepareStatement(select);	
   			resultSet = statement.executeQuery();
   			
   			while (resultSet.next())
   			{
   				email = resultSet.getString(3);	
   				emails.add(email);
   			}
   			connection.close();
   		}   		
   		catch(SQLException sqlEx)
   		{
   			sqlEx.printStackTrace();
   		}
   		
   		System.out.println(emails.size());
   		noOfEmails = emails.size();
   		
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
            			String msg = emails.pollFirst();
            												
            			try 
            			{
                			if(msg.contains(".png"))
                			{
                			    outStream.writeUTF(msg);
        						outStream.flush();      
        			        	 				
        						String newMsg = msg.substring(msg.indexOf("C"));
        						// String fileType = msg.substring(msg.indexOf("."))
        						// String newMsg = msg.substring(msg.lastIndexOf(": ")) 
        						
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		}
            	}
        }
    }
 
    
    private static void doMoveToTrash(ObjectInputStream inStream, 
    						String username) throws PropertyVetoException   
    {
    	int emailID = 0;
    	String recipient = "";
    	String email = "";
    	
		try {
			email = inStream.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Connection connection3 = null;
  	    PreparedStatement statement3 = null;
  	    ResultSet resultSet = null;
  	    
  	    try
  	    {
  	    	ComboPooledDataSource dataSource = ClientHandler.getDBConnection();
  	    	connection3 = dataSource.getConnection();
  	    	String select = "SELECT * FROM emails WHERE email = '" + email + "'";
  	    	statement3 = connection3.prepareStatement(select);
  	    	resultSet = statement3.executeQuery();
  	    	while(resultSet.next())
  	    	{
  	    		emailID = resultSet.getInt(1);
  	    		recipient = resultSet.getString(2);
  	    		email = resultSet.getString(3);
  	    	}
  	    	connection3.close();
  	    	
  	    }
  	    catch(SQLException sqlEx)
  	    {
  	    	sqlEx.printStackTrace();
  	    }

  	    
		Connection connection = null;
		PreparedStatement statement = null;
		
  	    try
   		{
   			ComboPooledDataSource dataSource = ClientHandler.getDBConnection();
   			connection = dataSource.getConnection();
   			String delete = "DELETE FROM emails WHERE email = '" + email + "'"; 
   			statement = connection.prepareStatement(delete);
   			statement.executeUpdate(delete);
   			
   		    connection.close();
   		}
   		catch(SQLException sqlEx)
   		{
   			sqlEx.printStackTrace();
   		}
   		
  	    
  	    Connection connection2 = null;
  	    PreparedStatement statement2 = null;
  	    
  	    try
  	    {
  	    	ComboPooledDataSource dataSource = ClientHandler.getDBConnection();
   			connection2 = dataSource.getConnection();
   			String insert = "INSERT INTO deletedemails (emailID, recipient, email)" + "VALUES(?, ?, ?)";
   			statement2 = connection2.prepareStatement(insert);
   			statement2.setInt(1, emailID);
  			statement2.setString(2, recipient);
  			statement2.setString(3, email);
   			statement2.execute();
   		    connection2.close();
  	    }
  	    catch(SQLException sqlEx)
  	    {
  	    	sqlEx.printStackTrace();
  	    }
    }
}