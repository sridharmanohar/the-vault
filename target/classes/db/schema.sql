create table users (
id int(4) not null auto_increment primary key,
firstname varchar(20) not null,
lastname varchar(20) not null
)

insert into users values (1,'sridhar','manohar');

create table topics (
id int(4) not null auto_increment primary key,
topicname varchar(40) not null
)

insert into topics values (1,'sharekhan');
insert into topics values (2,'email');

create table topic_groups (
id int(4) not null auto_increment primary key,
topicid int(4) not null,
userid int(4) not null,
foreign key (topicid) references topics(id),
foreign key (userid) references users(id)
)

insert into topic_groups values (1,1,1);
insert into topic_groups values (2,2,1);
insert into topic_groups values (3,2,1);

create table topic_templates (
id int(4) not null auto_increment primary key,
topicid int(4) not null,
propname varchar(40) not null,
foreign key (topicid) references topics(id) 
)

insert into topic_templates values (1,1,'dp client id');
insert into topic_templates values (2,1,'cust id');
insert into topic_templates values (3,1,'login id');
insert into topic_templates values (4,1,'pwd');
insert into topic_templates values (5,2,'email id');
insert into topic_templates values (6,2,'email pwd');


create table topic_details (
id int(4) not null auto_increment primary key,
templateid int(4) not null,
topicgrpid int(4) not null,
propvalue varchar(100) not null,
foreign key (templateid) references topic_templates(id),
foreign key (topicgrpid) references topic_groups(id)
)

insert into topic_details values (1,1,1,'w386410');
insert into topic_details values (2,2,1,'303499');
insert into topic_details values (3,3,1,'kompally1985');
insert into topic_details values (4,4,1,'jingjong');
insert into topic_details values (5,5,2,'sridharm84@gmail.com');
insert into topic_details values (6,6,2,'chingchan@84');
insert into topic_details values (7,5,3,'bdory.dory@gmail.com');
insert into topic_details values (8,6,3,'dorydory@84');