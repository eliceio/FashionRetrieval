from flask import Flask, redirect, url_for, request
import tensorflow as tf
from train import *
from model import *
from PIL import Image
import cv2

app = Flask(__name__)

gpu_options = tf.GPUOptions(allow_growth=True)

sess = tf.Session(config=tf.ConfigProto(gpu_options=gpu_options))

loader = tf.train.import_meta_graph('./model/model-13850.meta')
loader.restore(sess, tf.train.latest_checkpoint('./model'))
sess.run(tf.global_variables_initializer())

MODEL = DeepLabModel("./deeplabv3_mnv2_pascal_train_aug.tar.gz")

UPLOAD_FOLDER = os.path.basename('uploads')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

@app.route("/")
def eval():
	return "A"

   
@app.route('/upload', methods=['GET', 'POST'])
def upload():
    file = request.files['file']
    img = Image.open(file)
    arr = file.stream.read()
    img = img.resize((64,64))
    img.save("uploads/before.jpg")
    img = np.asarray(img)
    
    img = img.reshape(1,64,64,3)
    print(img.shape)

    output = sess.run("Image-Output:0", feed_dict={"Image-Input0:0": img})
    output = np.array(output)
    print(output.shape)
    output = output[0].reshape(64, 64, 3)
    print(output.shape)
    output = output * 255
    output = output.astype(np.uint8)
    result = Image.fromarray(output)
    result.save("uploads/result.jpg")
    return result
    
if __name__ == "__main__":
	app.run(host='192.168.10.101', port=8000, debug=True)


#     f = os.path.join(app.config['UPLOAD_FOLDER'], file.filename)
#     file.save(f)
#return render_template('index.html')
