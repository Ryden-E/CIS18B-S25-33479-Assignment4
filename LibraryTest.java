import java.util.*;

// =============================
// Exception Classes
// =============================
class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }
}

class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}

// =============================
// Book Class
// =============================
class Book {
    private String title;
    private String author;
    private String genre;
    private boolean isAvailable;

    public Book(String title, String author, String genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isAvailable = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void checkout() throws BookNotAvailableException {
        // Throw exception if book is not available
        if (!isAvailable) {
            throw new BookNotAvailableException("Sorry, this book is not available.");
        }

        isAvailable = false;
    }

    public void returnBook() {
        // Mark book as available
        if (!isAvailable) {
            isAvailable = true;
        }
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    public String toString() {
        return title + " by " + author + " (" + genre + ")";
    }
}

// Iterator Class
class GenreIterator implements Iterator<Book> {
    private List<Book> books;
    private int index = 0;

    public GenreIterator(List<Book> books) {
        this.books = books;
    }

    // Allows the iterator to go through and find available books 
    @Override
    public boolean hasNext() {
        while (index < books.size()) {
            if (books.get(index).isAvailable()) {
                return true;
            } else {
                index++;
            }
    }
    return false;
}
    // Returns the next avaialble book 
    @Override
    public Book next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return books.get(index++);
    }
}

// =============================
// LibraryCollection Class
// =============================
class LibraryCollection {
    private Map<String, List<Book>> genreMap;

    public LibraryCollection() {
        genreMap = new HashMap<>();
    }

    public void addBook(Book book) {
        // Add books to genreMap
        genreMap.computeIfAbsent(book.getGenre(), k -> new ArrayList<>()).add(book); // This adds the book and its genre if not already there
    }

    public Iterator<Book> getGenreIterator(String genre) {
        // Return custom iterator for available books in that genre
        List<Book> list = genreMap.getOrDefault(genre, Collections.emptyList());
        return new GenreIterator(list);
    }

    // Add methods to search and return books
    public Book SearchTitle(String title) throws BookNotFoundException {
        for (List<Book> list : genreMap.values()) {
            for (Book book: list) {
                if (book.getTitle().equalsIgnoreCase(title)) {
                    return book;
                }
            }
        }
        throw new BookNotFoundException("Sorry, no book under that title was found.");
}

    public void checkoutBook(String title)
            throws BookNotFoundException, BookNotAvailableException {
        Book book = SearchTitle(title);
        book.checkout();       
    }

    public void returnBook(String title) throws BookNotFoundException {
        Book book = SearchTitle(title);
        book.returnBook();
    }
}

// =============================
// Main Program
// =============================
public class LibraryTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LibraryCollection library = new LibraryCollection();

        // Add sample books to library
        library.addBook(new Book("Percy Jackson: The Lightning Thief", "Rick Riordan", "Fantasy"));
        library.addBook(new Book("Percy Jackson: The Sea of Monsters", "Rick Riordan", "Fantasy"));
        library.addBook(new Book("Frankenstein", "Mary Shelley", "Gothic"));
        library.addBook(new Book("The Castle of Otranto", "Horace Walpole", "Gothic"));
        library.addBook(new Book("Dune", "Frank Herbert", "Science Fiction"));
        library.addBook(new Book("Brave New World", "Aldous Huxley", "Science Fiction"));

        // Prompt user for genre, list available books using iterator
        System.out.println("Welcome to the Library!");
        // Creates menu loop
        while (true) {
            System.out.print("Which genre do you want to view? (type 'exit' to quit): ");
            String genre = scanner.nextLine().trim();
            if (genre.equalsIgnoreCase("exit")) break;
            
            // Lists available books in genre
            Iterator<Book> it = library.getGenreIterator(genre);
            System.out.println("Books in the " + genre + " genre:");
            if (!it.hasNext()) {
                System.out.println("Sorry, no books are available in that genre.");
                continue;
            }
            while (it.hasNext()) {
                System.out.println(" - " + it.next());
            }
        
        // Allow checkout and return, handling exceptions
            System.out.print("Which title would you like to borrow? (Write 'none' if none)");
            String borrowing = scanner.nextLine().trim();
            if (!borrowing.equalsIgnoreCase("none")) {
                try {
                    Book book = library.SearchTitle(borrowing);
                    book.checkout();
                    System.out.println("Book borrowed successfully. Enjoy!");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            
            System.out.print("Return a book? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.print("Enter title to return: ");
                String returning = scanner.nextLine().trim();
                try {
                    Book book = library.SearchTitle(returning);
                    book.returnBook();
                    System.out.println("Book returned successfully. Thank you!");
                } catch (BookNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        System.out.println("Thank you for using the Library!");
        scanner.close();
    }
}
