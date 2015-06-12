package kafka.consumer;

import main.KafkaTopicConsumer;

public class LinearRegression {
	public static void main(String[] args) {
		Receiver r = new Receiver();
		KafkaTopicConsumer ktc = new KafkaTopicConsumer("192.168.1.104:2181",
				"Price", "Price", r);
	}
}