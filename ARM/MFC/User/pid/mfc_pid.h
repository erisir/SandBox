/********************************************************************
本程序只供学习使用，未经作者许可，不得用于其它任何用途
程序结构参考 安徽师范大学  Lyzhangxiang的EasyHW OS结构设计
datacomm.h
作者：bg8wj
建立日期: 2012.12.23
版本：V1.0

Copyright(C) bg8wj
********************************************************************/
#ifndef  __PID_H__
#define  __PID_H__

 
/************************************************
PID函数
*************************************************/ 
/*************PID**********************************/
struct PID {
float Proportion; // 比例常数 Proportional Const
float Integral; // 积分常数 Integral Const
float Derivative; // 微分常数 Derivative Const

int LastError; // Error[-1]
int PrevError; // Error[-2]
int SumError;

int rout;
unsigned int set_point;
unsigned int deadZone;


};

void PIDInit (void);  
int PIDCalc( struct PID *pp, unsigned int NextPoint ) ;
void PIDStart(void); 		 

void SetSetPoint(unsigned int v_data); 
void SetPWMValue(unsigned int v_data);
void SetPIDparam_P_inc(unsigned int v_data); 
void SetPIDparam_I_inc(unsigned int v_data); 
void SetPIDparam_D_inc(unsigned int v_data); 
void  SetTClose(void);
void  SetTOpen(void);
void  SetTPID(void);
unsigned char isPIDEnable(void);
void GetPIDStatu(void); 

#endif
