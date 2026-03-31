alter table users
    add column profile_picture_url varchar(255),
    add column bio text,
    add column followers_count int not null default 0,
    add column following_count int not null default 0;

alter table posts
    modify column caption text;

alter table comments
    change column `text` content text not null;