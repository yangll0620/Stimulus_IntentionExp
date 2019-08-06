import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

class CircleButton extends JButton {

    private int label = -1;
 
    public void setLabel(int l) { label = l; }

    public int getLabel_() { return label; }

    public CircleButton(String label) {
        super(label);
        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width, size.height);
        setPreferredSize(size);
        setContentAreaFilled(false);
    }
 
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.lightGray);
        } else {
            g.setColor(getBackground());
        }
        g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
        super.paintComponent(g);
    }
 
    protected void paintBorder(Graphics g) {
        g.setColor(Color.white);
        g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
    }
 
    Shape shape;
 
    public boolean contains(int x, int y) {
        if ((shape == null) || (!shape.getBounds().equals(getBounds()))) {
            shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        }
        return shape.contains(x, y);
    }
}

class NewWindow extends JFrame {

    private JMenuBar menuBar;
    private CircleButton button[] = new CircleButton[10];
    private int buttonNum = 5; 
    private boolean ifClick[] = new boolean[10];
    private static int window_w = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static int window_h = Toolkit.getDefaultToolkit().getScreenSize().height;
    private static int cir_radios = Toolkit.getDefaultToolkit().getScreenSize().height / 6;
    private static int cir_w = Toolkit.getDefaultToolkit().getScreenSize().height / 6;
    private static int cir_h = Toolkit.getDefaultToolkit().getScreenSize().height / 6;
    private int clock_w = Toolkit.getDefaultToolkit().getScreenSize().width / 4;
    private int clock_h = Toolkit.getDefaultToolkit().getScreenSize().height / 4;
    private Color color[] = new Color[10];
    private JButton reGen;

    private JFrame name_win = new JFrame();
    private String name = "Default_Name";

    private int TEST_MODE = 0, REAL_MODE = 1;
    private int mode = TEST_MODE;

    private long start_time_1 = -1, start_time_2 = -1, temp_time = -1;
    private boolean first = true, second = false;
    private long FIVE_MINUTES = 60 * 5 * 1000, ONE_MINUTE_CLOCK = 60, TWO_SECONDS = 2 * 1000;
    private long TEST_PART_A = 10 * 1000, TEST_PART_B = 10 * 1000, TEST_PART_CLOCK = 5;

    private JFrame clock_win = new JFrame("Take a rest for a minute");
    private JLabel clock_text = new JLabel();

    private int turn_ = 0;

    private void init_time() {
        clock_win.setVisible(false);
        clock_win.setLocation(window_w / 2 - clock_w / 2, window_h / 2 - clock_h / 2);
        clock_win.setSize(clock_w, clock_h);
        clock_win.setLayout(new FlowLayout(FlowLayout.CENTER));
        JPanel myJPanel = new JPanel();
		clock_text.setFont(new Font("Serif", Font.BOLD, 35));
        myJPanel.add(clock_text);
        clock_win.getContentPane().add(myJPanel);
        clock_win.setAlwaysOnTop(true);
    }

    // show the countdown dialog
    private void show_time(long t_) {
        clock_win.setVisible(true);
        while (t_ >= 0) {
            long hour = t_ / 3600;
            long minute = (t_ - hour * 3600) / 60;
            long seconds = t_ - hour * 3600 - minute * 60;
            clock_text.setText("<html>Keep Quite and Relax <br>Avoid Movement（including eyes, hands and body et.al）<br>"
                +"This Dialog will disapper once relax time is over<br><br>"+hour+" hour "+minute+" minute "+seconds+" seconds</html>");
            long time_old = System.currentTimeMillis();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println(t_);
            t_--;
        }
        clock_win.setVisible(false);
    }

    private void check_time() throws Exception {
        try {
            temp_time = System.currentTimeMillis();
            if (mode == TEST_MODE) {
                if (first && temp_time - start_time_1 > TEST_PART_A) {
                    show_time(TEST_PART_CLOCK);
                    start_time_2 = System.currentTimeMillis();
                    first = false; second = true;
                }
                if (second && temp_time - start_time_2 > TEST_PART_B) {
                    int result = JOptionPane.showConfirmDialog(this, "实验结束，感谢参与！", "确认", 
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    second = false; 
                    System.exit(0);
                } 
            } else if (mode == REAL_MODE) {
                if (first && temp_time - start_time_1 > FIVE_MINUTES) {
                    show_time(ONE_MINUTE_CLOCK);
                    start_time_2 = System.currentTimeMillis();
                    first = false; second = true;
                }
                if (second && temp_time - start_time_2 > FIVE_MINUTES) {
                    int result = JOptionPane.showConfirmDialog(this, "实验结束，感谢参与！", "确认", 
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    second = false; 
                    System.exit(0);
                } 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendTriggerToC(int l) {
        InputStream ins = null;
        String label = String.valueOf(l);
        String run_exe = "trigger.exe " + label;
        String[] cmd = new String[]{ "cmd.exe", "/C", run_exe };
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            ins = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void set_name() {
        int name_w = window_w / 3, name_h = window_h / 5;
        int input_name_w = name_w / 2, input_name_h = name_h / 4;
        int submit_name_w = name_w / 4, submit_name_h = name_h / 5;

        name_win.setSize(name_w, name_h);
        name_win.getContentPane().setLayout(null);
        name_win.setTitle("Please type in your name:");
        name_win.setLocation(window_w / 2 - name_w / 2, window_h / 2 - name_h / 2);

        JTextField name_input = new JTextField();
        name_input.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 35));
        name_input.setSize(input_name_w, input_name_h);
        name_input.setLocation(name_w / 2 - input_name_w / 2, name_h / 2 - name_h / 3);
        name_input.setVisible(true);
        name_win.getContentPane().add(name_input);

        JButton name_submit = new JButton("submit");
        name_submit.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 35));
        name_submit.setSize(submit_name_w, submit_name_h);
        name_submit.setLocation(name_w / 2 - submit_name_w / 2, name_h / 2 );
        name_submit.setVisible(true);
        name_submit.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                name = name_input.getText();
                name_win.setVisible(false);
                start_time_1 = System.currentTimeMillis();
            }
        });
        name_win.getContentPane().add(name_submit);

