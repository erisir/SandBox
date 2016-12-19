# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'UIControl.ui'
#
# Created: Sun Dec 18 16:57:39 2016
#      by: PyQt4 UI code generator 4.10.2
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui

try:
    _fromUtf8 = QtCore.QString.fromUtf8
except AttributeError:
    def _fromUtf8(s):
        return s

try:
    _encoding = QtGui.QApplication.UnicodeUTF8
    def _translate(context, text, disambig):
        return QtGui.QApplication.translate(context, text, disambig, _encoding)
except AttributeError:
    def _translate(context, text, disambig):
        return QtGui.QApplication.translate(context, text, disambig)

class Ui_Dialog(object):
    def setupUi(self, Dialog):
        Dialog.setObjectName(_fromUtf8("Dialog"))
        Dialog.resize(520, 439)
        self.groupBox = QtGui.QGroupBox(Dialog)
        self.groupBox.setGeometry(QtCore.QRect(0, 10, 120, 80))
        self.groupBox.setObjectName(_fromUtf8("groupBox"))
        self.radioButton = QtGui.QRadioButton(self.groupBox)
        self.radioButton.setGeometry(QtCore.QRect(10, 20, 89, 16))
        self.radioButton.setObjectName(_fromUtf8("radioButton"))
        self.radioButton_2 = QtGui.QRadioButton(self.groupBox)
        self.radioButton_2.setGeometry(QtCore.QRect(10, 40, 89, 16))
        self.radioButton_2.setObjectName(_fromUtf8("radioButton_2"))
        self.radioButton_3 = QtGui.QRadioButton(self.groupBox)
        self.radioButton_3.setGeometry(QtCore.QRect(10, 60, 89, 16))
        self.radioButton_3.setObjectName(_fromUtf8("radioButton_3"))
        self.groupBox_2 = QtGui.QGroupBox(Dialog)
        self.groupBox_2.setGeometry(QtCore.QRect(0, 100, 120, 101))
        self.groupBox_2.setObjectName(_fromUtf8("groupBox_2"))
        self.radioButton_4 = QtGui.QRadioButton(self.groupBox_2)
        self.radioButton_4.setGeometry(QtCore.QRect(10, 20, 89, 16))
        self.radioButton_4.setObjectName(_fromUtf8("radioButton_4"))
        self.radioButton_5 = QtGui.QRadioButton(self.groupBox_2)
        self.radioButton_5.setGeometry(QtCore.QRect(10, 40, 89, 16))
        self.radioButton_5.setObjectName(_fromUtf8("radioButton_5"))
        self.radioButton_6 = QtGui.QRadioButton(self.groupBox_2)
        self.radioButton_6.setGeometry(QtCore.QRect(10, 60, 89, 16))
        self.radioButton_6.setObjectName(_fromUtf8("radioButton_6"))
        self.radioButton_7 = QtGui.QRadioButton(self.groupBox_2)
        self.radioButton_7.setGeometry(QtCore.QRect(10, 80, 89, 16))
        self.radioButton_7.setObjectName(_fromUtf8("radioButton_7"))
        self.groupBox_3 = QtGui.QGroupBox(Dialog)
        self.groupBox_3.setGeometry(QtCore.QRect(130, 17, 361, 181))
        self.groupBox_3.setTitle(_fromUtf8(""))
        self.groupBox_3.setObjectName(_fromUtf8("groupBox_3"))
        self.progressBar = QtGui.QProgressBar(self.groupBox_3)
        self.progressBar.setGeometry(QtCore.QRect(0, 0, 20, 161))
        self.progressBar.setLayoutDirection(QtCore.Qt.LeftToRight)
        self.progressBar.setProperty("value", 24)
        self.progressBar.setOrientation(QtCore.Qt.Vertical)
        self.progressBar.setObjectName(_fromUtf8("progressBar"))
        self.progressBar_2 = QtGui.QProgressBar(self.groupBox_3)
        self.progressBar_2.setGeometry(QtCore.QRect(30, 0, 20, 161))
        self.progressBar_2.setLayoutDirection(QtCore.Qt.LeftToRight)
        self.progressBar_2.setProperty("value", 24)
        self.progressBar_2.setOrientation(QtCore.Qt.Vertical)
        self.progressBar_2.setObjectName(_fromUtf8("progressBar_2"))
        self.label = QtGui.QLabel(self.groupBox_3)
        self.label.setGeometry(QtCore.QRect(0, 160, 16, 16))
        self.label.setObjectName(_fromUtf8("label"))
        self.label_2 = QtGui.QLabel(self.groupBox_3)
        self.label_2.setGeometry(QtCore.QRect(30, 160, 16, 16))
        self.label_2.setObjectName(_fromUtf8("label_2"))
        self.groupBox_4 = QtGui.QGroupBox(self.groupBox_3)
        self.groupBox_4.setGeometry(QtCore.QRect(60, 10, 141, 51))
        self.groupBox_4.setObjectName(_fromUtf8("groupBox_4"))
        self.label_3 = QtGui.QLabel(self.groupBox_4)
        self.label_3.setGeometry(QtCore.QRect(85, 20, 51, 20))
        self.label_3.setObjectName(_fromUtf8("label_3"))
        self.lcdNumber = QtGui.QLCDNumber(self.groupBox_4)
        self.lcdNumber.setGeometry(QtCore.QRect(10, 18, 64, 23))
        self.lcdNumber.setObjectName(_fromUtf8("lcdNumber"))
        self.groupBox_5 = QtGui.QGroupBox(self.groupBox_3)
        self.groupBox_5.setGeometry(QtCore.QRect(60, 80, 141, 51))
        self.groupBox_5.setObjectName(_fromUtf8("groupBox_5"))
        self.label_4 = QtGui.QLabel(self.groupBox_5)
        self.label_4.setGeometry(QtCore.QRect(85, 20, 51, 20))
        self.label_4.setObjectName(_fromUtf8("label_4"))
        self.lcdNumber_2 = QtGui.QLCDNumber(self.groupBox_5)
        self.lcdNumber_2.setGeometry(QtCore.QRect(10, 18, 64, 23))
        self.lcdNumber_2.setObjectName(_fromUtf8("lcdNumber_2"))
        self.groupBox_6 = QtGui.QGroupBox(self.groupBox_3)
        self.groupBox_6.setGeometry(QtCore.QRect(230, 10, 120, 121))
        self.groupBox_6.setObjectName(_fromUtf8("groupBox_6"))
        self.radioButton_8 = QtGui.QRadioButton(self.groupBox_6)
        self.radioButton_8.setGeometry(QtCore.QRect(10, 20, 89, 16))
        self.radioButton_8.setObjectName(_fromUtf8("radioButton_8"))
        self.radioButton_9 = QtGui.QRadioButton(self.groupBox_6)
        self.radioButton_9.setGeometry(QtCore.QRect(10, 40, 89, 16))
        self.radioButton_9.setObjectName(_fromUtf8("radioButton_9"))
        self.radioButton_10 = QtGui.QRadioButton(self.groupBox_6)
        self.radioButton_10.setGeometry(QtCore.QRect(10, 60, 89, 16))
        self.radioButton_10.setObjectName(_fromUtf8("radioButton_10"))
        self.radioButton_11 = QtGui.QRadioButton(self.groupBox_6)
        self.radioButton_11.setGeometry(QtCore.QRect(10, 80, 89, 16))
        self.radioButton_11.setObjectName(_fromUtf8("radioButton_11"))
        self.radioButton_12 = QtGui.QRadioButton(self.groupBox_6)
        self.radioButton_12.setGeometry(QtCore.QRect(10, 100, 89, 16))
        self.radioButton_12.setObjectName(_fromUtf8("radioButton_12"))

        self.retranslateUi(Dialog)
        QtCore.QMetaObject.connectSlotsByName(Dialog)

    def retranslateUi(self, Dialog):
        Dialog.setWindowTitle(_translate("Dialog", "Dialog", None))
        self.groupBox.setTitle(_translate("Dialog", "阀控制", None))
        self.radioButton.setText(_translate("Dialog", "全开", None))
        self.radioButton_2.setText(_translate("Dialog", "关闭", None))
        self.radioButton_3.setText(_translate("Dialog", "常规", None))
        self.groupBox_2.setTitle(_translate("Dialog", "控制模式", None))
        self.radioButton_4.setText(_translate("Dialog", "数字", None))
        self.radioButton_5.setText(_translate("Dialog", "模拟电压", None))
        self.radioButton_6.setText(_translate("Dialog", "模拟电流", None))
        self.radioButton_7.setText(_translate("Dialog", "直接控制", None))
        self.label.setText(_translate("Dialog", "SP.", None))
        self.label_2.setText(_translate("Dialog", "RO.", None))
        self.groupBox_4.setTitle(_translate("Dialog", "设定点", None))
        self.label_3.setText(_translate("Dialog", "% of F.S.", None))
        self.groupBox_5.setTitle(_translate("Dialog", "瞬时流量", None))
        self.label_4.setText(_translate("Dialog", "% of F.S.", None))
        self.groupBox_6.setTitle(_translate("Dialog", "单位", None))
        self.radioButton_8.setText(_translate("Dialog", "% F.S.", None))
        self.radioButton_9.setText(_translate("Dialog", "sccm", None))
        self.radioButton_10.setText(_translate("Dialog", "slm", None))
        self.radioButton_11.setText(_translate("Dialog", "V", None))
        self.radioButton_12.setText(_translate("Dialog", "mV", None))

