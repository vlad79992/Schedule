-- drop database if exists schedule;

create table groups
(
    guid         uuid         primary key,
    name         varchar(100) not null,
    faculty      varchar(255),
    program_type varchar(50),
    course       varchar(50)
);
create table lessons
(
    id            serial  primary key,
    discipline    varchar not null,
    workload_type varchar,
    location      varchar,
    teacher       varchar,
    lesson_start  timestamp,
    lesson_end    timestamp,
    group_id      uuid    references groups
);
create table updates
(
    id          serial primary key,
    group_id    uuid   references groups,
    week_start  date,
    last_update timestamp
);