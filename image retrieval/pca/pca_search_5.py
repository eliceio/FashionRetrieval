
# coding: utf-8

# In[9]:


get_ipython().run_line_magic('run', 'load.py')


# In[117]:


import sklearn.preprocessing
import numpy as np
import pandas as pd
import scipy.spatial.distance
import operator
from scipy import linalg
from sklearn.decomposition import PCA
import matplotlib as mpl
import matplotlib.pyplot as plt
from PIL import Image
import matplotlib.gridspec as gridspec



def PIL2array(img):
    return np.array(img.getdata(),
                    np.uint8).reshape(img.size[1], img.size[0], 3)
def plot(samples):
      
    fig = plt.figure(figsize=(60,30))
    gs = gridspec.GridSpec(1,5)
    gs.update(wspace=0.05, hspace=0.05)

    for i, p in enumerate(samples):
        ax = plt.subplot(gs[i])
        plt.axis('off')
        p = p.astype(np.uint8)
        a = plt.imshow(p)


# In[86]:


sample = sample[:8726]
dataset = dataset[:8726]
print(len(sample))


# In[87]:


def get_closest_img(maps,PID,num):
    
    
    PID = input('write input pic_name including jpg or png :',)

    PATH = 'C://Users/HSS/lookbook/lookbook/data/'+ PID

    sam = loadImage(PATH)
    d = np.array(sam).astype(np.uint8)

    # print(type(npsam))
    print(d.shape)
    # print(npsam)
    #print(npsam.shape)
    # print(d.shape)
#     print(type(sample))
#     print(type(d))
    maps = maps.tolist()
    maps.append(np.array(d))
    
    blank = []
    for x in range(len(maps)):
        blank.append(np.array(maps[x]))
        if x % 100 == 0:
            print(x,"/",str(len(maps)))
    print('done')


    blank = np.array(blank)
    print(blank.shape)
    tmp = blank.reshape(blank.shape[0],3*64*64)
    print(tmp.shape)
    print('this will take a while, ready for the impact!')
    pca_model = PCA(n_components = 6)
    sample_array = pca_model.fit_transform(tmp)
    dataset.append(PATH)
    
    new_dict = {}
    
    for i in range(len(dataset)):
        new_dict[dataset[i]] = sample_array[i]
    print(new_dict)
    
  
    distance = {}
    standard = new_dict[PATH]
    
#     for i in range(len(new_dict)): 
#         distance[new_dict[i]] = scipy.spatial.distance.euclidean(standard, new_dict[i])
    print(len(new_dict))
    
    for i in new_dict.keys():
        distance[i] = scipy.spatial.distance.euclidean(standard, new_dict[i])
    sorted_distance = sorted(distance.items(), key = operator.itemgetter(1))
    result = sorted_distance[0:num]
   
    blank2 = []
    a=0
    print('appending started')

#     for ii in range(len(result)):
#         imgs=[Image.open(i) for i in result[ii]]
#         blank = []
#         for x in imgs:
#             blank.append(PIL2array(x))
#         blank2.append(blank)
#         a+=1
#         print(a,'/10')
    
#     result = plot(blank2)


    return result


# In[88]:


print(get_closest_img(sample, 'sample2.png',5))


# In[184]:




