import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionMain {
	
	public static void main(String[] args) {
			
	ExecutorService executor = Executors.newCachedThreadPool();
	for (int f = 0; f < 3; f++) {
		executor.execute(new TestTransaction(f));
	}
}

}
