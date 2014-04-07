package Model;

import java.util.Calendar;

public class BidInfo {
	private String seller;
	private String winner;
	private double price;
	private Calendar start;
	private Calendar end;
	
	public BidInfo(String seller, String winner, double price, Calendar start, Calendar end) {
		this.setSeller(seller);
		this.setWinner(winner);
		this.setPrice(price);
		this.setStart(start);
		this.setEnd(end);
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public Calendar getStart() {
		return start;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}
	
}
