package pack;
//hier ist die GUI
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class View extends JFrame implements ActionListener {
    JButton bVsBot, bVsLokal, bVsOnline;
    JLabel lTitle;
    JFrame fVsBot, fVsLokal, fVsOnline;
    //   frames, die auf buttonclick lokal und online entstehen
    Controller c;
    Graphics2D g;
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
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        //fenster: lokaler gegner
        if (e.getSource() == bVsLokal) {
            fVsLokal = new JFrame();
            fVsLokal.setTitle("Gegen Lokalen Gegner");
            fVsLokal.setLayout(new GridBagLayout());
            fVsLokal.setMinimumSize(new Dimension(1000,1000));
            fVsLokal.setLocationRelativeTo(null);
            Graphics2D g2d = (Graphics2D) g;
            c = new Controller(this, g2d);
            fVsLokal.add(c.board);
            fVsLokal.setVisible(true);
        }
        if (e.getSource() == bVsOnline) {
            fVsOnline = new JFrame();
            fVsOnline.setTitle("Gegen Online Gegner");
            fVsOnline.setLayout(new GridBagLayout());
            fVsOnline.setMinimumSize(new Dimension(1000,1000));
            fVsOnline.setLocationRelativeTo(null);
            Graphics2D g2d = (Graphics2D) g;
            c = new Controller(this, g2d);
            fVsOnline.add(c.board);
            fVsOnline.setVisible(true);
        }
        if (e.getSource() == bVsBot) {
            fVsBot = new JFrame();
            fVsBot.setTitle("Gegen Bot");
            fVsBot.setLayout(new GridBagLayout());
            fVsBot.setMinimumSize(new Dimension(1000,1000));
            fVsBot.setLocationRelativeTo(null);
            Graphics2D g2d = (Graphics2D) g;
            c = new Controller(this, g2d);
            fVsBot.add(c.board);
            fVsBot.setVisible(true);
        }
        }


    public void paint() {
        //int[] boardArray = c.board.giveBoard();
        //irgendwie ist c nicht instanziert, ich kann das board nicht holen...

    }
}
