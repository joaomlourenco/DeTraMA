package cartho_04.coord;

public class CoordMain extends Thread {

	Coord coord;

	public static void main(String[] args) {
		Coord c = new Coord();
		new CoordMain(c).start();
		new CoordMain(c).start();
	}

	public CoordMain(Coord coord) {
		this.coord = coord;
	}

	public void run() {
		swap();
		reset();
	}

	public void swap() {
		int oldX;
		atomic {
			oldX = coord.x;
			coord.x = coord.y; // swap X
			coord.y = oldX; // swap Y
		}
	}

	public void reset() {
		atomic {
			coord.x = 0;
		} // inconsistent state (0, y)
		atomic {
			coord.y = 0;
		}
	}

	static class Coord {
		int x, y;
	}
}
