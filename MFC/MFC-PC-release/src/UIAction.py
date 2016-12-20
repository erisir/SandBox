
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  *
import UIComm,UIControl,UIDetail,UIOther
import serial  #pip install pyserial
import sys
import time
import random
import binascii,encodings; 
#PWMOpen,PWMClose,PWMPID,CtrlMode_Dig,CtrlMode_Vot,CtrlMode_Cur,CtrlMode_Der,ShowUnit_FS,ShowUnit_sccm,ShowUnit_slm,ShowUnit_V,ShowUnit_mv
class UIAction():     
    firstUIComm = None
    secondUIDetail= None
    thirdUIControl= None
    fourUIOther= None
    
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
    
    def __init__(self,  firstUIComm, secondUIDetail, thirdUIControl, fourUIOther):
        self.firstUIComm = firstUIComm
        self.secondUIDetail = secondUIDetail
        self.thirdUIControl = thirdUIControl
        self.fourUIOther = fourUIOther
 
    def SendCommand(self,cmd):
        buf ='@'+cmd+'X'+'X'
        self.comm.write(buf.encode('ascii')) 
    def SendDataCommand(self,cmd,value):
        buf ='@'+cmd+str(((value/256)%256))+str((value%256))
        self.comm.write(buf.encode('ascii')) 
    def PWMOpen (self):
        self.SendCommand(self._U_SetTOpen)
        print("PWMOpen")
    def PWMClose (self):
        self.SendCommand(self._U_SetTClose)
        print("PWMClose")
    def PWMPID (self):
        self.SendCommand(self._U_SetTPID)
        print("PWMPID")
    def CtrlMode_Dig (self):
        print("CtrlMode_Dig")
    def CtrlMode_Vot (self):
        print("CtrlMode_Vot")
    def CtrlMode_Cur (self):
        print("CtrlMode_Cur")
    def CtrlMode_Der (self):
        print("CtrlMode_Der")
    def ShowUnit_FS (self):
        print("ShowUnit_FS")
    def ShowUnit_sccm (self):
        print("ShowUnit_sccm")
    def ShowUnit_slm (self):
        print("ShowUnit_slm")
    def ShowUnit_V (self):
        print("ShowUnit_V")
    def ShowUnit_mv (self):
        print("ShowUnit_mv")
    def GetVotage(self):
        try:
            self.SendCommand(self._U_GetVotage)
            time.sleep(2)
            res=self.comm.readall()
        except:
            print("读取串口出错")
        return str(random.randint(0, 100)+1000)#res
        
    def ConnectTest(self):
        commName = self.firstUIComm.CommName.currentText()
        Baudrate = self.firstUIComm.Baudrate.currentText()   
        try:   
            self.comm = serial.Serial(commName,int(Baudrate))   
        except:
            print("串口"+commName+"已打开")
 
 
        self.comm.write("this is a test msg".encode('ascii'))
        
 
    
