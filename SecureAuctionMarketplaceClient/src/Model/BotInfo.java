package Model;

public class BotInfo {
	private double maxPrice;
	private double increment;
	public BotInfo(double maxPrice, double increment) {
		this.maxPrice = maxPrice;
		this.increment = increment;
	}
	public double getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}
	public double getIncrement() {
		return increment;
	}
	public void setIncrement(double increment) {
		this.increment = increment;
	}
	
}
