package org.ndaguan.MFC;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author Administrator
 *
 */
public  class RoiItem {
	private static int counter = 0;
	private Color itemColor_;
	private int index_ = 0;

	private  boolean isPreference_  = false;
	private  boolean isFocus_ = true;

	private double x_ = 0 ;
	private double xPhy_ = 0 ;
	private double y_ = 0 ;
	private double yPhy_ = 0 ;
	private double zPhy_ = 0 ;
	private double fx_ = 0 ;
	private double fy_ = 0 ;

	private Writer dataFileWriter_;
	private double[][] calProfile_ = null;
	public ChartManager chart_ = null;

	private DescriptiveStatistics[] calcForceXYZStatis_;
	private DescriptiveStatistics[] showChartXYZStatis_;
	private DescriptiveStatistics[] feedbackXYZStatis_;
	private DescriptiveStatistics[] feedbackIntergStatis_;

	private double[] feedbackTarget_;//x y z position

	private double yPhy0_;
	private double xPhy0_;
	private double zPhy0_;
	private double l_;
	private boolean isBackground_;
	private double[] calProfileNorm;
	private double z_;
	private int Kp;
	private int Ki;
	private int Kd;

	public static RoiItem createInstance(double[] itemData) {
		return new RoiItem(itemData);
	}
	public boolean isFocus(){
		return isFocus_;
	}
	public void setFocus(boolean flag){
		isFocus_ = flag;
	}
	private RoiItem( double[] itemData) {
		index_ = counter;
		counter ++;
		isPreference_ = false;
		isBackground_ = false;
		setItemColor(Color.GREEN);
		if(itemData[2] == 1){
			isBackground_ = true;
			setItemColor(Color.BLUE);
		}
		chart_ = new ChartManager(MMT.CHARTLIST,(int) MMT.VariablesNUPD.chartWidth.value(),String.format("MFC_V0.1[%d]",index_));

		x_ = itemData[0];
		y_ = itemData[1];
		feedbackTarget_ = new double[3];

		int showChartWindowSize = (int) MMT.VariablesNUPD.chartStatisWindow.value();

		calcForceXYZStatis_ = new DescriptiveStatistics[3];//x,y,cross
		showChartXYZStatis_ = new DescriptiveStatistics[4];//z,x,y,l
		feedbackXYZStatis_ = new DescriptiveStatistics[3];//x,y,z (physic)
		feedbackIntergStatis_ = new DescriptiveStatistics[3];//x,y,z (physic)

	 
		for (int i = 0; i < showChartXYZStatis_.length; i++) {
			showChartXYZStatis_[i] = new DescriptiveStatistics(showChartWindowSize);
		}
		 

	}

	public void setCalcForceWidowSize(double size){
		for(DescriptiveStatistics stat: calcForceXYZStatis_)
			stat.setWindowSize((int)size);
	}
	public void setChartWidth(double size){
		chart_.setChartWidth((int)size);
	}
	public void setChartRangeWidowSize(double size){
		for(DescriptiveStatistics stat: showChartXYZStatis_)
			stat.setWindowSize((int)size);
	}
	public void setFeedbackWidowSize(double size){
		for(DescriptiveStatistics stat: feedbackXYZStatis_)
			stat.setWindowSize((int)size);
		for(DescriptiveStatistics stat: feedbackIntergStatis_)
			stat.setWindowSize((int)size);
	}
	public double[] getFeedbackTarget(){
		return feedbackTarget_;
	}

	public String getMsg(){
		return String.format("\\(%.2f, %.2f,%.2f)/",xPhy_,yPhy_,zPhy_);
	}

	public boolean isPreference() {
		return isPreference_;
	}
	public boolean isBackground() {
		return isBackground_;
	}

	public void setBackground(boolean flag){
		isBackground_ = flag;
		isPreference_ = false;
		if(flag){
			setItemColor(Color.BLUE);
		}
		else{
			setItemColor(Color.GREEN);
		}
	}
	public void setPreference(boolean flag){
		isPreference_ = flag;
		isBackground_ = false;;
		if(flag){
			setItemColor(Color.RED);
		}
		else{
			setItemColor(Color.GREEN);
		}
	}
	public Color getItemColor() {
		return itemColor_;
	}

	public void setItemColor(Color clr) {
		itemColor_ = clr;
	}

	public void dataClean(boolean flag){

		if (dataFileWriter_ != null) {
			try {
				dataFileWriter_.close();
			} catch (IOException e) {
				MMT.logError("DataWriter close false!"+e.toString());
			}
			dataFileWriter_ = null;
		}
		if(chart_ != null){
			for (int i = 0; i < MMT.CHARTLIST.length - 1; i++) {
				if(!flag && MMT.CHARTLIST[i].equals("Chart-Testing"))
					continue;
				chart_.getDataSeries().get(MMT.CHARTLIST[i]).clear();
			}
		}

		clearStaticData();

	}

	public void clearStaticData() {
		for (DescriptiveStatistics stat : calcForceXYZStatis_)
			stat.clear();
		for (DescriptiveStatistics stat : showChartXYZStatis_)
			stat.clear();
		for (DescriptiveStatistics stat : feedbackXYZStatis_)
			stat.clear();
		for (DescriptiveStatistics stat : feedbackIntergStatis_)
			stat.clear();
	}

