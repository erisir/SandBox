from PyQt5 import  QtGui
from matplotlib.backends.backend_qt5agg import  FigureCanvasQTAgg as FigureCanvas
from matplotlib.backends.backend_qt5agg import NavigationToolbar2QT as NavigationToolbar
from matplotlib.figure import Figure
import numpy as np
from array import array
import time
import random
import threading
from datetime import datetime
from matplotlib.dates import  date2num, MinuteLocator, SecondLocator, DateFormatter
from PyQt5.QtGui import *  
from PyQt5.QtGui import *  
from PyQt5.QtCore import *  
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  * 

X_MINUTES = 0.1
Y_MAX = 3200
Y_MIN = 1
INTERVAL = 0.01
MAXCOUNTER = int(X_MINUTES * 60*2/ 0.1)

class MplCanvas(FigureCanvas):
    def __init__(self):
        self.fig = Figure()
        self.ax = self.fig.add_subplot(211)
        self.ax1 = self.fig.add_subplot(212)
        FigureCanvas.__init__(self, self.fig)
        FigureCanvas.setSizePolicy(self, QSizePolicy.Expanding, QSizePolicy.Expanding)
        FigureCanvas.updateGeometry(self)
        self.curveObj = None # draw object
        self.curveObj1= None # draw object
        self.curveObj2= None # draw object
        
        self.initFigureUI(self.ax,"time (sec)",'Voltage(mv)')
        self.initFigureUI(self.ax1,"time (sec)",'OutPut(pwm)')
        
    def initFigureUI(self,ax,xlabel,ylabel):   
        ax.set_xlabel(xlabel)
        ax.set_ylabel(ylabel)
        ax.legend()
        ax.set_ylim(Y_MIN,Y_MAX)
        #self.ax.xaxis.set_major_locator(MinuteLocator())  # every minute is a major locator
        ax.xaxis.set_major_locator(SecondLocator([0,5,10,15,20,25,30,35,40,45,50,55]))
        ax.xaxis.set_minor_locator(SecondLocator())
        #self.ax.xaxis.set_minor_locator(SecondLocator([10,20,30,40,50])) # every 10 second is a minor locator
        ax.xaxis.set_major_formatter( DateFormatter('%M:%S') ) #tick label formatter
        
        
    def plot(self, datax, datay,datay1,datay2):
        if self.curveObj is None:
            #create draw object once
            self.curveObj, = self.ax.plot_date(np.array(datax), np.array(datay),'b-')
            self.curveObj1, = self.ax.plot_date(np.array(datax), np.array(datay),'r-')
            self.curveObj2, = self.ax1.plot_date(np.array(datax), np.array(datay),'r-')

        else:
            #update data of draw object
            npx = np.array(datax)
            npy =  np.array(datay)
            npy1 =  np.array(datay1)
            npy2 =  np.array(datay2)
           
            meany = np.mean(datay2)
            std = np.std(datay2)*10
            ystart = meany-std
            if ystart <0:
                ystart = 0
            yend = meany+std
            
            self.curveObj.set_data(npx,npy)
            self.curveObj1.set_data(npx, npy1)
            self.curveObj2.set_data(npx, npy2)
            #update limit of X axis,to make sure it can move
            self.ax.set_xlim(datax[0],datax[-1])
            self.ax1.set_xlim(datax[0],datax[-1])
            self.ax1.set_ylim(ystart,yend)
            
        ticklabels = self.ax.xaxis.get_ticklabels()
        for tick in ticklabels:
            tick.set_rotation(5)
        self.draw()
       
class  MyDynamicMplCanvas(QWidget):
    Interception=655
    Slope=25
    startTime =0
    def __init__(self , parent =None):
        QWidget.__init__(self, parent)
        self.canvas = MplCanvas()
        self.vbl = QVBoxLayout()
        self.ntb = NavigationToolbar(self.canvas, parent)
        self.vbl.addWidget(self.ntb)
        self.vbl.addWidget(self.canvas)
        self.setLayout(self.vbl)
        self.dataX= []
        self.dataY= []
        self.dataY1 = []
        self.dataY2 = []
        self.initDataGenerator()
        #self.startTime = date2num(datetime.now())
        
    def InitGUI(self,action,getpoint,getpointbar):
        self.UIAction = action
        self.getpoint=getpoint
        self.getpointbar = getpointbar
        
    def startPlot(self):
        self.UIAction.thirdUIControl.startPlot.setEnabled(False)
        self.UIAction.thirdUIControl.pausePlot.setEnabled(True)
        self.UIAction.ConnectTest()
        #self.startTime = date2num(datetime.now())
        self.__generating = True
       
    def pausePlot(self):
        self.UIAction.thirdUIControl.startPlot.setEnabled(True)
        self.UIAction.thirdUIControl.pausePlot.setEnabled(False)
        #self.dataX.clear()
        #self.dataY.clear()
        self.__generating = False
        self.UIAction.CloseComm()

    def initDataGenerator(self):
        self.__generating=False
        self.__exit = False
        self.tData = threading.Thread(name = "dataGenerator",target = self.generateData)
        self.tData.start()
        
    def releasePlot(self):
        self.__exit  = True
        self.tData.join()
    
    def generateData(self):
        counter=0
        while(True):
            if self.__exit:
                break
            if self.__generating:
                newTime= date2num(datetime.now())
                newData = self.UIAction.GetPlotData()
                if(newData == None):
                    continue
                try:                            
                    self.dataX.append(newTime)
                    self.dataY.append(newData[0])
                    self.dataY1.append(newData[1])
                    self.dataY2.append(newData[2])
                    self.getpoint.setProperty("value", newData[0])
                    self.canvas.plot(self.dataX, self.dataY,self.dataY1,self.dataY2)   
                    if counter >= MAXCOUNTER:
                        self.dataX.pop(0)
                        self.dataY.pop(0) 
                        self.dataY1.pop(0) 
                        self.dataY2.pop(0) 
                    else:
                        counter+=1
                except:
                    self.UIAction.errorMessage("绘图出错")
                    continue 
            time.sleep(INTERVAL)