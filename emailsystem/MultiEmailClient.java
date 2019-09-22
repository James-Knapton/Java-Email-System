import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.border.*;

/*
 * Created by JFormDesigner on Sun Sep 22 13:40:41 BST 2019
 */

/**
 * @author James Knapton
 */

public class MultiEmailClient extends JFrame {

    private static ObjectInputStream inStream;
    private static ObjectOutputStream outStream;
    private static String username;
    private static InetAddress host;
    private static Socket socket;
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
    public MultiEmailClient() {
        initComponents();

        final int PORT = 1234;

        try
        {
            host = InetAddress.getLocalHost();
        }

        catch (UnknownHostException uhEx)
        {
            System.out.println("No such host");
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
            System.out.println("Error setting up input and output streams");

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
        label1.setText("Welcome: " + username);
    }

    private void sendMail(ActionEvent e)
    {
        String recipient, email;
        recipient = recipientTxtF.getText();
        email = emailTxtF.getText();

        try
        {
            outStream.writeUTF("send");
            outStream.flush();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        try
        {
            outStream.writeUTF(recipient);
            outStream.flush();
        }
        catch (IOException e2)
        {
            e2.printStackTrace();
        }

        try
        {
            outStream.writeUTF(email);
            outStream.flush();
        }
        catch (IOException e3)
        {
            e3.printStackTrace();
        }

        recipientTxtF.setText("");
        btnSend.setText("");
    }

    private void readMail(ActionEvent e)
    {
        try
        {
            outStream.writeUTF("read");
            outStream.flush();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        int count = 0;

        try
        {
            count = inStream.readInt();
        }
        catch (IOException e2)
        {
            e2.printStackTrace();
        }

        if(count == 0)
        {
            System.out.println(("\nMailbox empty. \n"));
            return;
        }

        for(int i = 0; i < count; i++)
        {
            String message = "";
            try
            {
                message = inStream.readUTF();
                outStream.writeUTF(message);
                System.out.println(message);

                if(message.contains((".png")))
                {

                    String newMsg = message.substring(0, message.indexOf("C"));


                    byte[] byteArray = (byte[])inStream.readObject();

                    //image = new ImageIcon(byteArray);

                    // label1.setIcon(image);
                }

                else if(message.contains(".au"))
                {

                    String newMsg = message.substring(0, message.indexOf("C"));

                    // textArea1.append(newMsg + "\n");

                    byte[] byteArray = (byte[])inStream.readObject();
                    FileOutputStream mediaStream;

                    mediaStream =
                            new FileOutputStream("sound.au");

                    mediaStream.write(byteArray);
                }
                else if(message.contains(".txt"))
                {

                    String newMsg = message.substring(0, message.indexOf("C"));

                    int fileLength = inStream.readInt();
                    char charArray[] = new char[fileLength];
                    for(int x = 0; x < fileLength; x++)
                    {
                        charArray[x] = inStream.readChar();
                    }

                    String attachment = String.copyValueOf(charArray);

                    byte[] byteArray = (byte[])inStream.readObject();

                    FileOutputStream mediaStream;

                    mediaStream =
                            new FileOutputStream("example.txt");

                    mediaStream.write(byteArray);

                }
                else
                {
                    listModel.addElement(message);
                }

            }
            catch (IOException | ClassNotFoundException  e3)
            {
                e3.printStackTrace();
            }

        }
    }

    private void deleteMail(ActionEvent e) {
        String mailToDelete = (String) list1.getSelectedValue();
        listModel.removeElement(mailToDelete);
    }

    private void signOff(ActionEvent e) {
        try
        {
            outStream.writeUTF("quit");
            outStream.flush();
        }
        catch (IOException eX)
        {
            eX.printStackTrace();
        }
        System.exit(0);

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        recipientTxtF = new JTextField();
        label2 = new JLabel();
        label3 = new JLabel();
        emailTxtF = new JTextField();
        scrollPane1 = new JScrollPane();
        list1 = new JList(listModel);
        btnRead = new JButton();
        btnClose = new JButton();
        btnSend = new JButton();
        btnDelete = new JButton();
        buttonBar = new JPanel();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- label1 ----
                label1.setText("Welcome: ");

                //---- label2 ----
                label2.setText("Enter recipient: ");

                //---- label3 ----
                label3.setText("Enter e-mail:");

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(list1);
                }

                //---- btnRead ----
                btnRead.setText("Read emails");
                btnRead.addActionListener(e -> readMail(e));

                //---- btnClose ----
                btnClose.setText("Close Connection");
                btnClose.addActionListener(e -> signOff(e));

                //---- btnSend ----
                btnSend.setText("Send");
                btnSend.addActionListener(e -> sendMail(e));

                //---- btnDelete ----
                btnDelete.setText("Delete email");
                btnDelete.addActionListener(e -> deleteMail(e));

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(contentPanelLayout.createParallelGroup()
                                                .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                                                        .addGap(0, 23, Short.MAX_VALUE)
                                                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 817, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(23, 23, 23))
                                                .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                                                        .addGap(198, 198, 198)
                                                        .addComponent(btnRead)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 234, Short.MAX_VALUE)
                                                        .addComponent(btnDelete)
                                                        .addGap(230, 230, 230))
                                                .addGroup(contentPanelLayout.createSequentialGroup()
                                                        .addComponent(label2)
                                                        .addGap(65, 65, 65)
                                                        .addComponent(recipientTxtF, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 167, Short.MAX_VALUE)
                                                        .addComponent(label3)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(emailTxtF, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(33, 33, 33)
                                                        .addComponent(btnSend)
                                                        .addGap(64, 64, 64))))
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addGap(361, 361, 361)
                                        .addComponent(btnClose)
                                        .addGap(0, 382, Short.MAX_VALUE))
                                .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                                        .addContainerGap(413, Short.MAX_VALUE)
                                        .addComponent(label1, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
                                        .addGap(345, 345, 345))
                );
                contentPanelLayout.setVerticalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(label1)
                                        .addGap(34, 34, 34)
                                        .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(label2)
                                                .addComponent(btnSend)
                                                .addComponent(emailTxtF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(label3)
                                                .addComponent(recipientTxtF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 245, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(btnRead)
                                                .addComponent(btnDelete))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                                        .addComponent(btnClose)
                                        .addContainerGap())
                );
            }
            dialogPane.add(contentPanel, BorderLayout.NORTH);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JTextField recipientTxtF;
    private JLabel label2;
    private JLabel label3;
    private JTextField emailTxtF;
    private JScrollPane scrollPane1;
    private JList list1;
    private JButton btnRead;
    private JButton btnClose;
    private JButton btnSend;
    private JButton btnDelete;
    private JPanel buttonBar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
