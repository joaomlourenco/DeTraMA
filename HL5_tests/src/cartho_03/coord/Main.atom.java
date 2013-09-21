package cartho_03.coord;

public class Main {

	private Coord c;
	
	public static void main(String[] args) {
		new Main().execute();
	}
	
	public void execute () {
		this.c = new Coord(0, 0);
		new T1().start();
		new T2().start();
		new T3().start();
		new T4().start();
	}
	
	class T1 extends Thread {
		public void run() {
			Coord d1 = new Coord(1, 2);
			c.setXY(d1);
		}
	}
	
	class T2 extends Thread {
		public void run() {
			double x2 = c.getX();
			System.out.println(x2);
		}
	}
	
	class T3 extends Thread {
		public void run() {
			double x3 = c.getX();
			double y3 = c.getY();
			System.out.println(x3);
			System.out.println(y3);
		}
	}
	
	class T4 extends Thread {
		public void run() {
			double x4 = c.getX();
			System.out.println(x4);
			Coord d4 = c.getXY();
			x4 = d4.getX();
			double y4 = d4.getY();
			System.out.println(x4);
			System.out.println(y4);
		}
	}

}
