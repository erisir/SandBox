package org.ndaguan.MFC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

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

		Error = (int) (MMT.VariablesNUPD.SetPoint.value() - NextPoint);       // ƫ��E(t) 
		SumError = PrevError+LastError+Error; 	                // ����
		dError=Error - LastError;             // ��ǰ΢��
		PrevError = LastError; 
		LastError = Error; 

		if(Math.abs(Error)< deadZone ){
			SumError = 0;
			return 0;
		}

		return (int) ( 
				MMT.VariablesNUPD.Kp.value()/1000 * Error        //���� 
				+ MMT.VariablesNUPD.Ki.value()/1000 * SumError     //������ 
				+ MMT.VariablesNUPD.Kd.value()/1000 * dError	  // ΢����	 ����ɲ��
				);  
	}

	private void parseXML(List<RoiItem> rt, String path){
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = builder.build(path);
			List trkpt =    document.getRootElement().getChildren().get(1).getChildren().get(4).getChildren();
			for (int j = 0; j < trkpt.size(); j++) {
				double lat = Double.valueOf((((Element) trkpt.get(j)).getAttributeValue("lat"))).doubleValue();
				double lon = Double.valueOf((((Element) trkpt.get(j)).getAttributeValue("lon"))).doubleValue();
				double att = Double.valueOf(((Element) trkpt.get(j)).getChildren().get(0).getValue());
				String time =((Element) trkpt.get(j)).getChildren().get(1).getValue();
				String[] temp = time.substring(11,19).split(":");
				double dtime =  Double.valueOf(temp[0]).doubleValue()*3600+Double.valueOf(temp[1]).doubleValue()*60+Double.valueOf(temp[2]).doubleValue();

				System.out.print(j);
				System.out.print("\t");
				System.out.print(time.substring(11,19)+"\r\n");
				//System.out.print(String.format("lat:\t%f\tlon:\t%f\t%f\ttime:\t%s\t\n", lat,lon,att,time));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void getPID(String mode,/*1:p,3:pi,3:pid*/int Kc,double Tc,double T){
		double kp=0,ki=0,kd=0;
		double Ti=0,Td=0;
		switch(mode){
		case "P":
			kp = 0.5*Kc;
			break;
		case "PI":
			kp = 0.45*Kc;
			Ti = 0.85*Tc;
			break;
		case "PD":
			kp = 0.65*Kc;
			Td = 0.12*Tc;
			break;
		case "PID":
			kp = 0.6*Kc;
			Ti = 0.5*Tc;
			Td = 0.12*Tc;
			break;
		}
		kp = kp*(1+T/Ti+Td/T);
		ki = kp*(1+2*Td/T);
		kd = kp*Td/T;
		System.out.print(String.format("P:%.4f,I:%.4f,D:%.4f", kp,ki,kd));
	}
	public static void getLocPID(String mode,/*1:p,3:pi,3:pid*/int Kc,double Tc,double T){
		double kp=0,ki=0,kd=0;
		double Ti=0,Td=0;
		switch(mode){
		case "P":
			kp = 0.5*Kc;
			break;
		case "PI":
			kp = 0.45*Kc;
			Ti = 0.85*Tc;
			break;
		case "PD":
			kp = 0.65*Kc;
			Td = 0.12*Tc;
			break;
		case "PID":
			kp = 0.6*Kc;
			Ti = 0.5*Tc;
			Td = 0.12*Tc;
			break;
		}
		ki = kp*T/Ti;
		kd = kp*Td/T;
		System.out.print(String.format("P:%.4f,I:%.4f,D:%.4f", kp,ki,kd));
	}
	private double closeTimeout = 5;//s
	private double closeTimeout0 = 1;//s
	private boolean pwmBack;
	public void autoFixPID(PreferDailog preDla,CommTool comm,int setPoint,RoiItem roi){
		int Kp[]= new int[]{1500,4000};
		int T[]= new int[]{80,150};
		comm.setPIDMode(0);
		MMT.VariablesNUPD.SetPoint.value(setPoint);
		long start_time = System.nanoTime();
		long counter = 0;

		for (int t = T[0]; t < T[1]; t+=10) {
			comm.setPIDPeriod(t);
			Sleep(10);
			for (int p = Kp[0]; p < Kp[1]; p+=100) {

				System.out.print(String.format("\r\nSeting:\tT:%d,kp:%d,", t,p));
				counter++;
				roi.setPID(p,t,0);//save p t
				comm.setPTerm(p);
				Sleep(10);
				comm.CloseTunel();
				System.out.print("\r\nClose");
				double pos =   comm.getPosition();//temp[8];					
				boolean timeOut= false;
				long timeStart = System.nanoTime();
				while(!timeOut){
					timeOut = (System.nanoTime()-timeStart)/10e9 >closeTimeout0 ;
					try {
						showAndSave(roi,comm,start_time,counter,true,null);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.print(String.format("\r\n[CloseTunel]Waitfor timeout[%b]\r\n", timeOut));
				}
				comm.PIDTunel();
				System.out.print("\r\nPIDTunel");
				timeOut= false;
				timeStart = System.nanoTime();
				long std[] = new long[]{0};
				while(!timeOut){		
					try {
						showAndSave(roi,comm,start_time,counter,false,std);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut = (System.nanoTime()-timeStart)/10e9 >closeTimeout ;
					System.out.print(String.format("\r\n[PID]Waitfor timeout[%b]\r\n", timeOut));
				}
				updateStd((long) Math.sqrt(std[0]),counter,comm,start_time,roi);
			}
		}
	}
	public void autoFixPIDANDTime(PreferDailog preDla,CommTool comm,int setPoint,RoiItem roi){
		int Kp[]= new int[]{3000,3001};
		int T[]= new int[]{1,99};
		//comm.setPIDMode(0);
		MMT.VariablesNUPD.SetPoint.value(setPoint);
		long start_time = System.nanoTime();
		long counter = 0;

		for (int p = Kp[0]; p < Kp[1]; p+=100) {
			//comm.setPTerm(p);
			comm.CloseTunel();
			System.out.print("\r\nClose");
			double pos =   comm.getPosition();//temp[8];					
			boolean timeOut= false;
			long timeStart = System.nanoTime();
			while(!timeOut){
				timeOut = (System.nanoTime()-timeStart)/10e9 >closeTimeout0 ;
				try {
					showAndSave(roi,comm,start_time,counter,true,null);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.print(String.format("\r\n[CloseTunel]Waitfor timeout[%b]\r\n", timeOut));
			}
			comm.PIDTunel();
			System.out.print("\r\nPIDTunel");
			Sleep(10);
			for (int t = T[0]; t < T[1]; t+=1){

				System.out.print(String.format("\r\nSeting:\tT:%d,kp:%d,", t,p));
				counter++;
				roi.setPID(p,t,0);//save p t
				comm.setTIM4Prescaler(t);
				Sleep(10);

				timeOut= false;
				timeStart = System.nanoTime();
				long std[] = new long[]{0};
				while(!timeOut){		
					try {
						showAndSave(roi,comm,start_time,counter,false,std);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut = (System.nanoTime()-timeStart)/10e9 >closeTimeout ;
					System.out.print(String.format("\r\n[PID]Waitfor timeout[%b]\r\n", timeOut));
				}
				updateStd((long) Math.sqrt(std[0]),counter,comm,start_time,roi);
			}
		}
	}
	public void autoFixPIDMode1(PreferDailog preDla,CommTool comm,int setPoint,RoiItem roi){
		int Kp[]= new int[]{196,700};
		int Ki[]= new int[]{355,700};
		int Kd[]= new int[]{435,700};
		MMT.VariablesNUPD.SetPoint.value(setPoint);
		long start_time = System.nanoTime();
		long counter = 0;

		for (int p = Kp[0]; p < Kp[1]; p+=50) {
			for (int i = Ki[0]; i < Ki[1]; i+=50) {
				for (int d = Kd[0]; d < Kd[1]; d+=50) {
					System.out.print(String.format("\r\nSeting:\tKp:%d,Ki:%d,Kd:%d", p,i,d));
					//MMT.VariablesNUPD.Kp.value(p);
					//MMT.VariablesNUPD.Ki.value(i);
					//MMT.VariablesNUPD.Kd.value(d);
					counter++;
					roi.setPID(p,i,d);
					comm.setPTerm(p);
					Sleep(10);
					comm.setITerm(i);
					Sleep(10);
					comm.setDTerm(d);
					Sleep(10);
					comm.CloseTunel();
					System.out.print("\r\nClose");
					double pos =   comm.getPosition();//temp[8];					
					boolean timeOut= false;
					long timeStart = System.nanoTime();
					while(!timeOut){
						timeOut = (System.nanoTime()-timeStart)/10e9 >closeTimeout ;
						try {
							showAndSave(roi,comm,start_time,counter,true,null);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.print(String.format("\r\n[CloseTunel]Waitfor timeout[%b]\r\n", timeOut));
					}
					comm.PIDTunel();
					System.out.print("\r\nPIDTunel");
					timeOut= false;
					timeStart = System.nanoTime();
					long std[] = new long[]{0};
					while(!timeOut){		
						try {
							showAndSave(roi,comm,start_time,counter,false,std);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						timeOut = (System.nanoTime()-timeStart)/10e9 >closeTimeout ;
						System.out.print(String.format("\r\n[PID]Waitfor timeout[%b]\r\n", timeOut));
					}
					updateStd((long) Math.sqrt(std[0]),counter,comm,start_time,roi);
				}
			}
		}
	}
	private void updateStd(long std,long counter,CommTool comm, long start_time,RoiItem roi) {
		// TODO Auto-generated method stub
		double[] temp = comm.getPIDStatue();
		double pos = temp[8];
		int out = (int) temp[5];
		float eclipes= (float) ((System.nanoTime()-start_time)/10e6); 
		roi.writeData("MFC",counter,eclipes,out,false,std);
	}

	private void showAndSave(RoiItem roi, CommTool comm, long start_time, long counter, boolean b,long std[]) throws InterruptedException {
		// TODO Auto-generated method stub
		float eclipes= (float) ((System.nanoTime()-start_time)/10e6); 
		int out = 0;
		double  SetPoint = MMT.VariablesNUPD.SetPoint.value();
		double pos = 0;
		if(b){
			pos =   comm.getPosition();
			out = 32;
		}else{
			double[] temp = comm.getPIDStatue();
			pos = temp[8];
			out = (int) temp[5];
			double delta = pos - SetPoint;
			std[0] += (delta*delta);
		}
		roi.setXYZ(pos,0, 0);
		roi.writeData("MFC",counter,eclipes,out,b);
		if(counter%10 == 0)
			roi.flush();
		roi.updateDataSeries(eclipes, SetPoint,(int)(out*MMT.VariablesNUPD.PWMRate.value() ));

		TimeUnit.MILLISECONDS.sleep(10);

	}
	public void showChart(RoiItem roi, CommTool comm, long start_time) {
		// TODO Auto-generated method stub
		float eclipes= (float) ((System.nanoTime()-start_time)/10e6); 
		double pos = comm.getPosition();
		roi.setXYZ(pos,0, 0);
		roi.updateDataSeries(eclipes, 600,(int)(rout*MMT.VariablesNUPD.PWMRate.value() ));
	}
	private boolean isStable(RoiItem roi) {
		// TODO Auto-generated method stub
		double std = roi.getStandardDeviation()[0];
		return std<20 && std>0.001;
	}
	private void Sleep(int i) {
		// TODO Auto-generated method stub
		try {
			TimeUnit.MILLISECONDS.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void PidByPC(CommTool comm,RoiItem rt,long start_time){
		if(MMT.VariablesNUPD.WorkMode.value() == 1){
			double pos = comm.getPosition();
			float eclipes= (float) ((System.nanoTime()-start_time)/10e6);
			rout += PIDCalc((long) pos);
			if(rout <10)rout=10;
			if(rout>5000)rout=5000;
			comm.SetPWM(rout) ;		
			rt.setXYZ(pos,0, 0);
			rt.updateDataSeries(eclipes,MMT.VariablesNUPD.SetPoint.value(),(int)(rout*MMT.VariablesNUPD.PWMRate.value() ));
		} 
	}
	public void PWMVSVOL(CommTool comm,RoiItem rt){

		int temp = 0;
		if(pwmBack){
			rout -=10;
			temp = rout +5;

		}
		else{
			rout +=10;
			temp = rout -5;
		}
		if(rout <2500){
			rout=2500;
			pwmBack = false;
		}
		if(rout>4500){
			rout=4500;
			pwmBack = true;
		}
		comm.SetPWM(temp) ;		
		Sleep(10);
		double pos = comm.getPosition();
		rt.setXYZ(pos,0, 0);
		rt.updateDataSeries1(!pwmBack,(long) MMT.VariablesNUPD.SetPoint.value(), temp);
		comm.SetPWM(rout) ;	
		Sleep(10);
		pos = comm.getPosition();
		rt.setXYZ(pos,0, 0);
		rt.updateDataSeries1(pwmBack,(long) MMT.VariablesNUPD.SetPoint.value(), rout);
	}

	public  static void main(String[] args) {
		List<RoiItem> rt = Collections.synchronizedList(new ArrayList<RoiItem>());
		rt.add(RoiItem.createInstance(new double[]{130,130,0})); 
		Kernel kr = new Kernel();
		rt.get(0).setChartVisible(true);
		CommTool comm = new CommTool(kr,rt);
		int height = 270;
		PreferDailog preDla = new PreferDailog(kr,rt,comm,height);
		rt.get(0).addControlPanel(preDla.mainPanel,height);
		comm.OpenTunel();
		kr.pwmBack = false;
		kr.rout = 10;
		long start_time = System.nanoTime();
		comm.OpenTunel();
		long counter = 0;
		long std[] = new long[]{0};
		//kr.autoFixPIDANDTime( preDla, comm, 2800, rt.get(0));
		
		while(true){
			if(MMT.VariablesNUPD.Pause.value() ==1){
				try {
					if(MMT.VariablesNUPD.WorkMode.value() ==0)
						kr.showChart( rt.get(0), comm, start_time);								
					if(MMT.VariablesNUPD.WorkMode.value() ==1)//get position
						kr.showAndSave(rt.get(0), comm, start_time, counter++, true, std);
					if(MMT.VariablesNUPD.WorkMode.value() ==2)//get pid statu
						kr.showAndSave(rt.get(0), comm, start_time, counter++, false, std);
					if(MMT.VariablesNUPD.WorkMode.value() ==3)
						kr.PWMVSVOL( comm, rt.get(0));
					if(MMT.VariablesNUPD.WorkMode.value() ==4)
						kr.PidByPC(comm, rt.get(0), start_time);	
					TimeUnit.MILLISECONDS.sleep(10);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}			 

	}

}
