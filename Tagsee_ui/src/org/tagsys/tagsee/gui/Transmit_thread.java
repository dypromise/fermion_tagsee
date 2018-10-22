package org.tagsys.tagsee.gui;

public class Transmit_thread extends Thread {
	
	
	public boolean Flag = true; 

	@Override
	public void run() {
		while(Flag){
			String str  = doubles2str(MainController.values);
			try {
				MainController.producer.sendMessage(str);
				Thread.sleep(200);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	public String doubles2str(double[] a){
		StringBuilder stringBuffer = new StringBuilder();
		for(int i = 0;i<a.length-1;i++){
			stringBuffer.append(String.format("%.3f",a[i])+",");
		}
		stringBuffer.append(Double.toString(a[a.length-1]));
		return stringBuffer.toString();
	}

	
}
