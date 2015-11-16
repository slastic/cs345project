create table plans(pid int primary key, planName text, fee int, movieLimit int);
create table person(userName text, cid int primary key, password text, city text, state text, email text, phoneNumber text, planID int, foreign key(planID) references plans(pid));
CREATE TABLE MOVIE (id int PRIMARY KEY, name VARCHAR(150), year int);
create table rentals(cid int, foreign key(cid) references person(cid), movieId int, foreign key(movieId) references movie(id), dateCheckedOut date, dateCheckedIn date);

\copy MOVIE from 'C:/imdb2015/movie.txt' with delimiter '|' null as '';

insert into plans values(1, 'valuePlan', 8.99, 2);
insert into plans values(2, 'economyPlan',12.99, 5);
insert into plans values(3, 'jumboPlan',19.99, 20);

insert into person values('user1', 2, '12345', 'Houston', 'Texas', 'user1@emailClient.com', '555-4565', 1);
insert into person values('user2', 2, '6789', 'Houston', 'Texas', 'user2@emailClient.com', '555-4765', 1);
insert into person values('user3', 3, '12456', 'Boston', 'Massachussetts', 'user3@emailClient.com', '554-4568', 1);


insert into rentals values(1, 34, '2015-06-05', '2015-06-12');
insert into rentals values(1, 75, '2015-06-11', '2015-06-15');
insert into rentals values(3, 77, '2015-06-07', '2015-06-16');
insert into rentals values(1, 89, '2015-06-02', '2015-06-12');
