
from PyQt5.QtGui import *  
from PyQt5.QtCore import *  
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  *

import UIComm,UIControl,UIDetail,UIOther,UIControlProf
import sys  
from UIAction import   UIAction

class UIMainWindow(QDialog):  
    def __init__(self,parent=None):  
        super(UIMainWindow,self).__init__(parent)  
          
        self.firstUIComm=UIComm.Ui_Dialog()  
        self.secondUIDetail=UIDetail.Ui_Dialog()  
        self.thirdUIControl=UIControl.Ui_Dialog()
        self.fourUIOther=UIOther.Ui_Dialog()  
        
        self.UIControlProf=UIControlProf.Ui_Dialog()
        
        QtWidgets.QWidget
 
        tabWidget=QtWidgets.QTabWidget(self)  
        w1=QWidget()  
        self.firstUIComm.setupUi(w1)  
        w2=QWidget()  
        self.secondUIDetail.setupUi(w2)  
        w3=QWidget()  
        self.thirdUIControl.setupUi(w3)  
        w4=QWidget()  
        self.fourUIOther.setupUi(w4)  
      
        
  
        tabWidget.addTab(w3,"通讯信息")  
        tabWidget.addTab(w2,"产品信息")  
        tabWidget.addTab(w1,"控制信息")  
        tabWidget.addTab(w4,"其他项目")  
        tabWidget.resize(520,560)
        
        self.uiAction = UIAction(self.firstUIComm,self.secondUIDetail,self.thirdUIControl,self.fourUIOther)
        self.ConnectEvent()
        #QThread.sleep(3)
        
    def ConnectEvent(self):
        self.thirdUIControl.PWMOpen.clicked.connect(self.uiAction.PWMOpen)
        self.thirdUIControl.PWMClose.clicked.connect(self.uiAction.PWMClose)
        self.thirdUIControl.PWMPID.clicked.connect(self.uiAction.PWMPID)
        self.thirdUIControl.CtrlMode_Dig.clicked.connect(self.uiAction.CtrlMode_Dig)
        self.thirdUIControl.CtrlMode_Vot.clicked.connect(self.uiAction.CtrlMode_Vot)
        self.thirdUIControl.CtrlMode_Cur.clicked.connect(self.uiAction.CtrlMode_Cur)
        self.thirdUIControl.CtrlMode_Der.clicked.connect(self.uiAction.CtrlMode_Der)
        self.thirdUIControl.ShowUnit_FS.clicked.connect(self.uiAction.ShowUnit_FS)
        self.thirdUIControl.ShowUnit_sccm.clicked.connect(self.uiAction.ShowUnit_sccm)
        self.thirdUIControl.ShowUnit_slm.clicked.connect(self.uiAction.ShowUnit_slm)
        self.thirdUIControl.ShowUnit_V.clicked.connect(self.uiAction.ShowUnit_V)
        self.thirdUIControl.ShowUnit_mv.clicked.connect(self.uiAction.ShowUnit_mv)
        self.thirdUIControl.mplCanvas.InitGUI(self.uiAction,self.thirdUIControl.GetPoint,self.thirdUIControl.GetPointBar)
        self.thirdUIControl.startPlot.clicked.connect(self.thirdUIControl.mplCanvas.startPlot)
        self.thirdUIControl.pausePlot.setEnabled(False)
        self.thirdUIControl.pausePlot.clicked.connect(self.thirdUIControl.mplCanvas.pausePlot)
        self.thirdUIControl.Clear.clicked.connect(self.Clear)
        
        self.firstUIComm.connectTest.clicked.connect(self.uiAction.ConnectTest)
        self.fourUIOther.ProfControl.clicked.connect(self.showProfControlDlg)
 
    def Clear(self):
        self.showProfControlDlg()
        pass
     
    def showProfControlDlg(self):  
        dlg=QDialog()  
        self.UIControlProf.setupUi(dlg)  
        dlg.exec_()  
          
app=QApplication (sys.argv)  
#splash=QSplashScreen(QPixmap("../image/logo.png"))  
#splash.show()  
#app.processEvents()  
dialog=UIMainWindow()  
dialog.show()  
#splash.finish(dialog)  
app.exec_()  