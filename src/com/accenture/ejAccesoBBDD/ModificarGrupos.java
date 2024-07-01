package com.accenture.ejAccesoBBDD;

public class ModificarGrupos {

	public static void main(String[] args) {
		Connector.connectExecuteStmt("UPDATE grupos SET genero = 'Rock andaluz' WHERE nombre = 'Medina Azahara';",
				Connector.printMessage("Datos modificados correctamente"));
	}

}
