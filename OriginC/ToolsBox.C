/*------------------------------------------------------------------------------*
 * File Name:				 													*
 * Creation: 																	*
 * Purpose: OriginC Source C file												*
 * Copyright (c) ABCD Corp.	2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010		*
 * All Rights Reserved															*
 * 																				*
 * Modification Log:															*
 *------------------------------------------------------------------------------*/
////////////////////////////////////////////////////////////////////////////////////
// Including the system header file Origin.h should be sufficient for most Origin
// applications and is recommended. Origin.h includes many of the most common system
// header files and is automatically pre-compiled when Origin runs the first time.
// Programs including Origin.h subsequently compile much more quickly as long as
// the size and number of other included header files is minimized. All NAG header
// files are now included in Origin.h and no longer need be separately included.
//
// Right-click on the line below and select 'Open "Origin.h"' to open the Origin.h
// system header file.
#include <Origin.h>
#include <..\Originlab\graph_utils.h>  // needed for page_add_layer function,
//before use run " run.LoadOC(Originlab\graph_utils.c, 16); " in commandWindow
#include <..\Originlab\theme_utils.h> 
////////////////////////////////////////////////////////////////////////////////////
bool outPutPic = false;
bool clacFRET = true;
bool drawPic = false;
bool drawHistPic = false;
int totalOffset = 1000;
string FRETMODE =  "Manual";
string savePath = "H:\\FRET_TEMP\\";
string HIST_Ext = "_Hist.jpg";
string fileType = "jpg";
string FRET_Ext = "_FRET.jpg";
string THEME_HOME = "D:\\用户目录\\我的文档\\OriginLab\\85\\User Files\\Themes\\Graph\\";
string hist_theme_path = THEME_HOME+"FRET_HIST.oth";
string hist_theme_path1 = THEME_HOME+"FRET_HIST1.oth";
string fret_theme_path = THEME_HOME+"FRET.oth";
string star_theme_path = THEME_HOME+"star.oth";
//#pragma labtalk(0) // to disable OC functions for LT calling.
////////////////////////////////////////////////////////////////////////////////////
// Include your own header files here.
////////////////////////////////////////////////////////////////////////////////////
//GetWorksheetByGraphLayer
Worksheet GetWorksheetByGraphLayer(GraphLayer gl)
{
	XYRange xy;
	string strBookName, strSheetName;
	gl.DataPlots(0).GetDataRange(xy);
	xy.GetBookSheet(strBookName, strSheetName, 0); //get  book and sheet shortname
	return Project.FindPage(strBookName).Layers(0);
}
//geter and seter of data from wks by colIndex
void getValue(Worksheet wks,int colIndex,vector*  v)
{
	DataRange dr;
	dr.Add(wks,colIndex,"X");
	dr.GetData(v,0);
}
void setValue(Worksheet wks,int colIndex,vector  v)
{
	DataRange dr;
	dr.Add(wks,colIndex,"X");
	dr.SetData(v);
}

//Histgram
void Histgram(double nBinSize,vector vData,vector* vBinCenters,vector* vAbsoluteCounts ,vector* vCumulativeCounts)
{
	//===========================================》Set options, including bin size, from, to and border settings.    
	FreqCountOptions fcoOptions;
	fcoOptions.FromMin = min(vData);
	printf("min:%f,max:%f",min(vData),max(vData));
	fcoOptions.ToMax = max(vData);
	fcoOptions.StepSize = nBinSize;
	fcoOptions.IncludeLTMin = 0;
	fcoOptions.IncludeGEMax = 0;
	int nOption = FC_NUMINTERVALS; // to extend last bin if not a full bin
	int nRet = ocmath_frequency_count(
			vData, vData.GetSize(), &fcoOptions,
			*vBinCenters, nBinSize, *vAbsoluteCounts, nBinSize,
			*vCumulativeCounts, nBinSize, nOption);
}
//do clean 
void cleanALL()
{
	foreach(WorksheetPage wp in Project.WorksheetPages){
		printf("\ncleanning :%s\n",wp.Layers(0).GetName());
		Worksheet wks = wp.Layers(0);
		int columnNums = wks.GetNumCols();
		if(columnNums>5){
			for(int i=columnNums;i>=5;i--){
				wks.Columns(i).Destroy();
			}
		}
	}
	foreach(GraphPage gp in Project.GraphPages){
		gp.Destroy();
	}	
}
/* function:void activeGraph(String gname)
*  search graph in current workspace and active  
*  graph name=gname
*
*/
void activeGraph(String gname)
{
	foreach(GraphPage gps in Project.GraphPages){
		if( strcmp(gps.GetName() , gname)==0){
			gps.SetShow();
			updateFormat();
		}
	}
}
/* function:void check()
*  to check if each marker selected by user has 
*  its counterpoint [mark by user,to define the 
*  mean FRET ]
*
*
*/

void check()
{
	vector onTime;
	vector onTimeFRET;
	GraphLayer gl = Project.ActiveLayer();
	GraphPage gp = gl.GetPage();
	if(gp.Layers.Count() <2)return; 	
	GraphLayer glEFF = gp.Layers(1);//其实是Layer2
	int layerCount = glEFF.DataPlots.Count();
	DataPlot dp = glEFF.DataPlots(0); //get data between markers[data which selected by user]
	vector<double> tempX,tempY;
	for (int ilayerCount=1;ilayerCount<layerCount;ilayerCount++){
		DataPlot dpTemp = glEFF.DataPlots(ilayerCount);
		vector<double> tempiX,tempiY;
		dpTemp.GetDataPoints(0, -1, tempiX,tempiY);
		int dpCount = 	tempiX.GetSize();
		for(int j = 0;j<dpCount;j++){
			tempX.Add(tempiX[j]);
			tempY.Add(tempiY[j]);
		}
	}
	vector<int> vnBegin,vnEnd;
	vector<double> vX, vData;
	if (!dp.GetDataMarkers(vnBegin,vnEnd))
		return;   
	int lostData = 0;
	for(int i=0;i<vnBegin.GetSize();i++){
		double x0,y0,x1,y1,xTemp,yTemp;
		bool find = false;
		dp.GetDataPoint(vnBegin[i], & x0,& y0);
		dp.GetDataPoint(vnEnd[i], & x1,& y1);
		// 以下部分将Marker 中间的数据以人工选点值作为FRET值
		int ind = -1;
		for(int iv = 0;iv<tempX.GetSize();iv++){
			if(tempX[iv] >x0 && tempX[iv]< x1){
				ind = iv;
				find = true;
			}
		}
		if(!find){
			lostData++;
			printf("%.3f   %.3f\r\n",x0,x1);
		}		
	
	}
	if(lostData !=0){
		printf("Lost Points \t[%d] \r\n",lostData);
}else{
	printf("OK\r\n");
}
}
/* function:void dTime()
*  get dwell time between current selected marker
*
*
*/
void dTime()
{
	vector onTime;
	vector onTimeFRET;
	GraphLayer gl = Project.ActiveLayer();
	GraphPage gp = gl.GetPage();
	//if(gp.Layers.Count() <2)return; 	
	GraphLayer glEFF = gp.Layers(0);//其实是Layer2
	int layerCount = glEFF.DataPlots.Count();
	DataPlot dp = glEFF.DataPlots(0); //get data between markers[data which selected by user]
 
	vector<int> vnBegin,vnEnd;
	vector<double> vX, vData;
	if (!dp.GetDataMarkers(vnBegin,vnEnd))
		return;   
	int lostData = 0;
	for(int i=0;i<vnBegin.GetSize();i++){
		double x0,y0,x1,y1,xTemp,yTemp;
		bool find = false;
		dp.GetDataPoint(vnBegin[i], & x0,& y0);
		dp.GetDataPoint(vnEnd[i], & x1,& y1);
	 
		if(!find){
			lostData++;
			printf("Dwell Time:\t[ %.3f]\r\n",abs(x0-x1));
		}		
	
	}
 
}

