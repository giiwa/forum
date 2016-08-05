#drop table if exists gi_topic;
create table gi_topic
(
	id bigint,
	owner bigint,
	parent bigint,
	top int,
	created bigint,
	photo int,
	title varchar(1024),
	updated bigint,
	content varchar(8196),
	cid bigint,
	replies int,
	reads int,
	index_flag bigint
);

#drop table if exists gi_circling;
create table gi_circling
(
	id bigint,
	owner bigint,
	access varchar(20),
	created bigint,
	name varchar(128),
	memo varchar(255),
	photo varchar(128),
	updated bigint,
	user_state varchar(20),
	index_flag bigint
);

#drop table if exists gi_follower;
create table gi_follower
(
 	uid bigint,
 	created bigint,
 	name varchar(50),
 	state varchar(20),
 	id varchar(20),
 	updated bigint,
 	cid bigint
);
