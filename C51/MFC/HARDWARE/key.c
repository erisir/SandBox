#include "STC15W.h"
#include "key.h"
#include "pwm.h"
#define KeyPort P3	  //data口  

sbit KEY0	= P1^5;
sbit KEY1	= P2^6;
sbit KEY2	= P2^7;


sbit KEY3	= P2^6;
sbit KEY4	= P2^7;

void DelayUs2x(unsigned char t);	//us级延时函数声明 
unsigned char KeyScan(void);		//键盘扫描
unsigned char KeyPro(void);			//键盘输出值

 
/****************按键扫描函数，返回扫描键值*****************/
unsigned char KeyScan(void)  //键盘扫描函数，使用行列反转扫描法
{
	unsigned char cord_h,cord_l;	//行列值中间变量
	KeyPort=0x0f;            		//行线输出全为0
	cord_h=KeyPort&0x0f;     		//读入列线值
 	if(cord_h!=0x0f)    			//先检测有无按键按下
	 {
		  DelayMs(10);       		//去抖
		  if((KeyPort&0x0f)!=0x0f)
			  {
				    cord_h=KeyPort&0x0f; 	//读入列线值
				    KeyPort=cord_h|0xf0; 	//输出当前列线值
				    cord_l=KeyPort&0xf0; 	//读入行线值
				
				    while((KeyPort&0xf0)!=0xf0);	//等待松开并输出
				
				    return(cord_h+cord_l);	//键盘最后组合码值
			   }
	 }
	return(0xff);     //返回该值
}

/****************按键的输出值*****************/
unsigned char KeyPro(void)	 
{

	if(KEY0 == 0){
      DelayMs(10); 
	  if(KEY0 == 0)	return 9;
	  }

	if(KEY1 == 0){
      DelayMs(10); 
	  if(KEY1 == 0)	return 0;
	  }
	if(KEY2 == 0){
      DelayMs(10); 
	  if(KEY2 == 0)	return 1;
	  }

    if(KEY3 == 0){
      DelayMs(10); 
	  if(KEY3 == 0)	return 0;
	  }
	if(KEY4 == 0){
      DelayMs(10); 
	  if(KEY4 == 0)	return 1;
	  }


	switch(KeyScan())
	 {
		  case 0x7e:return 0;break;//0 按下相应的键显示相对应的码值
		  case 0x7d:return 1;break;//1
		  case 0x7b:return 2;break;//2
		  case 0x77:return 3;break;//3
		  case 0xbe:return 4;break;//4
		  case 0xbd:return 5;break;//5
		  case 0xbb:return 6;break;//6
		  case 0xb7:return 7;break;//7
		  case 0xde:return 8;break;//8
		  case 0xdd:return 9;break;//9
		  case 0xdb:return 10;break;//a
		  case 0xd7:return 11;break;//b
		  case 0xee:return 12;break;//c
		  case 0xed:return 13;break;//d
		  case 0xeb:return 14;break;//e
		  case 0xe7:return 15;break;//f
		  default:return 0xff;break;
	 }
}  