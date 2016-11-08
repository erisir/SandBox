
/*------------------------------------------------------------------*/
/* --- STC MCU International Limited -------------------------------*/
/* --- STC 1T Series MCU RC Demo -----------------------------------*/
/* --- Mobile: (86)13922805190 -------------------------------------*/
/* --- Fax: 86-0513-55012956,55012947,55012969 --------------------*/
/* --- Tel: 86-0513-55012928,55012929,55012966 ---------------------*/
/* --- Web: www.GXWMCU.com -----------------------------------------*/
/* --- QQ:  800003751 ----------------------------------------------*/
/* If you want to use the program or the program referenced in the  */
/* article, please specify in which data and procedures from STC    */
/*------------------------------------------------------------------*/

#include    <reg52.h>
#include    "PWM.h"


/*************  功能说明    **************

本程序演示使用定时器做软件PWM。

定时器0做16位自动重装，中断，从T0CLKO高速输出PWM。

本例程是使用STC15F/L系列MCU的定时器T0做模拟PWM的例程。

PWM可以是任意的量程。但是由于软件重装需要一点时间，所以PWM占空比最小为32T/周期，最大为(周期-32T)/周期, T为时钟周期。

PWM频率为周期的倒数。假如周期为6000, 使用24MHZ的主频，则PWM频率为4000HZ。

******************************************/

#define     MAIN_Fosc       24576000L      //定义主时钟



typedef     unsigned char   u8;
typedef     unsigned int    u16;
typedef     unsigned long   u32;

sfr P3M1  = 0xB1;   //P3M1.n,P3M0.n     =00--->Standard,    01--->push-pull
sfr P3M0  = 0xB2;   //                  =10--->pure input,  11--->open drain
sfr AUXR  = 0x8E;
sfr INT_CLKO = 0x8F;

sbit    P_PWM = P3^5;       //定义PWM输出引脚。
//sbit  P_PWM = P1^4;       //定义PWM输出引脚。STC15W204S

u16     pwm;                //定义PWM输出高电平的时间的变量。用户操作PWM的变量。

u16     PWM_high,PWM_low;   //中间变量，用户请勿修改。

void    delay_ms(unsigned char ms);
 


/******************** 主函数**************************/
void PWMn_init()
{
    P_PWM = 0;
    P3M1 &= ~(1 << 5);  //P3.5 设置为推挽输出
    P3M0 |=  (1 << 5);

//  P1M1 &= ~(1 << 4);  //P1.4 设置为推挽输出   STC15W204S
//  P1M0 |=  (1 << 4);

    TR0 = 0;        //停止计数
    ET0 = 1;        //允许中断
    PT0 = 1;        //高优先级中断
    TMOD &= ~0x03;  //工作模式,0: 16位自动重装
    AUXR |=  0x80;  //1T
    TMOD &= ~0x04;  //定时
    INT_CLKO |=  0x01;  //输出时钟

    TH0 = 0;
    TL0 = 0;
    TR0 = 1;    //开始运行

    EA = 1;

    pwm = PWM_DUTY/ 10;    //给PWM一个初值，这里为10%占空比
    LoadPWM(pwm);           //计算PWM重装值

 
}

 
/**************** 计算PWM重装值函数 *******************/
void    LoadPWM(u16 i)
{
    u16 j;

   // if(i > PWM_HIGH_MAX)        i = PWM_HIGH_MAX;   //如果写入大于最大占空比数据，则强制为最大占空比。
   // if(i < PWM_HIGH_MIN)        i = PWM_HIGH_MIN;   //如果写入小于最小占空比数据，则强制为最小占空比。
    j = 65536UL - PWM_DUTY + i; //计算PWM低电平时间
    i = 65536UL - i;            //计算PWM高电平时间
    EA = 0;
    PWM_high = i;   //装载PWM高电平时间
    PWM_low  = j;   //装载PWM低电平时间
    EA = 1;
}

/********************* Timer0中断函数************************/
void timer0_int (void) interrupt 1
{
    if(P_PWM)
    {
        TH0 = (u8)(PWM_low >> 8);   //如果是输出高电平，则装载低电平时间。
        TL0 = (u8)PWM_low;
    }
    else
    {
        TH0 = (u8)(PWM_high >> 8);  //如果是输出低电平，则装载高电平时间。
        TL0 = (u8)PWM_high;
    }
}

