package it.polito.dp2.RNS.sol3.service.storage;

public class PlaceCapacity {
	private int capacity;
	private int available;
	
	public PlaceCapacity(int capacity, int available) {
		this.capacity = capacity;
		this.available = available;
	}
	public int getCapacity() {
		return capacity;
	}
	public int getAvailable() {
		return available;
	}
	public void setAvailable(int available) {
		this.available = available;
	}
}
