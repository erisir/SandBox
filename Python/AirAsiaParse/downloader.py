import ParseAirAsia
#3电影15图片
depart = "PEK"
dest = "DPS"
url = ""
date = "2017-05-22"
ADT = "4"
index = "https://booking.airasia.com/Flight/Select?"
url = index+"o1="+depart+"&d1="+dest+"&culture=zh-CN&dd1="+date+"&ADT="+ADT+"CHD=0&inl=0&s=true&mon=true&cc=CNY&c=false"
instance = ParseAirAsia.ParseAirAsia(url)
instance.start()
    
    