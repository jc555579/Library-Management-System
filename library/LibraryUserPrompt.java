package library;

// This class is used for guiding user what to enter.
// All methods are static.
public class LibraryUserPrompt {

	public static void libraryMenu() {
		System.out.println("----------------------------------------");
		System.out.println("Welcome to Library Management System!");
		System.out.println("1. Admin Log-In");
		System.out.println("2. Student Log-In");
		System.out.println("3. Exit");
		System.out.println("----------------------------------------");
		System.out.print("Enter between [1-3] only: ");
	}

	public static void adminMenu() {
		System.out.println("----------------------------------------");
		System.out.println("--Admin Menu--");
		System.out.println("1. Add Book");
		System.out.println("2. Remove Book");
		System.out.println("3. Search Catalog ID");
		System.out.println("4. List of Books Available");
		System.out.println("5. List of Books Borrowed by Students");
		System.out.println("6. Log Out");
		System.out.println("----------------------------------------");
		System.out.print("Enter from [1-6] only : ");
	}

	public static void studentMenu() {
		System.out.println("----------------------------------------");
		System.out.println("--Student Menu--");
		System.out.println("1. Borrow Book");
		System.out.println("2. Return Book");
		System.out.println("3. Borrowed Book/s");
		System.out.println("4. Log Out");
		System.out.println("----------------------------------------");
		System.out.print("Enter from [1-4] only : ");
	}
}
