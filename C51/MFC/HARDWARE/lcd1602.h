#ifndef __lcd1602_H_
#define __lcd1602_H_
#define uchar unsigned char
#define uint  unsigned int


void LCD_Initial(void);
void LCD_print(uchar row,uchar column,uchar *str);


#endif