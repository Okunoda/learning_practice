create table chat_memory(
                             id bigint primary key auto_increment,
                             message text,
                             user_id bigint,
                             `role` varchar(36)
);