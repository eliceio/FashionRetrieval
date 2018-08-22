import requests
import os
from bs4 import BeautifulSoup

musinsaURL = "https://store.musinsa.com/app/product/detail/{}/0"


def requestURL(numberOfItem):
    request = requests.get(musinsaURL.format(numberOfItem))
    html = request.text
    bs = BeautifulSoup(html)
    category = bs.select('.item_categories')

    if len(category) == 0 :
        return -1

    if "상의" in category[0].text:

        price = bs.select("#sale_price")
        name = bs.select(".product_title > span")
        material = bs.select("div.product_info_table > table > tbody > tr > td")

        if len(price) == 0 or len(material[6].text)==0:
            return -1
        # print(material[6].text)
        # print(name[0].text)
        # print(price[0].text.replace(",",""))
        price = price[0].text.replace(",","")
        material = material[6].text.replace("\n"," ")
        name = name[0].text

        txt = open("./musinsa/annotations.txt","a")
        txt.write("{} & {} & {} & {}\n".format(numberOfItem,material,name,price))

        images = bs.select(".product_thumb > li > img")

        # makedir(numberOfItem)

        item_img_src = []

        for image in images:                # 상품 이미지
            item_img_src.append(image.get("src"))


        return item_img_src
    return -1

def download_Itemimg(numberofitem,items):
    if items == -1:
        return

    for idx,item in enumerate(items):
        item = item.replace("_60.jpg","_500.jpg")
        URL = item[2:]
        fileformat = item[len(item)-3:]
        data = requests.get("http://"+URL,stream=True)

        with open("/home/suka/eliceproject_dataset/musinsa/image/"+str(numberofitem)+"."+fileformat, "wb") as file:
            for chunk in data.iter_content(chunk_size=1024*36):
                file.write(chunk)


def makedir(numberofitem):
    homedirectory = "/home/suka/eliceproject_dataset/" + str(numberofitem)
    if not os.path.exists(homedirectory):
        os.mkdir(homedirectory)
    if not os.path.exists(homedirectory+"/item"):
        os.mkdir(homedirectory + "/item")
    if not os.path.exists(homedirectory+"/staff"):
        os.mkdir(homedirectory + "/staff")
    return

def main():
    for numberOfItem in range(802500,820000):
        print(numberOfItem)
        itemsrc = requestURL(numberOfItem)
        # download_Itemimg(numberOfItem,itemsrc)
    return


if __name__ == "__main__":
    main()