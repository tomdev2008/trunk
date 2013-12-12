com.mysql.jdbc.Driver
jdbc:mysql://127.0.0.1:3306/admin

create table offline(
   chat_id int auto_increment primary key,
   chat_target varchar(255) not null,
   chat_content varchar(255) not null,
   chat_date varchar(255) not null,
   chat_type int not null
);

insert into offline values (null, '13612935690','hello pansa', '2013-09-01',0);
insert into offline values (null, '13612935691','hello pansha','2013-09-08',1);
insert into offline values (null, '13612935692','hello admin', '2013-09-09',1);

create table user(
   user_id int auto_increment primary key,
   user_name varchar(255) unique not null,
   user_pwd varchar(255) not null,
   user_nick varchar(255) not null
);

create table user(
	_id  integer primary key,
	user_name text not null,
	user_nick  text not null,
	user_head  text not null,
	user_pwd text not null
);

insert into user values (null, '13612935690','admins', 'wells');
insert into user values (null, '13612935691','admins', 'pamsa');
insert into user values (null, '13612935692','admins', 'pansha');
insert into user values (null, '13612935693','admins', 'sasa');
insert into user values (null, '13612935694','admins', 'ps');
insert into user values (null, '13612935695','admins', 'wells');
insert into user values (null, '13612935696','admins', '水凌沙');
insert into user values (null, '13612935697','admins', 'well');
insert into user values (null, '13612935698','admins', '沙沙');
insert into user values (null, '13612935699','admins', 'admin');