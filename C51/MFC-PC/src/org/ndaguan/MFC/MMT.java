package org.ndaguan.MFC;

import java.awt.Color;

public class MMT {
	public static final String menuName = "MultZIndexMeasure";
	public static final String SIMPLE_ACQ = "Snap/Live Window";
	public static final String tooltipDescription = "MultZIndexMeasure";
	public static final int TCPIPPort = 50501;
	public static String DEFAULT_TITLE = "Magnetic Tweezers Images Analyzer(SM4.IOP.CAS.CN)";

	public static  String magnetXYstage_ = "MP285 XY Stage";
	public static  String magnetZStage_ = "MP285 Z Stage";
	public static  boolean debug = true;
	     
	public static  String xyStage_ = ""; 
	public static  String zStage_ = "";
	
	public static boolean isAnalyzerBusy_ = false;
	public static boolean isCalibrationRunning_ = false;
	public static boolean isTestingRunning_ = false;
	public static boolean isGetXYPositionRunning_ = false;
	public static boolean isGetXYZPositionRunning_ = false;
	
	public static Object Acqlock = 0;
	public static int calibrateIndex_ = 0;
	public static int testingIndex_ = 0;
	public static int currentframeIndex_ = 0;
	public static double[][] Coefficients = null;
	public static String lastError_ = "No Error";
	private static long timeStart;	
	
	public static String[] CHARTLIST = new String[]{
		"V-sensor","V-ref","V-out","Chart-RT"
	};

	public static void logError(String string) 
	{
		 
		System.out.print(String.format("Error!!!\t%s\r\n",string));
	}
	public static void tik() 
	{
		timeStart = System.nanoTime();
	}
	public static void tok(String mouldeName ) 
	{
		System.out.print(String.format("\r\nMoulde��%s��:\tcostTime:\t%f ms\t\n", mouldeName,(System.nanoTime()-timeStart)/10e6));
	}
	public static void debugError(String string) 
	{
		System.out.print(String.format("Error!!!\t%s\r\n",string));
	}
	public static void debugMSG(double[][] arry) 
	{
		for (int i = 0; i < arry.length; i++) {
			for (int j = 0; j < arry[0].length; j++) {
				
				System.out.print(String.format("%.6f,",arry[i][j]));
			}
			System.out.print("\r\n");
		}
	}
	public static void debugMSG(double[]arry) 
	{
			for (int j = 0; j < arry.length; j++) {
				
				System.out.print(String.format("%.6f,",arry[j]));
			}
			System.out.print("\r\n");
	}
	
	public static void logMessage(String string) 
	{
		 
		System.out.print(String.format("Msg>>\t%s\r\n",string));
	}
	public static void debugMessage(String string) 
	{
		System.out.print(String.format("Msg>>%s\t\r\n",string));
	}
	
	public static int[] unEditAfterCalbration = new int[]{0,4,5,8};
	public static double currXP = 40;
	public static boolean isFeedbackRunning_ = false;
	protected static String currentUser = "n~daguan";
	public static boolean magnetCurrentStage= false;
	public static double magnetCurrentPosition = 0;
	public static double stageCurrentPosition = 0;
	public static int maxN=0;
	public static String AcqName = "Snap/Live Window";
	public static int calibrateSubIndex_ = 0;
	public static enum VariablesClassify{
		Feedback,
		DataSheet,
		Debug,
		Other;
	} 
	public static enum VariablesNUPD {
		//constructor format:	unit,	default value,	precision,	importance,	toolTip,	classify
		chartWidth("",8000,0,1,"chartWidth",VariablesClassify.DataSheet.name()),
		
		//advance
		saveFile(" ",1,0,0,"saveFile",VariablesClassify.Debug.name()),
		PWMValue("",0,0,0,"PWMValue after ok",VariablesClassify.Debug.name()),
		
		chartStatisWindow("",200,0,0,"auto range window",VariablesClassify.DataSheet.name()),
		PWMRate("",0.01,0.0001,0,"rate to show pwm",VariablesClassify.DataSheet.name()),
		SetPoint("",50,0,0,"set point",VariablesClassify.Feedback.name()),
		Kp("",-0.2,0.0001,0,"p",VariablesClassify.Feedback.name()),
		Ki("",0.01,0.0001,0,"i",VariablesClassify.Feedback.name()),
		Kd("",0.01,0.0001,0,"d",VariablesClassify.Feedback.name()),
		AutoRange("",1,0,0,"AutoRange",VariablesClassify.DataSheet.name()),
		PIDbyPC("",1,1,0,"0:show,1:byPC,2:byMCU",VariablesClassify.Feedback.name()),
		PIDMode("",1,1,0,"PIDMode:0:IncAutoPIDCalc,1:IncPIDCalc,2:LocPIDCalc",VariablesClassify.Feedback.name()),
		votageSmoothWindow("",1,0,0,"get votage smooth window",VariablesClassify.Debug.name()),
		Pause("",1,0,0,"stop show chart",VariablesClassify.Debug.name()),
		Tu("",0.01,0.0001,0,"Tu",VariablesClassify.Feedback.name()),
		TIM4Prescaler("",0.01,0.0001,0,"TIM4Prescaler",VariablesClassify.Debug.name()),
		PIDPeriod("",1000,0,0,"data acquire period",VariablesClassify.Feedback.name());
		private String unit;
		private double value;
		private double presicion;
		private  int important;
		private String toolTip;
		private String classify;
		VariablesNUPD(String u,double v,double p,int i,String t,String c) {
			unit = u;
			value = v;
			presicion = p;
			important = i;
			toolTip = t;
			classify = c;
			
		}
	 
		public double value() {
			return value;
		}
		public void value(double v) {
			value = v;
		}
		public String getUnit() {
			return unit;
		}
		public double getPresicion() {
			return presicion;
		}
		public String getToolTip(){
			return toolTip;
		}
		public String getClassify(){
			return classify;
		}
		public int getImp() {
			return important;
		}

		public int getTabIndex() {
			VariablesClassify[] classifyArray = VariablesClassify.values();
			int classifyLen = classifyArray.length;
			for(int i =0;i<classifyLen;i++){
				if(classify.equals(classifyArray[i].name()))
						return i;
			}
			return -1;
		}
		
	}
	public static void SetCurrentStagePosition(String stageZLabel_,
			double target) {
			if(stageZLabel_.equals(magnetZStage_))
				magnetCurrentPosition = target;
			else
				stageCurrentPosition = target;
	};
}
