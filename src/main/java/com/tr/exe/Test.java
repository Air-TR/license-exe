package com.tr.exe;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;

/**
 * @Author: TR
 * @Date: 2023/8/21
 */
public class Test {

    public static void main(String[] args) {
        JFrame frame = new JFrame("加载框示例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // 设置为不确定模式
        progressBar.setPreferredSize(new Dimension(200, 20));

        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressPanel.setBackground(Color.WHITE);
        progressPanel.add(progressBar);

        panel.add(progressPanel, BorderLayout.CENTER);
        frame.add(panel);

        JDialog loadingDialog = new JDialog(frame, "加载中", true);
        loadingDialog.setSize(200, 100);
        loadingDialog.setLocationRelativeTo(frame);
//        loadingDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
//        loadingDialog.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                // 防止用户关闭加载框
//            }
//        });

        Thread thread = new Thread(() -> {
            // 模拟耗时操作
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 关闭加载框
            loadingDialog.dispose();
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // 显示加载框
                loadingDialog.setVisible(true);

                // 启动耗时操作的线程
                thread.start();
            }
        });

        frame.setVisible(true);
    }

}
