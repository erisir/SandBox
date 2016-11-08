package org.ndaguan.MFC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Kernel {
    
	static Kernel getInstance(){
		return new Kernel();
	}
	private static long SumError= 0;
	private static int PrevError = 0;
	private static int LastError= 0;
	private static int deadZone = 5;
	private static int maxMoveStep = 100;
	public static int rout = 0;
    
	static int PIDCalc( long NextPoint ) 
	{ 
	  int Error,dError;
	   
	  Error = (int) (MMT.VariablesNUPD.Setvotage.value() - NextPoint);       // 偏差E(t) 
	  SumError = PrevError+LastError+Error; 	                // 积分
	  dError=Error - LastError;             // 当前微分
	  PrevError = LastError; 
	  LastError = Error; 

	  if(Math.abs(Error)< deadZone ){
	  SumError = 0;
	  return 0;
	  }

	  return (int) ( 
	            MMT.VariablesNUPD.pTerm_x.value() * Error        //比例 
	            + MMT.VariablesNUPD.iTerm_x.value() * SumError     //积分项 
	            + MMT.VariablesNUPD.dTerm_x.value() * dError	  // 微分项	 负责刹车
				);  
	} 
	public  static void main(String[] args) {
		
		String body = "【北京市预约挂号统一平台】ffdsfa发发大幅度【445566】";              //这里我是要获取自己短信服务号码中的验证码~~              
		Pattern pattern = Pattern.compile("北京市预约挂号统一平台");  
		Pattern pattern1 = Pattern.compile("[0-9]{6}");  
		Matcher matcher = pattern.matcher(body);
		Matcher matcher1 = pattern1.matcher(body);
		if(matcher.find()){
		if (matcher1.find()) {                  
			String res = matcher1.group();//.substring(1, 11);                  
			System.out.print(res);           
		} }        
	 if(1==1)return;
	 
		List<RoiItem> rt = Collections.synchronizedList(new ArrayList<RoiItem>());
		rt.add(RoiItem.createInstance(new double[]{130,130,0})); 
		Kernel kr = new Kernel();
        CommTool comm = new CommTool(kr,rt);
        PreferDailog preDla = new PreferDailog(kr,rt,comm);
        preDla.setVisible(true);
		rt.get(0).setChartVisible(true);
		int ind = 0;
	    int poss = 0;
	    long start_time = System.nanoTime();
	    boolean pwmBack = false;
	    int counter = 0;
	    int delayTime = 50;
	    int delta = 0;
	    int dvalue = 0;
	    int last = 0;
      	int value = 1;
      	int tmm = 1000;
      	comm.OpenTunel();
	    while(ind <9999999){
			try {
				poss = (int) comm.getPosition();
				TimeUnit.MILLISECONDS.sleep(2);
				float eclipes= (float) ((System.nanoTime()-start_time)/10e6);   
				rt.get(0).writeData("MFC",ind,eclipes);
				System.out.print(String.format("value:\t%d\tind:\t%d\r\n",value,ind));
				rt.get(0).setXY(0, value);
                if((ind>=tmm) && (ind%tmm == 0)){
                	value += 20;
					comm.SetVotageTimes(value);
					
                }
				if(MMT.VariablesNUPD.PIDbyPC.value() == 1){
 
					delta = PIDCalc(poss);//5;
					rout += delta;
					/*if(!pwmBack)
						{
							rout += delta; 
						}
					else{
						rout -= delta; 
					}*/
				
						 if(rout>19000){
							 rout = 19000;
							pwmBack = true;
						 }

						 if(rout< 35) 
						{
							 rout = 35;
							 pwmBack = false;
						}					 						 
						 comm.SetPWM(rout) ;
						 System.out.print(String.format("get::%d\tset:%.1f\tdelta:%d\trout:%d",poss,MMT.VariablesNUPD.Setvotage.value(), delta,rout));
						 System.out.print("\r\n");
						 TimeUnit.MILLISECONDS.sleep(10);
				}else{
					  double[] temp = comm.getPIDStatue();
					  MMT.VariablesNUPD.Setvotage.value(temp[0]);
					  rout = (int) temp[1];
				}
				
				//counter++;
				rt.get(0).updateDataSeries1(pwmBack,(long) MMT.VariablesNUPD.Setvotage.value(),rout);
				rt.get(0).updateDataSeries(eclipes,(long) MMT.VariablesNUPD.Setvotage.value(),(int)(rout*MMT.VariablesNUPD.MMTrout.value() ));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ind++;
			
		}			 

	}
 
}
