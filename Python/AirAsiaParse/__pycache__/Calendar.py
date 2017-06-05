from PyQt5 import QtCore, QtGui, QtWidgets
class Calendar:
  pass
AppCal = Calendar()
import time
def calcFirstDayOfMonth(year,month,day):
  '''计算某一日的是星期几'''
  months = (0,31,59,90,120,151,181,212,243,273,304,334)
  if 0 <= month <= 12:
    sum = months[month - 1]
  else:
    print 'data error'
  # 对年月做了判断，日只是加了上下限，没有根据月判断输入的是否合法
  if year < 0 or month < 0 or month > 11 or day < 0 or day >31:
    import os
    os._exit(1)
    
  sum += day
  leap = 0
  if (year % 400 == 0) or ((year % 4 == 0) and (year % 100 != 0)):
    leap = 1
  if (leap == 1) and (month > 2):
    sum += 1
  # 先计算某年的第一天是星期几
  # (year + (year - 1)/4 - (year - 1)/100 + (year -1)/400)% 7
  return (sum % 7 - 1 + (year + (year - 1)/4 - (year - 1)/100 + (year -1)/400))% 7
def createMonth(master):
  '''创建日历'''
  for i in range(5):
    for j in range(7):
      Label(master,text = '').grid(row = i + 2,column = j)
def updateDate():
  ''' 更新日历'''
  #得到当前选择的日期
  year = int(AppCal.vYear.get())
  month = int(AppCal.vMonth.get())
  day = int(AppCal.vDay.get())
  months = [31,28,31,30,31,30,31,31,30,31,30,31]  
  # 判断是否瑞年
  if (year % 400 == 0) or ((year % 4 == 0) and (year % 100 != 0)):
    months[1] += 1
  fd = calcFirstDayOfMonth(year,month,1)
  for i in range(5):
    for j in range(7):
      root.grid_slaves(i +2,j)[0]['text'] = ''

  for i in range(1,months[month - 1] + 1):
    root.grid_slaves((i + fd - 1)/7 + 2,(i + fd -1)%7)[0]['text'] = str(i)
  
def drawHeader(master):
  '''添加日历头'''
  # 得到当前的日期，设置为默认值
  now = time.localtime(time.time())
  col_idx = 0
  
  # 创建年份组件
  AppCal.vYear = StringVar()
  AppCal.vYear.set(now[0])
  Label(master,text = 'YEAR').grid(row = 0,column = col_idx);col_idx += 1
  omYear = apply(OptionMenu,(master,AppCal.vYear) + tuple(range(2005,2010)))
  omYear.grid(row = 0,column = col_idx);col_idx += 1

  # 创建月份组件
  AppCal.vMonth = StringVar()
  AppCal.vMonth.set(now[1])
  Label(master,text = 'Month').grid(row = 0,column = col_idx);col_idx += 1
  omMonth = apply(OptionMenu,(master,AppCal.vMonth) + tuple(range(1,12)))
  omMonth.grid(row = 0,column = col_idx);col_idx += 1

  # 创建年份组件
  AppCal.vDay = StringVar()
  AppCal.vDay.set(now[2])
  Label(master,text = 'DAY').grid(row = 0,column = col_idx);col_idx += 1
  omDay = apply(OptionMenu,(master,AppCal.vDay) + tuple(range(1,32)))
  omDay.grid(row = 0,column = col_idx);col_idx += 1

  # 创建更新按钮
  btUpdate = Button(master,text = 'Update',command = updateDate)
  btUpdate.grid(row = 0,column = col_idx);col_idx += 1

  # 打印星期标签
  weeks = ['Sun.','Mon.','Tues.','Wed.','Thurs.','Fri.','Sat.']
  for week in weeks:
    Label(master,text = week).grid(row = 1,column = weeks.index(week))
  
from Tkinter import *
root = Tk()

drawHeader(root)
createMonth(root)
updateDate()

root.mainloop()