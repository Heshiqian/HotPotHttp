package cn.heshiqian.hotpothttp.gui.window;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private static final Rectangle DEFAULT_WINDOW_SIZE=new Rectangle(500,600);

    public MainWindow(String title) throws HeadlessException {
        super(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(DEFAULT_WINDOW_SIZE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);//不能改变大小
    }

    @Override
    public void dispose() {
        System.out.println("exit gui mode");
        System.exit(0);
    }
}
