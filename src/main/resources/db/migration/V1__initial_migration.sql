create table users
(
    id bigint auto_increment not null,
    name varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    date_created date default (curdate()),
    constraint users_pk primary key (id),
    constraint users_email_unique unique(email)
);

create table posts
(
    id bigint auto_increment not null,
    caption varchar(255),
    date_created timestamp default (current_timestamp) not null,
    user_id bigint not null,
    constraint posts_user_id_fk
        foreign key (user_id) references users(id) on delete cascade,
    constraint posts_pk primary key (id)
);

create table comments
(
    id bigint auto_increment not null,
    text varchar(255) not null,
    user_id bigint not null,
    post_id bigint not null,
    constraint user_comment_id_fk
        foreign key (user_id) references users(id) on delete cascade,
    constraint post_comment_id_fk
        foreign key (post_id) references posts(id) on delete cascade,
    constraint comments_pk primary key (id)
);