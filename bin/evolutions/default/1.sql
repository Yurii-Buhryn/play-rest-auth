# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                        bigint not null,
  auth_token                varchar(255),
  activation_token          varchar(255),
  email_address             varchar(256) not null,
  sha_password              varbinary(64) not null,
  active                    boolean not null,
  full_name                 varchar(256) not null,
  creation_date             timestamp not null,
  constraint uq_user_auth_token unique (auth_token),
  constraint uq_user_activation_token unique (activation_token),
  constraint uq_user_email_address unique (email_address),
  constraint pk_user primary key (id))
;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists user_seq;

