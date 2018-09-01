
# coding: utf-8

# In[1]:


new_dict = {}
for i in range(len(dataset)):
    new_dict[dataset[i]] = sample_array[i]
print(new_dict)


# In[ ]:


from scipy.spatial.distance import cosine

def get_closest_jpg(maps, PID, num):
    new_dict = maps
    PID = 'C://Users/HSS/lookbook/lookbook/data/'+ PID
    distance = {}
    standard = new_dict[PID]
#     for i in range(len(new_dict)): 
#         distance[new_dict[i]] = scipy.spatial.distance.euclidean(standard, new_dict[i])
    print(len(new_dict))
    
    for i in new_dict.keys():
        distance[i] = scipy.spatial.distance.euclidean(standard, new_dict[i])
    sorted_distance = sorted(distance.items(), key = operator.itemgetter(1))
    result = sorted_distance[0:num]
    
    co_distance = {}
    for i in new_dict.keys():
        co_distance[i] = cosine(standard, new_dict[i])
    sorted_co_distance = sorted(co_distance.items(), key = operator.itemgetter(1))
    result2 = sorted_co_distance[0:num]
    
#     result2 = co_distance.sort(key=lambda X: X[1])
    
    print(result)
    return  result



a = 'PID007795_CLEAN1_IID067117.jpg'


# In[ ]:


import time
start_time  = time.time()
search_result = get_closest_jpg(new_dict,'PID000020_CLEAN1_IID000333.jpg', 10 )
print('Time consumed for search :' , time.time() - start_time)


# In[ ]:


from PIL import Image


# In[ ]:


input = 'C://Users/HSS/lookbook/lookbook/data/PID003524_CLEAN1_IID034328.jpg'
Image.open(input)
len(search_result)


# In[ ]:


i = 0
idx = []
while i <= len(search_result)-1:
    idx.append(search_result[i][0])
    i+=1
print(idx)


# In[ ]:


Image.open(idx[0])


# In[ ]:


Image.open(idx[1])


# In[ ]:


Image.open(idx[2])


# In[ ]:


Image.open(idx[3])


# In[ ]:


Image.open(idx[4])


# In[ ]:


Image.open(idx[5])


# In[ ]:


Image.open(idx[6])


# In[ ]:


Image.open(idx[7])


# In[ ]:


Image.open(idx[8])


# In[ ]:


Image.open(idx[9])

