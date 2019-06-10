import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.awt.*;

/*
 * Created by JFormDesigner on Sun Jun 09 15:11:28 BST 2019
 */



/**
 * @author James Knapton
 */

public class MultiEmailClient extends JFrame  {

    private ImageIcon image;
    private static ObjectInputStream inStream;
    private static ObjectOutputStream outStream;
    private static String username;
    private static InetAddress host;
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

    public MultiEmailClient()
    {
        initComponents();

        final int PORT = 1234;

        try
        {
            host = InetAddress.getLocalHost();
        }

        catch (UnknownHostException uhEx)
        {
            textArea1.append("No such host");
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
            textArea1.append("Error setting up input and output streams");

        }

        username = JOptionPane.showInputDialog(null, "Please enter your username :");
        try
        {
            outStream.writeUTF(username);
            outStream.flush();
        }
        catch (IOException e)
        {

            e.printStackTrace();
        }
        label2.setText("Welcome: " + username);
    }

    private void sendActionPerformed(ActionEvent event) {
        String recipient, email;
        recipient = recipientTxtF.getText();
        email = emailToSend.getText() + attachmentToSend.getText();
        try
        {
            outStream.writeUTF("send");
            outStream.flush();
        }
        catch (IOException e)
        {
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
            e.printStackTrace();
        }

        recipientTxtF.setText("");
        emailToSend.setText("");
        attachmentToSend.setText("");
    }

    private void readActionPerformed(ActionEvent event) {

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
            e.printStackTrace();
        }

        if(count == 0)
        {
            textArea1.append("\nMailbox empty. \n");
            return;
        }


        textArea1.setText("");


        for(int i = 0; i < count; i++)
        {
            String message = "";
            try
            {
                message = inStream.readUTF();
                outStream.writeUTF(message);
                System.out.println(message);
                if(message.contains(".png"))
                {

                    String newMsg = message.substring(0, message.indexOf("C"));

                    textArea1.append(newMsg + "\n");


                    byte[] byteArray = (byte[])inStream.readObject();

                    image = new ImageIcon(byteArray);

                    label1.setIcon(image);
                }

                else if(message.contains(".au"))
                {

                    String newMsg = message.substring(0, message.indexOf("C"));


                    textArea1.append(newMsg + "\n");

                    byte[] byteArray = (byte[])inStream.readObject();
                    FileOutputStream mediaStream;

                    mediaStream =
                            new FileOutputStream("sound.au");

                    mediaStream.write(byteArray);

                    //    clip = Applet.newAudioClip(new URL("file:sound.au"));
                }
                else if(message.contains(".txt"))
                {

                    String newMsg = message.substring(0, message.indexOf("C"));


                    textArea1.append(newMsg + "\n");

                    int fileLength = inStream.readInt();
                    char charArray[] = new char[fileLength];
                    for(int x = 0; x < fileLength; x++)
                    {
                        charArray[x] = inStream.readChar();
                    }

                    String attachment = String.copyValueOf(charArray);
                    textArea1.append(attachment);

                    byte[] byteArray = (byte[])inStream.readObject();

                    FileOutputStream mediaStream;

                    mediaStream =
                            new FileOutputStream("example.txt");

                    mediaStream.write(byteArray);

                }
                else
                {
                    textArea1.append(message + "\n");
                }

            }
            catch (IOException | ClassNotFoundException  e)
            {

                e.printStackTrace();
            }

        }
    }

    private void closeConnectionActionPerformed(ActionEvent event) {
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        recipientPrompt = new JLabel();
        emailPrompt = new JLabel();
        attachmentPrompt = new JLabel();
        recipientTxtF = new JTextField();
        emailToSend = new JTextField();
        attachmentToSend = new JTextField();
        send = new JButton();
        read = new JButton();
        closeConnection = new JButton();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();
        label1 = new JLabel();
        label2 = new JLabel();

        //======== this ========
        var contentPane = getContentPane();

        //---- recipientPrompt ----
        recipientPrompt.setText("Enter recipient: ");

        //---- emailPrompt ----
        emailPrompt.setText("Enter e-mail: ");

        //---- attachmentPrompt ----
        attachmentPrompt.setText("Enter attachment: ");

        //---- send ----
        send.setText("Send");
        send.addActionListener(e -> sendActionPerformed(e));

        //---- read ----
        read.setText("Read");
        read.addActionListener(e -> readActionPerformed(e));

        //---- closeConnection ----
        closeConnection.setText("Close Connection");
        closeConnection.addActionListener(e -> closeConnectionActionPerformed(e));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(textArea1);
        }

        //---- label1 ----
        label1.setText("text");

        //---- label2 ----
        label2.setText("Welcome ");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(105, 105, 105)
                            .addComponent(send)
                            .addGap(53, 53, 53)
                            .addComponent(read)
                            .addGap(34, 34, 34)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(label1, GroupLayout.PREFERRED_SIZE, 221, GroupLayout.PREFERRED_SIZE)
                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(248, 248, 248)
                            .addComponent(closeConnection))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(30, 30, 30)
                                    .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(attachmentPrompt)
                                        .addComponent(emailPrompt)))
                                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(recipientPrompt)))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(attachmentToSend, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                                .addComponent(recipientTxtF, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                                .addComponent(emailToSend, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label2, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)
                            .addGap(174, 174, 174)))
                    .addContainerGap(124, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(31, 31, 31)
                                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(recipientTxtF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(recipientPrompt))
                                    .addGap(28, 28, 28)
                                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(attachmentPrompt)
                                        .addComponent(attachmentToSend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(33, 33, 33)
                                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(emailPrompt)
                                        .addComponent(emailToSend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(52, 52, 52)
                                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(9, 9, 9)
                                    .addComponent(send))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(26, 26, 26)
                                    .addComponent(label1, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE))))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addComponent(label2)
                            .addGap(165, 165, 165)
                            .addComponent(read)))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                    .addComponent(closeConnection)
                    .addGap(57, 57, 57))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel recipientPrompt;
    private JLabel emailPrompt;
    private JLabel attachmentPrompt;
    private JTextField recipientTxtF;
    private JTextField emailToSend;
    private JTextField attachmentToSend;
    private JButton send;
    private JButton read;
    private JButton closeConnection;
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    private JLabel label1;
    private JLabel label2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}

