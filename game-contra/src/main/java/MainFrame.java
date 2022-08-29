import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author 徒有琴
 * 所有组件通用的方法：
 * setBounds(x,y,width,height)
 * add方法添加子控件
 * remove移除子控件
 */
public class MainFrame {
    //JLabel 标签 setIcon(图)可以用来画图
    public static JLabel role = new JLabel();
    public static JLabel bagMan = null;
    //创建背景面板对象
    public static BackPanel bg = new BackPanel();
    //JLayeredPane 分层的面板,控制元素的深度
    public static JLayeredPane pane = new JLayeredPane();

    public static void main(String[] args) {
        //创建一个JFrame窗体
        JFrame frame = new JFrame();
        //对窗体进行设置
        frame.setSize(800, 723);
        frame.setTitle("专治八阿哥的孟老师-QQ 574549426");
        bg.init();
        role.setIcon(new ImageIcon(MainFrame.class.getResource("/media/R0.png")));
        role.setBounds(90, 400, 108, 113);
        bg.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        pane.add(role, 300);
        pane.add(bg, 100);
        //开启单独线程
        new Thread(new Runnable() {//背包男
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

//JPanel面板类,用来放置组件的容器
class BackPanel extends JPanel {
    String direction = "R";
    int pic = 0;
    int margin = 0;
    int count = 0;

    //绘制背景图
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

    //进行初始化设置
    public void init() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventPostProcessor(new KeyEventPostProcessor() {
            public boolean postProcessKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_LAST) {
                    pic = 0;
                    if ("u".equals(direction)) {
                        direction = "R";
                        MainFrame.role.setBounds(90, 400, 108, 113);
                    }
                    MainFrame.role.setIcon(new ImageIcon(this.getClass().getResource("/media/" + direction + "0.png")));
                }
                if (e.getID() != KeyEvent.KEY_PRESSED || count++ % 2 != 0) {
                    return true;
                }
                if (count > 9999) {
                    count = 0;
                }
                pic++;
                //处理不同的按键事件
                switch (e.getKeyCode()) {
                    case 39://右
                        direction = "R";
                        margin++;
                        break;
                    case 37://右
                        direction = "L";
                        margin--;
                        if (margin < 0) {
                            margin = 0;
                        }
                        break;
                    case 32://空格
                        direction = "u";
                        if (pic > 3) {
                            pic = 0;
                            direction = "R";
                            MainFrame.role.setIcon(new ImageIcon(this.getClass().getResource("/media/R0.png")));
                            MainFrame.role.setBounds(90, 400, 108, 113);
                            return true;
                        }
                        MainFrame.role.setIcon(new ImageIcon(this.getClass().getResource("/media/u" + pic + ".png")));
                        MainFrame.role.setBounds(90, 300, 108, 113);
                        return true;
                    case 10://回车
                        new Thread(new Runnable() {
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