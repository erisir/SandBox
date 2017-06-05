import requests
import re
import sys
from bs4 import BeautifulSoup
from collections import deque
import Conn
import random
import json
from _winapi import NULL
import urllib  
import os
import time
import unicodedata
import datetime
class ParseAirAsia:
    CityDict = {
    "北京":"PEK",
    "巴厘岛":"DPS",
    "吉隆坡":"KUL"
    }
    minQueue = deque()
    index = "https://booking.airasia.com/Flight/Select?"
    headers = {
        "Accept":"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "Accept-Encoding":"gzip, deflate",
        "Accept-Language":"zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4",
        "Cache-Control":"max-age=0",
        "Connection":"keep-alive",
        "Content-Length":"36",
        "Content-Type":"application/x-www-form-urlencoded",
        "User-Agent":"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36"
    }
    cookieFile = os.path.join(sys.path[0], "cookie")
    def __saveCookie(self):
        """cookies 序列化到文件
        即把dict对象转化成字符串保存
        """
        with open(self.cookieFile, "w") as output:
            cookies = self.__session.cookies.get_dict()
            json.dump(cookies, output)

    def __loadCookie(self):
        """读取cookie文件，返回反序列化后的dict对象，没有则返回None"""
        if os.path.exists(self.cookieFile):
            print("." * 50)
            with open(self.cookieFile, "r") as f:
                cookie = json.load(f)
                return cookie
        return None

    def open(self, url, delay=0, timeout=2):
        """打开网页，返回Response对象"""
        if delay:
            time.sleep(delay)
        return self.__session.get(url, timeout=self.timeout)

    def getSession(self):
        return self.__session

    def __init__(self,depCityName,arrCityName,depDate,adt,daysAround,timeout):
        self.depCityName = depCityName
        self.arrCityName = arrCityName
        self.depDate = depDate
        self.adt = adt
        self.daysAround = daysAround
        self.timeout = timeout
        os.chdir(sys.path[0])  # 设置脚本所在目录为当前工作目录
        self.__session = requests.Session()
        self.__session.headers = self.headers  # 用self调用类变量是防止将来类改名
        # 若已经有 cookie 则直接登录
        self.__cookie = self.__loadCookie()
        if self.__cookie:
            self.__session.cookies.update(self.__cookie)
                
    
    def getDataFromUrl(self,url):
        try:
            return requests.get(url,timeout=self.timeout)
        except:
            return None
      
    def getContent(self,url):
        response = self.getDataFromUrl(url)
        if(None == response):
            print("read NONTHING form current url")
            return   -999   
        response.encoding = 'UTF-8'
        soup=  BeautifulSoup(response.text, "html.parser") 
        content = soup.find('table',class_ ="table avail-table")
        soup = BeautifulSoup(str(content),"html.parser")
        outPutStr = ""
        minFare = 99999
        for x in soup.find_all('table',class_ ="avail-table-info"):
            soup1 = BeautifulSoup(str(x),"html.parser")
            
            outPutStr = ""
            for xx in soup1.find_all('td',class_ ="avail-table-detail"):
                availTime = self.cleanStr(xx.text)
                outPutStr =outPutStr+availTime+"->"
                print(availTime)
            for xx in soup1.find_all('td',class_ ="avail-stops-info"):
                totalFlyTime = self.cleanStr(xx.text)
                outPutStr =outPutStr+"["+totalFlyTime+"]"
            soup1 = BeautifulSoup(str(x.parent.parent),"html.parser")
            for xx in soup1.find_all('div',class_ ="avail-fare-price-container"):
                price = self.cleanStr(xx.text)
                outPutStr =outPutStr+"=="+price+""
                price = price.split('.')
                price = price[0].replace(",","")
                if int(price)<minFare:
                    minFare =  int(price)

                soup2 = BeautifulSoup(str(xx.parent.parent.parent),"html.parser")
                for xxx in soup2.find_all('div',class_ ="avail-table-seats-remaining"):
                    remaining = self.cleanStr(xxx.text)
                    outPutStr =outPutStr+"["+remaining+"]"
            #print('*'*50)
            #print(self.cleanStr(outPutStr))
        print( minFare)
        return minFare
 
      
    def cleanStr(self,str):
            temp =str.replace(" ","")
            temp = temp.replace("停留","")
            temp = temp.replace("->->"," ")
            temp = temp.replace("总飞行时间:","")
            temp = temp.replace("此航费仅剩","")
            temp = temp.replace("个座位","位")            
            temp = temp.replace("\r","")
            temp = temp.replace("\n","")
            return temp
        
  
    def start(self): 
        
        depCityCode = self.CityDict[self.depCityName]
        arrCityCode = self.CityDict[self.arrCityName]
        year = self.depDate.year()
        month = self.depDate.month()
        day = self.depDate.day()
        depDate = datetime.date(year,month,day) 
        queue = deque()       
        for x in range(-1*self.daysAround,self.daysAround+1):
            depDateStr = (depDate + datetime.timedelta(days=x)).strftime("%Y-%m-%d")
            print("Start to parse "+"="+depDateStr)
            depUrl = self.index+"o1="+depCityCode+"&d1="+arrCityCode+"&culture=zh-CN&dd1="+depDateStr+"&ADT="+str(self.adt)+"CHD=0&inl=0&s=true&mon=true&cc=CNY&c=false"
            try:
                minFare =  self.getContent(depUrl)  
            except:
                print ("ERR!")
                minFare = -999
            queue.append(minFare)
        return queue
        