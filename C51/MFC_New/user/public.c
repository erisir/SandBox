#include "public.h"	
#include "../hardware/Uart1.h"		
#include "intrins.h"
void UartFunction(unsigned char *rec){//rec 1-7  x @ x cmd xx data  '\0'
	 int a  = rec[0];
/* 
     switch(rec[1]){
	  	  case _U_SetVotage:SetSetPoint(v_data);break;//0 

		  case _U_SetPTerm:SetPIDparam_P_inc(v_data);break;//2

		  case _U_SetITerm:SetPIDparam_I_inc(v_data);break;//4
 
		  case _U_SetDTerm:SetPIDparam_D_inc(v_data);break;//6
 
		  case _U_SetDura:SetPIDparam_Dura_inc(v_data);break;//8
		   
		  case _U_SetPWMVal:PWM1_set(v_data);//SetPWMValue(v_data);break;//8
		  
		  case _U_GetVotage:GetPosition();break;//8

		  case _U_SetTClose:SetTClose();break;//8

		  case _U_SetTOpen:SetTOpen();break;//8

		  case _U_SetTPID:SetTPID();break;//8

		  case _U_SetVotageTimes:SetVotageTimes(v_data);break;//8
		  
 								
		  default:break;
	 }	   */
}

u8 checksumCalc(unsigned   char rec[],u8 cmd_len)
{ 
	char a  = 	cmd_len + rec[0];
	return 1;//((u8)rec[0])^((u8)rec[1])^((u8)rec[2])^((u8)rec[3]);
}
void Delay100ms()		//@11.0592MHz
{
	unsigned char i, j, k;

	_nop_();
	_nop_();
	i = 5;
	j = 52;
	k = 195;
	do
	{
		do
		{
			while (--k);
		} while (--j);
	} while (--i);
}
void Delay1000ms()		//@11.0592MHz
{
	unsigned char i, j, k;

	_nop_();
	_nop_();
	i = 43;
	j = 6;
	k = 203;
	do
	{
		do
		{
			while (--k);
		} while (--j);
	} while (--i);
}
 
void itoa (unsigned long int n,unsigned char s[])
{
	unsigned char i,j,temp;
	unsigned long int sign = n;
	if(n<0) n=-n;//使n成为正数

	i=0;
	do{
	      s[i++]=n%10+'0';//取下一个数字
	}
	while ((n/=10)>0);//删除该数字
	
	if(sign<0){
		s[i++]='-';
	}
	s[i+1]= 0;
	i--;
	for(j=i;j>=i/2;j--)//生成的数字是逆序的，所以要逆序输出
	{
		temp = s[i-j] ;
		s[i-j] = s[j];
		s[j] = temp;
	}    
} 