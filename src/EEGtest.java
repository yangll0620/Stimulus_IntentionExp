import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;

import java.awt.Toolkit;
import java.awt.Color;

import swing.CircleButton;

class NewWindow extends JFrame 
{

    private int buttonNum = 5;

    // NewWindow width and height
    private static int window_w = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static int window_h = Toolkit.getDefaultToolkit().getScreenSize().height;
    private static int cir_radios = window_h /6;
    private static int cir_w = window_h / 6;


    private Color color[] = new Color[10];



    public NewWindow() throws IOException {
        super();
        this.setSize(window_w, window_h);
        this.getContentPane().setLayout(null);
        this.setTitle("Welcome to EEG experiment");
        this.setResizable(false);
        initButton();
        for (int i = 0; i < buttonNum; i++)
            this.getContentPane().add(button[i]);
        for (int i = 0; i < 10; i++) ifClick[i] = false;
        this.setVisible(true);
    }

    private void initColor() {
        color[0] = Color.black;
        color[1] = Color.blue;
        color[2] = Color.red;
        color[3] = Color.yellow;
        color[4] = Color.green;
        color[5] = Color.orange;
        color[6] = Color.gray;
        color[7] = Color.pink;
        color[8] = Color.cyan;
        color[9] = Color.magenta;
    }

    private void initButton() {
        initColor();
        int all_ran[][] = EEG.getNextPos();
        for (int i = 0; i < buttonNum; i++) {
            button[i] = new CircleButton("");
            button[i].setLabel(all_ran[i][2]);
            button[i].setBackground(color[all_ran[i][2]]);
            button[i].setSize(cir_w, cir_h);
            button[i].setLocation(all_ran[i][0], all_ran[i][1]);
            button[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        CircleButton b = (CircleButton)e.getSource();
                        if (ifClick[b.getLabel_()]) return;
                        b.setBackground(new Color(225, 225, 225));
                        int X_ = Integer.parseInt(new java.text.DecimalFormat("0").format(b.getLocation().getX()))+e.getX();
                        int Y_ = Integer.parseInt(new java.text.DecimalFormat("0").format(b.getLocation().getY()))+e.getY();
                        String output = name + "   time:"+System.currentTimeMillis()+
                            "ms   label:"+b.getLabel_()+"   X="+X_+"   Y="+Y_+"\n";
                        System.out.print(output);
                        ifClick[b.getLabel_()] = true;
                        write_to_file(output, b.getLabel_());
                        //checkAllClick();
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                /*@Override
                public void mouseReleased(MouseEvent e) {
                    System.out.println("   X="+e.getX()+"   Y="+e.getY());
                }*/
            });
        }
        reGen = new JButton();
        reGen.setText("reset");
        reGen.setSize(80, 30);
        reGen.setLocation(10, 10);
        reGen.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                int re_c[] = initRandom_color();
                int re_p[][] = initRandom_position();
                for (int i = 0; i < buttonNum; i++) {
                    button[i].setBackground(color[re_c[i]]);
                    button[i].setLocation(re_p[i][0], re_p[i][1]);
                }
            }
        });
        for (int i = 0; i < 10; i++)
            ifClick[i] = false;
    }


}


public class EEGtest 
{

    // pos_store stores the positions
    private static int[][][] pos_store = new int[10000][10][3];
    private static int pos_idx = -1;


    // Method for loading the position values into pos_store
    private static void loadPosition(int num) throws IOException 
    {
        File file = new File("record/Random_Position.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String thisLine = "";
        int line = 0;

        System.out.println("Loading positions ...");

        while ((thisLine = br.readLine()) != null && line < num) 
        {
            int idx = 0, cir_idx = 0;
            while (idx < thisLine.length()) 
            {
                idx++;
                String X_ = "", Y_ = "", C_ = "";
                while (thisLine.charAt(idx) != ',') X_ += thisLine.charAt(idx++);
                idx++;
                while (thisLine.charAt(idx) != ',') Y_ += thisLine.charAt(idx++);
                idx++;
                while (thisLine.charAt(idx) != ')') C_ += thisLine.charAt(idx++);
                idx++;
                pos_store[line][cir_idx][0] = Integer.parseInt(X_);
                pos_store[line][cir_idx][1] = Integer.parseInt(Y_);
                pos_store[line][cir_idx][2] = Integer.parseInt(C_);
                cir_idx++;
            }
            line++;
        }

    }

    public static int[][] getNextPos() {
        if (pos_idx + 1 >= 10000) 
            {pos_idx = 0;}
        else 
            {pos_idx++;}
        
        return pos_store[pos_idx];
    }


    
    public static void main(String args[]) throws Exception 
    {
        loadPosition(10);
        
  //       NewWindow myWindow = new NewWindow();
		// myWindow.kernel();
  //       myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  //       myWindow.setVisible(true);
    }
}