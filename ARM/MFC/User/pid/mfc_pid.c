#include "../pid/mfc_pid.h"
#include "../adc/mfc_adc.h"

#include "../pwm/mfc_pwm.h"
#include "../usart/mfc_usart1.h"
#include "string.h"
#include "math.h"

//xdata struct PID spid; // PID Control Structure
struct PID spid; // PID Control Structure
 
unsigned int manuPWM = 0;

unsigned char PIDEnable=0;
/************************************************
              PID函数体 
51单片机最不擅长浮点数计算，转换成int型计算
*************************************************/
unsigned int abs(int value){
	return value>0?value:(-1*value);
}
int PIDCalc( struct PID *pp, unsigned int NextPoint ) 
{ 
  int Error,dError;
   
  Error = spid.set_point - NextPoint;       // 偏差E(t) 
  pp->SumError = pp->PrevError+pp->LastError+Error; 	                // 积分
  dError=Error - pp->LastError;             // 当前微分
  pp->PrevError = pp->LastError; 
  pp->LastError = Error; 

  if(abs(Error)< pp->deadZone){
  pp->SumError = 0;
  return 0;
  }

  return ( 
            pp->Proportion * Error        //比例 
            + pp->Integral * pp->SumError     //积分项 
            + pp->Derivative * dError	  // 微分项	 负责刹车
			);  
} 
void GetPIDStatu(){
printf("%d,%d,spid.Proportion:%.3f  ,spid.Integral:%.3f ,spid.Derivative:%.3f  ,spid.rout:%d ,spid.set_point:%d  ,spid.deadZone:%d  ",spid.set_point,spid.rout ,spid.Proportion  ,spid.Integral ,spid.Derivative  ,spid.rout ,spid.set_point  ,spid.deadZone  );	
}  
/*********************************************************** 
             PID温度控制做动函数
***********************************************************/ 

void PIDStart() 		//PID控制输出函数
{  
   spid.rout += PIDCalc ( &spid,getADCValue() ); 
	 if(spid.rout >PWM_HIGH_MAX)
	 	spid.rout = PWM_HIGH_MAX; 
	 if(spid.rout <PWM_HIGH_MIN)
	 	spid.rout = PWM_HIGH_MIN;  
	 LoadPWM(spid.rout) ;
  
}
/************************************************
				PID函数初始化
*************************************************/
void PIDInit() 
{ 

  memset (&spid,0,sizeof(struct PID)); 	// Initialize Structure 
  
  spid.Proportion = 0.2; // Set PID Coefficients 	0.009开始震荡，取60%~70%=	0.006  2 0 9 ok
  spid.Integral =   0.00; 
  spid.Derivative =3; 
  spid.rout = 0;
  spid.set_point = 3200;
  spid.deadZone = 20;
}
  
void SetSetPoint(unsigned int v_data)
{
  spid.set_point =v_data; 	 
}
void SetPWMValue(unsigned int v_data)
{
    manuPWM =   v_data;	
	if(manuPWM >PWM_HIGH_MAX)
		manuPWM = PWM_HIGH_MAX; 
	if(manuPWM <PWM_HIGH_MIN)
		manuPWM = PWM_HIGH_MIN;  
	LoadPWM(manuPWM); 
	spid.rout = manuPWM;

}  
void SetPIDparam_P_inc(unsigned int v_data)
{
 
	if(v_data != 9999)spid.Proportion = 	v_data/1000.0;
 

} 
void SetPIDparam_I_inc(unsigned int v_data)
{
	if(v_data != 9999)spid.Integral   = 	 v_data/1000.0;
 

} 
void SetPIDparam_D_inc(unsigned int v_data)
{
    if(v_data != 9999)spid.Derivative  = 	 v_data/1000.0;
 

} 
void  SetTClose()
{
	 PIDEnable = 0;
	 LoadPWM(PWM_HIGH_MIN) ;
}
void  SetTOpen()
{
	 PIDEnable = 0;
	 LoadPWM(PWM_HIGH_MAX) ;
}
void  SetTPID()
{
	 PIDEnable = 1;

}
unsigned char isPIDEnable(){
   return PIDEnable;
} 
