package cn.xiaosheng.ProtoTest;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import netty.ProtoManager;
import netty.ProtoPrinter;

import com.google.protobuf.LazyStringList;
import com.google.protobuf.Message;

/**
 * 协议联调工具
 * @author 小圣996
 * Java游戏服务器编程 https://www.jianshu.com/u/711bb4362a2a
 */
public class ProtoTestTool extends JFrame {
	private static final long serialVersionUID = 7163462189849326849L;
	private static ProtoTestTool ins = null;
	
	private static String server = "127.0.0.1";//默认链接的服务器ip
	private static String port = "38996";//默认链接的服务器端口
	private static String account = "18888888888";//账号
	private static String selectedStr = "----------请选择需要联调的协议----------";//协议列表默认提示
	
	private JPanel contentPane;//主面板
	
	private JTextField hostInput;//服务器ip输入框
	private JTextField portInput;//服务器端口输入框
	private JTextField accountInput;//游戏账号输入框
	
	private JButton loginBtn; //登录按钮
	private JButton logoutBtn; //下线按钮
	private JButton sendBtn; //发送按钮
	
	private JComboBox<String> protocolCombo; //协议列表
    private JTextArea reqText;//请求协议输入框
	
    private static JTextArea console;//返回协议控制台
	private static JScrollPane scroll;//滚动面板
	
