#include<reg52.h>   
#include<lcd1602.h>
#include <intrins.h>	 
#define uchar unsigned char
#define uint unsigned int


sbit RS	= P2^3;
sbit RW	= P2^4;
sbit E = P2^5; 


/**************短延时*****************/
void delay5ms()   
{
    uchar i,v,k;
    for(i=1;i>0;i--)
        for(v=168;v>0;v--)
            for(k=22;k>0;k--);
}

/***********写指令********************/
void lcd_Write_com(uchar com)  	
{
	RS=0;	                //定义指令寄存器
	RW=0;	          		//写允许
	P0=com;			  		//写指令
	delay5ms();
	E=1;					//片选端上拉
	delay5ms();	
	E=0;					//下降沿锁存
}

/***********写数据********************/
void lcd_write_date(uchar date)	
{
	RS=1;					//定义数据寄存器
	RW=0;					//写允许
	P0=date;				//写数据
	delay5ms();
	E=1;		//片选端上拉
	delay5ms();	
	E=0;		//下降沿锁存
}

/***********显示程序*******************/
/* void dis_lcd1602(uchar x,uchar y,uchar dat)
{
	uchar add;
	if(y==1) 	
		add=(0x80+x);
	if(y==2) 	
		add=(0xc0+x);
	lcd_Write_com(add);		//写指令
	lcd_write_date(dat);	//写数据
}  	*/

/***********液晶初始化*******************/
void lcd1602_init()
{
	lcd_Write_com(0x38);	//设置8位格式，2行，5*7
	lcd_Write_com(0x01);	//清屏
	lcd_Write_com(0x0c);	//整体显示，关光标，不闪烁
	lcd_Write_com(0x06);	//设定输入方式，增量不移位
	lcd_Write_com(0x80);	//初始坐标
}

/***********液晶显示汉字程序*******************/
void hz_lcdDis(uchar x,uchar y,uchar *p) 
{

   uchar i=0,temp;
   if(x)
   	temp = 0x40;   
   else 
   	temp = 0;
   for(i=y;*p!='\0';i++,p++)
   {
		lcd_Write_com(i|0x80+temp);
		lcd_write_date(*p);
		delay5ms(); 	 		
   } 
}

/**************液晶显示数字程序*******************/
void num_lcdDis(uchar X,uchar Y,uint num,uchar n)	
{

	uint i=0,temp,hang;    
	n +=1;
   	if(X)
		hang = 0x40;
	else 
		hang = 0;
   	for(i=(n-1);i>0;i--)
    { 	  
	  	lcd_Write_com((i+Y-1)|0x80+hang);	  //从个位开始显示
  		temp = num%10 +0x30;
  		lcd_write_date(temp);	
	 	delay5ms(); 
	  	num/=10;					 
	}	
}