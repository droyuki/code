package kafka.consumer;

import main.KafkaTopicConsumer;
public class Start{
    //-Djava.library.path="/home/kmlab/R/x86_64-pc-linux-gnu-library/3.0/rJava/jri"
    public static void main(String[] args) {
        Receiver r = new Receiver();
        @SuppressWarnings("unused")
        KafkaTopicConsumer ktc = new KafkaTopicConsumer("192.168.1.104:2181,192.168.1.105:2181,192.168.1.106:2181",
                "PriceTest", "PriceTest", r);
    }
}