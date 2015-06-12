import java.util.Random;

import main.KafkaTopicProducer;

class Producer extends Thread {
    public void run() {
        try {
            while (true) {
                Random rand = new Random();
                int random = rand.nextInt(1000)+1000;
                String sendMe = String.valueOf(random);
                KafkaTopicProducer.getInstance().send("PriceTest", sendMe);
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