/*To get each column a histgram file (and plot) within the active worksheet;
 *
 *
 *
 *
 */
void gdr(double BinSize,bool isDrawPic)
{
	GraphLayer gl = Project.ActiveLayer();
	if(!gl)
		return; 
	GraphPage gp1 = gl.GetPage();
	Worksheet wks = GetWorksheetByGraphLayer(gl);
	if(!wks)
		return; 	
	GraphLayer glEFF = gp1.Layers(0);
	DataPlot dp = glEFF.DataPlots(); //===========================================》get data between markers
	vector<int> vnBegin,vnEnd;
	vector<double> vX, vData;
	if (!dp.GetDataMarkers(vnBegin,vnEnd))
		return;   
	vector onTime;
	for(int i=0;i<vnBegin.GetSize();i++){
		double x0,y0,x1,y1;
		dp.GetDataPoint(vnBegin[i], & x0,& y0);
		dp.GetDataPoint(vnEnd[i], & x1,& y1);
		onTime.Add((double)(x1 - x0));
	}  
	double nBinSize = 1/BinSize;
	vector vBinCenters(nBinSize),vAbsoluteCounts(nBinSize),vCumulativeCounts(nBinSize);//fret stage
	Histgram(nBinSize,onTime,&vBinCenters,&vAbsoluteCounts ,&vCumulativeCounts);// do histgram
	DataRange drPlot;
	int	ibCenter = wks.AddCol("G");
	int	icount = wks.AddCol("H");
	drPlot.Add(wks,ibCenter,"X");
	drPlot.Add(wks,icount,"Y");
	wks.Columns(ibCenter).SetLongName("OnTime|BinCenters");
	wks.Columns(icount).SetLongName("OnTime|AbsoluteCounts");
	drPlot.SetData(&vAbsoluteCounts,&vBinCenters); 
	if(isDrawPic){ 
		GraphPage gp;
		gp.Create("stack");
		gp.SetName("OnTime_|_Histgram");
		int nNumYs = 1;		
		while ( gp.Layers.Count() < nNumYs ){
			page_add_layer(gp, false, false, false, true,ADD_LAYER_INIT_SIZE_POS_MOVE_OFFSET, false, 0, LINK_STRAIGHT); 
		}
		GraphLayer glHist = gp.Layers(0);
		int nPlot = glHist.AddPlot(drPlot, IDM_PLOT_COLUMN); // return plot index
		DataPlot dpHist = glHist.DataPlots(nPlot);
		//dpHist.SetColor(2);
		Tree  trHist;
		int a =themes_tree_from_file(trHist,hist_theme_path);
		if(0 == glHist.UpdateThemeIDs(trHist.Root) )
			glHist.ApplyFormat(trHist);
		if( nPlot >= 0 )
			glHist.Rescale();	
		if(outPutPic){
			string fileName =  savePath+wks.GetName()+HIST_Ext;
			export_page_to_image( fileName, fileType, gp, 1024, 768, 24, 80,  false );
		}
	}
}
/*To get each column a histgram file (and plot) within the active worksheet;
 *
 *
 *for mali and hushuxin
 *
 */
void hb(double BinSize = 0.02)
{
	Worksheet wks = Project.ActiveLayer();
	if(!wks)
		return;
	int columnNums = wks.GetNumCols();
	for(int i = 1;i<columnNums;i+=2){
		vector  vData;
		int colIndex = i;
		string colName = wks.Columns(colIndex).GetName();
		getValue(wks,colIndex,&vData);
		double nBinSize = (max(vData) - min(vData))/BinSize;
		vector vBinCenters(nBinSize),vAbsoluteCounts(nBinSize),vCumulativeCounts(nBinSize);//fret stage
		Histgram(nBinSize,vData,&vBinCenters,&vAbsoluteCounts ,&vCumulativeCounts);// do histgram
		setValue(wks,11,vBinCenters);
		setValue(wks,12,vAbsoluteCounts);
		DataRange drPlot;
		int	ibCenter = wks.AddCol("G");
		int	icount = wks.AddCol("H");
		drPlot.Add(wks,ibCenter,"X");
		drPlot.Add(wks,icount,"Y");
		wks.Columns(ibCenter).SetLongName(colName+"|BinCenters");
		wks.Columns(icount).SetLongName(colName+"|AbsoluteCounts");
		drPlot.SetData(&vAbsoluteCounts,&vBinCenters); 
		GraphPage gp;
		gp.Create("stack");
		gp.SetName(colName+"_|_Histgram");
		int nNumYs = 1;		
		while ( gp.Layers.Count() < nNumYs ){
			page_add_layer(gp, false, false, false, true,ADD_LAYER_INIT_SIZE_POS_MOVE_OFFSET, false, 0, LINK_STRAIGHT); 
		}
		GraphLayer glHist = gp.Layers(0);
		int nPlot = glHist.AddPlot(drPlot, IDM_PLOT_COLUMN); // return plot index
		DataPlot dpHist = glHist.DataPlots(nPlot);
		dpHist.SetColor(2);
		Tree  trHist;
		int a =themes_tree_from_file(trHist,hist_theme_path);
		if(0 == glHist.UpdateThemeIDs(trHist.Root) )
			glHist.ApplyFormat(trHist);
		if( nPlot >= 0 )
			glHist.Rescale();	
		if(outPutPic){
			string fileName =  savePath+wks.GetName()+HIST_Ext;
			export_page_to_image( fileName, fileType, gp, 1024, 768, 24, 80,  false );
		}
	}
}
/*To Get all FRET data selected by use
 *
 *
 *
 *
 */
