/*file name
#include "public.h"
*/
#ifndef _PUBLIC_H_ 
#define _PUBLIC_H_
 
typedef  unsigned char u8 ;
typedef  unsigned int u16;
typedef  unsigned long u32;
 
void UartFunction(unsigned   char *rec);
u8 checksumCalc(unsigned   char rec[],u8 cmd_len);
void Delay100ms();
void Delay1000ms();
void itoa (unsigned long int n,unsigned char s[]);

#endif
