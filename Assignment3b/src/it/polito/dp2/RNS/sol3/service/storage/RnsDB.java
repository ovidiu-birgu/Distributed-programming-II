package it.polito.dp2.RNS.sol3.service.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Webvehicle;
import it.polito.dp2.RNS.sol3.service.util.Util;

/**
 * Contains all the data structures
 * this version stores the data in main memory
 */
public class RnsDB {
	private Map<String, Integer> mapNodes;
	private Set<Integer> setRelationships;
	private Map<String, Object> mapPlaceReader;
	private Map<String, PlaceCapacity> mapPlaceCapacity;
	private Map<String, Webvehicle> mapWebVehicles;
	
	public RnsDB() {
		mapNodes = new HashMap<>();
		setRelationships = new HashSet<>();
		// map sorted by key
		mapPlaceReader = new TreeMap<>();
		mapPlaceCapacity = new ConcurrentHashMap<>();
		// concurrent map sorted by key
		mapWebVehicles = new ConcurrentSkipListMap<>();
	}
	
	/**
	 * get the map of all the nodes from the database
	 * @return
	 */
	public Map<String, Integer> getMapNodes() {
		return mapNodes;
	}
	
	/**
	 * get a node corresponding to a place
	 * @param place
	 * @return
	 */
	public Integer getNode(String place) {
		return mapNodes.getOrDefault(place, null);
	}
	
	/**
	 * add node to the database
	 * @param place
	 * @param id
	 */
	public void addNode(String place, Integer id) {
		mapNodes.putIfAbsent(place, id);
	}
	
	/**
	 * check if the database contains the node corresponding to the place
	 * @param place
	 * @return
	 */
	public boolean containsNode(String place) {
		return mapNodes.containsKey(place);
	}
	
	/**
	 * get the place corresponding to the node
	 * @param parseStringToInteger
	 * @return
	 */
	public String gePlaceCorrespondingNode(Integer node) {
		return Util.getKeyByValue(mapNodes, node);
	}
	
	/**
	 * remove all nodes from the database
	 */
	public void clearMapNodes() {
		mapNodes.clear();
	}
	
	/**
	 * get the set of all the relationships from the database
	 * @return
	 */
	public Set<Integer> getSetRelationships() {
		return setRelationships;
	}
	
	/**
	 * add relationship to the database
	 * @param id
	 */
	public void addRelationship(Integer id) {
		setRelationships.add(id);
	}

	/**
	 * remove all relationships from the database
	 */
	public void clearSetRelationships() {
		setRelationships.clear();
	}
	
	/**
	 * get the map of all the places from the database
	 * @return
	 */
	public Map<String, Object> getMapPlaceReader() {
		return mapPlaceReader;
	}	
	
	/**
	 * add place to database
	 * @param id
	 * @param p
	 */
	public void addPlaceReader(String id, Object p) {
		mapPlaceReader.putIfAbsent(id, p);
	}
	
	/**
	 * get the place identified by id from the database
	 * @param id
	 * @return
	 */
	public Object getPlaceReader(String id) {
		return mapPlaceReader.get(id);
	}
	
	/**
	 * check if the database contains the place
	 * @param id
	 * @return
	 */
	public boolean containsPlaceReader(String id) {
		return mapPlaceReader.containsKey(id);
	}
	
	/**
	 * remove all places from the database
	 */
	public void clearMapPlaceReader() {
		mapPlaceReader.clear();
	}	
	
	/**
	 * get the map of all the placesCapacity from the database
	 * @return
	 */
	public Map<String, PlaceCapacity> getMapPlaceCapacity() {
		return mapPlaceCapacity;
	}
	
	/**
	 * add PlaceCapacity to database
	 * @param place
	 * @param capacity
	 */
	public void addPlaceCapacity(String place, Integer capacity) {
		mapPlaceCapacity.putIfAbsent(place, new PlaceCapacity(capacity, capacity));
	}
	
	/**
	 * decrements the available capacity of a place by one
	 * @param place
	 * @return
	 */
	public synchronized PlaceCapacity decrementPlaceCapacity(String place) {
		PlaceCapacity old = mapPlaceCapacity.getOrDefault(place, null);
		// place does not exist
		if(old == null)
			return null;
		// no more free spots
		if(old.getAvailable() == 0)
			return null;
		// one spot is occupied
		old.setAvailable(old.getAvailable() - 1);
		
		return mapPlaceCapacity.replace(place, old);
	}
	
	/**
	 * increment the available capacity of a place by one
	 * @param place
	 * @return
	 */
	public synchronized PlaceCapacity incrementPlaceCapacity(String place) {
		PlaceCapacity old = mapPlaceCapacity.getOrDefault(place, null);
		// place does not exist
		if(old == null)
			return null;
		// illegal state, cannot have more available spots than capacity
		if(old.getAvailable() == old.getCapacity())
			return null;
		// one spot is freed
		old.setAvailable(old.getAvailable() + 1);
		return mapPlaceCapacity.replace(place, old);
	}
	
	/**
	 * remove all placeCapacity from the database
	 */
	public void clearMapPlaceCapacity() {
		mapPlaceCapacity.clear();
	}
	
	/**
	 * get the map of all the Webvehicles from the database
	 * @return
	 */
	public Map<String, Webvehicle> getMapWebvehicles() {
		return mapWebVehicles;
	}
	
	/**
	 * add a webvehicle to the database
	 * @param id
	 * @param webvehicle
	 * @return
	 */
	public Webvehicle addWebvehicle(String id, Webvehicle webvehicle) {
		return mapWebVehicles.putIfAbsent(id, webvehicle);
	}
	
	/**
	 * update a webvehicle in the database
	 * @param id
	 * @param webvehicle
	 * @return
	 */
	public Webvehicle updateWebvehicle(String id, Webvehicle webvehicle) {
		return mapWebVehicles.replace(id, webvehicle);
	}
	
	/**
	 * remove a webvehicle from the database
	 * @param id
	 * @return
	 */
	public Webvehicle removeWebvehicle(String id) {
		return mapWebVehicles.remove(id);
	}
	
	/**
	 * check if the database contains the webvehicle
	 * @param webvehicle
	 * @return
	 */
	public boolean containsWebvehicle(String webvehicle) {
		return mapWebVehicles.containsKey(webvehicle);
	}

	/**
	 * get the webvehicle identified by id
	 * @param id
	 * @return
	 */
	public Webvehicle getWebvehicle(String id) {
		// better for concurrency than doing: map.containsKey + map.get
		return mapWebVehicles.getOrDefault(id, null);
	}

	/**
	 * remove all webvehicles from the database
	 */
	public void clearMapWebVehicles() {
		mapWebVehicles.clear();
	}

}
