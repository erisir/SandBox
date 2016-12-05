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

		Error = (int) (MMT.VariablesNUPD.Setvotage.value() - NextPoint);       // ƫ��E(t) 
		SumError = PrevError+LastError+Error; 	                // ����
		dError=Error - LastError;             // ��ǰ΢��
		PrevError = LastError; 
		LastError = Error; 

		if(Math.abs(Error)< deadZone ){
			SumError = 0;
			return 0;
		}

		return (int) ( 
				MMT.VariablesNUPD.pTerm_x.value()/1000 * Error        //���� 
				+ MMT.VariablesNUPD.iTerm_x.value()/1000 * SumError     //������ 
				+ MMT.VariablesNUPD.dTerm_x.value()/1000 * dError	  // ΢����	 ����ɲ��
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
	public  static void main(String[] args) {

		List<RoiItem> rt = Collections.synchronizedList(new ArrayList<RoiItem>());
		rt.add(RoiItem.createInstance(new double[]{130,130,0})); 
		Kernel kr = new Kernel();

		rt.get(0).setChartVisible(true);
		/*kr.parseXML(rt,"D:\\tracklog\\20161117.gpx");
      	if(true)return;*/

		CommTool comm = new CommTool(kr,rt);
		PreferDailog preDla = new PreferDailog(kr,rt,comm);
		preDla.setVisible(true);
		int ind = 0;
		double poss = 0;
		long start_time = System.nanoTime();
		boolean pwmBack = false;
		int counter = 0;
		int delayTime = 50;
		int delta = 0;
		int dvalue = 0;
		int last = 0;
		int value = 1;
		int tmm = 1000000;
		comm.OpenTunel();

		while(ind <9999999){
			try {
				poss =  comm.getPosition();

				//System.out.print("\r\nVsensor,Vref,Vout=["+poss[0]+"]"+"["+poss[1]+"]"+"["+poss[2]+"]");
				rt.get(0).setXYZ(poss,0, 0);

				TimeUnit.MILLISECONDS.sleep(2);
				float eclipes= (float) ((System.nanoTime()-start_time)/10e6);   
				rt.get(0).writeData("MFC",ind,eclipes);
				//System.out.print(String.format("value:\t%d\tind:\t%d\r\n",value,ind));
				if((ind>=tmm) && (ind%tmm == 0)){
					value += 20;
					comm.SetVotageTimes(value);

				}
				if(MMT.VariablesNUPD.PIDbyPC.value() == 1){

					//delta = PIDCalc((long) poss);//5;
					delta = 5;
					//rout += delta;
					if(!pwmBack)
					{
						rout += delta; 
					}
					else{
						rout -= delta; 
					}			
					if(rout>=5000){
						rout = 5000;
						pwmBack = true;
					}

					if(rout< 10) 
					{
						rout = 10;
						pwmBack = false;
					}					 						 
					comm.SetPWM(rout) ;
					System.out.print(String.format("get::%f\tset:%.1f\tdelta:%d\trout:%d",poss,MMT.VariablesNUPD.Setvotage.value(), delta,rout));
					System.out.print("\r\n");
					TimeUnit.MILLISECONDS.sleep(10);
				}else{
					double[] temp = comm.getPIDStatue();
					MMT.VariablesNUPD.Setvotage.value(temp[0]);
					rout = (int) temp[1];
				}

				//counter++;
				if(MMT.VariablesNUPD.Pause.value() >0){
					rt.get(0).updateDataSeries1(pwmBack,(long) MMT.VariablesNUPD.Setvotage.value(),rout);//磁滞回线
					rt.get(0).updateDataSeries(eclipes,(long) MMT.VariablesNUPD.Setvotage.value(),(int)(rout*MMT.VariablesNUPD.MMTrout.value() ));
				}
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
