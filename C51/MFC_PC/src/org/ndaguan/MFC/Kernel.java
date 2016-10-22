package org.ndaguan.MFC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Kernel {
    
	static Kernel getInstance(){
		return new Kernel();
	}
	private static long SumError= 0;
	private static int PrevError = 0;
	private static int LastError= 0;
	private static int deadZone = 100;
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

		
		
		MMT.VariablesNUPD.frameToRefreshChart.value(1);		 
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
	    while(ind <999999){
			try {
				poss = (int) comm.getPosition();
				TimeUnit.MILLISECONDS.sleep(2);
				float eclipes= (float) ((System.nanoTime()-start_time)/10e6);   
				
				rt.get(0).writeData("MFC",ind,eclipes);
 
				if(MMT.VariablesNUPD.PIDbyPC.value() == 1){
 
					  delta = PIDCalc(poss);//5;
					if(!pwmBack)
						{
							rout += delta; 
						}
					else{
						rout -= delta; 
					}
				
						 if(rout>250){
							 rout = 250;
							// pwmBack = true;
						 }

						 if(rout< 5) 
						{
							 rout = 5;
							 pwmBack = false;
						}					 						 
						 comm.SetPWM(rout) ;
						 System.out.print(String.format("get::%d\tset:%.1f\tdelta:%d\trout:%d\ttime:%.1f\tcounter:%d",poss,MMT.VariablesNUPD.Setvotage.value(), delta,rout,(System.nanoTime()-start_time)/10e6,counter));
						 System.out.print("\r\n");
						 TimeUnit.MILLISECONDS.sleep(200);
				}
				
				//counter++;
				rt.get(0).updateDataSeries1(pwmBack,(long) MMT.VariablesNUPD.Setvotage.value(),rout);
				rt.get(0).updateDataSeries(eclipes,(long) MMT.VariablesNUPD.Setvotage.value(),rout*20);
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
