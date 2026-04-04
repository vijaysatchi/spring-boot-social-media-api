drop table post_likes;
drop table comment_likes;

create table post_likes (
    user_id bigint not null,
    post_id bigint not null,
    primary key (user_id, post_id),
    constraint post_likes_user_fk
        foreign key (user_id) references users(id) on delete cascade,
    constraint post_likes_post_fk
        foreign key (post_id) references posts(id) on delete cascade
);

create table comment_likes (
    user_id bigint not null,
    comment_id bigint not null,
    primary key (user_id, comment_id),
    constraint comment_likes_user_fk
       foreign key (user_id) references users(id) on delete cascade,
    constraint comment_likes_comment_fk
       foreign key (comment_id) references comments(id) on delete cascade
);