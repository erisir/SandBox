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

import javax.swing.SwingUtilities;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
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
		chart_ = new ChartManager(MMT.CHARTLIST,(int) MMT.VariablesNUPD.chartWidth.value(),String.format("Bean-----%d",index_));

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

	public boolean writeData(String acqName,long frameNum_,double elapsed) throws IOException{
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
			dataFileWriter_ = new BufferedWriter(new FileWriter(file));

			dataFileWriter_
			.write("Timestamp/ms, Frame, XPos/pixel,XPos/um, YPos/pixel, YPos/um, ZPos/um,StageZ/um,MagnetZ/um,ForceX/pN,ForceY/pN\r\n");/*,Std(x/y),skrewnessy*/
			dataFileWriter_.flush();
		}
		else{
			dataFileWriter_
			.write(String.format("%f,%d,%f,%f,%f,%f,%f,%f,%f,%f\r\n",elapsed,frameNum_,x_,xPhy_,y_,yPhy_,zPhy_,MMT.stageCurrentPosition,MMT.magnetCurrentPosition,fx_,fy_/*,stdXdY_,skrewness_*/));
		}
		return true;

	}


	private double[] getMean() {//z,x,y,l
		double pointNum = 0.01;
		return new double[]{((int)(showChartXYZStatis_[2].getMean()/pointNum))*pointNum,((int)(showChartXYZStatis_[0].getMean()/pointNum))*pointNum,((int)(showChartXYZStatis_[1].getMean()/pointNum))*pointNum,((int)(showChartXYZStatis_[3].getMean()/pointNum))*pointNum};
	}
	private double[] getXYMean() {
		return new double[]{showChartXYZStatis_[0].getMean(),showChartXYZStatis_[1].getMean()};
	}
	private double[] getStandardDeviation() {//z,x,y,l
		return new double[]{showChartXYZStatis_[2].getStandardDeviation(),showChartXYZStatis_[0].getStandardDeviation(),showChartXYZStatis_[1].getStandardDeviation(),showChartXYZStatis_[3].getStandardDeviation()};
	}
	private double[] getDrawScale() {
		double min = 50;
		double[] std = getStandardDeviation();
		for(int i = 0;i<std.length;i++){
			std[i] = std[i]*6;
		}
		return new double[]{(std[0]<min)?min:std[0],std[1]<min?min:std[1],std[2]<min?min:std[2],std[3]<min?min:std[3]};
	}

	public void updateDataSeries(final float eclipes,final double lon,final double lat) {
		if(!chart_.isVisible())return;
		

		final boolean update = true;
		final int selectedIndex = chart_.getSelectedTap();
		 
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				double data[] = getItemData();
				//for(int i = 0;i<1;i++){
					try{
						//chart_.getDataSeries().get(MMT.CHARTLIST[0]).add(eclipes,data[0],true);
						chart_.getDataSeries().get(MMT.CHARTLIST[0]+"SetVotage").add(eclipes,lon,update&&(0 == selectedIndex));
						//chart_.getDataSeries().get(MMT.CHARTLIST[0]+"PWMValue").add(eclipes, lat,update&&(0 == selectedIndex));
							
						
						
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
	public void updateDataSeries1(final boolean temp,final long setVotage,final long pwmValue) {
		if(!chart_.isVisible())return;
		

		final boolean update = true;
		final int selectedIndex = chart_.getSelectedTap();
		JFreeChart tem = chart_.getChartSeries().get(MMT.CHARTLIST[1]);
		int count = tem.getXYPlot().getDatasetCount();
		MMT.logMessage(String.format("count:%d\r\n", count));
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				double data[] = getItemData();
				//for(int i = 0;i<1;i++){
					try{
						if(temp)
							{
							chart_.getDataSeries().get(MMT.CHARTLIST[1]).add(pwmValue,data[0],true);							
							}else{
							chart_.getDataSeries().get(MMT.CHARTLIST[1]+"SetVotage").add(pwmValue,data[0],true);								 
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
		return new double[]{zPhy_};
	}
	public double[] getXY() {
		return new double[]{x_,y_};
	}
	public double getZ() {
		return zPhy_;
	}
	public void setXY(double xPos, double yPos) {//moving ROI
		x_ = xPos;
		y_ = yPos;
	}
	public void setXY(double[] pos){
		if(isBackground_)
			return;
		//pixel
		x_ = pos[0];
		y_ = pos[1];
		//uM
		//nM:calculate Force with a bigger windowSize
		calcForceXYZStatis_[0].addValue(xPhy_*1e3);
		calcForceXYZStatis_[1].addValue(yPhy_*1e3);
		calcForceXYZStatis_[2].addValue(xPhy_ * yPhy_*1e6);
		//uM:get mean&standardDeviation  to update chart with a smaller windowSize;
		showChartXYZStatis_[0].addValue(xPhy_);
		showChartXYZStatis_[1].addValue(yPhy_);
		//uM:get mean&sum of the history data
		 
	}
	 
	public void setZ(double zpos) {
		zPhy_ = zpos;
		showChartXYZStatis_[2].addValue(zPhy_);
		 
	}
	public void setL() {
		double deltax = ( xPhy_ -xPhy0_);
		double deltay = ( yPhy_ -yPhy0_);
		double deltaz = ( zPhy_ -zPhy0_);
		l_ = Math.sqrt(deltax*deltax + deltay*deltay + deltaz*deltaz);
		showChartXYZStatis_[3].addValue(l_);
	}
	public void setChartVisible(boolean flag) {
		chart_.setVisible(flag);
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
	public DescriptiveStatistics[] getStats() {
		return calcForceXYZStatis_;
	}
	public DescriptiveStatistics getStatCross() {
		return calcForceXYZStatis_[2];
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
	public void setXYOrign() {
		double[] xymean = getXYMean();
		xPhy0_ = xymean[0];
		yPhy0_ = xymean[1];
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

}
