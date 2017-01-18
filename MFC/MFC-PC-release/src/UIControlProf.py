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
        Dialog.resize(544, 587)
        self.groupBox = QtWidgets.QGroupBox(Dialog)
        self.groupBox.setGeometry(QtCore.QRect(10, 20, 401, 171))
        self.groupBox.setObjectName("groupBox")
        self.groupBox_2 = QtWidgets.QGroupBox(self.groupBox)
        self.groupBox_2.setGeometry(QtCore.QRect(10, 30, 91, 101))
        self.groupBox_2.setObjectName("groupBox_2")
        self.verticalLayoutWidget = QtWidgets.QWidget(self.groupBox_2)
        self.verticalLayoutWidget.setGeometry(QtCore.QRect(10, 20, 91, 71))
        self.verticalLayoutWidget.setObjectName("verticalLayoutWidget")
        self.verticalLayout = QtWidgets.QVBoxLayout(self.verticalLayoutWidget)
        self.verticalLayout.setContentsMargins(0, 0, 0, 0)
        self.verticalLayout.setObjectName("verticalLayout")
        self.PID_AutoInc = QtWidgets.QRadioButton(self.verticalLayoutWidget)
        self.PID_AutoInc.setChecked(True)
        self.PID_AutoInc.setObjectName("PID_AutoInc")
        self.verticalLayout.addWidget(self.PID_AutoInc)
        self.PID_ManuInc = QtWidgets.QRadioButton(self.verticalLayoutWidget)
        self.PID_ManuInc.setObjectName("PID_ManuInc")
        self.verticalLayout.addWidget(self.PID_ManuInc)
        self.PID_ByPC = QtWidgets.QRadioButton(self.verticalLayoutWidget)
        self.PID_ByPC.setObjectName("PID_ByPC")
        self.verticalLayout.addWidget(self.PID_ByPC)
        self.groupBox_3 = QtWidgets.QGroupBox(self.groupBox)
        self.groupBox_3.setGeometry(QtCore.QRect(110, 30, 281, 131))
        self.groupBox_3.setObjectName("groupBox_3")
        self.formLayoutWidget_3 = QtWidgets.QWidget(self.groupBox_3)
        self.formLayoutWidget_3.setGeometry(QtCore.QRect(9, 17, 121, 80))
        self.formLayoutWidget_3.setObjectName("formLayoutWidget_3")
        self.formLayout_3 = QtWidgets.QFormLayout(self.formLayoutWidget_3)
        self.formLayout_3.setContentsMargins(0, 0, 0, 0)
        self.formLayout_3.setObjectName("formLayout_3")
        self.label = QtWidgets.QLabel(self.formLayoutWidget_3)
        self.label.setObjectName("label")
        self.formLayout_3.setWidget(0, QtWidgets.QFormLayout.LabelRole, self.label)
        self.PID_Kp = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_3)
        self.PID_Kp.setKeyboardTracking(False)
        self.PID_Kp.setDecimals(4)
        self.PID_Kp.setMaximum(65535.0)
        self.PID_Kp.setSingleStep(0.001)
        self.PID_Kp.setProperty("value", 6.257)
        self.PID_Kp.setObjectName("PID_Kp")
        self.formLayout_3.setWidget(0, QtWidgets.QFormLayout.FieldRole, self.PID_Kp)
        self.label_2 = QtWidgets.QLabel(self.formLayoutWidget_3)
        self.label_2.setObjectName("label_2")
        self.formLayout_3.setWidget(1, QtWidgets.QFormLayout.LabelRole, self.label_2)
        self.PID_Ki = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_3)
        self.PID_Ki.setKeyboardTracking(False)
        self.PID_Ki.setDecimals(4)
        self.PID_Ki.setMaximum(65535.0)
        self.PID_Ki.setSingleStep(0.001)
        self.PID_Ki.setProperty("value", 6.4545)
        self.PID_Ki.setObjectName("PID_Ki")
        self.formLayout_3.setWidget(1, QtWidgets.QFormLayout.FieldRole, self.PID_Ki)
        self.label_3 = QtWidgets.QLabel(self.formLayoutWidget_3)
        self.label_3.setObjectName("label_3")
        self.formLayout_3.setWidget(2, QtWidgets.QFormLayout.LabelRole, self.label_3)
        self.PID_Kd = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_3)
        self.PID_Kd.setKeyboardTracking(False)
        self.PID_Kd.setDecimals(4)
        self.PID_Kd.setMaximum(65535.0)
        self.PID_Kd.setSingleStep(0.001)
        self.PID_Kd.setProperty("value", 2.23)
        self.PID_Kd.setObjectName("PID_Kd")
        self.formLayout_3.setWidget(2, QtWidgets.QFormLayout.FieldRole, self.PID_Kd)
        self.formLayoutWidget_2 = QtWidgets.QWidget(self.groupBox_3)
        self.formLayoutWidget_2.setGeometry(QtCore.QRect(140, 20, 134, 100))
        self.formLayoutWidget_2.setObjectName("formLayoutWidget_2")
        self.formLayout_2 = QtWidgets.QFormLayout(self.formLayoutWidget_2)
        self.formLayout_2.setContentsMargins(0, 0, 0, 0)
        self.formLayout_2.setObjectName("formLayout_2")
        self.label_4 = QtWidgets.QLabel(self.formLayoutWidget_2)
        self.label_4.setObjectName("label_4")
        self.formLayout_2.setWidget(1, QtWidgets.QFormLayout.LabelRole, self.label_4)
        self.label_5 = QtWidgets.QLabel(self.formLayoutWidget_2)
        self.label_5.setObjectName("label_5")
        self.formLayout_2.setWidget(2, QtWidgets.QFormLayout.LabelRole, self.label_5)
        self.PID_Inteval = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_2)
        self.PID_Inteval.setKeyboardTracking(False)
        self.PID_Inteval.setDecimals(0)
        self.PID_Inteval.setMaximum(65535.0)
        self.PID_Inteval.setSingleStep(2.0)
        self.PID_Inteval.setProperty("value", 20.0)
        self.PID_Inteval.setObjectName("PID_Inteval")
        self.formLayout_2.setWidget(1, QtWidgets.QFormLayout.FieldRole, self.PID_Inteval)
        self.PID_SetPoint = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_2)
        self.PID_SetPoint.setKeyboardTracking(False)
        self.PID_SetPoint.setMaximum(65535.0)
        self.PID_SetPoint.setSingleStep(1000.0)
        self.PID_SetPoint.setProperty("value", 2000.0)
        self.PID_SetPoint.setObjectName("PID_SetPoint")
        self.formLayout_2.setWidget(2, QtWidgets.QFormLayout.FieldRole, self.PID_SetPoint)
        self.label_15 = QtWidgets.QLabel(self.formLayoutWidget_2)
        self.label_15.setObjectName("label_15")
        self.formLayout_2.setWidget(0, QtWidgets.QFormLayout.LabelRole, self.label_15)
        self.PID_ThredHold = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_2)
        self.PID_ThredHold.setKeyboardTracking(False)
        self.PID_ThredHold.setDecimals(0)
        self.PID_ThredHold.setMaximum(65535.0)
        self.PID_ThredHold.setSingleStep(5.0)
        self.PID_ThredHold.setProperty("value", 100.0)
        self.PID_ThredHold.setObjectName("PID_ThredHold")
        self.formLayout_2.setWidget(0, QtWidgets.QFormLayout.FieldRole, self.PID_ThredHold)
        self.label_16 = QtWidgets.QLabel(self.formLayoutWidget_2)
        self.label_16.setObjectName("label_16")
        self.formLayout_2.setWidget(3, QtWidgets.QFormLayout.LabelRole, self.label_16)
        self.PID_VotageChanel = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_2)
        self.PID_VotageChanel.setKeyboardTracking(False)
        self.PID_VotageChanel.setDecimals(0)
        self.PID_VotageChanel.setMaximum(10.0)
        self.PID_VotageChanel.setProperty("value", 1.0)
        self.PID_VotageChanel.setObjectName("PID_VotageChanel")
        self.formLayout_2.setWidget(3, QtWidgets.QFormLayout.FieldRole, self.PID_VotageChanel)
        self.groupBox_4 = QtWidgets.QGroupBox(Dialog)
        self.groupBox_4.setGeometry(QtCore.QRect(10, 200, 401, 111))
        self.groupBox_4.setObjectName("groupBox_4")
        self.formLayoutWidget = QtWidgets.QWidget(self.groupBox_4)
        self.formLayoutWidget.setGeometry(QtCore.QRect(10, 20, 160, 80))
        self.formLayoutWidget.setObjectName("formLayoutWidget")
        self.formLayout = QtWidgets.QFormLayout(self.formLayoutWidget)
        self.formLayout.setContentsMargins(0, 0, 0, 0)
        self.formLayout.setObjectName("formLayout")
        self.label_6 = QtWidgets.QLabel(self.formLayoutWidget)
        self.label_6.setObjectName("label_6")
        self.formLayout.setWidget(1, QtWidgets.QFormLayout.LabelRole, self.label_6)
        self.Prescaler = QtWidgets.QDoubleSpinBox(self.formLayoutWidget)
        self.Prescaler.setKeyboardTracking(False)
        self.Prescaler.setDecimals(0)
        self.Prescaler.setMaximum(100.0)
        self.Prescaler.setProperty("value", 1.0)
        self.Prescaler.setObjectName("Prescaler")
        self.formLayout.setWidget(1, QtWidgets.QFormLayout.FieldRole, self.Prescaler)
        self.label_7 = QtWidgets.QLabel(self.formLayoutWidget)
        self.label_7.setObjectName("label_7")
        self.formLayout.setWidget(0, QtWidgets.QFormLayout.LabelRole, self.label_7)
        self.SmoothWindow = QtWidgets.QDoubleSpinBox(self.formLayoutWidget)
        self.SmoothWindow.setKeyboardTracking(False)
        self.SmoothWindow.setDecimals(0)
        self.SmoothWindow.setMaximum(65535.0)
        self.SmoothWindow.setSingleStep(50.0)
        self.SmoothWindow.setProperty("value", 500.0)
        self.SmoothWindow.setObjectName("SmoothWindow")
        self.formLayout.setWidget(0, QtWidgets.QFormLayout.FieldRole, self.SmoothWindow)
        self.label_8 = QtWidgets.QLabel(self.formLayoutWidget)
        self.label_8.setObjectName("label_8")
        self.formLayout.setWidget(2, QtWidgets.QFormLayout.LabelRole, self.label_8)
        self.PWMValue = QtWidgets.QDoubleSpinBox(self.formLayoutWidget)
        self.PWMValue.setKeyboardTracking(False)
        self.PWMValue.setDecimals(0)
        self.PWMValue.setMinimum(10.0)
        self.PWMValue.setMaximum(65535.0)
        self.PWMValue.setSingleStep(500.0)
        self.PWMValue.setProperty("value", 5000.0)
        self.PWMValue.setObjectName("PWMValue")
        self.formLayout.setWidget(2, QtWidgets.QFormLayout.FieldRole, self.PWMValue)
        self.formLayoutWidget_4 = QtWidgets.QWidget(self.groupBox_4)
        self.formLayoutWidget_4.setGeometry(QtCore.QRect(180, 20, 160, 80))
        self.formLayoutWidget_4.setObjectName("formLayoutWidget_4")
        self.formLayout_4 = QtWidgets.QFormLayout(self.formLayoutWidget_4)
        self.formLayout_4.setContentsMargins(0, 0, 0, 0)
        self.formLayout_4.setObjectName("formLayout_4")
        self.label_9 = QtWidgets.QLabel(self.formLayoutWidget_4)
        self.label_9.setObjectName("label_9")
        self.formLayout_4.setWidget(0, QtWidgets.QFormLayout.LabelRole, self.label_9)
        self.PWMRate = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_4)
        self.PWMRate.setKeyboardTracking(False)
        self.PWMRate.setMaximum(65535.0)
        self.PWMRate.setProperty("value", 0.01)
        self.PWMRate.setObjectName("PWMRate")
        self.formLayout_4.setWidget(0, QtWidgets.QFormLayout.FieldRole, self.PWMRate)
        self.label_10 = QtWidgets.QLabel(self.formLayoutWidget_4)
        self.label_10.setObjectName("label_10")
        self.formLayout_4.setWidget(1, QtWidgets.QFormLayout.LabelRole, self.label_10)
        self.Slope = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_4)
        self.Slope.setKeyboardTracking(False)
        self.Slope.setMaximum(65535.0)
        self.Slope.setProperty("value", 25.0)
        self.Slope.setObjectName("Slope")
        self.formLayout_4.setWidget(1, QtWidgets.QFormLayout.FieldRole, self.Slope)
        self.label_11 = QtWidgets.QLabel(self.formLayoutWidget_4)
        self.label_11.setObjectName("label_11")
        self.formLayout_4.setWidget(2, QtWidgets.QFormLayout.LabelRole, self.label_11)
        self.Interception = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_4)
        self.Interception.setKeyboardTracking(False)
        self.Interception.setMinimum(-32556.0)
        self.Interception.setMaximum(65535.0)
        self.Interception.setProperty("value", 700.0)
        self.Interception.setObjectName("Interception")
        self.formLayout_4.setWidget(2, QtWidgets.QFormLayout.FieldRole, self.Interception)
        self.groupBox_5 = QtWidgets.QGroupBox(Dialog)
        self.groupBox_5.setGeometry(QtCore.QRect(10, 320, 401, 161))
        self.groupBox_5.setObjectName("groupBox_5")
        self.formLayoutWidget_5 = QtWidgets.QWidget(self.groupBox_5)
        self.formLayoutWidget_5.setGeometry(QtCore.QRect(10, 20, 161, 81))
        self.formLayoutWidget_5.setObjectName("formLayoutWidget_5")
        self.formLayout_5 = QtWidgets.QFormLayout(self.formLayoutWidget_5)
        self.formLayout_5.setFieldGrowthPolicy(QtWidgets.QFormLayout.AllNonFixedFieldsGrow)
        self.formLayout_5.setContentsMargins(0, 0, 0, 0)
        self.formLayout_5.setObjectName("formLayout_5")
        self.label_13 = QtWidgets.QLabel(self.formLayoutWidget_5)
        self.label_13.setObjectName("label_13")
        self.formLayout_5.setWidget(0, QtWidgets.QFormLayout.LabelRole, self.label_13)
        self.BackForward_Start = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_5)
        self.BackForward_Start.setKeyboardTracking(False)
        self.BackForward_Start.setDecimals(0)
        self.BackForward_Start.setMaximum(65535.0)
        self.BackForward_Start.setProperty("value", 100.0)
        self.BackForward_Start.setObjectName("BackForward_Start")
        self.formLayout_5.setWidget(0, QtWidgets.QFormLayout.FieldRole, self.BackForward_Start)
        self.label_12 = QtWidgets.QLabel(self.formLayoutWidget_5)
        self.label_12.setObjectName("label_12")
        self.formLayout_5.setWidget(1, QtWidgets.QFormLayout.LabelRole, self.label_12)
        self.BackForward_End = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_5)
        self.BackForward_End.setKeyboardTracking(False)
        self.BackForward_End.setDecimals(0)
        self.BackForward_End.setMaximum(65535.0)
        self.BackForward_End.setProperty("value", 65530.0)
        self.BackForward_End.setObjectName("BackForward_End")
        self.formLayout_5.setWidget(1, QtWidgets.QFormLayout.FieldRole, self.BackForward_End)
        self.label_14 = QtWidgets.QLabel(self.formLayoutWidget_5)
        self.label_14.setObjectName("label_14")
        self.formLayout_5.setWidget(2, QtWidgets.QFormLayout.LabelRole, self.label_14)
        self.BackForward_StepSize = QtWidgets.QDoubleSpinBox(self.formLayoutWidget_5)
        self.BackForward_StepSize.setKeyboardTracking(False)
        self.BackForward_StepSize.setDecimals(0)
        self.BackForward_StepSize.setMinimum(10.0)
        self.BackForward_StepSize.setMaximum(65535.0)
        self.BackForward_StepSize.setSingleStep(500.0)
        self.BackForward_StepSize.setProperty("value", 2000.0)
        self.BackForward_StepSize.setObjectName("BackForward_StepSize")
        self.formLayout_5.setWidget(2, QtWidgets.QFormLayout.FieldRole, self.BackForward_StepSize)
        self.horizontalLayoutWidget = QtWidgets.QWidget(self.groupBox_5)
        self.horizontalLayoutWidget.setGeometry(QtCore.QRect(10, 110, 239, 31))
        self.horizontalLayoutWidget.setObjectName("horizontalLayoutWidget")
        self.horizontalLayout = QtWidgets.QHBoxLayout(self.horizontalLayoutWidget)
        self.horizontalLayout.setContentsMargins(0, 0, 0, 0)
        self.horizontalLayout.setObjectName("horizontalLayout")
        self.getVoltageVsPWMCurse = QtWidgets.QPushButton(self.horizontalLayoutWidget)
        self.getVoltageVsPWMCurse.setFocusPolicy(QtCore.Qt.NoFocus)
        self.getVoltageVsPWMCurse.setObjectName("getVoltageVsPWMCurse")
        self.horizontalLayout.addWidget(self.getVoltageVsPWMCurse)
        self.StopVoltageVsPWMCurse = QtWidgets.QPushButton(self.horizontalLayoutWidget)
        self.StopVoltageVsPWMCurse.setFocusPolicy(QtCore.Qt.NoFocus)
        self.StopVoltageVsPWMCurse.setObjectName("StopVoltageVsPWMCurse")
        self.horizontalLayout.addWidget(self.StopVoltageVsPWMCurse)
        self.savePVFDtoMCU = QtWidgets.QPushButton(self.horizontalLayoutWidget)
        self.savePVFDtoMCU.setFocusPolicy(QtCore.Qt.NoFocus)
        self.savePVFDtoMCU.setObjectName("savePVFDtoMCU")
        self.horizontalLayout.addWidget(self.savePVFDtoMCU)

        self.retranslateUi(Dialog)
        QtCore.QMetaObject.connectSlotsByName(Dialog)

    def retranslateUi(self, Dialog):
        _translate = QtCore.QCoreApplication.translate
        Dialog.setWindowTitle(_translate("Dialog", "高级设置"))
        self.groupBox.setTitle(_translate("Dialog", "PID设置"))
        self.groupBox_2.setTitle(_translate("Dialog", "PID模式"))
        self.PID_AutoInc.setText(_translate("Dialog", "自动增量"))
        self.PID_ManuInc.setText(_translate("Dialog", "手动增量"))
        self.PID_ByPC.setText(_translate("Dialog", "PC控制"))
        self.groupBox_3.setTitle(_translate("Dialog", "PID整定参数"))
        self.label.setText(_translate("Dialog", "Kp"))
        self.label_2.setText(_translate("Dialog", "Ki"))
        self.label_3.setText(_translate("Dialog", "Kd"))
        self.label_4.setText(_translate("Dialog", "Inteval"))
        self.label_5.setText(_translate("Dialog", "SetPoint"))
        self.label_15.setText(_translate("Dialog", "ThredHold"))
        self.label_16.setText(_translate("Dialog", "Chanel"))
        self.groupBox_4.setTitle(_translate("Dialog", "调试"))
        self.label_6.setText(_translate("Dialog", "Prescaler"))
        self.label_7.setText(_translate("Dialog", "Smooth"))
        self.label_8.setText(_translate("Dialog", "PWMValue"))
        self.label_9.setText(_translate("Dialog", "PWMRate"))
        self.label_10.setText(_translate("Dialog", "Slope"))
        self.label_11.setText(_translate("Dialog", "InterC"))
        self.groupBox_5.setTitle(_translate("Dialog", "控制"))
        self.label_13.setText(_translate("Dialog", "Start"))
        self.label_12.setText(_translate("Dialog", "End"))
        self.label_14.setText(_translate("Dialog", "StepSize"))
        self.getVoltageVsPWMCurse.setText(_translate("Dialog", "绘制滞回线"))
        self.StopVoltageVsPWMCurse.setText(_translate("Dialog", "停止"))
        self.savePVFDtoMCU.setText(_translate("Dialog", "保存"))

