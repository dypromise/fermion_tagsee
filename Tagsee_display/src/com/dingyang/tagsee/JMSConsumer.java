package com.dingyang.tagsee;


import javax.jms.Connection;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;


import com.comet.myapp.HelloWorld;


/**
 * 消息的消费者（接受者）
 * @author liang
 *
 */
public class JMSConsumer extends Thread{

    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;//默认连接用户名
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;//默认连接密码
    private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;//默认连接地址

    private static ConnectionFactory connectionFactory;//连接工厂
    private static Connection connection = null;//连接

    private static Session session;//会话 接受或者发送消息的线程
    private static Destination destination;//消息的目的地

    private static MessageConsumer messageConsumer;//消息的消费者
    public static String tag_observation = null;
    public static Boolean obs_arrived = false;

    
    
    public JMSConsumer(){
    	 //实例化连接工厂
        connectionFactory = new ActiveMQConnectionFactory(JMSConsumer.USERNAME, JMSConsumer.PASSWORD, JMSConsumer.BROKEURL);

        try {
            //通过连接工厂获取连接
            connection = connectionFactory.createConnection();
            //启动连接
            connection.start();
            //创建session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建一个连接HelloWorld的消息队列
            destination = session.createQueue("HelloWorld");
            //创建消息消费者
            messageConsumer = session.createConsumer(destination);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
    
	public static void receive_message(){
		 while (true) {
            TextMessage textMessage = null;
			try {
				textMessage = (TextMessage) messageConsumer.receive(100000);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             if(textMessage != null){
                 try {
                	
                	tag_observation = textMessage.getText();
                	HelloWorld.flag = true;
                	JMSConsumer.obs_arrived = true;
					System.out.println("收到的消息:" + tag_observation);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
             }else {
                 break;
             }
         }

	}
	


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	    JMSConsumer.receive_message();
		
	}
}