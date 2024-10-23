package accounts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import library.Book;

public class Student extends Person {
	private String studentID;

	// Declaring the variable for transaction
	private long daysLate = 0;
	private String deadline = "";

	// Declaring these variables to hold the data of the student and to use it in
	// this class.
	private String studentLibraryData;
	private ArrayList<Book> studentBorrowed = new ArrayList<>();

	// For transaction history.
	private LocalDate now = LocalDate.now();

	public Student(String firstName, String lastName, String studentID) throws IOException {
		super(firstName, lastName);
		this.studentID = studentID;

		// Checking if the student file exists.
		File file = new File(String.format("src/resources/student-database/%s.txt", studentID));

		if (file.exists()) {
			// Assigning this file to access by the whole methods.
			this.studentLibraryData = System.getProperty("user.dir")
					+ String.format("/src/resources/student-database/%s.txt", studentID);

			// Getting the files to be accessed by the list.
			List<String> lines = Files
					.readAllLines(Paths.get(String.format("src/resources/student-database/%s.txt", studentID)));

			if (lines.size() != 0) {
				for (String line : lines) {
					Book book = new Book(line);
					studentBorrowed.add(book);
				}
			}
		}
	}

	public boolean logIn() throws IOException {
		// getting the file of student.txt
		Path path = Paths.get("src/resources/accounts/student.txt").toAbsolutePath();
		// getting the file then putting it into a list to get the stream() later.
		List<String> studentAccounts = Files.lines(path).collect(Collectors.toList());

		if (studentAccounts.size() != 0) { // Removing the instruction of the file
			studentAccounts.remove(0);
		}

		Boolean inFile = studentAccounts.stream().anyMatch(
				studentAccount -> studentAccount.equals(String.format("%s %s %s", firstName, lastName, studentID)));

		return inFile;
	}

	public void borrowBook(int catalogID) throws IOException {
		// This will access the search and remove book in admin, that can borrow the
		// book.
		Admin adminBorrow = new Admin();

		Book bookToBorrow = adminBorrow.searchBook(catalogID);
		// Adding to student file

		// Getting the currently borrowed books by the student
		long booksCurrentlyBorrowed = studentBorrowed.stream().count();
		// Borrowed books maximum is 3 only
		if (booksCurrentlyBorrowed < 3) {
			addToFileOfStudent(bookToBorrow);

			// Removing the file to the books that has been borrowed
			adminBorrow.removeBook(catalogID);
			displayTransactionOfBorrowedBook(bookToBorrow);

			// Getting the borrowed books file for admin
			String borrowedBooksFile = System.getProperty("user.dir")
					+ String.format("/src/resources/library/borrowed-books.txt", studentID);

			// Appending the file
			try (FileWriter fw = new FileWriter(borrowedBooksFile, true)) {
				fw.write("\n" + String.format("%s (Deadline : %s) Student ID : %s", bookToBorrow, now.plusDays(7),
						studentID));
			}
		} else {
			System.out.println("Only 3 maximum of books you can borrow! You have 3 already!");
		}

	}

	public void returnBook(int catalogID) throws IOException {
		// This will access the add book of admin, that can borrow the
		// book.
		Admin adminReturn = new Admin();

		// Removing to student file, and store the removed book into a variable
		Book bookToReturn = removeSpecificLineOfFile(catalogID, studentLibraryData);

		// Add to books.txt
		adminReturn.addBook(bookToReturn);

		// Accessing the borrowed books file for removing it and looking for deadline.
		String borrowedBooksFile = System.getProperty("user.dir") + "/src/resources/library/borrowed-books.txt";

		// It will be removed to the borrowed-books and it convert the return type to
		// String to check for the deadline later.
		Book removeBorrowedBook = removeSpecificLineOfFile(catalogID, borrowedBooksFile);
		String borrowedBookStr = "";
		if (removeBorrowedBook != null) {
			borrowedBookStr = removeBorrowedBook.toString();
		}

		if (borrowedBookStr.contains("(Deadline :")) {
			// Getting startIndex and endIndex for accessing the date of deadline.
			int startIndex = borrowedBookStr.indexOf("(Deadline :") + "(Deadline : ".length();
			int endIndex = borrowedBookStr.indexOf(")", startIndex);
			deadline = borrowedBookStr.substring(startIndex, endIndex);

			// Convert the deadline string to a LocalDate
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate deadlineDate = LocalDate.parse(deadline, formatter);

			// Compare the dates
			if (now.isAfter(deadlineDate)) {
				// Getting the day between the deadline and today.
				daysLate = ChronoUnit.DAYS.between(deadlineDate, now);
			}

			// Running the transaction of returned book
			displayTransactionOfReturnedBook(bookToReturn);
		}

	}

