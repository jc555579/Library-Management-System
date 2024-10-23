package library;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.security.auth.login.AccountException;

import accounts.Admin;
import accounts.Student;
import exceptions.AccountsException;
import exceptions.BookException;

public class Main {

	// Global variables to access for the accounts
	private static Admin admin;
	private static Student student;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in); // This scanner instantiation will be used in private methods of this
													// class, to ensure only one Scanner object will be used.

		boolean isExitting = false; // Currently assigning to false.
		while (!isExitting) { // This loop will handle the main menu
			try {
				// Display the library menu
				LibraryUserPrompt.libraryMenu();

				String menuNumber = scanner.nextLine(); // User input

				// Validate input to make sure it's between 1 and 3
				if (!menuNumber.matches("[1-3]")) {
					System.out.println("Invalid input! Please enter [1-3] only.");
					continue; // Go back to the start of the loop
				}

				// Process the selected menu option
				switch (menuNumber) {
				case "1": // Admin login option
					if (AdminlogInUserInput(scanner)) {
						System.out.println("Successfully logged in!");
						System.out.println(); // space
						System.out.printf("Welcome, Admin %s!\n", admin.getFirstName());

						boolean isAdminLoggedOut = false; // currently assigning to false.
						while (!isAdminLoggedOut) { // this loop will handle the admin menu
							LibraryUserPrompt.adminMenu(); // Display the admin menu
							String adminMenuNumber = scanner.nextLine();

							switch (adminMenuNumber) {
							case "1": // For adding a book
								Book book = addBookUserInput(scanner);
								admin.addBook(book);
								break;
							case "2": // For removing a book
								removeBookUserInput(scanner);
								scanner.nextLine(); // buffer
								break;
							case "3": // For searching a book
								searchBookUserInput(scanner);
								scanner.nextLine(); // buffer
								break;
							case "4": // For displaying the list of books available
								admin.displayListOfBooks();
								break;
							case "5": // Tracking the borrowed books of the students.
								admin.displayBorrowedBooksOfStudents();
								break;
							case "6": // It will break the switch case if user enters to log-out
								if (adminLogOutUserInput(scanner)) {
									isAdminLoggedOut = true;
								}

								break;
							default:
								System.out.println("Invalid Input!");
								System.out.println("Your input must be within [1-6] only!");
							}
						}

					} else {
						throw (new AccountsException("Log in failed, please double check your inputs."));
					}
					break;
				case "2":
					if (StudentLogInUserInput(scanner)) {
						System.out.println("Successfully logged in!");
						System.out.println(); // space
						System.out.printf("Welcome, Student %s!\n", student.getFirstName());

						boolean isStudentLoggedOut = false; // currently assigning to false.
						while (!isStudentLoggedOut) {
							LibraryUserPrompt.studentMenu(); // student menu
							String studentMenuNumber = scanner.nextLine();

							switch (studentMenuNumber) {
							case "1": // Borrow option
								borrowBookUserInput(scanner);
								break;
							case "2": // Return book
								returnBookUserInput(scanner);
								break;
							case "3": // Displaying student borrowed books
								student.displayBorrowedBooksOfStudent();
//								scanner.nextLine(); // buffer
								break;
							case "4": // Student logout
								if (studentLogOutUserInput(scanner)) {
									isStudentLoggedOut = true;
								}
								break;
							default:
								System.out.println("Invalid Input!");
								System.out.println("Your input must be within [1-4] only!");
							}
						}

					} else {
						throw (new AccountsException("Log in failed, please double check your inputs."));
					}
					break;

				case "3": // Exit option
					System.out.println("Exiting...");
					System.out.println("Thank You! Come Again!");
					isExitting = true;
					break; // Exit the program
				default: // if user didn't enter from 1-3
					System.out.println("Invalid Input! It should be [1-3] only!");
				}
				// Exceptions that the user can possibly thrown.
			} catch (IOException e) {
				System.out.println("Something Went Wrong! Please Check your inputs!");
			} catch (AccountsException e) {
				System.out.println("Message : " + e.getMessage());
			} catch (NullPointerException e) {
				System.out.println("Something Went Wrong! Please Check your inputs!");
			} catch (Exception e) {
				System.out.println("Something Went Wrong! Please Check your inputs!");
			}
		}
	}

	// Admin log-in user input
	private static boolean AdminlogInUserInput(Scanner scanner) throws IOException {
		System.out.print("Enter First Name : ");
		String firstName = scanner.nextLine();

		System.out.print("Enter Last Name  : ");
		String lastName = scanner.nextLine();

		System.out.print("Password         : ");
		String password = scanner.nextLine();

		admin = new Admin(firstName, lastName, password); // Assigning the admin object to navigate the methods.

		return admin.logIn();
	}

	// Student log-in user input
	private static boolean StudentLogInUserInput(Scanner scanner) throws Exception {
		System.out.print("Enter First Name : ");
		String firstName = scanner.nextLine();

		System.out.print("Enter Last Name  : ");
		String lastName = scanner.nextLine();

		String studentID = studentNumberUserInput(scanner);

		student = new Student(firstName, lastName, studentID);
		return student.logIn();
	}

	// For catalog ID (admin option) user input
	private static int catalogIDUserInput(Scanner scanner) {
		int catalogID; // we use int data type, to sort them at future use.
		while (true) {
			try {
				System.out.print("Enter Catalog ID      : ");
				catalogID = scanner.nextInt();

				if (String.valueOf(catalogID).length() != 5) {
					throw (new BookException("Your Catalog ID should contain at exactly 5 digits only."));
				}

				break; // break the loop if user enter's correctly.
			} catch (InputMismatchException e) {
				System.out.println("Invalid Input! Only 1-9 is accepted and 5 digits only!");
				scanner.nextLine(); // buffer
			} catch (BookException e) {
				System.out.println("Message : " + e.getMessage());
				scanner.nextLine(); // buffer
			}
		}

		return catalogID;
	}

	// For student number user input
	private static String studentNumberUserInput(Scanner scanner) {
		String studentID = "";

		while (true) {
			try {
				System.out.print("Enter Student ID : ");
				studentID = scanner.nextLine();

				if (studentID.length() != 8) {
					throw (new AccountException("Student ID must be exactly 8 digits only!"));
				}

				for (int i = 0; i < studentID.length(); i++) {
					if (studentID.charAt(i) < '0' || studentID.charAt(i) > '9') {
						throw (new AccountException("Student ID must be digits only!"));
					}
				}

				break; // break the loop if user enter's correctly.
			} catch (AccountException e) {
				System.out.println("Message : " + e.getMessage());
			}

		}

		return studentID;
	}

	// For adding book (admin option)
	private static Book addBookUserInput(Scanner scanner) {
		// variables that will be used on adding Book.
		String title;
		String author;

		System.out.println(
				"For adding books, you need to fill up the catalog ID, \ntitle of the book, and author's name.");

		int catalogID = catalogIDUserInput(scanner);

		scanner.nextLine(); // Clear the invalid input from the scanner buffer
		System.out.print("Enter Book Title    : ");
		title = scanner.nextLine();

		System.out.print("Enter Author's Name : ");
		author = scanner.nextLine();

		return new Book(catalogID, title, author);
	}

	// For removing book (admin option)
	private static void removeBookUserInput(Scanner scanner) throws IOException {
		System.out.println("For removing a book, you need to input the catalog ID.");
		int catalogID = catalogIDUserInput(scanner);
		admin.removeBook(catalogID);
	}

	private static void searchBookUserInput(Scanner scanner) {
		System.out.println("For searching a book, you need to input the catalog ID.");
		int catalogID = catalogIDUserInput(scanner);
		if (admin.searchBook(catalogID) != null) {
			System.out.println(admin.searchBook(catalogID) + " is available!");
		} else {
			System.out.println("No book found with Catalog ID: " + catalogID);
		}

	}

	// For borrowing book (student option)
	private static void borrowBookUserInput(Scanner scanner) throws IOException {
		// Created an object of admin for borrow purposes.
		Admin forBorrowAdmin = new Admin();
		int catalogID = catalogIDUserInput(scanner);

		Book book = forBorrowAdmin.searchBook(catalogID);
		if (book != null) {
			System.out.println("Book to Borrow : " + book);

			scanner.nextLine(); // buffer
			System.out.print("Type \"Yes\" to borrow: ");
			String borrow = scanner.nextLine();

			if (borrow.equalsIgnoreCase("Yes")) {
				student.borrowBook(catalogID);
			} else {
				System.out.println("Back to Menu...");
			}
		} else {
			System.out.println("No book found with Catalog ID: " + catalogID);
			scanner.nextLine(); // buffer
		}

	}

	// For returning book (student option)
	private static void returnBookUserInput(Scanner scanner) throws IOException {
		// Created an object of admin for return purposes.
		int catalogID = catalogIDUserInput(scanner);

		Book book = student.searchStudentBorrowedBook(catalogID);

		if (book != null) {
			System.out.println("Book to Return : " + book);

			scanner.nextLine(); // buffer.
			System.out.print("Type \"Yes\" to return: ");
			String borrow = scanner.nextLine();

			if (borrow.equalsIgnoreCase("Yes")) {
				student.returnBook(catalogID);
			} else {
				System.out.println("Back to Menu...");
			}
		} else {
			System.out.println("No book found with Catalog ID: " + catalogID);
			scanner.nextLine(); // buffer
		}
	}

	// For admin log-out user input
	private static boolean adminLogOutUserInput(Scanner scanner) {
		System.out.print("Type \"Yes\" to log-out. Are you sure you want to log out? : ");
		String answer = scanner.nextLine();

		if (admin.logOut(answer)) {
			System.out.println("Thank You! Admin " + admin.getFirstName() + "!");
			return true;
		} else {
			System.out.println("Back to Menu...");
			return false;
		}
	}

	// For student log-out user input
	private static boolean studentLogOutUserInput(Scanner scanner) {
		System.out.print("Type \"Yes\" to log-out. Are you sure you want to log out? : ");
		String answer = scanner.nextLine();

		if (student.logOut(answer)) {
			System.out.println("Thank You! Student " + student.getFirstName() + "!");
			return true;
		} else {
			System.out.println("Back to Menu...");
			return false;
		}
	}

}