void getFRETData()
{
	vector FRETLow,FRETHigh,FRETAll;
	double   counter = 0;
	foreach(WorksheetPage wp in Project.WorksheetPages){
		//counter = counter+1;
		Worksheet wks = wp.Layers(0);
		String type = wp.GetFolder().GetName();
	    if( type.CompareNoCase("perfect")!=0 && type.CompareNoCase("good")!=0)  continue;
	
		vector tempLow,tempHigh;
		getValue(wks,0,&tempLow);
		getValue(wks,1,&tempHigh);
		int len = tempLow.GetSize();		
		for(int i = 0;i<len;i++){
			FRETLow.Add(tempLow[i]);
			FRETHigh.Add(tempHigh[i]);
			FRETAll.Add(tempLow[i]);
			FRETAll.Add(tempHigh[i]);
		}				 
	}
	//printf("%f",counter);
	Worksheet wks1;
	wks1= Project.FindPage("userFRET").Layers(0);
	if(!wks1){    	
		wks1.Create();
		wks1.GetPage().SetName("userFRET");
	}
	int columnNums = wks1.GetNumCols();
	if(columnNums<3){
		for(int i =0;i<3-columnNums;i++)
			wks1.AddCol("a");
	}
	wks1.Columns(0).SetLongName("FRETLow");
	wks1.Columns(1).SetLongName("FRETHigh");
	wks1.Columns(2).SetLongName("FRETAll");
	setValue(wks1,0,FRETLow);
	setValue(wks1,1,FRETHigh);
	setValue(wks1,2,FRETAll);
}
void addDataToall(int i,double st,double ed,vector data)
{
		Worksheet wks= Project.FindPage("OnTimeAll").Layers(0);
		String n0(i),n1(st),n2(ed),n3,dc(data.GetSize());
		n3 =n0+"-0"+n1+"-0"+n2;
		wks.Columns(i-1).SetLongName(n3);
		setValue(wks,i-1,data);
		wks= Project.FindPage("OnTimeAll").Layers(i);
		wks.SetName(n3);
		wks.Columns(0).SetLongName("Time(sec)"+n3+"["+dc+"]");
		//out_str(wks.GetName());
}
void Hist_All_Bat(int mismatch=22,int counter=100000)
{ 
 
	if(mismatch == 22){
	//22mismatch
	 DoubleArray saList = {/*1*/0.2,0.28, /*2*/0.29,0.42,/*3*/ 0.43,0.55,/*4*/ 0.56,0.66,/*5*/ 0.661,0.75,/*6*/  0.76,0.85, /*7*/ 0.85,1, /*8*/ 0,1};
	// DoubleArray saList = {/*10.2,0.28,*/ /*2*/0.29,0.42,/*3*/ 0.43,0.55,/*4*/ 0.56,0.66,/*5*/ 0.661,0.75,/*6 7*/  0.76,1, /*8*/ 0,1};
	 int len = saList.GetSize();
	Hist_All(1,saList[0],saList[9],counter);
	for (int i=2;i<=len/2 -1;i++){
		Hist_All(i,saList[i*2-2],saList[i*2-1],counter);
	}
	Hist_All(8,saList[10],1,counter);
	} 
	if(mismatch == 52){
	//52mismatch 	
	DoubleArray saList = {0.2,0.365,0.366,0.48,0.481,0.61,0.661,0.71,0.72,0.81,0.835,0.9,0.91,1,0.2,1};
	//DoubleArray saList = {0.2,0.365,0.366,0.48,0.481,0.61,0.661,0.71,0.72,0.81,0.835,0.9,0.91,1,0.2,1};
	int len = saList.GetSize();
	Hist_All(1,saList[0],saList[9],counter);
	for (int i=2;i<=len/2 -1;i++){
		Hist_All(i,saList[i*2-2],saList[i*2-1],counter);
	}
	Hist_All(8,saList[10],1,counter);
	}
	if(mismatch == 74){
	//74mismatch 
	//DoubleArray saList =  {0.32,0.41,0.42,0.52,0.53,0.64,0.67,0.78,0.781,0.87,0.84,0.92,0.931,1,0,1};
	DoubleArray saList =  {0.32,0.41,0.42,0.52,0.53,0.64,0.67,0.78,0.781,0.87,0.84,0.92,0.93,1,0,1};
	int len = saList.GetSize();
	Hist_All(1,saList[0],saList[9],counter);
	for (int i=2;i<=len/2 -1;i++){
		Hist_All(i,saList[i*2-2],saList[i*2-1],counter);
	}
	Hist_All(8,saList[10],1,counter);
	}
 
	
}
void GraphPageCalc()
{
	int GraphCounter = 0;
	foreach(GraphPage gp in Project.GraphPages){
		if(gp.Layers.Count() <2)continue; 	
		GraphCounter++;
	}
	printf("Totally:\t[%d]\tgraphs",GraphCounter);
}
/*To get all data histgram(and plot) with current selected marker;
 *
 *
 *
 *double fixstart=0,double fixend=1
 */
  
