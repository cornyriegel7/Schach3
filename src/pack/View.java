package pack;
//hier ist die GUI
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class View extends JFrame implements ActionListener, WindowListener {
    JButton bVsBot, bVsLokal, bVsOnline, bSend;
    JLabel lTitle;
    JTextArea taChat;
    JTextField tbEnter;
    JScrollPane scrollPane;
    JComboBox<String> dropdown;

    JFrame fVsBot, fVsLokal, fVsOnline, chatFrame, promFrame;
    //   frames, die auf buttonclick lokal und online entstehen
    Controller c;
    int promotionValue;
    private int pickedMode=0; //fake wert nur um if anweisung zu passen

    Image pieceImage;

    public View(){
        super("Schachprogramm");

        c = new Controller(this);

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

        taChat = new JTextArea();
        taChat.setEditable(false);

        scrollPane = new JScrollPane(taChat);
        scrollPane.setBounds(10,10,200,550);

        tbEnter = new JTextField();
        tbEnter.setBounds(10, 570, 100,30);
    }
    public void actionPerformed(ActionEvent e) {
        if(pickedMode==0) {
            c.createBoard();
            //fenster: lokaler gegner
            addPromoWindow(6);
            if (e.getSource() == bVsLokal) {
                pickedMode = 1; //modus:1,2 oder 3

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
            if (e.getSource() == bVsOnline) {
                pickedMode = 2;

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
            }
            if (e.getSource() == bVsBot) {
                pickedMode = 3;

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

        public int getPickedMode() { return pickedMode; }
    public void setPromotionValue(int pValue){
        promotionValue = pValue;
    }
    public int getPromotionValue(){
        return promotionValue;
    }

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
}

