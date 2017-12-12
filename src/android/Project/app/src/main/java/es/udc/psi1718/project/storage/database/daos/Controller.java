package es.udc.psi1718.project.storage.database.daos;

public class Controller {
	private int id;
	private String name;
	private int controllerType;
	private String dataType;
	private String pinType;
	private String pinNumber;
	private int position;
	private int panelId;
	private int data;

	public Controller(int id, String name, int controllerType, String dataType, String pinType,
					  String pinNumber, int position, int panelId, int data) {
		this.id = id;
		this.name = name;
		this.dataType = dataType;
		this.pinType = pinType;
		this.pinNumber = pinNumber;
		this.position = position;
		this.panelId = panelId;
		this.controllerType = controllerType;
		this.data=data;
	}

	public Controller(String name, int controllerType, String dataType, String pinType, String pinNumber,
					  int position, int panelId, int data) {
		this.name = name;
		this.controllerType = controllerType;
		this.dataType = dataType;
		this.pinType = pinType;
		this.pinNumber = pinNumber;
		this.position = position;
		this.panelId = panelId;
		this.data=data;
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getPinType() {
		return pinType;
	}

	public void setPinType(String pinType) {
		this.pinType = pinType;
	}

	public String getPinNumber() {
		return pinNumber;
	}

	public void setPinNumber(String pinNumber) {
		this.pinNumber = pinNumber;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPanelId() {
		return panelId;
	}

	public void setPanelId(int panelId) {
		this.panelId = panelId;
	}

	public int getControllerType() {
		return controllerType;
	}

	public void setControllerType(int controllerType) {
		this.controllerType = controllerType;
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}
}

