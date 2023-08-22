package com.tr.exe;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tr.exe.constant.RSAConst;
import com.tr.exe.kit.DiskKit;
import com.tr.exe.kit.FileKit;
import com.tr.exe.kit.HttpKit;
import com.tr.exe.kit.NetKit;
import com.tr.exe.kit.RSAKit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * 软件授权申请 JFrame 窗口
 * - 支持打包成 exe 使用
 *
 * @Author: TR
 * @Date: 2023/8/8
 */
public class SoftwareLicenseApply extends JFrame implements ActionListener {

    public static final String FILE_NAME = "-申请码.txt";
    public static final String WEB_APPLY_URL = "http://192.168.0.159:5173/#/licenseApply";
    public static final String APPLY = "生成申请码";
    public static final String LICENSE_APPLY = "网页版申请";

    /** 标签 */
    private JLabel label1, label2, label3, label4;
    /** 文本框 */
    private JTextField licenseUser, email;
    /** 下拉框 */
    private JComboBox software, licenseUserType;
    /** 按钮 */
    private JButton applyButton, webApplyButton;
    /** 加载框 */
    JDialog loadingDialog;

    public static void main(String[] args) {
        new SoftwareLicenseApply();
    }

    public SoftwareLicenseApply() {
        this.setTitle("软件授权申请");
        this.setSize(400, 325); // Set the size of the window
        this.setLocationRelativeTo(null); // 窗口显示在屏幕中央
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        this.setLayout(new FlowLayout());// 流式布局t());

        // 创建组件对象
        applyButton = new JButton(APPLY);
        applyButton.addActionListener(this); // 添加动作监听
        webApplyButton = new JButton(LICENSE_APPLY);
        webApplyButton.addActionListener(this); // 添加动作监听

        label1 = new JLabel("申请授权软件："); // 左侧说明文字
        label2 = new JLabel("申请用户：");
        label3 = new JLabel("申请用户类型：");
        label4 = new JLabel("邮箱：");

        licenseUser = new JTextField();
        email = new JTextField();

        software = new JComboBox(); // 创建下拉框并添加选项
        software.addItem("微博");
        software.addItem("支付宝");
        software.addItem("微信");
        software.addItem("QQ");
        software.addItem("抖音");
        software.addItem("京东");
        software.addItem("天猫");
        software.addItem("淘宝");
        software.addItem("Chrome");
        software.addItem("Navicat");
        software.addItem("IDEA");
        software.addItem("VsCode");
        licenseUserType = new JComboBox<>(); // 创建下拉框并添加选项
        licenseUserType.addItem("学校");
        licenseUserType.addItem("企业");
        licenseUserType.addItem("个人");

        // 窗体添加组件
        this.add(label1);
        this.add(software);
        this.add(label2);
        this.add(licenseUser);
        this.add(label3);
        this.add(licenseUserType);
        this.add(label4);
        this.add(email);
        this.add(applyButton);
        this.add(webApplyButton);

        // 设置组件对象的属性/内容
        Dimension dim = new Dimension(350, 30);
        software.setPreferredSize(dim); // 绝对尺寸
        licenseUser.setPreferredSize(dim);
        licenseUserType.setPreferredSize(dim);
        email.setPreferredSize(dim);

        // 加载框
        loadingDialog = new JDialog(this, "正在申请授权...");
        loadingDialog.setSize(200, 100);
        loadingDialog.setLocationRelativeTo(this);

        this.setVisible(true); // Make the window visible
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (APPLY.equals(e.getActionCommand())) {
            if (licenseUser.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "申请用户不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (email.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "邮箱不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath() + "/" + software.getSelectedItem().toString() + FILE_NAME;
                JSONObject baseJson = new JSONObject();
                baseJson.put("software", software.getSelectedItem().toString());
                baseJson.put("licenseUser", licenseUser.getText());
                baseJson.put("licenseUserType", licenseUserType.getSelectedItem().toString());
                baseJson.put("email", email.getText());
                JSONObject deviceJson = new JSONObject();
                deviceJson.put("mac", NetKit.getMacAddress());
                deviceJson.put("firstDiskSerial", DiskKit.getFirstDiskSerial());
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(baseJson);
                jsonArray.add(deviceJson);
                String applyCode = RSAKit.encrypt(jsonArray.toJSONString(), RSAConst.APPLY_CODE_PUBLIC_KEY);
                loadingDialog.setVisible(true);
                // 判断能否连接服务器 ip
                boolean ipReachable = NetKit.ipReachable("192.168.0.159");
                loadingDialog.setVisible(false);
                if (!ipReachable) {
                    JOptionPane.showMessageDialog(this, "无法连接授权服务器，检查本机网络", "网络异常", JOptionPane.ERROR_MESSAGE);
                }
                // 将申请码写入指定文件
                FileKit.writeStringToFile(filePath, applyCode);
                JOptionPane.showMessageDialog(this, "已生成申请码到文件：" + filePath);
                if (ipReachable) {
                    // 将申请码注册到授权服务器
                    JSONObject paramBody = new JSONObject();
                    paramBody.put("applyCode", applyCode);
                    JSONObject resultJson = HttpKit.postRequest("http://192.168.0.159:8082/licenseApply/add/applyCode", paramBody);
                    if (resultJson.getIntValue("responseCode") == 200) {
                        JOptionPane.showMessageDialog(this, "已成功向授权服务器申请授权，等待管理员审批，无需再网页版申请，审批通过后授权码将发送至申请邮箱", "申请成功", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "向授权服务器申请授权失败", "申请失败", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
        if (LICENSE_APPLY.equals(e.getActionCommand())) {
            JOptionPane.showInputDialog(null, "使用生成的申请码前往以下地址申请（用户名：user  密码：123456）：", "提示", JOptionPane.INFORMATION_MESSAGE, null, null, WEB_APPLY_URL);
        }
    }

}
