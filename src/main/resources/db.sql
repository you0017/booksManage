select * from books where 1=1 and bookauthor like '%张永强%'

ALTER TABLE books
    MODIFY COLUMN bookimage VARCHAR(1000);