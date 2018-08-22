#hello_world.py

from flask import Flask
import tensorflow as tf
from train import *
from model import *
from PIL import Image
app = Flask(__name__)
gpu_options = tf.GPUOptions(allow_growth=True)

sess = tf.Session(config=tf.ConfigProto(gpu_options=gpu_options))

loader = tf.train.import_meta_graph('./model/model-500.meta')
loader.restore(sess, tf.train.latest_checkpoint('./model'))
sess.run(tf.global_variables_initializer())

MODEL = DeepLabModel("./deeplabv3_mnv2_pascal_train_aug.tar.gz")


@app.route("/")
def eval():
    test_set = (read_testset(MODEL))

    print(np.array(test_set).shape)

    test_output = sess.run("Image-Output:0", feed_dict={"Image-Input0:0": test_set})
    test_output = np.array(test_output)
    print(test_output.shape)
    test_output = test_output[0].reshape(64, 64, 3)
    print(test_output.shape)
    test_output = test_output * 255
    test_output = test_output.astype(np.uint8)
    Image.fromarray(test_output).show()
    return "AA"
