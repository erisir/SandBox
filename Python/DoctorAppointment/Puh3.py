import traceback  
import BjguahaoClient
from bs4 import BeautifulSoup as BS
import re
import Conn
import time
import datetime
import requests
from _winapi import NULL
 

class Puh3(object):

    homeurl = "http://www.bjguahao.gov.cn"
    appointMainurl = "http://www.bjguahao.gov.cn/hp/appoint/142.htm" #北医三院
    appointPosturl = "http://www.bjguahao.gov.cn/dpt/partduty.htm"
    hospitalId="142"
    departmentId="0"
    doctorId=""
    outOfService = False#第一个没抢着
    dutySourceId = "0"
    patientId = ""
    keshiurl = ""
    keshiName = "普外门诊"
    appDate = ""
    appTime = ""
    client = NULL
    appOk = False
    tryCounter = 0;
    conn_ = NULL;
    doctorName = ""
    
    userDict = {
        "农大官":"218874960",
        "张宇微":"225572924"
        }
    doctorDict = {
        "ts":"201122688",
        #妇科
        "田惠":"201105238",
        "李华":"201121994",
        "耿力":"201105234",
        "张璐芳":"201105996",
        "王晓晔":"201111378",
        "韩劲松":"201112760",
        "王威":"201109446",
        "王秀云":"201105994",
        "郭红燕":"201105700",
        "刘朝辉":"201105704",
        "杨池荪":"201105706",
        "梁华茂":"201105992",
        "朱馥丽":"201121662",
        #风湿免疫
        "姚中强":"201105228",
        "邓晓莉":"201105120",
        "刘湘源":"201109444",
        #口腔
        "陈晓红":"201112316",
        "徐莉":"201119156",
        "李志刚":"201105756",
        "张平":"201105332",
        "张玉苹":"201105334",
        "黎远皋":"201105326",
        "郑旭":"201105760",
        "王霄":"201105758",
        #内分泌
        "雷天光":"201113806",
        "谢超":"201111434",
        "王海宁":"201105796",
        "邓正照":"201105792",
        "洪天配":"201111432",
        "肖文华":"201105388",
        "高洪伟":"201105794",
        "王敏":"201106050",
        #妇科
        "test":"201105712",
        "正畸":"201123194"
        }
    
    def __init__(self,patientName,date,time,deparmentName,doctorName):
        self.keshiName = deparmentName
        self.appDate = date
        self.doctorName = doctorName
        self.patientId = self.userDict[patientName]
        self.doctorId = self.doctorDict[doctorName]
        print("="*50)
        print("\r\n即将为["+patientName+"]预约 "+self.appDate+" "+time+"["+deparmentName+"]的["+doctorName+"]")
        self.conn_=Conn.conn("BJGuahao")
        #2_2_2016-11-10 行_上1/下2午_日期
        if time == "上午" :
            self.appTime = "1"
        if time == "下午" :
            self.appTime = "2"
            
        self.client = BjguahaoClient.BjguahaoClient()
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
                print("." * 50)                
                break
            
        # 若已经有 cookie 则直接登录
    def getSMSCode(self):
        getsmscodeurl = self.homeurl+"/v/sendorder.htm"
        smsSaveUrl = "http://sm4.iphy.ac.cn/g.php"
        smsCoder =requests.get(smsSaveUrl) 
        smsCoder = smsCoder.text
 
        while smsCoder !="0":#清零
                smsCoder =  requests.get(smsSaveUrl).text
 
        print("[02]正在发送验证码")
        res = self.client.getSession().post(getsmscodeurl,data="")#点击获取验证码按钮
        acqmode = "auto"
        print("[03]正在尝试接收验证码\r\n")
        if acqmode == "auto" :
            while smsCoder =="0":
                res1 =  requests.get(smsSaveUrl) 
                smsCoder = res1.text
                 
        else :
            smsCoder = input("请输入手机中的验证码\r\n")
                      
        return smsCoder 
    
    def confirm(self,smsVerifyCode):
        data = {"dutySourceId":self.dutySourceId,
                "hospitalId":self.hospitalId,
                "departmentId":self.departmentId,
                "doctorId":self.doctorId,
                "patientId":self.patientId,
                "hospitalCardId":"",
                "medicareCardId":"",
                "reimbursementType":"-1",
                "smsVerifyCode":str(smsVerifyCode),
                "isFirstTime":"2",
                "hasPowerHospitalCard":"2",
                "cidType":"1",
                "childrenBirthday":"",
                "childrenGender":"2",
                "isAjax":"true"   
                }
        url = "http://www.bjguahao.gov.cn/order/confirm.htm"
        res = self.client.getSession().post(url, data=data)   
        if res.json()["msg"]== "OK":
            self.appOk = True
            tmp = res.json()["data"][0]
            print("["+tmp["patientName"]+"]\t已经成功预约\t"+tmp["hospitalName"]+"\t["+tmp["departmentName"]+"]\t\r\n"+tmp["dutyDate"]+tmp["ampm"]+"的号")
        else:
            self.outOfService = True
            print(res.json()["msg"])
           
    def start(self):
        
        self.tryCounter += 1
        try:       
            res = self.client.open(self.keshiurl).text
            soup = BS(res, "html.parser")
            
            for avilable in soup.find_all(class_="ksorder_ym"):#检查是否已经放号
                temp = avilable.input["value"].split('_', 3 )
                if temp[2] == self.appDate and  temp[1] == self.appTime:
                    data = {
                    "hospitalId":self.hospitalId,
                    "departmentId": self.departmentId,
                    "dutyCode":self.appTime,
                    "dutyDate":self.appDate,
                    "isAjax":"true"
                    }
                    res = self.client.getSession().post(self.appointPosturl, data=data)
                    backup = res.json()["data"].copy()
              
                    x =backup.pop()
                    while x:
                        if x["doctorId"] == self.doctorId:#目标医生       
                            print("[01]已匹配医生信息\t"+x["doctorTitleName"]+"["+x["skill"]+"]\t  剩余\t["+str(x["remainAvailableNumber"])+"]\t可约")                 
                            self.dutySourceId = x["dutySourceId"]                      
                            smsCode = self.getSMSCode()
                            print("[04]已收到验证码["+str(smsCode)+"]，正在提交订单")
                            self.confirm(smsCode)   
                            break
                        
                        x =backup.pop()
                       
                    break
                
            if(not self.appOk):
                print(".",end='-',flush=True)
                if self.tryCounter >=25:
                    self.tryCounter = 0
                    print("\n")
        except:
            print("连接错误")

               
