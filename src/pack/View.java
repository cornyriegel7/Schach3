package pack;
//hier ist die GUI
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class View extends JFrame implements ActionListener {
    JButton bVsBot, bVsLokal, bVsOnline, bSend;
    JLabel lTitle;
    JTextArea taChat;
    JTextField tbEnter;
    JScrollPane scrollPane;
    JFrame fVsBot, fVsLokal, fVsOnline, chatFrame;
    //   frames, die auf buttonclick lokal und online entstehen
    Controller c;
    private int pickedMode;

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
        c.createBoard();
        //fenster: lokaler gegner
        if (e.getSource() == bVsLokal) {
            pickedMode = 1; //modus:1,2 oder 3

            fVsLokal = new JFrame();
            fVsLokal.setTitle("Gegen Lokalen Gegner");
            fVsLokal.getContentPane().setBackground(new Color(27, 40, 93));
            fVsLokal.setLayout(new GridBagLayout());
            fVsLokal.setMinimumSize(new Dimension(1000,1000));
            fVsLokal.setLocationRelativeTo(null);
            fVsLokal.add(c.board.boardgui);
            fVsLokal.setVisible(true);
        }
        if (e.getSource() == bVsOnline) {
            pickedMode = 2;

            fVsOnline = new JFrame();
            fVsOnline.setTitle("Gegen Online Gegner");
            fVsOnline.getContentPane().setBackground(new Color(236, 5, 5));
            fVsOnline.setLayout(new GridBagLayout());
            fVsOnline.setMinimumSize(new Dimension(1000,1000));
            fVsOnline.setLocationRelativeTo(null);
            fVsOnline.add(c.board.boardgui);
            fVsOnline.setVisible(true);

            chatFrame = new JFrame();
            chatFrame.setTitle("Chat");
            chatFrame.setBounds(1275,20,250,700);
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
            fVsBot.setMinimumSize(new Dimension(1000,1000));
            fVsBot.setLocationRelativeTo(null);
            fVsBot.add(c.board.boardgui);
            fVsBot.setVisible(true);
        }

        if(e.getSource()==bSend) {
            this.appendToArea(tbEnter.getText());
        }
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
}
