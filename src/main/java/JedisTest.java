
public class JedisTest {
    public static void main(String[] args) {
        for(int i=0;i<10;i++){
            new Thread(()->{
                JedisLock lock=new JedisLock();
                if(lock.lock(Thread.currentThread().getName(),10,0)){
                    System.out.println("线程"+Thread.currentThread().getName()+"获得锁啦");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        lock.unlock(Thread.currentThread().getName());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                lock.close();
            }).start();
        }
    }
}
