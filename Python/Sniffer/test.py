import requests

smsSaveUrl = "http://sm4.iphy.ac.cn/g.php"
falg = True
while falg:
    res1 =  requests.get(smsSaveUrl) 
    smsCoder = res1.text
    if int(smsCoder)>1:
        falg = False
    print(smsCoder) 