void Hist_All(int RecAindex=1,double vFRETbegin=0 ,double vFRETend=1,int GraphUse=50000)
{
	double fixstart=0;
	double fixend=0;
	vector onTime,onTimeFRET,onTimeFRETLow,onTimeFRETHist;
	double BinSize = 0.02;
	int OnTimenormalCounter=0,OnTimePlus15Counter=0,FRETSeperaterCounter=0,FRETSeperaterCounter2=0;
	int findNewAddPoint = 0;
	int GraphCounter = 0;
	foreach(GraphPage gp in Project.GraphPages){
		if(gp.Layers.Count() <2)continue; 
	    //if(gp.GetName().CompareNoCase("Graph79") ==0)continue;
		String type = gp.GetFolder().GetParent().GetName();
		GraphCounter++;
	    if(GraphCounter>GraphUse)continue;
		//if( type.CompareNoCase("perfect")!=0 && type.CompareNoCase("good")!=0)  continue;
	
		GraphLayer glEFF = gp.Layers(1);////get all data selected by user	
		int layerCount = glEFF.DataPlots.Count();
		vector<double> tempX,tempY;	
		for (int ilayerCount=1;ilayerCount<layerCount;ilayerCount++){
			DataPlot dpTemp = glEFF.DataPlots(ilayerCount);
			vector<double> tempiX,tempiY;
			dpTemp.GetDataPoints(0, -1, tempiX,tempiY);
			int dpCount = 	tempiX.GetSize();
			for(int j = 0;j<dpCount;j++){
				tempX.Add(tempiX[j]);
				tempY.Add(tempiY[j]);
			}
		}
	
		DataPlot dp = glEFF.DataPlots(0); //get  markers		
		vector<int> vnBegin,vnEnd;
		vector<double> vX, vData;
		if (!dp.GetDataMarkers(vnBegin,vnEnd))
			continue;   
		int lostData = 0;
		//new add point without marker,just add to onTimeFRET
		
		for(int xSize = 0;xSize<tempX.GetSize();xSize++){
		     double vvi = tempX[xSize];
			 bool find = false;
			 for(int i=0;i<vnBegin.GetSize();i++){
				double x0,y0,x1,y1,xTemp,yTemp;
				dp.GetDataPoint(vnBegin[i], & x0,& y0);
				dp.GetDataPoint(vnEnd[i], & x1,& y1);
				if(x0<vvi && vvi<x1){
					find = true;
					continue;
				}
			 }
			if(!find){
				findNewAddPoint ++;
		     	onTimeFRET.Add((double)tempY[xSize]);
			}
		}
		
		//end new add point without marker
		// 以下部分将Marker 中间用户选的点的均值作为FRET值
		 for(int i=0;i<vnBegin.GetSize();i++){
			double x0,y0,x1,y1,xTemp,yTemp;
			dp.GetDataPoint(vnBegin[i], & x0,& y0);
			dp.GetDataPoint(vnEnd[i], & x1,& y1);
		
			bool find = false,findsedPoint = false,find3rdPoint = false;			//get data between markers
			double temp = 0,temp1=0,temp2 = 0,tempt=0,counter=0;
		
				// 以下部分将Marker 中间的数据平均来作为FRET值
		    if(FRETMODE == "AUTO"){
		    int counts = vnEnd[i] - vnBegin[i];
			for(int j = vnBegin[i];j<vnEnd[i];j++){
				dp.GetDataPoint(j, & xTemp,& yTemp);
				temp += yTemp;
			}
			temp = temp/counts;
			find = true;  
		    }else{
			for(int iv = 0;iv<tempX.GetSize();iv++){
				double v = tempX[iv];
				if( v>x0 && v< x1){//data point's location is between this marker
				     if(findsedPoint){
				     	tempt=tempY[iv];//there are 3 data points
				     		if(tempt<temp1){
						    	temp2 = temp1;
						        temp1 = tempt;
						    }else{
						    	temp2 = tempt;
						    }
				     	find3rdPoint = true;
				     }else{
						if(find){
							tempt=tempY[iv];//there are two data points
						    if(tempt<temp){
						    	temp1 = temp;
						        temp = tempt;
						    }else{
						    	temp1 = tempt;
						    }
						        
							findsedPoint = true;
						}else{
							temp = tempY[iv];
							find = true;
						}
				     }
					
				}
			}
			if(!find){
				lostData++;
				//printf("[%.3f,%.3f]-",x0,x1);
			}
		    }
			// 以下部分将Marker 中间的数据以人工选点值作为FRET值
			/* 注意：总共有三种情况，
			1、marker之间只有一个点：用来做OnTime统计
			2、marker之间有两个点，且都小于1.5：用来统计分离峰
			3、marker之间有两个点，且有一个点大于1.5：用来做OnTime统计，且此处OnTime要加15ms
			4、marker之间有三个点，且有一个点大于1.5；用来做分离峰及OnTime统计，且此处OnTime要加15ms
			*/
				
			if(max(temp,temp1)>=vFRETbegin && max(temp,temp1)<=vFRETend && find){// use the data between begin 、end only
				if(find3rdPoint){//有3个点
					onTime.Add((double)( x1 - x0 +0.015));
					onTimeFRET.Add(temp1);
					if(fixstart<temp1 && temp1<fixend){
						printf("  %s\t\t[%f]\t[%f]\r\n ",gp.GetName(),x0, x1);
					}
					onTimeFRETLow.Add(temp);
				    onTimeFRETHist.Add(temp);
				    onTimeFRETHist.Add(temp1);	
					FRETSeperaterCounter2++;
				}
				else{
					if(findsedPoint){//有两个点
						if(temp >1.5 || temp1>1.5){// 有两个点 且有一个点大于1.5：用来做OnTime统计，且此处OnTime要加15ms
							onTime.Add((double)(( x1 - x0)+0.015));
						 	onTimeFRET.Add((double)min(temp,temp1));
						 	if(fixstart<temp1 && temp1<fixend){
						printf("  %s\t\t[%f]\t[%f]\r\n ",gp.GetName(),x0, x1);
					}
						 	//onTimeFRETHist.Add((double)min(temp,temp1));
						OnTimePlus15Counter++;
						}else{//有两个点，且都小于1.5：用来统计分离峰
							onTime.Add((double)( x1 - x0));
							onTimeFRET.Add((double)max(temp,temp1));
							if(fixstart<temp1 && temp1<fixend){
						printf("  %s\t\t[%f]\t[%f]\r\n ",gp.GetName(),x0, x1);
					}
							onTimeFRETLow.Add((double)min(temp,temp1));
						    onTimeFRETHist.Add(temp);
						    onTimeFRETHist.Add(temp1);	
						FRETSeperaterCounter++;
						//printf(" [%f]-[%f] FRETSeperaterCounter \t %s\r\n",x0, x1,gp.GetName());
						}
					}else{
						onTime.Add((double)( x1 - x0));
						onTimeFRET.Add((double)temp);
						if(fixstart<temp1 && temp1<fixend){
						printf("  %s\t\t[%f]\t[%f]\r\n ",gp.GetName(),x0, x1);
					}
						//onTimeFRETHist.Add(temp);
						OnTimenormalCounter ++;
					}
				}
			}

			
		}
			
		if(lostData !=0){
			//printf(" [%d] Points Lost \t %s\r\n",lostData,gp.GetName());
		}
	}
			printf("  Total Graph:\t\t[%d]\r\n ",GraphCounter);
			printf("  GraphUse :\t\t[%d]\r\n ",GraphUse);
 			printf(" OnTimenormalCounter\t\t[%d]\r\n ",OnTimenormalCounter);
			printf(" OnTimePlus15Counter\t\t[%d]\r\n ",OnTimePlus15Counter);
			printf(" FRETSeperaterCounter\t\t[%d]\r\n ",FRETSeperaterCounter);
			printf(" FRETSeperaterCounter2\t\t[%d]\r\n ",FRETSeperaterCounter2);
			printf(" findNewAddPoint\t\t[%d]\r\n ",findNewAddPoint);
			
	Worksheet wks;
	wks= Project.FindPage("OnTimeHist").Layers(0);
	if(!wks){    	
		wks.Create();
		wks.GetPage().SetName("OnTimeHist");
	}

	int columnNums = wks.GetNumCols();
	if(columnNums<6){
		for(int i =0;i<6-columnNums;i++)
			wks.AddCol("a");
	}
	wks.Columns(0).SetLongName("OnTime|deltaT");
	wks.Columns(1).SetLongName("OnTime|FRET");
	wks.Columns(2).SetLongName("OnTime|FRETLow");
	wks.Columns(3).SetLongName("OnTime(sec)|BinCenters");
	wks.Columns(4).SetLongName("AbsoluteCounts");
	wks.Columns(5).SetLongName("onTimeFRETHist");
	setValue(wks,0,onTime);
	setValue(wks,1,onTimeFRET);
	setValue(wks,2,onTimeFRETLow);
	setValue(wks,5,onTimeFRETHist);
	addDataToall(RecAindex,vFRETbegin,vFRETend,onTime);
	if(drawHistPic){ 
		foreach(GraphPage gps in Project.GraphPages){		 		 
			if( strcmp(gps.GetName() , "OnTime_Histgram")==0) gps.Destroy();
		}
		GraphPage gp;
		gp.Create("Origin");
		gp.SetName("OnTime_Histgram");
		int nNumYs = 1;		
		while ( gp.Layers.Count() < nNumYs ){
			page_add_layer(gp, false, false, false, true,ADD_LAYER_INIT_SIZE_POS_MOVE_OFFSET, false, 0, LINK_STRAIGHT); 
		}
		GraphLayer glHist = gp.Layers(0);
		Tree  trHist;
		int a =themes_tree_from_file(trHist,hist_theme_path1);
		double inputs = 0;
		double binBegin = 0.005;
		double binInc = 0.0001;
		double defaultValue = 0.0189;
		
		while(inputs != -1){
		//while(binBegin <0.05){
			//inputs = InputBox( defaultValue, NULL );
		    //inputs = 0; 
			if( inputs == 1){
				binBegin = binBegin - binInc;
			}else if(inputs != defaultValue){
				binBegin = inputs;
			}else{
				binBegin = binBegin + binInc;
			}
			inputs = -1;
			BinSize = binBegin;
			printf("binning:%.4f\r\n",BinSize);
			double nBinSize = 1/BinSize;
			vector vBinCenters(nBinSize),vAbsoluteCounts(nBinSize),vCumulativeCounts(nBinSize);//fret stage
			Histgram(nBinSize,onTimeFRETHist,&vBinCenters,&vAbsoluteCounts ,&vCumulativeCounts);// do histgram
			DataRange drPlot;
			drPlot.Add(wks,3,"X");
			drPlot.Add(wks,4,"Y");
			drPlot.SetData(&vAbsoluteCounts,&vBinCenters); 
			int nPlot = glHist.AddPlot(drPlot, IDM_PLOT_COLUMN); // return plot index
			DataPlot dpHist = glHist.DataPlots(nPlot);
			dpHist.SetColor(1);		
		/*	if(0 == glHist.UpdateThemeIDs(trHist.Root) )
				glHist.ApplyFormat(trHist);*/		
			if( nPlot >= 0 )
				glHist.Rescale();
			if(true){
				string fileName ;
				fileName.Format("%s%s%.4f%s",savePath,fileName,binBegin,HIST_Ext);
				export_page_to_image( fileName, fileType, gp, 1024, 768, 24, 80,  false );
			}
		}//while
	}//if
	
}
/*To get all data histgram(and plot) with current selected marker;
 *for 马丽
 *
 *
 *
 */
