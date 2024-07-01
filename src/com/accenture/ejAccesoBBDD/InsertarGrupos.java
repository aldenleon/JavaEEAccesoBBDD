package com.accenture.ejAccesoBBDD;

public class InsertarGrupos {

	public static void main(String[] args) {
		Connector.connectExecuteStmt("INSERT INTO grupos (nombre, creacion, origen, genero, discograficaIdActual) VALUES"
				+ "('ramones', 1967, 'Reino Unido', 'Hard rock', 10);", Connector.printMessage("Datos insertados correctamente"));
	}

}
