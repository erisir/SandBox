# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'UIControlProf.ui'
#
# Created by: PyQt5 UI code generator 5.7
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets

class Ui_Dialog(object):
    def setupUi(self, Dialog):
        Dialog.setObjectName("Dialog")
        Dialog.resize(520, 560)
        self.groupBox = QtWidgets.QGroupBox(Dialog)
        self.groupBox.setGeometry(QtCore.QRect(10, 20, 501, 161))
        self.groupBox.setObjectName("groupBox")
        self.groupBox_2 = QtWidgets.QGroupBox(self.groupBox)
        self.groupBox_2.setGeometry(QtCore.QRect(10, 30, 91, 101))
        self.groupBox_2.setObjectName("groupBox_2")
        self.verticalLayoutWidget = QtWidgets.QWidget(self.groupBox_2)
        self.verticalLayoutWidget.setGeometry(QtCore.QRect(10, 20, 71, 51))
        self.verticalLayoutWidget.setObjectName("verticalLayoutWidget")
        self.verticalLayout = QtWidgets.QVBoxLayout(self.verticalLayoutWidget)
        self.verticalLayout.setContentsMargins(0, 0, 0, 0)
        self.verticalLayout.setObjectName("verticalLayout")
        self.radioButton = QtWidgets.QRadioButton(self.verticalLayoutWidget)
        self.radioButton.setObjectName("radioButton")
        self.verticalLayout.addWidget(self.radioButton)
        self.radioButton_2 = QtWidgets.QRadioButton(self.verticalLayoutWidget)
        self.radioButton_2.setObjectName("radioButton_2")
        self.verticalLayout.addWidget(self.radioButton_2)
        self.groupBox_3 = QtWidgets.QGroupBox(self.groupBox)
        self.groupBox_3.setGeometry(QtCore.QRect(110, 30, 141, 101))
        self.groupBox_3.setObjectName("groupBox_3")
        self.verticalLayoutWidget_3 = QtWidgets.QWidget(self.groupBox_3)
        self.verticalLayoutWidget_3.setGeometry(QtCore.QRect(30, 20, 101, 71))
        self.verticalLayoutWidget_3.setObjectName("verticalLayoutWidget_3")
        self.verticalLayout_2 = QtWidgets.QVBoxLayout(self.verticalLayoutWidget_3)
        self.verticalLayout_2.setContentsMargins(0, 0, 0, 0)
        self.verticalLayout_2.setObjectName("verticalLayout_2")
        self.doubleSpinBox = QtWidgets.QDoubleSpinBox(self.verticalLayoutWidget_3)
        self.doubleSpinBox.setObjectName("doubleSpinBox")
        self.verticalLayout_2.addWidget(self.doubleSpinBox)
        self.doubleSpinBox_2 = QtWidgets.QDoubleSpinBox(self.verticalLayoutWidget_3)
        self.doubleSpinBox_2.setObjectName("doubleSpinBox_2")
        self.verticalLayout_2.addWidget(self.doubleSpinBox_2)
        self.doubleSpinBox_3 = QtWidgets.QDoubleSpinBox(self.verticalLayoutWidget_3)
        self.doubleSpinBox_3.setObjectName("doubleSpinBox_3")
        self.verticalLayout_2.addWidget(self.doubleSpinBox_3)
        self.verticalLayoutWidget_2 = QtWidgets.QWidget(self.groupBox_3)
        self.verticalLayoutWidget_2.setGeometry(QtCore.QRect(10, 20, 21, 71))
        self.verticalLayoutWidget_2.setObjectName("verticalLayoutWidget_2")
        self.verticalLayout_3 = QtWidgets.QVBoxLayout(self.verticalLayoutWidget_2)
        self.verticalLayout_3.setContentsMargins(0, 0, 0, 0)
        self.verticalLayout_3.setObjectName("verticalLayout_3")
        self.label = QtWidgets.QLabel(self.verticalLayoutWidget_2)
        self.label.setObjectName("label")
        self.verticalLayout_3.addWidget(self.label)
        self.label_2 = QtWidgets.QLabel(self.verticalLayoutWidget_2)
        self.label_2.setObjectName("label_2")
        self.verticalLayout_3.addWidget(self.label_2)
        self.label_3 = QtWidgets.QLabel(self.verticalLayoutWidget_2)
        self.label_3.setObjectName("label_3")
        self.verticalLayout_3.addWidget(self.label_3)
        self.verticalLayoutWidget_3.raise_()
        self.label_2.raise_()
        self.verticalLayoutWidget_2.raise_()

        self.retranslateUi(Dialog)
        QtCore.QMetaObject.connectSlotsByName(Dialog)

    def retranslateUi(self, Dialog):
        _translate = QtCore.QCoreApplication.translate
        Dialog.setWindowTitle(_translate("Dialog", "Dialog"))
        self.groupBox.setTitle(_translate("Dialog", "PID设置"))
        self.groupBox_2.setTitle(_translate("Dialog", "PID模式"))
        self.radioButton.setText(_translate("Dialog", "自动增量"))
        self.radioButton_2.setText(_translate("Dialog", "手动增量"))
        self.groupBox_3.setTitle(_translate("Dialog", "PID整定参数"))
        self.label.setText(_translate("Dialog", "Kp"))
        self.label_2.setText(_translate("Dialog", "Ki"))
        self.label_3.setText(_translate("Dialog", "Kd"))

