create table post_likes(
    id bigint auto_increment not null,
    post_id bigint not null,
    user_id bigint not null,
    constraint post_likes_post_id_fk
       foreign key (post_id) references posts(id) on delete cascade,
    constraint post_likes_user_id_fk
        foreign key (user_id) references users(id) on delete cascade,
    constraint post_likes_pk primary key (id),
    constraint post_likes_unique unique (user_id, post_id)
);

create table comment_likes(
    id bigint auto_increment not null,
    comment_id bigint not null,
    user_id bigint not null,
    constraint comment_likes_comment_id_fk
       foreign key (comment_id) references comments(id) on delete cascade,
    constraint comment_likes_user_id_fk
       foreign key (user_id) references users(id) on delete cascade,
    constraint comment_likes_pk primary key (id),
    constraint comment_likes_unique unique (user_id, comment_id)
);

alter table posts add column like_count bigint default 0;
alter table comments add column like_count bigint default 0;
