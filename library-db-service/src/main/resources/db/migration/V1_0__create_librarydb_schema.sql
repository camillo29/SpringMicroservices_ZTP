CREATE TABLE IF NOT EXISTS tbl_users (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(255) NOT NULL,
    roles varchar(255) NOT NULL);

CREATE TABLE IF NOT EXISTS tbl_books (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    quantity INT NOT NULL);

CREATE TABLE IF NOT EXISTS tbl_rentals (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userid_fk INT NOT NULL,
    bookid_fk INT NOT NULL,
    rental_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (bookid_fk) REFERENCES tbl_books(id));

INSERT INTO tbl_users(id, name, roles) VALUES (10, 'Jan Nowak', 'user');
INSERT INTO tbl_users(id, name, roles) VALUES (20, 'Grażyna Kowalska', 'user,admin');
INSERT INTO tbl_users(id, name, roles) VALUES (30, 'Tomasz Wójcik', 'user');
ALTER TABLE tbl_users ALTER COLUMN id RESTART WITH 40;

INSERT INTO tbl_books(id, title, author, quantity) VALUES (1, 'Praktyczna inżynieria wsteczna. Metody, techniki i narzędzia', 'Gynvael Coldwind', 2);
INSERT INTO tbl_books(id, title, author, quantity) VALUES (2, 'Zrozumieć programowanie', 'Gynvael Coldwind', 3);
INSERT INTO tbl_books(id, title, author, quantity) VALUES (3, 'Python - Wprowadzenie', 'Mark Lutz', 4);
INSERT INTO tbl_books(id, title, author, quantity) VALUES (4, 'Python. Leksykon kieszonkowy', 'Mark Lutz', 5);
INSERT INTO tbl_books(id, title, author, quantity) VALUES (5, 'Python. Zadania z programowania. Przykładowe imperatywne rozwiązania', 'Mirosław J. Kubiak', 7);
ALTER TABLE tbl_books ALTER COLUMN id RESTART WITH 6;

INSERT INTO tbl_rentals(id, userid_fk, bookid_fk, rental_date, return_date) VALUES (1001, 10, 5, '2021-10-01', '2021-10-14');
INSERT INTO tbl_rentals(id, userid_fk, bookid_fk, rental_date, return_date) VALUES (1002, 10, 3, '2021-11-01', NULL);
INSERT INTO tbl_rentals(id, userid_fk, bookid_fk, rental_date, return_date) VALUES (1003, 10, 1, '2021-10-15', NULL);
INSERT INTO tbl_rentals(id, userid_fk, bookid_fk, rental_date, return_date) VALUES (1004, 20, 1, '2021-10-16', NULL);
ALTER TABLE tbl_rentals ALTER COLUMN id RESTART WITH 1005;

