#include "StepMotorDriver.h"
#include "SerialDriver.h"
#include "1602LCDDriver.h" 
#include <stdlib.h>
#include <string.h>

/*------------------------------------------------
                   变量声明
------------------------------------------------*/
bool isBusy = false;
uchar	startdelay = 60;
uchar	runningdelay = 0; 
uchar	rota_startdelay = 100;
uchar	rota_runningdelay = 0; 
float   currPosition = 0;//nm
float   currAngel = 0;//nm
float	step2Um = 1.0;//0.3064;//0.124601;	   //40xf
float   step2Angel = 1.0;//0.3064;
bit	    isSetZero = 0;	
uchar str[12];
uchar ret;

/*	 协议：

void debug(char rec[])
{
	uchar d = 0x00;
	rec[2] = 0x00;
	rec[3] = 0x00;
	rec[4] = 0x00;
	rec[5] = 0x00;

	switch(rec[1]){
	case SetZeroPosition:
	break;
	case MoveUp:
	rec[5] = 0xFF;
	break;
	case MoveDown:
	rec[5] = 0xFF;
	break;
	case SetRunningDelay:
	rec[5] = 0x00;
	break;
	case SetStartDelay:
	rec[5] = 0x10;
	break;
	case FindLimit:
	rec[5] = d;
	break;
	case ReleasePower:
	rec[5] = d;
	break;
	case SetPosition:
	rec[5] = 0x10;
	break;
	case SetUM2Step:
	rec[5] = 0x01;
	rec[4] = 0x00;
	rec[3] = 0x01;
	rec[2] = 0x00;
	break;
	}
	rec[6] = checksumCalc(rec);
} */
/************************************************************

 ************************************************************/
bool checksum(uchar rec[])//rec[] = @ C XXXX C
{
	if(rec[6] == checksumCalc(rec))
		return 1;
	else
		return 0;
}
 
