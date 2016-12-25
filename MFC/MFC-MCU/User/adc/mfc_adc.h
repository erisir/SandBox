#ifndef __ADC_H
#define	__ADC_H


#include "stm32f10x.h"
void ADC1_Init(void);
void GetPosition(void);
unsigned int GetADCVoltage(unsigned char ch);
void SetVotageTimes(unsigned int val);
unsigned int ADC_Filter(unsigned char ch) ;
unsigned int ADC_Mean(unsigned char ch);
#endif /* __ADC_H */

