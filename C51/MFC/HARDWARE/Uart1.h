/********************************************************************
本程序只供学习使用，未经作者许可，不得用于其它任何用途
程序结构参考 安徽师范大学  Lyzhangxiang的EasyHW OS结构设计
uart1.h
作者：bg8wj
建立日期: 2010.10.23
版本：V1.0

Copyright(C) bg8wj
/********************************************************************/
#ifndef _UART1_H_ 
#define _UART1_H_
extern void UartInit();
extern void UartInit1();
extern void send_char_com(unsigned char ch);
extern void send_string_com(unsigned char *str);
void UartFunction(unsigned char *rec);
void send_int_com(unsigned int v);
unsigned char checksumCalc(unsigned char rec[]);

#endif