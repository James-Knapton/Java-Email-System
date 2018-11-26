import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.applet.*;

public class MultiEmailClient extends JFrame implements ActionListener 
{
	private JPanel contentPane;
	private JTextField recipientTxtF, emailToSend;
	private static JTextArea inbox;
	private JButton send;
	private JButton read;
	private JButton moveToTrash;
	private JButton openInbox;
	private JButton closeInbox;
	private JButton closeConnection;
	private JButton play;
	private static ObjectInputStream inStream;
	private static ObjectOutputStream outStream;
	private static String username;
	private static InetAddress host;
	private ImageIcon image;
	private JTextField attachmentToSend;
	private JLabel lblNewLabel;
	private AudioClip clip;
	private static Socket socket;

	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					MultiEmailClient frame = new MultiEmailClient();
					frame.setVisible(true);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MultiEmailClient() 
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1676, 687);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel entryPanel, readPanel;
		JLabel recipientPrompt, emailPrompt, attachmentPrompt;
		contentPane.setLayout(null);
	
		entryPanel = new JPanel();
		entryPanel.setBounds(373, 80, 1059, 43);
		entryPanel.setLayout(null);
		getContentPane().add(entryPanel);
		send = new JButton("Send");
		send.setBounds(957, 12, 87, 23);
		entryPanel.add(send);
		
		recipientPrompt = new JLabel("Enter recipient: ");
		recipientPrompt.setBounds(15, 16, 126, 14);
		entryPanel.add(recipientPrompt);
		recipientTxtF = new JTextField(15);
		recipientTxtF.setBounds(145, 13, 126, 20);
		entryPanel.add(recipientTxtF);
		recipientTxtF.setEditable(true);
		emailPrompt = new JLabel("Enter e-mail: ");
		emailPrompt.setBounds(405, 16, 97, 14);
		entryPanel.add(emailPrompt);
		emailToSend = new JTextField(15);
		emailToSend.setBounds(517, 13, 126, 20);
		entryPanel.add(emailToSend);
		emailToSend.setEditable(true);
		
		attachmentToSend = new JTextField();
		attachmentToSend.setBounds(811, 13, 116, 20);
		entryPanel.add(attachmentToSend);
		attachmentToSend.setColumns(10);
		attachmentPrompt = new JLabel("Enter attachment: ");
		attachmentPrompt.setBounds(669, 16, 139, 14);
		entryPanel.add(attachmentPrompt);
		send.addActionListener(this);
		
		closeConnection = new JButton("Close Connection");
		closeConnection.setBounds(373, 475, 848, 23);
		closeConnection.addActionListener(this);
		getContentPane().add(closeConnection);
		
		readPanel = new JPanel();
		readPanel.setBounds(373, 126, 1059, 329);
		contentPane.add(readPanel);
		inbox = new JTextArea(10, 15);
		inbox.setVisible(false);
		readPanel.setLayout(null);
		JScrollPane scrollPane = new JScrollPane(inbox);
		scrollPane.setBounds(519, 131, 117, 187);
		readPanel.add(scrollPane);
		
		lblNewLabel = new JLabel(image);
		lblNewLabel.setBounds(838, 113, 155, 187);
		readPanel.add(lblNewLabel);
		read = new JButton("Read e-mails");
		read.setBounds(15, 190, 123, 23);
		readPanel.add(read);
		moveToTrash = new JButton("Move to trash");
		moveToTrash.setBounds(280, 190, 142, 23);
		readPanel.add(moveToTrash);
		openInbox = new JButton("Open Inbox");
		openInbox.setBounds(140, 113, 145, 23);
		readPanel.add(openInbox);
		closeInbox = new JButton("Close inbox");
		closeInbox.setBounds(140, 145, 145, 23);
		readPanel.add(closeInbox);
		
		play = new JButton("Play");
		play.setBounds(147, 190, 118, 23);
		play.addActionListener(this);
		readPanel.add(play);
		closeInbox.addActionListener(this);
		openInbox.addActionListener(this);
		moveToTrash.addActionListener(this);
		read.addActionListener(this);
		

		final int PORT = 1234;

		try
		{
			host = InetAddress.getLocalHost();
		}

		catch (UnknownHostException uhEx)
		{
			inbox.append("No such host");
		}

		try
		{
			socket = new Socket(host, PORT);
			outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.flush();
			inStream = new ObjectInputStream(socket.getInputStream());
		}

		catch (IOException ioEx)
		{
			inbox.append("Error setting up input and output streams");
			
		}

		username = JOptionPane.showInputDialog(null, "Please enter your username :");
		try 
		{
			outStream.writeUTF(username);
			outStream.flush();
		} 
		catch (IOException e) 
		{
			// TODO 
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent event) 
	{
		String recipient, email;
		
		if(event.getSource() == send)
		{
			recipient = recipientTxtF.getText();
			email = emailToSend.getText() + attachmentToSend.getText();
		//	attachment = attachmentToSend.getText();
			try 
			{
				outStream.writeUTF("send");
				outStream.flush();
			} 
			catch (IOException e) 
			{
				// 
				e.printStackTrace();
			}
			try 
			{
				outStream.writeUTF(recipient);
				outStream.flush();
			} 
			catch (IOException e) 
			{ 
				e.printStackTrace();
			}
			try 
			{
				outStream.writeUTF(email);
				outStream.flush();
			} 
			catch (IOException e) 
			{
				// 
				e.printStackTrace();
			}
			
			recipientTxtF.setText("");
			emailToSend.setText("");
			attachmentToSend.setText("");
		}
	
		if(event.getSource() == read)
		{
			
			try 
			{
				outStream.writeUTF("read");
				outStream.flush();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			int count = 0;
			try 
			{
				count = inStream.readInt();
			} 
			catch (IOException e) 
			{
				// 
				e.printStackTrace();
			}
		
			if(count == 0)
			{
				inbox.append("\nMailbox empty. \n");
				return;
			}
			
			/*
			 * // create 100 elements of dummy data. 
			 *  	// *** here will add value of count into array
			 *   
    			Integer[] data = new Integer[100];
    				for (int i = 0; i < data.length; i++) {
        				data[i] = i + 1;
    			}

    		// create a paginated list with page size 20
    		PaginatedList list = new PaginatedList(new JList(data), 20);  
    				// here use array size  which will contain the number of emails
			 * 
			 * 
			 */
			inbox.setText("");

			for(int i = 0; i < count; i++)
			{
				String message = "";
				try 
				{
					message = inStream.readUTF();
				} 
				catch (IOException e) 
				{
					// 
					e.printStackTrace();
				}
				inbox.append(message + "\n");
			}
			
			/*
			
			int count = 0;
			try {
				count = inStream.readInt();
			} catch (IOException e) {
				// 
				e.printStackTrace();
			}
			
			try {
				outStream.writeInt(count);
				outStream.flush();
			} catch (IOException e1) {
				// 
				e1.printStackTrace();
			}
			if(count == 0)
			{
				inbox.append("\nMailbox empty. \n");
				return;
			}
			
			
			for(int i = 0; i < count; i++)
			{
				String message = "";
				try {
					message = inStream.readUTF();
					outStream.writeUTF(message);
					System.out.println(message);
					if(message.contains(".png"))
					{
						
						String newMsg = message.substring(0, message.indexOf("C"));
						
						inbox.append(newMsg + "\n");
						
				
						byte[] byteArray = (byte[])inStream.readObject();

						image = new ImageIcon(byteArray);
					
						lblNewLabel.setIcon(image);
					}
					else if(message.contains(".au"))
					{
						
						String newMsg = message.substring(0, message.indexOf("C"));
						
					
						inbox.append(newMsg + "\n");

						byte[] byteArray = (byte[])inStream.readObject();
				      	FileOutputStream mediaStream;
				      	
				      	mediaStream = 
								new FileOutputStream("sound.au");
				      	
				    	mediaStream.write(byteArray);
				    	
				    	clip = Applet.newAudioClip(new URL("file:sound.au"));
					}
					else if(message.contains(".txt"))
					{
				
						String newMsg = message.substring(0, message.indexOf("C"));
						
					
						inbox.append(newMsg + "\n");
						
						int fileLength = inStream.readInt();
						char charArray[] = new char[fileLength];
						for(int x = 0; x < fileLength; x++)
						{
							charArray[x] = inStream.readChar();
						}
						
						String attachment = String.copyValueOf(charArray);
						inbox.append(attachment);
						
						byte[] byteArray = (byte[])inStream.readObject();
						
				      	FileOutputStream mediaStream;
				      	
				      	mediaStream = 
								new FileOutputStream("cff.txt");
				      	
				    	mediaStream.write(byteArray);
			
					}
					else
					{
						inbox.append(message + "\n");
					}	
				}
				catch (IOException | ClassNotFoundException  e) {
					// 
					e.printStackTrace();
				}
				*/
			}
		
		
		
		if(event.getSource() == play )
    	{
    		clip.play();
    	}		
		
		
		if(event.getSource() == moveToTrash)
		{
			try 
			{
				outStream.writeUTF("trash");
				outStream.flush();
			} 
			catch (IOException e1) 
			{ 
				e1.printStackTrace();
			}
			try 
			{
				outStream.writeUTF(inbox.getSelectedText());
				outStream.flush();
			} 
			catch (IOException e) 
			{ 
				e.printStackTrace();
			}
			inbox.replaceSelection("");
		}
	
		if(event.getSource() == openInbox)
		{
			inbox.setVisible(true);
		}
		
		if(event.getSource() == closeInbox)
		{
			inbox.setVisible(false);
		}
		
		
		if (event.getSource() == closeConnection)
		{
			try 
			{
				outStream.writeUTF("quit");
				outStream.flush();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			System.exit(0);
		}	
	}
}