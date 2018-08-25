from model import *
from dataset import *
import tensorflow as tf
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec
from deeplab import *
from read_tfrecord import *
import time

class PixelDTgan():
    def __init__(self,converter,discriminator,discriminatorA,data,MODEL=None):
        self.converter = converter
        self.discriminator = discriminator
        self.discriminatorA = discriminatorA

        self.MODEL = MODEL

        self.data = data
           
        # data
        self.size = self.data.size
        self.channel = self.data.channel
        
        #learning rate
        self.lr = tf.placeholder(tf.float32,shape=[])
        
        #input data
        self.X = tf.placeholder(tf.float32, shape=[None, self.size, self.size, self.channel],name="Image-Input0")
        self.un_Y = tf.placeholder(tf.float32,shape=[None,self.size,self.size,self.channel],name="Image-Input1")
        self.Y = tf.placeholder(tf.float32,shape=[None,self.size,self.size,self.channel],name="Image-Input2")
        
        # nets
        self.G = self.converter(self.X)
        print(self.G)
        self.G = tf.identity(self.G, name="Image-Output")
        print(self.G)

        self.D_ass   = self.discriminator(self.Y)
        self.D_noass = self.discriminator(self.un_Y,reuse=True)
        self.D_fake  = self.discriminator(self.G,reuse=True)
        
        self.A_ass   = self.discriminatorA(tf.concat([self.X,self.Y],3))
        self.A_noass = self.discriminatorA(tf.concat([self.X,self.un_Y],3),reuse=True)
        self.A_fake  = self.discriminatorA(tf.concat([self.X,self.G],3),reuse=True)

        # loss
        self.D_loss = (tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=self.D_ass, labels=tf.ones_like(self.D_ass)))+ tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=self.D_noass, labels=tf.ones_like(self.D_noass)))+tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=self.D_fake, labels=tf.zeros_like(self.D_fake))))/3
                                     
        self.A_loss = (tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=self.A_ass, labels=tf.ones_like(self.A_ass)))+ tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=self.A_noass, labels=tf.zeros_like(self.A_noass)))+tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=self.A_fake, labels=tf.zeros_like(self.A_fake))))/3

        self.C_loss = (tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=self.D_fake, labels=tf.ones_like(self.D_fake)))+tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=self.A_fake, labels=tf.ones_like(self.A_fake))))/2
        
        
        # solver
        self.D_optimizer = tf.train.AdamOptimizer(beta1=0.5,learning_rate=self.lr).minimize(self.D_loss, var_list=self.discriminator.vars)
        self.A_optimizer = tf.train.AdamOptimizer(beta1=0.5,learning_rate=self.lr).minimize(self.A_loss, var_list=self.discriminatorA.vars)
        self.C_optimizer = tf.train.AdamOptimizer(beta1=0.5,learning_rate=self.lr).minimize(self.C_loss, var_list=self.converter.vars)

        self.saver = tf.train.Saver()
        gpu_options = tf.GPUOptions(allow_growth=True)

        self.sess = tf.Session(config=tf.ConfigProto(gpu_options=gpu_options))

    def train(self,training_epoch=1000000,batch_size=128):
        self.sess.run(tf.global_variables_initializer())

        start_point = -1
        model = tf.train.get_checkpoint_state("./model")
        if model and tf.train.checkpoint_exists(model.model_checkpoint_path):
            self.loader = tf.train.import_meta_graph(model.model_checkpoint_path+".meta")
            self.loader.restore(self.sess,model.model_checkpoint_path)
            start_point = int(model.model_checkpoint_path.split('/')[-1].split('-')[-1].split(".")[0])
        print("Start Training !!")
#         coord = tf.train.Coordinator()
#         threads = tf.train.start_queue_runners(sess=self.sess,coord=coord)

        for epoch in range(start_point+1,training_epoch):
            start_time = time.time()
            # ass_label, noass_label, img = self.data.get(batch_size)
            # ass_label, noass_label, img = self.data.getdata(batch_size)
            ass_label, noass_label, img = self.data.getbatch(batch_size)
            # ass_label, noass_label, img = self.sess.run([ass_label, noass_label, img])


#             ass_label = scaling_img(np.array(ass_label))
#             noass_label = scaling_img(np.array(noass_label))
            img = scaling_img(np.array(img))
            
            D_loss_curr, _ = self.sess.run([self.D_loss,self.D_optimizer],feed_dict={self.X: img, self.Y : ass_label,self.un_Y:noass_label,self.lr:0.0002/3})
            A_loss_curr, _ = self.sess.run([self.A_loss,self.A_optimizer],feed_dict={self.X: img, self.Y : ass_label,self.un_Y:noass_label,self.lr:0.0002/3})
            C_loss_curr, _ = self.sess.run([self.C_loss,self.C_optimizer],feed_dict={self.X: img, self.Y : ass_label,self.lr:0.0002/2})


            print('epoch: {}; C loss: {:.4},D loss: {:.4},A loss: {:.4} total loss : {} sec : {:.4}'.format(epoch, C_loss_curr, D_loss_curr, A_loss_curr,C_loss_curr+D_loss_curr+A_loss_curr,time.time()-start_time))

            if epoch%50 == 0:
                # test_set = scaling_img(read_testset(self.MODEL))
                # test_set[test_set==0] = 1
                # test_output = self.sess.run(self.G,feed_dict={self.X:test_set})
                # fig = testplot(test_set,test_output)
                # plt.savefig('outputs/test/test{}.png'.format(epoch), bbox_inches='tight')
                # plt.close(fig)
                
                outputs = self.sess.run(self.G,feed_dict={self.X:img})
                fig = plot(outputs[0:10],img[0:10],ass_label[0:10])
                plt.savefig('outputs/{}.png'.format(epoch), bbox_inches='tight')
                plt.close(fig)
                 
            if epoch%50== 0:
                self.saver.save(self.sess, './model/model', global_step=epoch)

#         coord.request_stop()

#         coord.join(threads)


def main():
    #MODEL = DeepLabModel("./deeplabv3_mnv2_pascal_train_aug.tar.gz")

    # dataset = Dataset()
    converter = Converter()
    discriminator = Discriminator()
    discriminatora = DiscriminatorA()
    data = LookbookDataset(data_dir="/home/suka/dataset/lookbook5/data/",index_dir="/home/suka/PycharmProjects/pixelDTgan/")


    # run
    pixeldtgan = PixelDTgan(converter, discriminator,discriminatora,data)
    pixeldtgan.train()

if __name__=="__main__":
    main()
