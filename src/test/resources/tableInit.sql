truncate table USER_TABLE;
truncate table feed;

insert into user_table(user_id, user_name, user_password, email, weight)
values ('mockUser', '이영진', '$2a$10$uPM5cI9oLxVlppZLDrkxCOwnj/IJBi0kltM2gdrfVibA9m05hK3M2', 'dudwls0505@naver.com',
        50.3);

insert into user_table(user_id, user_name, user_password, email, weight)
values ('wrongUser', '김영진', '$2a$10$uPM5cI9oLxVlppZLDrkxCOwnj/IJBi0kltM2gdrfVibA9m05hK3M2', 'dudwls0505@nate.com',
        50.3);

insert into feed(contents, user_id)
values ('게시글내용1', 1);

