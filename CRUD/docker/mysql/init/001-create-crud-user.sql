create user if not exists 'crud'@'%' identified by '';
grant all privileges on cruddb.* to 'crud'@'%';
flush privileges;
