
from PyQt5.QtGui import *  
from PyQt5.QtCore import *  
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  *
import UIComm,UIControl,UIDetail,UIOther,UIControlProf
import sys  
import time
from UIAction import   UIAction
class Form(QDialog):
    def __init__(self, parent=None):
        super(Form, self).__init__(parent)
        self.browser = QTextBrowser()
        self.setWindowTitle('Just a dialog')
        self.lineedit = QLineEdit("Write something and press Enter")
        self.lineedit.selectAll()
        layout = QVBoxLayout()
        layout.addWidget(self.browser)
        layout.addWidget(self.lineedit)
        self.setLayout(layout)
        self.lineedit.setFocus()
    def update_ui(self):
        self.browser.append(self.lineedit.text())
if __name__ == "__main__":
    import sys, time
    app = QApplication(sys.argv)
    # Create and display the splash screen
    splash_pix = QPixmap('../image/logo.png')

    splash = QSplashScreen(splash_pix, Qt.WindowStaysOnTopHint)
    # adding progress bar
    
    height= splash_pix.height()*0.85
    width = splash_pix.width()*0.8
    progressBar = QProgressBar(splash)
    progressBar.setGeometry(QtCore.QRect(60, height, width, 10))
    progressBar.setProperty("value", 24)
    progressBar.setObjectName("progressBar")
    
    tipLabel = QtWidgets.QLabel(splash)
    tipLabel.setGeometry(QtCore.QRect(60, height-30, width, 20))
    tipLabel.setObjectName("label")
    tipLabel.setText( "发的说法")
    splash.setMask(splash_pix.mask())
    splash.show()
    for i in range(0, 100):
        progressBar.setValue(i)
        t = time.time()
        while time.time() < t + 0.1:
            app.processEvents()
    # Simulate something that takes time
    time.sleep(2)
    form = Form()
    form.show()
    splash.finish(form)
    app.exec_()