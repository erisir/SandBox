
#include "../user/public.h"

#ifndef __Uart1_H__
#define __Uart1_H__

void Uart1Init(); 
void SendData(unsigned   char dat);
void SendString(unsigned   char *s);
void SendInt(unsigned long   int v);
 
void parseCMD(); 
bit  cmd_ready();
 


#endif
