#ifndef _PUBLIC_H_ 
#define _PUBLIC_H_

/////////////////////////////////////////////////
#define u8 unsigned char;
#define u16 unsigned int;
#define u32 unsigned long;
/////////////////////////////////////////////////
void UartFunction(unsigned char *rec);
unsigned char checksumCalc(unsigned char rec[]);

#endif
