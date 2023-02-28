CREATE TABLE books(
      id INT AUTO_INCREMENT PRIMARY KEY,
      title TEXT,
      published_date DATE,
      author_id INT,
      deleted BOOL NOT NULL DEFAULT false
);

CREATE TABLE authors(
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name TEXT,
    last_name TEXT,
    birth_date DATE,
    deleted BOOL NOT NULL DEFAULT false
);

CREATE TABLE categories(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name TEXT,
    deleted BOOL NOT NULL DEFAULT false
);

CREATE TABLE book_categories(
    book_id INT NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY(book_id) REFERENCES books(id),
    FOREIGN KEY(category_id) REFERENCES categories(id),
    UNIQUE(book_id, category_id)
)
