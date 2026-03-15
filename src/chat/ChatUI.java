package chat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatUI {

    public static void main(String[] args) {

        try {

            String username = JOptionPane.showInputDialog("Enter your username:");

            Socket socket = new Socket("localhost", 5000);

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            PrintWriter output = new PrintWriter(
                    socket.getOutputStream(), true);

            JFrame frame = new JFrame("Chat Messenger - Client");
            frame.setSize(550,400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(Color.WHITE);

            JTextArea chatArea = new JTextArea();
            chatArea.setEditable(false);
            chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            chatArea.setBackground(new Color(245,245,245));
            chatArea.setLineWrap(true);
            chatArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(chatArea);

            JTextField messageField = new JTextField();
            messageField.setPreferredSize(new Dimension(300,40));
            messageField.setFont(new Font("Arial", Font.PLAIN,16));

            JButton sendButton = new JButton("Send");
            sendButton.setPreferredSize(new Dimension(90,40));
            sendButton.setFont(new Font("Arial", Font.BOLD,14));
            sendButton.setBackground(new Color(0,120,215));
            sendButton.setForeground(Color.WHITE);

            JButton clearButton = new JButton("Clear");
            clearButton.setPreferredSize(new Dimension(90,40));
            clearButton.setFont(new Font("Arial", Font.BOLD,14));

            JLabel statusLabel = new JLabel("Status: Connected");
            statusLabel.setFont(new Font("Arial",Font.BOLD,14));

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(messageField, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(sendButton);
            buttonPanel.add(clearButton);

            bottomPanel.add(buttonPanel, BorderLayout.EAST);

            frame.setLayout(new BorderLayout());
            frame.add(statusLabel,BorderLayout.NORTH);
            frame.add(scrollPane,BorderLayout.CENTER);
            frame.add(bottomPanel,BorderLayout.SOUTH);

            frame.setVisible(true);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

            // SEND MESSAGE
            sendButton.addActionListener(e -> {

                String msg = messageField.getText();

                if(!msg.isEmpty()){

                    String time = timeFormat.format(new Date());

                    output.println(username + ": " + msg);

                    chatArea.append("[" + time + "] " + username + ": " + msg + "\n");

                    messageField.setText("");

                    chatArea.setCaretPosition(chatArea.getDocument().getLength());

                }

            });

            messageField.addActionListener(sendButton.getActionListeners()[0]);

            clearButton.addActionListener(e -> chatArea.setText(""));

            // RECEIVE MESSAGES
            new Thread(() -> {

                try {

                    String msg;

                    while((msg = input.readLine()) != null){

                        String time = timeFormat.format(new Date());

                        chatArea.append("[" + time + "] Server: " + msg + "\n");

                        chatArea.setCaretPosition(chatArea.getDocument().getLength());

                    }

                }
                catch(Exception ex){

                    statusLabel.setText("Status: Disconnected");

                }

            }).start();

        }
        catch(Exception e){

            e.printStackTrace();

        }

    }
}