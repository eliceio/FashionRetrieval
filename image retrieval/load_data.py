
# coding: utf-8

# In[1]:


print('kernel started successfully')


# In[ ]:


import six.moves.cPickle as Pickle
import os

print('loading data..')
dataset_dir = 'C://Users/HSS/lookbook/lookbook/data'
models = []
clothes = []

for filename in os.listdir(dataset_dir):
    if filename.endswith('.jpg'):
        if filename.split('_')[1].endswith('0'):
            models.append(filename)
        else:
            clothes.append(filename)

print(len(models))
print(len(clothes))
print('appending data..')
i = 0
match = []
while i < len(clothes):
    pid = clothes[i][3:9]
    match_i = []
    j = 0
    while j < len(models):
        if models[j][3:9] == pid:
            match_i.append(models[j])
        j += 1
    match.append(match_i)
    i += 1
    if i % 200 == 0:
        print(i)

with open('cloth_table.pkl', 'wb') as cloth_table:
    Pickle.dump(clothes, cloth_table)
with open('model_table.pkl', 'wb') as model_table:
    Pickle.dump(match, model_table)

print('done')


# In[ ]:


import six.moves.cPickle as Pickle
import torch as th
import cv2
import numpy as np
import os


# In[ ]:


def loadImage(path):
    inImage_ = cv2.imread(path)
    inImage = cv2.cvtColor(inImage_, cv2.COLOR_RGB2BGR)
    info = np.iinfo(inImage.dtype)
    inImage = inImage.astype(np.float) / info.max

    iw = inImage.shape[1]
    ih = inImage.shape[0]
    if iw < ih:
        inImage = cv2.resize(inImage, (64, int(64 * ih/iw)))
    else:
        inImage = cv2.resize(inImage, (int(64 * iw / ih), 64))
    inImage = inImage[0:64, 0:64]
    return th.from_numpy(2 * inImage - 1).transpose(0, 2).transpose(
        1, 2
    )


class LookbookDataset():
    def __init__(self, data_dir, index_dir):
        self.data_dir = data_dir
        with open(index_dir+'cloth_table.pkl', 'rb') as cloth:
            self.cloth_table = Pickle.load(cloth)
        with open(index_dir+'model_table.pkl', 'rb') as model:
            self.model_table = Pickle.load(model)

        self.cn = len(self.cloth_table)
        self.path = data_dir

    def getbatch(self, batchsize):
        batch1 = []
        batch2 = []
        batch3 = []
        for i in range(batchsize):
            seed = th.randint(1, 100000, (1,)).item()
            th.manual_seed((i+1)*seed)
            r1 = th.randint(0, self.cn, (1,)).item()
            r2 = th.randint(0, self.cn, (1,)).item()
            r1 = int(r1)
            r2 = int(r2)
            mn = len(self.model_table[r1])
            r3 = th.randint(0, mn, (1,)).item()
            r3 = int(r3)

            path1 = self.cloth_table[r1]
            path2 = self.cloth_table[r2]
            path3 = self.model_table[r1][r3]
        
            img1 = loadImage(self.path + path1)
            img2 = loadImage(self.path + path2)
            img3 = loadImage(self.path + path3)
            batch1.append(img1)
            batch2.append(img2)
            batch3.append(img3)
        return th.stack(batch1), th.stack(batch2), th.stack(batch3)
    
dataset = LookbookDataset(data_dir = "C://Users/HSS/lookbook/lookbook/data",index_dir = 'C://Users/HSS/lookbook/tool/')


# In[ ]:


dataset = []
for i in (clothes):
    new_path  = 'C://Users/HSS/lookbook/lookbook/data/'+str(i)
    dataset.append(new_path)
print(dataset)


# In[ ]:


len(dataset)
sample = []
i = 0
while i <= len(dataset)-1:
    if i%100 == 0:
        print(str(i)+'/'+str(len(dataset))+'completed')
    sample.append(loadImage(dataset[i]))
    i += 1


# In[ ]:


import sklearn.preprocessing
import numpy as np
import pandas as pd
import scipy.spatial.distance
import operator
from scipy import linalg
from sklearn.decomposition import PCA
import matplotlib as mpl
import matplotlib.pyplot as plt


# In[ ]:


for x in range(len(sample)):
    sample[x] = np.array(sample[x])
    if x % 100 == 0:
        print(x,"/",str(len(sample)))
print('done')


# In[ ]:


sample = np.array(sample)
print(sample.shape)
sample = sample.reshape(8726,3*64*64)


# In[ ]:


print('this will take a while, ready for the impact!')
pca_model = PCA(n_components = 6)

sample_array = pca_model.fit_transform(sample)


# In[ ]:


# if you want to visuallize
# len(sample_array)
x = []
y = []
z = []
for i in range(len(sample_array)):
    x.append(sample_array[i][0])
    y.append(sample_array[i][1])


# In[ ]:


plt.scatter(x,y,marker = '+', norm = 0)

