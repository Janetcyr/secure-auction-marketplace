package Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import Protocol.PrivateKeyProtectorPBEKey;
import Protocol.ProtocolLibrary;
import Protocol.SaveClientInformation;

public class BidsEngine {
	/**
	 * Return the bidding info according to the bid ID
	 * 
	 * @param ID
	 * @return
	 */
	public static BidInfo searchBid(String ID) {
		File f = new File("bids/" + ID);
		if (!f.exists())
			return null;
		try {
			Scanner bidsParser = new Scanner(f);
			String seller = bidsParser.next();
			String winner = bidsParser.next();
			double price = Double.valueOf(bidsParser.next());
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			// SimpleDateFormat sdf = new
			// SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
			try {
				start.setTime(sdf.parse(bidsParser.next()));
				end.setTime(sdf.parse(bidsParser.next()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new BidInfo(seller, winner, price, start, end);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Return all bidding info
	 * 
	 * @return
	 */
	public static Map<String, BidInfo> getAllBids() {
		Map<String, BidInfo> bids = new HashMap<String, BidInfo>();

		File folder = new File("bids/");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				bids.put(listOfFiles[i].getName(), searchBid(listOfFiles[i].getName()));
			}
		}

		return bids;
	}

	/**
	 * Get all product sold by sellerID
	 * 
	 * @param sellerID
	 * @return
	 */
	public static List<SellerInfo> searchSeller(String sellerID) {
		List<SellerInfo> ret = new ArrayList<SellerInfo>();
		File f = new File("sellers/" + sellerID);
		if (!f.exists())
			return null;
		try {
			Scanner sellersParser = new Scanner(f);
			while (sellersParser.hasNext()) {
				String itemID = sellersParser.next();
				ret.add(new SellerInfo(itemID));
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Get all bid information according to the Buyer ID
	 * 
	 * @param ID
	 *            Buyer's ID
	 * @return
	 */

	public List<BuyerInfo> searchBuyer(String ID) {
		List<BuyerInfo> ret = new ArrayList<BuyerInfo>();
		File f = new File("buyers/" + ID);
		if (!f.exists())
			return null;
		try {
			Scanner buyersParser = new Scanner(f);
			while (buyersParser.hasNext()) {
				String itemID = buyersParser.next();
				ret.add(new BuyerInfo(itemID));
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public void updateBuyer(String buyerID, String itemID) {
		// File f = new File("sellers/" + itemID);
		System.out.println("enter update seller");
		String directory = "buyers" + "/";
		String path = directory + buyerID;
		System.out.println("buyerID:" + buyerID);
		if (!SaveClientInformation.isUserNameExist(directory, buyerID)) {
			System.out.println("buyer file doesn't exist");
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(new File(path));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String content = itemID + "\n";
			try {
				output.write(content.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("seller file exists");
			String f = null;
			try {
				f = new String(ProtocolLibrary.readByteFromFile(path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Scanner sc = new Scanner(f);
			while (sc.hasNext()) {
				String s = sc.nextLine();
				// Scanner line = new Scanner(s);
				// String item = line.next();

				if (s.equals(itemID)) {
					// debug
					System.out.println("found itemID in the sellers file");
					return;
				}

			}
			String content = f + itemID + "\n";
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(new File(path));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				output.write(content.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void updateSeller(String sellerID, String itemID) {
		// File f = new File("sellers/" + itemID);
		System.out.println("enter update seller");
		String directory = "sellers" + "/";
		String path = directory + sellerID;
		System.out.println("sellerID:" + sellerID);
		if (!SaveClientInformation.isUserNameExist(directory, sellerID)) {
			System.out.println("seller file doesn't exist");
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(new File(path));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String content = itemID + "\n";
			try {
				output.write(content.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("seller file exists");
			String f = null;
			try {
				f = new String(ProtocolLibrary.readByteFromFile(path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Scanner sc = new Scanner(f);
			while (sc.hasNext()) {
				String s = sc.nextLine();
				// Scanner line = new Scanner(s);
				// String item = line.next();

				if (s.equals(itemID)) {
					// debug
					System.out.println("found itemID in the sellers file");
					return;
				}

			}
			String content = f + itemID + "\n";
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(new File(path));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				output.write(content.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * 
	 * @param sellerID
	 * @param itemID
	 * @param price
	 * @param start
	 * @param end
	 * @return Adding new bid successful
	 * @throws IOException
	 */
	public boolean addBid(String itemID, String sellerID, String winnerID,
			double price, Calendar start, Calendar end) throws IOException {

		// String[] s=itemID.trim().split(" ");
		// debug
		// System.out.println(s[3]);
		System.out.println(itemID);
		String directory = "bids" + "/";
		String path = directory + itemID;

		if (!SaveClientInformation.isUserNameExist(directory, itemID)) {
			// debug

			FileOutputStream output = new FileOutputStream(new File(path));

			// SimpleDateFormat sdf = new
			// SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
			String content = sellerID + " " + winnerID + " " + price + " "
					+ sdf.format(start.getTime()) + " "
					+ sdf.format(end.getTime()) + "\n";
			output.write(content.getBytes());
			output.close();
		}
		// debug
		System.out.println("BidsEngine write file test:");
		return true;
	}

	public boolean hasBid(String itemID) {
		File f = new File("seller/" + itemID);
		if (f.exists())
			return true;
		return false;
	}

	public boolean updateBid(String itemID, String winnerID, String bidPrice) {

		System.out.println("enter update Bid");
		File f = new File("bids/" + itemID);
		if (!f.exists())
			return false;
		else {
			Scanner sc = null;
			try {
				sc = new Scanner(f);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String content = sc.next() + " " + winnerID;
			sc.next(); //skip previous winner
			content += " " + bidPrice;
			sc.next(); //skip old price
			content += " " + sc.next() + " " + sc.next()
					+ "\n";
			FileWriter fw = null;
			try {
				fw = new FileWriter(f.getAbsoluteFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedWriter bw = new BufferedWriter(fw);
			try {
				bw.write(content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return true;
	}
}
