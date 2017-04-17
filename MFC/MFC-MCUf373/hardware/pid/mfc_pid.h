/********************************************************************
������ֻ��ѧϰʹ�ã�δ��������ɣ��������������κ���;
����ṹ�ο� ����ʦ����ѧ  Lyzhangxiang��EasyHW OS�ṹ���
datacomm.h
���ߣ�bg8wj
��������: 2012.12.23
�汾��V1.0

Copyright(C) bg8wj
 ********************************************************************/
#ifndef  __PID_H__
#define  __PID_H__

#include "stdint.h"
/************************************************
PID����
 *************************************************/ 
/*************PID**********************************/
struct PID {
	double Proportion; // �������� Proportional Const
	double Integral; // ���ֳ��� Integral Const
	double Derivative; // ΢�ֳ��� Derivative Const

	int LastError; // Error[-1]
	int PrevError; // Error[-2]
	int SumError;
	
	unsigned int sumMax;
	unsigned int sumMin;

	int Output;
	unsigned int Thredhold;
	unsigned int SetPoint;
	unsigned int DeadZone;
	unsigned int Period;
};
struct PWMVotageFitPara {
	double ForwardA; // �������� Proportional Const
	double ForwardB; // ���ֳ��� Integral Const
	double ForwardC; // ΢�ֳ��� Derivative Const
	double BackwardA; // �������� Proportional Const
	double BackwardB; // ���ֳ��� Integral Const
	double BackwardC; // ΢�ֳ��� Derivative Const
};

void PIDInit (void);  
void PIDStart(void); 		 

void SetSetPoint(float v_data); 
void SetPWMValue(float v_data);
void SetPIDPeriod(float v_data);
void SetPIDparam_P_inc(float v_data); 
void SetPIDparam_I_inc(float v_data); 
void SetPIDparam_D_inc(float v_data); 
void SetTClose(void);
void SetTOpen(void);
void SetTPID(void);
void SetPIDMode(float mode);
void SetPIDVotageChanel(float ch);
void SetPIDThredHold(float v_data);
void SetForwardA(float v_data); 
void SetForwardB(float v_data); 
void SetForwardC(float v_data);
void SetBackwardA(float v_data);
void SetBackwardB(float v_data);
void SetBackwardC(float v_data);
unsigned int getPWMByVotage(unsigned int votage,char forBackward);
uint32_t GetPIDOutput(void);
void GetPIDStatu(void); 
unsigned int getPeriod(void);
unsigned char isPIDEnable(void);
unsigned int abs( int val);
unsigned int LocPIDCalc(struct PID *spid,int NextPoint);
int IncPIDCalc(struct PID *spid,int NextPoint);
int IncAutoPIDCalc(struct PID *spid,int NextPoint);
#endif
/*********************************************END OF FILE**********************/
