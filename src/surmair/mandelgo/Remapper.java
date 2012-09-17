package surmair.mandelgo;

public class Remapper {
	double oldLow, oldHigh;
	double newLow, newHigh;

	public Remapper(double oldLow, double oldHigh, double newLow, double newHigh) {
		this.oldLow = oldLow;
		this.oldHigh = oldHigh;
		this.newLow = newLow;
		this.newHigh = newHigh;
	}

	public double map(double v) {
		return (v-oldLow)/(oldHigh-oldLow) * (newHigh-newLow) + newLow;
	}
}
