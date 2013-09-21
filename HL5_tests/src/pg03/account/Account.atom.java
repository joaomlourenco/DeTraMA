package pg03.account;

public class Account {

	int balance;
	
	int read () {
		atomic {
			return balance;
		}
	}
	
	//int update (int a) {
	void update (int a) {
		int tmp = read();
		atomic {
			balance = tmp + a;
		}
	}
}
