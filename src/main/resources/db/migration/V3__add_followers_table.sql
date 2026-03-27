create table follows
(
    id bigint auto_increment not null,
    follower_id bigint not null,
    following_id bigint not null,
    date_created datetime default (curdate()),
    constraint follows_pk primary key (id),
    constraint follower_id_fk
        foreign key (follower_id) references users(id) on delete cascade,
    constraint following_id_fk
        foreign key (following_id) references users(id) on delete cascade,
    constraint follower_following_unique unique(follower_id, following_id)
);