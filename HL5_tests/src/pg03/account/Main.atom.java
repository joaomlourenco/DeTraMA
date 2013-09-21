package pg03.account;

public class Main {

	static Account a;
	public static void main(String[] args) {
		a = new Account();
		new Update().start();
		new Update().start();
	}

}