void hs()
{
	vector onTime;
	foreach(GraphPage gp in Project.GraphPages){		 	
		GraphLayer glEFF = gp.Layers(0);////get all data selected by user	
		int layerCount = glEFF.DataPlots.Count();

		DataPlot dp = glEFF.DataPlots(0); //get  markers
		DataPlot dp1 = glEFF.DataPlots(1); //get  markers
		vector<int> vnBegin,vnEnd,vnBegin1,vnEnd1;
		dp.GetDataMarkers(vnBegin,vnEnd);
		dp1.GetDataMarkers(vnBegin1,vnEnd1);
		if (!dp.GetDataMarkers(vnBegin,vnEnd) && !dp1.GetDataMarkers(vnBegin1,vnEnd1))
			continue;   

	
		// 以下部分将Marker 中间用户选的点的均值作为FRET值
		 for(int i=0;i<vnBegin.GetSize();i++){
			double x0,y0,x1,y1,xTemp,yTemp;
			dp.GetDataPoint(vnBegin[i], & x0,& y0);
			dp.GetDataPoint(vnEnd[i], & x1,& y1);				 
			onTime.Add((double)( x1 - x0));				 			
		}	
		for(i=0;i<vnBegin1.GetSize();i++){
			double x0,y0,x1,y1,xTemp,yTemp;
			dp1.GetDataPoint(vnBegin1[i], & x0,& y0);
			dp1.GetDataPoint(vnEnd1[i], & x1,& y1);				 
			onTime.Add((double)( x1 - x0));				 			
		}	
	}
		 
			
	Worksheet wks;
	wks= Project.FindPage("OnTimeHist").Layers(0);
	if(!wks){    	
		wks.Create();
		wks.GetPage().SetName("OnTimeHist");
	}

	int columnNums = wks.GetNumCols();
	if(columnNums<1){
		for(int i =0;i<1-columnNums;i++)
			wks.AddCol("a");
	}
	wks.Columns(0).SetLongName("OnTime|deltaT");
	setValue(wks,0,onTime); 	
}
/*To get histgram(and plot) with current selected marker in a single graph;
 *
 *
 *
 *
 */
void getTime()
{
	GraphLayer gl = Project.ActiveLayer();
	if(!gl)
		return; 
	GraphPage gp = gl.GetPage();
	Worksheet wks = GetWorksheetByGraphLayer(gl);
	if(!wks)
		return; 	
	GraphLayer glEFF = gp.Layers(0);
	DataPlot dp = glEFF.DataPlots(); //===========================================》get data between markers
	vector<int> vnBegin,vnEnd;
	if (!dp.GetDataMarkers(vnBegin,vnEnd))
		return;   
	double x0,y0,x1,y1,xTemp,yTemp;
	for(int i=0;i<vnBegin.GetSize();i++){
		dp.GetDataPoint(vnBegin[i], & x0,& y0);
		dp.GetDataPoint(vnEnd[i],   & x1,& y1);
		printf("Time:%.4f sec height:%.1f nm\r\n",x1 - x0,y1 - y0); 
	}
	GraphLayer glMT = gp.Layers(0);
	int layerCount = glMT.DataPlots.Count();
	DataPlot dpMT = glMT.DataPlots(0); //===========================================》get data between markers
	vector<double> tempX,tempY;
	for (int ilayerCount=1;ilayerCount<layerCount;ilayerCount++){
		DataPlot dpTemp = glMT.DataPlots(ilayerCount);
		vector<double> tempiX,tempiY;
		dpTemp.GetDataPoints(0, -1, tempiX,tempiY);
		int dpCount = 	tempiX.GetSize();
		for(int j = 0;j<dpCount;j++){
			tempX.Add(tempiX[j]);
			tempY.Add(tempiY[j]);
			printf("x:%.4f sec y:%.1f nm\r\n",tempiX[j],tempiY[j]); 
		}
	}
	//===========================================》get data between markers
}
/*To get histgram(and plot) with current selected marker in a single graph;
 *
 *
 *
 *
 */
