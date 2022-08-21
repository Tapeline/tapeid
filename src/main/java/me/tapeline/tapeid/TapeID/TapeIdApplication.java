package me.tapeline.tapeid.TapeID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@SpringBootApplication
@RestController
public class TapeIdApplication {

	public static String hash(String s) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(
					(s + "salty-salty").getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder(2 * hash.length);
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException ignored) {}
		return null;
	}

	public static String clear(String s) {
		return s.replaceAll("[^a-zA-Z_]", "_");
	}

	public static void main(String[] args) throws ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException,
			InstantiationException, IllegalAccessException {
		Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
		SpringApplication.run(TapeIdApplication.class, args);
	}

	@RequestMapping({"/auth/login/{username}/{password}",
			"/api/login username={username} password={password}"})
	public String authLogin(@PathVariable String username, @PathVariable String password) throws SQLException {
		String response = "ok";
		String responseData = "";
		username = clear(username);
		Connector c = new Connector();
		ResultSet users = c.query("SELECT * FROM `users` WHERE `username`='" + username + "';");
		UserEntry user = null;
		if (users.next()) {
			user = new UserEntry(users.getString("username"),
					users.getString("password"));
		}
		if (user == null) {
			response = "err";
			responseData = "\"cause\": \"unknown user\"";
		} else {
			String passHash = hash(password);
			if (passHash == null) {
				response = "err";
				responseData = "\"cause\": \"internal error\"";
			} else if (passHash.equals(user.getPassHash())) {
				response = "ok";
				responseData = "\"username\": " + username + "\"";
			} else {
				response = "err";
				responseData = "\"cause\": \"wrong password\"";
			}
		}
		return "{\"response\": " + response + "\", \"data\": {" + responseData + "}}";
	}

	@RequestMapping("/auth/register/{username}/{password}")
	public String authRegister(@PathVariable String username, @PathVariable String password) throws SQLException {
		String response = "ok";
		String responseData = "";
		username = clear(username);
		Connector c = new Connector();
		ResultSet users = c.query("SELECT * FROM `users` WHERE `username`='" + username + "';");
		UserEntry user = null;
		if (users.next()) {
			user = new UserEntry(users.getString("username"),
					users.getString("password"));
		}
		if (user == null) {
			String passHash = hash(password);
			if (passHash == null) {
				response = "err";
				responseData = "\"cause\": \"internal error\"";
			} else {
				c.execute("INSERT INTO `users` (`username`, `password`) VALUES ('" +
						username + "', '" + passHash + "');");
				response = "ok";
				responseData = "\"username\": " + username + "\"";
			}
		} else {
			response = "err";
			responseData = "\"cause\": \"user already exists\"";
		}
		return "{\"response\": " + response + "\", \"data\": {" + responseData + "}}";
	}

}
