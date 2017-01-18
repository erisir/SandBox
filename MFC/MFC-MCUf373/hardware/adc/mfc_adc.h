#ifndef __ADC_H
#define	__ADC_H


#include "stm32f37x.h"
 
uint32_t SDADC1_Config(void);
void GetPosition(void);
float GetADCVoltage(unsigned char ch);
void SetVotageTimes(float val);
float ADC_Filter(unsigned char ch) ;
int16_t ADC_Mean(unsigned char ch);

#endif /* __ADC_H */

