package edu.sjsu.cmpe273.lab2;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class PollClient {
  private static final Logger logger = Logger.getLogger(PollClient.class.getName());

  private final ChannelImpl channel;
  private final PollServiceGrpc.PollServiceBlockingStub blockingStub;

  public PollClient(String host, int port) {
    channel =
        NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT)
            .build();
    blockingStub = PollServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTerminated(5, TimeUnit.SECONDS);
  }

  public void createPoll() {
    try {
      logger.info("Will try to create poll ...");
      List<String> lst = new ArrayList<String>();
      lst.add("5");
      Iterator<String> itr = lst.iterator();
	String[] choice = new String[]{"Android", "iPhone"};

      PollRequest request = PollRequest.newBuilder().setModeratorId("moderator id 1").setQuestion("Android or Iphone").setStartedAt("3/18/2015").setExpiredAt("3/19/2015").addChoice(choice[0]).addChoice(choice[1]).build();
      PollResponse response = blockingStub.createPoll(request);
      logger.info("Poll: " + response.getId());
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

      public void createPoll(String moderatorId , String question, String startedAt, String expiredAt, String[] choice) {
        if (choice == null || choice.length < 2) {
            new RuntimeException("choice must have two items in it");
        }
        try {
            logger.info("Creating a new poll for moderator " + moderatorId);
            PollRequest request = PollRequest.newBuilder()
                    .setModeratorId(moderatorId)
                    .setQuestion(question)
                    .setStartedAt(startedAt)
                    .setExpiredAt(expiredAt)
                    .addChoice(choice[0])
                    .addChoice(choice[1])
                    .build();
 
            PollResponse response = blockingStub.createPoll(request);
            logger.info("Created a new poll with id = " + response.getId());
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "RPC failed", e);
            return;
        }
    }

  public static void main(String[] args) throws Exception {
    PollClient client = new PollClient("localhost", 50051);
    try {
      	    String moderatorId = "1";
            String question = "What type of smartphone do you have?";
            String startedAt = "2015-03-18T13:00:00.000Z";
            String expiredAt = "2015-03-19T13:00:00.000Z";
            String[] choice = new String[] { "Android", "iPhone" };
 
            client.createPoll(moderatorId, question, startedAt, expiredAt, choice);
    } finally {
      client.shutdown();
    }
  }
}