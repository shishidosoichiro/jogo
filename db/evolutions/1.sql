# Basic Schema
 
# --- !Ups
create table event (
		id bigserial not null,
		created_at timestamp,
		name varchar(255),
		session_id varchar(255),
		value varchar(255),
		site_id bigserial,
		testcase_id bigserial,
		primary key (id)
);

create table site (
		id bigserial not null,
		name varchar(255),
		url varchar(255),
		jogo_user_id bigserial,
		primary key (id)
);

create table testcase (
		id bigserial not null,
		name varchar(255),
		site_id bigserial,
		primary key (id)
);

create table twitter_account (
		id bigserial not null,
		description varchar(255),
		name varchar(255),
		profile_image_url varchar(255),
		savedAt timestamp,
		screen_name varchar(255),
		secret varchar(255),
		token varchar(255),
		twitter_id varchar(255),
		jogo_user_id bigserial,
		primary key (id)
);

create table jogo_user (
		id bigserial not null,
		fullname varchar(255),
		iconUrl varchar(255),
		profile varchar(255),
		trackKey varchar(255),
		username varchar(255),
		primary key (id)
);

alter table event 
		add constraint fk_event_testcase
		foreign key (testcase_id) 
		references testcase;

alter table event 
		add constraint fk_event_site
		foreign key (site_id) 
		references site;

alter table site 
		add constraint fk_site_jogo_user 
		foreign key (jogo_user_id) 
		references jogo_user;

alter table testcase 
		add constraint fk_testcase_site
		foreign key (site_id) 
		references site;

alter table twitter_account 
		add constraint fk_twitter_account_jogo_user
		foreign key (jogo_user_id) 
		references jogo_user;

create sequence hibernate_sequence;

# --- !Downs
alter table event 
		drop constraint fk_event_testcase;

alter table event 
		drop constraint fk_event_site;

alter table site 
		drop constraint fk_site_jogo_user;

alter table testcase 
		drop constraint fk_testcase_site;

alter table twitter_account 
		drop constraint fk_twitter_account_jogo_user;

drop table event cascade;

drop table site cascade;

drop table testcase cascade;

drop table twitter_account cascade;

drop table jogo_user cascade;

drop sequence hibernate_sequence;