void Hist(double bs = 0.02)
{
	double nBinSize = 1/bs;
	GraphLayer gl = Project.ActiveLayer();
	if(!gl)
		return; 
	GraphPage gp = gl.GetPage();
	Worksheet wks = GetWorksheetByGraphLayer(gl);
	if(!wks)
		return; 	
	GraphLayer glEFF = gp.Layers(1);
	DataPlot dp = glEFF.DataPlots(); //===========================================》get data between markers
	vector<int> vnBegin,vnEnd;
	vector<double> vX, vData;
	if (!dp.GetDataMarkers(vnBegin,vnEnd))
		return;   
	for(int i=0;i<vnBegin.GetSize();i++){
		vector x,y;
		dp.GetDataPoints(vnBegin[i], vnEnd[i], x, y);
		for(int j=0;j<x.GetSize();j++){
			vX.Add((double)x[j]);
			vData.Add((double)y[j]);
		}  	
	}                            //===========================================》get data between markers
	return;
	setValue(wks,9,vX);//===========================================》Save ROI of fret into  worksheet
	setValue(wks,10,vData);
	vector vBinCenters(nBinSize),vAbsoluteCounts(nBinSize),vCumulativeCounts(nBinSize);//fret stage
	Histgram(nBinSize,vData,&vBinCenters,&vAbsoluteCounts ,&vCumulativeCounts);// do histgram
	DataRange drPlot;
	drPlot.Add( wks, 11,"X");
	drPlot.Add( wks, 12,"Y");
	drPlot.SetData(&vAbsoluteCounts,&vBinCenters);//Save ROI of fret hit into last two column int the worksheet
	int nNumYs = 3;		
	while ( gp.Layers.Count() < nNumYs ){
		page_add_layer(gp, false, false, false, true,ADD_LAYER_INIT_SIZE_POS_MOVE_OFFSET, false, 0, LINK_STRAIGHT); 
	}
	GraphLayer glHist = gp.Layers(2);
	gp.Layers(0).Show(false);	
	int nPlot = glHist.AddPlot(drPlot, IDM_PLOT_COLUMN); // return plot index
	DataPlot dpHist = glHist.DataPlots(nPlot);
	dpHist.SetColor(2);
	Tree  trHist;
	int a =themes_tree_from_file(trHist,hist_theme_path);
	if(0 == glHist.UpdateThemeIDs(trHist.Root) )
		glHist.ApplyFormat(trHist);
	if( nPlot >= 0 )
		glHist.Rescale();	
	if(outPutPic){
		string fileName =  savePath+wks.GetName()+HIST_Ext;
		export_page_to_image( fileName, fileType, gp, 1024, 768, 24, 80,  false );
	}
}
void getCurrentXYScatterPlot()
{
	Worksheet wks = Project.ActiveLayer();
	if(!wks)
		return;	
}
void getAllMTStepsize(){//0=OK,1=notuse
	Worksheet wks;
	wks= Project.FindPage("MTStepSize").Layers(0);
	if(!wks){    	
		wks.Create();
		wks.GetPage().SetName("MTStepSize");
	}
    
	int columnNums = wks.GetNumCols();
	StringArray ColumnName = {"File-Date","File-Time","TotalStepSize","TotaldwellTime","Time-Low","Time-High","Low","High",
	"StepSize","dwellTime","FirstStepSize","restStepSize"};
	
	if(columnNums<ColumnName.GetSize()){
		for(int i =0;i<ColumnName.GetSize()-columnNums;i++)
			wks.AddCol("C");
	}
	for(int i = 0;i<ColumnName.GetSize();i++){
		wks.Columns(i).SetLongName(ColumnName[i]);
	}
	vector<double> Low,LowTime,High,HighTime,Step,TotalStepSize,dwellTime,TotalDwellTime,FirstStepSize,RestStepSize;	
	vector<long> Date,Time;
	foreach(GraphPage gp in Project.GraphPages){
		if(gp.Layers.Count() >1)continue; 					
		GraphLayer gl = gp.Layers(0);
		if(!gl)
			return; 	
		String name = gp.GetLongName();
	//	out_str("working on\t"+name);
		int nameLen = name.GetLength();
		String flag = name.Mid(nameLen-6,6);
		String flag1 = name.Mid(nameLen-2,2);
	    if( strcmp(flag , "notuse")==0 ) continue;
	    if( strcmp(flag1 , "ok") !=0 ) continue;
		long ldate =  atoi(name.Mid(2,6));
		long ltime =  atoi(name.Mid(9,6));
		int layerCount = gl.DataPlots.Count();	
		double _totalStepSize=0,_totalDwellTime=0;
		bool isFirst = false;
		for (int ilayerCount=1;ilayerCount<layerCount;ilayerCount++){			 
			DataPlot dpTemp = gl.DataPlots(ilayerCount);
			vector<double> tempiX,tempiY;
			dpTemp.GetDataPoints(0, -1, tempiX,tempiY);
			if(tempiX.GetSize()>2)continue;
	        LowTime.Add(tempiX[0]);
	        HighTime.Add(tempiX[1]);
			Low.Add(tempiY[0]);
			High.Add(tempiY[1]);
			
			Step.Add(abs(tempiY[1]-tempiY[0])/1.141);
			if(!isFirst){
				FirstStepSize.Add(abs(tempiY[1]-tempiY[0]));
				RestStepSize.Add(0);
				isFirst = true;
			}else{
				RestStepSize.Add(abs(tempiY[1]-tempiY[0]));
			}
			_totalStepSize +=abs(tempiY[1]-tempiY[0]);		 
		}
		for(int j=dwellTime.GetSize();j<LowTime.GetSize()-1;j++){
			dwellTime.Add(LowTime[j+1]-HighTime[j]);
			_totalDwellTime +=abs(LowTime[j+1]-HighTime[j]);
		}
		Date.Add(ldate);
		Time.Add(ltime);
		TotalStepSize.Add(_totalStepSize);
		TotalDwellTime.Add(_totalDwellTime);
		for(int i=Date.GetSize();i<LowTime.GetSize();i++){
			Date.Add(0);
			Time.Add(0);
			TotalStepSize.Add(0);
			TotalDwellTime.Add(0);
			FirstStepSize.Add(0);
		}
		Date.Add(0);
		Time.Add(0);
		LowTime.Add(0);
		HighTime.Add(0);
		Low.Add(0);
		High.Add(0);
		Step.Add(0);
		dwellTime.Add(0);
		dwellTime.Add(0);
		TotalStepSize.Add(0);
		TotalDwellTime.Add(0);
		FirstStepSize.Add(0);
		RestStepSize.Add(0);
		
	    
	}//for each
	setValue(wks,0,Date);
    setValue(wks,1,Time);
    setValue(wks,2,TotalStepSize);
	setValue(wks,3,TotalDwellTime);
	setValue(wks,4,LowTime);
	setValue(wks,5,HighTime);
	setValue(wks,6,Low);
	setValue(wks,7,High);
	setValue(wks,8,Step);
	setValue(wks,9,dwellTime);
	setValue(wks,10,FirstStepSize);
	setValue(wks,11,RestStepSize);

}
void getCurrentMTStepsize()
{
	Worksheet wks;
	wks= Project.FindPage("MTStepSize").Layers(0);
	if(!wks){    	
		wks.Create();
		wks.GetPage().SetName("MTStepSize");
	}
    
	int columnNums = wks.GetNumCols();
	StringArray ColumnName = {"File-Date","File-Time","Time-Low","Time-High","Low","High",
	"StepSize","dwellTime","TotalStepSize","TotaldwellTime"};
	
	if(columnNums<ColumnName.GetSize()){
		for(int i =0;i<ColumnName.GetSize()-columnNums;i++)
			wks.AddCol("C");
	}
	for(int i = 0;i<ColumnName.GetSize();i++){
		wks.Columns(i).SetLongName(ColumnName[i]);
	}
		
	GraphLayer gl = Project.ActiveLayer();
	if(!gl)
		return; 
	GraphPage gp = gl.GetPage();	 		
	GraphLayer glEFF = gp.Layers(0); 	
	String name = gp.GetLongName();
	int layerCount = glEFF.DataPlots.Count();
	vector<double> Low,LowTime,High,HighTime,Step,dwellTime;	
	vector<long> Date,Time;
	long ldate =  atoi(name.Mid(2,6));
	long ltime =  atoi(name.Mid(9,6));
	double totalStepSize=0,totalDwellTime=0;
	Date.Add(ldate);
	Time.Add(ltime);
	for (int ilayerCount=1;ilayerCount<layerCount;ilayerCount++){
		DataPlot dpTemp = glEFF.DataPlots(ilayerCount);
		vector<double> tempiX,tempiY;
		dpTemp.GetDataPoints(0, -1, tempiX,tempiY);
        LowTime.Add(tempiX[0]);
        HighTime.Add(tempiX[1]);
		Low.Add(tempiY[0]);
		High.Add(tempiY[1]);
		Step.Add(abs(tempiY[1]-tempiY[0]));
		totalStepSize +=abs(tempiY[1]-tempiY[0]);
		//Date.Add(0);
		//Time.Add(0);		 
	}
	for(int j=0;j<LowTime.GetSize()-1;j++){
		dwellTime.Add(LowTime[j+1]-HighTime[j]);
		totalDwellTime +=abs(LowTime[j+1]-HighTime[j]);
	}
    setValue(wks,0,Date);
    setValue(wks,1,Time);
	setValue(wks,2,LowTime);
	setValue(wks,3,HighTime);
	setValue(wks,4,Low);
	setValue(wks,5,High);
	setValue(wks,6,Step);
	setValue(wks,7,dwellTime);
	setValue(wks,8,totalStepSize);
	setValue(wks,9,totalDwellTime);
   
}
/*To generate Current fret chart on a key down
 *
 *
 *
 *
 */
void getCurrentFRET()
{
	Worksheet wks = Project.ActiveLayer();
	if(!wks)
		return;
	getFRET(wks);
	
}
/*To generate fret chart on a key down
 *
 *
 *
 *
 */
