set referential_integrity false;
truncate table transaction_tb restart identity ;
truncate table account_tb restart identity ;
truncate table user_tb restart identity ;
set referential_integrity true;