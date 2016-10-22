#include <reg52.h>

#ifndef STEPMOTOR_20131219
#define STEPMOTOR_20131219

				   	   	  
sbit _rotationDirectionPort	= P2^0;
sbit _directionPort 		= P2^1;
sbit _rotationPlusePort		= P2^2;
sbit _plusePort         	= P2^3;

sbit _manualUpPort  		= P2^4;
sbit _manualDownPort  		= P2^5;
sbit _manualRotaUpPort  	= P2^6;
sbit _manualRotaDownPort 	= P2^7;

sbit _manualAcceleratePort  = P1^0;
sbit _manualRotaAcceleratePort	= P1^1;

sbit _releasePort     		= P1^4;
sbit _lowLimitPort 	        = P1^2;
sbit _highLimitPort    		= P1^3;

#define uchar unsigned char  //0~255
#define uint unsigned int	 //0^65535
#define ulong unsigned long
#define bool bit
#define true 1
#define false 0


//error code


#define DEVICE_OK 0x00  
#define DEVICE_BUSY 0x00 +'A'
#define OUT_OF_LOW_LIMIT 0x00 +'B'
#define OUT_OF_HIGH_LIMIT 0x00 +'C'
#define CHECK_SUM_ERROR 0x00  +'D'
#define BAD_COMMAND	    0x00 +'E'

/*协议格式

@ 起始
U 命令 ,moveUP等
X
X
X
X 以上四位为命令参数，需要转码
X 校验位，使用异或校验 
*/
//command string		以下
#define SetZeroPosition 0x00 +'F'
#define MoveUp	        0x00 +'G'
#define MoveDown	    0x00 +'H'
#define SetRunningDelay 0x00 +'I'
#define SetStartDelay 	0x00 +'J'
#define FindLimit		0x00 +'K'
#define ReleasePower	0x00 +'L'
#define QueryPosition   0x00 +'M'
#define QueryStage   	0x00 +'N'
#define SetPosition	    0x00 + 'O' 
#define SetUM2Step	    0x00 + 'P'
#define GetUM2Step	    0x00 + 'Q'
#define SetAngel2Step   0x00 + 'R'
#define GetAngel2Step   0x00 + 'S'
#define SetDivMode		0x00 + 'T'

#define QueryAngel	    0x00 + 'U'
#define SetZeroAngel    0x00 +'V'
#define SetAngel        0x00 +'W'

/*------------------------------------------------
                   函数声明
------------------------------------------------*/
void parseCMD(uchar rec[]);
bool checksum(uchar rec[]);
uchar SetStagePosition(long step);
uchar SetStageAngel(long recData);
uchar SendPluse(long step);
uchar SendAngelPluse(long step);
uchar FindUpLimit(bit flag);
uchar Move(long step,bit flag);
void refLCD();
bool InitDevice();
bool checkBoundary();
uchar checksumCalc(uchar rec[]);

void ltoa(long step,uchar* str);
void longToRaw(long step,uchar* str);
void ManualMove(bit deriction,bit flag);
void ManualMove1(bit deriction,bit flag);
void delay(uchar interval);
void delay_ms(uchar xms);

void debug(uchar rec[]);
#endif