uchar checksumCalc(uchar rec[])
{ 
	return ((uchar)rec[0])^((uchar)rec[1])^((uchar)rec[2])^((uchar)rec[3])^((uchar)rec[4])^((uchar)rec[5]);
}
uchar Move(long step,bit flag)
{
    long i = 0;
    for(i=0;i<step;i++)
	{
		ManualMove(flag,1);
		delay(255);

	}
	refLCD();

} 
void parseCMD(uchar rec[])
{	

	long recData = 0;
	char cmd = 0;
	ret = DEVICE_OK;
	//debug(rec);
	if( 0 == checksum(rec) ){
		ret = CHECK_SUM_ERROR;
	}else{
 
		cmd =  rec[1];
		
		if(rec[2] ==2){recData =  rec[3]*256*256+rec[4]*256+rec[5];}
		if(rec[2] ==0){recData =  -1*((long)(rec[3]*256*256+rec[4]*256+rec[5]));}
		 
		switch(cmd){

		case QueryPosition:
			longToRaw(currPosition,str);
			str[0] = 0;	
			SendStr(str);
			return;
			break;
		case GetUM2Step:
			longToRaw(step2Um*1000.0,str);
			str[0] = 0;	
			SendStr(str);
			return;
			break;
		case GetAngel2Step:
			longToRaw(step2Angel*1000.0,str);
			str[0] = 0;	
			SendStr(str);
			return;
			break;
		case QueryAngel:
			longToRaw(currAngel,str);
			str[0] = 0;
			SendStr(str);
			return;
			break;
		case SetPosition:
		    
		 	ltoa(recData,str);
	 		LCD_Printf1(str);
	 	    ret =  SetStagePosition(recData);
			break;
		case SetAngel:

		 	ltoa(recData,str);
	 		LCD_Printf1(str);
	 	    ret =  SetStageAngel(recData);
			break;

		case QueryStage:	
			SendStr("DEADN");
			return;
			break;
		case SetUM2Step:
			step2Um = recData/1000.0;
			ltoa(step2Um*1000,str);
			LCD_Printf1(str);
			rec = DEVICE_OK;
			break;
		case SetAngel2Step:
			step2Angel = recData/1000.0;
			ltoa(step2Angel*1000,str);
			LCD_Printf1(str);
			rec = DEVICE_OK;
			break;	  
		case SetZeroPosition:
			currPosition = 0;
			ltoa(0,str);
	 		LCD_Printf1(str);
			break;
		case SetZeroAngel:
			currAngel = 0;
			ltoa(0,str);
			LCD_Printf1(str);
			break;

	 	case MoveUp:
			ltoa(recData,str);
			LCD_Printf1(strcat(str,"-MU"));
			ret = Move(10,0);
			break;

		case MoveDown:
			ltoa(recData,str);
			LCD_Printf1(strcat(str,"-MD"));
			ret = Move(10,1);
			break;	  

		case SetDivMode:
			if(recData == 2){ step2Um = 2.491516;}
			if(recData == 4){ step2Um = 1.245758;}
			if(recData == 8){ step2Um = 0.622879;}
			if(recData == 16){ step2Um = 0.31144;}
			if(recData == 64){ step2Um = 0.15572;}
			if(recData == 128){ step2Um = 0.07786;}

			if(recData == 5){  step2Um = 0.996806;}
			if(recData == 10){ step2Um = 0.498403;}
			if(recData == 20){ step2Um = 0.249201;}
			if(recData == 40){ step2Um = 0.124601;}
			if(recData == 50){ step2Um = 0.099681;}
			if(recData == 100){ step2Um = 0.04984;}
			if(recData == 125){ step2Um = 0.039872;}

			ltoa(recData,str);
	 		LCD_Printf1(strcat(str,"-SETDM"));
			break;
		
		case SetRunningDelay:
			rota_runningdelay = recData;
			ltoa(runningdelay,str);
	 		LCD_Printf1(strcat(str,"-SETRD"));
			break;

		case SetStartDelay:
			rota_startdelay = recData;
			ltoa(startdelay,str);
	 		LCD_Printf1(strcat(str,"-SETSD"));
			break;

		case FindLimit:
			FindUpLimit(recData);
			break;

		case ReleasePower:
			if(recData == 1){_releasePort = 1;}
			if(recData == 0){_releasePort = 0;}
			break;
		default:
			ret = BAD_COMMAND;
			break;
		}
	}

	SendErr(ret);
	str[0]  = ret;
	str[1] = '\0';
	if(ret != DEVICE_OK){		
 	LCD_Printf1(strcat(str,"--ERROR!"));		
	}

 	refLCD();
	
}

uchar SetStagePosition(long pos)
{	 

	if(pos - currPosition>0){  //down
   	
	_directionPort = 0;
	delay(100);
	return SendPluse((pos - currPosition)/step2Um);

	}else{
	_directionPort = 1;
	delay(100);
	return SendPluse((currPosition - pos)/step2Um);
	}
 	 
}

uchar SetStageAngel(long pos)
{

	if(pos - currAngel>0){  //down
	_rotationDirectionPort = 0;
	delay(100);
	return SendAngelPluse((pos - currAngel)/step2Angel);

	}else{
	_rotationDirectionPort = 1;
	delay(100);
	return SendAngelPluse((currAngel - pos)/step2Angel);
	}

}

/************************************************************

 ************************************************************
uchar FindUpLimit(bit flag)
{
	if(flag == 0){
	_directionPort = 0;
	LCD_Printf1("FindUpLimit");	
	while(_highLimitPort == 1)
	{
		ManualMove(0,1);//up fast
	}
	currPosition =  -29760;	
	}
 if(flag == 1){
	_directionPort = 1;
		LCD_Printf1("FindlowLimit");	
		while(_lowLimitPort== 1)
		{
			ManualMove(1,1);
		}
	currPosition = 0;
	}
	ltoa(currPosition,str);
	LCD_Printf2(str);
	refLCD();
	return DEVICE_OK;
}
/************************************************************

 ************************************************************/
