package com.accenture.ejAccesoBBDD;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.io.Closeable;

/**
 * Connector se puede usar desde los métodos estáticos para conectarse y ejecutar una query SQL.
 * También se puede usar como un objeto Closeable, en un try-with-resources, para conectarse y ejecutar varias querys.
 * @author Alden León
 */
public class Connector implements Closeable {
	
	public static final String DEFAULT_CONNECTION_STRING = "jdbc:mysql://localhost:3306/musicadb2";
	public static final String DEFAULT_USER = "root";
	public static final String DEFAULT_PASSWD = "1234";
	
	private Connection connection;
	
	/**
	 * Conecta usando los valores por defecto, ejecuta una query y realiza una acción.
	 * @param query Puede ser una query de tipo SELECT, INSERT, UPDATE o DELETE.
	 * @param f Define qué acción realizar. Recibe el ResultSet resultado de la query en caso de ser de tipo SELECT. Recibe null en cualquier otro caso.
	 * @throws SQLExceptionUnchecked
	 */
	public static void connectExecuteStmt(String query, Consumer<ResultSet> f) {
		try {
			connectExecuteStmt(query, f, DEFAULT_CONNECTION_STRING, DEFAULT_USER, DEFAULT_PASSWD);
		} catch (SQLException e) {
			throw new SQLExceptionUnchecked(e);
		}
	}
	
	/**
	 * Conecta usando los valores por defecto ejecuta una query y descarta el resultado si lo hay.
	 * @param query Puede ser una query de tipo SELECT, INSERT, UPDATE o DELETE.
	 * @throws SQLExceptionUnchecked
	 */
	public static void connectExecuteStmt(String query) {
		connectExecuteStmt(query, noop());
	}

	/**
	 * Conecta usando los valores pasados como parámetros, ejecuta una query y realiza una acción.
	 * @param query Puede ser una query de tipo SELECT, INSERT, UPDATE o DELETE.
	 * @param f Define qué acción realizar. Recibe el ResultSet resultado de la query en caso de ser de tipo SELECT. Recibe null en cualquier otro caso.
	 * @throws SQLException
	 */
	public static void connectExecuteStmt(String query, Consumer<ResultSet> f,
			String connectionString, String user, String passwd) throws SQLException {
		try (Connection connection = DriverManager.getConnection(connectionString, user, passwd)) {
			executeStmt(connection, query, f);
		}
	}
	
	/**
	 * Usa una conexión pasada como parámetro para ejecutar una query y realizar una acción.
	 * @param query Puede ser una query de tipo SELECT, INSERT, UPDATE o DELETE.
	 * @param f Define qué acción realizar. Recibe el ResultSet resultado de la query en caso de ser de tipo SELECT. Recibe null en cualquier otro caso.
	 * @throws SQLException
	 */
	public static void executeStmt(Connection connection, String query, Consumer<ResultSet> f) throws SQLException {
		try (Statement statement = connection.createStatement();
				ResultSet resultSet = (query.toLowerCase().contains("select")) ? statement.executeQuery(query) : null;
				) {
			if (resultSet == null) statement.executeUpdate(query);
			f.accept(resultSet);
		}
	}
	
	/**
	 * Conecta usando los valores pasados como parámetros.
	 * @param connectionString Debe indicar el driver, el host y el nombre de la database
	 * @param user
	 * @param passwd
	 * @throws SQLException
	 */
	public Connector(String connectionString, String user, String passwd) throws SQLException {
		connection = DriverManager.getConnection(connectionString, user, passwd);
	}
	
	/**
	 * Conecta usando los valores por defecto
	 * @throws SQLException
	 */
	public Connector() throws SQLException {
		this(DEFAULT_CONNECTION_STRING, DEFAULT_USER, DEFAULT_PASSWD);
	}
	
	/**
	 * Cierra la conexión. Connection.close() lanza SQLException, lo cual no permite usar el try-with-resources. De ahí el uso de SQLExceptionUnchecked. 
	 * @throws SQLExceptionUnchecked
	 */
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new SQLExceptionUnchecked("Unchecked wrapper for a SQLException inside the close() method - ", e);
		}
	}
	
	/**
	 * Ejecuta una query y realiza una acción.
	 * @param query Puede ser una query de tipo SELECT, INSERT, UPDATE o DELETE.
	 * @param f Define qué acción realizar. Recibe el ResultSet resultado de la query en caso de ser de tipo SELECT. Recibe null en cualquier otro caso.
	 * @throws SQLException
	 */
	public void executeStmt(String query, Consumer<ResultSet> f) throws SQLException {
		executeStmt(connection, query, f);
	}
	
	/**
	 * Ejecuta una query y descarta el resultado si lo hay.
	 * @param query Puede ser una query de tipo SELECT, INSERT, UPDATE o DELETE.
	 * @throws SQLException
	 */
	public void executeStmt(String query) throws SQLException {
		executeStmt(connection, query, noop());
	}
	
	/**
	 * Imprime por consola el ResultSet como una tabla, separando las columnas con el tabulador.
	 * Sirve para pasar como parámetro funcional a los métodos executeStmt o connectExecuteStmt.
	 * Sirve para visualizar el resultado de un SELECT.
	 * @param resultSet Recibe el ResultSet resultado de la query en caso de ser de tipo SELECT.
	 */
	public static void printTable(ResultSet resultSet) {
		if (resultSet == null) {
			System.out.println("query output is null");
			return;
		}
		try {
			ResultSetMetaData rsmd = resultSet.getMetaData();
			StringBuilder out = new StringBuilder(String.format("Tabla '%s':%n", rsmd.getTableName(1)));
			for (int i = 1; i <= rsmd.getColumnCount(); i++)
				out.append(rsmd.getColumnName(i) + '\t');
			out.append(String.format("%n"));
			while (resultSet.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++)
					out.append(resultSet.getString(i) + '\t');
				out.append(String.format("%n"));
			}
			System.out.println(out);
		} catch (SQLException e) {
			throw new SQLExceptionUnchecked("Unchecked wrapper for a SQLException inside a functional call - ", e);
		}
	}
	
	/**
	 * Transforma un Runnable en Consumer&lt;ResultSet&gt;.
	 * Al ejecutar querys que no sean de tipo SELECT, el parámetro funcional recibe un ResultSet que es siempre null.
	 * En ese caso, tiene más sentido pasar un Runnable como parámetro. action() sirve como adaptador. 
	 * @param f El Runnable a ejecutar tras una query de tipo INSERT, UPDATE o DELETE.
	 * @return Retorna un Consumer&lt;ResultSet&gt; listo para usar en los métodos executeStmt o connectExecuteStmt.
	 */
	public static Consumer<ResultSet> action(Runnable f) {
		return x -> f.run();
	}
	
	/**
	 * Imprime un mensaje. Sirve como ejemplo de uso de action().
	 * @param message El mensaje a imprimir tras ejecutar la query.
	 * @return El Consumer&lt;ResultSet&gt; listo para usar en los métodos executeStmt o connectExecuteStmt.
	 */
	public static Consumer<ResultSet> printMessage(String message) {
		return action(() -> System.out.println(message));
	}
	
	/**
	 * No hace nada.
	 * @return El Consumer&lt;ResultSet&gt; listo para usar en los métodos executeStmt o connectExecuteStmt.
	 */
	public static Consumer<ResultSet> noop() {
		return action(() -> {});
	}
	
}
