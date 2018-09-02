import six.moves.cPickle as Pickle
import os


dataset_dir = '/home/ec2-user/SageMaker/data/'
clothes = []

for filename in os.listdir(dataset_dir):
    if filename.endswith('.jpg'):
        clothes.append(filename)

print(len(clothes))


with open('retrieval.pkl', 'wb') as cloth_table:
    Pickle.dump(clothes, cloth_table)

print('done')