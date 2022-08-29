package test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author 徒有琴
 */
public class MainFrame {
    public static JLayeredPane pane = new JLayeredPane();//主面板
    public static JLabel role = new JLabel();//人物
    public static BackPanel bg = new BackPanel();//背景
    public static JLabel bagMan = null;//背包男

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(800, 723);
        frame.setTitle("专治八阿哥的孟老师-QQ 574549426");

        role.setIcon(new ImageIcon(MainFrame.class.getResource("/media/R0.png")));
        role.setBounds(90, 400, 108, 113);
        pane.add(role, 300);
        bg.init();
        bg.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        pane.add(bg, 100);
        new Thread(new Runnable() {//背包男
            @Override
            public void run() {
                while (true) {
                    bagMan = new JLabel();
                    bagMan.setIcon(new ImageIcon(MainFrame.class.getResource("/media/v.gif")));
                    bagMan.setBounds(1390, 400, 71, 113);
                    pane.add(bagMan, 200);
                    pane.moveToFront(bagMan);
                    try {
                        int i = 0;
                        while (i < 1390) {
                            if (bagMan == null)
                                break;
                            bagMan.setBounds(1390 - i, 400, 71, 113);
                            i += 10;
                            Thread.sleep(200);
                        }
                        if (bagMan != null) {
                            MainFrame.pane.remove(bagMan);
                            MainFrame.pane.repaint();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        frame.setContentPane(pane);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class BackPanel extends JPanel {
    int margin = 0;
    String direction = "R";
    int count = 0;
    int pic = 0;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        try {
            BufferedImage bg = ImageIO.read(this.getClass().getResource("/media/bg.bmp"));
            g.drawImage(bg, 0, 0, this.getWidth(), this.getHeight(), margin, 0, margin + 200, 241, this);  //将图片的一部分显示在frame上
            g.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //键盘事件
    public void init() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventPostProcessor(new KeyEventPostProcessor() {
            public boolean postProcessKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_LAST) {//弹起
                    pic = 0;
                    if ("u".equals(direction)) {
                        direction = "R";
                        MainFrame.role.setBounds(90, 400, 108, 113);
                    }
                    MainFrame.role.setIcon(new ImageIcon(this.getClass().getResource("/media/" + direction + "0.png")));
                }
                if (e.getID() != KeyEvent.KEY_PRESSED || count++ % 2 != 0) {//按下
                    return true;
                }
                if (count > 9999) {
                    count = 0;
                }
                System.out.println(e.getKeyCode());
                pic++;
                switch (e.getKeyCode()) {
                    case 39://右
                        direction = "R";
                        margin++;
                        break;
                    case 37://左
                        direction = "L";
                        margin--;
                        if (margin < 0) {
                            margin = 0;
                        }
                        break;
                    case 32://空格 跳跃
                        direction = "u";
                        if (pic > 3) {//落地
                            pic = 0;
                            direction = "R";
                            MainFrame.role.setIcon(new ImageIcon(this.getClass().getResource("/media/R0.png")));
                            MainFrame.role.setBounds(90, 400, 108, 113);
                            return true;
                        }
                        MainFrame.role.setIcon(new ImageIcon(this.getClass().getResource("/media/u" + pic + ".png")));
                        MainFrame.role.setBounds(90, 300, 108, 113);
                        return true;
                    case 10://回车 开枪
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JLabel b = new JLabel();
                                Image image = new ImageIcon(this.getClass().getResource("/media/bullet.png"))
                                        .getImage().getScaledInstance(20, 20,
                                                Image.SCALE_DEFAULT);
                                b.setIcon(new ImageIcon(image));
                                b.setBounds(MainFrame.role.getX() + 70, 400, 20, 20);
                                MainFrame.pane.add(b, 120);//添加子弹
                                MainFrame.pane.moveToFront(b);//图层靠前
                                int i = 0;
                                while (i < 830) {
                                    b.setBounds(b.getX() + 10, 400, 71, 113);
                                    i += 20;
                                    if (MainFrame.bagMan != null) {
                                        if (b.getX() >= MainFrame.bagMan.getX()) {
                                            MainFrame.pane.remove(MainFrame.bagMan);
                                            MainFrame.pane.remove(b);
                                            MainFrame.pane.repaint();
                                            MainFrame.bagMan = null;
                                            break;
                                        }
                                    }
                                    try {
                                        Thread.sleep(80);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                MainFrame.pane.remove(b);
                                MainFrame.pane.repaint();
                            }
                        }).start();
                        break;
                }
                if (pic > 7) {
                    pic = 0;
                }
                MainFrame.bg.repaint();
                MainFrame.role.setIcon(new ImageIcon(this.getClass().getResource("/media/" + direction + pic + ".png")));
                return true;
            }
        });
    }
}
