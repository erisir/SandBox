/********************************************************************
������ֻ��ѧϰʹ�ã�δ��������ɣ��������������κ���;
����ṹ�ο� ����ʦ����ѧ  Lyzhangxiang��EasyHW OS�ṹ���
uart1.h
���ߣ�bg8wj
��������: 2010.10.23
�汾��V1.0

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