import java.util.Random;

import main.KafkaTopicProducer;

class Producer extends Thread {
    public void run() {
        try {
            while (true) {
                Random rand = new Random();
                int random = rand.nextInt(1000)+1000;
                String sendMe = "msgKey:taiwan_future_TFM5_1m@1434087245764;8:1192.0;5:1192.2;7:";
                sendMe += String.valueOf(random);
                sendMe += ";Date:1434087245764;9:50866.0;6:1192.6;KBAR:taiwan_future_TFM5_1m_1434087245764;";
                System.out.println(sendMe);
                KafkaTopicProducer.getInstance().send("Price", sendMe);
                Thread.sleep(500);
            }
        } catch (Exception e) {

        }
    }
}

public class Sender {
    public static void main(String[] args) {
        Producer p = new Producer();
        p.start();
    }
}