#include "public.h"		

void UartFunction(unsigned char *rec){//rec 1-7  x @ x cmd xx data  '\0'
 
	 unsigned int v_data;	 
	 v_data =  rec[2]*256+rec[3] ;
 
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
	 }
}

unsigned char checksumCalc(uchar rec[])
{ 
	return ((unsigned char)rec[0])^((unsigned char)rec[1])^((unsigned char)rec[2])^((unsigned char)rec[3]);
}