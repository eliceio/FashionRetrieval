import tensorflow as tf
from PIL import Image
import numpy as np
import os
import matplotlib
matplotlib.use('agg')
import matplotlib.mlab as mlab
import matplotlib.pyplot as plt

gpu_options = tf.GPUOptions(allow_growth=True)

sess = tf.Session(config=tf.ConfigProto(gpu_options=gpu_options))

loader = tf.train.import_meta_graph('./model/model-1400.meta')
loader.restore(sess, tf.train.latest_checkpoint('./model'))

flist = os.listdir("/home/ec2-user/data")
name = []
histo = []
# for f in flist:
#     name.append(f)
    
#     img = Image.open("/home/ec2-user/data/{}".format(f))
#     img = img.resize((64,64))
#     img = np.array(img,dtype=np.float32)
#     img /= 255
    
#     if len(img.shape) != 3:
#         continue
#     img = img.reshape(1,64,64,3)
#     latent = sess.run(["encoder/add:0"], feed_dict={"X:0": img,"Y:0":img})
#     histo.append(latent[0][0])
    
# np.save("name.npy",name)
# np.save('filename.npy', histo)
histo=np.load('filename.npy')
name = np.load("name.npy")

from sklearn.metrics.pairwise import euclidean_distances
# img = Image.open("/home/ec2-user/data/647398.jpg")
img = Image.open("test/6.jpg")
img = img.resize((64,64))

img = np.array(img,dtype=np.float32)
img = img[:,:,:3]
print(img.shape)
img /= 255
img= img.reshape(1,64,64,3)
latent = sess.run(["encoder/add:0"], feed_dict={"X:0": img,"Y:0":img})


dist = euclidean_distances(latent[0],histo)
distance = np.squeeze(dist)
dist = np.argsort(distance)

for i in dist[:10]:
    print(name[i])
    

# for i in range(10):
#     _max = np.percentile(histo[:,i],80)
#     _min = np.percentile(histo[:,i],20)
#     print(_max,_min)
#     hist = histo[:,i]
#     hist[hist>_max] = _max
#     hist[hist<_min] = _min
# #     n, bins, patches = 
#     plt.hist(hist, 50, normed=1, facecolor='green', alpha=0.75)

# #     y = mlab.normpdf( bins, 100, 100)
# #     l = plt.plot(bins, y, 'r--', linewidth=1)

# #     plt.axis([40, 160, 0, 0.03])
# #     plt.grid(True)
#     plt.savefig("abc{}.jpg".format(i))
    #     histo[_max<histo[:,i]]= _max
#     histo[_min>histo[:,i]]= _min

#     output = output.reshape(64,64,3)
#     print(np.max(output),np.min(output))
#     output *= 255
#     output = output.astype(np.uint8)
#     Image.fromarray(output).save("123.jpg")

#     decoder = decoder.reshape(64,64,3)
#     print(np.max(decoder),np.min(decoder))
#     decoder *= 255
#     decoder = decoder.astype(np.uint8)
#     Image.fromarray(decoder).save("124.jpg")