	public static ProtoTestTool getInstance(){
		if(ins == null){
			try {
				ins = new ProtoTestTool();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return ins;
	}
	/**
	 * Launch the application.
	 */
	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProtoTestTool frame = new ProtoTestTool();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws UnknownHostException 
	 */
	public ProtoTestTool() throws UnknownHostException {
		setResizable(false);
		setTitle("游戏协议联调工具 - 简书 小圣996");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 100, 450, 810);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//服务器ip输入框
		JLabel hostLabel = new JLabel("server:", SwingConstants.LEFT);
        hostLabel.setBounds(10, 5, 90, 40);
        contentPane.add(hostLabel);
        
        server = InetAddress.getLocalHost().getHostAddress();
        hostInput = new JTextField(server, 14);
        hostInput.setBounds(55, 10, 150, 30);
        contentPane.add(hostInput);
        
        //服务器端口输入框
        JLabel portLabel = new JLabel("port:", SwingConstants.LEFT);
        portLabel.setBounds(21, 45, 90, 40);
        contentPane.add(portLabel);
        
        portInput = new JTextField(port, 14);
        portInput.setBounds(55, 50, 150, 30);
        contentPane.add(portInput);
        
        //游戏账号输入框
        JLabel accountLabel = new JLabel("account:", SwingConstants.LEFT);
        accountLabel.setBounds(2, 85, 90, 40);
        contentPane.add(accountLabel);
        
        accountInput = new JTextField(account, 14);
        accountInput.setBounds(55, 90, 150, 30);
        contentPane.add(accountInput);
        
        //下线按钮
        logoutBtn = new JButton("下线");
        logoutBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        logoutBtn.setEnabled(false);
        logoutBtn.setVisible(false);
        logoutBtn.setBounds(210, 90, 60, 30);
		contentPane.add(logoutBtn);
		logoutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO 退出逻辑
				logoutBtn.setEnabled(false);
				logoutBtn.setVisible(false);
				loginBtn.setEnabled(true);
				loginBtn.setVisible(true);
				sendBtn.setEnabled(false);
			}
		});
        
		//登陆按钮
        loginBtn = new JButton("登陆");
        loginBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        loginBtn.setBounds(210, 90, 60, 30);
		contentPane.add(loginBtn);
        loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//请求登录
				ProtoManager.getInstance().login(hostInput.getText(), portInput.getText());
				
				loginBtn.setEnabled(false);
				loginBtn.setVisible(false);
				logoutBtn.setEnabled(true);
		        logoutBtn.setVisible(true);
				sendBtn.setEnabled(true);
			}
		});
        
        //请求协议
        JLabel label_2 = new JLabel("请求协议：");
		label_2.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		label_2.setBounds(30, 140, 80, 20);
		contentPane.add(label_2);
		
		//协议列表
		protocolCombo = new JComboBox<String>();
        //protocolCombo.setPreferredSize(new Dimension(215, 20));
        protocolCombo.setMaximumRowCount(25);
        protocolCombo.setBounds(100, 140, 300, 23);
        protocolCombo.addItem(selectedStr);
        protocolCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        protocolCombo.setSelectedItem(selectedStr);
        contentPane.add(protocolCombo);
		
		//发送协议滚动面板
		JScrollPane sendScroll = new JScrollPane();
		sendScroll.setBounds(10, 165, 425, 200);
		contentPane.add(sendScroll);
		sendScroll.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		// 分别设置水平和垂直滚动条自动出现
		sendScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sendScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		reqText = new JTextArea();
		sendScroll.setViewportView(reqText);
		reqText.setMargin(new Insets(5, 5, 5, 5));
		reqText.setLineWrap(true);
		reqText.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		reqText.setEditable(true);
		reqText.setColumns(100);
		reqText.setBackground(Color.WHITE);
		reqText.setText("请填入协议各字段");
		
		//发出请求按钮
		sendBtn = new JButton("发出请求");
		sendBtn.setEnabled(false);
		sendBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		sendBtn.setBounds(10, 370, 130, 25);
		contentPane.add(sendBtn);
		sendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sendReq();
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(ProtoTestTool.this, "请按参数类型填写正确格式，数组用逗号隔开！");
				}
			}
		});
		
		//返回协议
		JLabel label_1 = new JLabel("返回协议：");
		label_1.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		label_1.setBounds(10, 410, 95, 20);
		contentPane.add(label_1);
		
        //返回协议控制台
		scroll = new JScrollPane();
		scroll.setBounds(10, 435, 425, 270);
		scroll.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		contentPane.add(scroll);
		//分别设置水平和垂直滚动条自动出现
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		console = new JTextArea();
		scroll.setViewportView(console);
		console.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		console.setMargin(new Insets(5, 5, 5, 5));
		console.setLineWrap(true);
		console.setEditable(false);
		console.setColumns(100);
		console.setBackground(Color.LIGHT_GRAY);
		
		JButton clearBtn = new JButton("清空控制台");
		clearBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				console.setText("");
			}
		});
		clearBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		clearBtn.setBounds(10, 710, 130, 25);
		contentPane.add(clearBtn);
		
		ProtoManager.getInstance().initAllProtocol();
		TreeMap<Integer, Class<?>> reqMap = ProtoManager.getInstance().getReqMap();
		if(reqMap == null || reqMap.size() <= 0)
			return;
		listProtocal(reqMap);
	}
	
	private void listProtocal(TreeMap<Integer, Class<?>> reqMap){
		List<Integer> list = new ArrayList<Integer>();
        for (Integer protocol : reqMap.keySet()) {
            list.add(protocol);
        }
        Collections.sort(list);

        for (Integer protocol : list) {
            Class<?> clz = reqMap.get(protocol);
            if (clz == null) {
                continue;
            }
            String item = protocol + "-" + clz.getSimpleName();
            protocolCombo.addItem(item);
        }
        protocolCombo.addItemListener(new ComboBoxListener());
	}
	
    private class ComboBoxListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            selectedStr = e.getItem().toString();
            fillUpInput();
        }
    }
    
    public void fillUpInput() {
        String[] strs = selectedStr.split("-");
        if(strs.length != 2){//可能是那句提示语
        	reqText.setText("需填入协议各字段");
        	return;
        }
        int protocol = Integer.parseInt(strs[0]);
        TreeMap<Integer, Class<?>> reqMap = ProtoManager.getInstance().getReqMap();
        Class<?> selectClz = reqMap.get(protocol);
        
        Class<?> buildClass = null;
        for (Class<?> cls : selectClz.getDeclaredClasses()) {
            if ("Builder".equals(cls.getSimpleName())) {
                buildClass = cls;
                break;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        if (buildClass != null) {
            for (Field field : buildClass.getDeclaredFields()) {
                if ("bitField0_".equals(field.getName())) {
                    continue;
                }
                
                String fieldType;
                if(field.getType().isAssignableFrom(String.class)){
                	fieldType = "String";
                }else if(field.getType().isAssignableFrom(List.class)){
                	Class<?> parameterClass = (Class<?>) ((ParameterizedType) field.getGenericType())
                            .getActualTypeArguments()[0];
                	String subFieldType = parameterClass == Integer.class ? "int" : 
                		(parameterClass == Long.class ? "long" : "string");
                	fieldType = "List<"+subFieldType+">";
                }else if(field.getType().isAssignableFrom(LazyStringList.class)){
                	fieldType = "List<string>";
                }else{
                	fieldType = field.getType().toString();
                }
                if(sb.length() > 0)
                	sb.append("\n");
                sb.append(fieldType)
                	.append(" ")
                	.append(field.getName()
                	.replaceAll("_", ""))
                	.append(" = ");
            }
        }

        reqText.setText(sb.toString());
    }
    
    private void sendReq(){
    	String[] strs = selectedStr.split("-");
    	if(strs.length != 2){//可能是那句提示语
			JOptionPane.showMessageDialog(ProtoTestTool.this, "请选择正确协议！");
        	return;
        }
    	
    	//console.setText("等待响应中...");
    	
        int protocol = Integer.parseInt(strs[0]);
        Class<?> selectClz = ProtoManager.getInstance().getReqMap().get(protocol);
        
        try {
            Method newMethod = selectClz.getDeclaredMethod("newBuilder", new Class[0]);
            Object builder = newMethod.invoke(selectClz, new Object[0]);
            Method buildMethod = builder.getClass().getDeclaredMethod("build", new Class[0]);
            String[] keyValues = reqText.getText().split("\n");

            boolean isNullParam = true;
            for (String str : keyValues) {
                if ("".equals(str.trim())) {
                    continue;
                }
                String[] nameValue = str.split("=");
                if (nameValue.length < 2 || nameValue[1].trim().equals("")) {
                    continue;
                }
                isNullParam = false;
                String[] typeValue = nameValue[0].split(" ");
                Field field = builder.getClass().getDeclaredField(typeValue[1] + "_");
                Method setter = null;
                Class<?> parameterClass = null;
                if (field.getType().isAssignableFrom(String.class)) {
                    parameterClass = String.class;
                    setter = builder.getClass().getDeclaredMethod(
                        "set" + typeValue[1].substring(0, 1).toUpperCase()
                            + typeValue[1].substring(1),
                        parameterClass);
                } else if (field.getType().isAssignableFrom(List.class)) {
                    parameterClass = (Class<?>) ((ParameterizedType) field.getGenericType())
                        .getActualTypeArguments()[0];
                    setter = builder.getClass().getDeclaredMethod(
                        "add" + typeValue[1].substring(0, 1).toUpperCase()
                            + typeValue[1].substring(1),
                        getMethodClass(parameterClass));
                } else {
                    parameterClass = field.getType();
                    setter = builder.getClass().getDeclaredMethod(
                        "set" + typeValue[1].substring(0, 1).toUpperCase()
                            + typeValue[1].substring(1),
                        parameterClass);
                }
                if (field.getType().isAssignableFrom(String.class)) {
                    setter.invoke(builder, nameValue[1].trim());
                } else if ("int".equals(field.getType().getSimpleName())) {
                    setter.invoke(builder, Integer.parseInt(nameValue[1].trim()));
                } else if ("long".equals(field.getType().getSimpleName())) {
                    setter.invoke(builder, Long.parseLong(nameValue[1].trim()));
                } else if (field.getType().isAssignableFrom(List.class)) {
                    String[] values = nameValue[1].trim().split(",");
                    for (String value : values) {
                        if (parameterClass == Long.class) {
                            setter.invoke(builder, Long.parseLong(value));
                        } else if (parameterClass == Integer.class) {
                            setter.invoke(builder, Integer.parseInt(value));
                        } else {
                            setter.invoke(builder, value);
                        }
                    }
                }
            }
            
            if(keyValues.length > 1 && isNullParam){
            	JOptionPane.showMessageDialog(ProtoTestTool.this, "不能发送,参数为空!! cmd:"+protocol);
            	return;
            }
            
            //print("\n 协议:"+protocol+"参数如下:");
            Object builded = buildMethod.invoke(builder, new Object[0]);
            System.out.println(ProtoPrinter.parseResps(builded));
            ProtoManager.getInstance().send((Message) builded);
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
    
    private Class<?> getMethodClass(Class<?> clz) {
    	if (clz == Integer.class) {
    		return int.class;
    	} else if (clz == Long.class) {
    		return long.class;
    	} else {
    		return clz;
    	}
    }
	
	public static void print(int cmd, String msg) {
		System.out.println(msg);
		StringBuilder sb = new StringBuilder();
		if(!console.getText().isEmpty()){
			sb.append(console.getText()+"\r\n");
		}
		sb.append("------------收到返回协议："+cmd+"-----------------\n");
		sb.append(msg + "\r\n");
		console.setText(sb.toString());
		// 保持滚动条位于底部
		scroll.getVerticalScrollBar().setValue(
				scroll.getVerticalScrollBar().getMaximum());
	}
	public static JTextArea getConsole() {
		return console;
	}
}
