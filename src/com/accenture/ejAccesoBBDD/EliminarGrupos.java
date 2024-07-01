package com.accenture.ejAccesoBBDD;

public class EliminarGrupos {

	public static void main(String[] args) {
		Connector.connectExecuteStmt("DELETE FROM grupos WHERE nombre = 'Baron Rojo';",
				Connector.printMessage("Datos eliminados correctamente"));
	}

}
