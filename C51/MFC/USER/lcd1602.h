#ifndef __lcd1602_H_
#define __lcd1602_H_


#define uchar unsigned char
#define uint  unsigned int


void lcd1602_init();
void lcd_Write_com(uchar com);

void hz_lcdDis(uchar x,uchar y,uchar *p);
void num_lcdDis(uchar X,uchar Y,uint num,uchar n);


#endif