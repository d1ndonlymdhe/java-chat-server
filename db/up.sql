create database chat;
use chat;
create table users
(
    id       varchar(255),
    username varchar(255),
    salt     varchar(255),
    hash     varchar(255),
    primary key (id)
);

create table tokens
(
    id      varchar(255),
    user_id varchar(255),
    primary key (id),
    foreign key (user_id) references users (id)
);

create table notifications
(
    id          varchar(255),
    message     varchar(255),
    receiver_id varchar(255),
    primary key (id),
    foreign key (receiver_id) references users (id)
);


