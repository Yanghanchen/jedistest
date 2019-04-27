import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

public class JedisLock {
    private Jedis jedis;
    private JedisPool pool;
    private String key="lock";

    public JedisLock() {
        pool=new JedisPool(PropertiesUtil.get("lock.host"),Integer.parseInt(PropertiesUtil.get("lock.port")));
    }

    public JedisLock(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean lock(String id,int wait, long expire){
        boolean flag=false;
        if(jedis==null) {
            jedis = pool.getResource();
            flag = true;
        }
        if(expire>0){
            if(wait>0){
                while(wait>0){
                    if(jedis.get(key)==null) {
                        String result = jedis.set(key, "1", new SetParams().px(expire).nx());
                        if (result != null && result.equals("OK")) {
                            jedis.set(id, "1");
                            close(flag, jedis);
                            return true;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        wait--;
                    }
                }
            }else{
                if(jedis.get(key)==null){
                    String result=jedis.set(key,"1",new SetParams().px(expire).nx());
                    if(result!=null&&result.equals("OK")){
                        jedis.set(id,"1");
                        close(flag,jedis);
                        return true;
                    }
                }
            }
        }else{
            if(wait>0){
                while(wait>0){
                    if(jedis.get(key)==null) {
                        String result = jedis.set(key, "1", new SetParams().nx());
                        if (result != null && result.equals("OK")) {
                            jedis.set(id, "1");
                            close(flag, jedis);
                            return true;
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    wait--;
                }
            }else{
                if(jedis.get(key)==null){
                    String result=jedis.set(key,"1",new SetParams().nx());
                    if(result!=null&&result.equals("OK")){
                        jedis.set(id,"1");
                        close(flag,jedis);
                        return true;
                    }
                }
            }
        }
        close(flag,jedis);
        return false;
    }

    public void unlock(String id) throws IllegalAccessException {
        boolean flag=false;
        if(jedis==null) {
            jedis = pool.getResource();
            flag = true;
        }
        if(jedis.get(id)==null){
            throw new IllegalAccessException("Permission Denied.");
        }else{
            jedis.del(key);
            jedis.del(id);
            System.out.println("线程"+Thread.currentThread().getName()+"释放锁啦");
        }
        close(flag,jedis);
    }

    private void close(boolean flag, Jedis conn) {
        if(flag){
            conn.close();
            conn=null;
        }
    }

    public void close(){
        if(jedis!=null){
            jedis.close();
        }
        if(pool!=null){
            pool.close();
        }
    }
}
