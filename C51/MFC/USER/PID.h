/********************************************************************
������ֻ��ѧϰʹ�ã�δ��������ɣ��������������κ���;
����ṹ�ο� ����ʦ����ѧ  Lyzhangxiang��EasyHW OS�ṹ���
datacomm.h
���ߣ�bg8wj
��������: 2012.12.23
�汾��V1.0

Copyright(C) bg8wj
/********************************************************************/
#ifndef  __PID_H__
#define  __PID_H__

#define uint  unsigned int
#define uchar unsigned char
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
int maxMoveStep;
int rout;
uint set_point;
uint deadZone;
uint duration;

};

void PIDInit ();  
int PIDCalc( struct PID *pp, uint NextPoint ) ;
void compare_temper(uint votage_input); 		//PID�¶ȿ����������

uint getPIDSet_point();
void SetPointDown (); 
void SetPointUp ();
 
void SetSetPoint(uint v_data); 
void SetPWMValue(uint v_data);
void SetPIDparam_P_inc(uint v_data); 
void SetPIDparam_I_inc(uint v_data); 
void SetPIDparam_D_inc(uint v_data); 
void SetPIDparam_Dura_inc(uint v_data); 
#endif
