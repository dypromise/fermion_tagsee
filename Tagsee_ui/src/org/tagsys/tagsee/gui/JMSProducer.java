package org.tagsys.tagsee.gui;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
/**
 * 消息的生产者（发送者） 
 * @author liang
 *
 */
public class JMSProducer {

    //默认连接用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    //默认连接密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    //默认连接地址
    private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    //发送的消息数量
    private static final int SENDNUM = 1;
    //连接工厂
    private static ConnectionFactory connectionFactory;
    //连接
    private static Connection connection = null;
    //会话 接受或者发送消息的线程
    private static Session session;
    //消息的目的地
    private static Destination destination;
    //消息生产者
    private static MessageProducer messageProducer;
    
    
    public JMSProducer(){
    	//实例化连接工厂
        connectionFactory = new ActiveMQConnectionFactory(JMSProducer.USERNAME, JMSProducer.PASSWORD, JMSProducer.BROKEURL);

        try {
            //通过连接工厂获取连接
            connection = connectionFactory.createConnection();
            ((ActiveMQConnectionFactory)connectionFactory).setUseAsyncSend(true);
            //启动连接
            connection.start();
            //创建session
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            //创建一个名称为HelloWorld的消息队列
            destination = session.createQueue("HelloWorld");
            //创建消息生产者
            messageProducer = session.createProducer(destination);
            messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            ((ActiveMQConnectionFactory)connectionFactory).setUseAsyncSend(true);
            //发送消息
            //sendMessage(session, messageProducer);
            //session.commit();

            

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            
        }

    }
    
    public void stop_connection() throws JMSException{
    	
    	if(connection != null){
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void sendMessage(String str) throws Exception{
            //创建一条文本消息 
            TextMessage message = session.createTextMessage(str);
//            System.out.println("Activemq 发送消息:" +":"+str);
            //通过消息生产者发出消息 
            messageProducer.send(message);
            session.commit();

    }

   
}