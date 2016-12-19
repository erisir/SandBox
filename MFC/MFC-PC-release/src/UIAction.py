
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  *
import UIComm,UIControl,UIDetail,UIOther
import serial  #pip install pyserial
#PWMOpen,PWMClose,PWMPID,CtrlMode_Dig,CtrlMode_Vot,CtrlMode_Cur,CtrlMode_Der,ShowUnit_FS,ShowUnit_sccm,ShowUnit_slm,ShowUnit_V,ShowUnit_mv
class UIAction():     
    def PWMOpen (self):
        print("PWMOpen")
    def PWMClose (self):
        print("PWMClose")
    def PWMPID (self):
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
    def CommInit(self,commName,boteli): 
        t = serial.Serial(commName,boteli)  
        n = t.write('you are my world')  
        str = t.read(n)  
 
 
