
import requests
import time
import json
import os
import re
import sys
import subprocess
from bs4 import BeautifulSoup as BS
from warnings import catch_warnings


class BjguahaoClient(object):

    TYPE_PHONE_NUM = "phone_num"
    TYPE_EMAIL = "email"
    loginURL = r"http://www.bjguahao.gov.cn/quicklogin.htm"
    homeURL = r"http://www.bjguahao.gov.cn"
    captchaURL = r"http://www.bjguahao.gov.cn"
    needCaptcha = False
    
    headers = {
        "Accept":"application/json, text/javascript,text/txt, */*; q=0.01",
        "Accept-Encoding":"gzip, deflate",
        "Accept-Language":"zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4",
        "Connection":"keep-alive",
        "Content-Length":"0",
        "Content-Type":"application/x-www-form-urlencoded; charset=UTF-8",
        "Host":"www.bjguahao.gov.cn",
        "Origin":"http://www.bjguahao.gov.cn",
        "Referer":"http://www.bjguahao.gov.cn/order/confirm/142-200039572-201105418-40605496.htm",
        "User-Agent":"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36",
        "X-Requested-With":"XMLHttpRequest",
    }

    captchaFile = os.path.join(sys.path[0], "captcha.gif")
    cookieFile = os.path.join(sys.path[0], "cookie")

    def __init__(self):
        os.chdir(sys.path[0])  # 设置脚本所在目录为当前工作目录

        self.__session = requests.Session()
        self.__session.headers = self.headers  # 用self调用类变量是防止将来类改名
        # 若已经有 cookie 则直接登录
        self.__cookie = self.__loadCookie()
        if self.__cookie:
            try:
                print("检测到cookie文件，直接使用cookie登录")
                self.__session.cookies.update(self.__cookie)
                soup = BS(self.open(self.homeURL).text, "html.parser")
                print("已登陆账号： %s" % soup.find("p", class_="grdbnav_context_right").getText())
                print("=" * 50)
            except:
                print("检测到的cookie文件失效，重新登录")
                self.login("13810617774", "jiushizhu007")
                print("=" * 50)
            
        else:            
            print("没有找到cookie文件，正在调用login方法登录一次！")
            self.login("13810617774", "jiushizhu007")  
            print("=" * 50)
            

    # 登录
    def login(self, username, password):
        """
        验证码错误返回：
        {'errcode': 1991829, 'r': 1, 'data': {'captcha': '请提交正确的验证码 :('}, 'msg': '请提交正确的验证码 :('}
        登录成功返回：
        {'r': 0, 'msg': '登陆成功'}
        """
        self.__username = username
        self.__password = password
        self.__loginURL = self.loginURL


        while True:
            if(self.needCaptcha):
                captcha = self.open(self.captchaURL).content
                with open(self.captchaFile, "wb") as output:
                    output.write(captcha)
                # 人眼识别
                print("=" * 50)
                print("已打开验证码图片，请识别！")
                subprocess.call(self.captchaFile, shell=True)
                captcha = input("请输入验证码：")
                os.remove(self.captchaFile)
            else:
                captcha = ""
            # 发送POST请求
            data = {
                "mobileNo":self.__username,
                "password": self.__password,
                "yzm":"",
                "isAjax":"true"
                #"captcha": captcha
            }
            res = self.__session.post(self.__loginURL, data=data)
            print("=" * 50)
            if res.json()["msg"] == "OK":
                print("登录成功")
                self.__saveCookie()
                break
            else:
                print("登录失败")
                print("错误信息 --->", res.json()["msg"])
        soup = BS(self.open(self.homeURL).text, "html.parser")
        print("已登陆账号： %s" % soup.find("p", class_="grdbnav_context_right").getText())

    def __getUsernameType(self):
        """判断用户名类型
        经测试，网页的判断规则是纯数字为phone_num，其他为email
        """
        if self.__username.isdigit():
            return self.TYPE_PHONE_NUM
        return self.TYPE_EMAIL

    def __saveCookie(self):
        """cookies 序列化到文件
        即把dict对象转化成字符串保存
        """
        with open(self.cookieFile, "w") as output:
            cookies = self.__session.cookies.get_dict()
            json.dump(cookies, output)
            print("=" * 50)
            print("已在同目录下生成cookie文件：", self.cookieFile)

    def __loadCookie(self):
        """读取cookie文件，返回反序列化后的dict对象，没有则返回None"""
        if os.path.exists(self.cookieFile):
            print("=" * 50)
            with open(self.cookieFile, "r") as f:
                cookie = json.load(f)
                return cookie
        return None

    def open(self, url, delay=0, timeout=10):
        """打开网页，返回Response对象"""
        if delay:
            time.sleep(delay)
        return self.__session.get(url, timeout=timeout)

    def getSession(self):
        return self.__session

