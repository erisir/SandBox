from PyQt5.QtGui import *  
from PyQt5.QtCore import *  
import UIComm,UIControl,UIDetail,UIOther
import sys  
  
class TestDialog(QDialog):  
    def __init__(self,parent=None):  
        super(TestDialog,self).__init__(parent)  
          
        firstUIComm=UIComm.Ui_Dialog()  
        secondUIDetail=UIDetail.Ui_Dialog()  
        thirdUIControl=UIControl.Ui_Dialog()  
        FourUIOther=UIOther.Ui_Dialog()  
          
        tabWidget=QTabWidget(self)  
        w1=QWidget()  
        firstUIComm.setupUi(w1)  
        w2=QWidget()  
        secondUIDetail.setupUi(w2)  
        w3=QWidget()  
        thirdUIControl.setupUi(w3)  
        w4=QWidget()  
        FourUIOther.setupUi(w4)  
  
        tabWidget.addTab(w1,"通讯信息")  
        tabWidget.addTab(w2,"产品信息")  
        tabWidget.addTab(w3,"控制信息")  
        tabWidget.addTab(w4,"其他项目")  
        tabWidget.resize(520,480)  
  
        #self.connect(firstUi.pushButton,SIGNAL("clicked()"),self.slotChild)  
       #self.connect(secondUi.closePushButton,SIGNAL("clicked()"),self,SLOT("reject()"))  
        
     
          
app=QApplication(sys.argv)  
dialog=TestDialog()  
dialog.show()  
app.exec_()  