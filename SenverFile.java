import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class SenverFile {

    static ArrayList<MyFile> myFiles = new ArrayList<>();

    public static void main(String[] args) {
        int fileid = 0;

        JFrame jFrame = new JFrame("SENDER SIDE");
        jFrame.setSize(400, 400);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jlTitle = new JLabel("FILE RECEIVER");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        jFrame.add(jlTitle);
        jFrame.add(jScrollPane);
        jFrame.setVisible(true);

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(1234);
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                int FileNameLength = dataInputStream.readInt();

                if (FileNameLength > 0) {
                    byte[] fileNameBytes = new byte[FileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);

                    int fileContentLength = dataInputStream.readInt();

                    if (fileContentLength > 0) {
                        byte[] fileContentByte = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentByte, 0, fileContentLength);

                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));

                        JLabel jlFileName = new JLabel(fileName);
                        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                        jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));
                        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

                        if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                            jpFileRow.setName(String.valueOf(fileid));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);
                            jFrame.validate();
                        } else {
                            jpFileRow.setName(String.valueOf(fileid));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);

                            jFrame.validate();
                        }
                        myFiles.add(new MyFile(fileid, fileName, fileContentByte, getFileExtension(fileName)));
                        fileid++;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MouseAdapter getMyMouseListener() {

        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel jPanel = (JPanel) e.getSource();

                int fileId = Integer.parseInt(jPanel.getName());

                for (MyFile myFile : myFiles) {
                    if (myFile.getId() == fileId) {
                        JFrame jPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                        jPreview.setVisible(true);
                    }
                }
            }
        };
    }

    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {

        JFrame jFrame = new JFrame("File Download");
        jFrame.setSize(400, 400);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JLabel jlTitle = new JLabel("file download");
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));

        JLabel jlPrompt = new JLabel("Are you sure want to download " + fileName + "?");
        jlPrompt.setFont(new Font("Arial", Font.BOLD, 20));
        jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlPrompt.setAlignmentX((Component.CENTER_ALIGNMENT));

        JButton jbYes = new JButton("YES");
        jbYes.setPreferredSize(new Dimension(150, 75));
        jbYes.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jbNo = new JButton("NO");
        jbNo.setPreferredSize(new Dimension(150, 75));
        jbNo.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel jlFileContent = new JLabel();
        jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButton = new JPanel();
        jpButton.setBorder(new EmptyBorder(20, 0, 10, 0));
        jpButton.add(jbYes);
        jpButton.add(jbNo);

        if (fileExtension.equalsIgnoreCase("txt")) {
            jlFileContent.setText("<html>" + new String(fileData) + "/<html");
        } else {
            jlFileContent.setIcon(new ImageIcon(fileData));

        }

        jbYes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File fileToDownload = new File(fileName);

                try {

                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);

                    fileOutputStream.write(fileData);
                    fileOutputStream.close();

                    jFrame.dispose();

                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        });

        jbNo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });

        jPanel.add(jlTitle);
        jPanel.add(jlPrompt);
        jPanel.add(jlFileContent);
        jPanel.add(jpButton);

        jFrame.add(jPanel);

        return jFrame;
    }

    public static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');

        if (i > 0) {
            return fileName.substring(i + 1);
        } else {
            return "No extension found";
        }

    }
}


