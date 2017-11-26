package es.udc.psi1718.project.storage.database.daos;


public class Panel {
	private int id;
	private String name;
	// private ArrayList<Controller> controllers;


	public Panel(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Panel(String name) {
		this.name = name;
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

}
