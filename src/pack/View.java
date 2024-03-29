package pack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;

public class View extends JFrame implements ActionListener, WindowListener, StringListener {
    static Controller c;
    JFrame fVsBot, fVsLokal, fVsOnline, connectionFrame;
    JButton bVsBot, bVsLokal, bVsOnline, bSend, bJoin, bHost, bApply;
    JLabel lTitle, IPLabel, portLabel;
    JTextArea taChat;
    JTextField tbEnter, tbIP, tbPort;
    JScrollPane scrollPane;
    JComboBox<String> dropdownProm, dropdownSkins;

    public final static String setActive = "ACTN", setInActive = "ACTF", brettEmpfangen = "BOAR",  weiss = "WHIT", schwarz = "BLAC";

    boolean host = false;
    private String ip;
    private int pickedPort, pickedMode;
    public boolean isHost;

    public View(){
        super("Schachprogramm");

        setSize(500,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        lTitle = new JLabel("Schach");
        lTitle.setBounds(170,30,100,30);
        lTitle.setFont(new Font("Verdana", Font.PLAIN, 18));
        add(lTitle);

        bVsBot = new JButton("vs. Bot");
        bVsBot.setBounds(50,100,100,30);
        bVsBot.addActionListener(this);
        add(bVsBot);

        bVsLokal = new JButton("1v1 lokal");
        bVsLokal.setBounds(160,100,100,30);
        bVsLokal.addActionListener(this);
        add(bVsLokal);

        bVsOnline = new JButton("1v1 online");
        bVsOnline.setBounds(270,100,100,30);
        bVsOnline.addActionListener(this);
        add(bVsOnline);

        this.setVisible(true);

        //alle elemente, die nicht auf dem hauptframe sind
        bSend = new JButton("Senden");
        bSend.setBounds(110,625,100,30);
        bSend.addActionListener(this);

        bJoin = new JButton("Join");
        bJoin.setBounds(10,100,100,30);
        bJoin.addActionListener(this);

        bHost = new JButton("Host");
        bHost.setBounds(180,100,100,30);
        bHost.addActionListener(this);

        taChat = new JTextArea();
        taChat.setEditable(false);
        taChat.setLineWrap(true);
        taChat.setWrapStyleWord(true);

        scrollPane = new JScrollPane(taChat);
        scrollPane.setBounds(10,10,200,550);

        tbEnter = new JTextField();
        tbEnter.setBounds(10, 570, 100,30);
    }
    public void actionPerformed(ActionEvent e) {
        if(pickedMode==0) {

            //fenster: lokaler gegner
            if (e.getSource() == bVsLokal) {
                createVsLokal();
            }

            //Für Onlinespiel
            if (e.getSource() == bVsOnline) {
                addIpPortWindow();
            }

            if (e.getSource() == bJoin) {
                createVsOnline();
                c.createChatClient();
            }

            if (e.getSource() == bHost) {
                isHost=true;
                createVsOnline();
                //erst server erstellen
                c.createChatServer(getPickedPort());
                c.createChatClient();
                c.chatClient.StringEmpfangen = this;
            }

            //Für Spiel gegen Bot
            if (e.getSource() == bVsBot) {
               createVsBot();
            }
        }

        if(e.getSource()==bSend) {
            String text = tbEnter.getText();
            taChat.append("Du: " + text + "\r\n");
            sendMessage(text);
        }
        if(e.getSource()==bApply)
        {
            c.piece.changeSkins((String) dropdownSkins.getSelectedItem());
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        pickedMode = 0;
    }


    //Createt das lokale Spielfeld
    public void createVsLokal()
    {
        pickedMode = 1; //modus:1 lokal,2 online oder 3 bot
        c = new Controller(this);
        c.createBoard();
        fVsLokal = createDefaultWindow();
        fVsLokal.setTitle("Gegen Lokalen Gegner");
        fVsLokal.getContentPane().setBackground(new Color(27, 40, 93));
        fVsLokal.setLocationRelativeTo(null);
    }

    //createt das Onlinespielfeld
    public void createVsOnline()
    {
            if(isNumeric(tbPort.getText())) {
                pickedMode = 2;
                ip = tbIP.getText();
                pickedPort = Integer.parseInt(tbPort.getText());
                connectionFrame.dispose();

                c = new Controller(this);
                c.createBoard();

                fVsOnline = createDefaultWindow();
                fVsOnline.setTitle("Gegen Online Gegner");
                fVsOnline.getContentPane().setBackground(new Color(75, 70, 70));
                fVsOnline.setLocationRelativeTo(null);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 1; // Spalte 1
                gbc.gridy = 0; // Zeile 0
                gbc.anchor = GridBagConstraints.NORTHWEST;


                gbc.insets = new Insets(760, 10, 10, 10);
                tbEnter.setPreferredSize(new Dimension(225, 30));
                fVsOnline.add(tbEnter, gbc);

                gbc.insets = new Insets(760, 235, 10, 10);
                bSend.setPreferredSize(new Dimension(75, 30));
                bSend.addActionListener(this);
                fVsOnline.add(bSend, gbc);


                gbc.insets = new Insets(110, 10, 10, 10);
                fVsOnline.add(scrollPane, gbc);
                scrollPane.setPreferredSize(new Dimension(300, 640));


                fVsOnline.setVisible(true);

            }
        }


    //createt das Botspielfeld
    public void createVsBot()
    {
        pickedMode = 3;
        c = new Controller(this);
        c.createBoard();
        fVsBot = createDefaultWindow();
        fVsBot.setTitle("Gegen Bot");
        fVsBot.getContentPane().setBackground(new Color(63, 66, 77));
        fVsBot.setLocationRelativeTo(null);
    }

    public JFrame createDefaultWindow(){
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(1400, 1000));
        frame.addWindowListener(this);
        frame.add(c.boardGUI);
        frame.setVisible(true);


        String[] proms = {"Dame", "Turm", "Läufer", "Springer"};
        dropdownProm = new JComboBox<>(proms);
        dropdownProm.addActionListener(this);
        dropdownProm.setPreferredSize(new Dimension(225, 30));
        dropdownProm.setVisible(true);


        String[] skins = {"schwarz-weiß","pink-blau","pink-blau-apo","orange-lila","hehe"};
        dropdownSkins = new JComboBox<>(skins);
        dropdownSkins.addActionListener(this);
        dropdownSkins.setPreferredSize(new Dimension(225, 30));
        dropdownSkins.setVisible(true);


        bApply = new JButton("Apply");
        bApply.setPreferredSize(new Dimension(75, 30));
        bApply.addActionListener(this);
        bApply.setVisible(true);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1; // Spalte 1
        gbc.gridy = 0; // Zeile 0
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        frame.add(dropdownProm, gbc);

        gbc.insets = new Insets(50, 10, 10, 10);

        frame.add(dropdownSkins, gbc);

        gbc.insets = new Insets(50, 235, 10, 10);

        frame.add(bApply, gbc);

        frame.setVisible(true);


        return frame;
    }

    //createt das IP und Porteingabeframe
    public void addIpPortWindow(){

        connectionFrame = new JFrame();
        connectionFrame.setTitle("IP & Port Eingabe");
        connectionFrame.setSize(300, 200);
        connectionFrame.setLocationRelativeTo(null);
        connectionFrame.setLayout(null);

        // IP Label und TB
        IPLabel = new JLabel("Deine IP / IP des Servers:");
        IPLabel.setBounds(10,10,150,30);
        connectionFrame.add(IPLabel);

        tbIP = new JTextField("192.168.185.9");
        tbIP.setBounds(180,10,100,30);
        connectionFrame.add(tbIP);

        // Port &  TB
        portLabel = new JLabel("Port vom Server:");
        portLabel.setBounds(10,50,100,30);
        connectionFrame.add(portLabel);

        tbPort = new JTextField("33000");
        tbPort.setBounds(180,50,100,30);
        connectionFrame.add(tbPort);

        connectionFrame.add(bJoin);
        bJoin.addActionListener(this);
        connectionFrame.add(bHost);
        bHost.addActionListener(this);

        connectionFrame.setVisible(true);
    }

    /**
     * Hier kommt an, was der Client empfängt. (Client ruft diese Methode auf).
     * Anhand von 4-Buchstaben-Codewörtern
     */

    //Todo: Verschiedene Möglichkeiten behandeln: 1. Chatnachricht wird empfangen, 2. Schachbrett wird empfangen
    public void getString(String text){

        String neueNachricht ="";
        //Kommandowort rausfinden
        String command = text.substring(0, Math.min(text.length(), 4));

        switch (command) {
            case (brettEmpfangen): {
                c.input.setActive(true);
                neuesBrett(text.substring(4));
            }
                return;
            case(setActive): c.input.setActive(true);
                return;
            case(setInActive): c.input.setActive(false);
                return;
            case(weiss): c.dran = 1;
                return;
            case(schwarz): c.dran = -1;
                return;
        }
        taChat.append("Server: " + text + "\r\n");
    }

    public void neuesBrett(String boardString) {
        c.board.Square = stringToIntArray(boardString);
        c.boardGUI.repaint();
    }
    public int[] stringToIntArray(String str) {
        String[] parts = str.substring(1, str.length() - 1).split(", ");
        int[] array = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            array[i] = Integer.parseInt(parts[i]);
        }
        return array;
    }

