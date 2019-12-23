package pl.n2god.prepared.servlet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author n2god on 23/12/2019
 * @project prepared
 *
 * PreparedStatement prepStmt = conn.prepareStatement("SELECT username, password FROM user WHERE username=? AND password=?;");
 * prepStmt.setString(1, "nazwa uzytkownika");
 * prepStmt.setString(2, "hasło");
 * Oprócz metody setString() istnieją także odpowiedniki dla innych typów danych jak setInt(), setDouble(), setArray() o
 * raz ogólna metoda setObject(), którą w zasadzie możemy zastąpić wszystkie inne, ponieważ może ona przyjąć obiekt dowolnego typu.
 * W metodzie jako pierwszy argument podajemy numer parametru, który chcemy ustawić (numerując od 1) a jako drugi argument wartość,
 * którą chcemy wstawić pod dany znak zapytania / parametr.
 * Zabezpieczenie przed SQL Injection polega na tym, że używając np. metody setString() wstawiana wartość zawsze będzie
 * napisem, a nie np. dalszym fragmentem zapytania. Jeżeli więc ktoś teraz spróbuje jako hasło wstawić "haslo" OR '1'='1'; -- "
 * to całe zapytanie będzie wyglądało tak:
 * SELECT username, password FROM user WHERE username='cokolwiek' AND password='haslo" OR \'1\'=\'1\'; -- ';1
 * Całe wyrażenie 'haslo" OR \'1\'=\'1\'; -- ' jest tutaj traktowane jako napis a nie kawałek zapytania SQL.
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DataSource dataSource = null;
		try {
			dataSource = getDataSource();
		} catch (NamingException e) {
			e.printStackTrace();
			response.sendError(500);
		}


		final String sqlQuery = "SELECT username, password FROM user WHERE username=? AND password=?";
		try(Connection connection = dataSource.getConnection();
		    PreparedStatement statement = connection.prepareStatement(sqlQuery);) {

			String username = request.getParameter("username");
			String password = request.getParameter("password");
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()){
				String userFound = resultSet.getString("username");
				request.getSession().setAttribute("username", userFound);
				if ("admin".equals(userFound)){
					request.getSession().setAttribute("privigiles", "all");
				} else {
					request.getSession().setAttribute("privigiles", "view");
				}
			} else {
				request.getSession().setAttribute("username", "Nieznajomy");
				request.getSession().setAttribute("privigiles", "none");
			}
			request.getRequestDispatcher("result.jsp").forward(request, response);
		} catch (Exception e){
			e.printStackTrace();
			response.sendError(500);
		}
	}

	private DataSource getDataSource() throws NamingException {
		Context initialContext = new InitialContext();
		Context envContext = (Context) initialContext
				.lookup("java:comp/env");
		return (DataSource) envContext.lookup("jdbc/users");
	}
}
