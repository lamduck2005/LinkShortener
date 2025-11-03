-- Bảng 1: roles (Phân quyền)
create table roles (
                       id serial primary key, -- là INT INDENTITY
                       name varchar(50) not null unique
);

-- Bảng 2: users (Hoàn chỉnh)
create table users (
                       id bigserial primary key,
                       email varchar(255) not null unique, -- dùng để đăng nhập
                       username varchar(100) not null unique,
                       password_hash varchar(255) not null,
                       is_active boolean not null default false,

                       created_at timestamp with time zone default current_timestamp,
                       updated_at timestamp with time zone default current_timestamp
);

-- Bảng 3: user_roles (Bảng nối Nhiều-Nhiều)
create table user_roles (
                            user_id bigint not null,
                            role_id int not null,

    -- Khóa chính tổng hợp ( 1 user có thể có nhiều rule)
                            primary key (user_id, role_id),

    -- Khóa ngoại
                            constraint fk_user_roles_user
                                foreign key(user_id)
                                    references users(id)
                                    on delete cascade,

                            constraint fk_user_roles_role
                                foreign key(role_id)
                                    references roles(id)
                                    on delete cascade
);

-- Bảng 4: snippets (Bảng chính lưu mẩu tin)
create table snippets (
                          id bigserial primary key,
                          short_code varchar(20) unique,
                          content_type varchar(10) not null check (content_type in ('URL', 'TEXT')), -- cách tối ưu nhất
                          content_data text not null,
                          created_at timestamp with time zone default current_timestamp,
                          expires_at timestamp with time zone default null,
                          password_hash varchar(255) default null,

    -- user_id trỏ đến bảng users đã hoàn chỉnh
                          user_id bigint default null,

                          constraint fk_snippets_user
                              foreign key(user_id)
                                  references users(id)
                                  on delete set null
);

-- Bảng 5: click_analytics (Bảng phân tích)
create table click_analytics (
                                 id bigserial primary key,
                                 snippet_id bigint not null,
                                 click_time timestamp with time zone default current_timestamp,
                                 ip_address varchar(50) default null,
                                 user_agent text default null,

                                 constraint fk_analytics_snippet
                                     foreign key(snippet_id)
                                         references snippets(id)
                                         on delete cascade
);