if __name__ == '__main__':
    today = time.strftime("%y-%m-%d",time.localtime())
    appDate = (datetime.date.today() + datetime.timedelta(days=7)).strftime("%Y-%m-%d")
    today+= " "
    targetTime = today+"09:29:30" 
    releasedTime = today+"09:30:30" 
    
    print("正在等待\t"+targetTime)
    while True:
        if time.localtime()>time.strptime(targetTime, "%y-%m-%d %H:%M:%S"):
            break
        time.sleep(10)
        print(time.strftime("%y-%m-%d %H:%M:%S",time.localtime()))
    department = {1:"妇科门诊",2:"风湿免疫门诊",3:"口腔科门诊",4:"内分泌门诊",5:"运动医学门诊"}
    appInfo = ["张宇微,上午,1,张璐芳"]
    #时间，医生一一对应，按自然优先级抢号
    counter = 0
    instence = [0,1]
    for x in appInfo:
        a = x.split(",")
        instence[counter] = Puh3(a[0],appDate,a[1],department[int(a[2])],a[3])
        counter += 1
        print("*"*50)
    print("*"*50+"开始刷["+instence[0].doctorName+"]的号")
    timeOut = False
    while not (instence[0].appOk or instence[0].outOfService or timeOut):
        instence[0].start()
        timeOut = time.localtime()>time.strptime(releasedTime, "%y-%m-%d %H:%M:%S")
        time.sleep( 2 )
    
    if not instence[0].appOk:
        print("\r\n"+"*"*50+"\r\n"+ instence[0].doctorName +"号没了" )
    
   
    for i in range(1,len(appInfo)):
        print("*"*50 + "继续刷"+instence[i].doctorName+"的号")
        instence[i].start()
        time.sleep( 2 )
    
    print("\r\n"+"=" * 50+"结束")     
    while True:
        time.sleep( 2000 )