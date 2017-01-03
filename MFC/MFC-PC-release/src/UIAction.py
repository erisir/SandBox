
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
        
        self.comm = None
 
    def SendCommand(self,cmd):
        buf ='@'+cmd+'X'+'X'+'a'
        self.log("发送字符串\t"+buf)
        try:
            self.comm.write(buf.encode('ascii')) 
        except:
            self.errorMessage("发送命令失败")
    def SendDataCommand(self,cmd,value):
        buf ='@'+cmd+str(((value/256)%256))+str((value%256))
        try:
            self.comm.write(buf.encode('ascii')) 
        except:
            self.errorMessage("发送命令失败")
    def PWMOpen (self):
        self.SendCommand(self._U_SetTOpen)
        self.log("PWMOpen")
    def PWMClose (self):
        self.SendCommand(self._U_SetTClose)
        self.log("PWMClose")
    def PWMPID (self):
        self.SendCommand(self._U_SetTPID)
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
        return [self.GetShowValue(votage[0]),self.GetShowValue(votage[1])]

    def GetVotage(self):
        try:
            self.SendCommand(self._U_GetVotage)
            time.sleep(0.1)
            res=self.read()

            if res is None or len(res)<3:
                self.errorMessage("读取位置信息返回长度出错")
                #return None
                return [random.randint(660, 3200),random.randint(660, 3200)]
            temp = str(res[2:]).split(',')
            if len(temp)==2:
                return [float(temp[0]),float(temp[1])]
            else:
                self.errorMessage("读取位置信息返回数据出错")
                # return None
                return [random.randint(660, 3200),random.randint(660, 3200)]
        except:
            self.logMessage("读取串口出错")            
            return [random.randint(660, 3200),random.randint(660, 3200)]
        
        
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
         
        self.SendCommand(self._U_GetVotage)
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
        pass
        #print('-'*10+str)
    def errorMessage(self,str):
        print('!'*10+str)
    def warnningMessage(self,str):
        print('?'*10+str)
         