void getALL()
{
	foreach(WorksheetPage wp in Project.WorksheetPages){
		Folder fd = wp.GetFolder();
		Folder sfd = fd.GetFolder("FRET");
		if(!sfd.IsValid())
			sfd = fd.AddSubfolder("FRET");
		sfd.Activate();
		printf("\ndelling with:[%s]-[%s]\n",fd.GetName(),wp.Layers(0).GetName());
		getFRET(wp.Layers(0))
	}	
}
void MFC()
{
	foreach(WorksheetPage wp in Project.WorksheetPages){
		Folder fd = wp.GetFolder();
		Folder sfd = fd.GetFolder("FRET");
		if(!sfd.IsValid())
			sfd = fd.AddSubfolder("FRET");
		sfd.Activate();
		printf("\ndelling with:[%s]-[%s]\n",fd.GetName(),wp.Layers(0).GetName());
		getMinStd(wp.Layers(0))
	}	
}
/*To generate fret chart  
 *
 *
 *
 *
 */
void getMinStd(Worksheet wks)
{ 
	//Worksheet wks = Project.ActiveLayer();
	if( !wks )
		return;
	if(16 >wks.GetNumCols()){//Only the dataSheet with correct columns will be calculate	
	wks.AddCol("G");
	wks.AddCol("G");
	wks.AddCol("G");
	wks.AddCol("H");
	wks.AddCol("H");
	wks.Columns(11).SetLongName("Time");
	wks.Columns(12).SetLongName("Std");
	wks.Columns(13).SetLongName("Kp");
	wks.Columns(14).SetLongName("Ki");
	wks.Columns(15).SetLongName("Kd");
	}
	
	vector std,time,s1,t1,kp,ki,kd,P,I,D;   
	int counter = 0;
	int minStd = 1;
	int maxStd = 999999;
	getValue(wks,10,&std);
	getValue(wks,0,&time);
	getValue(wks,6,&P);
	getValue(wks,7,&I);
	getValue(wks,8,&D);

	for(int i=0;i<std.GetSize();i++){
		if(std[i]>=minStd && std[i]<=maxStd)
		{
			s1.Add(std[i]);
			t1.Add(time[i]/1000);
			kp.Add(P[i]);
			ki.Add(I[i]);
			kd.Add(D[i]);
		}
	}
    setValue(wks,11,t1);
    setValue(wks,12,s1);
    setValue(wks,13,kp);
    setValue(wks,14,ki);
    setValue(wks,15,kd);
	if(drawPic){
		DataRange drPlot;
		drPlot.Add(wks, 11, "X");
		drPlot.Add(wks, 12, "Y");
		drPlot.Add(wks, 13, "Y");
		drPlot.Add(wks, 14, "Y");
		drPlot.Add(wks, 15, "Y");
	
		string wsName = wks.GetPage().GetLongName();
		string pstrFileName;
		string pstrExt;		 
		separate_file_name_ext(wsName, pstrFileName, pstrExt);
		foreach(GraphPage gps in Project.GraphPages){	 		 
			if( strcmp(gps.GetName() , pstrFileName+"_FRET")==0) gps.Destroy();
		}
		GraphPage gp;
		gp.Create("stack");
		gp.SetLongName(pstrFileName+"_FRET");
		int nNumYs = 1;
		while ( gp.Layers.Count() < nNumYs ){
			page_add_layer(gp, false, false, false, true,ADD_LAYER_INIT_SIZE_POS_MOVE_OFFSET, false, 0, LINK_STRAIGHT); 
		}
		GraphLayer gl = gp.Layers(0);
		int nPlot = gl.AddPlot(drPlot, IDM_PLOT_LINE); // return plot index		 
	 
		if( nPlot >= 0 )
			gl.Rescale();	
 
	}
	printf("  OK!");
}
/*To fix cy5 data to zero if debackground is unsuitable;
 *
 *
 *
 *
 */
void fixToZero(double offset)
{
	GraphLayer gl = Project.ActiveLayer();
	if(!gl)
		return; 
	GraphPage gp = gl.GetPage();
	Worksheet wks = GetWorksheetByGraphLayer(gl);
	if(!wks)
		return; 
	vector  cy3,cy5,eff;   
	getValue(wks,5,&cy3);
	getValue(wks,6,&cy5);	
	cy5 = cy5 - offset;
	eff = cy5/(cy3+cy5); 
	setValue(wks,5,cy5);
	setValue(wks,8,eff);  	 	
}
/*
 *use to change MT data timestemp->sec zpos->nm
 *
 *
 *
 */
void cv() 
{
	foreach(WorksheetPage wp in Project.WorksheetPages){
		printf("\nchangeing value with:%s\n",wp.Layers(0).GetName());
		Worksheet wks = wp.Layers(0);
		DataRange drIn;//===========================================》get raw data from datasheet
		drIn.Add(wks,1,"X");
		drIn.Add(wks,6,"Y");
		wks.Columns(1).SetType(OKDATAOBJ_DESIGNATION_X); // A Y column
		vector timestemp,zpos;
		drIn.GetData(&zpos,1);
		drIn.GetData(&timestemp,0);
		timestemp = timestemp/1000;//to s
		zpos = -1000*zpos;//to nm and up side down
		//zpos = zpos - min(zpos);
		drIn.SetData(&zpos,&timestemp);//from back to front
	}	
}
void updateFormat()
{
	//	foreach(GraphPage gps in Project.GraphPages){
	GraphLayer gls = Project.ActiveLayer();
	if(!gls)
		return; 
	GraphPage gp = gls.GetPage();
	GraphLayer gl = gp.Layers(0);
	GraphLayer glEff = gp.Layers(1);
	Tree  tr;
	int b =themes_tree_from_file(tr,fret_theme_path);
	if(0 == gl.UpdateThemeIDs(tr.Root) )
		gl.ApplyFormat(tr);
	if(0 == glEff.UpdateThemeIDs(tr.Root) )
		glEff.ApplyFormat(tr);
	//legend_update(glEff, ALM_LONGNAME_UNITS);
	//legend_update(gl, ALM_LONGNAME_UNITS);
	//gl.Rescale();	
	int layerCount = glEff.DataPlots.Count();
	DataPlot dp = glEff.DataPlots(0); //===========================================》get data between markers
	vector<double> tempX,tempY;
	Tree  tr1;
	int c =themes_tree_from_file(tr1,star_theme_path);
	for (int ilayerCount=1;ilayerCount<layerCount;ilayerCount++){
		DataPlot dpTemp = glEff.DataPlots(ilayerCount);
		if(0 == dpTemp.UpdateThemeIDs(tr1.Root) )
			dpTemp.ApplyFormat(tr1);
	}
	//glEff.Rescale();
	//}
}
/*To generate fret chart  
 *
 *
 *
 *
 */
