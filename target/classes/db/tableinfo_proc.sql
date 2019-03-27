/* The use of delimiter below is to change the delimiter from the ; to the given one so the statements inside sp can execute. */
delimiter //

create procedure table_info()
begin
	declare exit_loop boolean;
	declare v_tabnames varchar(30) default "";
	declare v_createtime varchar(90) default "";
	declare tab_cursor cursor for select table_name, create_time from information_schema.tables where table_schema = 'TEST';
	/* this will set the exit_loop to true automatically as the cursors resultset is empty */
	declare continue handler for not found set exit_loop = true;
	open tab_cursor;
	
	read_loop: LOOP
		fetch tab_cursor into v_tabnames, v_createtime;
		/* check to see if the cursor is finished*/
		if exit_loop then
			leave read_loop;
		end if;
		select table_name, column_name, data_type, is_nullable, column_type, column_key, v_createtime as create_time from information_schema.columns where table_name = v_tabnames and table_schema = 'TEST';
	end loop;
	
	close tab_cursor;
end //
delimiter ;