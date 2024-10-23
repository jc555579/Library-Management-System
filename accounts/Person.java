package accounts;

import java.io.IOException;

public abstract class Person {
	protected String firstName, lastName;

	public Person(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Person() {

	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public abstract boolean logIn() throws IOException;

	public abstract boolean logOut(String answer);
}
