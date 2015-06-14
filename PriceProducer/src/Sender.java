import java.util.Random;

import main.KafkaTopicProducer;

class Producer extends Thread {
    String topic;
    public Producer(String arg) {
        this.topic = arg;
    }
    public void run() {
        try {
            while (true) {
                Random rand = new Random();
                int random = rand.nextInt(1000)+1000;
                String sendMe = "msgKey:taiwan_future_TFM5_1m@1434087245764;8:1192.0;5:1192.2;7:";
                sendMe += String.valueOf(random);
                sendMe += ";Date:1434087245764;9:50866.0;6:1192.6;KBAR:taiwan_future_TFM5_1m_1434087245764;";
                System.out.println(sendMe);
                KafkaTopicProducer.getInstance().send(topic, sendMe);
                Thread.sleep(500);
            }
        } catch (Exception e) {

        }
    }
}

public class Sender {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Please enter topic.");
            System.exit(0);
        }
        Producer p = new Producer(args[0]);
        p.start();
    }
}