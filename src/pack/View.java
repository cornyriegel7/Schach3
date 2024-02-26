package pack;
//hier ist die GUI
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class View extends JFrame implements ActionListener, WindowListener {
    static Controller c;
    JFrame fVsBot, fVsLokal, fVsOnline, chatFrame, promFrame, connectionFrame;
    //   alle frames
    JButton bVsBot, bVsLokal, bVsOnline, bSend, submitButton;
    JLabel lTitle, IPLabel, portLabel;
    JTextArea taChat;
    JTextField tbEnter, tbIP, tbPort;
    JScrollPane scrollPane;
    JComboBox<String> dropdown;

    private String ip = "";
    private int port = 0, promotionValue, pickedMode=0;//fake wert nur um if anweisung zu passen

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

        //das ist der main frame
        this.setVisible(true);

        bSend = new JButton("Senden");
        bSend.setBounds(110,625,100,30);
        bSend.addActionListener(this);

        submitButton = new JButton("Submit");
        submitButton.setBounds(100,100,100,30);
        submitButton.addActionListener(this);

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

            if (e.getSource() == submitButton) {
                submitButtonClicked();
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
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        pickedMode = 0;
        System.out.println("EEEEE");
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    //Createt das promframe
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

    //createt das IP und Porteingabeframe
    public void addIpPortWindow(){

        connectionFrame = new JFrame();
        connectionFrame.setTitle("IP & Port Eingabe");
        connectionFrame.setSize(300, 200);
        connectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //dann geht aber alles zu, ist nicht gewollt imo
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

        // Submit Button hinzufügen
        submitButton.addActionListener(this);
        connectionFrame.add(submitButton);

        connectionFrame.setVisible(true);
    }

    //createt das Onlinespielfeld
    public void submitButtonClicked()
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

    public static boolean isNumeric(String value) {
        try {
            int number = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
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

    public void appendToArea(String pText) {
            taChat.append("Du: " + pText + "\n");
        }

    //Get-Methoden
    public int getPickedMode() { return pickedMode; }
    public void setPromotionValue(int pValue){
        promotionValue = pValue;
    }
    public int getPromotionValue(){
        return promotionValue;
    }

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

    public String getIp(){
        return ip;
    }
    public int getPort(){
        return port;
    }

    public void checkMateMessage(boolean hasWhiteLost)
    {
        if(hasWhiteLost) JOptionPane.showMessageDialog(null,"Schachmatt! Schwarz gewinnt");
        else JOptionPane.showMessageDialog(null,"Schachmatt! Weiß gewinnt");
        c.input.setActive(false);
        //todo: außer betrieb setzen!
    }

    /**
     * Hier kommt an, was der Client empfängt. (Client ruft diese Methode auf).
     * Anhand von 4-Buchstaben-Codewörtern am Anfang kann verschiedenes gemacht werden, das kann man aber ändern.
     */

    //Todo: Verschiedene Möglichkeiten behandeln: 1. Neue Verbindung, 2. Verbindung getrennt, 3. Chatnachricht wird empfangen, 4. Schachbrett wird empfangen
    public void getString(String text){

        if(text.equals("OKOK")){
            text = "Server: Verbindung gewährt!";
            taChat.append("Sie sind Verbunden!");

        }
        char[] ersteVierZeichen = new char[4];
        if (text.length() > 3) {
            //Fehler bei einem Zeichen
            System.arraycopy(text.toCharArray(), 0, ersteVierZeichen, 0, 4);
            String command = String.valueOf(ersteVierZeichen);

//            switch (command)
//            {
//
//                case("MSSG"):
//                    text = text.replace("MSSG","");
//                    String[] a = text.split("RNAM");
//
//                    String nachricht = a[0];
//                    String tmpRaumName = a[1];
//
//                    if(tmpRaumName.equals(cbChatNamenList.get(cbChats.getSelectedIndex())))
//                    {
//                        tAchatText.append( nachricht + "\r\n");
//                        return;
//                    }
//                    else
//                    {
//                        tAchatText.append(nachricht + " (in Raum " + tmpRaumName + ")\r\n");
//                        return;
//                    }
//                case("RLEV"):
//                    text = text.replace("RLEV","");
//                    cbChats.removeItem(cbChats.getItemAt(cbChats.getSelectedIndex()));
//                    cbChatNamenList.remove(text);
//                    return;
//            }
        }
//        if(text.equals("DELG")) { tAchatText.setText(""); return;}
//        tAchatText.append(text + "\r\n");
    }
}

