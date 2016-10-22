#include<reg52.h>   
#include<lcd1602.h>
#include <intrins.h>	 
#define uchar unsigned char
#define uint unsigned int


sbit RS	= P2^3;
sbit RW	= P2^4;
sbit E = P2^5; 


/**************����ʱ*****************/
void delay5ms()   
{
    uchar i,v,k;
    for(i=1;i>0;i--)
        for(v=168;v>0;v--)
            for(k=22;k>0;k--);
}

/***********дָ��********************/
void lcd_Write_com(uchar com)  	
{
	RS=0;	                //����ָ��Ĵ���
	RW=0;	          		//д����
	P0=com;			  		//дָ��
	delay5ms();
	E=1;					//Ƭѡ������
	delay5ms();	
	E=0;					//�½�������
}

/***********д����********************/
void lcd_write_date(uchar date)	
{
	RS=1;					//�������ݼĴ���
	RW=0;					//д����
	P0=date;				//д����
	delay5ms();
	E=1;		//Ƭѡ������
	delay5ms();	
	E=0;		//�½�������
}

/***********��ʾ����*******************/
/* void dis_lcd1602(uchar x,uchar y,uchar dat)
{
	uchar add;
	if(y==1) 	
		add=(0x80+x);
	if(y==2) 	
		add=(0xc0+x);
	lcd_Write_com(add);		//дָ��
	lcd_write_date(dat);	//д����
}  	*/

/***********Һ����ʼ��*******************/
void lcd1602_init()
{
	lcd_Write_com(0x38);	//����8λ��ʽ��2�У�5*7
	lcd_Write_com(0x01);	//����
	lcd_Write_com(0x0c);	//������ʾ���ع�꣬����˸
	lcd_Write_com(0x06);	//�趨���뷽ʽ����������λ
	lcd_Write_com(0x80);	//��ʼ����
}

/***********Һ����ʾ���ֳ���*******************/
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

/**************Һ����ʾ���ֳ���*******************/
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
	  	lcd_Write_com((i+Y-1)|0x80+hang);	  //�Ӹ�λ��ʼ��ʾ
  		temp = num%10 +0x30;
  		lcd_write_date(temp);	
	 	delay5ms(); 
	  	num/=10;					 
	}	
}