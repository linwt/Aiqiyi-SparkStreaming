#coding=UTF-8
import random
import time

url_paths = [
   "www/2",
   "www/1",
   "www/6",
   "www/4",
   "www/3",
   "www/5",
   "pianhua/130",
   "tuokouxiu/821"
]

status_code =[404,302,200]

ip_slices=[132,156,124,10,29,167,143,187,30,100]

http_referers = [
"https://www.baidu.com/s?wd={query}",
"https://www.sogou.com/web?qu={query}",
"http://cn.bing.com/search?q={query}",
"https://search.yahoo.com/search?p={query}"
]

search_keyword = [
  "猎场",
  "快乐人生",
  "极限挑战",
  "我的体育老师",
  "幸福满院"
]
#ip地址
def sample_ip():
	slice = random.sample(ip_slices,4)
	return ".".join([str(item) for item in slice])
	
def sample_url():
	return random.sample(url_paths,1)[0]
def sample_status():
    return random.sample(status_code,1)[0]
def sample_referer():
	if random.uniform(0,1) > 0.2:
		return "-"
	refer_str = random.sample(http_referers,1)
	#print refer_str[0]
	query_str = random.sample(search_keyword,1)
	#print query_str[0]
	return refer_str[0].format(query=query_str[0])
   
	
#产生log
def generate_log(count=10):
	time_str = time.strftime("%Y-%m-%d %H:%M:%S",time.localtime())
	#f = open("E:\\aiqiyi_hadoop_spark\\code\\logs","w+")
	#f = open("C:\\code\\logs","w+")
	#f = open("/home/centos/log/log","a+")
	f = open("/home/hadoop/aiqiyi_logs/log","a+")
	while count >= 1:
		query_log = "{ip}\t{localtime}\t\"GET {url} HTTP/1.0\"\t{referece}\t{status1}".format(ip=sample_ip(),url=sample_url(),status1=sample_status(),referece=sample_referer(),localtime=time_str)
		#print (query_log)
		f.write(query_log+"\n")
		count = count-1;


if __name__ == '__main__':
	generate_log()
	