void ManualMove(bit deriction,bit flag)//deriction 0 up,1 down,flag 1 fast 0 low
{

	uchar _interval = 0;

	if(deriction ==0)
		currPosition += step2Um;
	else{
		currPosition -= step2Um;
	}

	if(flag ==1)
		_interval = runningdelay;
	else
		_interval = startdelay;

	_plusePort = 1;
	delay(_interval);
	_plusePort = 0;
	delay(_interval);
}
void ManualMove1(bit deriction,bit flag)//deriction 0 up,1 down,flag 1 fast 0 low
{

	uchar _interval = 0;

	if(deriction ==0)
		currAngel += step2Angel;
	else{
		currAngel -= step2Angel;
	}

	if(flag ==1)
		_interval = rota_runningdelay;
	else
		_interval = rota_startdelay;

	_rotationPlusePort = 1;
	delay(_interval);
	_rotationPlusePort = 0;
	delay(_interval);

}
/************************************************************

 ************************************************************/
uchar SendPluse(long step)
{
 
	isBusy =1;
	if(_directionPort == 0){ //down		 
			while(step>0){
			_plusePort = 1;
			currPosition += step2Um;
			delay(runningdelay);
			_plusePort = 0;
			delay(runningdelay);//正常行行
			step--;
			}
	}else{
 		while(step>0){
			_plusePort = 1;
			currPosition -= step2Um;
			delay(runningdelay);
			_plusePort = 0;
			delay(runningdelay);//正常行行
			step--;
			}
	}
	isBusy =0;
	return DEVICE_OK;
}
uchar SendAngelPluse(long step)
{
	isBusy =1;
	if(_rotationDirectionPort == 0){ //down		 
			while(step>0){
			_rotationPlusePort = 1;
			currAngel += step2Angel;
			delay(runningdelay);
			_rotationPlusePort = 0;
			delay(runningdelay);//正常行行
			step--;
			}
	}else{
 		while(step>0){
			_rotationPlusePort = 1;
			currAngel -= step2Angel;
			delay(runningdelay);
			_rotationPlusePort = 0;
			delay(runningdelay);//正常行行
			step--;
			}
	}	 
	isBusy =0;
	return DEVICE_OK;

}
bool checkBoundary()
{
	return   true;//( _directionPort  ==  0 &&_highLimitPort == 1) || (currPosition<0 &&_directionPort  ==  1 && _lowLimitPort== 1);
}

/************************************************************

 ************************************************************/
void delay(uchar _interval)
{
	uchar i=0;
	for(i;i<_interval;i++);	
}

/************************************************************

 ************************************************************/
void delay_ms(uchar xms) //ms级延时子程序
{ 
	uchar x,y; 
	for(x=xms;x>0;x--)
		for(y=130;y>0;y--);
}

void refLCD(  )
{ 
	ltoa(currPosition,str);
	LCD_Printf2(str);
}
/************************************************************

 ************************************************************/
bool InitDevice()
{
	isBusy = false;  
	//FindUpLimit(0);			
	return true;
}
/************************************************************

 ************************************************************/
void ltoa(long value,uchar* str)
{ 
   int  i,  j;
   if(value ==0){
   str[0]='0';
   str[1]='\0';
   return;
   }
   if(value < 0) 
    {
        str[0] = '-';
        value = 0-value;
    }else{
        str[0] = '+';
    }

    for(i=1; value > 0; i++,value/=10){ //从value[1]开始存放value的数字字符，不过是逆序，等下再反序过来
        str[i] = (char) (value%10+'0'); //将数字加上0的ASCII值(即'0')就得到该数字的ASCII值
	}
	str[i+1] = '\0';
    	   
}

/************************************************************

 ************************************************************/
void longToRaw(long step,uchar* str)
{
	if(step>0){	str[1] =  2;} 
	if(step<0){	str[1] =  0; step*=-1;}
	str[4] =  step%256;
	step /= 256;
	str[3] =  step%256;
	step /= 256;
	str[2] =  step%256;	 
}
