
from PyQt5.QtGui import *  
from PyQt5.QtCore import *  
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  *
import MainUI
import sys  
import ParseAirAsia
import datetime
 
 

class UIMainWindow(QDialog):  
    daysAround = 2;
    def __init__(self,parent=None):  
        super(UIMainWindow,self).__init__(parent)  
          
        self.firstMainUI=MainUI.Ui_Dialog()      
        QtWidgets.QWidget
 
        tabWidget=QtWidgets.QTabWidget(self)  
        w1=QWidget()  
        self.firstMainUI.setupUi(w1)  
  
        tabWidget.addTab(w1,"亚航查询")  
        tabWidget.resize(610,450)
        self.firstMainUI.DepTable.setAttribute(Qt.WA_TransparentForMouseEvents,True)
        self.firstMainUI.RetTable.setAttribute(Qt.WA_TransparentForMouseEvents,True)
        self.firstMainUI.SearchButton.clicked.connect(self.SearchStart)
        
    def SearchStart(self):
        depCityName = self.firstMainUI.DepCity.currentText()
        arrCityName = self.firstMainUI.ArrCity.currentText()
        ADT =  self.firstMainUI.ADT.value()
        timeout = self.firstMainUI.Timeout.value()
        daysAround = self.firstMainUI.Around.value()
        depDate = self.firstMainUI.DepCalendar.selectedDate()
        if depDate == None:
            return
        retDate = self.firstMainUI.RetCalendar.selectedDate()
        depInfo = ParseAirAsia.ParseAirAsia(depCityName,arrCityName,depDate,ADT,daysAround,timeout)
        retInfo = ParseAirAsia.ParseAirAsia(arrCityName,depCityName,retDate,ADT,daysAround,timeout)
        
        minDepFare = depInfo.start()
        minRetFare =retInfo.start()

        self.setTableTextByDate(self.firstMainUI.DepTable,depDate,minDepFare)
        self.setTableTextByDate(self.firstMainUI.RetTable,retDate,minRetFare)
        
    def setTableTextByDate(self,table,date,fare):
        depDate = datetime.date(date.year(),date.month(),date.day()) 
        counter = 0
        for x in range(-1*self.daysAround,self.daysAround+1):
            depDateTemp = depDate + datetime.timedelta(days=x)
            ret = self.getItemByDate(QDate(depDateTemp.year,depDateTemp.month,depDateTemp.day))
            row = ret[0]
            column = ret[1]
            item = table.item(row,column)
            item.setText(str(fare[counter]))
            counter = counter+1
 
    def getItemByDate(self,date):
        column= date.dayOfWeek()-1
        dateTemp = QDate(date.year(), date.month(), 1)
        row= date.weekNumber()[0] -dateTemp .weekNumber()[0]
        if dateTemp.dayOfWeek()==1:
            row= date.weekNumber()[0] -dateTemp .weekNumber()[0]+1
 
        return [row,column]
 
          
app=QApplication (sys.argv)  
dialog=UIMainWindow()  
dialog.show()  
app.exec_()  