	public boolean writeData(String acqName,long frameNum_,double elapsed, int rout, boolean b)  {
		if (dataFileWriter_ == null) {
			Calendar cal = new GregorianCalendar();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			File dir = null;
			String path;
			 
			 path=System.getProperty("user.home");
		 
			dir = new File(new File(path, "MTTracker"),
					dateFormat.format(cal.getTime()));
			if(!dir.isDirectory() && !dir.mkdirs()){
				dir = new File(new File(System.getProperty("user.home"),"MTTracker"),dateFormat.format(cal.getTime()));
				if(!dir.isFile())
					dir.mkdirs();
			}
			dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
			File file = new File(dir, dateFormat.format(cal.getTime()) + "_"
					+ acqName + "_bean_" + String.format("%d", index_) + "_" + ".txt");
			try {
			dataFileWriter_ = new BufferedWriter(new FileWriter(file));

				dataFileWriter_
				.write("Timestamp/ms, Frame, V-sensor,PWM,V-ref,V-out,Kp,Ki,Kd,CloseFlag,std\r\n");
				dataFileWriter_.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			try {
				dataFileWriter_
				.write(String.format("%f,%d,%f,%d,%f,%f,%d,%d,%d,%d,%d\r\n",elapsed,frameNum_,z_,rout,x_,y_,Kp,Ki,Kd,b?1:0,0));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return true;

	}
	public void writeData(String string, long counter2, float eclipes, int out, boolean b, long std) {
		// TODO Auto-generated method stub
		try {
			dataFileWriter_
			.write(String.format("%f,%d,%f,%d,%f,%f,%d,%d,%d,%d,%d\r\n",eclipes,counter2,z_,out,x_,y_,Kp,Ki,Kd,b?1:0,std));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
    public void flush(){
    	try {
			dataFileWriter_.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	private double[] getMean() {//z,x,y,l
		double pointNum = 0.01;
		double  temp[] = new double[3];
		for (int i = 0; i < 3; i++) {
			temp[i] = (showChartXYZStatis_[i].getMean()/pointNum)*pointNum;
		}
		return temp;
	}
	 
	public double[] getStandardDeviation() {//xyz
		double  temp[] = new double[3];
		for (int i = 0; i < 3; i++) {
			temp[i] = showChartXYZStatis_[i].getStandardDeviation();
		}
		return temp;
	}
	private double[] getDrawScale() {
		double min = 2;
		double[] std = getStandardDeviation();
		for(int i = 0;i<std.length;i++){
			std[i] = std[i]*2;
			std[i] = std[i]<min?min:std[i];
		}
		return std;
	}

	public void updateDataSeries(final float eclipes,double setVotage,final double PWM) {
		if(!chart_.isVisible())return;
		
		final double setPoint = MMT.getVotageToFlow(setVotage);
		final boolean update = true;
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				double data[] = getItemData();
				for(int i = 0;i<MMT.CHARTLIST.length;i++){
					try{
						String []temp = MMT.CHARTLIST[i].split(",");
						chart_.getDataSeries().get(temp[0]).add(eclipes,data[i],true);
						chart_.getDataSeries().get(temp[1]).add(eclipes,setPoint,true);
						chart_.getDataSeries().get(temp[2]).add(eclipes,PWM,true);
					}catch(Exception e){
						System.out.print(e.toString());
					}
				}

				if(update  && (MMT.VariablesNUPD.AutoRange.value() == 1)){
					double[] mean = getMean();
					double[] drawScale = getDrawScale();
					for(int i=0;i<MMT.CHARTLIST.length;i++){
						String []temp = MMT.CHARTLIST[i].split(",");
						chart_.getChartSeries().get(temp[0]).getXYPlot().getRangeAxis().setRange(mean[i] - drawScale[i],mean[i] + drawScale[i]);
					}
				}
			}

		});

	}
	public void updateDataSeries1(final boolean temp,final long setVotage,final long pwmValue) {
		if(!chart_.isVisible())return;
		

		final boolean update = true;
		final int selectedIndex = chart_.getSelectedTap();
		JFreeChart tem = chart_.getChartSeries().get(MMT.CHARTLIST[1]);
		int count = tem.getXYPlot().getDatasetCount();
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				double data[] = getItemData();
				//for(int i = 0;i<1;i++){
					try{
						if(temp)
							{
							chart_.getDataSeries().get("Chart-RT").add(pwmValue,data[0],true);							
							}else{
							chart_.getDataSeries().get("Chart-RT"+"1").add(pwmValue,data[0],true);								 
							}
									
						
						
					}catch(Exception e){
						System.out.print(e.toString());
					}
				//}

				if(update  && (MMT.VariablesNUPD.AutoRange.value() == 1)){
					double[] mean = getMean();
					double[] drawScale = getDrawScale();
					for(int i=0;i<1;i++){
						chart_.getChartSeries().get(MMT.CHARTLIST[i]).getXYPlot().getRangeAxis().setRange(mean[i] - drawScale[i],mean[i] + drawScale[i]);
					}
				}
			}

		});

	}
	private double[] getItemData(){
		return new double[]{z_,x_,y_};
	}
	public double[] getXY() {
		return new double[]{x_,y_};
	}
	public double getZ() {
		return zPhy_;
	}
 
	public void setXYZ(double zPos, double xPos, double yPos) {//moving ROI
		double votage = zPos;
		double flow = MMT.getVotageToFlow(zPos); 
		x_ = xPos;
		y_ = yPos;
		z_ = flow;
		String temp = ChartManager.getInstance().chart.getTitle().getText();
		String[] temp1 = temp.split("-");
		ChartManager.getInstance().chart.setTitle(temp1[0]+String.format("-GetFlow:%.1f", flow));
		showChartXYZStatis_[0].addValue(flow);
		showChartXYZStatis_[1].addValue(xPos);
		showChartXYZStatis_[2].addValue(yPos);
		
	}
	 
    
	 
	public void setChartVisible(boolean flag) {
		chart_.setVisible(flag);
	}
	public void addControlPanel(JPanel mainpanel,int height) {
		chart_.addControlPanel(mainpanel,height);
	}
	public void addChartData(String string, double x, double y,boolean flag) {
		XYSeries dataSeries = chart_.getDataSeries().get(string);
		if(dataSeries != null)
			dataSeries.add(x,y,flag);		
	}
	public void clearChart(String string) {
		final XYSeries dataSeries = chart_.getDataSeries().get(string);
		if(dataSeries != null){
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					dataSeries.clear();	
				}});
		}

	}
 
	public void setForce(double[] force) {
		fx_ = force[0];
		fy_ = force[1];
	}
	public double[] getXYZPhy() {
		return new double[]{xPhy_,yPhy_,zPhy_};
	}

	public String getName() {
		return String.format("%d      %d",index_,index_);
	}
 
	public void InitializeCalProflie(int r,int c) {
		calProfile_ = new double[r][c];
		setCalProfileNorm(new double[r]);
	}
	public void clearCalProfile() {
		calProfile_ = null;		
	}
	public double[][] getCalProfile() {
		return calProfile_;
	}
	public void setSelectTap(String string) {
		int i=0;
		for(String s:MMT.CHARTLIST){
			if(s.equals(string)){
				chart_.setSelectTap(i);
				break;
			}
			else{
				i++;
			}
		}
	}
	public void setZOrign(double z) {
		zPhy0_ = z;
	}
	public double[] getFeedbackIntegrate() {
		double[] integrate = new double[3];
		for(int i= 0;i<3;i++)
			integrate[i] = feedbackIntergStatis_[i].getMean();
		return integrate;
	}
	public double[] getFeedbackmeanPos() {
		double[] meanPos = new double[3];
		for(int i= 0;i<3;i++)
			meanPos[i] = feedbackXYZStatis_[i].getMean();
		return  meanPos;
	}
	public void clearFeedbackData() {
		for(int i= 0;i<3;i++){
			feedbackXYZStatis_[i].clear();		
			feedbackIntergStatis_[i].clear();		
		}
	}
	public void saveTestingChart() throws IOException {
		Calendar cal = new GregorianCalendar();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		File dir = new File(new File("C:\\", "MTTracker"),
				dateFormat.format(cal.getTime()));
		dir.mkdirs();
		dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

		String path = dir.getAbsolutePath() +"/"+ dateFormat.format(cal.getTime()) + String.format("_TestingResult_%d_", index_)+".png";
		FileOutputStream out = new FileOutputStream(path );

		ChartUtilities.writeChartAsPNG(out, chart_.getChartSeries().get("Chart-Testing") , 640, 480);
		System.out.print("Save chart");
		System.out.print(path);
		out.flush();
		out.close();
	}
	public void updateCalProfilesChart() {
		// TODO Auto-generated method stub  
		JFreeChart cChart = chart_.getChartSeries().get("Chart-Cal-Pos");
		XYSeries seriesMax = new XYSeries("");
		if(cChart != null){
			XYPlot plot = cChart.getXYPlot();
			XYSeriesCollection dataset = new XYSeriesCollection(); 
			for (int i = 0; i < calProfile_.length; i++) {
				double  maxValue = 0;
				XYSeries series = new XYSeries("");
				for (int j = 0; j < calProfile_[i].length; j++) {
					double v = calProfile_[i][j];
					series.add(j,v);
					if(maxValue  <v)
						maxValue  = v;
				}	
				 
				dataset.addSeries(series);
			}
			plot.setDataset(2, dataset); 
		}
	}
	public void setCalProfile(double[][] cp) {
		calProfile_ = cp;		
	}
	public double[] getCalProfileNorm() {
		return calProfileNorm;
	}
	public void setCalProfileNorm(double[] calProfileNorm) {
		this.calProfileNorm = calProfileNorm;
	}
	public void setPID(int p, int i, int d) {
		// TODO Auto-generated method stub
		Kp = p;
		Ki = i;
		Kd = d;
	}
	
 

}
