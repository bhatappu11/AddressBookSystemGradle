package com.bridgelabz.addressbook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class AddressBookDBService {

	private PreparedStatement addressBookPreparedStatement;

	public List<Contact> readData() {
		String sql = "select * from contact;";
		List<Contact> contactList = new ArrayList<>();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				String id = result.getString("id");
				String firstName = result.getString("first_name");
				String lastName = result.getString("last_name");
				String phoneNumber = result.getString("phone");
				String email = result.getString("email");
				String bookID = result.getString("book_id");
				contactList.add(new Contact(id, firstName, lastName,phoneNumber,email,bookID));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactList;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/address_book_service?useSSL=false";
		String userName = "root";
		String password = "perfios";
		Connection connection;
		System.out.println("connecting to database: "+jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("connection is successful!"+connection);
		return connection;
	}


	public void writeData(Contact contact) {
		String sql = String.format("insert into contact(id,first_name,last_name,phone,email,book_id) values ('%s','%s','%s','%s','%s','%s')",
				contact.getId(),contact.getFirstName(),contact.getLastName(),contact.getPhoneNumber(),contact.getEmail(),contact.getBookID());
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	public int countByCity(String city) {
		int count = 0;
		if(this.addressBookPreparedStatement == null) {
			this.prepareStatementForAddressBook();
		}
		try {
			addressBookPreparedStatement.setString(1, "Blore");
			ResultSet resultSet = addressBookPreparedStatement.executeQuery();
			while(resultSet.next()) {
				count++; 
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw new AddressBookDBException(AddressBookDBException.ExceptionType.CANNOT_EXECUTE_QUERY, "Failed to execute query");
		}
		return count;
	}

	private void prepareStatementForAddressBook() {
		try {
			Connection connection = this.getConnection();
			String sqlStatement = "select * from contact c join address a on c.id=a.id where city = ?;";
			addressBookPreparedStatement = connection.prepareStatement(sqlStatement);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
