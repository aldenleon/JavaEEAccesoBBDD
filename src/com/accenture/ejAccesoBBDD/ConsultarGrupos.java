package com.accenture.ejAccesoBBDD;

public class ConsultarGrupos {

	public static void main(String[] args) {
		Connector.connectExecuteStmt("SELECT * FROM grupos;", Connector::printTable);
	}

}
