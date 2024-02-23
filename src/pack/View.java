package pack;
//hier ist die GUI
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class View extends JFrame implements ActionListener, WindowListener {
    JButton bVsBot, bVsLokal, bVsOnline, bSend, submitButton;
    JLabel lTitle;
    JTextArea taChat;
    JTextField tbEnter;
    JScrollPane scrollPane;
    JComboBox<String> dropdown;

    JFrame fVsBot, fVsLokal, fVsOnline, chatFrame, promFrame, portFrame;
    //   frames, die auf buttonclick lokal und online entstehen
    static Controller c;
    int promotionValue;
    private int pickedMode=0;//fake wert nur um if anweisung zu passen
    private String ip = "";
    private int port = 0;

    Image pieceImage;

    private JTextField ipField;
    private JTextField portField;

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
        //submitButton.setBounds(x,y,width,height); falls schöner machen
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
                addPortWindow();
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
        fVsLokal.add(c.board.boardgui);
        fVsLokal.addWindowListener(this);
        fVsLokal.setVisible(true);
    }

    //createt das Porteingabeframe
    public void addPortWindow(){

        portFrame = new JFrame();
        portFrame.setTitle("IP und Port Eingabe");
        portFrame.setSize(300, 200);
        portFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        portFrame.setLocationRelativeTo(null);

        // Layout setzen
        portFrame.setLayout(new GridLayout(3, 2));

        // IP Label und Textfeld hinzufügen
        portFrame.add(new JLabel("IP: "));
        ipField = new JTextField();
        ipField.setText(null);
        portFrame.add(ipField);

        // Port Label und Textfeld hinzufügen
        portFrame.add(new JLabel("Port: "));
        portField = new JTextField();
        portField.setText(null);
        portFrame.add(portField);

        // Submit Button hinzufügen
        submitButton.addActionListener(this);
        portFrame.add(submitButton);

        portFrame.setVisible(true);
    }

    //createt das Onlinespielfeld
    public void submitButtonClicked()
    {
        //Prüft ob die Textfelder empty sind
        if(ipField.getText().equals("") == false && portField.getText().equals("") == false) {
           //Prüft ob im Port-Textfield nur Zahlen stehen
            if(portField.getText().matches("\\d")) {
                pickedMode = 2;
                ip = ipField.getText();
                port = Integer.parseInt(portField.getText());
                portFrame.dispose();

                c = new Controller(this);
                c.createBoard();

                fVsOnline = new JFrame();
                fVsOnline.setTitle("Gegen Online Gegner");
                fVsOnline.getContentPane().setBackground(new Color(236, 5, 5));
                fVsOnline.setLayout(new GridBagLayout());
                fVsOnline.setMinimumSize(new Dimension(1000, 1000));
                fVsOnline.setLocationRelativeTo(null);
                fVsOnline.add(c.board.boardgui);
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
        fVsBot.add(c.board.boardgui);
        fVsBot.setVisible(true);
    }

    public void appendToArea(String pText) {
            taChat.append("Du: " + pText + "\n");
        }

//    public void createScrollPanel(JFrame pframe)
//    {
//        JTextArea textArea = new JTextArea();
//        for (int i = 0; i < 100; i++) {
//            textArea.append("Zeile " + (i + 1) + "\n");
//        }
//
//        JScrollPane scrollPane = new JScrollPane(textArea);
//        //scrollPane.add(send); was soll denn diese line hier
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//         pframe.add(scrollPane);
//         pframe.setVisible(true);
//    }

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
}

