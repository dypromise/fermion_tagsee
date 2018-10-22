package com.comet.myapp;

import javax.jms.JMSException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.comet4j.core.CometContext;
import org.comet4j.core.CometEngine;

import com.dingyang.tagsee.ConsumeTools;


public class HelloWorld implements ServletContextListener {  
    // 频道1  
    private static final String CHANNEL1 = "channel1";  
    // 通过频道1推送给前台的变量1  
    private static int number1 = 0;  
    public static boolean flag = false;
    public static CometEngine engine = null;
    static ConsumeTools cTools = new ConsumeTools();
    
    
    /** 
     * 初始化上下文 
     */  
    public void contextInitialized(ServletContextEvent servletContextEvent) {  
        // CometContext ： Comet4J上下文，负责初始化配置、引擎对象、连接器对象、消息缓存等。  
        CometContext cc = CometContext.getInstance();  
        // 注册频道，即标识哪些字段可用当成频道，用来作为向前台传送数据的“通道”  
        cc.registChannel(CHANNEL1);  

        try {
			cTools.consumeMessage();
			//System.out.println(cTools.flag);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       // myThread.start(); 
       // CometEngine ： 引擎，负责管理和维持连接，并能够必要的发送服务  
       engine = CometContext.getInstance().getEngine(); 
    }  
    
      
    
    
    public static void sendtoclient(String string){
 	   // 参数的意思：通过什么频道（CHANNEL1）发送什么数据（number1++），前台可用可用频道的值（result1）来获取某频道发送的数据  
        engine.sendToAll(CHANNEL1,string);  
        //System.out.println("推送:"+"可以的");
        //engine.sendToAll(CHANNEL2, number2++);  
    }




	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
    
     
}  