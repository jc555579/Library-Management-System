package accounts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import library.Book;

public class Admin extends Person {
	// Instance Variables
	ArrayList<Book> booksAvailable = new ArrayList<>();;
	private String password;

	// Getting the book file using String
	private String bookFile = System.getProperty("user.dir") + "/src/resources/library/books.txt";

	public Admin(String firstName, String lastName, String password) throws IOException {
		super(firstName, lastName);
		this.password = password;

		// List<String> lines = Files.lines(bookFilePath).collect(Collectors.toList());
		// This line is an error
		List<String> lines = Files.readAllLines(Paths.get("src/resources/library/books.txt"));

		if (lines.size() != 0) {
			lines.remove(0); // This will remove the instruction of the file.
			for (String line : lines) {
				Book book = new Book(line);
				booksAvailable.add(book);
			}
		}
	}

	public Admin() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("src/resources/library/books.txt"));

		if (lines.size() != 0) {
			lines.remove(0); // This will remove the instruction of the file.
			for (String line : lines) {
				Book book = new Book(line);
				booksAvailable.add(book);
			}
		}
	}

	@Override
	public boolean logIn() throws IOException { // Log-in method
		// Getting the file then putting it into a list to get the stream() later.
		List<String> adminAccounts = Files.lines(Paths.get("src/resources/accounts/admin.txt").toAbsolutePath())
				.collect(Collectors.toList());

		if (adminAccounts.size() != 0) { // Removing the instruction of the file
			adminAccounts.remove(0);
		}

		// Searching the admin's input and verifying through the account files.
		boolean inFile = adminAccounts.stream().anyMatch(
				adminAccount -> adminAccount.equals(String.format("%s %s %s", firstName, lastName, password)));

		return inFile;
	}

	// This method is for adding a book and updating it in the file.
	// This method will also be used when getting the books in the files.
	public void addBook(Book book) throws IOException {
		// Creating an Object of FileWriter to be able to write or append in the file.
		// It will automatically close because of try with resources.
		try (FileWriter fw = new FileWriter(bookFile, true)) {
			// Creating an Object of FileWriter to be able to write or append in the file.
			if (isCatalogIDUnique(book.getCatalogID())) {
				booksAvailable.add(book);
				fw.write("\n" + book);
				System.out.println("Book added to library books database!");
			} else {
				System.out.println("The Catalog ID is already in the books! You cannot add if it is not unique. ");
			}

		}

	}

	public void removeBook(int catalogID) throws IOException {
		// Get the matching book, if any.
		Book removedBook = booksAvailable.stream().filter(book -> book.getCatalogID() == catalogID).findAny()
				.orElse(null); // Return null if no matching book is found

		if (removedBook != null) {
			booksAvailable.remove(removedBook); // Remove the book from the list
			System.out.println("Removed Book: " + removedBook);
			removeFileLine(catalogID); // updating the books.txt
		} else {
			System.out.println("No book found with Catalog ID: " + catalogID);
		}
	}

	public void removeFileLine(int catalogID) throws IOException {
		// Temporary storage for books
		List<String> booksToKeep = new ArrayList<>();

		// Read the file first
		try (BufferedReader reader = new BufferedReader(new FileReader(bookFile))) {
			String bookLine;

			// Read the file line by line
			while ((bookLine = reader.readLine()) != null) {
				// If the line does not contain the catalog ID, add it to the list
				if (!bookLine.contains(Integer.toString(catalogID))) {
					booksToKeep.add(bookLine);
				}
			}
		}

		// Write the remaining lines back to the file (after the file has been read)
		try (FileWriter fw = new FileWriter(bookFile)) {
			for (int i = 0; i < booksToKeep.size(); i++) {
				// If the book is the last line, do not add a new line after it
				if (i == booksToKeep.size() - 1) {
					fw.write(booksToKeep.get(i));
				} else {
					fw.write(booksToKeep.get(i) + "\n");
				}
			}
		}
	}

	public void displayBorrowedBooksOfStudents() throws FileNotFoundException, IOException {
//		String bookBorrowedFile = System.getProperty("user.dir") + "/src/resources/library/borrowed-books.txt";

		List<String> borrowedBooks = Files.lines(Paths.get("src/resources/library/borrowed-books.txt").toAbsolutePath())
				.collect(Collectors.toList());

		if (borrowedBooks.size() != 0) {
			borrowedBooks.remove(0);

			for (String borrowedBook : borrowedBooks) {
				System.out.println(borrowedBook);
			}
		} else {
			System.out.println("No borrowed books yet!");
		}

	}

	// This method will display the list of books available.
	public void displayListOfBooks() {
		if (booksAvailable.isEmpty()) {
			System.out.println("No Books Available!");
		} else {
			System.out.println("Here are the list of books available!");

			// Using stream method to print the books available.
			booksAvailable.stream().forEach(e -> System.out.println(e));
		}
	}

	public Book searchBook(int catalogID) {
		Book searchedBook = booksAvailable.stream().filter(book -> book.getCatalogID() == Integer.valueOf(catalogID))
				.findAny().orElse(null);

		if (searchedBook != null) {
			return searchedBook;
		}

		// it will return null if the searchedBook does not find any.
		return null;
	}

	public boolean isCatalogIDUnique(int catalogID) throws FileNotFoundException, IOException {
		// Initializing the file lines into a list
		try (BufferedReader reader = new BufferedReader(new FileReader(bookFile))) {
			String line;

			while ((line = reader.readLine()) != null) {
				// Assuming catalog ID is the first element, split by a delimiter
				if (line.contains(String.valueOf(catalogID))) {
					return false; // Catalog ID is not unique
				}
			}
		}

		return true; // It will return true if it's unique
	}

	public String getPassword() {
		return password;
	}

	@Override
	public boolean logOut(String answer) {
		return answer.equalsIgnoreCase("Yes");
	}

}
