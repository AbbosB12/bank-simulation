drop table if exists otp;
create table otp
(
    id                  bigint not null
        primary key,
    otp_code            integer,
    verification_status varchar(255),
    account_id          bigint
);
drop table if exists transactions;

create table transactions
(
    id            bigint not null
        primary key,
    amount        numeric(19, 2),
    creation_date date,
    message       varchar(255),
    receiver_id   bigint,
    sender_id     bigint
);
drop table if exists accounts;
create table accounts
(
    id             bigint not null
        primary key,
    account_status varchar(255),
    account_type   varchar(255),
    balance        numeric(19, 2),
    creation_date  date,
    otp_verified   boolean,
    phone_number   varchar(255),
    user_id        bigint
);
