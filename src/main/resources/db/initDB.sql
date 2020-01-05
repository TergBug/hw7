create database if not exists JavaIONIO;
use javaionio;
drop table if exists developer_skill;
drop table if exists developers;
drop table if exists skills;
drop table if exists accounts;
create table if not exists skills(
    id int primary key auto_increment unique not null,
    name varchar(255) unique not null
);
create table if not exists accounts(
    id int primary key auto_increment unique not null,
    name varchar(255) unique not null,
    status varchar(255)
);
create table if not exists developers(
    id int primary key auto_increment unique not null,
    first_name varchar(255),
    last_name varchar(255),
    account_id int,
    foreign key (account_id) references accounts(id)
);
create table if not exists developer_skill(
    developer_id int not null,
    skill_id int not null,
    primary key (developer_id, skill_id),
    foreign key (developer_id) references developers(id),
    foreign key (skill_id) references skills(id)
);