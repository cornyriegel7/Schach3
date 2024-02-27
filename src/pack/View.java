package pack;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;

import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;

public class View extends JFrame implements ActionListener, WindowListener {
    static Controller c;
    JFrame fVsBot, fVsLokal, fVsOnline, chatFrame, promFrame, connectionFrame;
    JButton bVsBot, bVsLokal, bVsOnline, bSend, bJoin, bHost;
    JLabel lTitle, IPLabel, portLabel;
    JTextArea taChat;
    JTextField tbEnter, tbIP, tbPort;
    JScrollPane scrollPane;
    JComboBox<String> dropdown;

    boolean host = false;
    private String ip = "";
    private int port, promotionValue, pickedMode;

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
                createVsOnline(false);
                c.createChatClient();
            }

            if (e.getSource() == bHost) {
                createVsOnline(true);
                c.createChatServer();
                c.createChatClient();
            }

            //Für Spiel gegen Bot
            if (e.getSource() == bVsBot) {
               createVsBot();
                addPromoWindow(6);
            }
        }

        if(e.getSource()==bSend) {
            this.appendToArea(tbEnter.getText());
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        pickedMode = 0;
        System.out.println("EEEEE");
    }

    //Createt das Promotion-Menü
    public void addPromoWindow(int pieceValue){
        promFrame = new JFrame();
        promFrame.setTitle("Promotion");
        promFrame.setBounds(200,200,500,200);
        promFrame.setLocationRelativeTo(null);
        promFrame.setLayout(null);
        promFrame.addWindowListener(this);

        String[] options = {"Dame", "Turm", "Läufer", "Springer"};
        dropdown = new JComboBox<>(options);
        dropdown.addActionListener(this);
        dropdown.setBounds(50,50,300,30);
        dropdown.setVisible(true);

        promFrame.add(dropdown);
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
    public void createVsOnline(boolean isHost)
    {
        //Prüft ob die Textfelder empty sind
        if(!tbIP.getText().equals("") && !tbPort.getText().equals("")) {
            //Prüft ob im Port-Textfield nur Zahlen stehen  (hoffe es klappt :( )
            if(isNumeric(tbPort.getText())) {
                pickedMode = 2;
                ip = tbIP.getText();
                port = Integer.parseInt(tbPort.getText());
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
            else {
                System.out.println("Es dürfen NUR Zahlen im Port stehen du Knecht");
            }
        }
        else
        {
            System.out.println("Gib eine IP und einen Port ein du Knecht");
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
        IPLabel = new JLabel("IP:");
        IPLabel.setBounds(10,10,100,30);
        connectionFrame.add(IPLabel);

        tbIP = new JTextField();
        tbIP.setBounds(150,10,100,30);
        connectionFrame.add(tbIP);

        // Port &  TB
        portLabel = new JLabel("Port:");
        portLabel.setBounds(10,50,100,30);
        connectionFrame.add(portLabel);

        tbPort = new JTextField();
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
        System.out.println("Da kommt vom server" + text);

        if(text.equals("CONN")){
            taChat.append("Server: Verbindung gewährt!" + "\r\n" + "Sie sind Verbunden!");
        }
        else if(text.equals("DISC")) {
            taChat.append("Server: Verbindung zum Spielpartner getrennt.");
        }


        //sonst: nachricht oder board

        String firstChars = text.substring(0, Math.min(text.length(), 4));
        String rest = text.substring(4);
        switch (firstChars){

            case "MSSG": taChat.append("Gegner:" + rest);
            case "BORD": changeBoard(rest);
        }

    }

    public void changeBoard(String boardString) {
        //int[] newBoard =
        //HIer kommt ein String rein und das Board wird anhand diesem verändert
    }


    //(hoffentlich das richtige) Array wird auf das empfangene String gesetzt, hier braucht man aber ein int[] und kein string[] als rückgabe
    public void convertStringToSquareArray(String squareString) {
        String[] values = squareString.split(",");

        // Check if the string starts with "SERVERNACHRICHT"
        if (squareString.startsWith("SERVERNACHRICHT")) {
            values = Arrays.copyOfRange(values, 1, values.length);
        }

        for (int i = 0; i < values.length; i++) {
            c.board.setSquare(i, Integer.parseInt(values[i]));
        }
    }

    //sonstiges

    public int getPromotionInt()
        {
            switch ((String)dropdown.getSelectedItem())
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
    public int getPort(){
        return port;
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

