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
class Common:   
    cookieFile = os.path.join(sys.path[0], "cookie")
    def __init__(self,headers):
        os.chdir(sys.path[0])  # 设置脚本所在目录为当前工作目录
        self.__session = requests.Session()
        self.__session.headers = headers  # 用self调用类变量是防止将来类改名
        # 若已经有 cookie 则直接登录
        self.__cookie = self.__loadCookie()
        if self.__cookie:
            self.__session.cookies.update(self.__cookie)
                
    def __saveCookie(self):
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

    def open(self, url, delay=0, timeout=4):
        """打开网页，返回Response对象"""
        if delay:
            time.sleep(delay)
        return self.__session.get(url, timeout=timeout)

    def getSession(self):
        return self.__session     
    
    def downLoadFile(self,url,path):
        temp = url.split("/")
        path = path+"/"+temp[len(temp)-1]
        data = requests.get(url)
        if data.status_code == 200:
            with open(path,'wb') as f:
                for chunk in data:
                    f.write(chunk)             