    //Array wird zu einem String konvertiert, welches durch ein Komma getrennt werden (das encrypten braucht man hier in der view)
    public String intArrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public int getPromotionInt()
        {
            switch ((String) dropdownProm.getSelectedItem())
            {
                case("Springer"):return Piece.knight;
                case("Turm"):return Piece.rook;
                case("Läufer"):return Piece.bishop;
                default: return Piece.queen;
            }
        }

    public static boolean isNumeric(String value) {
        try {
            int number = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void sendMessage(String pMessage){
        c.input.setActive(false);
      c.chatClient.nachrichtVersenden(pMessage);
    }

    public void checkMateMessage(boolean hasWhiteLost)
    {
        if(hasWhiteLost) JOptionPane.showMessageDialog(null,"Schachmatt! Schwarz gewinnt");
        else JOptionPane.showMessageDialog(null,"Schachmatt! Weiß gewinnt");
        c.input.setActive(false);
        Integer a = JOptionPane.showOptionDialog(null, "Spiel neustarten?","Spiel beendet.", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"JA", "NEIN"}, "JA");

        if(a==YES_OPTION) {
            this.disposeAFrame();
            switch (getPickedMode()) {
                case 1: this.createVsLokal(); break;
                //case 2: this.createVsOnline(); break; muss man schaun
                case 3: this.createVsBot(); break;
            }
        }
        if(a==NO_OPTION) {this.disposeAFrame(); setPickedMode(0);}
    }

    public void staleMateMessage() {
        JOptionPane.showMessageDialog(null, "Unentschieden");
        c.input.setActive(false);
        Integer a = JOptionPane.showOptionDialog(null, "Spiel neustarten?", "AOEUO", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"JA", "NEIN"}, "JA");
    }

    public void disposeAFrame(){
        switch (getPickedMode()){
            case 1: this.fVsLokal.dispose(); break;
            case 2: this.fVsOnline.dispose(); break;
            case 3: this.fVsBot.dispose(); break;
        }
    }
    //Getter, setter-Methoden
    public String getIp(){
        return ip;
    }
    public int getPickedPort(){
        return pickedPort;
    }
    public int getPickedMode() { return pickedMode; }
    public void setPickedMode(int pPickedMode) {this.pickedMode = pPickedMode;}
    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}

