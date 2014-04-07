package Model;

public class BuyerInfo {
	private String itemID;
	public BuyerInfo(String itemID) {
		this.setItemID(itemID);
	}

	public String getItemID() {
		return itemID;
	}
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
}
