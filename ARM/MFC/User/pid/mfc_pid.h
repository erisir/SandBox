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


/************************************************
PID����
 *************************************************/ 
/*************PID**********************************/
struct PID {
	float Proportion; // �������� Proportional Const
	float Integral; // ���ֳ��� Integral Const
	float Derivative; // ΢�ֳ��� Derivative Const

	int LastError; // Error[-1]
	int PrevError; // Error[-2]
	int SumError;

	int Output;
	unsigned int SetPoint;
	unsigned int DeadZone;
};

void PIDInit (void);  
void PIDStart(void); 		 

void SetSetPoint(unsigned int v_data); 
void SetPWMValue(unsigned int v_data);
void SetPIDparam_P_inc(unsigned int v_data); 
void SetPIDparam_I_inc(unsigned int v_data); 
void SetPIDparam_D_inc(unsigned int v_data); 
void SetTClose(void);
void SetTOpen(void);
void SetTPID(void);
void SetPIDMode(unsigned int mode);

void GetPIDStatu(void); 

unsigned char isPIDEnable(void);

unsigned int LocPIDCalc(struct PID *spid,int NextPoint);
int IncPIDCalc(struct PID *spid,int NextPoint);
int IncAutoPIDCalc(struct PID *spid,int NextPoint);

#endif
/*********************************************END OF FILE**********************/
