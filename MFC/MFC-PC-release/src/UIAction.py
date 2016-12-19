
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  *
 
 
class MyGroupBox(QGroupBox):
    def __init__(self, parent=None, width=5, height=4, dpi=100):
        QGroupBox.__init__(self)
        self.setParent(parent)
        QGroupBox.setSizePolicy(self,
                                   QSizePolicy.Expanding,
                                   QSizePolicy.Expanding)
        QGroupBox.updateGeometry(self)
        QGroupBox.setCheckable(self,False)
    def changeEvent (self, QEvent):
        print(QEvent)
class BoxCtrl(QGroupBox):
    def __init__(self, *args, **kwargs):
        MyGroupBox.__init__(self, *args, **kwargs)


class BoxMode(MyGroupBox):
    def __init__(self, *args, **kwargs):
        self= MyGroupBox.__init__(self, *args, **kwargs)

class BoxUnit(MyGroupBox):
    def __init__(self, *args, **kwargs):
        self= MyGroupBox.__init__(self, *args, **kwargs)
