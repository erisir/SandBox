import demjson
text = "{\"data\":[{\"hospitalName\":\"北京大学第三医院\",\"departmentName\":\"口腔科门诊\",\"patientName\":\"农大官\",\"idTypeName\":\"身份证\",\"patientIdNo\":\"452627198807202573\",\"recognitionCode\":\"26089906\",\"dutyDate\":\"2016-12-12\",\"ampm\":\"上午\",\"offerTime\":\"7:00-11:00\",\"offerAddress\":\"五官科楼一层挂号处取号\",\"numericalSequence\":\"1\",\"temporalSequence\":\"08:00——09:00\",\"needTs\":true,\"needNs\":true}],\"hasError\":false,\"code\":200,\"msg\":\"OK\"}"
res =  demjson.encode(text)
print(text.json()["data"])