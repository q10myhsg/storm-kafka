package com.fangcheng.kafka;

import kafka.message.MessageAndMetadata;

public class KafkaPartitionTest {

	public class Produce extends Thread {
		public String topic = null;

		public String partition = null;
		
		public int time=300;

		public Produce(String topic, String partition) {
			this.topic = topic;
			this.partition = partition;
		}

		public void run() {
			KafkaUtil consumer = new KafkaUtil();
			for (int i = 0; i < 5000; i++) {
				consumer.sentMsgs(topic, partition, partition);
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class Consumer extends Thread {
		
		public String topic = null;

		public String groupId=null;
		
		public int count=1;
		public int index=0;
		public Consumer(String groupId,String topic,int count,int index) {
			this.topic=topic;
			this.groupId=groupId;
			this.count=count;
			this.index=index;
		}
		
		public void run() {
			KafkaUtil consumer = new KafkaUtil(){
				@Override
				public void consumer(MessageAndMetadata<String, String> msg) {
					System.out.println("name:"+Thread.currentThread().getName()+"\t:"+index+":"+"\tkey:" + msg.key() + "\tinput:"
							+ msg.message());
				}
			};
			System.out.println(Thread.currentThread().getName()+"\t启动");
			KafkaConf conf=new KafkaConf();
			//consumer.setProps("partitioner.class","com.catt.kafka.demo.PartitionerDemo");
			consumer.consumer(groupId,topic,index);
//			consumer.consumer(groupId,topic);
		}
	}

	public static void main(String[] args) {
		KafkaPartitionTest test=new KafkaPartitionTest();
		String topic="ADD_NEW_BUSINESS_INNER_MAP";
		String groupId="partitionTest";
		int consumerCount = 1;
		Thread[] threadsC = new Thread[consumerCount];
		for (int i = 0; i < consumerCount; i++) {
			Thread thread1 = test.new Consumer(groupId,topic,consumerCount,i);
			threadsC[i] = thread1;
			thread1.start();
		}
		int sendCount = 1;
		Thread[] threads = new Thread[sendCount];
		for (int i = 0; i < sendCount; i++) {
			Thread thread1 = test.new Produce(topic,Integer.toString(i));
			threads[i] = thread1;
			thread1.start();
		}
	}
}
