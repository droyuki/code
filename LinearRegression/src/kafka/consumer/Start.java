package kafka.consumer;

import main.KafkaTopicConsumer;
public class Start{
    //-Djava.library.path="/home/kmlab/R/x86_64-pc-linux-gnu-library/3.0/rJava/jri"
    public static void main(String[] args) {
        Receiver r = new Receiver();
        String groupid = "PriceForLR", topic = "Price";
//        if(args.length != 2) {
//            System.out.println("Please enter groupid and topic.");
//            System.exit(0);
//        }
        @SuppressWarnings("unused")
        //for km lab: 192.168.1.104:2181,192.168.1.105:2181,192.168.1.106:2181
        //for mis-lab: 140.119.19.230:2181,140.119.19.232:2181,140.119.19.238:2181
        KafkaTopicConsumer ktc = new KafkaTopicConsumer("192.168.1.104:2181,192.168.1.105:2181,192.168.1.106:2181",
                groupid, topic, r);
    }
}