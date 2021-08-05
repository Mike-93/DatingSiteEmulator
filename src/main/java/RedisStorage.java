import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;


import java.util.Date;

public class RedisStorage {

    private RedissonClient redisson;

    private RKeys rKeys;

    private RScoredSortedSet<String> onlineUsers;

    private final static String KEY = "ONLINE_USERS";


    private double getTs() {
        return new Date().getTime();
    }

    void init() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        try {
            redisson = Redisson.create(config);
        } catch (RedisConnectionException Exc) {
            System.out.println("Не удалось подключиться к Redis");
            System.out.println(Exc.getMessage());
        }
        rKeys = redisson.getKeys();
        onlineUsers = redisson.getScoredSortedSet(KEY);
        rKeys.delete(KEY);
    }


    public void logPageVisit() throws InterruptedException {
        for (int i = 1; i <= 20; i++) {
            onlineUsers.add(getTs(), String.valueOf(i));
        }
        for (String user : onlineUsers) {
            System.out.println("— На главной странице показываем пользователя: " + user);
            Thread.sleep(1000);
            if (Math.random() < 0.10) {
                int i = (int) (1 + onlineUsers.size() * Math.random());
                String payingUser = (String) onlineUsers.toArray()[i];
                System.out.println("> Пользователь " + payingUser + " оплатил платную услугу");
                System.out.println("— На главной странице показываем пользователя: " + payingUser);
                Thread.sleep(1000);
            }
        }
        logPageVisit();
    }

}
