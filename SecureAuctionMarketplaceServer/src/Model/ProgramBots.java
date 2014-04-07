package Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ProgramBots {
	private double win_max = 0;
	private double win_secend_max = 0;
	private double increment = 0;
	private double currentBid = 0;
	private String winner = null;
	private String item = null;

	public ProgramBots(String item) {
		this.setItem(item);
		File f = new File("bots/" + item + "/");
		// item not exist
		if (!f.exists()) {
			f.mkdirs();
		}
		win_max = win_secend_max = increment = 0;
		setWinner(null);
		for (File buyer : f.listFiles()) {
			Scanner line;
			try {
				line = new Scanner(buyer);
				BotInfo bidInfo = new BotInfo(line.nextDouble(),
						line.nextDouble());
				if (bidInfo.getMaxPrice() > win_max) {
					win_max = bidInfo.getMaxPrice();
					increment = bidInfo.getIncrement();
					setWinner(buyer.getName());
				} else if (bidInfo.getMaxPrice() > win_secend_max) {
					win_secend_max = bidInfo.getMaxPrice();
				} else {
					// skip
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @param bidPrice
	 * @param buyer
	 * @return
	 */
	public double updateBid(double bidPrice, String buyer) {
		if (bidPrice > win_max) {
			currentBid = bidPrice;
		} else if (bidPrice > win_secend_max) {
			currentBid = (currentBid + increment) > win_max ? (currentBid + increment)
					: win_max;
		} else {
			currentBid = (win_secend_max + increment) > win_max ? (win_secend_max + increment)
					: win_max;
		}
		return currentBid;
	}

	/**
	 * Add or update a new bot.
	 * 
	 * @param buyer
	 * @param maxPrice
	 * @param increment
	 */

	public void addBot(String buyer, double maxPrice, double increment) {
		this.setItem(item);
		try {
			File f = new File("bots/" + item + "/" + buyer);
			if (!f.exists()) {
				f.createNewFile();
			}
			String content = maxPrice + " " + increment;
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}
}
