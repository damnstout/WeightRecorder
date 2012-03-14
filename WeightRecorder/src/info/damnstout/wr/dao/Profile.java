package info.damnstout.wr.dao;


public class Profile {

	private int id;

	private String name;

	private int birthYear;

	private int gender;

	private int height;

	private double weight;
	
	public Profile() {
	}

	public Profile(int fid, String fname, int fBirthYear, int fGender,
			int fHeight, double fWeight) {
		id = fid;
		name = fname;
		birthYear = fBirthYear;
		gender = fGender;
		height = fHeight;
		weight = fWeight;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}
