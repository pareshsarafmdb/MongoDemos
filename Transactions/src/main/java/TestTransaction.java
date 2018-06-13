import java.util.concurrent.ThreadLocalRandom;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class TestTransaction implements Runnable{
	static MongoClient client = new MongoClient("localhost:27017");
    static MongoDatabase db = client.getDatabase("test");
    static MongoCollection<Document>  seats = db.getCollection("seats");
    static MongoCollection<Document>  payments = db.getCollection("payments");
    int custId;
    
    public TestTransaction(int i) {
    	   custId = i;
    }
    	
	private void bookSeat(int seatNo) {
		int price = ThreadLocalRandom.current().nextInt(100, 200 + 1);
		System.out.println("Booking seat : " + seatNo + " for customer: " + this.custId);
		if (seats.find(new Document("seat", seatNo)).iterator().hasNext()) {
			System.out.println("Sorry.." + seatNo + " is already booked");
		}
		else 
		try (ClientSession clientSession = client.startSession()) {
			   clientSession.startTransaction();
			   try {
			       seats.insertOne(clientSession, new Document("flight_no", "A100").
			    		   append("seat", seatNo).append("timestamp", new java.util.Date()));
			       int sleepTime = ThreadLocalRandom.current().nextInt(9000, 10000 + 1);
			       System.out.println("Customer " + custId + " Seat no: " + seatNo + 
			    		                              " sleeping for " +  sleepTime + "ms");
			       Thread.sleep(sleepTime);					
				
			       if (seats.find(new Document("seat", seatNo)).iterator().hasNext()) {
						System.out.println("Sorry Customer " + custId + " Seat: " + seatNo 
								           + " is already booked");
						throw new Exception();
					}
			       
			       payments.insertOne(clientSession, new Document("flight_no", "A100").
			    		   append("seat", seatNo).append("timestamp", new java.util.Date()).
			    		   append("price", price));
			       
			       clientSession.commitTransaction();
			       System.out.println("Customer " + custId + " Paying " + price + 
			    		                  " for seat no: " + seatNo);
			   } catch (Exception e) {
				   System.out.println("Transaction aborted");
			       clientSession.abortTransaction();
			   }
			}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int seatNo = ThreadLocalRandom.current().nextInt(1, 2 + 1);
		bookSeat(seatNo);
	}

}
