
#include "stm32f10x.h"
#define     PWM_DUTY        65535            //定义PWM的周期，数值为时钟周期数，假如使用24.576MHZ的主频，则PWM频率为6000HZ。

#define     PWM_HIGH_MIN    0              //限制PWM输出的最小占空比。用户请勿修改。
#define     PWM_HIGH_MAX    (PWM_DUTY-PWM_HIGH_MIN) //限制PWM输出的最大占空比。用户请勿修改。

extern unsigned int PWM_high;               //PWM空比寄存器，即PWM输出高电平的PCA时钟脉冲个数（占空比写入变量）。
void TIM3_Breathing_Init(void);
void LoadPWM(unsigned int pwm);
