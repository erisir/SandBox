#include "../pid/mfc_pid.h"
#include "../adc/mfc_adc.h"

#include "../pwm/mfc_pwm.h"
#include "../usart/mfc_usart1.h"
#include "string.h"
#include "math.h"

//xdata struct PID spid; // PID Control Structure
struct PID spid; // PID Control Structure

unsigned int PIDVotageChanel = 1;
unsigned int PIDMode = 0;
unsigned char PIDEnable=0;
void SetPIDMode(unsigned int mode){
	PIDMode = mode;
}
void SetPIDVotageChanel(unsigned int ch){
	PIDVotageChanel = ch;
}

void GetPIDStatu(){
	printf("%.3f,%.3f,%.3f,%d,%d,%d,%d,%d,%d,%d\n",spid.Proportion  ,spid.Integral ,spid.Derivative  ,spid.DeadZone ,spid.SetPoint ,spid.Output,spid.LastError,spid.PrevError,spid.SetPoint-spid.LastError,spid.SumError );	
}  
/*********************************************************** 
              PID温度控制动作函数
 ***********************************************************/ 
void PIDStart() 		 
{  
	switch(PIDMode){
	case 0:
		spid.Output += IncAutoPIDCalc ( &spid,GetADCVoltage(PIDVotageChanel) );
		break;
	case 1:
		spid.Output += IncPIDCalc ( &spid,GetADCVoltage(PIDVotageChanel) );
		break;
	case 2:
		spid.Output = LocPIDCalc ( &spid,GetADCVoltage(PIDVotageChanel) );
		break; 
	default:
		spid.Output += IncPIDCalc ( &spid,GetADCVoltage(PIDVotageChanel) );
		break;
	}		
	if(spid.Output >PWM_HIGH_MAX)
		spid.Output = PWM_HIGH_MAX; 
	if(spid.Output <PWM_HIGH_MIN)
		spid.Output = PWM_HIGH_MIN;  
	LoadPWM(spid.Output) ;

}
/************************************************
				PID函数初始化
 *************************************************/
void PIDInit() 
{ 

	memset (&spid,0,sizeof(struct PID)); 	// Initialize Structure 

	spid.Proportion = 0.2;  
	spid.Integral =   0.00; 
	spid.Derivative =3; 
	spid.Output = 0;
	spid.SetPoint = 3200;
	spid.DeadZone = 20;
	spid.Period = 1000;
	spid.sumMax=999999;
	spid.sumMin=10;
}
unsigned int getPeriod(){
return spid.Period;
}
void SetPIDPeriod(unsigned int v_data){
	spid.Period = v_data;
}
void SetSetPoint(unsigned int v_data)
{
	spid.SetPoint =v_data; 	 
}
void SetPWMValue(unsigned int v_data)
{
	unsigned int manuPWM =   v_data;	
	if(manuPWM >PWM_HIGH_MAX)
		manuPWM = PWM_HIGH_MAX; 
	if(manuPWM <PWM_HIGH_MIN)
		manuPWM = PWM_HIGH_MIN;  
	LoadPWM(manuPWM); 
	spid.Output = manuPWM;

}  
void SetPIDparam_P_inc(unsigned int v_data)
{

	if(PIDMode != 2)
		spid.Proportion = 	v_data/1000.0;
	else
		spid.Proportion = v_data;

} 
void SetPIDparam_I_inc(unsigned int v_data)
{
	if(PIDMode != 2)
		spid.Integral   = 	 v_data/1000.0;
	else
		spid.Integral   = 	 v_data;


} 
void SetPIDparam_D_inc(unsigned int v_data)
{
	if(PIDMode != 2)
		spid.Derivative  = 	 v_data/1000.0;
	else
		spid.Derivative  = 	 v_data;


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
//增量式PID控制设计
int IncPIDCalc(struct PID *spid,int NextPoint)
{
	register int iError, iIncpid;
	//当前误差
	iError = spid->SetPoint - NextPoint;
	//增量计算
	iIncpid = spid->Proportion * iError //E[k]项
			- spid->Integral * spid->LastError //E[k－1]项
			+ spid->Derivative * spid->PrevError; //E[k－2]项
	//存储误差，用于下次计算
	spid->PrevError = spid->LastError;
	spid->LastError = iError;
	//返回增量值
	return(iIncpid);
}

//增量式自适应PID控制设计
int IncAutoPIDCalc(struct PID *spid,int NextPoint)
{
	register int iError, iIncpid;
	//当前误差
	iError = spid->SetPoint - NextPoint;
	//增量计算
	iIncpid = spid->Proportion * (2.45*iError //E[k]项
			- 3.5*spid->LastError //E[k－1]项
			+ 1.25*spid->PrevError); //E[k－2]项
	//存储误差，用于下次计算
	spid->PrevError = spid->LastError;
	spid->LastError = iError;
	//返回增量值
	return(iIncpid);
}
//位置式PID控制设计
unsigned int abs( int val){
	return val>0?val:(-1*val);
}
unsigned int LocPIDCalc(struct PID *spid,int NextPoint)
{
	register int iError,dError;
	iError = spid->SetPoint - NextPoint; //偏差
	spid->SumError += iError; //积分
	if(abs(spid->SumError)>spid->sumMax)
		spid->SumError= spid->SumError>0?spid->sumMax:(-1*spid->sumMax);	
	dError = iError - spid->LastError; //微分
	spid->LastError = iError;

	return(spid->Proportion * iError //比例项
			+ spid->Integral * spid->SumError //积分项
			+ spid->Derivative * dError); //微分项
} 