	public Book removeSpecificLineOfFile(int catalogID, String file) throws FileNotFoundException, IOException {
		List<String> booksToKeep = new ArrayList<>();
		Book removedBook = null;

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String bookLine;

			// Reading each line of the file
			while ((bookLine = reader.readLine()) != null) {
				if (!bookLine.contains(Integer.toString(catalogID))) {
					booksToKeep.add(bookLine);
				} else {
					// Ensure the bookLine can correctly create a Book object
					try {
						removedBook = new Book(bookLine); // this might be where the issue occurs
					} catch (Exception e) {
						throw new IOException("Error parsing book line: " + bookLine, e);
					}
				}
			}
		}

		// Writing the remaining lines back to the file
		try (FileWriter fw = new FileWriter(file)) {
			for (int i = 0; i < booksToKeep.size(); i++) {
				if ((i + 1) == booksToKeep.size()) {
					fw.write(booksToKeep.get(i));
				} else {
					fw.write(booksToKeep.get(i) + "\n");
				}
			}
		} catch (IOException e) {
			throw new IOException("Error writing back to the file: " + file, e);
		}

		return removedBook;
	}

	public void displayBorrowedBooksOfStudent() throws IOException {
		List<String> lines = Files
				.readAllLines(Paths.get(String.format("src/resources/student-database/%s.txt", studentID)));

		if (lines.size() != 0) {
			lines.remove(0); // Remove the description of the file.

			System.out.println("Here are your books borrowed.");
			for (String line : lines) {
				System.out.println(line);
			}
		} else {
			System.out.println("No borrowed books yet.");
		}
	}

	public Book searchStudentBorrowedBook(int catalogID) {
		Book searchedBook = studentBorrowed.stream().filter(book -> book.getCatalogID() == Integer.valueOf(catalogID))
				.findAny().orElse(null);

		if (searchedBook != null) {
			return searchedBook;
		}

		// it will return null if the searchedBook does not find any.
		return null;
	}

	// Transaction of borrowed book
	public void displayTransactionOfBorrowedBook(Book book) {
		System.out.println("===========================================================================");
		System.out.println("\t RECEIPT");
		System.out.printf("Student Name  : %s %s \n", firstName, lastName);
		System.out.println("Student ID    : " + studentID);
		System.out.println("Book Borrowed : " + book);
		System.out.println("Date Today    : " + now);
		System.out.println("Deadline      : " + now.plusDays(7));
		System.out.println("Kindly return it before deadline and the receipt to avoid penalty.");
		System.out.println("===========================================================================");
	}

	// Transaction of returned book
	public void displayTransactionOfReturnedBook(Book book) {
		double penalty = daysLate * 50;
		System.out.println("===========================================================================");
		System.out.println("\t RECEIPT");
		System.out.printf("Student Name  : %s %s \n", firstName, lastName);
		System.out.println("Student ID    : " + studentID);
		System.out.println("Book Returned : " + book);
		System.out.println("Date Today    : " + now);
		System.out.println("Deadline      : " + deadline);
		// If the book returned after the deadline
		if (penalty > 0) {
			System.out.println("Penalty Pay   : " + penalty);
		} else {
			System.out.println("\tNo penalty.");
		}
		System.out.println("\tThank you!");
		System.out.println("===========================================================================");
	}

	public void addToFileOfStudent(Book book) throws IOException {
		// Putting the borrowed book to file
		try (FileWriter fw = new FileWriter(studentLibraryData, true)) {
			fw.write("\n" + book); // Add to file of student
			studentBorrowed.add(book); // Add to temporary storage for the books borrowed
		}
	}

	@Override
	public boolean logOut(String answer) {
		return answer.equalsIgnoreCase("Yes");
	}
}
