import traceback  
import BjguahaoClient
from bs4 import BeautifulSoup as BS
import re
import Conn
import time
import requests
from _winapi import NULL
from datetime import datetime
from datetime import timedelta 

class GetDoctorInfo(object):

    homeurl = "http://www.bjguahao.gov.cn"
    appointMainurl = "http://www.bjguahao.gov.cn/hp/appoint/142.htm" #北医三院
    appointPosturl = "http://www.bjguahao.gov.cn/dpt/partduty.htm"
    hospitalId="142"
    departmentId="0"
    doctorId=""
    dutySourceId = "0"
    patientId = ""
    keshiurl = ""
    keshiName = ""
    appDate = ""
    appTime = ""
    client = NULL
    appOk = False
    tryCounter = 0;
    conn_ = NULL;
    TimeDic ={1:"上",2:"下"}
    WeekDic = {"Monday":"周一","Tuesday":"周二","Wednesday":"周三",
    "Thursday":"周四","Friday":"周五",
    "Saturday":"周六","Sunday":"周日"
    }

    
    def __init__(self):
 
        self.conn_=Conn.conn("BJGuahao")
        self.client = BjguahaoClient.BjguahaoClient()
        
            
    def getDepartmentId(self):
        #以下，找到对应科室的预约网址
        soup = BS(self.client.open(self.appointMainurl).text, "html.parser")
        self.keshiurl = self.homeurl
        for x in soup.find_all("a", class_="kfyuks_islogin",text=re.compile(self.keshiName)):
            if x.get_text() == self.keshiName:
                self.keshiurl += x["href"]               
                res = self.client.open(self.keshiurl).text
                soup = BS(res, "html.parser")
                self.departmentId = soup.find(id="dId")["value"]
                print("正在打开["+x.get_text()+"]\t科室代码["+self.departmentId+"]")
                print("=" * 50)                
                break
    def saveDoctorInfo(self,skill,doctorTitleName,doctorId,avaliableTime):
            self.conn_.sqlStr_ = "select avialbeDate from doctorInfo where doctorId="+str(doctorId)+ " and keshiName='"+self.keshiName+"'"
            res = self.conn_.select() 
            try:
                if  not len(res):
                    sqlstr = "insert into doctorInfo (hospitalName,hospitalID,keshiName,skill,doctorTitleName,doctorId,avialbeDate) VALUES('北京大学第三医院',142,'"+self.keshiName+"','"+str(skill)+"','"+str(doctorTitleName)+"','"+str(doctorId)+"','"+str(avaliableTime)+"')"
                    print("insert"+doctorTitleName+"["+doctorId+"]"+skill)
                    self.conn_.sqlStr_ = sqlstr
                    res = self.conn_.insert()
                else:
                    old = str(res[0][0]);
                    new = old.replace(avaliableTime,"")+avaliableTime
                    sqlstr = "update doctorInfo  set avialbeDate='"+new+"' where doctorId='"+str(doctorId)+"'"
                    print("update"+doctorTitleName+"["+doctorId+"]"+skill)
                    self.conn_.sqlStr_ = sqlstr
                    res = self.conn_.update()
            except :  
                traceback.print_exc()
    def updateDoctorName(self,keshiName,avilableDate):
        avilableDate = avilableDate.split('、')
        self.conn_.sqlStr_ = "select doctorId,doctorTitleName,skill,avialbeDate,doctorName "+"from doctorInfo where  keshiName='"+keshiName+"'"
        for x in avilableDate:
            self.conn_.sqlStr_ +="  and avialbeDate like '%"+x+"%'"
        print(self.conn_.sqlStr_)
        res = self.conn_.select() 
        a = 0
        for x in res:
            doctorName = "--"
            if x[4] != None:
                doctorName=x[4]
            print("["+str(a)+"]"+x[1]+"["+doctorName+"]\t"+x[2]+"\t"+x[3])
            a +=1
        indStr = input("请选择序号\r\n")
        while not indStr.isnumeric():
            if indStr == "":
                return
            indStr = input("输入非法，请重新输入\r\n")
        ind = int(indStr)
        print("已选择：\t["+str(ind)+"]["+res[ind][0]+"]\t"+res[ind][2]+"\t"+res[ind][3])
        name = input("请输入姓名\r\n")
        self.conn_.sqlStr_ = "update doctorInfo  set doctorName='"+name+"' where doctorId='"+res[ind][0]+"'"
        print(self.conn_.sqlStr_)
        res = self.conn_.update()
         
    def getDoctorIdDict(self,name):
        self.keshiName = name;
        self.getDepartmentId()  
        now = datetime.now()
        for x in range(1,8):
            delta = timedelta(days=x)
            appDate = now + delta
            dateStr = appDate.strftime('%Y-%m-%d')
            weekStr = self.WeekDic[appDate.strftime('%A')]
            for dutyCode in range(1,3):
                data = {
                "hospitalId":self.hospitalId,
                "departmentId": self.departmentId,
                "dutyCode":dutyCode,
                "dutyDate":dateStr,
                "isAjax":"true"
                }
                time.sleep(1)
                try:
                    res = self.client.getSession().post(self.appointPosturl, data=data)
                    if(res.json()["hasError"]==False):
                        info = res.json()["data"]
                        print("="*20+dateStr+weekStr+self.TimeDic[dutyCode]+"午有"+str(len(info)-1)+"个专家应诊")
                        for  x in info:
                            
                            if x["doctorTitleName"] !="普通专业号5元":
                                self.saveDoctorInfo(x["skill"],x["doctorTitleName"],x["doctorId"],str(weekStr+self.TimeDic[dutyCode]))
                    else:
                        print("访问"+dateStr+self.TimeDic[dutyCode]+"返回错误")
                     
                except:
                    print("提交"+dateStr+self.TimeDic[dutyCode]+"时产生错误")
                    res.text
                
                
               
if __name__ == '__main__':
    instence = GetDoctorInfo()
    department = {1:"妇科门诊",2:"风湿免疫门诊",3:"口腔科门诊",4:"内分泌门诊",5:"运动医学门诊"}
    mode = "acq"
    #acq用来自动获取医生信息，但是没有医生名字
    #update 用来更新医生名字
    if mode =="acq":
        for x in range(5,len(department)+1):
            instence.getDoctorIdDict(department[x])
    if mode =="update" :   
        print(department)
        ind = input("请输入科室序号\r\n")
        keshiName  = department[int(ind)]
        print("已选择科室：\t"+keshiName)
        getInput  = input("请输入出诊时间\r\n")
        while getInput != "":
            instence.updateDoctorName(keshiName,getInput)
            getInput = input("请输入出诊时间\r\n")
    print("=" * 50) 
    print("=" * 50) 
    print("=" * 23+"结束"+"=" * 23) 
    print("=" * 50) 
    print("=" * 50)     
    time.sleep( 2000 )
        