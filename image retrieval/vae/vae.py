import tensorflow as tf
import tensorflow.contrib.layers as tcl
from dataset import *
import os 

def lrelu(x, leak=0.2, name="lrelu"):
    with tf.variable_scope(name):
        f1 = 0.5 * (1 + leak)
        f2 = 0.5 * (1 - leak)
        return f1 * x + f2 * abs(x)

    
def plot(samples,assimg):
    fig = plt.figure(figsize=(20, 10))
    gs = gridspec.GridSpec(2, 10)
    gs.update(wspace=0.05, hspace=0.05)

    for i, sample in enumerate(samples):
        ax = plt.subplot(gs[i])
        plt.axis('off')
        sample = (sample) * 255
        sample = sample.astype(np.uint8)
        plt.imshow(sample)
    for i, sample in enumerate(assimg):
        ax = plt.subplot(gs[10 + i])
        plt.axis('off')
        sample = (sample) * 255
        sample = sample.astype(np.uint8)
        plt.imshow(sample)

    return fig
    
    
def encoder(X,nef=128):
    n_latent=500
    with tf.variable_scope("encoder",reuse=None):
        X = tf.reshape(X,shape = [-1,64,64,3])
        X = tcl.conv2d(X,num_outputs=nef,kernel_size= 5,stride=2,padding="SAME",activation_fn=lrelu,weights_initializer=tf.random_normal_initializer(0, 0.02))
        X = tcl.conv2d(X, num_outputs=nef*2, kernel_size=5, stride=2, padding="SAME", activation_fn=lrelu,normalizer_fn=tcl.batch_norm,weights_initializer=tf.random_normal_initializer(0, 0.02))
        X = tcl.conv2d(X, num_outputs=nef*4, kernel_size=5, stride=2, padding="SAME", activation_fn=lrelu,normalizer_fn=tcl.batch_norm,weights_initializer=tf.random_normal_initializer(0, 0.02))
        X = tcl.conv2d(X, num_outputs=nef*8, kernel_size=5, stride=1, padding="SAME", activation_fn=lrelu,normalizer_fn=tcl.batch_norm,weights_initializer=tf.random_normal_initializer(0, 0.02))
        X = tf.contrib.layers.flatten(X)
        
        z_mean = tf.layers.dense(X,units=n_latent)
     
        
        z_log_sigma_sq = tf.layers.dense(X,units=n_latent,activation=tf.nn.softplus)
        # net_out2 = DenseLayer(net_h5, n_units=z_dim, act=tf.nn.relu,
        #         W_init = w_init, name='en/h5/lin_sigmoid')

        return z_mean, z_log_sigma_sq


def decoder(sampled_z, ndf=128):
    inputs_decoder = 8*8*ndf*8/2

    with tf.variable_scope("decoder", reuse=None):
        x = tf.layers.dense(sampled_z, units=inputs_decoder, activation=lrelu)
        x = tf.layers.dense(sampled_z, units=inputs_decoder*2, activation=lrelu)
        x = tf.reshape(x, [-1,8,8,ndf*8])
        
        x = tcl.conv2d_transpose(x, num_outputs=ndf*4, kernel_size=5, stride=2, padding='same', activation_fn=tf.nn.relu,normalizer_fn=tcl.batch_norm,weights_initializer=tf.random_normal_initializer(0, 0.02))

        x = tcl.conv2d_transpose(x, num_outputs=ndf*2, kernel_size=5, stride=2, padding='same', activation_fn=tf.nn.relu,normalizer_fn=tcl.batch_norm,weights_initializer=tf.random_normal_initializer(0, 0.02))
        x = tcl.conv2d_transpose(x, num_outputs=ndf, kernel_size=5, stride=2, padding='same', activation_fn=tf.nn.relu,normalizer_fn=tcl.batch_norm,weights_initializer=tf.random_normal_initializer(0, 0.02))
        x = tcl.conv2d_transpose(x, num_outputs=3, kernel_size=5, stride=1, padding='same', activation_fn=tf.nn.tanh,weights_initializer=tf.random_normal_initializer(0, 0.02))
        return x
    


def main():
    dataset = MangoDataset("/home/ec2-user/data/")
    X_in = tf.placeholder(dtype=tf.float32, shape=[None, 64, 64, 3], name='X')
    Y    = tf.placeholder(dtype=tf.float32, shape=[None, 64, 64, 3], name='Y')
    Y_flat = tf.reshape(Y, shape=[-1, 64 * 64 * 3])
        
    z_p = tf.random_normal(shape=(128, 500), mean=0.0, stddev=1.0)
    eps = tf.random_normal(shape=(128, 500), mean=0.0, stddev=1.0)


    z_mean, z_log_sigma_sq = encoder(X_in)

    z = tf.add(z_mean, tf.multiply(tf.sqrt(tf.exp(z_log_sigma_sq)), eps)) 
    dec = decoder(z)
    unreshaped = tf.reshape(dec, [-1, 64*64*3])

    SSE_loss = tf.reduce_mean(tf.square(unreshaped - Y_flat))# /FLAGS.output_size/FLAGS.output_size/3
      
    KL_loss = tf.reduce_mean(- 0.5 * tf.reduce_sum(1 + z_log_sigma_sq - tf.square(z_mean) - tf.exp(z_log_sigma_sq),1))

    VAE_loss = 0.005*KL_loss + SSE_loss 
        
    optimizer = tf.train.AdamOptimizer(0.0005,beta1=0.5).minimize(VAE_loss)
    
    gpu_options = tf.GPUOptions(allow_growth=True)

    sess = tf.Session(config=tf.ConfigProto(gpu_options=gpu_options))
    sess.run(tf.global_variables_initializer())
    
    saver = tf.train.Saver(max_to_keep=1)
    start_point = 0
    model = tf.train.get_checkpoint_state("./model")
    if model and tf.train.checkpoint_exists(model.model_checkpoint_path):
            loader = tf.train.import_meta_graph(model.model_checkpoint_path+".meta")
            loader.restore(sess,model.model_checkpoint_path)
            start_point = int(model.model_checkpoint_path.split('/')[-1].split('-')[-1].split(".")[0])
     
    for epoch in range(start_point,100000):
        batch = dataset.getbatch(128)
        sess.run(optimizer, feed_dict = {X_in: batch, Y: batch})
        ls, d, i_ls, d_ls = sess.run([VAE_loss, dec, SSE_loss, KL_loss], feed_dict = {X_in: batch, Y: batch})
        print(epoch, ls, np.mean(i_ls), np.mean(d_ls))
        if epoch % 20 == 0:
            fig =plot(batch[:10],d[:10])
            plt.savefig('output/{}.png'.format(epoch), bbox_inches='tight')
            plt.close(fig)
        if epoch % 150 == 0:
            try:
                os.remove("./model/model-{}.data-00000-of-00001".format(epoch-150))
            except:
                print("A")
            saver.save(sess, './model/model', global_step=epoch)

        
if __name__=="__main__":
    main()
