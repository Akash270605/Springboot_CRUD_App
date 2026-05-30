create table users (
    id bigint auto_increment primary key,
    username varchar(50) not null unique,
    email varchar(120) not null unique,
    password varchar(255) not null,
    role varchar(30) not null,
    created_at timestamp not null
);

create table posts (
    id bigint auto_increment primary key,
    title varchar(160) not null,
    content text not null,
    published boolean not null,
    author_id bigint not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

alter table posts
    add constraint fk_posts_author
    foreign key (author_id) references users(id)
    on delete cascade;

create index idx_posts_author_id on posts(author_id);
create index idx_posts_published on posts(published);
