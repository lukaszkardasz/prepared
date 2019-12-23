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
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author n2god on 23/12/2019
 * @project prepared
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
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		//pass2" OR '1'='1'; --
		final String sqlQuery = buildQuery(username, password);
		System.out.println(sqlQuery);

		try(Connection connection = dataSource.getConnection();
		    Statement statement = connection.createStatement();
		    ResultSet resultSet = statement.executeQuery(sqlQuery);) {
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

	private String buildQuery(String username, String password) {
		//pass2" OR '1'='1'; --
		return "SELECT username, password FROM user WHERE "
		+ "username=" + "\"" + username + "\" "
		+ "AND "
		+ "password=" + "\"" + password + "\";";
	}

	private DataSource getDataSource() throws NamingException {
		Context initialContext = new InitialContext();
		Context envContext = (Context) initialContext
				.lookup("java:comp/env");
		return (DataSource) envContext.lookup("jdbc/users");
	}
}