        name_win.setAlwaysOnTop(true);
        name_win.setVisible(true);
    }

    public static int[] initRandom_color() {
    	int count = 0, num[] = new int[10], tmp;
    	while (count < 10) {
    		tmp = (int)(1+Math.random()*10)-1;
    		for (int i = 0; i < count; i++)
    			if (num[i] == tmp) break;
    			else if (i == count-1) num[count++] = tmp;
    		if (count == 0) num[count++] = tmp;
    	}
    	return num;
    }

    public static int[][] initRandom_position() {
    	int count = 0, num[][] = new int[10][2], tmp_x, tmp_y;
    	while (count < 10) {
    		tmp_x = (int)(1+Math.random()*((window_w-cir_radios)-cir_radios+1))-1;
    		tmp_y = (int)(1+Math.random()*((window_h-cir_radios)-cir_radios+1))-1;
    		for (int i = 0; i < count; i++)
    			if ((num[i][0]-tmp_x)*(num[i][0]-tmp_x)+(num[i][1]-tmp_y)*(num[i][1]-tmp_y) 
    				< cir_radios*cir_radios) break;
    			else if (i == count-1) {
    				num[count][0] = tmp_x; num[count][1] = tmp_y; count++;
    			}
    		if (count == 0) {
   				num[count][0] = tmp_x; num[count][1] = tmp_y; count++;
    		}
    	}
    	return num;
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

    private void afterAllClick() {
        int all_ran[][] = EEG.getNextPos();
        for (int i = 0; i < buttonNum; i++) {
            button[i].setLabel(all_ran[i][2]);
            button[i].setBackground(color[all_ran[i][2]]);
            button[i].setLocation(all_ran[i][0], all_ran[i][1]);
        }
        for (int i = 0; i < 10; i++) ifClick[i] = false;
    }

    private boolean checkAllClick() {
        try {
            int clickCount = 0;
            for (int i = 0; i < 10; i++)
                if (ifClick[i])
                    clickCount++;
            if (clickCount != buttonNum) return false;
            //Thread.currentThread().sleep(TWO_SECONDS);
            check_time();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void write_to_file(String output, int label) throws IOException {

        sendTriggerToC(label);

        File file = new File("record/EEG_Result.txt");
        BufferedWriter out = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(file, true)));
        out.write(output);
        out.close();
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

    public void kernel() {
        try {
			set_name();
            while (start_time_1 == -1)
                Thread.sleep(1);
            init_time();
            while (true) {
                Thread.sleep(TWO_SECONDS);
                if (turn_ != 0) afterAllClick();
                while (!checkAllClick())
                    Thread.sleep(1);
                turn_++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
}

public class EEG {

    private static int[][][] pos_store = new int[10000][10][3];
    private static int pos_idx = -1;

    public static int[][] getNextPos() {
        if (pos_idx + 1 >= 10000) 
            {pos_idx = 0;}
        else 
            {pos_idx++;}
        
        return pos_store[pos_idx];
    }

    // generate the positions
    private static void generate(int num) throws IOException {
        File file = new File("record/Random_Position.txt");
        BufferedWriter out = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(file, false)));
        String output = "";
        int pos[][] = new int[10][2];
        int col[] = new int[10];
        System.out.println("generating random position ...");
        for (int i = 0; i < num; i++) {
            pos = NewWindow.initRandom_position();
            col = NewWindow.initRandom_color();
            output = "";
            for (int j = 0; j < 10; j++)
                output += "("+String.valueOf(pos[j][0])+","+String.valueOf(pos[j][1])+","+String.valueOf(col[j])+")";
            out.write(output+"\n");
        }
        System.out.println(num + " positions have been generated.");
        out.close();
    }

    //
    private static void load(int num) throws IOException {
        File file = new File("record/Random_Position.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String thisLine = "";
        int line = 0;
        while ((thisLine = br.readLine()) != null && line < num) {
            int idx = 0, cir_idx = 0;
            while (idx < thisLine.length()) {
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

    private static void test_load() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++)
                System.out.print("("+pos_store[i][j][0]+","+pos_store[i][j][1]+","+pos_store[i][j][2]+")");
            System.out.println("");
        }
        for (int i = 9995; i < 10000; i++) {
            for (int j = 0; j < 10; j++)
                System.out.print("("+pos_store[i][j][0]+","+pos_store[i][j][1]+","+pos_store[i][j][2]+")");
            System.out.println("");
        }
    }

    public static void main(String args[]) throws Exception {
        generate(10000);
        load(10000);
        //test_load();
        
        NewWindow myWindow = new NewWindow();
		myWindow.kernel();
        myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myWindow.setVisible(true);
    }

}

/*

Appendix# 1 (color-label table)

LABEL COLOR
  0   black
  1   blue
  2   red
  3   yellow
  4   green
  5   orange
  6   gray
  7   pink
  8   cyan
  9   magenta

*/
