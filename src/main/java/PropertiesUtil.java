import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesUtil {
    private static String path="lock.properties";
    private static ConcurrentHashMap<String,String> properties=new ConcurrentHashMap<>();

    static {
        try {
            BufferedReader br=new BufferedReader(new FileReader(PropertiesUtil.class.getResource(path).getFile()));
            String line=null;
            while((line=br.readLine())!=null){
                String[] kv=line.split("=");
                properties.put(kv[0].trim(),kv[1].trim());
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    static public String get(String key){
        return properties.get(key);
    }
}
