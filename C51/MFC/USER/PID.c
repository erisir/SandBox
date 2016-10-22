#include "PID.h"
#include "adc.h"

#include "PWM.h"
#include "Uart1.h"
#include "string.h"
#include "math.h"
 #include "lcd1602.h"

//xdata struct PID spid; // PID Control Structure
struct PID spid; // PID Control Structure
int pidCounter =0;
uint manuPWM = 0;
char falgForPID = 0;
char  fuhao[2] = "+";  
/************************************************
              PID������ 
51��Ƭ����ó����������㣬ת����int�ͼ���
*************************************************/
int PIDCalc( struct PID *pp, uint NextPoint ) 
{ 
  int Error,dError;
   
  Error = spid.set_point - NextPoint;       // ƫ��E(t) 
  pp->SumError = pp->PrevError+pp->LastError+Error; 	                // ����
  dError=Error - pp->LastError;             // ��ǰ΢��
  pp->PrevError = pp->LastError; 
  pp->LastError = Error; 

  if(abs(Error)< pp->deadZone){
  pp->SumError = 0;
  return 0;
  }

  return ( 
            pp->Proportion * Error        //���� 
            + pp->Integral * pp->SumError     //������ 
            + pp->Derivative * dError	  // ΢����	 ����ɲ��
			);  
} 
 
/*********************************************************** 
             PID�¶ȿ�����������
***********************************************************/ 

void compare_temper(uint v_input) 		//PID�����������
{  
	 int delta = 0;
 	 pidCounter++;
	 if(pidCounter<spid.duration)return;
	 pidCounter = 0; 

	 delta =  PIDCalc ( &spid,v_input ); 

	 if( delta > spid.maxMoveStep){
	  delta =  spid.maxMoveStep;
	 }
	 if( delta < (-1*spid.maxMoveStep)){
	  delta =  (-1*spid.maxMoveStep);
	 }

     spid.rout += delta; 
	 

	 if(spid.rout>510)spid.rout = 510;

	 if(spid.rout< 5) spid.rout = 5;
 
	 
	 PWM1_set9(spid.rout) ;
	// DelayMs(abs(spid.set_point - v_input))	; //�󲽵ȴ�
	if(1==1){
	 num_lcdDis(0,0x0D,spid.rout,3);

	  if(delta>0){
	  
	    fuhao[0] = '+';
	 	num_lcdDis(1,0x0D,delta,3);
	}else{
 
		fuhao[0] = '-';
		num_lcdDis(1,0x0D,-1*delta,3);
	}
	if(falgForPID == 0){
	 falgForPID =1;
	 hz_lcdDis(1,0x0C,fuhao);	
	 }
	 else{
	 falgForPID =0;
	 hz_lcdDis(1,0x0C," ");
	 }
	 } 
}
/************************************************
				PID������ʼ��
*************************************************/
void PIDInit() 
{ 

  memset (&spid,0,sizeof(struct PID)); 	// Initialize Structure 
  
  spid.Proportion = 0.09; // Set PID Coefficients 	0.009��ʼ�𵴣�ȡ60%~70%=	0.006  2 0 9 ok
  spid.Integral =   0.00; 
  spid.Derivative =0.380; 
  spid.rout = 0;
  spid.set_point = 3500;
  spid.maxMoveStep = 100;
  spid.deadZone = 20;
  spid.duration = 1;
}

uint getPIDSet_point()
{
	return spid.set_point; 
}
void SetPointDown ()
{
	spid.set_point-=500;
 
}
 
void SetPointUp ()
{
	spid.set_point+=500;
 
}
 
void SetSetPoint(uint v_data)
{
  spid.set_point =v_data; 	 
}
void SetPWMValue(uint v_data)
{
    manuPWM =   v_data;	 
	if(manuPWM>512)manuPWM=512;
	if(manuPWM<10)manuPWM=10;
	PWM1_set9(manuPWM);
    hz_lcdDis(0,0x06,"W");
	num_lcdDis(0,0x07,manuPWM,3);
	spid.rout = manuPWM;

}  
void SetPIDparam_P_inc(uint v_data)
{
 
	if(v_data != 9999)spid.Proportion = 	v_data/1000.0;
	hz_lcdDis(0,0x06,"P");
	num_lcdDis(0,0x07,spid.Proportion*1000,3);

} 
void SetPIDparam_I_inc(uint v_data)
{
	if(v_data != 9999)spid.Integral   = 	 v_data/1000.0;
//	hz_lcdDis(0,0x0b,"I");
//	num_lcdDis(0,0x0c,spid.Integral*1000,3);

} 
void SetPIDparam_D_inc(uint v_data)
{
    if(v_data != 9999)spid.Derivative  = 	 v_data/1000.0;
//	hz_lcdDis(1,0x0b,"D");
//	num_lcdDis(1,0x0c,spid.Derivative*1000,3);

} 
void SetPIDparam_Dura_inc(uint v_data)
{
    if(v_data != 9999)spid.duration    = 	   v_data;
	hz_lcdDis(0,0x06,"T");
	num_lcdDis(0,0x07,spid.duration,3);

}   