
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  *
import UIComm,UIControl,UIDetail,UIOther
import serial  #pip install pyserial
import sys
import time
import random
import binascii,encodings; 
from sympy.strategies.core import switch
#PWMOpen,PWMClose,PWMPID,CtrlMode_Dig,CtrlMode_Vot,CtrlMode_Cur,CtrlMode_Der,ShowUnit_FS,ShowUnit_sccm,ShowUnit_slm,ShowUnit_V,ShowUnit_mv
class UIAction():     
    firstUIComm = None
    secondUIDetail= None
    thirdUIControl= None
    fourUIOther= None
    isDeviceReady = False
    
    _U_SetVotage     = '0';
    _U_SetPTerm      = '1';
    _U_SetITerm      = '2';
    _U_SetDTerm        = '3';
    _U_SetDura       = '4';
    _U_SetPWMVal     = '5';
    _U_GetVotage     = '6';
    _U_SetTClose     = '7';
    _U_SetTOpen      = '8';
    _U_SetTPID       = '9';
    _U_SetVotageTimes= 'a';
    _U_SetPIDMode= 'b';
    _U_SetPIDPeriod = 'c';
    _U_SetTIM4Prescaler = 'd';
    
    showUnit = "mv"
    
    def __init__(self,  firstUIComm, secondUIDetail, thirdUIControl, fourUIOther):
        self.firstUIComm = firstUIComm
        self.secondUIDetail = secondUIDetail
        self.thirdUIControl = thirdUIControl
        self.fourUIOther = fourUIOther
        self.pidPara =["Kp","Ki","Kd","DeadZone","SetPoint","Output","LastError","PrevError","voltage","SumErr"];
        self.comm = None

    def SendDataCommand(self,cmd,value):
        High = int(((value/256)%256))
        Low = int((value%256))
        buf = bytes([ord('@'),ord(cmd),High,Low])
        #print(buf)
        #self.log("发送字符串\t"buf)  
        #print("发送字符串")      
        #print(buf)
        try:
            self.comm.write(buf) 
        except:
            self.errorMessage("发送命令失败")
    def PWMOpen (self):
        self.SendDataCommand(self._U_SetTOpen,0)
        self.log("PWMOpen")
    def PWMClose (self):
        self.SendDataCommand(self._U_SetTClose,0)
        self.log("PWMClose")
    def PWMPID (self):
        self.SendDataCommand(self._U_SetTPID,0)
        self.log("PWMPID")
    def CtrlMode_Dig (self):
        self.log("CtrlMode_Dig")
    def CtrlMode_Vot (self):
        self.log("CtrlMode_Vot")
    def CtrlMode_Cur (self):
        self.log("CtrlMode_Cur")
    def CtrlMode_Der (self):
        self.log("CtrlMode_Der")
    def ShowUnit_FS (self):
        self.showUnit = "FS"
        self.log("ShowUnit_FS")
    def ShowUnit_sccm (self):
        self.showUnit = "sccm"
        self.log("ShowUnit_sccm")
    def ShowUnit_slm (self):
        self.showUnit = "slm"
        self.log("ShowUnit_slm")
    def ShowUnit_V (self):
        self.showUnit = "V"
        self.log("ShowUnit_V")
    def ShowUnit_mv (self):
        self.showUnit = "mv"
        self.log("ShowUnit_mv")
    
    def Set_PID_AutoInc(self):
        self.SendDataCommand(self._U_SetPIDMode,0)
    
    def Set_PID_ManuInc(self):
        self.SendDataCommand(self._U_SetPIDMode,1)
    
    def PID_Kp_valueChanged(self,value):
        self.SendDataCommand(self._U_SetPTerm,value)
    
    def PID_Ki_valueChanged(self,value):
        self.SendDataCommand(self._U_SetITerm,value)
        
    def PID_Kd_valueChanged(self,value):
        self.SendDataCommand(self._U_SetDTerm,value)
        
    def PID_Inteval_valueChanged(self,value):
        self.SendDataCommand(self._U_SetPIDPeriod,value)
        
    def PID_SetPoint_valueChanged(self,value):
        self.SendDataCommand(self._U_SetVotage,value)
        
    def SmoothWindow_valueChanged(self,value):
        self.SendDataCommand(self._U_SetVotageTimes,value)
        
    def Prescaler_valueChanged(self,value):
        self.SendDataCommand(self._U_SetTIM4Prescaler,value)
        
    def PWMValue_valueChanged(self,value):
        self.SendDataCommand(self._U_SetPWMVal,value)
        
    def PWMRate_valueChanged(self,value):
        self.PWMRate = value
    def Slope_valueChanged(self,value):
        self.Slope=value
    def Interception_valueChanged(self,value):
        self.Interception=value

        
    def VotageToFlow(self,votage):
        Flow = (votage-self.Interception)/self.Slope
        if Flow <0:
            Flow = 0
        return Flow
    def FlowToVotage(self,flow):
        Votage = self.Slope*flow+self.Interception
        if Votage<0:
            Votage = 0
        return Votage
    
    def GetShowValue(self,votage):
        if self.showUnit == "FS":
            return votage/3200
        if self.showUnit == "sccm":
            return votage/320
        if self.showUnit == "slm":
            return votage/32
        if self.showUnit == "V":
            return votage/1000
        if self.showUnit == "mv":
            return votage
    def GetPlotData(self):
        votage = self.GetVotage()
        if votage is None:
            return None
        return [self.GetShowValue(votage[0]),self.GetShowValue(votage[1]),votage[2]]

    def GetVotage(self):
        try:
            ouput = 0
            if self.thirdUIControl.PWMPID.isChecked():
                self.SendDataCommand(self._U_SetDura,0)
                time.sleep(0.1)
                res=self.read()
                ret = res.split(",")
                str = ""
                i=0                
                for x in ret:                                    
                    str += self.pidPara[i]+":"+x+" , "
                    if i == 5:
                        ouput = int(x)
                    i = i+1
                print(str)
                
            self.SendDataCommand(self._U_GetVotage,0)
            time.sleep(0.1)
            res=self.read()
            return self.getRandom() 
            if res is None or len(res)<3:
                self.errorMessage("读取位置信息返回长度出错")
                return self.getRandom() 
            temp = res[2:].split(',')
            if len(temp)==2:
                return [float(temp[0]),float(temp[1]),float(ouput)]
            else:
                self.errorMessage("读取位置信息返回数据出错")
                return self.getRandom()
        except:
            self.logMessage("读取串口出错")            
            return self.getRandom()
        
    def getRandom(self):
            start = 2000
            end = 2050
            return [random.randint(start, end),random.randint(start, end),random.randint(start, end)] 
    
    def read(self,terminator='\n', size=None):
        try:
            res=self.comm.read_until()
            if(len(res)>0):
                return res.decode('ascii').replace('\n', '', 1)
            else:
                return None
        except:
            self.errorMessage("读取串口信息失败")
        
    def ConnectTest(self):
        commName = self.firstUIComm.CommName.currentText()
        Baudrate = self.firstUIComm.Baudrate.currentText()   
        try:   
            self.comm = serial.Serial(commName,int(Baudrate))              
        except:
            self.errorMessage("串口"+commName+"被其他程序占用")
            return
         
        self.SendDataCommand(self._U_GetVotage,0)
        res=self.read() 
        if res is not None and len(res)>2 and str(res)[:2] == "@P" :
            self.isDeviceReady = True
            self.logMessage("连接成功")
        else:
            self.isDeviceReady = False
            self.errorMessage("连接失败，请检查串口参数")
        #self.comm.close()
    def CloseComm(self):
        if  self.comm is None:
            pass
        else:
            print(self.comm)
            self.comm.close()
    def logMessage(self,str):
        print('-'*10+str)
    def log(self,str):
        #pass
        print('-'*10+str)
    def errorMessage(self,str):
        print('!'*10+str)
    def warnningMessage(self,str):
        print('?'*10+str)
         
