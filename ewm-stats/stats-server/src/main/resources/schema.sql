drop table if exists hits;
create table hits (
   id integer generated by default as identity,
   app varchar(255),
   uri varchar(255),
   ip varchar(255),
   time timestamp,
   hits_amount integer,
   primary key (id)
);