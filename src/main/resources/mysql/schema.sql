create schema if not exists sausage;
use sausage;

create table if not exists app_users (
    user_id     varchar(255) not null unique default (uuid()),
    username    varchar(255) not null unique,
    icon        varchar(255) not null default 'cat',
    date_joined timestamp not null default now(),
    FULLTEXT (username)

) ;

alter table app_users add primary key app_users(user_id);
alter table app_users add foreign key app_users_users_fk(username) references users(username) on delete cascade ;

create unique index person_username_idx using hash on app_users (username);


create table if not exists reviews (
    review_id       varchar(255) not null unique default (uuid()),
    author_id       varchar(255) not null ,
    date_created    timestamp not null default now(),
    date_review     timestamp not null,
    date_updated    timestamp null default null,
    stars           smallint not null default 0,
    text            varchar(512) not null default '',
    img_link        varchar(255) null ,
    FULLTEXT (text),

    constraint foreign key review_author_id_fk(author_id) references app_users(user_id) on delete cascade
) ;

alter table reviews add primary key review(review_id);

create index reviews_date_idx using hash on reviews (date_created);
create index reviews_auther_idx using hash on reviews (author_id);


create table if not exists crims_involved (
    review_id   varchar(255) not null ,
    user_id     varchar(255) not null ,

    constraint primary key (review_id, user_id),
    constraint foreign key crims_review_fk(review_id) references reviews(review_id),
    constraint foreign key crims_user_fk(user_id)     references app_users(user_id)
) ;


create table if not exists unknown_crims_involved (
    review_id   varchar(255) not null ,
    crim_name   varchar(255) not null ,

    constraint primary key (review_id, crim_name),
    constraint foreign key unknown_crims_review_fk(review_id) references reviews(review_id)
) ;


create table if not exists review_likes (
    review_id varchar(255) not null ,
    user_id   varchar(255) not null ,

    constraint primary key (review_id, user_id) ,
    constraint foreign key likes_review_fk(review_id) references reviews(review_id) on delete cascade ,
    constraint foreign key likes_user_fk(user_id)     references app_users(user_id) on delete cascade
) ;


create table if not exists review_comments (
    comment_id varchar(255) not null unique default (uuid()) ,
    review_id  varchar(255) not null ,
    user_id    varchar(255) not null ,
    comment    varchar(255) not null ,

    constraint primary key (review_id, user_id) ,
    constraint foreign key likes_review_fk(review_id) references reviews(review_id) on delete cascade ,
    constraint foreign key likes_user_fk(user_id)     references app_users(user_id) on delete cascade

) ;



#### VIEWS
create or replace view crim_info as
select
    au.username as username,
    au.icon as icon,
    count(r.review_id) as total,
    avg(stars) score,
    min(r.date_review) as first_review,
    max(r.date_review) as last_review
from reviews r
         join crims_involved ci on r.review_id = ci.review_id # get all crims for the review
         join (select username, user_id, icon from app_users) as au on ci.user_id = au.user_id        # get the crims username
         group by au.username
;