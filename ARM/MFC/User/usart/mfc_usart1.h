#ifndef __USART1_H
#define	__USART1_H

#include "stm32f10x.h"
#include <stdio.h>

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
#define _U_SetPIDMode 	  'b'
#define _U_SetPIDPeriod   'c'

void USART1_Config(void);
void NVIC_Configuration(void);
int fputc(int ch, FILE *f);
int fgetc(FILE *f);
unsigned char cmd_ready(void);
void parseCMD(void);
#endif /* __USART1_H */
