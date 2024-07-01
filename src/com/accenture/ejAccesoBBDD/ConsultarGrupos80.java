package com.accenture.ejAccesoBBDD;

public class ConsultarGrupos80 {

	public static void main(String[] args) {
		Connector.connectExecuteStmt("SELECT * FROM grupos WHERE creacion BETWEEN 1980 AND 1989;", Connector::printTable);
	}

}
