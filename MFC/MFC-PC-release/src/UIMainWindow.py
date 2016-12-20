
from PyQt5.QtGui import *  
from PyQt5.QtCore import *  
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  *

import UIComm,UIControl,UIDetail,UIOther
import sys  
import UIAction  

class UIMainWindow(QDialog):  
    def __init__(self,parent=None):  
        super(UIMainWindow,self).__init__(parent)  
          
        self.firstUIComm=UIComm.Ui_Dialog()  
        self.secondUIDetail=UIDetail.Ui_Dialog()  
        self.thirdUIControl=UIControl.Ui_Dialog()
        self.fourUIOther=UIOther.Ui_Dialog()  
         
 
        tabWidget=QtWidgets.QTabWidget(self)  
        w1=QWidget()  
        self.firstUIComm.setupUi(w1)  
        w2=QWidget()  
        self.secondUIDetail.setupUi(w2)  
        w3=QWidget()  
        self.thirdUIControl.setupUi(w3)  
        w4=QWidget()  
        self.fourUIOther.setupUi(w4)  
  
        tabWidget.addTab(w1,"通讯信息")  
        tabWidget.addTab(w2,"产品信息")  
        tabWidget.addTab(w3,"控制信息")  
        tabWidget.addTab(w4,"其他项目")  
        tabWidget.resize(520,480)
        
        self.ConnectEvent()
        UIAction.CommInit("COM4",119200)
        
    def ConnectEvent(self):
        self.thirdUIControl.PWMOpen.clicked.connect(UIAction.UIAction.PWMOpen)
        self.thirdUIControl.PWMClose.clicked.connect(UIAction.UIAction.PWMClose)
        self.thirdUIControl.PWMPID.clicked.connect(UIAction.UIAction.PWMPID)
        self.thirdUIControl.CtrlMode_Dig.clicked.connect(UIAction.UIAction.CtrlMode_Dig)
        self.thirdUIControl.CtrlMode_Vot.clicked.connect(UIAction.UIAction.CtrlMode_Vot)
        self.thirdUIControl.CtrlMode_Cur.clicked.connect(UIAction.UIAction.CtrlMode_Cur)
        self.thirdUIControl.CtrlMode_Der.clicked.connect(UIAction.UIAction.CtrlMode_Der)
        self.thirdUIControl.ShowUnit_FS.clicked.connect(UIAction.UIAction.ShowUnit_FS)
        self.thirdUIControl.ShowUnit_sccm.clicked.connect(UIAction.UIAction.ShowUnit_sccm)
        self.thirdUIControl.ShowUnit_slm.clicked.connect(UIAction.UIAction.ShowUnit_slm)
        self.thirdUIControl.ShowUnit_V.clicked.connect(UIAction.UIAction.ShowUnit_V)
        self.thirdUIControl.ShowUnit_mv.clicked.connect(UIAction.UIAction.ShowUnit_mv)
        self.thirdUIControl.mplCanvas.setGetPoint(self.thirdUIControl.GetPoint,self.thirdUIControl.GetPointBar)

 

     
          
app=QApplication (sys.argv)  
dialog=UIMainWindow()  
dialog.show()  
app.exec_()  