
#include "../user/public.h"

#ifndef __Uart1_H__
#define __Uart1_H__

#define _U_SetVotage	 '0'
#define _U_SetPTerm      '1'
#define _U_SetITerm      '2'
#define _U_SetDTerm	     '3'
#define _U_SetDura       '4'
#define _U_SetPWMVal     '5'
#define _U_GetVotage     '6'
#define _U_SetTClose     '7'
#define _U_SetTOpen      '8'
#define _U_SetTPID       '9'
#define _U_SetVotageTimes 'a'
#define _U_SetVotageChanel 'b'

void Uart1Init(); 
void SendData(unsigned   char dat);
void SendString(unsigned   char *s);
void SendInt(unsigned    int v[]);
unsigned char checksumCalc(unsigned   char rec[]); 
void parseCMD(); 
bit  cmd_ready();
 
 
void SendInt1(unsigned int setV,unsigned int pwm);
 

#endif