void getFRET(Worksheet wks)
{ 
	//Worksheet wks = Project.ActiveLayer();
	if( !wks )
		return;
	if(5 >wks.GetNumCols()){//Only the dataSheet with correct columns will be calculate	
		printf("  Pass!");
		return;
	}
	vector cy3,newcy3,cy3bg,newcy5,cy5,cy5bg,Eff,total,temp;   
	int	iCol3 = wks.AddCol("G");
	int	iCol5 = wks.AddCol("H");
	int	iTotal = wks.AddCol("I");
	int	iColeff = wks.AddCol("J");
	int	iColTimes = wks.AddCol("K");
	int	iColeffs = wks.AddCol("L");
	int	ibinCenter = wks.AddCol("M");
	int	icount = wks.AddCol("N");
	wks.Columns(0).SetLongName("Time(sec)");
	wks.Columns(1).SetLongName("Cy3|raw");
	wks.Columns(2).SetLongName("Cy3BG|raw");
	wks.Columns(3).SetLongName("Cy5|raw");
	wks.Columns(4).SetLongName("Cy5BG|raw");
	wks.Columns(5).SetLongName("Cy3(a.u.)");
	wks.Columns(6).SetLongName("Cy5(a.u.)");
	wks.Columns(7).SetLongName("Total(a.u.)");
	wks.Columns(8).SetLongName("FRET(E)");
	wks.Columns(9).SetLongName("Time|sel");
	wks.Columns(10).SetLongName("Eff|sel");
	wks.Columns(11).SetLongName("binCenter");
	wks.Columns(12).SetLongName("count");
	getValue(wks,1,&cy3);
	getValue(wks,2,&cy3bg);
	getValue(wks,3,&cy5);
	getValue(wks,4,&cy5bg);
	//=========================================================》recaculate data 
	newcy3 = cy3 - cy3bg;		//Cy3 deBackground
	newcy5 = cy5 - cy5bg;		//Cy5 deBackground
	temp = newcy3;
	newcy3 = newcy3 - 0.02*newcy5;//leakage
	newcy5 = newcy5 - 0.07*temp; //leakage
	Eff = newcy5/(newcy5+newcy3); //calculate FRET effiency
	total = newcy3 + newcy5+totalOffset;    //calculate total energy		
	setValue(wks,iCol3,newcy3);
	setValue(wks,iCol5,newcy5);
	setValue(wks,iTotal,total);
	setValue(wks,iColeff,Eff);  
	if(drawPic){
		DataRange drPlot;
		drPlot.Add(wks, 0, "X");
		drPlot.Add(wks, 7, "Y");
		drPlot.Add(wks, 6, "Y");
		drPlot.Add(wks, 5, "Y");
		DataRange drPlot1;
		drPlot1.Add(wks, 0, "X");
		drPlot1.Add(wks, 8, "Y");
		string wsName = wks.GetPage().GetLongName();
		string pstrFileName;
		string pstrExt;		 
		separate_file_name_ext(wsName, pstrFileName, pstrExt);
		foreach(GraphPage gps in Project.GraphPages){	 		 
			if( strcmp(gps.GetName() , pstrFileName+"_FRET")==0) gps.Destroy();
		}
		GraphPage gp;
		gp.Create("stack");
		gp.SetLongName(pstrFileName+"_FRET");
		int nNumYs = 2;
		while ( gp.Layers.Count() < nNumYs ){
			page_add_layer(gp, false, false, false, true,ADD_LAYER_INIT_SIZE_POS_MOVE_OFFSET, false, 0, LINK_STRAIGHT); 
		}
		GraphLayer gl = gp.Layers(0);
		int nPlot = gl.AddPlot(drPlot, IDM_PLOT_LINE); // return plot index
		GraphLayer glEff = gp.Layers(1);
		int nPlot1 = glEff.AddPlot(drPlot1, IDM_PLOT_LINE); // return plot index
		DataPlot dp = glEff.DataPlots(nPlot1);
		dp.SetColor(3);
		Tree  tr;
		int b =themes_tree_from_file(tr,fret_theme_path);
		if(0 == gl.UpdateThemeIDs(tr.Root) )
			gl.ApplyFormat(tr);
		if(0 == glEff.UpdateThemeIDs(tr.Root) )
			glEff.ApplyFormat(tr);
		legend_update(glEff, ALM_LONGNAME_UNITS);
		//legend_update(gl, ALM_LONGNAME_UNITS);
		if( nPlot >= 0 )
			gl.Rescale();	
		if( nPlot1 >= 0 )
			//glEff.Rescale();
			if(outPutPic){
				string fileName =  savePath+wsName+FRET_Ext;
				export_page_to_image( fileName, fileType, gp, 1024, 768, 24, 80,  false );
			}
	}
	printf("  OK!");
}
void FRET_Hist(double baseLine,double BinSize)
{
	Worksheet wks = Project.FindPage("Book2").Layers(0);
	vector<double> b,t,lb,lt,deltaFret,deltaR;
	getValue(wks,0,&b);
	getValue(wks,1,&t);
	lb = b;
	lt = t;
	deltaFret = b;
	deltaR = t;
	wks.Columns(0).SetLongName("baseLine");
	wks.Columns(1).SetLongName("target");
	wks.Columns(2).SetLongName("baseLineToOne");
	wks.Columns(3).SetLongName("targetToOne");
	wks.Columns(4).SetLongName("baseLineToDistance");
	wks.Columns(5).SetLongName("targetToDistance");
	wks.Columns(6).SetLongName("deltaFRET");
	wks.Columns(7).SetLongName("deltaR");
	wks.Columns(8).SetLongName("FRET");
	int len = b.GetSize();
	vector<double> fret(2*len);
	for(int i = 0;i<len;i++){
		if(b[i] >1) b[i] = 1;
		if(t[i] >1) t[i] = 1;
		deltaFret[i] = t[i] - b[i];
		lb[i] = 6*pow((1/b[i]-1),(1.0/6.0));
		lt[i] = 6*pow((1/t[i]-1),(1.0/6.0));
		deltaR[i] = lt[i] - lb[i];
		if(b[i]>baseLine)deltaR[i] = 0;
		if(deltaR[i]>0)deltaR[i] = 0;
		deltaR[i] = abs(deltaR[i]);
		fret[i] = b[i];
		fret[i+len] = t[i];
		if(b[i]>baseLine){
			fret[i] = 0;
			fret[i+len] = 0;
		}
	}
	setValue(wks,2,b);
	setValue(wks,3,t);
	setValue(wks,4,lb);
	setValue(wks,5,lt);
	setValue(wks,6,deltaFret);
	setValue(wks,7,deltaR);
	setValue(wks,8,fret);
	double nBinSize = (max(deltaR) - min(deltaR))/BinSize;
	vector vBinCenters(nBinSize),vAbsoluteCounts(nBinSize),vCumulativeCounts(nBinSize);//fret stage
	Histgram(nBinSize,deltaR,&vBinCenters,&vAbsoluteCounts ,&vCumulativeCounts);// do histgram
	setValue(wks,9,vBinCenters);
	setValue(wks,10,vAbsoluteCounts);
	string colName = "DeltaR";
	wks.Columns(9).SetLongName(colName+"|BinCenters");
	wks.Columns(10).SetLongName(colName+"|AbsoluteCounts");		
	DataRange drPlot;
	drPlot.Add(wks,9,"X");
	drPlot.Add(wks,10,"Y");
	drPlot.SetData(&vAbsoluteCounts,&vBinCenters); 
	foreach(GraphPage gps in Project.GraphPages){
		if( strcmp(gps.GetName() , colName+"_Histgram")==0) gps.Destroy();
	}
	GraphPage gp;
	gp.Create("stack");
	gp.SetName(colName+"_Histgram");
	int nNumYs = 1;		
	while ( gp.Layers.Count() < nNumYs ){
		page_add_layer(gp, false, false, false, true,ADD_LAYER_INIT_SIZE_POS_MOVE_OFFSET, false, 0, LINK_STRAIGHT); 
	}
	GraphLayer glHist = gp.Layers(0);
	int nPlot = glHist.AddPlot(drPlot, IDM_PLOT_COLUMN); // return plot index
	DataPlot dpHist = glHist.DataPlots(nPlot);
	dpHist.SetColor(2);
	Tree  trHist;
	int a =themes_tree_from_file(trHist,hist_theme_path1);
	if(0 == glHist.UpdateThemeIDs(trHist.Root) )
		glHist.ApplyFormat(trHist);
	if( nPlot >= 0 )
		glHist.Rescale();	
}


