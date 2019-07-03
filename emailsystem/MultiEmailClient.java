import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.applet.*;

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
    private AudioClip clip;
    DefaultListModel listModel = new DefaultListModel();

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

    private void sendActionPerformed(ActionEvent event)
    {
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
            outStream   .writeUTF("read");
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

                    clip = Applet.newAudioClip(new URL("file:sound.au"));
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
                    listModel.addElement(message);
                 //   textArea1.append(message + "\n");
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

    private void attachmentToSendInputMethodTextChanged(InputMethodEvent e) {
        // TODO add your code here
    }

    private void button1ActionPerformed(ActionEvent e) {
        clip.play();
    }

    private void deleteActionPerformed(ActionEvent e) {
        String mailToDelete = (String) list1.getSelectedValue();
        listModel.removeElement(mailToDelete);
        // TODO add your code here
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
        play = new JButton();
        scrollPane2 = new JScrollPane();
        list1 = new JList(listModel);
        delete = new JButton();

        //======== this ========
        var contentPane = getContentPane();

        //---- recipientPrompt ----
        recipientPrompt.setText("Enter recipient: ");

        //---- emailPrompt ----
        emailPrompt.setText("Enter e-mail: ");

        //---- attachmentPrompt ----
        attachmentPrompt.setText("Enter attachment: ");

        //---- attachmentToSend ----
        attachmentToSend.addInputMethodListener(new InputMethodListener() {
            @Override
            public void caretPositionChanged(InputMethodEvent e) {}
            @Override
            public void inputMethodTextChanged(InputMethodEvent e) {
                attachmentToSendInputMethodTextChanged(e);
            }
        });

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

        //---- play ----
        play.setText("Play");
        play.addActionListener(e -> button1ActionPerformed(e));

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(list1);
        }

        //---- delete ----
        delete.setText("Delete");
        delete.addActionListener(e -> deleteActionPerformed(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(30, 30, 30)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(emailPrompt)
                                .addComponent(attachmentPrompt)))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(recipientPrompt)
                            .addGap(18, 18, 18)))
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(emailToSend, GroupLayout.PREFERRED_SIZE, 257, GroupLayout.PREFERRED_SIZE)
                                .addComponent(attachmentToSend, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE))
                        .addComponent(recipientTxtF, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addComponent(send)
                            .addGap(297, 297, 297)
                            .addComponent(read)
                            .addGap(30, 30, 30)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(delete)
                                .addComponent(play))))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(label1, GroupLayout.PREFERRED_SIZE, 221, GroupLayout.PREFERRED_SIZE)
                    .addGap(7, 7, 7))
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(289, 289, 289)
                            .addComponent(label2, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(370, 370, 370)
                            .addComponent(closeConnection)))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(354, 354, 354))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(31, 31, 31)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(recipientTxtF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(recipientPrompt))
                            .addGap(44, 44, 44)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(emailToSend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(emailPrompt))
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(26, 26, 26)
                                    .addComponent(attachmentPrompt))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(attachmentToSend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(224, 224, 224)
                            .addComponent(send))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addComponent(label2))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(71, 71, 71)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label1, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(read)
                                .addComponent(play))))
                    .addGap(31, 31, 31)
                    .addComponent(delete)
                    .addGap(17, 17, 17)
                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(closeConnection)
                    .addGap(26, 26, 26))
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
    private JButton play;
    private JScrollPane scrollPane2;
    private JList list1;
    private JButton delete;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}

