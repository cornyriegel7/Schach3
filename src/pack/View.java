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
    JFrame fVsBot, fVsLokal, fVsOnline, chatFrame, promFrame, connectionFrame;
    JButton bVsBot, bVsLokal, bVsOnline, bSend, bJoin, bHost, bApply;
    JLabel lTitle, IPLabel, portLabel;
    JTextArea taChat;
    JTextField tbEnter, tbIP, tbPort;
    JScrollPane scrollPane;
    JComboBox<String> dropdownProm, dropdownSkins;

    public final static String verbindungGewaehrt = "OKOK", verbindungAbgebrochen = "CCUT", spielZuende = "GMOV", brettEmpfangen = "BOAR";

    boolean host = false;
    private String ip;
    private int pickedPort, promotionValue, pickedMode;
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
        bJoin.setBounds(40,100,100,30);
        bJoin.addActionListener(this);

        bHost = new JButton("Host");
        bHost.setBounds(160,100,100,30);
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
                addPromoWindow(6);
            }

            //Für Onlinespiel
            if (e.getSource() == bVsOnline) {
                addIpPortWindow();
            }

            if (e.getSource() == bJoin) {
                c.chatClient.setIp(tbIP.getText());
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

                c.chatClient.setIp(tbIP.getText());
            }

            //Für Spiel gegen Bot
            if (e.getSource() == bVsBot) {
               createVsBot();
                addPromoWindow(6);
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

    //Createt das Promotion-Menü
    public void addPromoWindow(int pieceValue){
        promFrame = new JFrame();
        promFrame.setTitle("Options:");
        promFrame.setBounds(200,200,600,400);
        promFrame.setLocation(20,500);
        promFrame.setLayout(null);
        promFrame.addWindowListener(this);

        String[] proms = {"Dame", "Turm", "Läufer", "Springer"};
        dropdownProm = new JComboBox<>(proms);
        dropdownProm.addActionListener(this);
        dropdownProm.setBounds(50,50,300,30);
        dropdownProm.setVisible(true);

        String[] skins = {"pink-blau","pink-blau-Apo","oransch-lila","",""};
        dropdownSkins = new JComboBox<>(skins);
        dropdownSkins.addActionListener(this);
        dropdownSkins.setBounds(50,100,300,30);
        dropdownSkins.setVisible(true);

        bApply = new JButton("Apply");
        bApply.addActionListener(this);
        bApply.setBounds(380, 100, 140, 30);
        bApply.setVisible(true);

        promFrame.add(dropdownProm);
        promFrame.add(dropdownSkins);
        promFrame.add(bApply);
        promFrame.setVisible(true);

        promFrame.toBack();
    }

    //Createt das lokale Spielfeld
    public void createVsLokal()
    {
        pickedMode = 1; //modus:1,2 oder 3

        c = new Controller(this);
        c.createBoard();

        fVsLokal = new JFrame();
        fVsLokal.setTitle("Gegen Lokalen Gegner");
        fVsLokal.getContentPane().setBackground(new Color(27, 40, 93));
        fVsLokal.setLayout(new GridBagLayout());
        fVsLokal.setMinimumSize(new Dimension(1000, 1000));
        fVsLokal.setLocationRelativeTo(null);
        fVsLokal.add(c.boardGUI);
        fVsLokal.addWindowListener(this);
        fVsLokal.setVisible(true);
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

                fVsOnline = new JFrame();
                fVsOnline.setTitle("Gegen Online Gegner");
                fVsOnline.getContentPane().setBackground(new Color(236, 5, 5));
                fVsOnline.setLayout(new GridBagLayout());
                fVsOnline.setMinimumSize(new Dimension(1000, 1000));
                fVsOnline.setLocationRelativeTo(null);
                fVsOnline.add(c.boardGUI);
                fVsOnline.addWindowListener(this);
                fVsOnline.setVisible(true);

                chatFrame = new JFrame();
                chatFrame.setTitle("Chat");
                chatFrame.setBounds(1275, 20, 250, 700);
                chatFrame.setLayout(null);

                chatFrame.add(bSend);
                chatFrame.add(scrollPane);
                chatFrame.add(tbEnter);

                chatFrame.setVisible(true);

                addPromoWindow(6);
            }
        }


    //createt das Botspielfeld
    public void createVsBot()
    {
        pickedMode = 3;

        c = new Controller(this);
        c.createBoard();

        fVsBot = new JFrame();
        fVsBot.setTitle("Gegen Bot");
        fVsBot.getContentPane().setBackground(new Color(63, 66, 77));
        fVsBot.setLayout(new GridBagLayout());
        fVsBot.setMinimumSize(new Dimension(1000, 1000));
        fVsBot.setLocationRelativeTo(null);
        fVsBot.addWindowListener(this);
        fVsBot.add(c.boardGUI);
        fVsBot.setVisible(true);
    }

    //createt das IP und Porteingabeframe
    public void addIpPortWindow(){

        connectionFrame = new JFrame();
        connectionFrame.setTitle("IP & Port Eingabe");
        connectionFrame.setSize(300, 200);
        connectionFrame.setLocationRelativeTo(null);
        connectionFrame.setLayout(null);

        // IP Label und TB
        IPLabel = new JLabel("Deine IP:");
        IPLabel.setBounds(10,10,100,30);
        connectionFrame.add(IPLabel);

        tbIP = new JTextField("127.0.0.1");
        tbIP.setBounds(150,10,100,30);
        connectionFrame.add(tbIP);

        // Port &  TB
        portLabel = new JLabel("Port vom Server:");
        portLabel.setBounds(10,50,100,30);
        connectionFrame.add(portLabel);

        tbPort = new JTextField("4000");
        tbPort.setBounds(150,50,100,30);
        connectionFrame.add(tbPort);

        connectionFrame.add(bJoin);
        bJoin.addActionListener(this);
        connectionFrame.add(bHost);
        bHost.addActionListener(this);

        connectionFrame.setVisible(true);
    }

    /**
     * Hier kommt an, was der Client empfängt. (Client ruft diese Methode auf).
     * Anhand von 4-Buchstaben-Codewörtern am Anfang kann verschiedenes gemacht werden, das kann man aber ändern.
     */

    //Todo: Verschiedene Möglichkeiten behandeln: 1. Neue Verbindung, 2. Verbindung getrennt, 3. Chatnachricht wird empfangen, 4. Schachbrett wird empfangen
    public void getString(String text){

        String neueNachricht ="";
        //Kommandowort rausfinden
        String command = text.substring(0, Math.min(text.length(), 4));


            switch (command) {
                case (verbindungGewaehrt):
                    neueNachricht = "Du bist connected \uD83D\uDE0E";
                    break;
                case (verbindungAbgebrochen):
                    neueNachricht = "Verbindung zum Spielpartner getrennt";
                    break;
                //case (spielZuende):
                case (brettEmpfangen):
                    neuesBrett(text.substring(4));
                    return;
            }
        taChat.append("Server: " + text + "\r\n");
        }

    public void neuesBrett(String boardString) {
        int[] recievedBoard = stringToIntArray(boardString);
        c.boardGUI.setSquare(recievedBoard);
        c.boardGUI.repaint();
    }
    public int[] stringToIntArray(String str) {
        // Zuerst entfernen wir die Klammern und Leerzeichen und teilen den String in Teile
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

    public void sendMessage(String message){
        if(message.equals("TEST")) c.chatClient.send(message);
        else c.chatClient.send("MSSG" + message);
    }

    public void checkMateMessage(boolean hasWhiteLost)
    {
        if(hasWhiteLost) JOptionPane.showMessageDialog(null,"Schachmatt! Schwarz gewinnt");
        else JOptionPane.showMessageDialog(null,"Schachmatt! Weiß gewinnt");
        c.input.setActive(false);
        Integer a = JOptionPane.showOptionDialog(null, "Spiel neustarten?","AOEUO", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"JA", "NEIN"}, "JA");

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

    public void disposeAFrame(){
        switch (getPickedMode()){
            case 1: this.fVsLokal.dispose(); break;
            case 2: this.fVsOnline.dispose(); break;
            case 3: this.fVsBot.dispose(); break;
        }
        promFrame.dispose();
    }
    //Getter, setter-Methoden
    public String getIp(){
        return ip;
    }
    public int getPickedPort(){
        return pickedPort;
    }
    public void appendToArea(String pText) {
        taChat.append("Du: " + pText + "\n");
    }
    public int getPickedMode() { return pickedMode; }
    public void setPickedMode(int pPickedMode) {this.pickedMode = pPickedMode;}
    public void setPromotionValue(int pValue){
        promotionValue = pValue;
    }
    public int getPromotionValue(){
        return promotionValue;
    }
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

