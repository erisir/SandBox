 
from datetime import datetime
from datetime import timedelta

now = datetime.now()
TimeStr ={
    1:"上午",
    2:"下午"
    }
WeekStr = {
    "Monday":"周一","Tuesday":"周二","Wednesday":"周三",
    "Thursday":"周四","Friday":"周五",
    "Saturday":"周六","Sunday":"周日"
    }
for x in range(1,8):
    delta = timedelta(days=x)
    appDate = now + delta
    dateStr = appDate.strftime('%Y-%m-%d')
    weekStr = WeekStr[appDate.strftime('%A')]
    print(weekStr)
    for t in range(1,3):
        apTime = t
        