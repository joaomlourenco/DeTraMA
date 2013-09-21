package cartho_03.coord;

public class Coord {
	private double x, y;

	public Coord(double px, double py) {
		x = px;
		y = py;
	}

	double getX() {
		atomic {
			return x;
		}
	}

	double getY() {
		atomic {
			return y;
		}
	}

	Coord getXY() {
		atomic {
			return new Coord(x, y);
		}
	}

	void setX(double px) {
		atomic {
			x = px;
		}
	}

	void setY(double py) {
		atomic {
			y = py;
		}
	}

	void setXY(Coord c) {
		atomic {
			x = c.x;
			y = c.y;
		}
	}
}
