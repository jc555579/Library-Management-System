package library;

public class Book {
	private int catalogID;
	private String bookName;
	private String author;
	private String fileLine;

	public Book(int catalogID, String bookName, String author) {
		this.catalogID = catalogID;
		this.bookName = bookName;
		this.author = author;
	}

	public Book(String fileLine) {
		this.fileLine = fileLine;

		/*
		 * this will assign the catalog ID of the file, in the respective index. It will
		 * be essential for removing a line in a file that requires a catalog ID.
		 */
		this.catalogID = Integer.valueOf(fileLine.substring(2, 7));
	}

	public String toString() { // The toString() method (belongs to the Object Class) is helpful for direct
								// print of the reference variable
								// This will be used to display the content of the book
		if (fileLine != null) {
			// If fileLine was set, return its value
			return String.format(fileLine);
		} else {
			// Default behavior when catalogID, bookName, and author are used
			return String.format("- %d, %s, %s", catalogID, bookName, author);
		}
	}

	public String toString(String fileLine) {
		return String.format(fileLine);
	}

	public String toStringFileLine() {
		return String.format(fileLine);
	}

	// Getters and Setters
	public int getCatalogID() {
		return catalogID;
	}

	public void setCatalogID(int catalogID) {
		this.catalogID = catalogID;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
